package com.Sanleone.progress

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.Sanleone.progress.arguments.Arguments
import com.Sanleone.progress.dataHandler.CheckType
import com.Sanleone.progress.dataHandler.Progress
import com.Sanleone.progress.dataHandler.ProgressLoader
import com.Sanleone.progress.dataHandler.Task
import com.Sanleone.progress.databinding.FragmentTaskDetailsAndPropertiesBinding
import com.Sanleone.progress.menuHandler.MenuHandler

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FragmentTaskDetailsAndProperties : Fragment() {

    private var _binding: FragmentTaskDetailsAndPropertiesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var progress: Progress
    lateinit var task: Task
    var progressIndex: Int = 0
    var taskIndex: Int = 0
    var valueChanged: Boolean = false
    var goalChanged: Boolean = false
    var deleteNameOnEdit: Boolean = false
    var checkTypeChanged: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTaskDetailsAndPropertiesBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = Arguments.GetAllArguments()
        var create: Boolean = false

        println("ARGS: " + args)

        args.forEach {
            val argumentParts = it.split(" ")

            when (argumentParts[0]) {
                "create" -> {
                    progressIndex = argumentParts[1].toInt()

                    val progressList = ProgressLoader.LoadProgess(context!!)
                    progress = progressList[progressIndex]

                    println(progress.tasks.size)
                    taskIndex = progress.tasks.size
                    progress.tasks.add(Task(getString(R.string.defaultTaskName), "", CheckType.Bool, "false", "true"))
                    task = progress.tasks[taskIndex]
                    create = true
                }
                "open"->{
                    progressIndex = argumentParts[1].toInt()
                    taskIndex = argumentParts[2].toInt()

                    val progressList = ProgressLoader.LoadProgess(context!!)
                    progress = progressList[progressIndex]
                    task = progress.tasks[taskIndex]
                }
            }
        }
        println("Args: " + Arguments.GetAllArguments())
        Arguments.Clear()
        Arguments.AddArgument("open " + progressIndex + " " + taskIndex)

        binding.taskNameView.addTextChangedListener {
            println("DefaultTaskName use: " + (binding.taskNameView.text.toString() == getString(R.string.defaultTaskName).substring(0, getString(R.string.defaultTaskName).length - 1)))
            if (!checkTypeChanged) {
                if (deleteNameOnEdit/*binding.taskNameView.text.toString() == getString(R.string.defaultTaskName).substring(0, getString(R.string.defaultTaskName).length - 1)*/) {
                    deleteNameOnEdit = false
                    binding.taskNameView.setText("")
                    SetTitle("")
                }
                task.shortDescription = it.toString()
                SetTitle(task.shortDescription)
                //UpdateView()
                SaveTask()
            }
            else{
                checkTypeChanged = false
            }
        }

        binding.descriptionView.addTextChangedListener {
            task.longDescription = it.toString()
            SaveTask()
        }

        if (task.checkType == CheckType.Int){
            binding.typeSpinner.setSelection(1)
        }

        binding.typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                checkTypeChanged = true
                val selectedType = parent?.getItemAtPosition(position) as String
                var change: Boolean = false
                println("Selected Type: " + selectedType + ":" + position)
                when (position){
                    0->{
                        if (task.checkType == CheckType.Bool){
                            return
                        }
                        change = true
                        task.checkType = CheckType.Bool
                        task.value = "false"
                        task.goal = "true"
                    }
                    1->{
                        if (task.checkType == CheckType.Int){
                            return
                        }
                        change = true
                        task.checkType = CheckType.Int
                        task.value = "0"
                        task.goal = "2"
                    }
                }
                if (change) {
                    UpdateView()
                    SaveTask()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Hier passiert nichts, wenn nichts ausgewählt wurde
            }
        }

