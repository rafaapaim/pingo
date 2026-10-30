# Pesquisa técnica: Redesign da experiência do Pingo

**Atualizado em**: 2026-10-30  
**Escopo**: decisões técnicas do redesign, direitos de uso dos logos e comportamento atual relevante.

## 1. Decisões e pesquisa técnica

### Navegação Compose

O app já usa Compose, Material 3 e `navigation-compose` não está no catálogo nem nas dependências do app. A API oficial de Navigation Compose fornece `NavHost`, `NavController` e destinos com suporte a back stack, deep links, padrões de navegação e integração com ViewModels. Isso atende os cinco destinos requeridos e a abertura de Detalhes a partir de intents do widget/notificação com uma única biblioteca específica.

**Decisão**: adicionar somente `androidx.navigation:navigation-compose`, fixando uma versão estável compatível com o Compose/Kotlin do projeto quando implementado e mantendo-a no version catalog. Não adicionar `material-icons-extended`, `hilt-navigation-compose`, biblioteca de ícones ou outra dependência. Usar vetores próprios por recurso; ViewModels podem ser entregues aos destinos pela activity/API lifecycle existente.

**Deep link**: usar a rota `pingo://asset/{symbol}` no grafo do Navigation Compose. Notificação e widget enviam intents explícitas para `MainActivity` com essa URI, mantendo a origem e o símbolo. Não declarar um intent-filter público nesta entrega: a rota é para navegação interna, não uma integração externa. O app valida o símbolo contra a lista fechada de ativos.

