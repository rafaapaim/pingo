# Feature Specification: Alertas de Preço de Criptomoedas

**Feature Branch**: `001-alerta-cripto`

**Created**: 2026-10-29

**Status**: Draft

**Input**: User description: Aplicativo Android para acompanhar preços de criptomoedas em reais, configurar alertas por cruzamento de preço e consultar cotações em um widget.

## User Scenarios & Testing

### User Story 1 - Consultar preços (Priority: P1)

Como pessoa interessada em criptomoedas, quero consultar preços em reais e a variação das últimas 24 horas para acompanhar o mercado.

**Why this priority**: A consulta de preços é o uso principal do aplicativo e dá contexto para escolher limites de alerta.

**Independent Test**: Abrir o aplicativo com conexão disponível e confirmar que a lista apresenta as moedas suportadas, seus preços em BRL e a variação de 24 horas.

**Acceptance Scenarios**:

1. **Given** que há conexão e cotações disponíveis, **When** a pessoa abre a tela inicial, **Then** vê BTC, ETH, SOL, XRP, ADA e DOGE com preço em reais e variação de 24 horas.
2. **Given** que a lista de moedas está disponível, **When** a pessoa pesquisa pelo nome ou símbolo de uma moeda suportada, **Then** consegue localizá-la e selecioná-la.
3. **Given** que uma cotação está sendo exibida, **When** a pessoa consulta seus detalhes, **Then** consegue identificar quando o preço foi atualizado.

### User Story 2 - Criar e acompanhar alertas (Priority: P1)

Como pessoa que acompanha uma criptomoeda, quero definir um preço-alvo e a direção do movimento para ser avisada quando o preço cruzar esse limite.

**Why this priority**: Alertas de cruzamento são a proposta central do produto e permitem acompanhar o mercado sem observar a tela continuamente.

**Independent Test**: Criar um alerta para uma moeda e verificar, com uma sequência controlada de preços, que a notificação só ocorre quando há cruzamento na direção escolhida.

**Acceptance Scenarios**:

1. **Given** que uma moeda é suportada, **When** a pessoa cria um alerta com valor-alvo em reais, **Then** pode escolher disparo ao subir, ao cair ou em ambas as direções.
2. **Given** que um alerta está ativo, **When** o preço permanece do mesmo lado do limite ou apenas o toca sem atravessá-lo, **Then** o alerta não dispara.
3. **Given** que um alerta de alta está ativo, **When** o preço passa de igual ou abaixo do limite para acima dele, **Then** a pessoa recebe uma notificação que identifica a moeda, o limite e o preço observado.
4. **Given** que um alerta de queda está ativo, **When** o preço passa de igual ou acima do limite para abaixo dele, **Then** a pessoa recebe uma notificação que identifica a moeda, o limite e o preço observado.
5. **Given** que um alerta já disparou, **When** o preço continua do mesmo lado do limite, **Then** o alerta não repete o disparo; um alerta configurado para repetir só rearma quando o preço retorna ao lado oposto do alvo com margem de 0,1% do valor-alvo e só dispara novamente após uma nova travessia válida.
6. **Given** que a pessoa escolheu disparo único, **When** o alerta dispara, **Then** ele não dispara novamente.
7. **Given** que existem alertas configurados, **When** a pessoa os administra, **Then** pode ativar, desativar ou excluir cada alerta sem afetar os demais, inclusive alertas da mesma moeda.
8. **Given** que uma notificação de alerta foi recebida, **When** a pessoa toca nela, **Then** o aplicativo abre o detalhe da moeda correspondente.
9. **Given** que notificações estão bloqueadas ou sem permissão, **When** um alerta precisa avisar a pessoa, **Then** o aplicativo informa claramente que a entrega depende de habilitar notificações.
10. **Given** um alerta repetível de alta com alvo de R$ 100 que disparou quando o preço chegou a R$ 101, **When** o preço retorna a R$ 99,95, **Then** permanece desarmado; **When** o preço chega a R$ 99,89, **Then** rearma, pois cruzou a banda inferior de R$ 99,90.

### User Story 3 - Acompanhar cotações em um widget (Priority: P2)

Como pessoa que quer consultar o mercado rapidamente, quero ver uma moeda escolhida na tela inicial do dispositivo sem precisar navegar pelo aplicativo.

**Why this priority**: O widget facilita consultas recorrentes, mas depende da experiência principal de cotações.

**Independent Test**: Adicionar o widget, escolher uma moeda e conferir preço, variação e horário da última atualização; tocar no widget deve abrir o aplicativo.

