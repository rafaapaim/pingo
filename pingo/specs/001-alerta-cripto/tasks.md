# Tasks: Alertas de Preço de Criptomoedas

**Feature**: `001-alerta-cripto`  
**Documentos de entrada**: [spec.md](./spec.md), [plan.md](./plan.md), [research.md](./research.md), [data-model.md](./data-model.md)

**Formato**: `- [ ] Tnnn [P?] [USn?] Descrição com caminho de arquivo`

- `[P]` indica trabalho independente, em arquivos distintos e sem depender de tarefa incompleta.
- `[US1]` consulta de preços; `[US2]` alertas; `[US3]` widget; `[US4]` transparência de conectividade e monitoramento.
- Os caminhos abaixo seguem a estrutura `android/app/` definida em `plan.md`; o projeto Android ainda precisa ser criado na Fase 1.

## Fase 1: Setup e lista de preços

**Objetivo**: Inicializar o app Android e entregar a consulta das seis cotações em BRL.

**Verificação independente**: Em aparelho/emulador com rede, abrir o app e encontrar BTC, ETH, SOL, XRP, ADA e DOGE com preço, variação 24h e horário de atualização.

- [X] T001 Criar o projeto Gradle Android com wrapper, `settings.gradle.kts`, build raiz e módulo `android/app/build.gradle.kts`, definindo namespace/applicationId `com.pingo.alertacrypto`, `minSdk 26` e `targetSdk 36` conforme o mínimo vigente do Google Play descrito em `research.md`.
- [X] T002 Configurar plugins e dependências Kotlin, Compose, Hilt, coroutines/Flow, Retrofit, OkHttp, Room, DataStore, WorkManager e Glance em `android/build.gradle.kts` e `android/app/build.gradle.kts`.
- [X] T003 Criar o manifest com permissão de internet e strings-base pt-BR em `android/app/src/main/AndroidManifest.xml` e `android/app/src/main/res/values/strings.xml`.
- [X] T004 [P] [US1] Implementar o ponto de entrada `Application` e o módulo Hilt inicial em `android/app/src/main/kotlin/com/pingo/alertacrypto/PingoApplication.kt` e `android/app/src/main/kotlin/com/pingo/alertacrypto/di/AppModule.kt`.
- [X] T005 [P] [US1] Definir o catálogo canônico das seis moedas e `PriceTick(symbol, priceBRL, change24hPercent, observedAt)` em `android/app/src/main/kotlin/com/pingo/alertacrypto/domain/price/Asset.kt` e `android/app/src/main/kotlin/com/pingo/alertacrypto/domain/price/PriceTick.kt`, preservando BRL em decimal exato.
- [X] T006 [US1] Definir `PriceSource.fetchLatest(symbols)` e `StreamingPriceSource.observeTicks(symbols)` em `android/app/src/main/kotlin/com/pingo/alertacrypto/domain/price/PriceSource.kt`, expondo falhas de fonte em vez de convertê-las em lista vazia.
- [X] T007 [US1] Implementar o adaptador Binance WebSocket com stream combinado `btcbrl@ticker/ethbrl@ticker/solbrl@ticker/xrpbrl@ticker/adabrl@ticker/dogebrl@ticker`, normalizando os campos de ticker e timestamps conforme `research.md`, em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/price/BinancePriceSource.kt`.
- [X] T008 [P] [US1] Implementar o adaptador CoinGecko REST para os seis IDs documentados, incluindo preço BRL, variação 24h e timestamp, com tratamento explícito de `429`, campos ausentes e erros, em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/price/CoinGeckoPriceSource.kt`.
- [X] T009 [US1] Implementar o repositório de preços para selecionar streaming em primeiro plano e fallback REST quando necessário, com backoff limitado e atualização observável, em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/price/PriceRepositoryImpl.kt` e `android/app/src/main/kotlin/com/pingo/alertacrypto/domain/price/PriceRepository.kt`.
- [X] T010 [US1] Implementar ViewModel e tela Compose de lista, busca/seleção de moeda, variação e horário de cotação em `android/app/src/main/kotlin/com/pingo/alertacrypto/presentation/prices/PriceListViewModel.kt` e `android/app/src/main/kotlin/com/pingo/alertacrypto/presentation/prices/PriceListScreen.kt`.
- [X] T011 [US1] Implementar tema claro/escuro e formatação pt-BR de BRL (`R$ 1.234,56`) em `android/app/src/main/kotlin/com/pingo/alertacrypto/presentation/theme/Theme.kt` e `android/app/src/main/kotlin/com/pingo/alertacrypto/presentation/format/PriceFormatter.kt`.

**Checkpoint**: A lista de preços funciona com a fonte de streaming disponível e apresenta falhas de rede sem mostrar cotação antiga como atual.

## Fase 2: CRUD de alertas e Room

**Objetivo**: Persistir alertas e cotações localmente e oferecer criação, edição, ativação, desativação e exclusão.

**Verificação independente**: Criar dois alertas para a mesma moeda, alterar ou remover um deles, fechar e reabrir o app e confirmar que o outro permanece inalterado.

- [X] T012 [US2] Criar as entidades Room `Alert`, `LatestPrice` e `AlertNotificationEvent` conforme `data-model.md` em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/local/entity/AlertEntity.kt`, `LatestPriceEntity.kt` e `AlertNotificationEventEntity.kt`, incluindo `firedUp`, estados de entrega do outbox e serialização decimal sem perda.
- [X] T013 [US2] Criar converters, índices e DAOs para alertas, cotações mais recentes e eventos pendentes em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/local/dao/AlertDao.kt`, `LatestPriceDao.kt`, `AlertNotificationEventDao.kt` e `android/app/src/main/kotlin/com/pingo/alertacrypto/data/local/converter/DecimalConverters.kt`; indexar alertas por `(symbol, enabled)` e impedir duplicidade lógica do mesmo `(alertId, tickObservedAt)`.
- [X] T014 [US2] Criar `AppDatabase` e migrations iniciais em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/local/AppDatabase.kt`, e injetar banco/DAOs via `android/app/src/main/kotlin/com/pingo/alertacrypto/di/DatabaseModule.kt`.
- [X] T015 [US2] Implementar repositório local de alertas em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/alert/AlertRepositoryImpl.kt` e `android/app/src/main/kotlin/com/pingo/alertacrypto/domain/alert/AlertRepository.kt`; validar `targetPriceBRL > 0`, aceitar somente símbolos suportados e permitir múltiplos alertas por moeda, sem unicidade indevida por alvo.
- [X] T016 [US2] Implementar operações de criar, atualizar, ativar/desativar e excluir alertas em `android/app/src/main/kotlin/com/pingo/alertacrypto/domain/alert/AlertUseCases.kt` e ViewModel correspondente em `android/app/src/main/kotlin/com/pingo/alertacrypto/presentation/alerts/AlertListViewModel.kt`.
- [X] T017 [US2] Criar telas Compose de lista e formulário de alertas em `android/app/src/main/kotlin/com/pingo/alertacrypto/presentation/alerts/AlertListScreen.kt` e `android/app/src/main/kotlin/com/pingo/alertacrypto/presentation/alerts/AlertEditorScreen.kt`, incluindo direção `UP`/`DOWN`/`BOTH` e modo `ONE_SHOT`/`REPEATING`.
- [X] T018 [US2] Persistir o último preço por símbolo no Room e adaptar o repositório de preços para usar `LatestPrice` como cache local em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/price/PriceRepositoryImpl.kt` e `android/app/src/main/kotlin/com/pingo/alertacrypto/data/local/dao/LatestPriceDao.kt`.

