# Plano de implementação: Cabeçalho abaixo da barra de status

**Feature**: `004-cabecalho-abaixo-da-barra-de-status`
**Escopo**: correção de insets e posicionamento do cabeçalho superior.
**Especificação**: [spec.md](./spec.md)
**Pesquisa técnica**: [research.md](./research.md)
**Validação manual**: [quickstart.md](./quickstart.md)

## 1. Objetivo e limites

Corrigir a sobreposição do lockup Pingo com a barra de status, respeitando o inset superior do sistema em vez de depender de uma distância fixa em dp. Preservar integralmente o lockup, os destinos, os conteúdos e a navegação.

Não alterar cores/configurações da barra de status nem ativar/desativar edge-to-edge global sem evidência de que seja necessário. Não adicionar dependências, persistência ou estado de domínio.

## 2. Estado atual verificado e diagnóstico

- `MainActivity` define o conteúdo Compose sem chamada explícita a `enableEdgeToEdge`, `WindowCompat.setDecorFitsSystemWindows` ou configuração de insets.
- O manifest usa `Theme.Material.Light.NoActionBar`; não há configuração explícita de fullscreen nem de cor da barra de status encontrada nos recursos examinados.
- `PingoAppNavigation` tem um único `Scaffold` para a navegação e renderiza `PingoLogo` diretamente no slot `topBar` com padding fixo de 12dp no topo. Não há `statusBarsPadding`, `WindowInsets` ou outro tratamento explícito para a barra de status nesse cabeçalho.
- A captura fornecida mostra a hora do sistema e o lockup na mesma área vertical, compatível com conteúdo sendo desenhado sob a barra de status ou com inset superior não aplicado ao slot superior.
- A captura descreve o defeito observado; não é layout de destino. Confirmar o comportamento em aparelho/API usados pelo usuário antes da correção.

## 3. Decisões de implementação

1. Manter a gestão atual da janela, salvo se a reprodução demonstrar que o app está intencionalmente desenhando edge-to-edge sem aplicar os insets.
2. Aplicar o inset superior do sistema no cabeçalho compartilhado do `Scaffold` (`PingoLogo`) usando API Compose já disponível, ou configurar `WindowInsets` no scaffold se isso se alinhar melhor ao comportamento observado. Escolher um único ponto de aplicação.
3. Remover ou ajustar o padding fixo superior atual somente após incluir o inset, para evitar soma duplicada. Preservar os paddings horizontais e inferior existentes.
4. Manter a política de insets do conteúdo principal e da navegação inferior intacta. Não adicionar `statusBarsPadding` a cada tela; o cabeçalho comum é o ponto de correção para evitar divergência entre Mercado, Alertas e Ajustes.
5. Se o `Scaffold` já consumir/aplicar inset de forma diferente na versão Compose efetivamente resolvida, adaptar a solução à API disponível e verificar bounds em runtime; não somar manualmente alturas estimadas da status bar.

## 4. Superfícies prováveis

- `android/app/src/main/kotlin/com/pingo/alertacrypto/presentation/navigation/PingoAppNavigation.kt`: slot `topBar` compartilhado e padding fixo do `PingoLogo`.
- `android/app/src/main/kotlin/com/pingo/alertacrypto/MainActivity.kt`: somente se reprodução apontar configuração da janela/edge-to-edge como causa.
- `android/app/src/androidTest/kotlin/com/pingo/alertacrypto/presentation/navigation/`: teste de renderização/insets e regressão de navegação.

Não alterar `SettingsScreen`, o componente de marca `PingoLogo`, o tema da janela ou telas individuais, a menos que evidência de teste exija uma mudança estritamente necessária.

## 5. Sequência de implementação

1. Reproduzir no aparelho e identificar API Android, orientação, tema e bounds visuais do relógio e do lockup.
2. Adicionar um teste instrumentado focado para garantir que o cabeçalho ocupa a área abaixo dos insets superiores reportados pelo sistema; incluir a captura/checagem visual nas telas principais.
3. Aplicar o inset superior exatamente uma vez no slot compartilhado do cabeçalho e remover padding fixo redundante.
4. Confirmar que o conteúdo do `NavHost` continua iniciando abaixo do cabeçalho, que busca/títulos não ficam cortados e que a navegação inferior mantém os insets atuais.
5. Validar modo claro/escuro, recorte de tela/inset aumentado se disponível, rotação e fonte/tamanho de exibição ampliados.
6. Rodar build debug, testes unitários e testes instrumentados aplicáveis; seguir [quickstart.md](./quickstart.md) no aparelho.

## 6. Estratégia de validação

- **Automatizada**: teste Compose instrumentado verifica que o bounds superior do lockup não cruza o inset superior da janela, usando o inset real do dispositivo de teste em vez de um número fixo de dp.
- **Regressão de telas**: confirmar cabeçalho alinhado e conteúdo visível em Mercado, Alertas e Ajustes, bem como em destinos secundários que mantêm o lockup comum.
- **Insets**: validar um perfil com recorte/inset superior maior ou simulação de recorte, quando disponível; mudar orientação e confirmar recalculação do inset.
- **Visual**: comparar a captura do aparelho antes/depois em claro e escuro, confirmar hora e ícones legíveis e marca sem mudança visual.
- **Acessibilidade/layout**: conferir fonte ampliada, alvos de toque da barra de status sem interceptação e ausência de padding superior duplicado.
- **Build/testes**: `.\gradlew.bat assembleDebug testDebugUnitTest`; compilar/executar testes instrumentados em emulador ou aparelho conectado.

## 7. Gate de conclusão

1. AC-001–AC-005 passam em dispositivo Android e nos testes aplicáveis.
2. O lockup fica integralmente abaixo do inset superior real em todos os destinos compartilhados.
3. Hora/ícones de sistema permanecem visíveis e não são cobertos nem interceptados.
4. O lockup mantém aparência; conteúdo principal não é deslocado por insets duplicados.
5. Build e testes previstos passam; nenhuma alteração fora do escopo de apresentação/insets é incluída.
