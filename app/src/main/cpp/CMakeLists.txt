cmake_minimum_required(VERSION 3.22.1)
project(nexaai_engine)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# ---------------------------------------------------------------------------
# Integração com llama.cpp (Seção 3 / Seção 7 do master prompt)
#
# O submódulo real ainda não está clonado neste projeto. Quando estiver
# (git submodule add https://github.com/ggerganov/llama.cpp app/src/main/cpp/llama.cpp),
# descomente as linhas abaixo para linkar a biblioteca de verdade.
# ---------------------------------------------------------------------------
# add_subdirectory(llama.cpp)

add_library(
    nexaai_engine
    SHARED
    jni/native-lib.cpp
)

# Quando o llama.cpp estiver integrado, adicionar `llama` (ou `common`) aqui:
target_link_libraries(
    nexaai_engine
    android
    log
    # llama
)

target_include_directories(
    nexaai_engine
    PRIVATE
    jni
    # llama.cpp/include
)
