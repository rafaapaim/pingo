# Constituição do Alerta de Cripto para Android

## Princípios Fundamentais

### I. Android nativo e arquitetura consistente
O aplicativo será desenvolvido em Kotlin com Jetpack Compose, seguindo MVVM e Repository e usando Hilt para injeção de dependências. A lógica de interface, domínio e acesso a dados deve permanecer separada: telas não acessam fontes de dados diretamente, e regras de negócio não dependem de Android, rede ou persistência. O `minSdk` é 26; o `targetSdk` deve acompanhar a versão estável atual exigida para distribuição e ser reavaliado a cada release.

### II. Domínio puro e desenvolvimento orientado a testes
Regras de domínio — especialmente a detecção de cruzamento de preço, direção do alerta, rearme e comportamento de disparo único ou repetido — devem ser expressas como funções puras, determinísticas e sem efeitos colaterais. Antes de implementar ou alterar uma regra, escreva testes unitários que definam seus casos normais, limites e casos de erro; confirme que falham pelo motivo esperado e, então, implemente a menor solução que os faça passar. Efeitos como notificação, persistência e atualização de interface ficam fora dessas funções.

### III. Bateria e execução em segundo plano são requisitos de primeira classe
O consumo de energia deve orientar as decisões de monitoramento, não ser uma otimização posterior. Reconexões devem usar backoff com espera crescente e limites razoáveis; o monitoramento deve pausar ou reduzir atividade no modo Economia de bateria e respeitar conectividade e demais restrições do sistema. Ofereça alternativas compatíveis com o sistema, como atualização periódica econômica quando monitoramento contínuo não for necessário. Não prometa precisão ou execução contínua que o Android não garante, e torne as escolhas de monitoramento compreensíveis para a pessoa usuária.

### IV. Local-first e privacidade por padrão
A versão 1 não terá login nem backend próprio: alertas, preferências e demais dados do usuário permanecem no dispositivo. A rede será usada somente para obter preços de provedores externos necessários ao produto. Não introduza sincronização, coleta remota ou dependência de conta sem alteração explícita deste princípio e dos requisitos do produto.

### V. Experiência localizada, acessível e transparente
A interface e as mensagens serão em pt-BR. Valores monetários devem usar formatação local, por exemplo, `R$ 1.234,56`, sem conversões ou arredondamentos silenciosos que alterem o valor do alerta. O app deve oferecer tema claro e escuro e manter estados offline, carregamento e erros relevantes visíveis, com orientação útil e sem apresentar dados desatualizados como atuais. Notificações e widgets também devem respeitar a localização e indicar quando o preço foi atualizado.

### VI. Conformidade com limites e ciclo de vida do Android
Serviços em primeiro plano, permissões, reinicialização após boot e atualização de widgets devem respeitar as regras da versão do Android em execução e as políticas de distribuição. Em particular, considere o limite de tempo do tipo `dataSync` no Android 15 e versões posteriores, as restrições a inicialização de serviços a partir de `BOOT_COMPLETED`, as permissões e os limites de frequência de atualização de widgets. Use WorkManager para trabalho adiável e garantido apenas dentro das condições do sistema; não contorne restrições de plataforma para manter monitoramento contínuo. Mudanças de SDK exigem revisar permissões, tipos de serviço, boot e comportamento de widgets.

## Restrições Técnicas e de Produto

- Kotlin, Jetpack Compose, MVVM + Repository e Hilt são a base obrigatória da aplicação.
- `minSdk` permanece em 26; `targetSdk` acompanha a versão estável atual aplicável no momento da release.
- Persistência e processamento de alertas são locais na v1. A arquitetura não deve exigir backend próprio nem login.
- A obtenção de preços pode depender de rede e de provedores externos; indisponibilidade, desconexão e dados antigos devem ser tratados explicitamente.
- Monitoramento contínuo não é garantido pelo sistema operacional. Restrições de bateria, rede, serviço, boot e widgets prevalecem sobre expectativas de atualização em tempo real.

## Qualidade e Fluxo de Desenvolvimento

- Toda regra de domínio nova ou alterada começa por testes unitários de função pura, escritos antes da implementação.
- Mudanças em serviço, repositório, notificações, permissões ou widgets devem validar os respectivos limites de integração e ciclo de vida, além dos testes de domínio afetados.
- Revisões devem verificar a separação de responsabilidades, o impacto em bateria, a visibilidade de estados offline/erro, a localização pt-BR e a compatibilidade com as restrições atuais do Android.
- Documentação de especificações, planos e tarefas deve refletir esta constituição; conflitos devem ser resolvidos atualizando os artefatos afetados, não ignorando silenciosamente os princípios.

## Governança

Esta constituição orienta especificações, planos, tarefas, implementação e revisão do produto. Uma proposta que conflite com estes princípios deve explicitar o conflito, justificar a necessidade e atualizar esta constituição e os documentos diretamente relacionados antes de ser considerada parte do escopo. Alterações em requisitos de plataforma devem ser verificadas contra a documentação oficial vigente para a versão do Android alvo.

**Versão**: 1.0.0 | **Ratificada**: 2026-10-29 | **Última alteração**: 2026-10-29