**Acceptance Scenarios**:

1. **Given** que o widget foi adicionado, **When** a pessoa o configura, **Then** pode escolher a moeda exibida.
2. **Given** que o widget tem uma moeda selecionada, **When** há cotação disponível, **Then** exibe moeda, preço em reais, variação de 24 horas e horário da última atualização.
3. **Given** que a cotação do widget está desatualizada ou indisponível, **When** a pessoa consulta o widget, **Then** consegue perceber que o dado não é atual.
4. **Given** que o widget está visível, **When** a pessoa toca nele, **Then** o aplicativo é aberto.
5. **Given** que o dispositivo permite diferentes tamanhos de widget, **When** a pessoa escolhe um tamanho, **Then** o conteúdo permanece legível nos formatos compacto e ampliado.

### User Story 4 - Entender conectividade e monitoramento (Priority: P1)

Como pessoa que depende dos alertas, quero saber quando os dados estão indisponíveis ou o monitoramento está limitado para não confiar em uma cotação antiga como se fosse atual.

**Why this priority**: A transparência sobre conectividade e limitações do sistema é essencial para interpretar preços e a confiabilidade dos alertas.

**Independent Test**: Interromper a conexão e ativar o modo de economia de bateria; confirmar que o aplicativo sinaliza os estados e não apresenta dados antigos como atuais.

**Acceptance Scenarios**:

1. **Given** que a conexão com a fonte de preços foi perdida, **When** a pessoa consulta a tela inicial, **Then** vê um estado offline ou de erro e o horário da última cotação disponível.
2. **Given** que há uma cotação anterior salva, **When** ela é apresentada sem atualização recente, **Then** é identificada como desatualizada, e não como preço atual.
3. **Given** que a pessoa ativa o monitoramento contínuo, **When** a opção é apresentada, **Then** é informada de que isso pode consumir mais bateria e não garante execução ininterrupta.
4. **Given** que o modo de economia de bateria ou uma restrição do sistema limita o monitoramento, **When** a atividade for pausada ou reduzida, **Then** o estado limitado é comunicado de forma visível.
5. **Given** que a pessoa não usa monitoramento contínuo, **When** deixa o aplicativo em segundo plano, **Then** o produto explica que verificações menos frequentes podem atrasar alertas e atualizações.

## Edge Cases

- O preço recebido é igual ao valor-alvo: tocar o limite, sem atravessá-lo, não conta como cruzamento.
- A primeira cotação disponível não tem um preço anterior conhecido: não deve gerar um alerta de cruzamento retroativo.
- Uma cotação é inválida, não positiva, está ausente ou chega fora de ordem: não deve ser apresentada como atual nem causar disparo indevido.
- A conexão cai durante uma verificação: a pessoa deve ver o estado de erro/offline e a idade da última cotação.
- A pessoa desativa ou exclui um alerta enquanto outros alertas da mesma moeda continuam ativos: os demais não devem ser alterados.
- As notificações não foram autorizadas: o estado e a ação necessária devem ser explicados sem afirmar que o alerta foi entregue.
- O modo de economia de bateria, uma reinicialização ou uma restrição do sistema interrompe verificações: o aplicativo não deve prometer monitoramento contínuo.
- O widget não pode atualizar no instante esperado pelo sistema: deve preservar a indicação de horário e de desatualização da cotação.

## Requirements

### Functional Requirements

