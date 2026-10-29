# Data Model: Alertas de Preço de Criptomoedas

**Feature**: [spec.md](./spec.md)  
**Research**: [research.md](./research.md)  
**Status**: Draft para implementação

## Visão geral

Os dados do produto são locais. Não existem entidades de conta, usuário remoto ou sincronização. Room guarda alertas e a cotação mais recente por símbolo; preferências de interface, monitoramento e widget ficam em DataStore/estado do Glance, conforme o escopo abaixo.

```text
Alert (N) ──────── (1) Asset/Symbol
                           │
                           └────── (0..1) LatestPrice / PriceTick

WidgetInstance (N) ─────── (1) Asset/Symbol
MonitoringState (0..1)    estado global local do monitoramento
```

`Asset/Symbol` é um identificador canônico da aplicação, não uma entidade persistida obrigatoriamente: `BTC`, `ETH`, `SOL`, `XRP`, `ADA` ou `DOGE`.

## Value Objects e enums

### `PriceTick`

Cotação validada e normalizada emitida por uma fonte de preço.

| Campo | Tipo conceitual | Obrigatório | Regra |
|---|---|---:|---|
| `symbol` | `String` | Sim | Símbolo canônico suportado, em maiúsculas. |
| `priceBRL` | `BigDecimal` | Sim | Preço positivo em BRL; sem arredondamento na camada de domínio. |
| `change24hPercent` | `BigDecimal?` | Não | Variação percentual 24h; nula se a fonte não a fornecer ou indicar dado indisponível. |
| `observedAt` | `Instant` | Sim | Instante informado pela fonte para a observação/evento, normalizado para UTC. |

O símbolo é a chave lógica da cotação mais recente. Cada nova cotação deve ser rejeitada para processamento se o símbolo não for suportado, o preço não for positivo/finito, ou o timestamp for inválido ou mais antigo que a cotação aceita, exceto quando explicitamente reconciliado pela política da fonte.

### Contratos de fonte

- `PriceSource.fetchLatest(symbols)` recebe um conjunto de símbolos canônicos e retorna zero ou mais `PriceTick` validados para consulta REST pontual.
- `StreamingPriceSource` estende `PriceSource` e oferece `observeTicks(symbols): Flow<PriceTick>` para cotações contínuas. Cancelar o collector deve encerrar a sessão de stream; erros de rede devem ser expostos ao coordenador de monitoramento para atualizar o estado e aplicar backoff, não convertidos silenciosamente em lista vazia.
- Adaptadores normalizam payload e timestamps para o modelo acima. O identificador da fonte (`sourceId`) é gravado em `LatestPrice` para proveniência; ele não altera a regra de domínio de cruzamento.

#### Persistência `LatestPrice`

Tabela Room com um registro atual por símbolo para alertas e widget.

| Campo | Tipo Room recomendado | Obrigatório | Descrição |
|---|---|---:|---|
| `symbol` | `TEXT` | Sim | Chave primária, código canônico. |
| `priceBRL` | `TEXT` | Sim | Decimal serializado em representação decimal sem perda. |
| `change24hPercent` | `TEXT?` | Não | Decimal ou nulo. |
| `observedAtEpochMillis` | `INTEGER` | Sim | Timestamp UTC para ordenação e frescor. |
| `receivedAtEpochMillis` | `INTEGER` | Sim | Hora local de recepção; ajuda a diagnosticar atraso/rede. |
| `sourceId` | `TEXT` | Sim | Identificador da fonte (`binance_ws`, `coingecko_rest`, etc.). |

`PriceTick` é o objeto de domínio; `LatestPrice` é seu registro persistido enriquecido com proveniência/recepção. A conversão entre `BigDecimal` e Room deve preservar precisão (por exemplo, serialização decimal em texto), sem `Double` como representação persistente de valores de preço ou limiar.

### `AlertDirection`

| Valor | Significado |
|---|---|
| `UP` | Notificar em travessia ascendente do alvo. |
| `DOWN` | Notificar em travessia descendente do alvo. |
| `BOTH` | Notificar em travessia válida em qualquer direção. |

### `RepeatMode`

| Valor | Significado |
|---|---|
| `ONE_SHOT` | Dispara uma vez e permanece desarmado. |
| `REPEATING` | Pode disparar novamente após rearme pela banda de histerese. |

### Estados operacionais

Conectividade, frescor de cotação e execução do monitoramento são dimensões distintas e não devem ser comprimidas em um único status.

#### `ConnectivityStatus`

