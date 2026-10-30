# Quickstart: validar cabeçalho abaixo da barra de status

## Pré-requisitos

- Android Studio e Android SDK configurados para o projeto.
- Aparelho Android conectado por depuração USB ou emulador iniciado.
- Build atualizado do Pingo instalado.

## Testes automatizados

No PowerShell:

```powershell
Set-Location C:\Users\r_pai\OneDrive\Desktop\workspace\Pingo\pingo\android
.\gradlew.bat assembleDebug testDebugUnitTest
.\gradlew.bat connectedDebugAndroidTest
```

## Validação no aparelho

1. Registrar modelo do dispositivo, versão Android, orientação e escala de exibição.
2. Abrir Pingo e verificar a barra de status durante a exibição de Mercado.
3. Confirmar que o lockup começa abaixo da hora; verificar também ícones de notificação, conectividade e bateria.
4. Abrir Alertas e Ajustes e confirmar a mesma posição do lockup.
5. Repetir em tema claro e escuro.
6. Se disponível, repetir em aparelho/emulador com recorte de câmera ou inset superior diferente.
7. Rotacionar o aparelho e confirmar que o espaçamento acompanha o sistema e não usa altura fixa.
8. Aumentar tamanho da fonte/exibição nas configurações de Android e confirmar que a marca não invade a barra de status nem corta o título, a busca ou os controles.
9. Verificar que nenhum elemento interativo do app cobre ou intercepta o toque na área da barra de status.

## Resultado esperado

Em todos os destinos, hora e ícones do Android permanecem visíveis na barra de status; o lockup Pingo fica inteiramente abaixo dela, sem mudança visual na marca nem espaçamento redundante no conteúdo.

Como evidência opcional, guardar capturas antes/depois em claro e escuro e registrar dispositivo, API, orientação e escala de exibição.
