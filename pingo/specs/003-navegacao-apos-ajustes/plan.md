# Plano de implementação: Navegação após alterar Ajustes

**Feature**: `003-navegacao-apos-ajustes`  
**Escopo**: correção localizada da navegação principal depois de interações em Ajustes.  
**Especificação**: [spec.md](./spec.md)  
**Pesquisa técnica**: [research.md](./research.md)  
**Validação manual**: [quickstart.md](./quickstart.md)

## 1. Objetivo e limites

Garantir que Mercado, Alertas e Ajustes permaneçam navegáveis pela barra inferior depois de uma alteração em Ajustes, e que a alteração continue aplicada ao trocar de destino.

Esta feature não redesenha Ajustes, não muda o armazenamento ou os callbacks das opções, e não altera navegação de detalhes/deep links, alertas, preços, notificações ou monitoramento, salvo se a reprodução demonstrar que uma mudança localizada é indispensável ao bug.

Não há mudança de modelo de dados, persistência ou dependências prevista. Reutilizar Navigation Compose, `PingoRoutes`, `PingoAppNavigation`, `SettingsScreen` e a infraestrutura de testes existente.

## 2. Estado atual verificado e hipótese a validar

- `PingoAppNavigation` hospeda os destinos em um `NavHost` e constrói a navegação inferior no `Scaffold` que o envolve.
- A barra inferior é renderizada quando `currentRoute` corresponde exatamente a `market`, `alerts` ou `settings`. Os itens navegam com `popUpTo(market)`, `saveState`, `restoreState` e `launchSingleTop`.
- O chip de monitoramento do Mercado abre Ajustes com navegação direta; Ajustes não possui um `Scaffold` nem uma barra de navegação próprios.
- A seleção de tema é enviada para `MainActivity`, que grava em `ThemePreferenceStore` e observa o valor para atualizar o tema Compose. As demais ações de Ajustes mantêm seus callbacks atuais.
- O teste de navegação existente cobre um `NavHost` mínimo para Mercado/Detalhes, mas não cobre a barra inferior real, o destino Ajustes nem uma alteração de configuração seguida por troca de aba.
- O relato do aparelho não identifica ainda qual controle foi alterado nem se a barra inferior desaparece, fica coberta ou recebe toque sem navegar. Portanto, não presumir causa: reproduzir tema e outras opções e observar rota atual, estado/semântica da barra, back stack e ciclo de vida ao retornar de telas do sistema.

## 3. Estratégia técnica

1. Reproduzir o cenário em aparelho e teste instrumentado, registrando separadamente: alteração do tema; alteração de monitoramento; retorno de uma tela externa de configurações, quando aplicável; troca para Mercado por toque na barra inferior.
2. Cobrir a navegação principal real (ou extrair somente o menor componente/helper testável da barra, se a criação de `PingoAppNavigation` exigir dependências de produção) com verificações de:
   - barra inferior visível em Ajustes antes e depois da alteração;
   - Mercado selecionável, ativo após toque e conteúdo de Mercado exibido;
   - ida e volta entre os três destinos sem empilhar cópias nem perder estado relevante;
   - persistência/aplicação do tema e do estado da opção alterada depois da navegação;
   - barra e rota corretas após retorno de uma Activity de configurações do Android.
3. Identificar pela reprodução se o defeito vem do cálculo de destino selecionado/visibilidade da barra, do callback/retorno de configuração ou da ação de navegação. Aplicar somente a correção compatível com a causa confirmada; não alterar a semântica de `SettingsScreen` nem o repositório de preferências.
4. Usar seletores/semântica acessíveis já existentes ou adicionar tags de teste estáveis somente quando necessário para localizar os itens da barra sem depender de detalhes visuais.
5. Executar testes instrumentados focados e a suíte unitária existente; confirmar manualmente em aparelho Android conforme [quickstart.md](./quickstart.md).

## 4. Arquivos e superfícies prováveis

- `android/app/src/main/kotlin/com/pingo/alertacrypto/presentation/navigation/PingoAppNavigation.kt`: seleção/visibilidade dos destinos e ações dos itens da barra inferior.
- `android/app/src/main/kotlin/com/pingo/alertacrypto/presentation/settings/SettingsScreen.kt`: pontos de interação que disparam callbacks, sem redesenho.
- `android/app/src/main/kotlin/com/pingo/alertacrypto/MainActivity.kt`: callbacks de tema, monitoramento e retorno de Activity externa; alterar somente se a causa comprovada estiver aqui.
- `android/app/src/androidTest/kotlin/com/pingo/alertacrypto/presentation/navigation/`: teste instrumentado da barra inferior e fluxo Ajustes → Mercado.
- `android/app/src/androidTest/kotlin/com/pingo/alertacrypto/presentation/settings/`: cobrir callbacks de configuração somente se necessário para reproduzir o encadeamento.

Não modificar arquivos de Room, domínio, fontes de preços, serviços, workers, notificações ou widgets.

## 5. Estratégia de validação

- **Instrumentado Compose**: iniciar em Mercado, abrir Ajustes, alterar tema, confirmar que a navegação inferior continua acessível, tocar Mercado e verificar destino/tema; retornar a Ajustes e confirmar estado selecionado.
- **Instrumentado de configuração**: executar o mesmo fluxo com uma configuração não visual disponível que possa ser controlada sem conceder permissões ou depender de serviço real; confirmar que a mudança não é revertida ao navegar.
- **Ciclo de Activity**: para a ação que abre configurações do sistema, voltar ao app e verificar barra e destino antes de selecionar Mercado.
- **Regressão de navegação**: selecionar repetidamente Mercado, Alertas e Ajustes; validar destaque do item ativo, ausência de duplicação da pilha e funcionamento do botão Voltar conforme comportamento existente.
- **Build/testes**: `.\gradlew.bat assembleDebug testDebugUnitTest`; executar o teste instrumentado focado em dispositivo/emulador conectado.
- **Aparelho real**: completar os passos do quickstart em tema claro e escuro, sem usar o botão Voltar do sistema para sair de Ajustes.

## 6. Gate de conclusão

Considerar a feature pronta somente quando:

1. o defeito tiver reprodução automatizada no teste de navegação antes da correção, quando viável;
2. os cenários de aceite AC-001–AC-004 passarem no teste instrumentado e a navegação por toque funcionar em aparelho;
3. a configuração alterada permanecer aplicada após navegar;
4. build debug e testes unitários existentes passarem;
5. revisão confirmar que nenhum fluxo de domínio ou tela fora do escopo foi alterado.
