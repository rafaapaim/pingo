# Plano de implementação: Redesign da experiência do Pingo

**Feature**: `002-pingo-redesign`  
**Escopo**: camada de apresentação e preferências locais solicitadas pelo redesign.  
**Especificação**: [spec.md](./spec.md)  
**Referência normativa de marca e Ajustes**: [pingo-assets-e-ajustes.md](./refs/pingo-assets-e-ajustes.md)  
**Decisões técnicas e fontes**: [research.md](./research.md)  
**Dados locais novos**: [data-model.md](./data-model.md)  
**Validação manual**: [quickstart.md](./quickstart.md)

## 1. Objetivo e limites

Redesenhar Mercado, Alertas, formulário de alerta, Detalhes, Ajustes, navegação, widget e identidade sem alterar o domínio de preços/alertas nem a execução em segundo plano. Preservar os contratos atuais de Binance WebSocket, CoinGecko REST, Room de alertas, detecção/idempotência de cruzamentos, WorkManager, foreground service, permissões e textos/conteúdo das notificações.

Mudanças funcionais permitidas nesta entrega:

- Persistir favoritos e escolha de tema em Preferences DataStore.
- Adotar navegação Compose e deep link interno para Detalhes, usado por notificações e widget.
- Projetar os AlertCards combinando os alertas persistidos com cotações existentes.
- Aplicar recursos visuais aos ícones, marca, telas, notificações e widget.

Não criar uma fonte paralela de preços ou alertas. Não alterar entidades, DAOs, migrations ou semântica do Room de alertas. Não adicionar dependências além de `androidx.navigation:navigation-compose`; usar os componentes Compose/Material 3, DataStore, Glance e bibliotecas já presentes.

## 2. Estado atual verificado e decisões

- `PriceListScreen` mantém `selectedSymbol` em estado local de composição. Tocar numa linha apenas atualiza esse símbolo e mostra “Selecionada”; o valor não é persistido, não ordena a lista, não é compartilhado com widget/alertas e não modifica a cotação. No redesign, remover o rótulo ambíguo conforme FR-011 e preservar a função útil de seleção encaminhando o símbolo escolhido ao destino Detalhes e mantendo o estado de navegação ao voltar.
- O tema atual é um `darkTheme` transitório em `MainActivity`, inicializado com o tema do sistema e alterado por um botão. Não foi encontrado valor antigo persistido em DataStore/SharedPreferences; portanto não existe valor durável a migrar. O primeiro valor persistido será `SYSTEM`; se for encontrado durante implementação um arquivo/chave legado real, migrar somente valores reconhecidos Claro/Escuro e não inferir uma preferência a partir do modo atual do sistema.
- Notificações atualmente abrem `MainActivity` com `EXTRA_SYMBOL`; o widget abre `MainActivity` sem símbolo. Ambos passarão a usar a mesma URI interna do detalhe, mantendo também o extra de símbolo por moeda. Para uma `MainActivity` existente, `onNewIntent` chama `setIntent(intent)` e entrega a URI ao `NavController` para navegar/atualizar Detalhes. PendingIntents usam `FLAG_UPDATE_CURRENT` e request codes distintos e determinísticos por moeda, evitando que extras/URIs de moedas diferentes substituam o destino um do outro. O comportamento de conteúdo, canais, prioridade, permissão e cancelamento das notificações não muda.
- O widget já persiste um símbolo por instância e lê a última cotação persistida. Preservar configuração e atualização atuais; alterar apenas identidade, paleta e destino ao toque.
- Não usar `material-icons-extended`; os ícones da interface serão recursos vetoriais individuais. A marca deve seguir integralmente a geometria e os arquivos descritos na referência oficial.
- Os logos das moedas são fornecidos como PNGs na raiz do repositório, e não devem ser buscados de terceiros nem trocados por iniciais. Integrá-los como recursos locais de densidade neutra com nomes de recurso estáveis por símbolo, preservando alfa, proporção e cor; o ícone XRP preto precisa de fundo neutro claro para contraste nos dois temas.
- A composição de Mercado deve respeitar os requisitos compactos de FR-008/FR-010/FR-010a/FR-010b: manter conexão e horário em status global, mover o acesso ao monitoramento para um chip que navega a Ajustes e não renderizar o toggle/texto longo de monitoramento nessa tela.

## 3. Arquitetura e fluxo de apresentação

### Navegação

Adicionar `androidx.navigation:navigation-compose` como a única dependência nova. Declarar destinos tipados ou rotas centralizadas para:

