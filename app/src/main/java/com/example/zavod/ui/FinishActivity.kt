package com.example.zavod.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.zavod.R
import com.example.zavod.model.RepairRequest
import com.example.zavod.model.RepairType
import com.example.zavod.model.RepairTypesResponse
import com.example.zavod.repository.RepairRepository
import com.example.zavod.viewmodel.FinishViewModel
import com.example.zavod.viewmodel.FinishViewModelFactory

class FinishActivity : AppCompatActivity() {

    private var equipmentId: Int = 0
    private lateinit var repairBtn: Button
    private var repairDialog: AlertDialog? = null
    private lateinit var viewModel: FinishViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        val resultText: TextView = findViewById(R.id.resultText)
        repairBtn = findViewById(R.id.repairBtn)
        val homeBtn: Button = findViewById(R.id.homeBtn)

        val hasErrors = intent.getBooleanExtra("hasErrors", false)
        val errorSteps = intent.getIntegerArrayListExtra("errorSteps")
        equipmentId = intent.getIntExtra("equipmentId", 0)

        viewModel = ViewModelProvider(
            this,
            FinishViewModelFactory(
                RepairRepository(applicationContext)
            )
        ).get(FinishViewModel::class.java)

        observeViewModel()

        if (hasErrors) {
            resultText.text = buildResultMessage(errorSteps)
            repairBtn.visibility = View.VISIBLE
        } else {
            resultText.text = "Проверка успешно завершена :)"
            repairBtn.visibility = View.GONE
        }

        repairBtn.setOnClickListener {
            viewModel.loadRepairTypes()
        }

        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            startActivity(intent)
            finish()
        }
    }

    private fun buildResultMessage(errorSteps: ArrayList<Int>?): String {
        if (errorSteps.isNullOrEmpty()) {
            return "Проверка завершена с ошибками :("
        }

        if (errorSteps.size == 1) {
            return "Проверка завершена с ошибкой :(\nОшибка на шаге: ${errorSteps[0]}"
        }

        return "Проверка завершена с ошибками :(\nОшибки на шагах: ${formatSteps(errorSteps)}"
    }

    private fun formatSteps(steps: ArrayList<Int>): String {
        return steps.joinToString(", ")
    }

    private fun observeViewModel() {
        viewModel.repairTypes.observe(this) { response ->
            showRepairDialog(response)
        }

        viewModel.repairSent.observe(this) { sent ->
            if (sent == true) {
                toast("Заявка успешно отправлена")

                repairDialog?.dismiss()
                repairBtn.visibility = View.GONE
            }
        }

        viewModel.error.observe(this) { message ->
            toast(message)
        }
    }

    private fun showRepairDialog(response: RepairTypesResponse?) {
        if (response?.types == null) {
            toast("Не удалось загрузить типы ремонтов")
            return
        }

        val typeNames = mutableListOf<String>()

        for (type: RepairType in response.types.orEmpty()) {
            typeNames.add(type.toString())
        }

        showRepairDialog(typeNames)
    }

    private fun showRepairDialog(types: List<String>) {
        val view = layoutInflater.inflate(R.layout.dialog_repair, null)

        val typeSpinner: Spinner = view.findViewById(R.id.typeSpinner)
        val prioritySpinner: Spinner = view.findViewById(R.id.prioritySpinner)
        val commentField: EditText = view.findViewById(R.id.commentField)
        val sendBtn: Button = view.findViewById(R.id.sendBtn)

        val typeAdapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            types
        )

        typeAdapter.setDropDownViewResource(R.layout.item_spinner_drop)
        typeSpinner.adapter = typeAdapter

        val priorities = arrayOf("Низкий", "Средний", "Высокий")

        val priorityAdapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            priorities
        )

        priorityAdapter.setDropDownViewResource(R.layout.item_spinner_drop)
        prioritySpinner.adapter = priorityAdapter

        repairDialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(true)
            .create()

        sendBtn.setOnClickListener {
            val selectedType = typeSpinner.selectedItem.toString()
            val selectedPriority = prioritySpinner.selectedItem.toString()
            val comment = commentField.text.toString().trim()

            if (comment.isEmpty()) {
                commentField.error = "Опишите неисправность"
                return@setOnClickListener
            }

            sendRepairRequest(
                defect = comment,
                category = selectedType,
                priority = selectedPriority
            )
        }

        repairDialog?.show()

        val window: Window? = repairDialog?.window

        if (window != null) {
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        val parent = view.parent as? View

        if (parent != null) {
            parent.setPadding(0, 0, 0, 0)
        }
    }

    private fun sendRepairRequest(
        defect: String,
        category: String,
        priority: String
    ) {
        val request = RepairRequest(
            defect = defect,
            category = category,
            equipmentId = equipmentId,
            priority = priority
        )

        viewModel.createRepair(request)
    }

    private fun toast(msg: String?) {
        if (msg == null) {
            return
        }

        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }
}