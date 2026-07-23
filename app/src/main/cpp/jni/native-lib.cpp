// Bridge JNI entre com.nexaai.engine.LlamaEngine (Kotlin) e o motor de inferência.
//
// MODO SIMULAÇÃO: enquanto o submódulo llama.cpp real não estiver vendorado
// (ver CMakeLists.txt), este arquivo implementa um "eco tokenizado" determinístico,
// suficiente para exercitar toda a UI, o streaming e a persistência do app.
// Nenhuma chamada de rede é feita em nenhum momento (Seção 15 — privacidade).
//
// Para integrar o llama.cpp de verdade, substituir o corpo de cada função nativa
// pelas chamadas equivalentes de llama.h (llama_load_model_from_file, llama_new_context_with_model,
// llama_decode, llama_sampler_sample, etc.), mantendo as mesmas assinaturas JNI abaixo.

#include <jni.h>
#include <string>
#include <sstream>
#include <vector>
#include <android/log.h>

#define LOG_TAG "NexaAiEngine"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

namespace {

struct SimulatedModelState {
    bool loaded = false;
    int contextSize = 0;
    long ramUsageBytes = 0L;
    std::vector<std::string> pendingTokens;
    size_t cursor = 0;
};

SimulatedModelState g_state;

// Simula a tokenização/geração dividindo o prompt em palavras e devolvendo
// uma resposta simples token a token, para exercitar o streaming ponta a ponta.
std::vector<std::string> simulateResponseTokens(const std::string& prompt, int maxTokens) {
    std::vector<std::string> tokens;
    std::istringstream iss(prompt);
    std::string word;
    int count = 0;
    tokens.push_back("[modo-simulacao]");
    while (iss >> word && count < maxTokens) {
        tokens.push_back(word + " ");
        count++;
    }
    return tokens;
}

}  // namespace

extern "C" JNIEXPORT jboolean JNICALL
Java_com_nexaai_engine_LlamaEngine_nativeLoadModel(
        JNIEnv* env, jobject /* this */, jstring modelPath, jint contextSize) {
    const char* pathChars = env->GetStringUTFChars(modelPath, nullptr);
    LOGI("Carregando modelo (simulado): %s", pathChars);
    env->ReleaseStringUTFChars(modelPath, pathChars);

    g_state.loaded = true;
    g_state.contextSize = contextSize;
    g_state.ramUsageBytes = 512L * 1024 * 1024; // valor simulado de 512MB
    return JNI_TRUE;
}

extern "C" JNIEXPORT void JNICALL
Java_com_nexaai_engine_LlamaEngine_nativeUnloadModel(JNIEnv* /* env */, jobject /* this */) {
    g_state = SimulatedModelState{};
    LOGI("Modelo descarregado.");
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_nexaai_engine_LlamaEngine_nativeIsModelLoaded(JNIEnv* /* env */, jobject /* this */) {
    return g_state.loaded ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_nexaai_engine_LlamaEngine_nativeRamUsageBytes(JNIEnv* /* env */, jobject /* this */) {
    return static_cast<jlong>(g_state.ramUsageBytes);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_nexaai_engine_LlamaEngine_nativeGenerateToken(
        JNIEnv* env, jobject /* this */, jstring prompt, jint maxTokens, jboolean isFirstCall) {

    if (isFirstCall == JNI_TRUE) {
        const char* promptChars = env->GetStringUTFChars(prompt, nullptr);
        g_state.pendingTokens = simulateResponseTokens(std::string(promptChars), maxTokens);
        env->ReleaseStringUTFChars(prompt, promptChars);
        g_state.cursor = 0;
    }

    if (g_state.cursor >= g_state.pendingTokens.size()) {
        return nullptr; // sinaliza fim de geração (EOS simulado)
    }

    const std::string& token = g_state.pendingTokens[g_state.cursor];
    g_state.cursor++;
    return env->NewStringUTF(token.c_str());
}
