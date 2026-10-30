# Especificação: Redesign da experiência do Pingo

**Feature Branch**: `002-pingo-redesign`  
**Criado**: 2026-10-29  
**Status**: Draft  
**Documento de origem**: [Pingo_SPEC_Redesign.docx](../../Pingo_SPEC_Redesign.docx)
**Baseline funcional**: [spec original do aplicativo](../001-alerta-cripto/spec.md)
**Referência oficial de marca e Ajustes**: [pingo-assets-e-ajustes.md](./refs/pingo-assets-e-ajustes.md)
**Ativos de moedas**: imagens `btc.png`, `eth.png`, `sol.png`, `xrp.png`, `ada.png` e `doge.png` na raiz do repositório.

> **Precedência**: Para marca/VectorDrawables e estrutura/comportamento de Ajustes, prevalece a referência oficial `refs/pingo-assets-e-ajustes.md`, exceto pelos tokens específicos de texto/contêiner e pela regra de contraste definidos nesta especificação. Para os logos das moedas, prevalecem os seis PNGs fornecidos na raiz e o mapeamento desta especificação. Para Mercado, os requisitos de hierarquia, compacidade e informação global abaixo prevalecem sobre a captura de validação enviada em 2026-10-30, que registra a implementação atual e não é referência de design desejado. Para as demais divergências funcionais e de escopo, prevalecem as decisões de esclarecimento abaixo e nesta especificação. Nenhuma decisão visual altera as regras de domínio existentes.

## Objetivo

Modernizar a experiência do Pingo com identidade visual própria, leitura rápida das cotações e alertas mais fáceis de compreender. O redesign abrange Mercado, Alertas, criação/edição de alertas, detalhes da moeda, Ajustes, navegação e widget.

Esta especificação trata da apresentação e da experiência da pessoa usuária. O comportamento de domínio e os recursos existentes de cotações, persistência, alertas, notificações e monitoramento devem permanecer compatíveis com a especificação funcional original, salvo quando um requisito abaixo explicitar uma mudança de apresentação ou interação.

## Escopo

### Incluído

- Criar uma identidade visual consistente para o Pingo, incluindo marca e ícones.
- Redesenhar as telas Mercado, Alertas, Criar/editar alerta, Detalhes da moeda e Ajustes.
- Introduzir navegação inferior entre Mercado, Alertas e Ajustes.
- Melhorar hierarquia visual, estados, acessibilidade e suporte aos temas Sistema, Claro e Escuro.
- Adaptar a apresentação do widget Glance e das notificações à identidade do produto.

### Fora de escopo

- Alterar preços, fontes de cotação, conjunto de moedas suportadas ou frequência de atualização.
- Alterar regras de cruzamento, direção, histerese, rearme, persistência ou entrega idempotente de alertas.
- Alterar agendamento, política de bateria, permissões ou comportamento de execução em segundo plano.
- Adicionar negociação, compra/venda, carteira, custódia, recomendações financeiras, login, backend ou sincronização em nuvem.
- Em Detalhes da moeda: gráfico, seletor de períodos, máxima/mínima de 24 horas, variação absoluta e seção “Sobre a moeda”. Esses itens não fazem parte desta entrega, mesmo se aparecerem nas pranchas.
- Exigir correspondência pixel a pixel com as pranchas conceituais.

## Princípios de experiência

- O Pingo deve parecer simples, confiável, preciso e acolhedor; não deve parecer uma corretora nem um terminal de trading.
- Priorizar informação essencial e reduzir repetição visual e altura ocupada por controles.
- Usar linguagem orientada à pessoa usuária; não expor nomes internos como `UP`, `DOWN` ou jargões sem explicação.
- Nunca representar alta ou baixa apenas pela cor: combinar cor com sinal, seta ou texto.
- Não exibir como atual uma cotação desatualizada, indisponível ou sem horário confiável.
- Mudanças visuais não podem causar saltos de layout nem piscar a tela inteira durante atualizações de preço.

## Histórias de usuário

### US1 — Consultar o Mercado (P1)

Como pessoa que acompanha criptomoedas, quero encontrar rapidamente preço, variação e estado das cotações para entender o mercado sem ler informação repetida.

**Teste independente**: abrir Mercado, localizar os seis ativos suportados, pesquisar um ativo, conferir o estado global e abrir detalhes.

