# Research: Alertas de Preço de Criptomoedas

**Feature**: [spec.md](./spec.md)  
**Plan**: [plan.md](./plan.md)  
**Data da validação**: 2026-10-29

## Decisões e conclusões

### 1. Pares BRL na Binance

**Decisão**: Usar os streams spot combinados `btcbrl@ticker`, `ethbrl@ticker`, `solbrl@ticker`, `xrpbrl@ticker`, `adabrl@ticker` e `dogebrl@ticker`. Os símbolos de mercado REST correspondentes são `BTCBRL`, `ETHBRL`, `SOLBRL`, `XRPBRL`, `ADABRL` e `DOGEBRL`.

**Verificação**: Em 29/10/2026, foi consultado o endpoint público `GET /api/v3/exchangeInfo` da Binance com os seis símbolos. Todos foram retornados com `status=TRADING`, ativos-base esperados e ativo de cotação `BRL`.

| Símbolo canônico | Mercado spot | Stream de ticker |
|---|---|---|
| BTC | BTCBRL | `btcbrl@ticker` |
| ETH | ETHBRL | `ethbrl@ticker` |
| SOL | SOLBRL | `solbrl@ticker` |
| XRP | XRPBRL | `xrpbrl@ticker` |
| ADA | ADABRL | `adabrl@ticker` |
| DOGE | DOGEBRL | `dogebrl@ticker` |

Uma conexão combinada deve usar o endpoint `/stream` com os nomes de stream separados por `/`, por exemplo:

```text
wss://stream.binance.com:9443/stream?streams=btcbrl@ticker/ethbrl@ticker/solbrl@ticker/xrpbrl@ticker/adabrl@ticker/dogebrl@ticker
```

Cada mensagem combinada inclui o nome do stream e o payload do ticker. O stream individual `@ticker` tem velocidade de atualização documentada de 1000 ms e representa uma janela móvel das últimas 24 horas. Mapear `c` (último preço) para `priceBRL`, `P` (variação percentual de 24 horas) para `change24hPercent` e `E` (tempo do evento em milissegundos) para `observedAt`. Validar formatos, símbolo e timestamp antes de atualizar estado ou avaliar alertas.

Uma conexão WebSocket Binance é válida por no máximo 24 horas e recebe ping do servidor a cada 20 segundos; responder corretamente ao ping e preparar renovação/reconexão. Reutilizar backoff para quedas inesperadas e não fazer tentativas apertadas. As listagens de mercado são dinâmicas: consultar `exchangeInfo` durante a implementação/diagnóstico e não tratar esta fotografia datada como garantia permanente de listagem.

**Alternativas consideradas**: seis conexões WebSocket individuais foram rejeitadas em favor de uma conexão combinada, que reduz o número de conexões; consulta de um endpoint REST por moeda foi rejeitada para o fluxo primário por latência e custo de solicitações.

### 2. CoinGecko como fallback REST

**Decisão**: Usar endpoint `/api/v3/simple/price` com IDs canônicos, `vs_currencies=brl`, `include_24hr_change=true` e `include_last_updated_at=true`. IDs validados pela resposta pública:

| Símbolo | CoinGecko ID |
|---|---|
| BTC | `bitcoin` |
| ETH | `ethereum` |
| SOL | `solana` |
| XRP | `ripple` |
| ADA | `cardano` |
| DOGE | `dogecoin` |

Uma chamada de validação em 29/10/2026 retornou preço BRL, variação percentual de 24h e `last_updated_at` para os seis IDs. A documentação confirma que `include_24hr_change` e `include_last_updated_at` estão disponíveis; a variação pode vir `null` quando os dados estão desatualizados. Tratar campo ausente/nulo como dado incompleto, e não preencher com zero.

**Limites e condições de uso**:

