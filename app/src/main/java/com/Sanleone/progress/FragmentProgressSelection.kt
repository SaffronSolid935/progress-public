package com.Sanleone.progress

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.Sanleone.progress.arguments.Arguments
import com.Sanleone.progress.dataHandler.ProgressLoader
import com.Sanleone.progress.databinding.FragmentProgressSelectionBinding
import com.Sanleone.progress.listviewAdapter.ProgressAdapter

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FragmentProgressSelection : Fragment() {

    private var _binding: FragmentProgressSelectionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProgressSelectionBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SetTitle(getString(R.string.app_name))

        binding.addProgressButton.setOnClickListener {
            Arguments.Clear()
            Arguments.AddArgument("create")
            findNavController().navigate(R.id.action_fragmentProgressSelection_to_fragmentProgressTaskView)
        }

        val adapter = ProgressAdapter(context!!,R.layout.listview_progress,ProgressLoader.LoadProgess(context!!),this)
        binding.progressSelection.isClickable = true
        binding.progressSelection.adapter = adapter

        binding.progressSelection.setOnItemClickListener { parent, view, position, id ->
            Arguments.Clear()
            Arguments.AddArgument("open " + adapter.getItem(position)!!.id)
            findNavController().navigate(R.id.action_fragmentProgressSelection_to_fragmentProgressTaskView)
            (activity as AppCompatActivity).supportActionBar?.title = adapter.getItem(position)!!.name
        }
    }

    fun SetTitle(newTitle: String){
        (activity as AppCompatActivity).supportActionBar?.title = newTitle
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}