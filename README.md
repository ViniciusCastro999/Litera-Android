# Litera

App Android nativo de leitura — Kotlin + Jetpack Compose, MVVM + Clean Architecture.

Design original **LiteraUX**, criado por **Milla Giulie** no Figma:
📎 https://www.figma.com/design/HjDOMSiT5S17DZKR77b8SY/LiteraUX.fig

## Stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **MVVM + Clean Architecture**: `presentation` (Compose + ViewModels) → `domain` (models, use cases, interfaces de repositório) → `data` (Retrofit/Room/Firebase, implementações de repositório)
- **Hilt** para injeção de dependência
- **Retrofit + kotlinx.serialization** para a Google Books API e para a Wikipedia REST API (biografia de autor)
- **Room** para tudo que é dado local: Estante, sessões de foco, metas de leitura, anotações e a Comunidade (posts/comentários/clubes)
- **DataStore Preferences** para categorias favoritas, flags de onboarding/quiz e configurações do Modo foco
- **Firebase Authentication** para login/cadastro (e-mail e senha)
- **Coil** para carregar capas de livros, com cache em disco e memória configurado (`LiteraApplication`) — cada capa só é baixada uma vez

## Configuração antes de rodar

### 1. Firebase (obrigatório para login funcionar)

1. Crie um projeto gratuito em https://console.firebase.google.com
2. Adicione um app Android com o pacote `com.litera.app`.
3. Baixe o `google-services.json` e coloque em `app/google-services.json`.
4. No console do Firebase, em **Authentication → Sign-in method**, ative o provedor **E-mail/senha**.
5. Sem esse arquivo o projeto builda normalmente (o plugin do Google Services só é aplicado se ele existir), mas o app quebra ao abrir — a checagem de sessão de login roda logo na tela inicial.

### 2. Chave da Google Books API (opcional, recomendado)

1. Copie `local.properties.example` para `local.properties`.
2. Gere uma chave gratuita em https://console.cloud.google.com/apis/credentials (ative a "Books API" no projeto antes).
3. Preencha `booksApiKey=SUA_CHAVE` em `local.properties`.

Sem chave, o app funciona, só que com uma cota compartilhada mais baixa (pode retornar erro 503 em uso intenso).

### 3. Abrir no Android Studio

Abra a pasta `LiteraApp` no Android Studio (Koala ou mais recente) e deixe o Gradle sincronizar — o wrapper (`gradle-wrapper.jar`, Gradle 8.9) já está incluído no repositório.

## O que foi implementado

### Fluxo principal
- **Onboarding** — 4 telas com ilustrações e cópia exatas do Figma, "Pular"
- **Login, Criar conta e Esqueci minha senha** — Firebase Auth, campos em pill preenchido, toggle mostrar/ocultar senha
- **Quiz de preferências** — categorias favoritas, mínimo 3
- **Home** — banner com citação, "Continuar sua última leitura", destaques nacionais e recomendados (baseado nas categorias escolhidas)
- **Explorar** — busca por título/autor, sugestões para você, grade de categorias, resultados de busca
- **Detalhes do livro** — capa, sinopse, avaliação, favoritar, começar/continuar leitura, atalho para Modo foco, atalho para Anotações, autor clicável (leva para "Sobre o autor"), "mais obras do autor"
- **Minha Estante** — Continue lendo (com progresso e atualização de página), Favoritos, Lidos
- **Perfil** — cabeçalho com avatar/stats (lidos, lendo, favoritos), card de meta de leitura em destaque, categorias favoritas, atalhos, sair da conta

### Ferramentas de leitura (novo, 100% local — Room/DataStore)
- **Ritmo de leitura** — cronômetro de 1 minuto → informa página inicial/final → calcula páginas/hora e estimativa de dias para terminar o livro
- **Modo foco** (Pomodoro) — tela de intro, sessão com cronômetro regressivo e pausa, configurações (duração, notificações, estatísticas de tempo total/XP)
- **Metas de leitura** — criar/editar metas (páginas por semana, livros por mês, livros nacionais) com acompanhamento de progresso
- **Anotações** — texto livre + tags por livro (os botões "Escanear texto"/"Capturar página" aparecem desabilitados — OCR ficou fora de escopo)
- **Progresso de leitura** — painel com % de páginas lidas, conquistas calculadas a partir de dados reais (Estante/Foco/Metas) e histórico de sessões de foco
- **Sobre o autor** — biografia buscada na Wikipedia (com fallback gracioso quando não encontra) + grade de outras obras

### Comunidade (novo — local ao dispositivo)
- Feed "Amigos" (publicar, curtir, comentar, compartilhar), "Clubes literários" (entrar/sair), compositor de post com tags

> ⚠️ **Importante**: a Comunidade roda inteiramente em Room, no aparelho — não existe backend/Firestore por trás. Os posts de exemplo são semeados na primeira instalação, curtidas/comentários feitos pelo usuário ficam só naquele aparelho, e não há sincronização real entre pessoas diferentes. Para virar uma rede social de verdade, precisa entrar um backend (Firestore ou similar).

## Design system

Todo o visual foi conferido direto contra o arquivo Figma (não aproximado) via MCP do Figma:

- **Cores**: paleta exata "Daisy Bush" (roxo primário, escala 50→950) e "Bunker" (neutro, escala 50→950), mais as cores de alerta (Sucesso `#068932`, Erro `#9F0808`) — `core/theme/Color.kt`.
- **Tipografia**: "Raleway", carregada via Downloadable Fonts do Compose (`core/theme/Type.kt` + `res/values/font_certs.xml`), baixada em runtime pelo Google Play Services no aparelho do usuário — sem empacotar arquivos de fonte no APK.
- **Ícones**: os ícones do Phosphor Icons usados no app (~25) foram exportados diretamente do Figma como paths SVG e convertidos em `ImageVector` via `PathParser` em `presentation/components/icons/PhosphorIcons.kt` — não são aproximações desenhadas à mão, são os glifos reais do design.
- **Ilustrações do onboarding**: exportadas em alta resolução do Figma, recortadas para a bounding box real (sem a margem transparente sobrando) e ficam em `res/drawable-nodpi/onboarding_illustration_1..4.png`.
- **Componentes**: `LiteraButtons.kt`, `LiteraTextField.kt` (campo em pill preenchido) e `CategoryChip.kt` seguem os componentes reais "Botão", "Campo" e "Categoria de livro" do Style Guide.

## Limitações conhecidas / o que falta para produção

- **Dados do usuário ficam só no aparelho.** Fora a conta de login (Firebase Auth), tudo — Estante, Metas, Anotações, histórico de Foco, Comunidade — é local (Room/DataStore). Desinstalar o app ou trocar de aparelho perde tudo.
- **Sem backend social real** (ver aviso da Comunidade acima).
- **Nunca testamos um build de release.** O `buildType release` tem `isMinifyEnabled = true` (R8/ProGuard); as regras em `proguard-rules.pro` não foram validadas contra um build real — isso pode quebrar Room/Hilt/Retrofit/serialization em release mesmo com o debug funcionando.
- **Sem assinatura de release configurada** (keystore de upload), sem política de privacidade, sem preenchimento do formulário "Data safety" do Play Console — tudo isso é exigido para publicar.
- **Ícone do launcher** é só o wordmark "Litera" simples, não um ícone adaptativo trabalhado.
- **Sem testes automatizados** (unitários ou instrumentados) e sem Crashlytics/analytics configurado.
