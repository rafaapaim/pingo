# Tarefas: Cabeçalho abaixo da barra de status

**Fonte de verdade**: [spec.md](./spec.md), [plan.md](./plan.md), [research.md](./research.md) e [quickstart.md](./quickstart.md).

## Regras de execução

- Implementar uma correção por vez, baseada em comportamento confirmado; não presumir edge-to-edge nem altura fixa da barra de status.
- Aplicar o inset superior em um único ponto do cabeçalho compartilhado. Não empilhar insets no Activity, Scaffold e telas.
- Preservar marca, navegação inferior, conteúdo e lógica de domínio.
- Gate final: build debug, testes unitários, testes instrumentados aplicáveis e validação visual em aparelho nos temas claro e escuro.

## Fase única — Corrigir o inset do cabeçalho

- [ ] T001 Reproduzir a sobreposição no aparelho e registrar modelo, API Android, orientação, escala de exibição e tema; guardar captura anterior para comparação e confirmar bounds relativos da barra de status e do lockup. **Pendente:** não há aparelho/emulador acessível nem `adb` instalado neste ambiente.
- [ ] T002 Inspecionar em runtime os insets de status bar entregues ao `Scaffold` e ao slot `topBar`; determinar se a janela desenha sob a barra ou se o slot não está aplicando o inset. Não escolher a correção antes dessa observação. **Pendente para confirmação física:** a inspeção estática encontrou target SDK 36 e `PingoLogo` no `topBar` sem inset; o teste instrumentado abaixo valida a aplicação de um inset variável.
- [x] T003 Adicionar teste instrumentado/layout para o cabeçalho compartilhado que compara o topo do lockup com o inset real reportado pelo sistema e com um inset superior variável; cobrir a navegação entre Mercado, Alertas e Ajustes sem depender de dp fixo.
- [x] T004 Aplicar o inset superior dinâmico exatamente uma vez no slot compartilhado do cabeçalho, usando `WindowInsets.statusBars`, e manter o espaçamento visual existente. Não alterar `PingoLogo`.
- [ ] T005 Confirmar visualmente que conteúdo do `NavHost`, título, busca, navegação inferior e destinos secundários não receberam insets/padding duplicados nem ficaram cortados; inspeção física pendente.
- [ ] T006 Validar visualmente Mercado, Alertas e Ajustes em tema claro e escuro; conferir que hora e ícones do sistema permanecem legíveis e não são cobertos nem interceptados.
- [ ] T007 Validar um inset superior diferente/recorte de tela quando disponível, rotação e fonte/tamanho de exibição ampliados; conferir clipping e posição do primeiro conteúdo.
- [ ] T008 Executar `.\gradlew.bat assembleDebug testDebugUnitTest`, compilar e executar testes instrumentados em dispositivo/emulador conectado; corrigir falhas e registrar limitações de execução em aparelho. **Resultado parcial:** build debug e testes unitários passaram; testes instrumentados compilaram, mas não foram executados porque `adb`/dispositivo não estão disponíveis.
- [ ] T009 Concluir o roteiro de [quickstart.md](./quickstart.md), guardar capturas opcionais antes/depois com contexto do dispositivo e verificar os critérios AC-001–AC-005.
- [ ] T010 Revisar as alterações da feature após as validações: limitar a mudança a layout/insets e não alterar identidade da marca, conteúdo, navegação ou regras de domínio.

## Dependências

- T002 depende de T001.
- T003 depende de T002 para testar o contrato de inset observado.
- T004 depende de T002 e T003.
- T005 depende de T004.
- T006 e T007 dependem de T005.
- T008 depende de T004 e pode executar em paralelo com validações visuais quando o dispositivo estiver disponível.
- T009 depende de T006–T008.
- T010 depende de T004–T009.
