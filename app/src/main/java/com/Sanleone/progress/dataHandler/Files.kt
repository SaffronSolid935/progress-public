package com.Sanleone.progress.dataHandler

import android.content.Context
import java.io.File

object Files {

    fun GetPath(context: Context):String{
        return context.filesDir?.absolutePath.toString()
    }

    fun Read(path: String, context: Context):String?{
        var filePath: String = path
        if (!path.startsWith("/")){
            filePath = "/" + path
        }
        filePath = GetPath(context) + filePath
        val file: File = File(filePath)
        if (!file.exists()){
            return null
        }
        return file.readText()
    }

    fun ReadBytes(path: String, context: Context):ByteArray?{
        var filePath: String = path
        if (!path.startsWith("/")){
            filePath = "/" + path
        }
        filePath = GetPath(context) + filePath
        val file: File = File(filePath)
        if (!file.exists()){
            return null
        }
        return file.readBytes()
    }

    fun Save(path: String, value: String, context: Context){
        var filePath: String = path
        if (!path.startsWith("/")){
            filePath = "/" + path
        }
        filePath = GetPath(context) + filePath
        val file = File(filePath)
        if (!file.exists()){
            file.createNewFile()
        }
        file.writeText(value)
    }

    fun SaveBytes(path: String, value: ByteArray, context: Context){
        var filePath: String = path
        if (!path.startsWith("/")){
            filePath = "/" + path
        }
        filePath = GetPath(context) + filePath
        val file = File(filePath)
        if (!file.exists()){
            file.createNewFile()
        }
        file.writeBytes(value)
    }

}