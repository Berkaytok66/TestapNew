package com.mebubilisim.trtoolspro.Class

import ServerDataRepository
import SocketServer
import android.app.Activity
import android.content.Context
import android.content.Intent


fun iptalAndNextClass(
    context: Context,
    italActivityClass: Class<*>,
    NextIntent:Class<*>,
    string: String
    ) {
    val iptalIntent = Intent(context, italActivityClass)
    val NextIntent = Intent(context, NextIntent)

    when(ServerDataRepository.pageController.value){
        2 -> {
            context.startActivity(NextIntent)
            (context as? Activity)?.finish()
            ServerDataRepository.resetPageController()
            SocketServer.sendMessageToAllClients(success = false, statusReason = "Skipped by Admin", progress = 100, functionName = string)
        }
        3 -> {
            context.startActivity(iptalIntent)
            (context as? Activity)?.finish()
            ServerDataRepository.resetPageController()
            SocketServer.sendMessageToAllClients(success = false, statusReason = "Canceled by admin", progress = 100,functionName = string)

        }
    }
}