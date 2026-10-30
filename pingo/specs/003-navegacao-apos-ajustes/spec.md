# Especificação: Navegação após alterar Ajustes

**Feature Branch**: `003-navegacao-apos-ajustes`  
**Criado**: 2026-10-30  
**Status**: Draft  
**Contexto**: Bug observado em aparelho Android durante validação do Pingo.

## Objetivo

Corrigir a navegação principal quando a pessoa altera uma configuração em Ajustes. Atualmente, após a alteração, não consegue voltar à aba Mercado pela navegação do app e depende do botão Voltar do Android.

## Escopo

### Incluído

- Manter acessíveis os destinos principais Mercado, Alertas e Ajustes enquanto a pessoa usa o app.
- Permitir sair de Ajustes diretamente para Mercado pela navegação inferior após alterar uma configuração.
- Preservar a configuração alterada e indicar corretamente o destino selecionado.

### Fora de escopo

- Redesenhar ou reorganizar a tela Ajustes.
- Alterar o comportamento, armazenamento ou conteúdo das configurações.
- Alterar navegação de deep links, detalhes de moedas, formulário de alertas, serviços ou notificações, exceto quando estritamente necessário para corrigir o defeito descrito.

## História do usuário

### US1 — Voltar ao Mercado após alterar Ajustes (P1)

Como pessoa usuária, quero voltar à aba Mercado tocando no destino correspondente do app depois de mudar uma configuração, sem depender do botão Voltar do Android.

**Teste independente**: abrir Ajustes, modificar uma configuração disponível e tocar em Mercado na navegação principal; Mercado deve abrir e a alteração deve continuar aplicada.

## Requisitos funcionais

- **FR-001**: Os destinos principais Mercado, Alertas e Ajustes devem permanecer acessíveis pela navegação principal ao longo do uso normal do app.
- **FR-002**: Depois que uma configuração for alterada em Ajustes, tocar em Mercado na navegação principal deve navegar diretamente para Mercado sem exigir o botão Voltar do Android, fechar/reabrir o app ou repetir a alteração.
- **FR-003**: A navegação entre Ajustes e Mercado não deve desfazer nem atrasar a aplicação/persistência da configuração modificada.
- **FR-004**: A navegação deve indicar visualmente o destino atual. Ao retornar a Ajustes, sua opção deve aparecer selecionada.

## Critérios de aceite

- **AC-001**: Em aparelho Android, após alterar o tema em Ajustes, tocar em Mercado abre Mercado pela navegação principal; o botão Voltar do sistema não é necessário e o tema escolhido permanece aplicado.
- **AC-002**: Após alterar outra configuração disponível em Ajustes, tocar em Mercado abre Mercado e a configuração permanece aplicada ao retornar a Ajustes.
- **AC-003**: Mercado, Alertas e Ajustes continuam selecionáveis pela navegação principal, e o destino ativo é indicado corretamente antes e depois da troca.
- **AC-004**: A correção não altera o conteúdo nem a semântica das configurações, nem os fluxos de preços, alertas, notificações ou monitoramento.

## Casos de borda

- A pessoa toca em Mercado imediatamente após alterar uma opção: a navegação não deve interromper a gravação ou descartar a mudança.
- A pessoa alterna entre Mercado e Ajustes várias vezes: o destino atual e as preferências permanecem consistentes.
- A configuração selecionada já tinha o valor desejado: a navegação continua funcionando mesmo sem uma nova gravação.
