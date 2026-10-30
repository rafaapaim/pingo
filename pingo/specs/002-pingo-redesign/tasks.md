# Tarefas: Redesign da experiência do Pingo

**Fonte de verdade**: [spec.md](./spec.md), [plan.md](./plan.md), [pingo-assets-e-ajustes.md](./refs/pingo-assets-e-ajustes.md), [research.md](./research.md), [data-model.md](./data-model.md) e [quickstart.md](./quickstart.md).

## Regras de execução

- Executar uma fase completa por etapa, na ordem 1 a 7. Não iniciar a fase seguinte até concluir o gate da fase atual.
- Em **cada fase**, finalizar com build debug, todos os testes unitários existentes verdes e checklist visual em tema claro **e** escuro. Se o gate falhar, corrigir antes de avançar.
- Itens de implementação marcados como concluídos foram compilados; gates visuais/manuais seguem pendentes quando dependem de aparelho conectado.
- Marcar `[P]` apenas em tarefas que podem ser feitas em paralelo sem depender dos arquivos/saídas uma da outra.
- Não alterar lógica de domínio, Room de alertas, fontes de preços, thresholds, serviços, agendamento, permissões ou conteúdo/comportamento das notificações; exceção visual permitida: ícone pequeno e URI de destino.
- Manter os estados transitórios/derivados na apresentação; persistir apenas favoritos e tema neste escopo.

## Fase 1 — Navegação e casca visual

- [x] T001 Adicionar `androidx.navigation:navigation-compose` como a única dependência nova no catálogo e módulo Android, conforme [research.md](./research.md).
- [x] T002 [P] Criar destinos/rotas centralizadas para Mercado, Alertas, Ajustes, Detalhes (`symbol`) e criar/editar alerta (`alertId`/`symbol`).
- [x] T003 Criar um `NavHost` único e navegação inferior para Mercado, Alertas e Ajustes, preservando ViewModels existentes e estado de destino ao alternar abas.
- [x] T004 Implementar rota interna `pingo://asset/{symbol}` para Detalhes, validar símbolos suportados e fornecer fallback visível para URI/símbolo inválido.
- [x] T005 Integrar composables atuais aos destinos sem alterar CRUD, estado de preços ou alertas; garantir retorno/back stack para Detalhes e formulário.
- [x] T006 Adicionar testes unitários de resolução/validação de rota e testes instrumentados básicos de navegação, incluindo back stack, rotação e URI inválida.
- [ ] T007 Executar o gate da Fase 1: build debug, suíte unitária existente completa verde e checklist visual claro/escuro para navegação, barra superior com apenas marca, seleção de destino, back e estado preservado. T052 deve estar concluída antes da troca visual.

## Fase 2 — Preferências persistidas

- [x] T008 Criar repositório Preferences DataStore separado do estado de monitoramento para `favorite_symbols` e `theme_mode`, sem tocar no Room.
- [x] T009 Implementar validação do conjunto fechado de símbolos, atualização atômica de favorito e ordenação canônica definida no [data-model.md](./data-model.md).
- [x] T010 Implementar enum de tema `SYSTEM`/`LIGHT`/`DARK`, default `SYSTEM`, tratamento de valor desconhecido e migração somente de chave legada efetivamente encontrada.
- [x] T011 Adicionar testes unitários de favorito (inclusão, remoção, inválidos, ordem estável) e tema (defaults, valores válidos/desconhecidos e migração aplicável).
- [x] T012 Adicionar testes de integração/instrumentados confirmando persistência após recriação/process restart e ausência de alterações no Room de alertas.
- [ ] T013 Executar o gate da Fase 2: build debug, todos os testes unitários existentes verdes e checklist visual claro/escuro do estado de preferências/default Sistema. Registrar que ainda não há controles visuais finais se estes pertencem à fase seguinte.

## Fase 3 — Sistema visual e componentes compartilhados

- [x] T014 Implementar `ColorScheme` Material 3 com tokens do spec para claro/escuro, Roboto nativa, estilos tipográficos e shapes especificados.
- [x] T015 Implementar `PingoLogo` e VectorDrawables de launcher/adaptive/monochrome e ícone de status conforme a referência oficial; nenhum raster novo.
- [x] T016 Criar componentes vetoriais individuais para navegação/ações (incluindo `query_stats` para Mercado), sem `material-icons-extended`.
- [ ] T017 **Fase 3**: Criar o componente compartilhado `AssetMark` preparado para recursos locais por símbolo; preservar proporção, alfa e cores, manter fallback com inicial somente para recurso ausente/ilegível e dar suporte a um fundo claro neutro para XRP. A integração e validação dos seis arquivos da raiz em Mercado e Detalhes ficam na T060 (Fase 4).
- [x] T018 Conectar tema persistido à aplicação imediata de Sistema/Claro/Escuro; verificar alterações do sistema no modo Sistema.
- [x] T019 [P] Adicionar teste unitário de contraste que calcule todos os pares texto/contêiner de chip, texto/fundo de variação, `onSurface`/fundo neutro e `primary` do lockup/fundo neutro nos temas claro e escuro; incluir todos os pares definidos em `spec.md` e falhar para qualquer razão <4,5:1. Variação deve ser medida sobre `background` e `surface`; chip usa seu foreground/container pareado.
- [ ] T020 Adicionar testes instrumentados dos componentes compartilhados para semântica, rótulos, alvos >=48dp e rendering dos vetores em tema claro/escuro.
- [ ] T021 Executar o gate da Fase 3: build debug, todos os testes unitários existentes verdes e checklist visual claro/escuro da marca, tokens, ícones, chips, contraste e estados vazios. Confirmar também os previews T053.

