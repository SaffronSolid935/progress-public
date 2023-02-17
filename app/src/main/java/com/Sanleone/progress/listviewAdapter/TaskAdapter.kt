package com.Sanleone.progress.listviewAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.Sanleone.progress.FragmentProgressSelection
import com.Sanleone.progress.FragmentProgressTaskView
import com.Sanleone.progress.R
import com.Sanleone.progress.dataHandler.CheckType
import com.Sanleone.progress.dataHandler.Progress
import com.Sanleone.progress.dataHandler.Task
import com.Sanleone.progress.debug.Debug
import kotlin.math.pow

class TaskAdapter(context: Context, @LayoutRes private val layoutResource: Int, private val content: List<Task>, val fragment: FragmentProgressTaskView):
    ArrayAdapter<Task>(context, layoutResource, content) {

    var currentIndex: Int = 0

    public fun SetCurrentIndex(index: Int){
        this.currentIndex = index
    }
    public fun GetCurrentIndex():Int{
        return currentIndex
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        Debug.Log("Test: getView")
        val retView: View

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            retView = inflater.inflate(layoutResource, parent, false)


        } else {
            retView = convertView
        }

        val taskName: TextView = retView.findViewById<TextView>(R.id.taskNameView)
        val taskStateAbsolute: TextView = retView.findViewById<TextView>(R.id.taskAbsoluteSateView)
        val taskStateRelative: TextView = retView.findViewById<TextView>(R.id.taskStateView)
        val taskBar: ProgressBar = retView.findViewById<ProgressBar>(R.id.taskBarView)

        val taskProgress = getItem(position)!!

        taskName.setText(taskProgress.shortDescription)

        taskStateAbsolute.setText(taskProgress.value + "/" + taskProgress.goal)
        taskStateRelative.setText(taskProgress.GetProgress().toInt().toString() + "%" )
        taskBar.progress = taskProgress.GetProgress().toInt()

        return retView
    }

//    fun Round(num: Float, decimals: Int):Float{
//        var integer: Int = (num * (10.0f).pow(decimals + 1)).toInt()
//        integer += 5
//        return integer.toFloat() / (10.0f).pow(decimals + 1)
//    }
}