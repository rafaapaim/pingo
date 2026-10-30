# Quickstart: validar navegação após alterar Ajustes

## Pré-requisitos

- Android Studio e Android SDK configurados para o projeto.
- Aparelho Android conectado com depuração USB ou emulador.
- Build instalado a partir da versão atualizada do Pingo.

## Testes automatizados

No PowerShell:

```powershell
Set-Location C:\Users\r_pai\OneDrive\Desktop\workspace\Pingo\pingo\android
.\gradlew.bat assembleDebug testDebugUnitTest
.\gradlew.bat connectedDebugAndroidTest
```

## Validação em aparelho

1. Abrir o app e confirmar que Mercado está selecionado na barra inferior.
2. Tocar em **Ajustes** pela navegação do app.
3. Alterar o tema para **Claro** ou **Escuro** e aguardar a atualização visual.
4. Sem pressionar o botão Voltar do Android, tocar em **Mercado** na barra inferior.
5. Confirmar que Mercado aparece e fica selecionado; verificar que o tema escolhido continua aplicado.
6. Tocar novamente em **Ajustes** e confirmar que a opção de tema previamente escolhida continua selecionada.
7. Repetir alternância entre Mercado, Alertas e Ajustes várias vezes; confirmar que a barra permanece visível e que o destino selecionado corresponde à tela exibida.
8. Repetir com outra opção de Ajustes que não exija conceder uma permissão. Para uma ação que abra configurações do Android, retornar pelo próprio Android e então confirmar que Mercado pode ser selecionado pela barra do Pingo.
9. Fazer uma passada em tema claro e outra em tema escuro.

## Resultado esperado

Após alterar qualquer configuração em Ajustes, tocar em Mercado na navegação inferior troca diretamente para Mercado. A alteração permanece aplicada e o botão Voltar do Android não é necessário para navegar entre as abas principais.