Fontes:
- [Navigation Compose — guia oficial Android](https://developer.android.com/develop/ui/compose/navigation)
- [Navigation — notas de versão AndroidX](https://developer.android.com/jetpack/androidx/releases/navigation)

### DataStore

Preferences DataStore já é dependência do app e é usado pelo estado de monitoramento. A documentação oficial descreve DataStore como armazenamento assíncrono baseado em Flow, com transações, apropriado para pares chave-valor pequenos. Favoritos e modo de tema são preferências locais, não dados relacionais nem eventos de alerta.

**Decisão**: adicionar um repositório de preferências separado, sem modificar Room de alertas nem o arquivo DataStore atual de monitoramento. Persistir favoritos como conjunto validado de símbolos e tema como `SYSTEM`, `LIGHT` ou `DARK`.

**Migração de tema verificada no código**: o `darkTheme` atual em `MainActivity` é estado Compose `rememberSaveable`, inicializado com `isSystemInDarkTheme()` e alterado por botão. Não foi encontrada chave persistida em DataStore/SharedPreferences para tema. Assim, não há valor durável antigo que possa ser migrado entre atualizações. O novo valor inicial é `SYSTEM`; somente se uma chave/arquivo legado for encontrado no código ou em dados de produção durante a implementação, converter valores conhecidos claro/escuro e manter desconhecidos em `SYSTEM`. Não persistir uma suposição baseada na aparência do sistema.

Fonte:
- [DataStore — documentação oficial Android](https://developer.android.com/topic/libraries/architecture/datastore)

### Material 3, cor e tipografia

O app já tem Material 3. A referência oficial de marca e Ajustes manda usar os tokens cromáticos do documento de redesign e Roboto; não requer biblioteca de fonte. A paleta de Material deve expor os tokens do spec para claro/escuro. `onSurface` aplica-se a textos em superfícies neutras; chips usam os pares específicos de texto/contêiner, e as variações usam suas cores de texto próprias em `background` ou `surface`. As cores semânticas originais ficam disponíveis para ícones, setas e preenchimentos. Cada par definido deve ser verificado e atingir contraste mínimo 4,5:1.

**Decisão**: estender o tema existente, sem adicionar biblioteca. Validar contraste renderizado dos pares de texto/superfície, não apenas os valores nominais da paleta.

Fonte normativa:
- [Spec do redesign](./spec.md), seção “Sistema visual”.
- [Referência de marca e Ajustes](./refs/pingo-assets-e-ajustes.md).

## 2. Investigação de “Selecionada”

No código atual, `PriceListScreen` declara `selectedSymbol` como `remember { mutableStateOf<String?>(null) }`. Tocar uma linha atribui seu símbolo; igualdade com o símbolo da linha controla a exibição da string `asset_selected`, cujo texto é “Selecionada”. O estado:

- existe apenas enquanto a composição/estado da tela é mantido;
- não é escrito em DataStore nem Room;
- não altera a lista, a cotação, favoritos, alertas ou widget;
- não é lido por outro componente;
- funciona como feedback transitório da linha tocada.

O redesign remove o rótulo porque é ambíguo com favorito (FR-011), mas preserva a função de selecionar a moeda ao encaminhar o símbolo tocado para Detalhes, mantendo a rota e o back stack para retorno à lista. A ação de favorito é independente e explicitamente rotulada.

Arquivos verificados:
- `android/app/src/main/kotlin/com/pingo/alertacrypto/presentation/prices/PriceListScreen.kt`
- `android/app/src/main/res/values/strings.xml`

## 3. Origem e uso dos seis logos de moedas

### Decisão atualizada após revisão visual

O usuário apontou os arquivos de imagem existentes na raiz do repositório como os ícones que devem aparecer no app. Essa fonte local substitui a decisão anterior de mostrar círculos com iniciais e substitui os SVGs externos do Simple Icons como proposta de origem para esta entrega. Não baixar nem redesenhar logos.

| Ativo | Arquivo fornecido na raiz | Formato observado | Decisão visual |
|---|---|---|---|
| BTC — Bitcoin | `btc.png` | PNG RGBA, 512×512, transparência | Usar o recurso local, sem recolorir |
| ETH — Ethereum | `eth.png` | PNG RGBA, 512×512, transparência | Usar o recurso local, sem recolorir |
| SOL — Solana | `sol.png` | PNG RGBA, 512×512, transparência | Usar o recurso local, sem recolorir |
| XRP | `xrp.png` | PNG RGBA, 512×512, transparência; símbolo preto | Usar sobre um círculo/fundo claro neutro para manter contraste em ambos os temas |
| ADA — Cardano | `ada.png` | PNG RGBA, 512×512, transparência | Usar o recurso local, sem recolorir |
| DOGE — Dogecoin | `doge.png` | PNG RGBA, 512×512, transparência | Usar o recurso local, sem recolorir |

Os arquivos foram inspecionados localmente para formato, dimensões, canal alfa e legibilidade aproximada. A solicitação explícita do usuário autoriza usá-los como assets desta implementação; isso não é uma declaração de licença pública nem de titularidade das marcas. Antes de distribuir o app publicamente, confirmar que os direitos de reprodução/redistribuição destes arquivos estão cobertos. Não bloquear a implementação visual aguardando logos alternativos nem exibir iniciais como padrão.

## 4. Compatibilidade com serviços, notificações e widgets

- Não alterar `PriceRepository`, `PriceSource`, protocolo Binance, fallback REST CoinGecko, classificação de frescor ou persistência do último PriceTick.
- Não alterar Room de alertas, algoritmo `CrossingDetector`, `AlertNotificationDispatcher`, idempotência, canais, prioridade alta, texto das notificações, permissão runtime ou comportamento de negação.
- Não alterar serviço `dataSync`, `onTimeout`, WorkManager, boot, restrições de bateria ou frequências.
- Atualizar apenas o `smallIcon` por vetor `ic_stat_pingo` e a Intent para abrir Detalhes; o conteúdo da notificação permanece pt-BR/BRL conforme strings existentes.
- Manter a configuração Glance por instância. Aplicar cores dia/noite e abrir o detalhe do símbolo armazenado, sem polling ou alteração de `updateAll()`.

Arquivos inspecionados incluem `MainActivity.kt`, `AlertNotificationFactory.kt`, `PriceGlanceWidget.kt`, `PriceWidgetConfigureActivity.kt`, `PriceWidgetState.kt` e `MonitoringStateStore.kt`.
