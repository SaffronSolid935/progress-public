package com.Sanleone.progress

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.Sanleone.progress.arguments.Arguments
import com.Sanleone.progress.dataHandler.IDGenerator
import com.Sanleone.progress.dataHandler.Progress
import com.Sanleone.progress.dataHandler.ProgressLoader
import com.Sanleone.progress.databinding.FragmentProgressSelectionBinding
import com.Sanleone.progress.listviewAdapter.ProgressAdapter
import com.Sanleone.progress.menuHandler.MenuHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FragmentProgressSelection : Fragment() {

    private var _binding: FragmentProgressSelectionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission is granted, you can now access external storage
            } else {
                // Permission is denied, you can't access external storage
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProgressSelectionBinding.inflate(inflater, container, false)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted, you can now access external storage
        } else {
            // Permission is not granted, request it
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        setHasOptionsMenu(true)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        when {
//            ContextCompat.checkSelfPermission(
//                context!!,
//                Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                // You can use the API that requires the permission.
//            }
//            else -> {
//                // You can directly ask for the permission.
//                // The registered ActivityResultCallback gets the result of this request.
//                requestPermissionLauncher.launch(
//                    Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION)
//            }
//        }

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
            SetTitle(adapter.getItem(position)!!.name)
        }
    }

    fun SetTitle(newTitle: String){
        (activity as AppCompatActivity).supportActionBar?.title = newTitle
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        println("onOptionsCreated")
        inflater.inflate(R.menu.menu_main, menu)
        MenuHandler.ShowElementsInMenu(menu, mutableListOf(R.id.action_import,R.id.action_export))

        MenuHandler.GetMenuItemById(menu,R.id.action_export)?.setOnMenuItemClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/json"
                putExtra(Intent.EXTRA_TITLE, "progressList.json")
            }

            startActivityForResult(intent, MainActivity.EXPORT_FILE_REQUEST_CODE)

            true
        }

        MenuHandler.GetMenuItemById(menu,R.id.action_import)?.setOnMenuItemClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select a file"), MainActivity.IMPORT_FILE_REQUEST_CODE)
            true
        }


        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MainActivity.EXPORT_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                println("Path: " + uri.encodedPath)
                val content = ProgressLoader.LoadProgressFile(context!!)
                context?.contentResolver?.openOutputStream(uri)?.use { outputStream ->
                    val text = content
                    outputStream.write(text.toByteArray())
                }
            }
        }
        else if (requestCode == MainActivity.IMPORT_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){

            data?.data?.also{uri ->
                val progressList = ProgressLoader.LoadProgess(context!!)
                context?.contentResolver?.openInputStream(uri)?.bufferedReader()?.use {
                    val rawText = it.readText()
                    println("Import Data:" + rawText)
                    val gson = Gson()
                    val type = object : TypeToken<MutableList<Progress>>() {}.type
                    val importList = gson.fromJson<MutableList<Progress>>(rawText,type)

                    importList.forEach {importItem ->
                        var isUnique = false

                        while (!isUnique) {
                            isUnique = true
                            progressList.forEach {
                                if (it.id == importItem.id) {
                                    isUnique = false
                                    importItem.id = IDGenerator.GenerateStringID(32)
                                }
                            }
                        }
                        progressList.add(importItem)
                    }
                }
                ProgressLoader.SaveProgress(progressList,context!!)
                val adapter = (binding.progressSelection.adapter as ProgressAdapter)

                adapter.clear()
                adapter.addAll(progressList)

                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}