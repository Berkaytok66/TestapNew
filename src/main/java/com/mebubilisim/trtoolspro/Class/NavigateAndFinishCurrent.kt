package com.mebubilisim.trtoolspro.Class

import android.app.Activity
import android.content.Context
import android.content.Intent

fun navigateAndFinishCurrent(context: Context, targetActivityClass: Class<*>,bool : Boolean = true) {
    val intent = Intent(context, targetActivityClass)
    context.startActivity(intent)
    if (bool){
        (context as? Activity)?.finish()
    }
}