- A documentação CoinGecko consultada informa, para o plano Demo, limite de 100 chamadas por minuto; o limite do plano pago varia conforme o plano.
- O acesso sem chave é limitado por IP e compartilhado com outros usuários, portanto não oferece uma cota previsível. Erros `429` significam limite excedido. Todas as respostas, inclusive `4xx` e `5xx`, contam para o limite por minuto documentado.
- A documentação recomenda API key Demo para o plano gratuito. Uma chave embutida em um aplicativo Android pode ser extraída; não a tratar como segredo. Como v1 não inclui backend próprio, não aumentar a frequência de fallback nem prometer SLA: consultar apenas quando necessário, limitar e espaçar tentativas, usar backoff em `429`/falhas transitórias e manter o horário de atualização visível.
- O intervalo de atualização/caching depende do plano; não assumir frequência mais rápida do que a indicada para a credencial e plano efetivamente usados. Usar o timestamp retornado para detectar preço antigo.
- Confirmar termos, limites e exigência de chave no momento de release. Se uma chave protegida ou uma cota garantida for requisito comercial, isso conflita com a restrição de não ter backend na v1 e exige revisão explícita de escopo.

**Alternativas consideradas**: identificação por símbolos foi rejeitada por ambiguidade; usar IDs evita colisões. CoinGecko foi escolhido em vez de Mercado Bitcoin para contingência porque o endpoint de preço consultado cobre os seis ativos e os dados requeridos em uma única solicitação. Binance continua sendo a fonte em tempo real primária.

### 3. Foreground service `dataSync`, timeout e `targetSdk 36`

**Decisão**: Para `targetSdk 36`, quando a pessoa habilitar explicitamente o modo contínuo, usar foreground service com tipo declarado `dataSync`, a permissão geral de foreground service e a permissão específica `FOREGROUND_SERVICE_DATA_SYNC`, além da notificação persistente. O tipo corresponde a transferência/obtenção de dados; não concede execução sem limite.

Em Android 15+ para app que mira API 35+, `dataSync` tem orçamento agregado de seis horas em uma janela de 24 horas em segundo plano. Ao esgotar o orçamento, o sistema chama `Service.onTimeout(int, int)`; o callback precisa terminar o serviço com `stopSelf()` em poucos segundos. Após o limite, outro `dataSync` não pode ser iniciado até interação do usuário que restaure o limite.

**Comportamento no timeout**:

1. Marcar o estado de monitoramento como interrompido pelo sistema.
2. Enfileirar trabalho único WorkManager (e manter o trabalho periódico econômico já configurado) para retomar verificações sem iniciar outro foreground service.
3. Emitir notificação informativa, se as permissões e o canal permitirem.
4. Chamar `stopSelf()` dentro do prazo curto. Não executar rede nem aguardar processamento no callback; os passos auxiliares devem ser rápidos e não podem impedir a parada.
5. Se o sistema não permitir agendamento/execução imediata do Worker, manter o estado interrompido visível; não apresentar o fallback como monitoramento instantâneo garantido.

O limite de `dataSync` não é um tempo máximo ininterrupto por sessão; é acumulado entre os serviços `dataSync` no período de 24 horas. Trazer o app ao primeiro plano restaura o orçamento conforme o comportamento documentado pelo Android.

**Android 16**: trabalhos em segundo plano iniciados a partir de foreground services também estão sujeitos às cotas correspondentes de jobs a partir do Android 16. Não tratar WorkManager iniciado pelo serviço como fonte de execução imediata/ilimitada. Usar Worker periódico ou único como fallback eventual e comunicar sua latência.

#### Alternativa `specialUse`

`specialUse` foi avaliado e **não será adotado por padrão**. A finalidade do tipo é um caso de foreground service válido que não se enquadra nos tipos existentes; requer `FOREGROUND_SERVICE_SPECIAL_USE` e uma propriedade de subtipo explicativa no manifest, além de revisão da declaração no Google Play. A conexão contínua a uma fonte de preços não deve ser classificada como `specialUse` apenas para escapar do orçamento `dataSync`. Só reabrir essa decisão se houver justificativa funcional específica, elegibilidade comprovada e aprovação de política.

### 4. Boot e agendamento persistente

**Decisão**: Não iniciar o foreground service `dataSync` a partir de `BOOT_COMPLETED`. Para apps que miram API 35+, Android 15 proíbe o receiver de boot de iniciar tipos como `dataSync`; isso gera `ForegroundServiceStartNotAllowedException`. Não pedir `RECEIVE_BOOT_COMPLETED` apenas para reiniciar o serviço contínuo.

WorkManager mantém/reagenda seu trabalho persistente após reinicializações por meio de seus mecanismos de agendamento. Agendar trabalho único/periódico com política única e restrições apropriadas; um receiver próprio de boot só deve existir se houver uma necessidade adicional verificada que o WorkManager não cubra e sem iniciar serviço proibido. Ao boot, o modo contínuo pode voltar a estado interrompido/limitado e a pessoa deve ser informada ao abrir o app.

