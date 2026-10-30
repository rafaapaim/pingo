# Modelo de dados: preferências do redesign

Este documento registra as novas preferências persistidas pelo redesign (favoritos e tema) e a projeção de apresentação do AlertCard necessária para o redesign. A projeção não introduz campos persistidos. As entidades Room permanecem inalteradas; ver o [modelo funcional original](../001-alerta-cripto/data-model.md).

## Preferências

Armazenamento em um DataStore de preferências do produto, separado do DataStore de estado de monitoramento existente.

| Preferência | Tipo persistido | Default | Regras |
|---|---|---|---|
| `favorite_symbols` | `Set<String>` | conjunto vazio | Símbolos de `Asset.supported`; descartar entradas desconhecidas ao ler. Atualização atômica ao favoritar/desfavoritar. |
| `theme_mode` | `String` enum: `SYSTEM`, `LIGHT`, `DARK` | `SYSTEM` | Valor desconhecido/malformado é tratado como `SYSTEM`; preferências mudam a apresentação e não o domínio. |

### Migração do tema

O código atual mantém `darkTheme` em `rememberSaveable`, inicializa pelo tema do sistema e não persiste um modo selecionado em DataStore ou SharedPreferences. Não há valor durável conhecido para converter. Portanto:

1. A primeira leitura sem valor salvo resulta em `SYSTEM`.
2. Não copiar o estado do sistema como preferência manual Claro/Escuro.
3. Se a implementação localizar uma chave de tema antiga persistida fora do código atualmente verificado, migrar somente strings/valores explicitamente reconhecidos para `LIGHT` ou `DARK`, remover a chave antiga após migração bem-sucedida e usar `SYSTEM` para valores ausentes/desconhecidos.
4. Uma escolha feita no novo seletor é persistida imediatamente; `SYSTEM` segue `isSystemInDarkTheme()`.

### Favoritos

- Favoritar e desfavoritar não modifica moedas, cotações, alertas, configuração de widget ou ordem-base.
- A lista de favoritos é ordenada conforme a ordem canônica de `Asset.supported`, sem depender da ordem de iteração do conjunto persistido.
- A lista completa continua acessível. Favoritos ficam primeiro; filtro de busca aplica-se ao conjunto ordenado.
- Símbolos inválidos são ignorados ao carregar e nunca criam ativos novos.

## Projeção de estado do AlertCard (não persistida)

O card lê a entidade de alerta existente e aplica esta precedência, sem criar um estado paralelo:

1. `repeatMode == ONE_SHOT && armed == false` → **Disparado**.
2. `enabled == false` → **Pausado**.
3. `enabled == true && armed == false && repeatMode == REPEATING` → **Aguardando rearme**.
4. Qualquer outra combinação → **Ativo**.

`repeat=false` corresponde ao `repeatMode == ONE_SHOT` existente. A primeira condição tem precedência inclusive se o alerta disparado posteriormente estiver desabilitado. Pausar um alerta repetível desarmado mostra Pausado, porque a condição 2 precede a condição 3.

### Persistência do disparo único

O esquema Room existente já registra o disparo sem novas colunas:

- `repeatMode = ONE_SHOT` identifica a configuração de disparo único;
- `armed = false` registra que o único disparo já ocorreu;
- `firedUp` guarda a direção do último disparo (`true` ascendente, `false` descendente);
- `lastFiredAt` e `lastFiredTickAt` guardam instante e tick do disparo.

Essa combinação representa permanentemente o estado **Disparado** até edição explícita ou criação de outro alerta. O data-model funcional afirma que ONE_SHOT não rearma automaticamente. Nenhuma alteração ao esquema Room é necessária para representar esse estado.

Para apresentar a histerese, pode ser necessário expor a constante de domínio como leitura imutável; isso não altera o esquema Room nem a lógica de disparo/rearme.

### Texto de rearme e histerese

Somente para estado **Aguardando rearme**, usar a direção persistida em `firedUp` e a constante do domínio `CrossingDetector.HYSTERESIS` (atualmente `0.001`). Se a apresentação precisar acessá-la, expor a mesma constante como leitura imutável/API pura do domínio; não criar uma cópia da constante na camada de apresentação:

- `firedUp == true`: `X = targetPriceBRL × (1 - HYSTERESIS)` e texto **“Reativa quando o preço voltar abaixo de R$ X”**.
- `firedUp == false`: `X = targetPriceBRL × (1 + HYSTERESIS)` e texto **“Reativa quando o preço voltar acima de R$ X”**.

O domínio compara com limite inferior/superior usando decimal exato e inclusão no limiar (`<=`/`>=`). O texto `R$ X` é apenas apresentação formatada; não arredondar o limite usado pela lógica. Para `Direction.BOTH`, `firedUp` sempre representa o disparo mais recente e determina o lado oposto exigido para rearme.

Testes de apresentação devem cobrir UP, DOWN e BOTH para cruzamentos ascendentes e descendentes, incluindo rearme e cópia de `firedUp` nos dois sentidos possíveis de BOTH.