| Valor | Significado |
|---|---|
| `ONLINE` | A fonte está acessível. |
| `OFFLINE` | Sem conectividade ou fontes indisponíveis. |
| `UNKNOWN` | Ainda não houve resultado suficiente para determinar conectividade. |

#### `QuoteFreshness`

| Valor | Significado |
|---|---|
| `FRESH` | Timestamp da cotação dentro da janela de frescor definida pelo produto. |
| `STALE` | Cotação persistida excedeu a janela de frescor. |
| `MISSING` | Ainda não há cotação disponível para o símbolo. |

#### Política centralizada de frescor

Centralizar os limiares em um único `QuoteFreshnessPolicy`, sem duplicar valores na UI, repositórios ou widget. A idade é calculada desde `LatestPrice.receivedAtEpochMillis`, isto é, desde a última cotação aceita recebida e persistida. A idade igual ao limiar ainda é `FRESH`; somente uma idade maior que o limiar é `STALE`.

| Contexto de apresentação | Constante | Limiar |
|---|---|---:|
| Stream ativo por Binance WebSocket | `WEBSOCKET_ACTIVE_STALE_AFTER` | 30 segundos |
| Última cotação obtida por REST/Worker | `REST_WORKER_STALE_AFTER` | 20 minutos |
| Indicação de dado desatualizado no widget | `WIDGET_STALE_AFTER` | 30 minutos |

Os limiares são políticas para classificar o dado, não garantias de frequência de atualização do provedor ou execução do Android. A interface principal aplica o limiar correspondente à fonte/estratégia ativa; o widget aplica seu limiar visual de 30 minutos à cotação persistida.

#### `MonitoringStatus`

| Valor | Significado |
|---|---|
| `STOPPED` | Monitoramento não solicitado ou parado voluntariamente. |
| `CONTINUOUS` | Foreground service em execução, sujeito às cotas e políticas do sistema. |
| `WORK_SCHEDULED` | Verificações adiáveis/periódicas agendadas; não implica execução imediata. |
| `SYSTEM_INTERRUPTED` | Monitoramento contínuo encerrado/limitado pelo sistema, por exemplo, timeout de `dataSync`. |
| `LIMITED` | Execução afetada por economia de bateria, cotas ou outras restrições do sistema. |

Esses estados são locais e observáveis pelo usuário; nenhum estado é garantia de execução futura. O limiar apropriado é selecionado pela fonte/estratégia ativa usando `QuoteFreshnessPolicy`; a hora de recepção local é usada para idade, sem confundi-la com o timestamp de mercado `observedAt`.

### `MonitoringState`

Estado operacional local persistido para que a interrupção pelo sistema permaneça visível após recriação do processo.

| Campo | Tipo conceitual | Obrigatório | Descrição |
|---|---|---:|---|
| `monitoringStatus` | `MonitoringStatus` | Sim | Situação do serviço/agendamento. |
| `connectivityStatus` | `ConnectivityStatus` | Sim | Situação da fonte/rede mais recente. |
| `lastChangedAt` | `Instant` | Sim | Instante da última mudança conhecida. |
| `reasonCode` | `String?` | Não | Motivo localizável (por exemplo, timeout do sistema, sem rede, permissão ausente). Não persistir texto técnico bruto como mensagem de UI. |

Persistir `MonitoringState` em DataStore. Ao processo iniciar, não restaurar `CONTINUOUS` como se o serviço ainda estivesse ativo; validar a execução e expor `SYSTEM_INTERRUPTED`, `LIMITED` ou `WORK_SCHEDULED` conforme o estado observado.

## Entidades Room

### `Alert`

Um alerta independente associado a um símbolo. Vários alertas podem usar o mesmo símbolo.

| Campo | Tipo conceitual | Obrigatório | Regra |
|---|---|---:|---|
| `id` | `UUID` | Sim | Chave primária, estável e única. |
| `symbol` | `String` | Sim | Símbolo canônico suportado; índice para consulta por moeda. |
| `targetPriceBRL` | `BigDecimal` | Sim | Valor estritamente maior que zero. |
| `direction` | `AlertDirection` | Sim | `UP`, `DOWN` ou `BOTH`. |
| `repeatMode` | `RepeatMode` | Sim | `ONE_SHOT` ou `REPEATING`. |
| `armed` | `Boolean` | Sim | Indica se uma travessia do alvo pode disparar. Novo alerta começa armado. |
| `firedUp` | `Boolean?` | Sim (nullable) | `null` antes do primeiro disparo; `true` se o último disparo foi ascendente; `false` se descendente. Mantém a direção do último disparo enquanto aguarda rearme. |
| `lastPriceBRL` | `BigDecimal?` | Não | Última cotação aceita para comparar o estado anterior e o atual deste alerta. |
| `enabled` | `Boolean` | Sim | Novo alerta começa habilitado; desabilitado não avalia cruzamento. |
| `createdAt` | `Instant` | Sim | Data/hora de criação UTC. |
| `updatedAt` | `Instant` | Sim | Última alteração persistida. |
| `lastFiredAt` | `Instant?` | Não | Instante do último disparo, para apresentação e diagnóstico. |
| `lastFiredTickAt` | `Instant?` | Não | Timestamp da cotação que causou o último disparo; chave para evitar duplicar uma notificação ao reprocessar o mesmo tick. |