### US2 — Entender e administrar alertas (P1)

Como pessoa que acompanha preços, quero ver preço-alvo, cotação atual, distância e estado de cada alerta para saber o que acontecerá e administrar alertas sem confundir estados internos.

**Teste independente**: abrir Alertas com estados ativos e pausados, conferir os dados dos cards e editar, pausar, retomar ou excluir um alerta.

### US3 — Criar ou editar um alerta com clareza (P1)

Como pessoa que quer ser avisada sobre um preço, quero configurar a moeda, a direção, o alvo e a repetição em um fluxo simples, com validação e explicação compreensíveis.

**Teste independente**: criar e editar alertas ascendentes, descendentes e em ambas as direções, usando entrada decimal brasileira e verificando que a regra de negócio existente não muda.

### US4 — Consultar detalhes e preferências (P2)

Como pessoa usuária, quero consultar os detalhes de uma moeda e ajustar tema e monitoramento em locais previsíveis, entendendo permissões, restrições e fontes de dados.

**Teste independente**: abrir detalhes, alternar os temas Sistema/Claro/Escuro e revisar preferências e informações do aplicativo.

### US5 — Consultar o widget (P2)

Como pessoa que consulta preços rapidamente, quero um widget legível e consistente com o aplicativo, que apresente cotação e estado sem controles que não funcionem com confiabilidade.

**Teste independente**: adicionar e configurar o widget, conferir o conteúdo nos tamanhos disponíveis e tocar para abrir a tela correspondente no aplicativo.

## Requisitos funcionais

### Navegação e identidade

- **FR-001**: O aplicativo MUST oferecer navegação inferior com os destinos Mercado, Alertas e Ajustes, indicando claramente o destino atual.
- **FR-002**: O aplicativo MUST usar a marca Pingo e uma família coerente de ícones vetoriais; ícones funcionais MUST NOT ser substituídos por emojis.
- **FR-002a**: A barra superior MUST exibir somente o lockup `PingoLogo` (marca vetorial e texto Pingo); MUST NOT conter sino, engrenagem ou ação de tema.
- **FR-003**: A marca MUST seguir a geometria e os VectorDrawables de `refs/pingo-assets-e-ajustes.md`: gota na grade 108×108 com ponto vazado, ícones adaptativos launcher/round com versão monocromática e marca `ic_pingo_mark` vetorial colorida pelo tema. Nenhum PNG novo pode substituir esses vetores.
- **FR-003a**: O lockup horizontal MUST usar a marca vetorial a 28dp e “Pingo” em Roboto Medium 22sp, cor `primary`, com espaçamento de 8dp; o launcher MUST ter fundo vetorial em gradiente roxo e manter o ponto vazado.
- **FR-003b**: A marca monocromática MUST ser declarada no ícone adaptativo para ícones temáticos do Android 13 ou superior. A variante redonda MUST ser vinculada em `android:roundIcon`.
- **FR-003c**: O ícone pequeno das notificações MUST usar `ic_stat_pingo` vetorial branco, com a cor `primary` como cor de destaque, sem alterar canal, prioridade, entrega ou navegação existente.
- **FR-004**: A pessoa MUST conseguir navegar entre destinos sem perder estado de tela relevante, como busca ou posição, quando retornar a um destino.

### Mercado

