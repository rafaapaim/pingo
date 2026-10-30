# Quickstart: validação do redesign do Pingo

Este roteiro valida apresentação e persistência das preferências, além de confirmar que os fluxos existentes de preço, alerta, notificação, monitoramento e widget continuam funcionando.

## Pré-requisitos

- JDK e Android SDK compatíveis com o projeto, dispositivo/emulador API 26+.
- Para validação final, aparelho real com Android 13+ e acesso à internet.
- App compilado a partir do workspace.
- Para provar alertas de preço ponta a ponta, um alerta de teste pode ser configurado para um alvo próximo da cotação. Não alterar ou simular a lógica de `CrossingDetector`.

## Build e testes automatizados

No PowerShell:

```powershell
Set-Location C:\Users\r_pai\OneDrive\Desktop\workspace\Pingo\pingo\android
.\gradlew.bat assembleDebug testDebugUnitTest
```

Se houver dispositivo/emulador conectado, executar também os testes instrumentados:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

Gerar o release local para verificar shrink/R8 e integração:

```powershell
.\gradlew.bat bundleRelease
```

Executar testes focados de UI/acessibilidade, se estiverem no projeto, pelo runner Gradle padrão. Não considerar o roteiro manual substituto de testes unitários.

## Validação manual

### 1. Primeira abertura e tema

1. Instalar o build e abrir o app; a tela inicial deve ser Mercado com os seis ativos.
2. Conferir marca Pingo vetorial e barra superior apenas com a marca, sem sino/engrenagem.
3. Abrir Ajustes → Aparência. A seleção inicial deve ser Sistema.
4. Alternar Claro e Escuro; conferir aplicação imediata em todas as telas e reiniciar o app para confirmar persistência.
5. Selecionar Sistema e alternar o tema do Android; o app deve acompanhar o sistema.
6. Confirmar `onSurface` sobre superfícies neutras, pares texto/contêiner definidos para chips, tokens de texto das variações e contraste textual mínimo 4,5:1.

### 2. Mercado, busca e favoritos

1. Confirmar título “Moedas”, BTC, ETH, SOL, XRP, ADA e DOGE disponíveis, cotações e variações quando recebidas.
2. Confirmar estado online/offline/desatualizado e horário sem repetição por linha.
3. Pesquisar por nome e símbolo; limpar a busca e verificar restauração dos seis ativos.
4. Favoritar moedas diferentes; confirmar rótulo/semântica do controle, ordenação com favoritos primeiro e ordem-base estável.
5. Encerrar e reabrir o processo; confirmar persistência dos favoritos.
6. Tocar numa moeda: deve abrir Detalhes. Voltar deve retornar à lista, mantendo busca/posição quando aplicável. Não deve aparecer “Selecionada” como se fosse favorito.
7. Conferir os seis ícones entregues na raiz do repositório, mapeados a BTC/ETH/SOL/XRP/ADA/DOGE, em Mercado e Detalhes. O XRP preto deve ter fundo claro neutro e continuar legível no tema escuro.
8. Confirmar que Mercado não exibe o toggle nem o texto longo de Monitoramento contínuo, que o chip leva a Ajustes, que o horário aparece uma única vez no status global e que a lista não é deslocada por espaço vazio ou banner alto.

### 3. Detalhes e formulário de alerta

1. Conferir Detalhes: logo/fallback, nome, ticker, BRL, variação 24h, hora e ação “Criar alerta”.
2. Confirmar ausência de gráfico, períodos, máxima/mínima 24h, variação absoluta e seção “Sobre”.
3. Abrir “Criar alerta” e confirmar formulário único com moeda, valor, Acima/Abaixo/Ambas e Repetir.
4. Confirmar pré-seleção da moeda sem preenchimento silencioso do alvo e entrada decimal brasileira.
5. Salvar; conferir retorno à lista e confirmação acessível.

### 4. AlertCards

1. Criar/usar alertas ativos, pausados, repetíveis aguardando rearme e de disparo único já disparados.
2. Confirmar que preço atual e distância vêm da cotação existente, distância em cor neutra e estados:
   - ativo: habilitado e armado;
   - pausado: desabilitado;
   - aguardando rearme: habilitado e desarmado;
   - disparado: `ONE_SHOT` desarmado, inclusive se estiver desabilitado.
