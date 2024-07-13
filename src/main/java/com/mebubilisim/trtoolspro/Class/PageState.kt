package com.mebubilisim.trtoolspro.Class

import ServerDataRepository
import android.content.Context

fun PageState(context: Context,int1: Int,int2:Int, nextActbitiy: Class<*>,mainActvt: Class<*>){

    when(ServerDataRepository.pageController.value){
        int1 -> {
            navigateAndFinishCurrent(context, nextActbitiy)
            ServerDataRepository.pageController

        }
        int2 -> {
            navigateAndFinishCurrent(context, mainActvt)
            ServerDataRepository.pageController
        }
    }
}