- **FR-005**: Mercado MUST ter o título “Moedas” e, sem busca ativa, apresentar BTC, ETH, SOL, XRP, ADA e DOGE na lista; favoritas aparecem primeiro e as demais continuam acessíveis na mesma lista.
- **FR-005a**: A busca MUST localizar as seis moedas por nome ou símbolo e filtrar a lista enquanto houver texto de busca; limpar a busca restaura os seis ativos, ordenados com favoritos primeiro.
- **FR-006**: Cada linha de moeda MUST apresentar o PNG fornecido na raiz do repositório, de acordo com o mapeamento BTC → `btc.png`, ETH → `eth.png`, SOL → `sol.png`, XRP → `xrp.png`, ADA → `ada.png`, DOGE → `doge.png`. Os arquivos MUST ser integrados como recursos locais, preservando transparência, proporção e cores originais; MUST NOT substituí-los por círculos com iniciais, emojis, logos baixados ou vetores inventados. O logo MUST permanecer legível nos temas claro e escuro; em particular, o `xrp.png` preto/transparente MUST ser mostrado sobre fundo claro neutro com contraste suficiente. Cada linha também MUST apresentar nome, ticker, cotação BRL e variação percentual de 24 horas, quando disponíveis.
- **FR-007**: A variação percentual MUST usar duas casas decimais. Variação positiva ou negativa MUST ser distinguível por texto/sinal ou ícone, além da cor.
- **FR-008**: O horário da última cotação MUST ser apresentado uma única vez no status global da lista. As linhas individuais MUST NOT repetir horário ou texto “Atualizado às”.
- **FR-009**: O status global MUST distinguir Online, Reconectando e Offline e apresentar o horário global da última cotação disponível quando existir.
- **FR-010**: O monitoramento MUST ser indicado somente por um chip compacto, em posição previsível próxima ao status global. Tocar no chip MUST abrir Ajustes; conexão e estado do monitoramento continuam semanticamente distintos. Mercado MUST NOT mostrar o toggle “Monitoramento contínuo”, sua descrição longa, controles de permissão ou mensagens explicativas de Ajustes.
- **FR-010a**: Em Mercado, o status de conexão e o horário global MUST ocupar uma área compacta, sem banner alto ou parágrafos de estado duplicados. O chip de monitoramento MUST permanecer visível sem deslocar a lista para fora da primeira dobra.
- **FR-010b**: A hierarquia vertical de Mercado MUST ser compacta: título, busca, status/chip e lista devem começar logo abaixo da barra de marca, sem espaço em branco decorativo amplo. Linhas/cards MUST usar apenas a altura necessária para ícone, nome/ticker, preço/variação e favorito; MUST NOT aumentar a altura para repetir metadados já apresentados globalmente.
- **FR-011**: A ação de favorito MUST usar estado e rótulo acessíveis que comuniquem adicionar ou remover favorito. A interface MUST NOT usar “Selecionada” como substituto ambíguo de favorito.
- **FR-012**: Tocar em uma moeda MUST abrir seus detalhes.
- **FR-012a**: Mercado MUST NOT apresentar seletor de período.

### Alertas

- **FR-013**: A tela Alertas MUST apresentar um resumo discreto da quantidade de alertas ativos e uma ação primária “Criar alerta”.
- **FR-014**: Cada card de alerta MUST apresentar moeda, direção em português, preço-alvo, preço atual, distância até o alvo e estado do alerta, quando os dados necessários estiverem disponíveis.
- **FR-015**: O estado do AlertCard MUST ser derivado por esta ordem de precedência: (1) `repeat=false` (`repeatMode == ONE_SHOT`) e `armed == false` → “Disparado”; (2) `enabled == false` → “Pausado”; (3) `enabled == true`, `armed == false` e repetível → “Aguardando rearme”; (4) qualquer outro caso → “Ativo”. “Disparado” só pode ser exibido com o estado persistido que o justifica.
- **FR-016**: A tela MUST permitir editar, pausar/retomar e excluir cada alerta individualmente. Excluir MUST solicitar confirmação.
- **FR-017**: Os rótulos e descrições MUST NOT expor `UP`/`DOWN` nem detalhes internos do modelo de dados.
- **FR-018**: A opção de repetição MUST ser um toggle rotulado “Repetir”. Quando ativo, deve explicar: “Você receberá outro aviso quando o preço voltar a cruzar o valor-alvo, depois de retornar ao outro lado com uma margem de 0,1%.”
- **FR-019**: O redesign MUST manter a semântica existente de alerta de alta, de queda e de ambas as direções, incluindo a histerese de 0,1%, disparo único e idempotência.

### Criar e editar alerta

- **FR-020**: Criar e editar alerta MUST ocorrer em uma única tela/formulário, sem fluxo em etapas ou navegação intermediária. O formulário MUST apresentar seleção de moeda, condição, valor-alvo e toggle “Repetir”.
- **FR-020a**: O seletor de condição MUST oferecer exatamente “Acima”, “Abaixo” e “Ambas”, mapeados respectivamente às direções existentes de alta, queda e `Direction.BOTH`; selecionar “Ambas” não pode ser reduzido a duas regras independentes nem mudar a semântica do domínio.
- **FR-021**: O formulário MUST mostrar a cotação atual da moeda como contexto, quando disponível, sem preencher ou modificar o valor-alvo silenciosamente.
- **FR-022**: A entrada MUST aceitar e validar o separador decimal brasileiro e valores positivos, mostrando erro junto ao campo inválido.
- **FR-023**: A explicação do toggle “Repetir” MUST deixar claro que um novo aviso exige retornar ao outro lado do alvo com a margem de 0,1% e depois cruzar novamente o valor-alvo.
- **FR-024**: Após salvar, o aplicativo MUST retornar à lista de alertas e apresentar uma confirmação acessível; falhas MUST permanecer visíveis.