**Checkpoint**: CRUD persiste localmente, aceita múltiplos alertas por moeda e não depende de login ou backend.

## Fase 3: Serviço de monitoramento e notificações

**Objetivo**: Detectar cruzamentos corretamente, emitir notificações idempotentes e respeitar limites do Android.

**Verificação independente**: Reproduzir uma sequência controlada de ticks e confirmar disparo apenas em cruzamento, rearme por histerese, estado de monitoramento correto e nenhuma notificação duplicada para o mesmo tick.

### TDD de `CrossingDetector` — obrigatório antes da implementação

- [X] T019 [US2] Escrever primeiro os testes unitários de `CrossingDetector` em `android/app/src/test/kotlin/com/pingo/alertacrypto/domain/alert/CrossingDetectorTest.kt`; cobrir subida, queda, igualdade sem cruzamento, primeira cotação sem referência, entrada inválida, direção `BOTH`, `ONE_SHOT`, repetição e histerese `0,1%`, incluindo o exemplo SC-008 (alvo R$ 100: dispara em R$ 101, R$ 99,95 continua desarmado e R$ 99,89 rearma); executar e confirmar falha porque o detector ainda não existe.
- [X] T020 [US2] Implementar a função pura `CrossingDetector` em `android/app/src/main/kotlin/com/pingo/alertacrypto/domain/alert/CrossingDetector.kt` somente após T019; usar `BigDecimal`, disparar por travessia estrita do alvo e rearmar alerta repetível em `target * 0,999` após alta ou `target * 1,001` após queda.