1. Mercado;
2. Alertas;
3. Ajustes;
4. Detalhes da moeda, com argumento `symbol`;
5. Criar/editar alerta, com identificador opcional do alerta e moeda opcional pré-selecionada.

Usar um `NavHost` único na activity e navegação inferior para os três destinos principais. Detalhes e formulário são destinos de navegação comuns, não itens da barra inferior. Preservar busca/posição ao alternar destinos principais e definir back stack coerente para retorno do formulário e de Detalhes.

Definir a URI estável `pingo://asset/{symbol}` para o destino Detalhes. Notificações e widgets constroem intents explícitas para `MainActivity`, com a URI como `data` e o símbolo também em extra; o grafo Navigation mapeia a URI ao destino. Não registrar intent-filter público para o esquema customizado nesta entrega. Validar o argumento contra os seis símbolos suportados antes de consultar estado; argumento inválido não pode causar crash e deve retornar ao Mercado com mensagem apropriada.

Ao receber intent com a activity já criada, `onNewIntent` chama `setIntent(intent)` e encaminha a URI ao `NavController` por `handleDeepLink` ou navegação centralizada equivalente, inclusive com a activity em primeiro plano ou retornando do background. Testar abertura com app encerrado, em background, em foreground e moeda desconhecida. PendingIntents de notificação/widget usam `FLAG_UPDATE_CURRENT` e request codes estáveis distintos por moeda, verificando que códigos/URIs/extras de moedas diferentes não colidem nem reutilizam extra antigo.

Os ViewModels existentes continuam sendo os proprietários das fontes de estado. Integrar os ViewModels necessários ao NavHost pela activity ou pela API lifecycle já instalada; não incluir `hilt-navigation-compose`.

### Mercado e favoritos

- Manter os seis ativos sempre disponíveis e a busca por nome/símbolo.
- Exibir cada logo dos recursos locais conforme o símbolo, sem recolorir, e conferir o contraste do XRP em claro/escuro.
- Manter o cabeçalho/status de Mercado compacto; conexão e horário são globais, sem timestamp repetido nas linhas e sem controles ou explicações de Ajustes no Mercado.
- Expor favoritos primeiro e preservar ordem-base estável entre os demais ativos.
- Adicionar/remover favorito em DataStore, com rótulo e semântica acessíveis; a busca não modifica favoritos.
- Tocar numa moeda navega ao detalhe. O símbolo selecionado é argumento da rota, não um novo estado de domínio.
- “Selecionada” não será apresentado como sinônimo de favorito. Preservar a função anterior de seleção encaminhando o ativo tocado ao detalhe, que é o comportamento requerido por FR-012.

### Alertas

- Continuar observando Room por meio do repositório existente.
- Combinar no ViewModel de apresentação o fluxo dos alertas com o fluxo/preços em memória ou cotação persistida já existente, criando modelos de UI derivados.
- Para cada card, projetar cotação atual, distância ao alvo e estado. A distância usa cor neutra; não gravar cotação ou distância derivadas em Room.
- Resolver estado por precedência exata: (1) `repeatMode == ONE_SHOT && !armed` → Disparado; (2) `!enabled` → Pausado; (3) `enabled && !armed && repeatMode == REPEATING` → Aguardando rearme; (4) caso contrário → Ativo. A precedência permite que um disparo único já persistido continue identificado como Disparado.
- Para “Aguardando rearme”, ler `firedUp` persistido: `true` → “Reativa quando o preço voltar abaixo de R$ X”; `false` → “Reativa quando o preço voltar acima de R$ X”. Calcular X pela constante de histerese existente no domínio (`CrossingDetector.HYSTERESIS`), sem duplicar o valor, sem arredondar a comparação e arredondando somente o texto monetário. Como a constante é atualmente privada, expor seu valor por API imutável/pura do domínio se necessário; não mover a regra para a UI.
- Criar/editar usa um único formulário com Acima/Abaixo/Ambas, mantendo `Direction.BOTH`, tolerância numérica pt-BR e toggle “Repetir”. Reusar as operações existentes de CRUD.

### Tema, tokens e acessibilidade

