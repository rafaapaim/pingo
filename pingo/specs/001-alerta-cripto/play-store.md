# Material para a ficha do Google Play

## Nome

Pingo

## Descrição curta

Acompanhe cripto em reais e receba alertas de preço no widget.

## Descrição longa

Consulte preços de criptomoedas em reais e acompanhe BTC, ETH, SOL, XRP, ADA e DOGE em um só lugar.

Crie alertas para quando o preço cruzar um valor definido, para cima, para baixo ou em ambas as direções. Os alertas são guardados no aparelho e podem ser ativados, pausados ou removidos. Uma notificação informa a moeda, o valor-alvo e a cotação observada.

Adicione o widget Pingo à tela inicial, escolha a moeda para cada instância e veja a última cotação disponível, a variação de 24 horas e o horário da atualização. O widget não faz consultas de rede por conta própria e informa quando a cotação está desatualizada.

O Pingo não exige conta. Os preços são obtidos de serviços públicos de cotação; disponibilidade e frequência de atualização dependem da conexão, das fontes e das limitações do Android. O monitoramento contínuo é opcional, pode consumir mais bateria e pode ser interrompido pelo sistema. O modo econômico usa verificações periódicas, que podem atrasar.

## Política de privacidade

**Última atualização: 29 de outubro de 2026**

O Pingo não exige cadastro e não opera servidor próprio. O aplicativo não coleta nem compartilha dados pessoais, não usa publicidade e não inclui analytics ou rastreadores.

Alertas, preferências e a última cotação ficam armazenados localmente no aparelho. A desinstalação do aplicativo pode remover esses dados.

Para consultar cotações, o aplicativo se conecta aos serviços públicos Binance e CoinGecko e solicita preços dos ativos suportados. Como em qualquer conexão à internet, esses serviços podem receber dados técnicos da conexão, como endereço IP, horário e informações da solicitação, segundo as políticas próprias de cada provedor. O Pingo não envia a esses serviços o nome, e-mail, identificador de conta ou alertas pessoais da pessoa usuária.

O uso de notificações e widget é opcional. A permissão de notificações pode ser revogada nas configurações do Android. Os dados locais podem ser removidos apagando os dados do aplicativo ou desinstalando-o.

Para dúvidas de privacidade, contate a equipe responsável pelo aplicativo por meio do canal de suporte indicado na ficha do Google Play.

## Justificativa do foreground service `dataSync`

O serviço `dataSync` é iniciado somente quando a pessoa ativa explicitamente “Monitoramento contínuo”. Ele mantém uma conexão com a fonte de cotações para detectar cruzamentos de preço e entregar alertas locais enquanto o app está em segundo plano. A notificação persistente informa que o monitoramento está ativo. O serviço para ao ser desativado, respeita a economia de bateria e o limite de tempo aplicável a `dataSync`; no timeout, informa a interrupção e deixa as verificações periódicas para o WorkManager. O app não inicia esse serviço no boot nem o reinicia para contornar os limites do sistema.

## Build de release

O build usa R8/minificação e gera `app-release.aab`. Para assinar, forneça ao Gradle as quatro variáveis `PINGO_RELEASE_STORE_FILE`, `PINGO_RELEASE_STORE_PASSWORD`, `PINGO_RELEASE_KEY_ALIAS` e `PINGO_RELEASE_KEY_PASSWORD` pelo gerenciador de segredos do ambiente de build. Mantenha o keystore fora do repositório; nenhum valor secreto deve ser salvo em propriedades, arquivos Gradle ou controle de versão. Sem variáveis, o build local produz um AAB não assinado, adequado apenas para validar o empacotamento.

## Checklist antes do envio

- [ ] Publicar esta política em uma URL acessível e inserir a URL na ficha do Play.
- [ ] Confirmar a declaração de segurança de dados e a política de serviços em primeiro plano no Play Console.
- [ ] Adicionar screenshots reais do app e do widget, além dos recursos gráficos exigidos pelo Console.
- [ ] Gerar AAB release assinado com credenciais protegidas fora do repositório.
- [ ] Rever os termos e limites atuais dos provedores de cotação e as exigências vigentes do Google Play.
