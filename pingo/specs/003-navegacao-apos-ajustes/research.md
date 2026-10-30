# Pesquisa técnica: Navegação após alterar Ajustes

## Escopo da pesquisa

Inspeção local da implementação atual para planejar a correção de navegação descrita em [spec.md](./spec.md). Nenhuma causa foi considerada confirmada sem reprodução no aparelho.

## Implementação observada

- `PingoAppNavigation` mantém um `NavController` criado por `rememberNavController()`, um `NavHost` e a `NavigationBar` no mesmo `Scaffold`.
- Os destinos principais são as rotas constantes `market`, `alerts` e `settings`. A barra inferior é exibida se `currentBackStackEntry.destination.route` corresponder exatamente a um desses nomes.
- Cada item chama `navigate(route)` com `popUpTo(market) { saveState = true }`, `launchSingleTop = true` e `restoreState = true`.
- `SettingsScreen` é conteúdo rolável em `Column`; ela não controla nem instancia a barra inferior.
- Alterar tema chama `onThemeModeChanged`; em `MainActivity`, esse callback grava `ThemePreferenceStore.setThemeMode` dentro de uma coroutine. A mudança do valor observado recompõe o tema e a navegação.
- A ação de abrir configurações de notificação sai temporariamente para uma Activity externa, cujo retorno é um cenário distinto da mudança de tema.
- Há um teste instrumentado de navegação para um host reduzido Mercado/Detalhes, mas não há teste do `PingoAppNavigation` com os três itens principais e Ajustes. Os testes de Ajustes isolam `SettingsScreen` e validam somente o callback de seleção do tema.

## Lacuna de cobertura

A cobertura atual não verifica o requisito central em um único fluxo: abrir Ajustes pelo destino principal, mudar uma preferência, confirmar que a barra inferior continua disponível e tocar Mercado. Também não verifica seleção visual, comportamento repetido entre abas ou retorno de uma Activity de configurações do sistema.

## Decisões técnicas

- Reutilizar a dependência e o `NavController` existentes; nenhuma dependência nova é justificada.
- Não alterar DataStore ou o estado das opções: o problema relatado é de navegação e deve ser resolvido na camada de apresentação/ciclo de Activity apenas se a reprodução apontar essa causa.
- Preferir teste instrumentado da casca real da navegação. Se a injeção dos ViewModels de produção impedir esse teste isolado, extrair apenas a lógica/componente mínimo necessário ou usar um host de teste que compartilhe a mesma barra e política de navegação. Não duplicar comportamento de produção em uma implementação de teste divergente.
- A causa permanece aberta até reproduzir no aparelho e identificar se a barra some, fica inacessível, não recebe clique ou navega para rota errada.
