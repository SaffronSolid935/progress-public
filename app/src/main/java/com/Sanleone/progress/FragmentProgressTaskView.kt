package com.Sanleone.progress

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.Sanleone.progress.arguments.Arguments
import com.Sanleone.progress.dataHandler.Progress
import com.Sanleone.progress.dataHandler.ProgressLoader
import com.Sanleone.progress.databinding.FragmentProgressTaskViewBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FragmentProgressTaskView : Fragment() {

    private var _binding: FragmentProgressTaskViewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var progress: Progress
    var progressIndex: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProgressTaskViewBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressNameInput = binding.progressNameInput
        val progressTextState = binding.progressTextState
        val progressBarView = binding.progressBarView
        val args = Arguments.GetAllArguments()

        args.forEach {
            val argumentParts = it.split(" ")

            when (argumentParts[0]){
                "create"->{

                    progressNameInput.setText("Unnamed progress")
                    progressTextState.setText("0%")
                    progressBarView.progress = 0
//                    progressBarView.max = 100
//                    progressBarView.isIndeterminate = false
//                    progressBarView.visibility = View.VISIBLE
                    val progressList = ProgressLoader.LoadProgess(context!!)

                    //id wird berechnet

                    var newId = 0

                    progressList.forEach {
                        if (it.id >= newId){
                            newId = it.id + 1
                        }
                    }

                    // progress wird erstellt und gespeichert
                    progress = Progress(newId,progressNameInput.text.toString(), mutableListOf())
                    progressList.add(progress)
                    progressIndex = progressList.size - 1

                    ProgressLoader.SaveProgress(progressList,context!!)
                }
                "open"->{
                    val id: Int = argumentParts[1].toInt()

                    val progressList = ProgressLoader.LoadProgess(context!!)
                    for (i in 0 until progressList.size){
                        if (progressList[i] .id == id){
                            progress = progressList[i]
                            (activity as AppCompatActivity).supportActionBar?.title = progress.name
                            progressIndex = i

                            progressNameInput.setText(progress.name)
                            progressTextState.setText(progress.GetProgressFloat().toString() + "%")
                            progressBarView.progress = progress.GetProgressInt()

                            break
                        }
                    }
                }
            }
//            if (argumentParts.size == 1){
//                when (argumentParts[0]){
//                    "create"->{
//
//                    }
//                }
//            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}