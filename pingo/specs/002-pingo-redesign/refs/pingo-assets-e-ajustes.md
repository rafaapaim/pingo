# Pingo — Assets de marca e tela de Ajustes

Documento de referência para o agente (colocar em `specs/002-.../refs/`). Estas especificações têm precedência sobre as pranchas conceituais.
Tokens de cor: os do documento "Pingo SPEC Redesign" (primary #6750A4 / #D0BCFF).

---

## PARTE 1 — Marca (VectorDrawable)

### Geometria (referência única)
Grade 108×108 (ícone adaptativo; zona segura = círculo central de 66, conteúdo entre 18 e 90).
- **Gota:** ponta em (54,23). Corpo = semicírculo inferior de raio 22 com centro (54,63): extremos (32,63) e (76,63), base em y=85. Laterais = curvas cúbicas da ponta até os extremos, com controles (46,33)→(32,49) à esquerda e (62,33)→(76,49) à direita.
- **Ponto (pulso):** círculo de raio 7 em (54,65), vazado (fillType evenOdd), de modo que mostre o fundo.
- Proporção: gota ≈ 44 de largura × 62 de altura. Não adicionar outros detalhes (legibilidade em launcher, widget e notificação).

### `res/drawable/ic_launcher_background.xml`
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp" android:height="108dp"
    android:viewportWidth="108" android:viewportHeight="108">
    <path android:pathData="M0,0h108v108h-108z">
        <aapt:attr name="android:fillColor" xmlns:aapt="http://schemas.android.com/aapt">
            <gradient android:type="linear"
                android:startX="0" android:startY="0" android:endX="108" android:endY="108">
                <item android:offset="0" android:color="#8B78C6"/>
                <item android:offset="1" android:color="#6750A4"/>
            </gradient>
        </aapt:attr>
    </path>
</vector>
```

### `res/drawable/ic_launcher_foreground.xml`
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp" android:height="108dp"
    android:viewportWidth="108" android:viewportHeight="108">
    <path android:fillColor="#FFFFFF" android:fillType="evenOdd"
        android:pathData="M54,23 C46,33 32,49 32,63 A22,22 0 0 0 76,63 C76,49 62,33 54,23 Z M47,65 a7,7 0 1,0 14,0 a7,7 0 1,0 -14,0 Z"/>
</vector>
```

### `res/drawable/ic_launcher_monochrome.xml` (Android 13+, ícone temático)
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp" android:height="108dp"
    android:viewportWidth="108" android:viewportHeight="108">
    <path android:fillColor="#000000" android:fillType="evenOdd"
        android:pathData="M54,23 C46,33 32,49 32,63 A22,22 0 0 0 76,63 C76,49 62,33 54,23 Z M47,65 a7,7 0 1,0 14,0 a7,7 0 1,0 -14,0 Z"/>
</vector>
```

### `res/mipmap-anydpi-v26/ic_launcher.xml` e `ic_launcher_round.xml` (conteúdo idêntico)
```xml
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
    <monochrome android:drawable="@drawable/ic_launcher_monochrome"/>
</adaptive-icon>
```
No manifest: `android:icon="@mipmap/ic_launcher"` e `android:roundIcon="@mipmap/ic_launcher_round"`.

### `res/drawable/ic_pingo_mark.xml` (marca na barra superior e telas; colorida pelo tema)
Mesma geometria em 24×24 (ponta em x=12, y=2; semicírculo de raio 7 com centro em x=12, y=15; ponto de raio 2,4 centrado em x=12, y=15,5). Use `android:tint="?attr/colorPrimary"` ou `Icon(tint = MaterialTheme.colorScheme.primary)`.
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp"
    android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#6750A4" android:fillType="evenOdd"
        android:pathData="M12,2 C9.5,5.5 5,10 5,15 A7,7 0 0 0 19,15 C19,10 14.5,5.5 12,2 Z M9.6,15.5 a2.4,2.4 0 1,0 4.8,0 a2.4,2.4 0 1,0 -4.8,0 Z"/>
</vector>
```
Use o mesmo arquivo como **`ic_stat_pingo.xml`** (ícone pequeno de notificação, trocando `fillColor` para `#FFFFFF`) em `setSmallIcon`, e `colorPrimary` do tema como cor de destaque da notificação.

### Lockup horizontal (marca + nome)
Marca (`ic_pingo_mark`, 28dp) + texto "Pingo" em Roboto Medium 22sp, espaçamento de 8dp entre eles, ambos na cor `primary`. Implementar como Composable `PingoLogo`, sem imagem rasterizada.

### Critérios
- Launcher e ícone temático mostram a gota centralizada e legível em 48dp.
- O ponto vazado revela o fundo (roxo no launcher, transparente no monochrome).
- Nenhum PNG novo; só vetores.

---

## PARTE 2 — Tela Ajustes

Destino da aba "Ajustes" (ícone `settings`). Apenas apresentação: reutilizar as preferências e os estados já existentes (tema, monitoramento contínuo, estado da conexão, permissão de notificação). Não criar nova fonte de verdade.

### Estrutura
- Barra superior: `PingoLogo` à esquerda; título da tela "Ajustes" (28sp) abaixo.
- Conteúdo em `LazyColumn`, margens laterais de 20dp, espaço de 16dp entre cartões.
- Cinco cartões (surface, raio 16dp, padding 16dp). Título de cada cartão: 20sp, semibold. Linhas com altura mínima de 48dp.
- Rodapé: marca + "Acompanhe. Monitore. Seja alertado." (legenda 12sp, cor neutral).

### Cartão 1 — Aparência
- Título: **Aparência**
- Rótulo: "Tema"
- Seletor segmentado (3 opções, uma selecionada): **Sistema** · **Claro** · **Escuro** (ícones: `brightness_auto`, `light_mode`, `dark_mode`)
- Ajuda: "Sistema segue o tema do seu celular."
- Estados: padrão "Sistema". Ao alterar, aplica imediatamente e persiste. Migrar o valor antigo (claro/escuro) se existir.

### Cartão 2 — Monitoramento
- Título: **Monitoramento**
- Linha principal com Switch: **Monitoramento contínuo**
  - Descrição: "Atualiza os preços quase em tempo real, mesmo com o app fechado. Consome mais bateria e o Android pode interromper."
- Linha de status (chip + texto), sempre refletindo o estado real:

| Estado | Chip | Texto |
|---|---|---|
| Desligado | Econômico (neutro) | "Verificações aproximadamente a cada 15 minutos. Os horários podem atrasar." |
| Iniciando | indicador de progresso | "Iniciando monitoramento…" |
| Ativo | Ativo (positivo, com ícone) | "Cotações quase em tempo real. Última atualização às HH:mm:ss." |
| Pausado pela economia de bateria | Pausado (neutro) | "Pausado enquanto a economia de bateria está ativa. Volta sozinho depois." |
| Interrompido pelo Android | Interrompido (atenção) | "O Android encerrou o monitoramento contínuo. Usando verificações econômicas." + botão **Reativar** |
| Sem conexão | Offline (negativo, com ícone) | "Sem conexão. Tentando reconectar." |

- Seção expansível **Como funciona** (recolhida por padrão):
  - "Contínuo: mantém a conexão aberta e avisa assim que o preço cruza o valor. Mostra uma notificação fixa."
  - "Econômico: o Android verifica os preços de tempos em tempos. Gasta menos bateria, mas pode perder um cruzamento rápido."
- Erro ao ligar: o Switch volta ao estado anterior e aparece um aviso: "Não foi possível iniciar o monitoramento contínuo. Tente novamente."
- Não duplicar aqui o status de conexão do Mercado: este cartão mostra o modo, o Mercado mostra a fonte.

### Cartão 3 — Notificações
- Título: **Notificações**
- Estados:

| Estado | Chip | Texto | Botão |
|---|---|---|---|
| Permitidas | Ativadas (positivo) | "Você será avisado quando o preço cruzar o valor de um alerta." | Gerenciar |
| Negadas (pode pedir) | Desativadas (atenção) | "Sem esta permissão, o Pingo não consegue avisar você quando um alerta disparar." | Permitir notificações |
| Negadas definitivamente | Desativadas (atenção) | mesmo texto | Abrir configurações |
| Android anterior ao 13 | Ativadas (positivo) | mesmo texto de "Permitidas" | Gerenciar |

- "Gerenciar" e "Abrir configurações" abrem as configurações de notificação do app. "Permitir notificações" dispara o pedido de permissão em tempo de execução.
- Ao voltar das configurações, reler a permissão e atualizar o chip.

### Cartão 4 — Bateria
- Título: **Bateria**
- Texto: "Alguns celulares encerram apps em segundo plano para economizar bateria. Se os alertas atrasarem, permita que o Pingo funcione sem restrições."
- Chip opcional, só se o sistema permitir consultar: "Sem restrições" (positivo) ou "Otimizada" (neutro).
- Botão: **Abrir configurações do app** (tela de detalhes do aplicativo). Não pedir isenção de otimização por diálogo do sistema.

### Cartão 5 — Sobre
- Título: **Sobre**
- Linhas:
  - "Versão" — valor: `1.0.0 (build N)`, obtido do `BuildConfig`/`PackageInfo`.
  - "Política de privacidade" — abre o link no navegador (URL em um único lugar do código).
  - "Fontes de dados" — expande: "Preços: Binance (tempo real) e CoinGecko (reserva). Podem ter atraso ou pequenas diferenças entre si."
  - "Aviso" — expande: "O Pingo mostra preços e avisa quando eles cruzam valores definidos por você. Não é recomendação de investimento."

### Acessibilidade e comportamento
- Linha com Switch é um único alvo de toque (função de alternância); rótulo lido: "Monitoramento contínuo, ativado/desativado".
- O seletor de tema anuncia a opção selecionada.
- Mudanças de status do monitoramento são anunciadas de forma educada (live region).
- Cor nunca é o único indicador: todo chip tem texto e, quando útil, ícone.
- Fonte ampliada: textos quebram em várias linhas sem truncar; botões ocupam a largura toda quando não couberem ao lado do texto.
- Alvos de toque de pelo menos 48dp; contraste de texto de pelo menos 4,5:1. Texto comum em superfícies neutras sempre em `onSurface`; lockup `PingoLogo` usa `primary`; textos de variação usam os tokens próprios sobre `background`/`surface`; em chips, usar os tokens pareados de texto/contêiner definidos em `spec.md`. Cores semânticas originais ficam em ícones, setas e preenchimentos. Os tokens específicos de chips e variações da especificação prevalecem sobre esta regra geral.
- Respeitar "remover animações": expandir e recolher sem animação nesse caso.
- Estado de carregamento da tela: placeholders simples; nunca exibir estado inventado enquanto o real não carrega.

### Componentes
`SettingsCard`, `SettingsRow`, `ThemeSegmentedSelector`, `StatusChip`, `MonitoringStatus`, `PermissionRow`. Reutilizar `StatusChip` e `EmptyState` já previstos no plano.