### Processamento, serviço e notificações

- [X] T021 [US2] Implementar o processamento serializado de `PriceTick` por símbolo e a transação Room que atualiza alertas, `LatestPrice` e `AlertNotificationEvent` usando `CrossingDetector`, em `android/app/src/main/kotlin/com/pingo/alertacrypto/domain/alert/ProcessPriceTick.kt` e `android/app/src/main/kotlin/com/pingo/alertacrypto/data/alert/PriceTickProcessor.kt`; em `BOTH`, salvar o lado do último disparo em `firedUp` e só rearmar ao atingir a banda oposta.
- [X] T022 [US4] Definir persistência de `MonitoringState` em DataStore, separando estado operacional de preferência de monitoramento, em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/monitoring/MonitoringStateStore.kt`.
- [X] T023 [US4] Implementar agendamento único/periódico e `PriceCheckWorker` com restrições e backoff via WorkManager, atualizando o estado e preços sem prometer execução imediata, em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/monitoring/PriceCheckWorker.kt` e `android/app/src/main/kotlin/com/pingo/alertacrypto/data/monitoring/MonitoringScheduler.kt`.
- [X] T024 [US2] Implementar o foreground service do modo contínuo solicitado pela pessoa, incluindo notificação persistente e início somente por interação permitida, em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/monitoring/PriceMonitoringService.kt` e declaração/permissões em `android/app/src/main/AndroidManifest.xml`; usar tipo `dataSync` e respeitar limites aplicáveis ao `targetSdk 36`.
- [X] T025 [US4] Implementar `Service.onTimeout(int, int)` para `dataSync` em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/monitoring/PriceMonitoringService.kt`: marcar `SYSTEM_INTERRUPTED`, enfileirar fallback WorkManager, informar a pessoa quando notificações permitirem e chamar `stopSelf()` rapidamente; não iniciar outro foreground service nem bloquear o callback esperando rede.
- [X] T026 [US2] Implementar o dispatcher idempotente do outbox em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/notification/AlertNotificationDispatcher.kt`; publicar usando ID determinístico, atualizar `deliveryStatus` e não tratar `POSTED` como confirmação de leitura nem repetir publicação para o mesmo `(alertId, tickObservedAt)`.
- [X] T027 [US2] Criar canal, permissões e notificação de alerta com navegação ao detalhe da moeda em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/notification/AlertNotificationFactory.kt`, `android/app/src/main/AndroidManifest.xml` e `android/app/src/main/kotlin/com/pingo/alertacrypto/presentation/navigation/PriceDetailDestination.kt`.
- [X] T028 [US4] Implementar apresentação visível dos estados online, offline, cotação desatualizada e monitoramento interrompido/limitado, com último horário conhecido e sem rotular preço antigo como atual, em `android/app/src/main/kotlin/com/pingo/alertacrypto/presentation/monitoring/MonitoringStatusBanner.kt` e integração em `android/app/src/main/kotlin/com/pingo/alertacrypto/presentation/prices/PriceListScreen.kt`.
- [X] T029 [US4] Restaurar apenas trabalho permitido após reinício, sem iniciar `dataSync` a partir de `BOOT_COMPLETED`, e revisar `RECEIVE_BOOT_COMPLETED`/manifest em `android/app/src/main/AndroidManifest.xml` e `android/app/src/main/kotlin/com/pingo/alertacrypto/data/monitoring/MonitoringScheduler.kt`.

