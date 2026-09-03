# Litera — App Android nativo (Kotlin, MVVM + Clean Architecture)

App de leitura gerado a partir do design **LiteraUX** (PDF exportado do Figma). Este README cobre o que foi implementado, como configurar o projeto e o que falta para replicar 100% do design original.

## Stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **MVVM + Clean Architecture**: `presentation` (Compose + ViewModels) → `domain` (models, use cases, interfaces de repositório) → `data` (Retrofit/Room/Firebase, implementações de repositório)
- **Hilt** para injeção de dependência
- **Retrofit + kotlinx.serialization** para a API de livros
- **Room** para a "Estante" (favoritos, lidos, progresso de leitura) — funciona offline
- **DataStore Preferences** para as categorias favoritas e flags de onboarding/quiz
- **Firebase Authentication** para login/cadastro (e-mail e senha)
- **Coil** para carregar capas de livros

## API de livros escolhida: Google Books API

Foi usada a **Google Books API** (`https://www.googleapis.com/books/v1/volumes`) porque:

- É gratuita e pública (não exige backend próprio nem aprovação de editora).
- Tem cobertura enorme de livros em português, incluindo autores brasileiros (os mesmos livros usados no mock — "O Vilarejo", "Porém Bruxa", "A Hora da Estrela" etc. — aparecem nela).
- Suporta o parâmetro `langRestrict=pt` (restringe a idioma português — a API não distingui pt-BR de pt-PT, então o app também envia `country=BR` para enviesar a disponibilidade/resultados para o Brasil) e busca por categoria (`subject:`) e autor (`inauthor:`).
- Não exige chave de API para volume baixo de requisições — mas você pode (e deveria, para produção) gerar uma chave gratuita e colocar em `local.properties` (veja abaixo), o que aumenta bastante a cota.

Se no futuro quiser uma base 100% focada em literatura brasileira, dá para trocar `GoogleBooksApiService`/`BookRepositoryImpl` por outra fonte (ex. Open Library, ou uma API de alguma distribuidora/editora brasileira) sem tocar no resto do app — a interface `BookRepository` no domínio já isola essa dependência.

## Configuração antes de rodar

### 1. Firebase (obrigatório para login funcionar)

1. Crie um projeto gratuito em https://console.firebase.google.com
2. Adicione um app Android com o pacote `com.litera.app`.
3. Baixe o `google-services.json` e coloque em `app/google-services.json`.
4. No console do Firebase, em **Authentication → Sign-in method**, ative o provedor **E-mail/senha**.
5. Pronto — o projeto detecta o `google-services.json` automaticamente (o plugin do Google Services só é aplicado se o arquivo existir, então o projeto builda normalmente mesmo antes desse passo, mas login/cadastro só funcionam depois).

### 2. Chave da Google Books API (opcional, recomendado)

1. Copie `local.properties.example` para `local.properties`.
2. Gere uma chave gratuita em https://console.cloud.google.com/apis/credentials (ative a "Books API" no projeto antes).
3. Preencha `booksApiKey=SUA_CHAVE` em `local.properties`.

Sem chave, o app funciona, só que com uma cota compartilhada mais baixa.

### 3. Abrir no Android Studio

Abra a pasta `LiteraApp` no Android Studio (Koala ou mais recente). Se o Gradle reclamar do wrapper (o `gradle-wrapper.jar` binário não foi incluído neste pacote de código para manter o download leve), deixe o Android Studio usar o Gradle 8.9 automaticamente, ou rode `gradle wrapper --gradle-version 8.9` uma vez com um Gradle instalado localmente.

## O que foi implementado (MVP)

- Onboarding (4 telas, com "Pular")
- Login, Criar conta e Esqueci minha senha (Firebase Auth)
- Quiz de preferências (categorias favoritas, mínimo 3)
- Home: banner com citação, "Continuar sua última leitura", destaques nacionais e recomendados (baseado nas categorias escolhidas)
- Explorar: busca por título/autor + grade de categorias + livros por categoria
- Detalhes do livro: capa, sinopse, avaliação, favoritar, começar/continuar leitura, "mais obras do autor"
- Minha Estante: Continue lendo (com progresso e atualização de página), Favoritos, Lidos
- Perfil: dados do usuário, estatísticas simples (lidos/lendo/favoritos), categorias favoritas, sair da conta

## O que ainda não foi implementado (ficou fora do MVP combinado)

O PDF tem 42 telas — várias delas dependem de um **backend social** que não existe (comunidade, clubes literários, posts, comentários, amigos) ou são complementares (calibração de ritmo de leitura, modo foco com cronômetro e anotações, metas de leitura com XP/conquistas). Nenhuma dessas foi construída agora para manter o escopo do MVP viável, mas a arquitetura (Clean Architecture com camadas bem separadas) foi pensada para isso ser incremental — dá pra adicionar cada uma como um novo "feature module" em `presentation/` sem mexer no resto.

## Sobre cores, fontes, ícones e ilustrações

Depois da primeira versão (baseada só no PDF exportado), ganhamos acesso de leitura ao arquivo Figma original (`LiteraUX.fig` — Style Guide) e o app foi atualizado para usar os tokens e assets **reais**, não mais aproximações:

- **Cores**: paleta exata "Daisy Bush" (roxo primário, escala 50→950, `#F4F1FF`→`#2C0174`) e "Bunker" (neutro, escala 50→950) mais as cores de alerta (Sucesso `#068932`, Erro `#9F0808`, Aviso `#CDA823`) — tudo em `core/theme/Color.kt`.
- **Tipografia**: fonte real do design, "Raleway", carregada via Downloadable Fonts do Compose (`core/theme/Type.kt` + `res/values/font_certs.xml`) — baixada em runtime pelo Google Play Services no aparelho do usuário, sem precisar empacotar arquivos de fonte no APK. Escala de tamanhos 32/24/20 (títulos) e 18/16/14/8 (corpo/labels), igual ao Figma.
- **Botões e chips**: `LiteraButtons.kt` e `CategoryChip.kt` foram redesenhados para bater com os componentes reais "Botão" (cantos 100dp, padding 24×18, cores `#5908CF`/`#F4F1FF`) e "Categoria de livro" (retângulo 16dp, `#A178FF`) do Style Guide.
- **Logo e ilustrações**: o wordmark "Litera" e as 4 ilustrações do onboarding foram exportados diretamente do Figma e estão em `res/drawable-nodpi/` (`litera_logo.png`, `onboarding_illustration_1..4.png`) — usados no ícone do app (`res/drawable/ic_launcher_foreground.xml` + mipmaps gerados para API 24/25) e no `OnboardingScreen.kt`.
- **Ícones**: o design usa a biblioteca **Phosphor Icons**. A distribuição Compose desse pacote (`br.com.devsrsouza.compose.icons:phosphor`) não está publicada no Maven Central (só `simple-icons` e `tabler-icons` estão) e o build no JitPack está quebrado no momento — então, para não arriscar o projeto inteiro não sincronizar, os ~10 ícones realmente usados no app (casa, lupa, livros, perfil, coração, setas, estrela, etc.) foram desenhados à mão como `ImageVector` em `presentation/components/icons/PhosphorIcons.kt`, seguindo o estilo "regular" (linha, 24×24, cantos arredondados) do Phosphor. Se um dia quiser trocar pela biblioteca oficial, é só apagar esse arquivo e importar `Phosphor.NomeDoIcone` no lugar de `PhosphorIcons.NomeDoIcone` — os nomes foram escolhidos para bater 1:1.
