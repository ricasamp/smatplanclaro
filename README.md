# Estações Claro Smartplan W30 — aplicativo Android

Aplicativo nativo Android em Kotlin, configurado para pesquisar estações **somente pelo código** e abrir a rota **de carro** no Google Maps. Nome exibido no celular: **Estações Claro Smartplan W30**.

## Base incluída
- 4.163 registros importados da planilha fornecida.
- Base local: `app/src/main/assets/sites.json`.
- Campos disponíveis: código da estação, tipo de infraestrutura, endereço, CEP, latitude e longitude.
- A pesquisa considera exclusivamente o código da estação; o endereço aparece ao selecionar um resultado.
- O botão **IR PARA O SITE** abre o Google Maps com a rota de carro. Usa coordenadas quando disponíveis e endereço como alternativa.

## Gerar o APK online, sem instalar Android Studio
Este projeto inclui um fluxo de compilação automática em GitHub Actions. O APK ainda precisa ser compilado em um ambiente com Android SDK; este pacote não contém um APK compilado.

1. Entre em https://github.com/ e crie uma conta, se ainda não tiver.
2. Crie um repositório novo, por exemplo `estacoes-claro-smartplan-w30`.
3. Envie o conteúdo desta pasta para o repositório, incluindo a pasta oculta `.github/workflows/build-apk.yml`. Ao enviar arquivos manualmente pelo site, confirme que a estrutura das pastas foi preservada. É possível também extrair o ZIP e enviar os arquivos usando GitHub Desktop.
4. Abra a aba **Actions** do repositório e selecione **Build APK - Estações Claro Smartplan W30**.
5. Clique em **Run workflow** (ou aguarde a compilação automática após enviar os arquivos para `main` ou `master`).
6. Quando a execução ficar verde, abra a execução e baixe o artefato **Estacoes-Claro-Smartplan-W30-APK** na seção **Artifacts**.
7. Extraia o ZIP do artefato: dentro estará `app-debug.apk`. Transfira para o celular Android e abra o arquivo para instalar. Se solicitado, autorize a instalação dessa origem nas configurações do Android.

O GitHub Actions pode solicitar confirmação de termos/ativação de Actions na primeira utilização. O fluxo usa Java 17, Gradle 8.7 e Android SDK; a compilação pode falhar se o GitHub mudar políticas ou se o ambiente não conseguir baixar as dependências.

## Compilar localmente (alternativa)
1. Instale Android Studio, JDK 17 e Android SDK Platform 35.
2. Abra a pasta do projeto no Android Studio.
3. Aguarde a sincronização e selecione **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. O APK de teste será gerado em `app/build/outputs/apk/debug/app-debug.apk`.

## Testar sem instalar
Abra `preview.html` em um navegador para experimentar a pesquisa por código, consultar o endereço e abrir a rota no Google Maps.

## Observações
- O ZIP contém código-fonte, base de estações, prévia HTML e configuração para compilação automática no GitHub Actions; **não contém um APK compilado**.
- O APK `debug` é adequado para teste e instalação manual. Para publicação na Play Store, será necessário preparar assinatura de release e revisar os requisitos de publicação.
- Valide endereços e coordenadas com a equipe responsável antes de uso operacional.
