# Tarefas: Navegação após alterar Ajustes

**Fonte de verdade**: [spec.md](./spec.md), [plan.md](./plan.md), [research.md](./research.md) e [quickstart.md](./quickstart.md).

## Regras de execução

- Concluir as tarefas na ordem indicada; não implementar uma causa presumida antes de reproduzir o defeito.
- Manter o escopo em navegação e apresentação. Não alterar configuração, DataStore, Room, domínio, serviços ou permissões, exceto quando a reprodução comprovar que uma alteração localizada é necessária.
- Não adicionar dependências: a navegação Compose já está instalada.
- O gate final exige build debug, testes unitários verdes, teste instrumentado focado e validação em aparelho sem depender do botão Voltar do sistema para sair de Ajustes.

## Fase única — Reproduzir e corrigir a navegação após Ajustes

- [x] T001 Registrar a reprodução relatada em aparelho: após alterar uma configuração em Ajustes, não é possível retornar à aba Mercado pela navegação do app e é necessário usar o botão Voltar do Android. O tipo exato da configuração e o estado visual da barra ainda precisam ser confirmados.
- [ ] T002 Reproduzir separadamente uma alteração de outra configuração e o retorno de uma Activity de configurações do Android, quando aplicável; identificar se o defeito é barra ausente/coberta, toque sem efeito, rota selecionada incorreta ou restauração de estado.
- [ ] T002 Reproduzir separadamente uma alteração de outra configuração e o retorno de uma Activity de configurações do Android, quando aplicável; identificar se o defeito é barra ausente/coberta, toque sem efeito, rota selecionada incorreta ou restauração de estado.
- [x] T003 Criar teste instrumentado do componente compartilhado de navegação principal, cobrindo Mercado → Ajustes → Mercado após alterar o tema, o destino selecionado e a permanência da alteração ao retornar a Ajustes.
- [x] T004 Adicionar cobertura de alternância repetida Mercado/Alertas/Ajustes e seleção do destino atual; teste compilado, aguardando execução instrumentada.
- [x] T005 Aplicar uma correção candidata localizada em `PingoAppNavigation`: centralizar as trocas de destino principal e usar o start destination do grafo para `popUpTo`, `saveState`, `restoreState` e `launchSingleTop`. A causa física final ainda deve ser confirmada executando o teste e reproduzindo no aparelho.
- [ ] T006 Executar os testes instrumentados focados e a suíte unitária; verificar os critérios AC-001–AC-004 e confirmar que o teste reproduz a regressão antes da correção sempre que viável.
- [x] T007 Executar `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest` e compilar os testes instrumentados com `:app:compileDebugAndroidTestKotlin`; tudo passou. Execução instrumentada não concluída: nenhum dispositivo conectado e o AVD `Small_Phone` falhou ao iniciar por falta de espaço em disco. Reexecutar conectado após liberar espaço/disponibilizar aparelho.
- [ ] T008 Validar em aparelho, em tema claro e escuro, o procedimento de [quickstart.md](./quickstart.md): após alterar tema e outra configuração disponível, tocar em Mercado pela navegação do app, confirmar que a alteração continua aplicada e que o destino atual está indicado.
- [x] T009 Revisar o escopo do diff: alterações de produção limitadas à navegação principal compartilhada; nenhum ajuste em configuração, armazenamento, preço, alerta, notificação, monitoramento ou widget. Validação em aparelho permanece pendente.

## Dependências

- T002 depende de T001, pois a observação parte da reprodução inicial e aprofunda os caminhos de configuração.
- T003 depende de T001 e T002 para codificar uma reprodução fiel e não um cenário presumido.
- T004 depende de T002; executar somente os caminhos relevantes ao diagnóstico.
- T005 depende de T003 e T004: primeiro obter evidência reproduzível e localização provável da falha. A causa só se considera confirmada após teste/runtime.
- T006 depende de T005.
- T007 depende de T006.
- T008 depende de T007.
- T009 depende de T005–T008.
