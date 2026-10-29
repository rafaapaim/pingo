# Implementation Plan: Alertas de Preço de Criptomoedas

**Branch**: `001-alerta-cripto` | **Date**: 2026-10-29 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `specs/001-alerta-cripto/spec.md`

## Summary

Implementar um aplicativo Android nativo para consultar cotações de criptomoedas em BRL, configurar alertas por cruzamento, apresentar notificações e oferecer widget. O produto funciona localmente e prioriza consumo baixo de bateria, transparência de dados e conformidade com as restrições de execução do Android.

## Technical Context

**Language/Version**: Kotlin; versão exata a alinhar com a versão estável do Android Gradle Plugin e do Kotlin escolhida no setup.

**Primary Dependencies**: Jetpack Compose para interface, Jetpack Glance para widget, Hilt para injeção, Retrofit e OkHttp (incluindo WebSocket) para comunicação de rede.

**Storage**: Room para alertas, último `PriceTick` por símbolo e outbox de eventos de notificação; DataStore para preferências e estado operacional local do monitoramento. Sem backend próprio, login ou sincronização na nuvem na v1.

**Testing**: Testes unitários Kotlin para regras puras do domínio, escritos antes da implementação; testes de integração para persistência, fontes de preços, notificações, monitoramento em segundo plano e widget.

**Target Platform**: Android, `minSdk 26`, `targetSdk 36` (Android 16), mínimo vigente para novos apps e atualizações Android enviados ao Google Play desde 31/08/2026.

**Project Type**: Aplicativo móvel Android nativo.

**Performance Goals**: Atualizar preços aproximadamente a cada segundo enquanto o aplicativo está aberto, sujeito à disponibilidade da fonte e da rede. Em segundo plano, favorecer verificações econômicas e respeitar os limites de execução impostos pelo sistema.

**Constraints**: Consumo de bateria baixo; backoff exponencial com limite para reconexões; pausar ou reduzir monitoramento em modo de economia de bateria; estados online, offline, dado desatualizado e monitoramento interrompido pelo sistema visíveis. Serviços, boot e widgets devem seguir as regras aplicáveis ao `targetSdk`, incluindo os limites de serviços `dataSync` a partir do Android 15.

**Quote Freshness**: Centralizar os limiares em `QuoteFreshnessPolicy`: 30 segundos sem cotação recebida enquanto o WebSocket estiver ativo; 20 minutos desde a última cotação recebida por REST/Worker; 30 minutos desde a última cotação persistida para a indicação de desatualização no widget. O limite é inclusivo para dados frescos; a cotação fica desatualizada quando a idade excede o limiar. Os limiares são políticas de apresentação, não garantias de frequência de execução ou entrega.

**Scale/Scope**: Uma pessoa usuária e seus dados locais; conjunto inicial de seis criptomoedas; múltiplos alertas por moeda; sem serviço de conta ou backend próprio.

## Constitution Check

- **Arquitetura Android nativa**: PASS — Kotlin, Compose, MVVM + Repository e Hilt; `minSdk 26` e `targetSdk 36`, mínimo vigente do Google Play para novos apps e atualizações Android.
- **Domínio puro e test-first**: PASS — cruzamento, direção, rearme e repetição serão regras puras, com testes unitários definidos antes da implementação.
- **Bateria e segundo plano**: PASS — backoff, redução/pausa no modo de economia e alternativa periódica; o desenho não pressupõe execução ininterrupta.
- **Local-first e privacidade**: PASS — armazenamento e preferências locais, sem login nem backend próprio na v1.
- **Localização e transparência**: PASS — pt-BR, BRL, tema claro/escuro e estados de rede/erro/dado antigo visíveis.
- **Limites do Android**: PASS — plano exige validar limites de serviço `dataSync`, inicialização após boot, permissões e frequência de atualização de widgets para o `targetSdk` selecionado.

## Technical Decisions

### Aplicativo e apresentação

- Kotlin com Jetpack Compose para telas e estados de interface.
- MVVM + Repository para separar apresentação, domínio e acesso a dados; Hilt para injeção de dependências.
- Jetpack Glance para o widget configurável, com layouts compacto (2x1) e ampliado (2x2). Não haverá polling pelo widget: o serviço ou Worker persistirá o último `PriceTick` e acionará `updateAll()` para atualizar os widgets existentes.
- Formatação de moeda e mensagens localizadas para pt-BR; tema claro e escuro.

