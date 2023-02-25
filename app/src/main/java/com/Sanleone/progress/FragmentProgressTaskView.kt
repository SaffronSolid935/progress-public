package com.Sanleone.progress

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.Sanleone.progress.arguments.Arguments
import com.Sanleone.progress.dataHandler.CheckType
import com.Sanleone.progress.dataHandler.Progress
import com.Sanleone.progress.dataHandler.ProgressLoader
import com.Sanleone.progress.databinding.FragmentProgressTaskViewBinding
import com.Sanleone.progress.listviewAdapter.TaskAdapter
import com.google.gson.GsonBuilder

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
    var deleteNameOnEdit: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProgressTaskViewBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressNameInput = binding.progressNameInput
        val progressTextState = binding.progressTextState
        val progressBarView = binding.progressBarView
        val args = Arguments.GetAllArguments()

        println("ARGS: " + args)

        args.forEach {
            val argumentParts = it.split(" ")

            when (argumentParts[0]){
                "create"->{

                    progressNameInput.setText(getString(R.string.defaultProgressName))
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
                    deleteNameOnEdit = true

                }
                "open"->{
                    val id: Int = argumentParts[1].toInt()

                    val progressList = ProgressLoader.LoadProgess(context!!)
                    for (i in 0 until progressList.size){
                        if (progressList[i] .id == id){
                            progress = progressList[i]
                            println("Open: " + progress.name)
                            progressIndex = i

                            progressNameInput.setText(progress.name)
                            progressTextState.setText(progress.GetProgressInt().toString() + "%")

                            break
                        }
                    }
                    deleteNameOnEdit = false
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
        Arguments.Clear()
        Arguments.AddArgument("open " + progress.id.toString())
        println("Progress=Null:" + (this::progress.isInitialized))
//        if (!this::progress.isInitialized){
//            val id: Int = argumentParts[1].toInt()
//
//            val progressList = ProgressLoader.LoadProgess(context!!)
//            for (i in 0 until progressList.size){
//                if (progressList[i] .id == id){
//                    progress = progressList[i]
//                    progressIndex = i
//
//                    progressNameInput.setText(progress.name)
//                    progressTextState.setText(progress.GetProgressInt().toString() + "%")
//
//                    break
//                }
//            }
//        }
        progressBarView.progress = progress.GetProgressInt()
        println("progress bar: " + progress.GetProgressInt().toString() + ":" + progressBarView.progress.toString())


        //val gson = GsonBuilder().setPrettyPrinting().create()
        //println("JSON: " + gson.toJson(progress))
        SetTitle(progress.name)
        //Arguments.Clear()

        progressNameInput.addTextChangedListener {
            //println("Delete Name: " + deleteNameOnEdit)
            if (deleteNameOnEdit){//.text.toString() == getString(R.string.defaultProgressName).substring(0, getString(R.string.defaultProgressName).length - 1)){
                deleteNameOnEdit = false
                progressNameInput.setText("")
            }
            UpdateProgressName()
        }

        binding.addTaskButton.setOnClickListener {
            Arguments.Clear()
            Arguments.AddArgument("create " + progressIndex)
            deleteNameOnEdit = false
            findNavController().navigate(R.id.action_fragmentProgressTaskView_to_fragmentTaskDetailsAndProperties)
        }



        val adapter = TaskAdapter(context!!,R.layout.listview_task,progress.tasks,this)

        binding.taskListView.isClickable = true
        binding.taskListView.adapter = adapter
        binding.taskListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // Kurzer Klick
            Log.d("TAG", "Kurzer Klick auf Element $position")

            val task = progress.tasks[position]

            when (task.checkType){
                CheckType.Bool->{
                    task.value = (task.value == "false").toString()
                }
                CheckType.Int->{
                    task.value = ((task.value.toInt() + 1) % (task.goal.toInt()+1)).toString()
                }
            }

            val progressBar = view.findViewById<ProgressBar>(R.id.taskBarView)
            val relativeState = view.findViewById<TextView>(R.id.taskStateView)
            val absoluteState = view.findViewById<TextView>(R.id.taskAbsoluteSateView)

            println(task.value + "/" + task.goal + "=" + task.GetProgress().toInt() + "->" + task.GetProgress().toInt()::class.simpleName + "\n" + (progressBar == null))
            progressBar.progress = task.GetProgress().toInt()
            relativeState.text = task.GetProgress().toInt().toString() + "%"
            absoluteState.text = task.value + "/" + task.goal

            binding.progressBarView.progress = progress.GetProgressInt()
            binding.progressTextState.text = progress.GetProgressInt().toString() + "%"

            progress.tasks[position] = task

            val progressList = ProgressLoader.LoadProgess(context!!)
            progressList[progressIndex] = progress

            ProgressLoader.SaveProgress(progressList,context!!)

        }

        binding.taskListView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            // Langer Klick
            Log.d("TAG", "Langer Klick auf Element $position")

            Arguments.Clear()
            Arguments.AddArgument("open " + progressIndex + " " + position)
//
            deleteNameOnEdit = false
            //println("Delete Name Test: " + deleteNameOnEdit)
            findNavController().navigate(R.id.action_fragmentProgressTaskView_to_fragmentTaskDetailsAndProperties)

            // true zurückgeben, um zu signalisieren, dass das Ereignis verarbeitet wurde
            true
        }

    }

    fun UpdateProgressName(){
        progress.name = binding.progressNameInput.text.toString()
        SetTitle(progress.name)
        val progressList = ProgressLoader.LoadProgess(context!!)
        progressList[progressIndex] = progress
        ProgressLoader.SaveProgress(progressList,context!!)
    }

    fun SetTitle(newTitle: String){
        (activity as AppCompatActivity).supportActionBar?.title = newTitle
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        println("onOptionsCreated")
        inflater.inflate(R.menu.menu_main, menu)
        GetMenuItemById(menu,R.id.action_delete)?.setOnMenuItemClickListener {
            println("onOptionsItemSelected")

            val confirmDialog = (activity as MainActivity).confirmDialog
            var title = progress.name
            if (title.length > 10){
                title = title.substring(0, 7) + "..."
            }

            confirmDialog.setTitle(getString(R.string.deleteDialogTitle) + " " + title)
                .setMessage(getString(R.string.deleteProgressDialogMessage))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.delete)){dialogInterfact, it ->
                    val progressList = ProgressLoader.LoadProgess(context!!)

                    progressList.removeAt(progressIndex)

                    ProgressLoader.SaveProgress(progressList,context!!)

                    findNavController().popBackStack()
                    SetTitle(getString(R.string.app_name))
                }
                .setNegativeButton(getString(R.string.cancel)){dialogInterfact, it->
                    dialogInterfact.cancel()
                }
                .show()


            true
        }

        GetMenuItemById(menu, R.id.action_copy)?.setOnMenuItemClickListener {
            CopyProgress()
            true
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    fun CopyProgress(){
        val newProgress = progress.Copy(context)

        val splittedName = progress.name.split(" ")
        val lastPartOfName = splittedName[splittedName.size - 1]
        var newName = ""
        if (IsInt(lastPartOfName)){
            for (i in 0 until splittedName.size- 1){
                newName += splittedName[i] + " "
            }

            newName += lastPartOfName.toInt() + 1
        }
        else{
            for (i in 0 until splittedName.size){
                newName += splittedName[i] + " "
            }

            newName += "2"
        }

        newProgress.name = newName
        progress = newProgress
        val progressList = ProgressLoader.LoadProgess(context!!)
        progressIndex = progressList.size
        progressList.add(progress)

        ProgressLoader.SaveProgress(progressList,context!!)

        SetTitle(newProgress.name)

        binding.progressNameInput.setText(newProgress.name)


        Arguments.Clear()
        Arguments.AddArgument("open " + progressIndex)
    }

    fun IsInt(num: Any):Boolean{
        try{
            num.toString().toInt()
            return true
        }
        catch (e: java.lang.Exception){
            return false
        }
    }

    fun GetMenuItemById(menu: Menu, id: Int):MenuItem?{
        for (i in 0 until menu.size()){
            val item = menu.getItem(i)
            if (item.itemId == id){
                return item
            }
        }
        return null
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        println("onOptionsItemSelected")
        when (item.itemId) {
            R.id.action_delete -> {
                // Handle delete item click here
                findNavController().navigate(R.id.action_fragmentProgressTaskView_to_fragmentProgressSelection)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    override fun onResume() {
        super.onResume()
        // Fortschrittsbalken aktualisieren
        binding.progressBarView.progress = progress.GetProgressInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}