- Usar Material 3 e Roboto nativa.
- Implementar o `ColorScheme` Material 3 e tokens light/dark do spec. `onSurface` aplica-se a texto em superfícies neutras; chips usam os pares específicos de texto/contêiner e variações usam seus tokens de texto em `background`/`surface`. Cores semânticas originais ficam para ícones, setas e preenchimentos. Medir e testar contraste >=4,5:1 em todos os pares especificados.
- Persistir `SYSTEM`, `LIGHT` ou `DARK` no mesmo DataStore de preferências do redesign. `SYSTEM` acompanha `isSystemInDarkTheme()`.
- Definir vetores individuais para ícones de navegação/ações, incluindo `query_stats` para Mercado. Não adicionar coleção de ícones Material.
- Implementar `PingoLogo`, launcher, ícone monocromático e ícone pequeno de notificação conforme a referência. A troca do ícone pequeno é somente visual.
- Integrar os seis PNGs existentes na raiz como recursos locais `drawable-nodpi`, sem recompressão/recolorização. O mapeamento é BTC → `btc.png`, ETH → `eth.png`, SOL → `sol.png`, XRP → `xrp.png`, ADA → `ada.png`, DOGE → `doge.png`. `AssetMark`/componente compartilhado resolve o recurso pelo enum `Asset` e é reutilizado em Mercado e Detalhes; quando um alerta ou widget mostrar a identificação da moeda em espaço compatível, usar o mesmo recurso e mapeamento.
- Preservar proporção e transparência dos PNGs. XRP é preto em fundo transparente: aplicar um contêiner circular claro neutro atrás do recurso, independente do tema do app, sem aplicar tint ao arquivo. Os demais logos permanecem com suas cores originais.
- Registrar dimensões, formato, caminho de origem e comportamento de contraste/legibilidade em `research.md`. A origem fornecida pelo usuário autoriza a integração nesta mudança, mas não prova licença para publicação; confirmar direitos de redistribuição antes de lançar o app.
- Cobrir semântica, alvos de toque de 48dp, fontes ampliadas, modo escuro e redução de animações.

### Mercado: correção de composição e hierarquia

A captura de validação documenta um desvio da especificação, não o layout de destino. Corrigir a composição existente em vez de reproduzir a captura:

- Remover de Mercado o controle interativo “Monitoramento contínuo” e a explicação longa de bateria/limitações. Essas configurações pertencem à tela Ajustes.
- Preservar o acesso ao estado do monitoramento como chip compacto, próximo do estado de conexão; ao tocar, navegar a Ajustes. Não fundir monitoramento e conectividade em um único estado.
- Mostrar conexão (Online/Reconectando/Offline) e o último horário disponível num bloco global compacto; não repetir “Atualizado às” em cada linha.
- Reduzir a altura ocupada antes da lista: usar o `Scaffold` principal já existente, evitar um segundo `Scaffold` aninhado no destino Mercado, respeitar insets uma única vez e remover espaçamentos/banners redundantes. Manter título, busca, status e lista, sem deslocar a lista para baixo por cópia de conteúdo de Ajustes.
- Compactar cada linha para conter apenas logo, nome, ticker, preço, variação e ação acessível de favorito, sem horário individual. Preservar toque na linha para abrir Detalhes, busca, ordenação e estado de favoritos.
- Não fixar um número absoluto de linhas visíveis para todos os aparelhos; verificar em Preview e em aparelho que os elementos não ficam ocultos por padding duplicado e que a lista inicia logo após a área de busca/status.

### Ajustes

Seguir a referência oficial de cinco cartões: Aparência, Monitoramento, Notificações, Bateria e Sobre. Reutilizar o estado já exposto por preferências e serviços; não duplicar status de conexão, preferências ou permissões. Manter as mensagens, estados, ações e acessibilidade detalhadas na referência.

### Widget e notificações

- Preservar moeda por instância, formatos Glance 2×1/2×2, leitura do último PriceTick persistido e política de atualização existente.
- Aplicar cores e identidade dia/noite do spec. Toque abre `pingo://asset/{símbolo configurado}`.
- Notificações de alerta mantêm texto pt-BR, valores BRL, canal, prioridade, permissão, idempotência e comportamento de toque. Usar a mesma URI para abrir Detalhes da moeda alertada.
- O ícone de status/foreground e as notificações de monitoramento só recebem ajuste visual compatível com a referência; não alterar serviço, frequência, timeout, boot, Worker ou permissões.

## 4. Sequência de implementação

### Fase 1 — Navegação e casca visual