### Detalhes da moeda

- **FR-025**: A tela de detalhes MUST apresentar o PNG da moeda mapeado em FR-006, além de nome, ticker, preço atual em BRL, variação percentual de 24 horas e horário da cotação, quando disponíveis. O recurso local MUST manter proporção, transparência, cores e contraste em ambos os temas; o fallback por inicial fica reservado a arquivo ausente ou ilegível e MUST NOT substituir os logos fornecidos como comportamento normal.
- **FR-026**: A tela MUST oferecer a ação “Criar alerta” para a moeda exibida; acioná-la MUST abrir o formulário único com essa moeda pré-selecionada, sem preencher o preço-alvo.
- **FR-027**: Gráfico, seletor de período, máxima/mínima 24h, variação absoluta e seção “Sobre a moeda” MUST NOT fazer parte desta entrega.

### Ajustes

- **FR-028**: Ajustes MUST seguir a estrutura de cinco cartões funcionais definida em `refs/pingo-assets-e-ajustes.md`, com barra superior contendo `PingoLogo`, título “Ajustes”, conteúdo rolável, margens de 20dp, espaçamento de 16dp e rodapé com a assinatura da marca. MUST NOT usar mockup estático.
- **FR-029**: O cartão Aparência MUST oferecer seletor segmentado Sistema/Claro/Escuro com ícones, indicar a escolha, aplicar e persistir a seleção imediatamente; padrão Sistema. MUST explicar “Sistema segue o tema do seu celular.” e migrar uma preferência antiga Claro/Escuro, se existente.
- **FR-030**: O cartão Monitoramento MUST apresentar o toggle “Monitoramento contínuo” com a descrição “Atualiza os preços quase em tempo real, mesmo com o app fechado. Consome mais bateria e o Android pode interromper.” e uma seção “Como funciona” recolhida por padrão. O status MUST ser real e distinguir:
  - Econômico: “Verificações aproximadamente a cada 15 minutos. Os horários podem atrasar.”
  - Iniciando: “Iniciando monitoramento…”
  - Ativo: “Cotações quase em tempo real. Última atualização às HH:mm:ss.”
  - Pausado pela economia de bateria: “Pausado enquanto a economia de bateria está ativa. Volta sozinho depois.”
  - Interrompido pelo Android: “O Android encerrou o monitoramento contínuo. Usando verificações econômicas.” e ação “Reativar”.
  - Offline: “Sem conexão. Tentando reconectar.”
  
  A seção “Como funciona” MUST explicar que Contínuo mantém a conexão aberta e mostra uma notificação fixa, enquanto Econômico verifica periodicamente, gasta menos bateria e pode perder cruzamentos rápidos. Se ativar falhar, o toggle MUST voltar ao estado anterior e apresentar “Não foi possível iniciar o monitoramento contínuo. Tente novamente.” O cartão descreve o modo; o estado da fonte permanece no Mercado.