**Chave/índices**:

- Chave primária: `id`.
- Índice por `(symbol, enabled)` para obter alertas ativos da moeda.
- Não impor unicidade por `(symbol, targetPriceBRL)`: a especificação permite múltiplos alertas por moeda, inclusive limites iguais com direções/configurações diferentes.

**Serialização**: `BigDecimal` em `TEXT` decimal sem perda; `UUID` e enums em `TEXT`; `Instant` em epoch milliseconds UTC (`INTEGER`) ou conversor Room equivalente, mantendo semântica consistente. Boolean em `INTEGER`.

### `AlertNotificationEvent`

Registro local/outbox do evento lógico de alerta, para não perder a tentativa de notificação se o processo encerrar entre a atualização do alerta e o envio ao Android.

| Campo | Tipo conceitual | Obrigatório | Regra |
|---|---|---:|---|
| `eventId` | `UUID` | Sim | Chave primária e chave idempotente para o efeito de notificação. |
| `alertId` | `UUID` | Sim | Referência ao alerta que disparou. |
| `symbol` | `String` | Sim | Símbolo canônico. |
| `directionUp` | `Boolean` | Sim | Direção do cruzamento. |
| `targetPriceBRL` | `BigDecimal` | Sim | Alvo no momento do disparo. |
| `observedPriceBRL` | `BigDecimal` | Sim | Preço do tick causador do disparo. |
| `tickObservedAt` | `Instant` | Sim | Timestamp do tick e chave para deduplicação. |
| `deliveryStatus` | enum | Sim | `PENDING`, `POSTED`, `BLOCKED_PERMISSION` ou `FAILED`. |
| `createdAt` | `Instant` | Sim | Data/hora do evento. |
| `updatedAt` | `Instant` | Sim | Última tentativa/alteração de estado. |

Impor unicidade lógica em `(alertId, tickObservedAt)` além da chave primária `eventId`, para impedir que o mesmo tick crie eventos duplicados para o mesmo alerta. O evento e a transição do alerta devem ser gravados na mesma transação. Um dispatcher fora da transação consulta `PENDING`, verifica permissão/canal, publica com ID de notificação determinístico e atualiza o status. `POSTED` significa que a chamada local de notificação foi aceita pelo sistema, não que a pessoa viu a notificação. `BLOCKED_PERMISSION` e `FAILED` não podem ser reportados como entregues. A política de retentativa deve evitar duplicação visível.

## Regras de estado e domínio

### Estados de alerta

```text
Novo:            enabled=true, armed=true, firedUp=null, lastPriceBRL=null
Disparado:       armed=false, firedUp=(true|false), lastFiredAt/tick definidos
Rearmado:        enabled=true, armed=true, firedUp preservado, lastPrice atualizado
Desabilitado:    enabled=false; não avalia ticks
Excluído:        removido localmente; nunca notifica
ONE_SHOT fired:  armed=false permanentemente, até edição explícita/criação de novo alerta
```

Desativar não apaga o alerta nem altera outros alertas. Enquanto estiver desabilitado, o alerta não dispara. Ao reabilitá-lo, inicializar `lastPriceBRL` com o último preço persistido disponível, sem avaliar cruzamentos ocorridos durante a desativação. A política de edição após um disparo deve atualizar `updatedAt`; alterações em alvo/direção/repetição devem reinicializar o estado de avaliação de modo explícito, evitando cruzamentos retroativos.

### Predicado de cruzamento

Para alvo `T`, preço anterior válido `P` e tick atual válido `C`:

- Cruzamento ascendente: `P <= T` e `C > T`.
- Cruzamento descendente: `P >= T` e `C < T`.
- Igualdade isolada ou permanência do mesmo lado não dispara.
- Sem `P`, inicializar a referência e não emitir cruzamento retroativo.
- Aplicar direção: `UP` aceita somente ascendente, `DOWN` somente descendente e `BOTH` aceita ambos.
- Só considerar alertas habilitados e armados.