1. Antes de substituir visualmente a UI, capturar as telas atuais e preservá-las em `refs/before/`.
2. Adicionar alias da biblioteca `navigation-compose` no catálogo de versões e a dependência no módulo Android.
3. Criar rotas centralizadas, `NavHost`, navegação inferior e barra superior apenas com `PingoLogo`.
4. Ligar os destinos principais existentes ao NavHost, preservando ViewModels e ações.
5. Introduzir `pingo://asset/{symbol}` e validação/entrada de deep link sem alterar ainda os chamadores de notificação/widget.
6. Testar navegação principal, back stack e rota inválida; criar/atualizar `@Preview` da casca/navegação em tema claro e escuro.

### Fase 2 — Preferências persistidas

1. Criar repositório Preferences DataStore para favoritos e tema; nenhuma alteração em Room.
2. Migrar chaves legadas somente se encontradas; caso contrário inicializar tema em Sistema.
3. Implementar operações/tipos de favoritos e tema conforme `data-model.md`.
4. Testar persistência após recriação/process restart, conjunto fechado de símbolos e tema padrão/migração.

### Fase 3 — Sistema visual e componentes compartilhados

1. Implementar Material 3 com tokens claros/escuros, Roboto e `PingoLogo` vetorial segundo a referência oficial.
2. Criar componentes compartilhados de ícone vetorial, `AssetMark` para os PNGs locais (com fallback por inicial somente se um recurso estiver ausente/ilegível), chips e superfícies acessíveis; não adicionar ícones em pacote.
3. Implementar seleção Sistema/Claro/Escuro usando a preferência persistida da Fase 2.
4. Medir contraste textual, verificar alvos e semântica de componentes compartilhados.
5. Criar `@Preview` claro/escuro dos componentes compartilhados, incluindo cada variante de chip e cada estado relevante.

### Fase 4 — Mercado e favoritos

1. Capturar e conferir a referência atual guardada em `refs/before/`; aplicar tokens e hierarquia da especificação, sem copiar o layout diagnosticado como incorreto.
2. Executar T059: remover toggle e explicação longa de Monitoramento contínuo de Mercado; manter chip compacto que abre Ajustes, conexão e horário em status global e sem horário repetido nos cards. Remover `Scaffold` aninhado/padding redundante se presente e reduzir os espaços superiores para que a lista comece logo após busca/status.
3. Executar T060: integrar os seis PNGs fornecidos na raiz como recursos `drawable-nodpi` locais, preservando transparência, proporção e cor; validar o mapeamento BTC/ETH/SOL/XRP/ADA/DOGE em Mercado e Detalhes e manter XRP preto sobre base circular clara neutra, sem tint. Não buscar logos de terceiros.
4. Redesenhar as linhas compactas com `AssetMark`, nome, ticker, preço, variação e ação acessível de favorito; remover “Selecionada” como rótulo, manter toque na linha para Detalhes, busca, favoritos primeiro e ordem-base estável.
5. Testar unitariamente ordenação, busca e atualização de favoritos, além do mapeamento de cada símbolo ao recurso correto e fallback apenas para recurso ausente/ilegível.
6. Adicionar testes instrumentados de Mercado cobrindo seis ativos, busca, favoritos, navegação ao detalhe, status global e ausência do toggle/horário por linha.
7. Criar `@Preview` claro/escuro de Mercado para dados carregados, carregamento, online, offline/desatualizado, busca sem resultado, variações de favoritos e composição compacta com os seis logos; inspecionar legibilidade do XRP em ambos os temas.

### Fase 5 — Alertas, formulário e Detalhes

1. Combinar cotações e alertas no ViewModel para preencher AlertCards sem persistir campos derivados.
2. Redesenhar Alertas e formulário único com três condições; manter CRUD e regras existentes.
3. Redesenhar Detalhes apenas com o escopo permitido, vinculando a rota de criar alerta.
4. Adicionar testes instrumentados de Criar alerta cobrindo validação, condições, toggle Repetir e confirmação de gravação/edição.
5. Testar projeções de AlertCard, valores/direções em pt-BR e BRL e exclusão com confirmação.
6. Criar `@Preview` claro/escuro de Alertas nos quatro estados (Ativo, Pausado, Aguardando rearme, Disparado), Criar alerta e Detalhes nos estados relevantes.
7. Testar UP, DOWN e BOTH para cruzamentos ascendentes e descendentes; para BOTH, cobrir disparo ascendente seguido de rearme inferior e disparo descendente seguido de rearme superior, mantendo `firedUp` e validando a frase/limiar de rearme em cada caso.

### Fase 6 — Ajustes