### 5. Widget Glance e limites de atualização

**Decisão**: O widget é passivo: não faz polling de rede nem mantém execução própria. O serviço (enquanto ativo) ou Worker (em execução permitida) persiste o `PriceTick` mais recente e chama `GlanceAppWidget.updateAll(context)` para atualizar as instâncias. A composição do widget lê dados locais persistidos, incluindo horário da cotação e estado de frescor.

`updateAll()` é uma solicitação de atualização, não uma garantia de renderização imediata pelo launcher. A documentação Glance também descreve `updatePeriodMillis` com frequência de até uma vez a cada 30 minutos e WorkManager para trabalhos mais frequentes, a partir de 15 minutos; a execução permanece sujeita ao escalonador, Doze, cotas e launcher. Esta feature não usa polling periódico dedicado ao widget: atualiza o widget quando já há uma atualização de preço válida ou mudança relevante de estado, respeitando a cota do sistema.

## Regras técnicas consolidadas

- `targetSdk 36` (Android 16), mínimo vigente para novos apps e atualizações Android enviados ao Google Play desde 31/08/2026; revalidar antes de cada envio.
- Binance combinado para os seis pares BRL ativos verificados; reconfirmar os mercados ao integrar.
- CoinGecko `/simple/price` por IDs, com BRL, variação 24h e timestamp; respeitar 100 chamadas/minuto do Demo documentado, limite por IP compartilhado sem chave, `429` e caching por plano.
- `dataSync` para modo contínuo solicitado pela pessoa, `onTimeout` no Android 15+, WorkManager como alternativa não imediata e sem reiniciar foreground service no timeout/boot.
- Sem `specialUse` como bypass de limite.
- Widget sem polling, lê `PriceTick` persistido e atualiza sob demanda com `updateAll()`.
- Estados visíveis: online, offline, cotação desatualizada e monitoramento interrompido pelo sistema.

## Fontes

Todas as fontes abaixo foram consultadas em 29/10/2026.

1. Binance Spot API — WebSocket streams, ticker e formato de stream combinado: [WebSocket Streams](https://developers.binance.com/docs/binance-spot-api-docs/web-socket-streams) e [documentação-fonte oficial](https://github.com/binance/binance-spot-api-docs/blob/master/web-socket-streams.md).
2. Binance Spot API — listagem, estado e ativos dos mercados: [Exchange Information](https://developers.binance.com/docs/binance-spot-api-docs/rest-api/general-endpoints#exchange-information). Consulta ao endpoint público `https://api.binance.com/api/v3/exchangeInfo` com os seis símbolos.
3. CoinGecko API — erros e limites: [Errors & Rate Limits](https://docs.coingecko.com/docs/errors-and-rate-limits).
4. CoinGecko API — preço, variação 24h e timestamp: [Simple Price](https://docs.coingecko.com/reference/simple-price).
5. CoinGecko API — IDs dos ativos: [Coins List](https://docs.coingecko.com/reference/coins-list).
6. Android — tempo limite de `dataSync`: [Foreground service timeout behavior](https://developer.android.com/develop/background-work/services/fgs/timeout).
7. Android — alterações de foreground service e `BOOT_COMPLETED`: [Android 15 behavior changes](https://developer.android.com/about/versions/15/behavior-changes-15#fgs-boot-completed).
8. Android — alterações por SDK e cotas Android 16: [Foreground service changes](https://developer.android.com/develop/background-work/services/fgs/changes).
9. Android — tipos e condições de `specialUse`: [Foreground service types](https://developer.android.com/develop/background-work/services/fgs/service-types#special-use).
10. Android — estado, atualização e periodicidade de widgets Glance: [Glance app widgets](https://developer.android.com/develop/ui/compose/glance/glance-app-widget).
11. Android — persistência e reagendamento de trabalho: [Persistent tasks](https://developer.android.com/develop/background-work/background-tasks/persistent).
12. Android — trabalho persistente e periodicidade: [Define work requests](https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work).
13. Google Play — API alvo mínima: [Target API level requirements](https://support.google.com/googleplay/android-developer/answer/11926878?hl=en).
