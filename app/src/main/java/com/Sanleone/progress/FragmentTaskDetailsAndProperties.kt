package com.Sanleone.progress

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.Sanleone.progress.arguments.Arguments
import com.Sanleone.progress.dataHandler.CheckType
import com.Sanleone.progress.dataHandler.Progress
import com.Sanleone.progress.dataHandler.ProgressLoader
import com.Sanleone.progress.dataHandler.Task
import com.Sanleone.progress.databinding.FragmentTaskDetailsAndPropertiesBinding

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
                    progress.tasks.add(Task("Unnamed-Task", "", CheckType.Bool, "false", "true"))
                    task = progress.tasks[taskIndex]
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

        binding.taskNameView.addTextChangedListener {
            task.shortDescription = it.toString()
            SetTitle(task.shortDescription)
            //UpdateView()
            SaveTask()
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

        Arguments.Clear()

        UpdateView()
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
        menu.getItem(0).setOnMenuItemClickListener {
            println("onOptionsItemSelected")

            val progressList = ProgressLoader.LoadProgess(context!!)

            progressList[progressIndex].tasks.removeAt(taskIndex)

            ProgressLoader.SaveProgress(progressList,context!!)

            findNavController().popBackStack()
            SetTitle(progress.name)
            true
        }
        super.onCreateOptionsMenu(menu, inflater)
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
        Arguments.AddArgument("open " + progressIndex.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}