Essas regras devem viver em função pura e ser cobertas por testes unitários antes da implementação.

### Rearme com histerese de 0,1%

Definir banda:

```text
lower = T × 0,999
upper = T × 1,001
```

Para `REPEATING`, um alerta desarmado só volta a `armed=true` quando o preço atingir o lado oposto ao último disparo:

- `firedUp=true` (último disparo ascendente): rearma em `C <= lower`.
- `firedUp=false` (último disparo descendente): rearma em `C >= upper`.

Ao rearmar, manter `firedUp` até o próximo disparo, para que o lado de rearme continue inequívoco; atualizar `lastPriceBRL` mesmo quando não há notificação. `ONE_SHOT` nunca rearma automaticamente. A comparação deve usar decimal exato e limiares sem arredondamento intermediário; eventual arredondamento é somente de apresentação.

### Comportamento `BOTH`

1. Um alerta `BOTH` armado dispara na primeira travessia válida do alvo, para cima ou para baixo.
2. O disparo salva a direção em `firedUp` e desarma o alerta.
3. O alerta permanece desarmado até atravessar a banda de histerese no lado oposto ao do último disparo (`lower` depois de alta; `upper` depois de queda).
4. Após rearme, a próxima travessia válida do alvo em qualquer direção pode disparar; a nova direção substitui `firedUp`.
5. Variação do preço dentro da zona de histerese, inclusive repassar o alvo sem ter alcançado a banda oposta, não produz disparo repetido.

### Atomicidade e idempotência

Processar um `PriceTick` de forma serializada por símbolo. Na mesma transação local:

1. validar frescor/ordenação do tick;
2. carregar alertas da moeda e seus `lastPriceBRL`;
3. calcular disparo/rearme por função pura para alertas habilitados e criar `AlertNotificationEvent` para cada disparo;
4. atualizar estados e último preço dos alertas;
5. gravar o `LatestPrice` e os eventos pendentes de notificação.

Gerar efeitos externos (notificação e `GlanceAppWidget.updateAll`) depois do commit, com controle para não reenviar evento quando o mesmo `lastFiredTickAt` já foi processado. A ordem evita que widget ou worker leia estado parcialmente aplicado. Falhas de notificação não devem desfazer o histórico de cruzamento nem falsamente marcar entrega; manter o resultado no outbox com retentativa idempotente.

## Preferências e configuração do widget

### DataStore — `AppPreferences`

| Campo | Tipo conceitual | Padrão | Descrição |
|---|---|---|---|
| `continuousMonitoringEnabled` | `Boolean` | `false` | Solicitação explícita da pessoa para monitoramento contínuo; não garante execução permitida. |
| `themeMode` | enum (`SYSTEM`, `LIGHT`, `DARK`) | `SYSTEM` | Preferência de tema. |

`MonitoringState` é armazenado separadamente da preferência, pois o estado operacional (online/offline/desatualizado/interrompido/limitado) pode divergir da solicitação. Frescor é calculado a partir de `LatestPrice.observedAt` e da janela definida pelo produto; não é preferência persistida por moeda.

### Configuração Glance — `WidgetInstance`

Armazenar estado por instância de widget usando o estado de preferência do Glance ou armazenamento local equivalente.

| Campo | Tipo conceitual | Obrigatório | Descrição |
|---|---|---:|---|
| `glanceId` | identificador Glance | Sim | Identifica uma instância colocada pelo usuário. |
| `symbol` | `String` | Sim | Símbolo selecionado para exibição. |

O preço, variação, horário e frescor não são copiados para a configuração: o widget lê o `LatestPrice` persistido por `symbol`, renderiza estado de indisponibilidade/desatualização e não executa polling. Após persistir uma cotação ou mudança de estado relevante, serviço/Worker solicita atualização usando `updateAll()`.

## Notificações

Notificação é um efeito externo; `AlertNotificationEvent` é o outbox local para durabilidade/idempotência, não parte da regra pura de cruzamento. A plataforma pode bloquear a entrega; o app deve refletir a permissão/canal disponível e nunca afirmar entrega sem confirmação.

## Fora do modelo da v1

- Conta, usuário remoto, credencial de login, sincronização ou banco de dados de servidor.
- Histórico completo de preços ou gráfico.
- Métrica percentual como condição de alerta.
- Chave de API CoinGecko tratada como segredo embutido no APK.
