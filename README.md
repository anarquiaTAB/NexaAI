# NexaAI

Cliente Android de LLM local (on-device), baseado em `llama.cpp`, seguindo a
especificação em `NexaAI_MasterPrompt_v1.md`.

## Estado deste projeto

Este pacote contém a **arquitetura completa** do app (Presentation, Domain, Data, DI)
e a **ponte JNI** pronta, mas o motor de inferência real (`llama.cpp`) ainda **não está
vendorado** — o app roda em **modo simulação** (eco tokenizado) até você integrar o
submódulo de verdade. Isso permite compilar, instalar e navegar por todas as telas
imediatamente.

## Pré-requisitos

- Android Studio (Ladybug ou mais recente)
- JDK 17
- Android SDK (API 34) + NDK 26+ + CMake 3.22+ instalados via SDK Manager
- (Opcional, para IA real) `git` para clonar o submódulo `llama.cpp`

## Passo a passo para abrir e compilar

1. Extraia o `.zip` deste projeto em uma pasta local.
2. Abra o Android Studio → **Open** → selecione a pasta `NexaAI/` (a que contém `settings.gradle.kts`).
3. Deixe o Gradle sincronizar (primeira vez pode demorar — baixa dependências).
4. Se pedir para instalar NDK/CMake, aceite pelo SDK Manager.
5. Conecte um celular Android (ou use um emulador) e clique em **Run ▶**.

O app deve abrir na tela de conversas. Toque em **+** para criar uma conversa e testar
o chat em modo simulação (a IA "ecoa" o que você digita, token a token, para provar que
o streaming e a persistência funcionam).

## Como integrar o llama.cpp de verdade

```bash
cd NexaAI/app/src/main/cpp
git submodule add https://github.com/ggerganov/llama.cpp llama.cpp
```

Depois:

1. Em `app/src/main/cpp/CMakeLists.txt`, descomente `add_subdirectory(llama.cpp)` e a
   linha `# llama` em `target_link_libraries`.
2. Em `app/src/main/cpp/jni/native-lib.cpp`, substitua o corpo das funções
   `nativeLoadModel`, `nativeGenerateToken`, etc. pelas chamadas reais da API do
   llama.cpp (`llama_load_model_from_file`, `llama_decode`, `llama_sampler_sample`...).
3. Sincronize o Gradle novamente e rode.

## Gerando o APK (linha de comando, alternativa ao Android Studio)

```bash
cd NexaAI
./gradlew assembleDebug        # gera app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease      # gera o APK de release (precisa configurar assinatura)
```

> Nota: o wrapper do Gradle (`gradlew`, `gradle-wrapper.jar`, `gradle-wrapper.properties`)
> não está incluído neste pacote — o Android Studio o gera automaticamente na primeira
> sincronização. Se for usar só linha de comando sem abrir o Android Studio antes, rode
> `gradle wrapper` uma vez (requer o Gradle instalado globalmente).

## O que falta (próxima leva, seguindo a Seção 13 do master prompt)

- OCR (ML Kit), STT/TTS, exportação/importação de conversas, backup criptografado,
  busca full-text, widget, migrações de schema, testes automatizados, e a
  recuperação cross-conversa por embeddings (Seção 10.5).