3. Alterar cotações/conectividade e confirmar atualização dos cards sem gravar preço/distância derivada no Room.
4. Editar, pausar/retomar e excluir; exclusão deve pedir confirmação.
5. Confirmar semântica de `Direction.BOTH`, histerese, repetição e idempotência nos testes de domínio existentes.

### 5. Ajustes

1. Confirmar cinco cartões: Aparência, Monitoramento, Notificações, Bateria e Sobre, com textos e ações da referência `refs/pingo-assets-e-ajustes.md`.
2. Alterar cada preferência e confirmar que o estado exibido acompanha a fonte existente, sem duplicar preferências ou status de conexão.
3. Confirmar que permissão negada não aparece como permitida; testar ação de pedir permissão e retorno das configurações.
4. Confirmar que a tela não pede isenção de bateria por diálogo.
5. Testar texto ampliado, TalkBack, alvos de toque >=48dp e opção de remover animações.
6. Configurar a URL pública da política antes do lançamento. Até ela ser informada/publicada, a ação exibe uma mensagem explícita de que a política ainda será publicada e não abre um link presumido.

### 6. Deep links de notificação e widget

1. Com um alerta de teste, aguardar um cruzamento real controlado pelo preço do mercado. A notificação deve manter texto original pt-BR/BRL, canal e prioridade; ícone pequeno deve ser o vetor branco Pingo.
2. Tocar na notificação e confirmar abertura dos detalhes da moeda alertada, inclusive com app encerrado e app já aberto.
3. Adicionar duas instâncias do widget e selecionar moedas diferentes. Confirmar configuração isolada por instância e layouts 2×1/2×2.
4. Conferir identidade e tokens dia/noite, frescor, texto de preço BRL e horário/variação conforme tamanho.
5. Tocar em cada widget; deve abrir Detalhes da moeda configurada naquela instância.
6. Confirmar que widget continua sem polling e usa os ciclos de atualização já existentes.

### 7. Regressão de execução em segundo plano

1. Verificar que toggle, serviço `dataSync`, `onTimeout`, fallback WorkManager, boot, modo economia de bateria, permissões e restrições permanecem iguais à implementação anterior.
2. Confirmar que a mudança de UI/ícone/deep link não altera a notificação fixa do serviço nem textos de notificações.
3. Usar modo avião e restaurar a rede; confirmar estados offline e recuperação existentes, sem alterar backoff ou limiares.
4. Repetir abertura por notificação/widget após reinício do processo.
5. Repetir a abertura de deep link com app encerrado, em segundo plano e em primeiro plano; com activity existente, confirmar `onNewIntent` → `setIntent` → navegação do NavController. Uma moeda desconhecida deve retornar a Mercado sem travar.
6. Abrir notificações e widgets configurados para moedas diferentes; confirmar que cada PendingIntent usa `FLAG_UPDATE_CURRENT`, request code distinto e extra/URI com o símbolo correto.

### Capturas opcionais do aparelho

Como evidência opcional, registrar capturas do aparelho real após aprovação visual em claro e escuro, cobrindo Mercado, os estados relevantes de Alertas, Criar alerta, Ajustes, widget e notificação. Guardar somente imagens sem dados pessoais e referenciar o modelo/versão Android usados. Essa documentação é complementar: os `@Preview` claros/escuros são a cobertura visual versionada esperada no código.

## Critérios de conclusão

- Testes automatizados e builds aplicáveis terminam sem regressões.
- Favoritos e tema persistem; o tema inicial é Sistema se não houver chave de legado válida.
- Todas as rotas do app e os dois pontos de entrada por deep link chegam ao destino correto.
- Nenhum campo derivado de AlertCard é salvo como dado de domínio.
- Serviços de monitoramento, política de preço/frescor, canais e semântica/idempotência de alertas permanecem intactos.
- Evidências manuais realizadas em aparelho devem registrar modelo, versão Android e resultado; não marcar validação real se foi feita somente em emulador.
