# Pesquisa técnica: Cabeçalho abaixo da barra de status

## Escopo

Inspeção do layout de Activity/Compose e da janela Android para fundamentar a correção de sobreposição descrita em [spec.md](./spec.md). A captura fornecida é evidência do defeito, não referência visual.

## Evidências no código

- `MainActivity` chama `setContent` e compõe o tema/superfície/navegação; não foi encontrada chamada explícita a `enableEdgeToEdge`, `WindowCompat.setDecorFitsSystemWindows` ou outro ajuste de insets no código principal examinado.
- `AndroidManifest.xml` declara `Theme.Material.Light.NoActionBar`; não foi encontrada declaração de fullscreen ou configuração específica de status bar no manifest/recursos consultados.
- `PingoAppNavigation` usa um `Scaffold` que contém a barra superior, `NavHost` e navegação inferior.
- O slot superior renderiza `PingoLogo` com padding fixo de 12dp no topo. Não há aplicação explícita de `WindowInsets.statusBars`/`statusBarsPadding()` nesse slot.
- O `PingoLogo` é um componente compartilhado e sua geometria/estilo são definidos separadamente; não há necessidade aparente de redesenhar a marca para corrigir o posicionamento.
- O projeto usa target SDK 36; validar o comportamento no Android usado na reprodução, pois o tratamento de edge-to-edge depende da combinação de target, versão Android e comportamento do scaffold.

## Decisão

Preferir aplicar o inset superior dinâmico no cabeçalho compartilhado e eliminar apenas o padding fixo que se tornar redundante. Não estimar altura da barra de status com valores fixos e não aplicar inset simultaneamente no Activity, Scaffold e cabeçalho.

## Incertezas para confirmar

- A captura não informa versão/API Android, recorte, escala de exibição ou orientação. Registrar esses dados ao reproduzir.
- A versão exata do Material 3/Compose pode já fornecer algum tratamento automático em slots/contents; conferir bounds no dispositivo e evitar duplicar insets que a biblioteca já consuma.
- Nenhum teste instrumentado de insets no cabeçalho compartilhado foi identificado nesta inspeção.

## Dependências e impacto

Nenhuma dependência adicional é necessária. A correção deve permanecer em `PingoAppNavigation`/layout do cabeçalho, com mudança em `MainActivity` apenas se a reprodução apontar a configuração da janela como causa.