- **FR-031**: O cartão Notificações MUST refletir corretamente estes estados e ações: Permitidas/Android anterior ao 13 — “Ativadas”, explicação de que a pessoa será avisada quando o preço cruzar o alerta e ação “Gerenciar”; negadas com solicitação possível — “Desativadas”, explicação de que o Pingo não pode avisar e ação “Permitir notificações”; negadas definitivamente — a mesma explicação e ação “Abrir configurações”. Gerenciar e Abrir configurações MUST abrir as configurações de notificação do app; Permitir notificações MUST solicitar a permissão em runtime. Ao voltar das configurações, MUST reler a permissão.
- **FR-031a**: O cartão Bateria MUST exibir “Alguns celulares encerram apps em segundo plano para economizar bateria. Se os alertas atrasarem, permita que o Pingo funcione sem restrições.” O status “Sem restrições” ou “Otimizada” só MUST ser mostrado se consultável pelo sistema. A ação “Abrir configurações do app” MUST abrir os detalhes do aplicativo. MUST NOT solicitar isenção de otimização por diálogo do sistema.
- **FR-031b**: O cartão Sobre MUST apresentar versão/build obtidos de `BuildConfig`/`PackageInfo`, abrir a política de privacidade usando URL definida em um único lugar do código e expandir “Fontes de dados” com “Preços: Binance (tempo real) e CoinGecko (reserva). Podem ter atraso ou pequenas diferenças entre si.” MUST também exibir o aviso “O Pingo mostra preços e avisa quando eles cruzam valores definidos por você. Não é recomendação de investimento.”
- **FR-031c**: Os cinco cartões MUST usar superfícies com raio 16dp e padding 16dp; títulos 20sp semibold; linhas com altura mínima de 48dp. O rodapé MUST exibir a marca e “Acompanhe. Monitore. Seja alertado.”
- **FR-031d**: Ajustes MUST reutilizar preferências e estados já existentes de tema, monitoramento, conexão e permissão; MUST NOT introduzir uma nova fonte de verdade.
- **FR-031e**: A linha do toggle MUST ser um único alvo de alternância, anunciado como “Monitoramento contínuo, ativado” ou “Monitoramento contínuo, desativado”. A opção selecionada do tema MUST ser anunciada e mudanças do monitoramento MUST ser comunicadas por live region educada. Chips MUST incluir texto e, quando útil, ícone; cor não pode ser o único indicador. Texto comum em superfícies neutras MUST usar `onSurface`; texto de variação MUST usar os tokens de variação sobre `background`/`surface`; o lockup da marca MUST usar `primary`; texto sobre chips MUST usar os tokens de texto/container pareados abaixo. Textos MUST quebrar linhas sem truncar, botões MUST ocupar a largura disponível quando não couberem junto ao texto, alvos MUST ter ao menos 48dp e contraste textual MUST ser de pelo menos 4,5:1. Ao remover animações, expandir/recolher sem animação. Durante carregamento, mostrar placeholders simples sem inventar estado.

### Widget e notificações

- **FR-032**: Cada instância do widget MUST continuar configurada para uma única moeda e preservar os tamanhos Glance 2×1 e 2×2, identidade Pingo, contraste e indicação de atualização.
- **FR-033**: Nos dois tamanhos, o widget MUST mostrar somente a moeda configurada para aquela instância, respeitando a densidade disponível e os estados de frescor já existentes.
- **FR-034**: O redesign MUST preservar o modelo de configuração por instância já implementado; selecionar vários ativos em uma instância MUST NOT ser introduzido nesta entrega.
- **FR-035**: Tocar no widget MUST abrir o aplicativo. O widget MUST NOT apresentar controles que não executem ações confiáveis.
- **FR-036**: Notificações MUST manter conteúdo em pt-BR, valores em BRL e identidade visual compatível com o Pingo, preservando navegação e comportamento já existentes.

### Acessibilidade e apresentação

- **FR-037**: Ações interativas MUST ter alvos de toque de pelo menos 48dp e descrições acessíveis que comuniquem o propósito da ação.
- **FR-038**: Leitores de tela MUST conseguir identificar moeda, preço, variação, direção e estado do alerta; elementos decorativos MUST NOT poluir a árvore semântica.
- **FR-039**: As telas MUST continuar utilizáveis com fonte ampliada, contraste suficiente e configuração de remoção de animações do sistema.
- **FR-040**: Atualizações MUST evitar mudanças abruptas de layout; animações MUST ser sutis, curtas e respeitar a preferência de redução de movimento.
- **FR-041**: Estados sem dados, sem resultado de busca, sem alertas, offline, erro, dado desatualizado e monitoramento limitado/interrompido MUST ter mensagens claras e não ambíguas.

## Sistema visual

Os tokens abaixo são normativos e prevalecem sobre cores, sombras ou acabamentos das pranchas conceituais. Para a marca e Ajustes, os detalhes geométricos e comportamentais da referência oficial vinculada no cabeçalho são igualmente normativos.