### Preços e conectividade

- Definir `PriceTick` com símbolo canônico, preço em BRL (`BigDecimal`), variação percentual de 24 horas (`BigDecimal`) e horário de observação (`Instant`).
- Definir `PriceSource` com `suspend fun fetchLatest(symbols): List<PriceTick>` para consulta pontual. Definir `StreamingPriceSource : PriceSource` com `fun observeTicks(symbols): Flow<PriceTick>` para fontes contínuas; o adaptador Binance implementará streaming e o adaptador CoinGecko REST fornecerá cotações de contingência.
- Usar Retrofit para REST e OkHttp WebSocket para atualizações em tempo real enquanto o aplicativo está visível.
- Conectar ao Binance WebSocket usando um stream combinado com ticker para os seis pares BRL correspondentes a BTC, ETH, SOL, XRP, ADA e DOGE; converter símbolos recebidos para o formato canônico da aplicação.
- Usar CoinGecko como fallback REST quando o stream principal estiver indisponível, observando limites de uso e mapeando as cotações para BRL.
- Usar backoff exponencial limitado em reconexões; não fazer tentativas contínuas em loop apertado.
- Manter horário de recepção e estado da última cotação para distinguir preço atualizado de dado armazenado/desatualizado. Aplicar os limiares centralizados de `QuoteFreshnessPolicy` descritos em Technical Context; o widget usa seu limiar de 30 minutos para sua própria indicação visual.

### Dados locais e regras de domínio

- Room para alertas e o último `PriceTick` por símbolo, necessário para detectar cruzamentos após mudanças de estado do processo e renderizar o widget sem rede/polling próprio.
- DataStore para preferências, incluindo a escolha de monitoramento e a configuração de tema quando aplicável.
- O modelo conceitual do `Alert` inclui identificador, símbolo, valor-alvo em BRL, direção (`UP`, `DOWN` ou `BOTH`), modo de repetição, estado armado, `firedUp` (lado do último disparo; nulo antes do primeiro), último preço, habilitado/desabilitado e data de criação.
- A decisão se uma cotação cruza um limite deve ser uma função pura sem dependências Android, de rede ou de armazenamento. Escrever primeiro testes unitários para igualdade com o limite, primeira cotação sem preço anterior, cruzamentos em cada direção, `BOTH`, repetição, histerese, disparo único e entrada inválida; só então implementar a regra.
- Com histerese de 0,1% do alvo, após disparo de alta (`firedUp = true`) um alerta repetível só rearma quando o preço retorna a `targetPriceBRL * 0,999` ou abaixo; após disparo de queda (`firedUp = false`), só rearma quando chega a `targetPriceBRL * 1,001` ou acima. Por exemplo, com alvo de R$ 100 e disparo ascendente em R$ 101, R$ 99,95 não rearma, mas R$ 99,89 rearma. As comparações devem preservar precisão decimal.
- Em `BOTH`, `firedUp` registra a direção do disparo mais recente. O alerta permanece desarmado até cruzar a banda de histerese no lado oposto ao disparo; depois de rearmado, pode disparar na próxima travessia válida do alvo em qualquer direção, atualizando `firedUp`. Oscilações que não alcancem a banda oposta não repetem notificações.
- A histerese controla somente o rearme; o disparo continua exigindo cruzamento do alvo. Um alerta `UP` só dispara para cima, `DOWN` só para baixo, e `BOTH` aceita qualquer uma das duas direções.
- Alertas de disparo único permanecem desarmados após a primeira notificação, sem rearme por histerese. Na primeira cotação sem preço anterior, inicializar a referência sem disparo retroativo.
- Persistir o estado necessário para evitar duplicidade de disparos e identificar cotação antiga; efeitos colaterais (persistência, notificação e atualização visual) ficam fora da função de domínio.

### Execução em segundo plano, notificações e widgets