**Checkpoint**: Timeout de `dataSync` não causa exceção por serviço que permaneceu ativo; o fallback não reinicia serviço proibido; o estado interrompido fica visível.

## Fase 4: Widget Glance

**Objetivo**: Exibir a última cotação persistida em widget configurável, sem polling próprio.

**Verificação independente**: Em aparelho real, adicionar widget, selecionar moeda, verificar preço/variação/horário, confirmar dado desatualizado e tocar para abrir o app.

- [X] T030 [US3] Criar o widget Glance com apresentação compacta e ampliada em `android/app/src/main/kotlin/com/pingo/alertacrypto/widget/PriceGlanceWidget.kt` e metadados em `android/app/src/main/res/xml/price_widget_info.xml`.
- [X] T031 [US3] Implementar configuração por instância de widget para selecionar um símbolo suportado em `android/app/src/main/kotlin/com/pingo/alertacrypto/widget/PriceWidgetConfigureActivity.kt` e armazenamento de estado Glance em `android/app/src/main/kotlin/com/pingo/alertacrypto/widget/PriceWidgetState.kt`.
- [X] T032 [US3] Implementar leitura do `LatestPrice` persistido, formatação pt-BR, indicador de cotação ausente/desatualizada e ação de toque para abrir o app em `android/app/src/main/kotlin/com/pingo/alertacrypto/widget/PriceGlanceWidget.kt`; não fazer requisições de rede no widget.
- [X] T033 [US3] Acionar `PriceGlanceWidget().updateAll(context)` após persistência de preço ou mudança relevante de estado em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/monitoring/PriceMonitoringService.kt`, `PriceCheckWorker.kt` e `android/app/src/main/kotlin/com/pingo/alertacrypto/widget/PriceWidgetUpdater.kt`; respeitar cotas do sistema, sem polling dedicado.

**Checkpoint**: O widget consulta somente o estado local e identifica a idade da cotação; `updateAll()` é tratado como solicitação, não garantia de atualização instantânea.

## Fase 5: Testes, bateria e publicação

**Objetivo**: Validar comportamento integrado, consumo, limites Android e release em condições reais.

- [X] T034 [P] Criar testes de unidade dos mapeadores Binance/CoinGecko para preço, variação, timestamp, símbolo não suportado e payload inválido em `android/app/src/test/kotlin/com/pingo/alertacrypto/data/price/PriceSourceMapperTest.kt`.
- [X] T035 [P] Criar testes de integração Room para CRUD, persistência de `LatestPrice`, transição de `Alert` e unicidade do outbox em `android/app/src/androidTest/kotlin/com/pingo/alertacrypto/data/local/AlertDatabaseTest.kt`.
- [X] T036 [P] Criar testes de UI para criação/edição de alerta, formatação monetária pt-BR e visibilidade dos estados offline/desatualizado/interrompido em `android/app/src/androidTest/kotlin/com/pingo/alertacrypto/presentation/alerts/AlertCrudUiTest.kt` e `android/app/src/androidTest/kotlin/com/pingo/alertacrypto/presentation/monitoring/MonitoringStatusUiTest.kt`.
- [X] T037 [P] Criar testes do dispatcher de notificação para deduplicação de `(alertId, tickObservedAt)`, permissão ausente, falha de publicação, textos pt-BR e valores no formato BRL (`R$ 1.234,56`) em `android/app/src/test/kotlin/com/pingo/alertacrypto/data/notification/AlertNotificationDispatcherTest.kt`.
- [ ] T038 [P] Criar testes do Worker e do fluxo `onTimeout` para confirmar fallback agendado, estado `SYSTEM_INTERRUPTED`, parada do serviço e ausência de reinício de `dataSync` em `android/app/src/androidTest/kotlin/com/pingo/alertacrypto/data/monitoring/MonitoringServiceTimeoutTest.kt`.
- [ ] T039 [P] Criar testes do widget para configuração por instância, renderização sem cotação, cotação desatualizada, formatos compacto/ampliado, navegação ao toque, textos pt-BR e valores no formato BRL (`R$ 1.234,56`) em `android/app/src/androidTest/kotlin/com/pingo/alertacrypto/widget/PriceGlanceWidgetTest.kt`.
- [X] T040 [US4] Escrever primeiro testes unitários de `QuoteFreshnessPolicy` em `android/app/src/test/kotlin/com/pingo/alertacrypto/domain/monitoring/QuoteFreshnessPolicyTest.kt`; validar para cada contexto (WebSocket ativo 30 s, REST/Worker 20 min e widget 30 min) que a idade exatamente no limiar é fresca e que uma idade maior é desatualizada; executar e confirmar falha antes da implementação.
- [X] T041 [US4] Implementar `QuoteFreshnessPolicy` com constantes centralizadas `WEBSOCKET_ACTIVE_STALE_AFTER`, `REST_WORKER_STALE_AFTER` e `WIDGET_STALE_AFTER` em `android/app/src/main/kotlin/com/pingo/alertacrypto/domain/monitoring/QuoteFreshnessPolicy.kt`; calcular idade a partir de `LatestPrice.receivedAtEpochMillis` e aplicar o limiar conforme contexto, sem duplicar constantes na UI ou widget.
- [X] T042 [P] Criar testes de UI para tema claro e escuro, verificando aplicação de cores e legibilidade nos dois modos conforme FR-015, em `android/app/src/androidTest/kotlin/com/pingo/alertacrypto/presentation/theme/ThemeUiTest.kt`.
- [ ] T043 Executar os testes unitários, instrumentados, lint e build de release pelo wrapper `android/gradlew.bat`; corrigir falhas relacionadas à feature e registrar os comandos/resultados em `specs/001-alerta-cripto/tasks.md`.
- [ ] T044 Medir consumo e comportamento de rede em aparelho real com tela aberta, segundo plano, reconexões e Economia de bateria; ajustar backoff em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/price/PriceRepositoryImpl.kt` e pausas/agendamento em `android/app/src/main/kotlin/com/pingo/alertacrypto/data/monitoring/MonitoringScheduler.kt`.
- [X] T045 [P] Criar `specs/001-alerta-cripto/quickstart.md` com roteiro ponta a ponta em aparelho Android real para validar lista de preços, criar e disparar alerta, modo contínuo, `onTimeout` e fallback WorkManager, widget, modo avião, tema e reinicialização; registrar pré-requisitos, passos, resultados esperados e espaço para registrar aparelho/versão/resultado.
- [ ] T046 Validar manualmente em aparelho Android real a lista de seis pares Binance, stream combinado, variação 24h, timestamp, queda de rede e reconexão seguindo `quickstart.md`; registrar resultado e versão do aparelho nesta tarefa em `specs/001-alerta-cripto/tasks.md` e verificar `android/app/src/main/kotlin/com/pingo/alertacrypto/data/price/BinancePriceSource.kt`.
- [ ] T047 Validar manualmente em aparelho Android real com Android 15+ o serviço `dataSync` em segundo plano, estado da notificação persistente, pausa/restrições de bateria, callback `onTimeout` e fallback WorkManager seguindo `quickstart.md`; registrar resultado nesta tarefa em `specs/001-alerta-cripto/tasks.md` e verificar `android/app/src/main/kotlin/com/pingo/alertacrypto/data/monitoring/PriceMonitoringService.kt` e `PriceCheckWorker.kt`.
- [ ] T048 Validar manualmente em aparelho Android real o widget Glance nos formatos compacto e ampliado, escolha da moeda, `updateAll()`, preço desatualizado e toque de navegação seguindo `quickstart.md`; registrar resultado nesta tarefa em `specs/001-alerta-cripto/tasks.md` e verificar `android/app/src/main/kotlin/com/pingo/alertacrypto/widget/PriceGlanceWidget.kt`.
- [ ] T049 Revisar `targetSdk 36`, permissões, tipos de foreground service, declarações de política Play, texto de privacidade, screenshots e artefato assinado antes do envio; validar configuração em `android/app/build.gradle.kts` e `android/app/src/main/AndroidManifest.xml` e registrar checklist/resultados em `specs/001-alerta-cripto/tasks.md`.