//        binding.stateGroup.setOnClickListener {
//            onRadioButtonClicked(it)
//            SaveTask()
//        }

        binding.radioDone.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                onRadioButtonClicked(buttonView)
                SaveTask()
            }
        }
        binding.radioNotDone.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                onRadioButtonClicked(buttonView)
                SaveTask()
            }
        }

        binding.valueNumber.addTextChangedListener {
            if (!valueChanged) {
                if (isNumber(it.toString())) {
                    task.value = it.toString()
                    if (task.value.toInt() > task.goal.toInt()) {
                        task.goal = task.value
                        binding.goalNumber.setText(task.goal)
                    }
                    SaveTask()
                } else if (it.toString().replace(" ", "") != "") {
                    binding.valueNumber.setText(task.value)
                }
            }
            valueChanged = false
        }

        binding.goalNumber.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Code ausführen, wenn der Benutzer mit dem Bearbeiten fertig ist

                if (task.goal.toInt() <= 1){
                    task.goal = "2"
                    binding.goalNumber.setText(task.goal)
                }
                SaveTask()
                true
            } else {
                false
            }
        }

        binding.goalNumber.setOnFocusChangeListener { v, hasFocus ->
            if (task.goal.toInt() <= 1){
                task.goal = "2"
                binding.goalNumber.setText(task.goal)
            }
            SaveTask()
        }

        binding.goalNumber.addTextChangedListener {
            if (!goalChanged) {
                if (isNumber(it.toString())) {
                    task.goal = it.toString()
                    if (task.value.toInt() > task.goal.toInt()) {
                        task.value = task.goal
                        binding.valueNumber.setText(task.value)
                    }
                    SaveTask()
                } else if (it.toString().replace(" ", "") != "") {
                    binding.goalNumber.setText(task.goal)
                }
            }
            goalChanged = false
        }
        view.setOnKeyListener { v, keyCode, event ->
            if( keyCode == KeyEvent.KEYCODE_BACK || true)
            {
                if (task.goal.toInt() <= 1){
                    task.goal = "2"
                    binding.goalNumber.setText(task.goal)
                }
                SaveTask()
                true
            }
            else {
                false
            }
        }

        //Arguments.Clear()

        UpdateView()
        deleteNameOnEdit = create
        SaveTask()


        

    }

    fun isNumber(number: Any):Boolean{
        try{
            number.toString().toInt()
            return true
        }
        catch (e: java.lang.Exception){
            return false
        }
        return true
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.id) {
                R.id.radioDone-> {
                    if (checked) {
                        // Aktionen ausführen, wenn Radio Button 1 ausgewählt wurde
                        task.value = "true"
                    }
                }
                R.id.radioNotDone -> {
                    if (checked) {
                        // Aktionen ausführen, wenn Radio Button 2 ausgewählt wurde
                        task.value = "false"
                    }
                }
            }
        }

        println("New Value: " + task.value)
    }


    fun UpdateView(){
        binding.taskNameView.setText(task.shortDescription)
        SetTitle(task.shortDescription)
        binding.descriptionView.setText(task.longDescription)

        when (task.checkType){
            CheckType.Bool->{
                binding.stateGroup.visibility = View.VISIBLE
                binding.numStateGroup.visibility = View.INVISIBLE
                println("Task Done:" + (task.value == task.goal))
                if (task.value != task.goal){
                    binding.stateGroup.check(R.id.radioNotDone)
                }
                else{
                    binding.stateGroup.check(R.id.radioDone)
                }
            }
            CheckType.Int->{
                binding.stateGroup.visibility = View.INVISIBLE
                binding.numStateGroup.visibility = View.VISIBLE


                //valueChanged = true
                binding.valueNumber.setText(task.value)
                //goalChanged = true
                binding.goalNumber.setText(task.goal)
            }
        }
    }

    fun SaveTask(){
        progress.tasks[taskIndex] = task
        val progressList = ProgressLoader.LoadProgess(context!!)
        progressList[progressIndex] = progress
        ProgressLoader.SaveProgress(progressList,context!!)
        println("TASK SAVED")
    }

    fun SetTitle(newTitle: String){
        (activity as AppCompatActivity).supportActionBar?.title = newTitle
    }

    //    override fun onBackPressed() {
//
//        // Führe den Code aus, den du ausführen möchtest, wenn der Benutzer die Tastatur schließt.
//        // Zum Beispiel:
//        if (task.goal.toInt() <= 1){
//            task.goal = "2"
//            binding.goalNumber.setText(task.goal)
//        }
//    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        println("onOptionsCreated")
        inflater.inflate(R.menu.menu_main, menu)
        MenuHandler.ShowElementsInMenu(menu, mutableListOf(R.id.action_delete,R.id.action_copy))
        val copyItem = GetMenuItemById(menu, R.id.action_copy)

        GetMenuItemById(menu, R.id.action_delete)?.setOnMenuItemClickListener {
            println("onOptionsItemSelected")
//            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            val confirmDialog = (activity as MainActivity).confirmDialog
            var title = task.shortDescription
            if (title.length > 10){
                title = title.substring(0, 7) + "..."
            }

            confirmDialog.setTitle(getString(R.string.deleteDialogTitle) + " " + title)
                .setMessage(getString(R.string.deleteTaskDialogMessage))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.delete)){dialogInterfact, it ->
                    val progressList = ProgressLoader.LoadProgess(context!!)

                    progressList[progressIndex].tasks.removeAt(taskIndex)

                    ProgressLoader.SaveProgress(progressList, context!!)

                    findNavController().popBackStack()
                    SetTitle(progress.name)
                }
                .setNegativeButton(getString(R.string.cancel)){dialogInterfact, it->
                    dialogInterfact.cancel()
                }
                .show()

//            val progressList = ProgressLoader.LoadProgess(context!!)
//
//            progressList[progressIndex].tasks.removeAt(taskIndex)
//
//            ProgressLoader.SaveProgress(progressList, context!!)
//
//            findNavController().popBackStack()
//            SetTitle(progress.name)
            true
        }

        GetMenuItemById(menu, R.id.action_copy)?.setOnMenuItemClickListener {
            CopyTask()
            true
        }

        super.onCreateOptionsMenu(menu, inflater)
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

    fun CopyTask(){
        val newTask = CopyTaskProperties(task)

        taskIndex = progress.tasks.size
        progress.tasks.add(newTask)
        task = newTask

        deleteNameOnEdit = false

        SetTitle(task.shortDescription)
        binding.taskNameView.setText(task.shortDescription)

        SaveTask()
        Arguments.Clear()
        Arguments.AddArgument("open " + progressIndex + " " + taskIndex)

    }

    fun CopyTaskProperties(task: Task):Task{
        val newTask = task.Copy()

        val splittedName = task.shortDescription.split(" ")
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

        newTask.shortDescription = newName

        return newTask
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

    override fun onPause() {
        super.onPause()
        if (task.checkType == CheckType.Int) {
            if (task.goal.toInt() <= 1) {
                task.goal = "2"
                binding.goalNumber.setText(task.goal)
            }
            SaveTask()
        }
        Arguments.Clear()
        Arguments.AddArgument("open " + progressIndex.toString() + " " + taskIndex.toString())
        Arguments.AddArgument("from properties")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}