| Token | Claro | Escuro | Uso |
|---|---|---|---|
| `primary` | `#6750A4` | `#D0BCFF` | Ações principais, seleção e navegação ativa |
| `background` | `#F7F5FB` | `#141218` | Fundo das telas |
| `surface` | `#FFFFFF` | `#211F26` | Cards e superfícies |
| `surfaceVariant` | `#E9E4F2` | `#49454F` | Campos, divisores e áreas secundárias |
| `onSurface` | `#171522` | `#E6E1E9` | Texto principal sobre superfícies neutras |
| `positive` | `#16865B` | `#4CCB91` | Ícones, setas e preenchimentos positivos; não usar como texto em chip |
| `negative` | `#D64D59` | `#FF6B75` | Ícones, setas e preenchimentos negativos; não usar como texto em chip |
| `warning` | `#D69A31` | `#F2BE58` | Ícones, setas e preenchimentos de atenção; não usar como texto em chip |
| `neutral` | `#85818E` | `#AAA5B2` | Ícones, setas e preenchimentos neutros; não usar como texto em chip |

### Tokens para texto em chips e variações

Texto comum usa `onSurface` sobre superfícies neutras (`background`, `surface` e `surfaceVariant`). O lockup `PingoLogo` é exceção de marca e usa `primary`. Texto dentro de chips usa obrigatoriamente o par de primeiro plano/container da tabela; não substituir o foreground do chip por `onSurface`. Texto de variação usa as cores próprias somente sobre `background` ou `surface`, nunca sobre `surfaceVariant` ou fundos de chip.

| Uso | Texto claro | Contêiner claro | Texto escuro | Contêiner escuro |
|---|---|---|---|---|
| Chip positivo | `#0B5C3E` | `#DDF3E9` | `#8FE3BC` | `#12372A` |
| Chip negativo | `#8F2330` | `#FBE3E5` | `#FFB3B8` | `#4A1D22` |
| Chip atenção | `#6B4700` | `#FBEBCB` | `#F2BE58` | `#4A3710` |
| Chip neutro | `#4A4653` | `#ECE9F0` | `#CFCAD8` | `#36323D` |
| Texto de variação positiva | `#0F6B47` | `background` ou `surface` | `#4CCB91` | `background` ou `surface` |
| Texto de variação negativa | `#B3303D` | `background` ou `surface` | `#FF6B75` | `background` ou `surface` |

Todos os pares de texto comum `onSurface` com os três fundos neutros, `primary` do lockup com `background`/`surface`, texto/contêiner de chips e texto/fundo de variação MUST atingir contraste WCAG de pelo menos 4,5:1. A implementação MUST testar matematicamente todos os pares acima nos dois temas e falhar se qualquer par ficar abaixo do limite. Os tokens semânticos originais da tabela principal permanecem disponíveis para ícones, setas e preenchimentos.

- Usar Roboto nativa ou Inter, sem misturar famílias tipográficas.
- Títulos de tela: 28–32sp; títulos de seção: 20–24sp; corpo: 14–16sp; legendas: no mínimo 12sp.
- Usar raio de 16dp em cards e 12–16dp em campos e botões.
- Usar grade de espaçamento de 4dp e margens laterais de 20–24dp.
- Para BTC, ETH, SOL, XRP, ADA e DOGE MUST ser usado o PNG fornecido na raiz, conforme FR-006. `research.md` MUST registrar caminho/origem, dimensões, transparência e estado de legibilidade de cada arquivo. Não baixar nem trocar por logos de terceiros. A permissão de uso para distribuição pública MUST ser confirmada antes da publicação se a origem/licença não estiver documentada no repositório.
- O ícone de navegação Mercado MUST usar `query_stats` (ou seu equivalente vetorial compatível) e manter consistência com os demais ícones Rounded.
- O logotipo final do Pingo MUST ser entregue como vetor, incluindo variações horizontal, quadrada e monocromática. As pranchas são referência de direção, não substituem o arquivo vetorial final.
- A tela Ajustes MUST seguir a composição, textos, estados, acessibilidade e comportamento descritos em `refs/pingo-assets-e-ajustes.md`; em caso de conflito entre sua especificação e mockup/prancha, prevalece a referência oficial.

## Decisões de esclarecimento resolvidas

As seguintes decisões são explícitas e prevalecem sobre qualquer conteúdo conflitante nas imagens ou no texto descritivo da fonte:

1. **Detalhes**: somente logo, nome, ticker, preço, variação percentual 24h, horário e “Criar alerta”. Gráfico, períodos, máxima/mínima, variação absoluta e “Sobre a moeda” ficam fora desta entrega.
2. **Formulário de alerta**: tela única; condições “Acima”, “Abaixo” e “Ambas”, preservando `Direction.BOTH`; toggle “Repetir” com explicação em português.
3. **Barra superior**: mostra apenas a marca. Não inclui sino nem engrenagem; tema é configurado em Ajustes nos modos Sistema/Claro/Escuro.
4. **Mercado**: título “Moedas”; lista completa dos seis ativos, favoritos primeiro; sem seletor de período; chip de monitoramento abre Ajustes.
5. **Ajustes**: tela real de cinco cartões conforme `refs/pingo-assets-e-ajustes.md` — Aparência, Monitoramento, Notificações, Bateria e Sobre — reutilizando estados existentes.
6. **Widget**: uma moeda por instância em 2×1 e 2×2; o modelo de configuração existente não muda.
7. **AlertCard**: distância ao alvo em cor neutra. Estado segue a precedência em FR-015. Alerta repetível desarmado após disparo mostra “Aguardando rearme” e informa o preço/condição que o reativará usando a mesma constante de histerese do domínio (`CrossingDetector.HYSTERESIS`): após disparo para cima, “Reativa quando o preço voltar abaixo de R$ X”; após disparo para baixo, “Reativa quando o preço voltar acima de R$ X”. X é o alvo multiplicado pelo limite inferior/superior da banda, respectivamente; a comparação subjacente mantém a inclusão/exatidão definida pelo domínio. Para `BOTH`, usar `firedUp` do último disparo. Alerta de disparo único desarmado mostra “Disparado”.
8. **Identidade**: tokens e pranchas seguem a precedência deste documento; a referência oficial define exatamente os VectorDrawables e a composição de Ajustes; Mercado usa `query_stats`; o logo é vetorial.
9. **Correção visual de Mercado após validação**: a captura enviada em 2026-10-30 é evidência do estado atual, não um mockup a copiar. Corrigir a área superior excessivamente alta, remover do Mercado o toggle e a explicação longa de monitoramento, manter apenas o chip compacto que leva a Ajustes, apresentar o último horário apenas uma vez no status global e usar os seis PNGs locais em vez das iniciais.

## Critérios de aceite

