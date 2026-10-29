# Quickstart: validação ponta a ponta em aparelho real

Este roteiro valida os fluxos principais da feature em um aparelho Android real. A execução depende de rede e das políticas/limites da versão Android instalada; nenhum passo pressupõe execução imediata ou contínua garantida pelo sistema.

## Pré-requisitos

- Build de debug instalado, com `targetSdk 36`.
- Aparelho Android conectado por USB ou disponível para inspeção manual.
- Notificações permitidas para o aplicativo e canal de alertas habilitado.
- Acesso à internet, exceto nos passos que instruem ativar o modo avião.
- Para validar `onTimeout`, aparelho/emulador Android 15 ou superior e um método de teste seguro que provoque o callback sem esperar o esgotamento real da cota diária.
- Anotar modelo do aparelho, versão Android/API, versão do app, data/hora e resultado de cada passo.

## Roteiro

### 1. Lista de preços

1. Inicie o app com internet disponível.
2. Confirme que BTC, ETH, SOL, XRP, ADA e DOGE aparecem com preço BRL, variação de 24 horas e horário da cotação.
3. Confirme textos em pt-BR e valores no formato brasileiro, por exemplo `R$ 1.234,56`.
4. Verifique que a lista atualiza enquanto o app está aberto e que erro/falha da fonte não é exibido como preço atual.

**Esperado**: os seis ativos suportados podem ser consultados; cada preço tem contexto temporal e formato local.

### 2. Criar e disparar alerta

1. Escolha um ativo cujo preço esteja variando e crie um alerta repetível com alvo próximo do preço atual e direção compatível com o movimento esperado.
2. Mantenha o app no modo de monitoramento que está sendo validado e aguarde uma travessia real do alvo; confirme que o alerta dispara apenas uma vez até o rearme.
3. Para validar o rearme de forma determinística, execute também os testes de domínio com a sequência SC-008: alvo R$ 100, disparo em R$ 101, R$ 99,95 mantendo desarmado e R$ 99,89 rearmando.
4. Confirme que uma nova notificação exige nova travessia do alvo após o rearme.
5. Confira moeda, alvo, preço observado, direção, texto pt-BR e formato BRL na notificação.
6. Toque na notificação e confirme que abre o detalhe da moeda correspondente.

**Esperado**: cruzamento e rearme seguem a histerese documentada; a notificação é idempotente e navega ao ativo correto.

### 3. Modo contínuo e interrupção `dataSync`

1. Ative monitoramento contínuo pela interface e confirme que a notificação persistente explica o estado do serviço.
2. Coloque o app em segundo plano e confirme recebimento de ticks/avaliação de alertas enquanto o sistema mantém o serviço.
3. Provoque `Service.onTimeout` por meio do mecanismo de teste definido para o app, sem aguardar nem tentar consumir a cota real de seis horas.
4. Confirme que o serviço para rapidamente, o estado `SYSTEM_INTERRUPTED` fica visível, o fallback WorkManager é agendado e não há tentativa de iniciar outro `dataSync` para contornar o limite.

**Esperado**: o timeout não deixa o serviço ativo nem promete continuidade; o fallback é adiado/limitado conforme o sistema.

### 4. Widget

1. Adicione uma instância do widget e selecione um ativo.
2. Confira preço BRL, variação de 24 horas, moeda e horário nos formatos compacto (2x1) e ampliado (2x2).
3. Verifique texto pt-BR e formato BRL, ausência de polling próprio e atualização solicitada por `updateAll()` após persistência de uma cotação.
4. Com a cotação além do limiar específico de 30 minutos, confirme a indicação de dado desatualizado.
5. Toque no widget e confirme que o app abre.

**Esperado**: o widget lê a última cotação persistida, identifica dado ausente/desatualizado e não faz rede por conta própria.

### 5. Modo avião e recuperação

1. Com uma cotação já carregada, ative o modo avião.
2. Confirme que o app sinaliza offline, mantém visível o horário da última cotação e não apresenta o valor armazenado como atual depois do limiar de frescor correspondente.
3. Confirme comportamento de reconexão com backoff, sem tentativas em loop apertado.
4. Desative o modo avião e aguarde a recuperação da fonte ou a próxima execução permitida do Worker.
5. Confirme atualização do estado de conectividade, persistência da nova cotação e solicitação de atualização do widget.

**Esperado**: offline, dado antigo e recuperação ficam claros; não há promessa de execução imediata em segundo plano.

## Registro de execução

| Data/hora | Aparelho / Android | Passo | Resultado | Observações |
|---|---|---|---|---|
|  |  |  |  |  |