- **FR-001**: O aplicativo MUST apresentar preços em BRL e variação de 24 horas para BTC, ETH, SOL, XRP, ADA e DOGE, quando houver cotação disponível.
- **FR-002**: O aplicativo MUST permitir localizar e selecionar uma moeda suportada.
- **FR-003**: O aplicativo MUST permitir criar vários alertas por moeda, cada um com valor-alvo positivo em BRL e direção de alta, queda ou ambas.
- **FR-004**: O aplicativo MUST permitir ativar, desativar e excluir alertas individualmente.
- **FR-005**: Um alerta MUST disparar somente quando uma cotação válida atravessar o limite na direção configurada; permanecer do mesmo lado ou apenas tocar o limite não é cruzamento.
- **FR-006**: O aplicativo MUST permitir configurar um alerta como disparo único ou repetido. Após disparar, um alerta repetível MUST permanecer desarmado até o preço retornar ao lado oposto do alvo com margem de histerese de 0,1% do valor-alvo; só poderá disparar novamente após uma nova travessia válida do alvo.
- **FR-007**: A notificação MUST identificar a moeda, o valor-alvo e o preço observado, e seu toque MUST abrir o detalhe da moeda.
- **FR-008**: O aplicativo MUST comunicar quando notificações estiverem desabilitadas ou sem permissão e MUST NOT indicar que uma notificação foi entregue nesses casos.
- **FR-009**: O aplicativo MUST exibir estado offline, erro ou dado desatualizado de forma visível, junto do horário da última cotação disponível quando houver uma.
- **FR-010**: O aplicativo MUST oferecer uma opção de monitoramento contínuo e explicar seu impacto potencial na bateria e que a execução em segundo plano pode ser limitada pelo sistema.
- **FR-011**: Quando economia de bateria ou outra restrição limitar o monitoramento, o aplicativo MUST indicar que ele está pausado ou limitado.
- **FR-012**: O aplicativo MUST oferecer um widget configurável por moeda, em formatos compacto e ampliado, com preço em BRL, variação de 24 horas e horário da última atualização.
- **FR-013**: O toque no widget MUST abrir o aplicativo.
- **FR-014**: O aplicativo MUST apresentar interface, mensagens, notificações e valores no padrão pt-BR, incluindo moeda no formato `R$ 1.234,56`.
- **FR-015**: O aplicativo MUST oferecer tema claro e escuro.
- **FR-016**: A versão 1 MUST funcionar sem login; alertas e preferências devem permanecer no dispositivo, sem exigir backend próprio ou sincronização em nuvem.

### Key Entities

- **Moeda**: ativo disponível para consulta, identificado por símbolo e acompanhado de preço em BRL, variação de 24 horas e horário de atualização.
- **Alerta**: regra associada a uma moeda, com valor-alvo, direção, opção de repetição e estado ativado/desativado.
- **Cotação**: preço observado de uma moeda, com horário de observação e informação suficiente para distinguir dado atual de dado desatualizado.
- **Preferência de monitoramento**: escolha da pessoa entre monitoramento contínuo ou verificações menos frequentes.
- **Widget configurado**: apresentação vinculada à moeda escolhida e à última cotação conhecida.

## Success Criteria

### Measurable Outcomes

- **SC-001**: Em todos os cenários de teste de cruzamento para alta, queda e ambas as direções, somente a transição válida através do limite dispara o alerta; preços iguais ou mantidos do mesmo lado não disparam.
- **SC-002**: Em uma sequência de preços de teste, alertas repetidos só disparam novamente após retorno à banda oposta com margem de histerese de 0,1% do alvo e novo cruzamento; alertas de disparo único não disparam uma segunda vez.
- **SC-003**: Todas as seis moedas previstas podem ser localizadas na lista quando há cotações disponíveis, e seus valores são apresentados em BRL com variação e horário.
- **SC-004**: Em todos os testes de desconexão e cotação antiga, o estado offline/desatualizado e o horário da última atualização ficam visíveis, sem rotular o dado antigo como atual.
- **SC-005**: Uma pessoa consegue criar um alerta, desativá-lo e excluí-lo sem criar conta e sem modificar outros alertas.
- **SC-006**: Um widget configurado apresenta os quatro dados requeridos — moeda, preço, variação e horário — em ambos os formatos, e abre o aplicativo ao toque.
- **SC-007**: Todas as telas, notificações e widgets verificados apresentam textos em pt-BR e valores monetários no formato brasileiro.
- **SC-008**: Para um alerta repetível de alta com alvo de R$ 100 que dispara em R$ 101, o preço de R$ 99,95 mantém o alerta desarmado e o preço de R$ 99,89 rearma-o; a próxima notificação exige nova travessia válida do alvo.

## Assumptions

- Os símbolos BTC, ETH, SOL, XRP, ADA e DOGE são o conjunto inicial obrigatório de moedas; adições futuras não mudam as regras gerais da funcionalidade.
- A pessoa usuária dispõe de conexão com a internet para obter cotações novas; o produto comunica indisponibilidade e pode mostrar uma cotação anterior claramente identificada.
- A comparação de cruzamento usa o limite como lado de referência: cruzar para cima significa sair de igual/abaixo e chegar acima; cruzar para baixo significa sair de igual/acima e chegar abaixo.
- Para alertas em ambas as direções, cada disparo é rearmado somente quando o preço retorna à banda oposta com margem de histerese de 0,1% do alvo, calculada em relação à direção do último cruzamento.
- O instante de entrega de alertas em segundo plano e a frequência de atualização do widget dependem das condições e limites do Android; a interface não promete garantias que o sistema não oferece.
- O escopo da v1 não inclui alertas por percentual de variação, gráficos, notificações push por servidor, login ou sincronização em nuvem.
