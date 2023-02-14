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
import com.Sanleone.progress.R
import com.Sanleone.progress.dataHandler.CheckType
import com.Sanleone.progress.dataHandler.Progress
import com.Sanleone.progress.debug.Debug
import kotlin.math.pow

class ProgressAdapter(context: Context, @LayoutRes private val layoutResource: Int, private val content: List<Progress>, val fragment: FragmentProgressSelection):
    ArrayAdapter<Progress>(context, layoutResource, content) {

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

        val progressName: TextView = retView.findViewById<TextView>(R.id.progressNameView)
        val progressState: TextView = retView.findViewById<TextView>(R.id.progressSateView)
        val progressBar: ProgressBar = retView.findViewById<ProgressBar>(R.id.progressBarView)

        val progress = getItem(position)!!

        progressName.setText(progress.name)

        var count: Int = 0
        var value: Int = 0

        progress.tasks.forEach{
            when(it.checkType){
                CheckType.Int->{
                    count += it.goal.toInt()
                    value += it.value.toInt()
                }
                CheckType.Bool->{
                    count += 1
                    value += if (it.value == it.goal) 1 else 0
                }
            }
        }

        val p = (value.toFloat() / count).toInt()

        progressState.setText(p.toString() + "%")
        progressBar.progress = p.toInt()

        return retView
    }

//    fun Round(num: Float, decimals: Int):Float{
//        var integer: Int = (num * (10.0f).pow(decimals + 1)).toInt()
//        integer += 5
//        return integer.toFloat() / (10.0f).pow(decimals + 1)
//    }
}