## Fase 4 — Mercado e favoritos

- [x] T022 Redesenhar Mercado com título “Moedas”, seis ativos, busca, estado/horário global e preços/variações disponíveis conforme `spec.md`.
- [x] T023 Integrar favoritos do DataStore, ordenar favoritos primeiro com ordem-base estável e expor controle acessível de adicionar/remover favorito.
- [x] T024 Remover o rótulo “Selecionada”; ao tocar na linha, navegar ao Detalhe da moeda e conservar busca/posição ao retornar.
- [x] T025 Adicionar testes unitários de ordenação combinada com busca, conjunto vazio, todas favoritas e nenhuma favorita.
- [x] T026 Adicionar testes instrumentados de Mercado cobrindo os seis ativos, pesquisa por nome/símbolo, favoritos, persistência e navegação ao detalhe; verificar também status/horário global e ausência de toggle de monitoramento e de horário repetido por linha.
- [x] T027 Adicionar verificação instrumentada/automatizada de estado online, offline, erro, frescor e textos pt-BR/BRL sem alterar os produtores desses estados.
- [ ] T028 Executar o gate da Fase 4: build debug, todos os testes unitários existentes verdes e checklist visual claro/escuro de Mercado, ordenação, busca, estados de cotação, composição compacta, logos e ações acessíveis. Confirmar T059, T060 e o preview T054.

## Fase 5 — Alertas, formulário e Detalhes

- [x] T029 Combinar fluxos já existentes de alertas e preços no ViewModel de apresentação para derivar cotação atual, distância neutra e estado do AlertCard; não persistir valores derivados.
- [x] T030 Redesenhar AlertCards com direção em português e estado derivado pela precedência: ONE_SHOT desarmado = Disparado; desabilitado = Pausado; repetível habilitado desarmado = Aguardando rearme; senão = Ativo. O texto de rearme usa `firedUp` e a constante do domínio, sem duplicá-la.
- [x] T031 Implementar tela única de criar/editar com moeda, valor-alvo, Acima/Abaixo/Ambas e Repetir; manter operações CRUD e semântica `Direction.BOTH`.
- [x] T032 Redesenhar Detalhes somente com logo/fallback, nome, ticker, preço BRL, variação 24h, horário e ação “Criar alerta”.
- [x] T033 [P] Adicionar testes unitários de projeção de AlertCard cobrindo precedência de estado, `firedUp`, texto/limiar de rearme via constante do domínio, preço ausente e distância.
- [ ] T034 Adicionar testes instrumentados de Criar alerta para tela única, moedas, três condições, validação decimal BR, toggle Repetir, edição e confirmação acessível.
- [ ] T035 Adicionar testes instrumentados de Alertas/Detalhes para conteúdo permitido, exclusão com confirmação, navegação e texto em pt-BR/BRL.
- [ ] T036 Executar o gate da Fase 5: build debug, todos os testes unitários existentes verdes e checklist visual claro/escuro de Alertas, AlertCard, formulário e Detalhes. Confirmar também T055 e T057.

## Fase 6 — Ajustes

- [x] T037 Implementar os cinco cartões funcionais de Ajustes conforme [pingo-assets-e-ajustes.md](./refs/pingo-assets-e-ajustes.md), reutilizando estado e operações existentes.
- [ ] T038 Implementar seletor de tema acessível, controles de monitoramento/notificações, links de bateria/privacidade e expansões Sobre com mensagens da referência.
- [x] T039 Adicionar testes unitários de mapeamento dos estados para chips, textos e ações sem conceder permissões ou inventar estado.
- [ ] T040 Adicionar testes instrumentados de Ajustes: persistência de tema, estado real do monitoramento, permissão permitida/negada, navegação de configurações, bateria, versão, privacidade e créditos.
- [ ] T041 Medir contraste em todos os textos e chips de Ajustes em ambos os temas; validar semântica TalkBack, live region, alvos >=48dp, quebra com fonte ampliada e remoção de animações.
- [ ] T042 Executar o gate da Fase 6: build debug, todos os testes unitários existentes verdes e checklist visual claro/escuro dos cinco cartões, estados de carregamento/erro e ações. Confirmar também T056.

## Fase 7 — Widget, notificações e regressão