- **AC-001**: Mercado tem título “Moedas”, lista os seis ativos sem busca ativa, ordena favoritos primeiro e apresenta identificação visual, preço BRL e variação com duas casas decimais quando disponíveis.
- **AC-002**: Horário/estado de conexão aparecem globalmente sem repetição; a busca filtra os seis ativos; chip de monitoramento abre Ajustes; tocar em moeda abre Detalhes.
- **AC-003**: Favorito comunica claramente adicionar/remover e não se confunde com seleção de moeda.
- **AC-004**: Cards mostram os dados e estados exigidos; ações de pausar/retomar e exclusão com confirmação afetam somente o alerta escolhido.
- **AC-005**: Nenhum texto de interface expõe `UP`, `DOWN` ou jargão interno sem explicação em linguagem simples.
- **AC-006**: Criar/editar usa uma tela única, aceita entrada decimal pt-BR, apresenta Acima/Abaixo/Ambas e toggle Repetir, preservando regras existentes de disparo, `BOTH`, rearme, histerese e repetição.
- **AC-007**: Navegação inferior contém Mercado, Alertas e Ajustes; a barra superior mostra somente a marca e mantém estado relevante ao alternar destinos.
- **AC-008**: Tema Sistema, Claro e Escuro funciona em todas as telas; widgets e notificações mantêm legibilidade.
- **AC-009**: TalkBack, fonte ampliada, alvos mínimos e contraste são verificados nos principais fluxos.
- **AC-010**: Cada widget mostra somente sua moeda configurada, permanece legível em 2×1 e 2×2, identifica cotação antiga/indisponível e abre o app; seu modelo de configuração continua por instância.
- **AC-011**: Dados que não existem ou não são confiáveis — como histórico, máxima ou mínima — não são inventados nem apresentados como disponíveis.
- **AC-012**: Build debug e release com R8 terminam com sucesso e a suíte existente continua passando sem regressão em cotação, alertas, notificações, monitoramento ou widget.
- **AC-013**: Ajustes contém controles reais de tema e monitoramento, permissões, bateria, versão, política de privacidade e créditos das fontes.
- **AC-014**: AlertCard mostra distância em cor neutra e, para alerta repetível desarmado, exibe “Aguardando rearme” com uma condição/preço explícito de reativação coerente com a última direção disparada.
- **AC-015**: O ícone Mercado usa `query_stats` e o logotipo final está disponível em formato vetorial nas variações horizontal, quadrada e monocromática.
- **AC-016**: Marca do launcher e barra superior corresponde à geometria vetorial e aos recursos/nomes da referência oficial; o manifest liga ícones launcher e redondo, e inclui a variante monocromática.
- **AC-017**: Ajustes contém os cinco cartões da referência com seus estados, textos, ações e preferências persistidas; não há estado inventado durante carregamento nem fonte de verdade paralela.
- **AC-018**: Os seis recursos locais de moeda são associados ao símbolo correto em Mercado e Detalhes, preservam cores/proporção/transparência e permanecem legíveis em temas claro e escuro; a interface não usa iniciais como substituição normal. `research.md` registra origem, caminho, formato e estado de legibilidade de cada arquivo, sem alegar licença externa não verificada.
- **AC-019**: Teste unitário calcula o contraste de todos os pares `onSurface`/fundo neutro, `primary` do lockup/fundo neutro, texto/contêiner de chip e texto/fundo de variação nos temas claro e escuro; qualquer razão abaixo de 4,5:1 falha o teste.
- **AC-020**: `@Preview` de tema claro e escuro existe para cada tela e estado visual relevante: Mercado, Alertas nos estados Ativo/Pausado/Aguardando rearme/Disparado, Criar alerta, Ajustes e variantes de chips.
- **AC-021**: Mercado não exibe a descrição/toggle de monitoramento contínuo nem horário repetido por moeda; conexão e atualização global são compactas, o chip abre Ajustes e a lista não é empurrada para baixo por espaço vazio amplo.
- **AC-022**: Mercado e Detalhes exibem os seis PNGs fornecidos, incluindo XRP legível sobre fundos claro e escuro; o layout permanece compacto sem sacrificar legibilidade, nomes, ticker, preço, variação ou ação de favorito.

## Restrições de implementação para a etapa de plano

As decisões abaixo são diretrizes técnicas oriundas do documento de redesign e deverão ser detalhadas no `plan.md`, sem alterar a semântica de domínio:

- Concentrar as mudanças em apresentação e navegação; reutilizar ViewModels, StateFlows, casos de uso, repositórios e entidades existentes.
- Preservar as capturas atuais em `refs/before/` e criar `@Preview` claro/escuro para cada tela e estado relevante, incluindo Mercado, Alertas nos quatro estados, Criar alerta, Ajustes e chips. Capturas do aparelho são evidência opcional descrita no quickstart.
- Preservar Material 3, MVVM, Hilt, Room, Binance WebSocket, CoinGecko, WorkManager, foreground service, notificações e Glance.
- Composables observam estado existente; chamadas de rede não devem ser movidas para a UI.
- Não criar fonte de verdade paralela para preços, alertas ou preferências.
- Não alterar thresholds, cruzamento, histerese, frequência de agendamento ou permissões como parte do redesign.
- Preferir dependências já adotadas no projeto; seguir os requisitos de composição/hierarquia desta especificação e as referências oficiais. Detalhes de sistema podem variar, mas não podem reintroduzir as divergências registradas na correção visual de Mercado.

## Assunções e questões para planejamento

- O conjunto de ativos permanece BTC, ETH, SOL, XRP, ADA e DOGE.
- Valores, variações e estados só são exibidos como atuais quando sustentados pelos dados disponíveis e pelas regras de frescor existentes.
- Cada instância do widget corresponde a exatamente uma moeda, nos tamanhos 2×1 e 2×2; a seleção múltipla vista em prancha está descartada nesta entrega.
- `refs/pingo-assets-e-ajustes.md` é o contrato oficial para arquivos de marca e tela Ajustes; sua existência no caminho indicado deve ser preservada e vinculada, não substituída por transcrição das pranchas.
- As pranchas conceituais não substituem validação dos previews e capturas reais das telas implementadas.
- Créditos, origem e licenças de logotipos e fontes devem ser verificados antes da publicação.
