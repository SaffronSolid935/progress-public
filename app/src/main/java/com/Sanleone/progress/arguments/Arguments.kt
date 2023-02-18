package com.Sanleone.progress.arguments

object Arguments {

    var arguments: MutableList<String> = mutableListOf()

    fun Clear(){
        arguments = mutableListOf()
    }

    fun AddArgument(argument: String){
        arguments.add(argument)
    }

    fun GetAllArguments():MutableList<String>{
        return arguments
    }
}