**Resultados locais da Fase 5**: `assembleDebug`, `testDebugUnitTest` (36 testes), `compileDebugAndroidTestKotlin`, `lint` e `bundleRelease` passaram. Os testes instrumentados foram compilados, mas não executados: este ambiente não tem `adb`/aparelho conectado. O AAB foi gerado sem assinatura porque as variáveis `PINGO_RELEASE_*` não estavam configuradas. Permanecem pendentes a execução instrumentada (incluindo T038/T039), a medição de bateria e as validações manuais T044 e T046–T048. O manifesto-fonte declara somente INTERNET, POST_NOTIFICATIONS, FOREGROUND_SERVICE e FOREGROUND_SERVICE_DATA_SYNC; o manifesto mesclado inclui RECEIVE_BOOT_COMPLETED pelo WorkManager para restaurar trabalho persistido, sem receiver próprio de boot nem início de `dataSync`.

## Dependências e ordem de execução

### Dependências entre fases

- **Fase 1** não depende de outra fase; estabelece a aplicação e a lista de preços.
- **Fase 2** depende do projeto inicializado na Fase 1; adiciona persistência local e CRUD.
- **Fase 3** depende das entidades/repositórios locais da Fase 2. T019 é pré-requisito obrigatório de T020: os testes de `CrossingDetector` devem existir e falhar antes da implementação.
- **Fase 4** depende de `LatestPrice` persistido na Fase 2 e do pipeline de atualização na Fase 3.
- **Fase 5** depende das funcionalidades correspondentes estarem implementadas; pode validar áreas distintas em paralelo quando os arquivos e ambientes de teste não forem compartilhados.