- Usar WebSocket enquanto o aplicativo está visível.
- No modo contínuo solicitado explicitamente pela pessoa usuária, declarar o foreground service como tipo `dataSync`, exibir a notificação persistente exigida e respeitar os limites de execução aplicáveis ao SDK.
- Em Android 15 ou superior quando o app mira API 35+, implementar `Service.onTimeout(int, int)` para o serviço `dataSync`: marcar o estado interrompido, enfileirar fallback via WorkManager e notificar a pessoa usuária quando permitido, sem bloquear a chamada a `stopSelf()` dentro do prazo curto fornecido pelo sistema. O callback não pode iniciar outro foreground service para contornar o limite.
- Respeitar o limite `dataSync` de seis horas acumuladas por período de 24 horas em segundo plano; depois de esgotado, não tentar iniciar outro serviço desse tipo até que a pessoa traga o app ao primeiro plano e o limite seja restaurado pelo sistema.
- Usar WorkManager para verificações adiáveis/periódicas como alternativa econômica e fallback após timeout, respeitando o intervalo mínimo do sistema e sem prometer periodicidade exata. Evitar verificações duplicadas quando o modo contínuo estiver ativo.
- Registrar `specialUse` como alternativa técnica avaliada, não como escolha padrão: só considerar se o caso se enquadrar genuinamente em um tipo não coberto, declarar `FOREGROUND_SERVICE_SPECIAL_USE` e a propriedade/subtipo explicativo no manifest, e submeter a justificativa à revisão do Google Play. Não usar `specialUse` para escapar do limite de `dataSync`; confirmar elegibilidade e política antes de qualquer adoção.
- Ao reiniciar o dispositivo, restaurar somente trabalho agendável permitido. Verificar as restrições vigentes para iniciar serviços a partir de `BOOT_COMPLETED`; não iniciar um serviço proibido para contornar os limites da plataforma.
- O serviço ou Worker deve persistir cada último `PriceTick` antes de acionar `updateAll()` no widget Glance. O widget lê a cotação persistida e não executa polling próprio; respeitar as cotas de atualização do sistema e exibir horário e condição de dado desatualizado.
- Manter estados observáveis de conectividade/monitoramento (online, offline, dado desatualizado e interrompido pelo sistema) e apresentá-los na interface; notificar interrupção pelo sistema sem apresentar cotação antiga como atual.
- Criar canal de notificação para alertas e tratar a permissão de notificações em runtime onde aplicável. O toque na notificação deve abrir o detalhe da moeda.
- Revisar permissões de rede, notificações, serviço em primeiro plano e boot conforme a versão de Android e o `targetSdk` selecionados; solicitar `RECEIVE_BOOT_COMPLETED` apenas se o comportamento permitido realmente exigir.
- Não exigir desativação da otimização de bateria nem contornar mecanismos de economia do sistema; explicar as limitações e estados ao usuário.

## Project Structure

### Documentation (this feature)

```text
specs/001-alerta-cripto/
├── plan.md
├── spec.md
├── research.md       # Validação de fontes, limites de API e políticas Android/Play
├── data-model.md     # Entidades, estados e regras de persistência locais
├── quickstart.md     # A detalhar antes da validação ponta a ponta
└── tasks.md          # Criado no fluxo de tarefas
```

### Source Code (repository root)

```text
android/
├── app/
│   └── src/
│       ├── main/
│       │   ├── kotlin/
│       │   │   ├── data/      # Fontes locais/remotas e repositórios
│       │   │   ├── domain/    # Modelos e regras puras de negócio
│       │   │   ├── presentation/ # Telas Compose e ViewModels
│       │   │   ├── monitoring/   # Coordenação de alertas e execução em segundo plano
│       │   │   ├── widget/    # Interface e configuração do widget
│       │   │   └── di/        # Módulos Hilt
│       │   └── AndroidManifest.xml
│       └── test/
└── build.gradle.kts
```

**Structure Decision**: Aplicativo Android Gradle em um único módulo `app`, organizado em pacotes por camada e funcionalidade. O workspace ainda não contém o projeto Android; configurações Gradle e namespace serão definidos durante o setup.

## Complexity Tracking

Não há violações da constituição que precisem de justificativa.

## Validation Follow-up

- Mercados Binance, IDs e retorno CoinGecko, limites documentados e políticas atuais de Android/Play estão registrados em [research.md](./research.md) (validação em 29/10/2026). Consultar novamente antes da release, pois mercados, limites e políticas podem mudar.
- A validação em dispositivo/emulador de `onTimeout`, permissões, restrições de boot, execução/cotas do Worker e atualização Glance é critério de implementação e teste; não é uma dúvida documental pendente.
- Os limiares de frescor (WebSocket ativo: 30 s; REST/Worker: 20 min; widget: 30 min) estão definidos no Technical Context e devem ser mantidos centralizados em `QuoteFreshnessPolicy`, cobertos por testes de fronteira e revalidados se a política do produto mudar.