- [x] T043 Atualizar widget com marca/tokens para claro/escuro, preservando configuração por instância, uma moeda, tamanhos 2×1/2×2, leitura persistida e política sem polling.
- [x] T044 Atualizar toque do widget para `pingo://asset/{symbol}` usando o símbolo configurado naquela instância.
- [x] T045 Atualizar a Intent de toque da notificação de alerta para `pingo://asset/{symbol}` e trocar somente o ícone pequeno pelo vetor definido na referência; preservar texto, canal, prioridade, permissão e idempotência.
- [ ] T046 Adicionar testes instrumentados de widget em 2×1/2×2 e duas instâncias, incluindo moeda, frescor, tema, formatação e destino correto.
- [ ] T047 Adicionar teste de integração de deep link de notificação e regressão de texto pt-BR/BRL, canal, prioridade e ID/evento idempotente.
- [ ] T048 Executar regressão automatizada e instrumentada de Binance WebSocket, CoinGecko fallback, Worker, serviço `dataSync`/`onTimeout`, permissões, boot e modo economia de bateria; não alterar thresholds nem agendamento.
- [ ] T049 Executar o roteiro completo em aparelho real conforme [quickstart.md](./quickstart.md), registrando aparelho, versão Android e evidências; cobrir notificação, WebSocket/fallback, monitoramento e widget.
- [ ] T050 Medir contraste final de telas, chips, widget e notificação; realizar inspeção TalkBack e fonte ampliada nos fluxos principais.
- [ ] T051 Executar o gate final: build debug e release/R8, todos os testes unitários existentes verdes, testes instrumentados aplicáveis e checklist visual final em claro/escuro de todas as telas, widgets e notificações. Confirmar T047–T050 e T058.

## Tarefas complementares vinculadas às fases

Os IDs T052–T058 preservam a numeração já publicada. Executá-los na fase indicada abaixo; os gates originais dependem da conclusão das tarefas complementares daquela fase.

- [ ] T052 **Fase 1, antes de T003**: Capturar a UI atual antes de qualquer substituição e preservar as imagens em `refs/before/`; registrar quais telas/estados foram capturados.
- [x] T053 **Fase 3, após T014/T016** [P]: Criar `@Preview` claro/escuro dos componentes compartilhados e chips em todas as variantes: positivo, negativo, atenção e neutro, verificando o par de texto/contêiner correspondente.
- [ ] T054 **Fase 4, após T022/T023/T059/T060**: Criar `@Preview` claro/escuro de Mercado para conteúdo carregado, carregando, online, offline/desatualizado, busca sem resultados, variações de favoritos e layout compacto com logos locais.
- [x] T055 **Fase 5, após T029–T032**: Criar `@Preview` claro/escuro de Alertas nos quatro estados (Ativo, Pausado, Aguardando rearme, Disparado), de Criar alerta e de Detalhes para dados disponíveis/ausentes/desatualizados.
- [x] T056 **Fase 6, após T037/T038**: Criar `@Preview` claro/escuro de Ajustes, incluindo estados relevantes de carregamento, Monitoramento, Notificações e Bateria.
- [x] T057 **Fase 5, após T030**: Adicionar testes com alertas armados para cruzamentos ascendentes e descendentes: UP dispara somente no ascendente, DOWN somente no descendente e BOTH nos dois, sempre validando `firedUp`. Para BOTH, testar separadamente disparo ascendente → rearme somente no limite inferior → texto “abaixo de R$ X” e disparo descendente → rearme somente no limite superior → texto “acima de R$ X”. Confirmar os limiares com a constante única de histerese, sem duplicar o valor.
- [ ] T058 **Fase 7, após T045/T046**: Testar deep link com app encerrado, em segundo plano e em primeiro plano; no foreground validar `onNewIntent` → `setIntent` → entrega da rota ao NavController. Símbolo desconhecido deve cair em Mercado sem crash. Validar PendingIntents de notificação e widget para moedas diferentes: URI/extra corretos, `FLAG_UPDATE_CURRENT` e request codes distintos.
- [ ] T059 **Fase 4, correção visual**: Remover de Mercado o toggle e a explicação longa de Monitoramento contínuo; manter chip compacto que navega a Ajustes, conexão e último horário em um bloco global compacto e nenhum timestamp por linha. Remover `Scaffold` aninhado e padding/insets duplicados caso existam, reduzir espaços superiores redundantes e conferir a posição da lista na primeira dobra em telas representativas; não alterar estado, frequência ou lógica do monitoramento.
- [ ] T060 **Fase 4, após T017/T059**: Integrar os seis arquivos PNG da raiz (`btc.png`, `eth.png`, `sol.png`, `xrp.png`, `ada.png`, `doge.png`) como recursos locais `drawable-nodpi`; verificar mapeamento BTC/ETH/SOL/XRP/ADA/DOGE em Mercado e Detalhes, preservação de alfa, proporção e cores e fallback por inicial somente se o recurso estiver ausente/ilegível. Validar o XRP preto/transparente sobre fundo circular claro neutro, sem tint, nos temas claro e escuro. Adicionar teste do mapeamento e preview dos seis logos; não buscar assets de terceiros.