1. Implementar os cinco cartões de Ajustes, usando estados existentes e mensagens da referência.
2. Testar instrumentadamente tema, monitoramento, notificação, bateria, versão, privacidade e créditos.
3. Validar estados sem dados/carregamento e ações de permissão/configurações sem duplicar fonte de verdade.
4. Criar `@Preview` claro/escuro de Ajustes, incluindo estados relevantes de carregamento, Monitoramento, Notificações e Bateria.

### Fase 7 — Widget, notificações e regressão final

1. Aplicar tokens/identidade ao widget e manter uma moeda por instância, tamanhos e frescor.
2. Atualizar destino do widget e da notificação de alerta para `pingo://asset/{symbol}`; trocar apenas o ícone visual da notificação.
3. Testar configuração por instância, conteúdo 2×1/2×2, contraste e abertura de detalhes por ambos os deep links.
4. Executar regressão de notificações/idempotência, Binance WebSocket, CoinGecko fallback e monitoramento (Foreground Service, timeout e WorkManager).
5. Executar [quickstart.md](./quickstart.md) em aparelho real e confirmar que domínio, Room, permissões e execução em segundo plano não mudaram.
6. Testar deep links com app encerrado, em segundo plano e em primeiro plano (`onNewIntent` → `setIntent` → NavController), moeda desconhecida e PendingIntents de notificação/widget para moedas distintas (`FLAG_UPDATE_CURRENT`, extras corretos e request codes distintos).

**Gate obrigatório de cada fase**: build debug, todos os testes unitários existentes verdes e checklist visual da superfície alterada executado em tema claro e escuro antes de iniciar a fase seguinte. Na Fase 7, o gate também inclui build release/R8.

## 5. Estratégia de testes

- **Unitários**: ordenação de favoritos; conversão/validação de símbolos; mapeamento unívoco dos seis símbolos aos PNGs locais e fallback somente para recurso indisponível; projeção de AlertCard pela precedência de `repeatMode`/`enabled`/`armed`; distância e texto de rearme usando `firedUp` e constante de domínio; UP/DOWN/BOTH em cruzamento ascendente/descendente; enumeração/migração de preferência de tema; construção/validação de rotas; contraste de todos os pares tokenizados nos dois temas (falha <4,5:1).
- **Compose/UI**: navegação inferior e estado de retorno; Mercado compacto (chip de Ajustes, status/horário global, nenhum toggle ou timestamp por linha) e busca; logos e XRP em claro/escuro; seletores de favorito/tema; textos e estados dos AlertCards; formulário único; conteúdo de Detalhes; Ajustes e estados de carregamento.
- **Tema/acessibilidade**: tokens claro/escuro e Sistema; pares de contraste >=4,5:1; semântica dos botões/chips; alvos >=48dp; fonte ampliada e redução de animações; `@Preview` claro/escuro para telas e estados relevantes.
- **Integração**: preferências DataStore sobrevivem a recriação; favoritos e tema não modificam Room; intents abrem `Details(symbol)` com app encerrado/background/foreground; `onNewIntent` atualiza a rota do NavController; moeda desconhecida retorna ao Mercado; PendingIntents por moeda preservam extra e request code; widget mantém estado por instância.
- **Regressão**: testes atuais de cruzamento, notificação, frescor, monitoramento e widget permanecem inalterados e passando. Verificações instrumentadas e de aparelho real estão descritas no quickstart.

## 6. Riscos e mitigação

| Risco | Mitigação |
|---|---|
| Adicionar Navigation junto a telas que hoje usam estado condicional na activity pode quebrar back stack | Implementar um NavHost único e validar rotas, rotação e intents antes de remover o estado antigo |
| Um deep link customizado malformado pode causar rota inválida | Validar símbolos contra `Asset.supported`; manter PendingIntents explícitos e sem filtro externo |
| Não há valor de tema antigo persistido | Não inventar migração; iniciar em Sistema e preservar apenas valores legados encontrados de forma verificável |
| PNGs de moeda fornecidos não trazem licença/documentação de origem e XRP é preto em fundo transparente | Usar os PNGs locais conforme a especificação; manter XRP sobre fundo claro neutro; não afirmar licença pública e confirmar direitos antes de distribuir o app |
| Estado derivado de preço/alerta ficar obsoleto ou duplicado | Derivar no ViewModel a partir das fontes existentes; não armazenar preço, distância ou estado de card |
| Alterações visuais afetarem serviços/notificações | Limitar diffs à UI, URI/intent e `smallIcon`; validar idempotência, texto, canal e política de monitoramento em regressão |
