/**
  * 移植 SDL 到 Android 平台的 Hello World 程序
  * SimpleSDL2Sample Android Hello World
  *
  * MUFCRyan 参考自已故的雷霄骅大神，大神一路走好
  * 本程序是移植 SDL 到 Android 平台的最简单程序，它可以读取并显示一张 BMP 图片
  */

#ifdef _ANDROID_

#include <jni.h>
#include <android/log.h>
#define LOGI(...) _android_log_print(ANDROID_LOG_INFO, "(^_^)", _VA_ARGS_)
#define LOGE(...) _android_log_print(ANDROID_LOG_ERROR, "(^_^)", _VA_ARGS)
#else
#define LOGE(format, ...)  printf("(>_<) " format "\n", ##_VA_ARGS_)
#define LOGI(format, ...)  printf("(>_<) " format "\n", ##_VA_ARGS_)
#endif

#include "SDL.h"
#include "SDL_log.h"
#include "SDL_main.h"

int main(int argc, char *argv[]){
    struct SDL_Window *window = NULL;
    struct SDL_Renderer *render = NULL;
    struct SDL_Surface *bmp = NULL;
    struct SDL_Texture *texture = NULL;

    char *filepath = "test.bmp";

    if(SDL_Init(SDL_INIT_VIDEO | SDL_INIT_AUDIO | SDL_INIT_TIMER) == -1){
        LOGE("SDL_Init failed %s", SDL_GetError());
    }

    window = SDL_CreateWindow("SDL HelloWorld!", 100, 100, 640, 480, SDL_WINDOW_SHOWN);
    if(window == NULL){
        LOGE("SDL_CreateWindow failed %s", SDL_GetError());
    }

    render = SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED | SDL_RENDERER_PRESENTVSYNC);
    if(render == NULL){
        LOGE("SDL_CreateRenderer failed %s", SDL_GetError());
    }

    bmp = SDL_LoadBMP(filepath);
    if(bmp == NULL){
        LOGE("SDL_LoadBMP failed %s", SDL_GetError());
    }

    texture = SDL_CreateTextureFromSurface(render, bmp);
    SDL_FreeSurface(bmp);

    SDL_RenderClear(render);
    SDL_RenderCopy(render, texture, NULL, NULL);
    SDL_RenderPresent(render);

    SDL_Delay(10000);

    SDL_DestroyTexture(texture);
    SDL_DestroyRenderer(render);
    SDL_DestroyWindow(window);

    // Quit SDL
    SDL_Quit();
    return 0;
}