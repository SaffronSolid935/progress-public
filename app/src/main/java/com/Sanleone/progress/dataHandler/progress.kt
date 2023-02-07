package com.Sanleone.progress.dataHandler

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.math.pow

object ProgressLoader{

    val progressFile = "progress.json"

    fun LoadProgess(context: Context):MutableList<Progress>{
        val fileContent = Files.Read(progressFile,context)
        val progress: MutableList<Progress>
        val gson = Gson()
        if (fileContent != null) {
            val type = object : TypeToken<MutableList<Progress>>() {}.type
            progress = gson.fromJson(fileContent,type)
        }
        else{
            progress = mutableListOf()

            Files.Save(progressFile, gson.toJson(progress),context)
        }
        return progress
    }

    fun SaveProgress(progress: MutableList<Progress>, context: Context){
        val gson = Gson()
        Files.Save(progressFile, gson.toJson(progress), context)
    }

}

data class Progress(
    var id: Int,
    var name: String,
    var tasks: MutableList<Task>
){

    fun AddTask(shortDescription: String,longDescription: String, checkType: CheckType, value: String, goal: Any):TaskStatus{
        val taskStatus: TaskStatus = TaskStatus()

        try {
            if (shortDescription == "" || shortDescription == "null") {
                taskStatus.status = TaskStatusCode.NoShortDescription
                taskStatus.description =
                    "Do not set the variable shortDescription to \"\" or \"null\"."
                return taskStatus
            }

            when (checkType) {
                CheckType.Bool -> {
                    if (goal != "true" && goal != "false") {
                        taskStatus.status = TaskStatusCode.GoalIsNotValid
                        taskStatus.description = "The value \"${goal}\" is not true or false."
                        return taskStatus
                    }
                }
                CheckType.Int -> {
                    if (!IsInteger(goal)) {
                        taskStatus.status = TaskStatusCode.GoalIsNotValid
                        taskStatus.description = "The value \"${goal} is not an integer."
                        return taskStatus
                    }
                    if (!IsInteger(value)) {
                        taskStatus.status = TaskStatusCode.ValueIsNotValid
                        taskStatus.description = "The value \"${value} is not an integer."
                        return taskStatus
                    }

                    if (value.toInt() > goal.toString().toInt()) {
                        taskStatus.status = TaskStatusCode.ValueCanNotBeHigherThenTheGoal
                        taskStatus.description =
                            "That the value ${value} is greater than ${goal} is not allowed."
                        return taskStatus
                    }
                }
            }

            try {
                tasks.add(
                    Task(
                        shortDescription,
                        longDescription,
                        checkType,
                        value,
                        goal.toString()
                    )
                )
            } catch (e: java.lang.Exception) {
                taskStatus.status = TaskStatusCode.AddEditTaskError
                taskStatus.description = "Error on adding Task: $e"
                taskStatus.exception = e
                return taskStatus
            }
        }
        catch (e:java.lang.Exception){
            taskStatus.description = "Unknown error: $e"
            taskStatus.exception = e
            return taskStatus
        }

        taskStatus.status = TaskStatusCode.Succsess
        taskStatus.description = "Adding task was successful!"
        taskStatus.task = tasks[tasks.size - 1]

        return taskStatus
    }

    fun EditTask(index: Int, shortDescription: String,longDescription: String, checkType: CheckType, value: String, goal: Any):TaskStatus{
        val taskStatus = TaskStatus()

        try {

            when (checkType) {
                CheckType.Bool -> {
                    if (goal != "true" && goal != "false") {
                        taskStatus.status = TaskStatusCode.GoalIsNotValid
                        taskStatus.description = "The value \"${goal}\" is not true or false."
                        return taskStatus
                    }
                }
                CheckType.Int -> {
                    if (!IsInteger(goal)) {
                        taskStatus.status = TaskStatusCode.GoalIsNotValid
                        taskStatus.description = "The value \"${goal} is not an integer."
                        return taskStatus
                    }
                    if (!IsInteger(value)) {
                        taskStatus.status = TaskStatusCode.ValueIsNotValid
                        taskStatus.description = "The value \"${value} is not an integer."
                        return taskStatus
                    }

                    if (value.toInt() > goal.toString().toInt()) {
                        taskStatus.status = TaskStatusCode.ValueCanNotBeHigherThenTheGoal
                        taskStatus.description =
                            "That the value ${value} is greater than ${goal} is not allowed."
                        return taskStatus
                    }
                }
            }

            try {
                val task = tasks[index]//.shortDescription = shortDescription
                task.shortDescription = shortDescription
                task.longDescription = longDescription
                task.checkType = checkType
                task.value = value
                task.goal = goal.toString()
                tasks[index] = task
            } catch (e: java.lang.Exception) {
                taskStatus.status = TaskStatusCode.AddEditTaskError
                taskStatus.description = "Error on editing Task: $e"
                taskStatus.exception = e
                return taskStatus
            }
        }
        catch(e: java.lang.Exception){
            taskStatus.description = "Unknown error: $e"
            taskStatus.exception = e
            return taskStatus
        }

        taskStatus.status = TaskStatusCode.Succsess
        taskStatus.description = "Editing task was successful!"
        taskStatus.task = tasks[tasks.size - 1]

        return taskStatus

    }

    fun IsInteger(num: Any):Boolean{
        return num.toString().all { char -> char.isDigit() }
    }

}

sealed class TaskValue{
    data class Bool(val value: Boolean):TaskValue()
    data class INT(val value: Int):TaskValue()
    data class None(val value: Any? = null):TaskValue()
}


data class Task(
    var shortDescription: String,
    var longDescription: String,
    var checkType: CheckType,
    var value: String,
    var goal: String
){

    fun GetValue():TaskValue{
        when (checkType){
            CheckType.Bool->{
                return TaskValue.Bool(value != "false")
            }
            CheckType.Int->{
                return TaskValue.INT(value.toInt())
            }
            else->{
                return TaskValue.None()
            }
        }
    }

    fun GetProgress():Float{
        when (checkType){
            CheckType.Bool->{
                return if ((value != "false") == (goal != "false")) 1f else 0f
            }
            CheckType.Int->{
                return Round(value.toFloat() / goal.toFloat(),1)
            }
        }
        return 0f
    }

    fun Round(num: Float, decimalPlaces: Int):Float{
        var integerWithDecimalPlaces: Int = (num * (10.0).pow(decimalPlaces)).toInt()
        if ((num * (10.0).pow(decimalPlaces + 1)).toInt() - integerWithDecimalPlaces * 10 >= 5){
            integerWithDecimalPlaces /= 10
            integerWithDecimalPlaces += 1
        }
        return (integerWithDecimalPlaces.toFloat() / (10.0).pow(decimalPlaces)).toFloat()
    }
}

enum class CheckType{
    Bool,
    Int
}

data class TaskStatus(
    var status: TaskStatusCode = TaskStatusCode.Unknown,
    var description: String  = "",
    var exception: java.lang.Exception? = null,
    var task: Task? = null
){}

enum class TaskStatusCode{
    Unknown,
    Succsess,
    ValueIsNotValid,
    GoalIsNotValid,
    ValueCanNotBeHigherThenTheGoal,
    NoShortDescription,
    AddEditTaskError
}