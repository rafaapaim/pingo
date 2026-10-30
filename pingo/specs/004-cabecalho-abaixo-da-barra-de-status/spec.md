# Especificação: Cabeçalho abaixo da barra de status

**Feature Branch**: `004-cabecalho-abaixo-da-barra-de-status`
**Criado**: 2026-10-30
**Status**: Draft
**Contexto**: Sobreposição observada em captura de tela de aparelho Android durante validação do Pingo.

## Objetivo

Posicionar o cabeçalho/lockup com o nome “Pingo” abaixo da barra de status do Android, mantendo visíveis a hora do telefone e os ícones de sistema.

A captura anexada mostra o lockup “Pingo” sobreposto à hora exibida na barra de status. Ela documenta o defeito atual; não é uma referência de layout a copiar.

## Escopo

### Incluído

- Corrigir a posição vertical do cabeçalho da aplicação em relação à barra de status.
- Respeitar a área segura superior do sistema em todos os destinos principais e secundários onde o cabeçalho Pingo é exibido.
- Preservar a marca, o conteúdo das telas e a navegação existentes.

### Fora de escopo

- Redesenhar ou alterar tamanho, cores, geometria ou conteúdo do lockup Pingo.
- Alterar títulos e conteúdo das telas, a barra de navegação inferior, configurações do telefone ou comportamento de domínio.
- Alterar a cor ou o estilo da barra de status, salvo ajuste estritamente necessário para manter a legibilidade atual.

## História do usuário

### US1 — Ver a hora do telefone sem obstrução (P1)

Como pessoa usuária, quero ver a hora e os ícones da barra de status sem que o cabeçalho “Pingo” fique por cima deles, para consultar o sistema e o aplicativo com clareza.

**Teste independente**: abrir o aplicativo em um aparelho Android e confirmar que a hora e os ícones de sistema permanecem desobstruídos, com o lockup Pingo posicionado inteiramente abaixo da barra de status.

## Requisitos funcionais

- **FR-001**: O cabeçalho/lockup “Pingo” MUST começar abaixo da área ocupada pela barra de status do Android e MUST NOT sobrepor a hora, notificações ou ícones de conectividade/bateria.
- **FR-002**: O layout MUST respeitar os insets superiores fornecidos pelo sistema, inclusive em dispositivos com recortes de tela ou diferentes alturas de barra de status.
- **FR-003**: A posição correta MUST ser mantida em Mercado, Alertas e Ajustes, bem como nos destinos de navegação onde o lockup permanece visível.
- **FR-004**: A correção MUST preservar integralmente a aparência e a identidade do lockup e MUST NOT introduzir espaçamento superior duplicado que empurre ou corte o conteúdo principal.
- **FR-005**: A hora e os ícones da barra de status MUST continuar legíveis nos temas claro e escuro, sem serem encobertos por superfícies, textos ou elementos clicáveis do app.

## Critérios de aceite

- **AC-001**: Em aparelho Android, o lockup Pingo aparece abaixo da barra de status; a hora não cruza nem fica visualmente misturada com a marca.
- **AC-002**: Nenhum ícone de status fica coberto ou interceptado por um elemento interativo do app.
- **AC-003**: Mercado, Alertas e Ajustes apresentam alinhamento vertical consistente do cabeçalho, respeitando a área superior do sistema.
- **AC-004**: A correção funciona em temas claro e escuro e em telas com dimensões/insets diferentes, sem clipping do cabeçalho nem padding superior duplicado.
- **AC-005**: Os títulos, a busca e o conteúdo das telas continuam acessíveis e não são empurrados para fora da área visível por espaçamento redundante.

## Casos de borda

- Dispositivo com recorte de câmera ou barra de status mais alta: o cabeçalho permanece abaixo da área segura.
- Mudança de orientação ou redimensionamento em tela dividida: o conteúdo continua respeitando o inset atual do sistema.
- Fonte/tamanho de exibição ampliados: a marca não invade a barra de status e o primeiro conteúdo continua acessível.
- Tema claro ou escuro: a posição não muda e os elementos do sistema permanecem legíveis.
