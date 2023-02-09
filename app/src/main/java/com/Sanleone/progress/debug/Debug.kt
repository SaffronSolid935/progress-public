package com.Sanleone.progress.debug

import android.util.Log

object Debug {


    fun Log(message: Any){
        //RawLog( "Log: " + message,Log)
        Log.println(Log.DEBUG,"Debug",message.toString())
    }

    fun Warning(message: Any){
        //RawLog("Warning: " + message,yellow)
        Log.println(Log.WARN,"Warning",message.toString())
    }

    fun Error(message: Any){
        //RawLog("Error: " + message, red)
        Log.println(Log.ERROR,"Error",message.toString())
    }
}