### Oportunidades de paralelismo

- Após T001–T003, T004 e T005 podem avançar em paralelo, pois alteram arquivos diferentes.
- Após T006, T007 e T008 podem avançar em paralelo; T009 integra ambos.
- Na Fase 5, T034–T039 e T042 são tarefas paralelizáveis por suíte/arquivo, após as implementações alvo; T040 deve preceder T041 para manter o fluxo test-first da política de frescor.
- T043 depende dos testes T034–T042. T045 deve existir antes das validações manuais T046–T048; essas validações devem ser feitas no aparelho real em execuções separadas e registradas, sem paralelismo se disputarem o mesmo aparelho.

### Caminho crítico

`T001 → T002/T003 → T005/T006 → T007/T008 → T009 → T010 → T012–T018 → T019 → T020 → T021–T029 → T030–T033 → T034–T041 → T042/T043 → T045 → T046–T049`

### MVP incremental

1. Entregar a Fase 1 para consulta de preços.
2. Acrescentar Fase 2 para criar e persistir alertas.
3. Na Fase 3, não implementar o detector antes de T019 comprovar a especificação por testes.
4. Completar notificação, resiliência/limites de segundo plano e estados transparentes antes de declarar alertas confiáveis.
5. Entregar o widget na Fase 4; finalizar testes de frescor e tema, validação manual guiada pelo `quickstart.md` em aparelho real, bateria e publicação na Fase 5.
