package com.example.zavod.ui

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.zavod.R
import com.example.zavod.api.HintUrlBuilder
import com.example.zavod.model.Step
import com.example.zavod.model.StepRequest
import com.example.zavod.model.StepResult
import com.example.zavod.model.step.StepState
import com.example.zavod.repository.StepRepository
import com.example.zavod.viewmodel.StepViewModel
import com.example.zavod.viewmodel.StepViewModelFactory
import java.io.File

class StepActivity : AppCompatActivity() {

    private lateinit var stepsContainer: LinearLayout
    private lateinit var titleText: TextView
    private lateinit var descText: TextView
    private lateinit var input: EditText
    private lateinit var commentField: EditText
    private lateinit var photoBtn: Button
    private lateinit var hintBtn: Button
    private lateinit var confirmBtn: Button

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private var photoPath: String? = null
    private var waitingNfc: Boolean = false

    private lateinit var viewModel: StepViewModel
    private var stepRepository: StepRepository? = null

    private var finishedNormally: Boolean = false
    private var cancelRequested: Boolean = false

    private var sessionId: String? = null
    private var step: Step? = null
    private var idEquipment: Int = 0
    private var inspectionType: String? = null

    private val stepViews: MutableList<TextView> = mutableListOf()
    private val stepResults: MutableList<StepState.StepResult> = mutableListOf()
    private val errorStepNumbers: ArrayList<Int> = arrayListOf()
    private var currentIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step)

        bindViews()
        readIntentData()

        if (step == null || sessionId == null) {
            toast("Ошибка данных")
            finish()
            return
        }

        setupViewModel()
        observeViewModel()
        setupHintButton()
        setupCamera()
        setupInputListener()
        setupButtons()

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        renderStep(step!!)
        renderSteps(intent.getIntExtra("totalSteps", 0))
        updateButtonsState()
    }

    private fun bindViews() {
        stepsContainer = findViewById(R.id.stepsContainer)
        titleText = findViewById(R.id.title)
        descText = findViewById(R.id.desc)
        input = findViewById(R.id.inputField)
        commentField = findViewById(R.id.commentField)
        photoBtn = findViewById(R.id.photoBtn)
        confirmBtn = findViewById(R.id.confirm)
        hintBtn = findViewById(R.id.btnHintStep)
    }

    private fun readIntentData() {
        sessionId = intent.getStringExtra("sessionId")
        idEquipment = intent.getIntExtra("equipment_id", 0)
        inspectionType = intent.getStringExtra("inspection_type")

        step = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("step", Step::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("step") as? Step
        }
    }

    private fun setupViewModel() {
        stepRepository = StepRepository(applicationContext)

        viewModel = ViewModelProvider(
            this,
            StepViewModelFactory(stepRepository!!)
        ).get(StepViewModel::class.java)

        viewModel.init(sessionId, step)
    }

    private fun observeViewModel() {
        viewModel.photoResult.observe(this) { result ->
            handlePhotoResult(result)
        }

        viewModel.checkResult.observe(this) { result ->
            handleStepResult(result)
        }

        viewModel.error.observe(this) { message ->
            toast(message)
        }
    }

    private fun setupHintButton() {
        hintBtn.setOnClickListener {
            showHintStep(
                step?.id,
                idEquipment,
                inspectionType
            )
        }
    }

    private fun setupCamera() {
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                photoPath = result.data?.getStringExtra("path")
                viewModel.setPhoto(photoPath)
                toast("Фото получено")
            }
        }
    }

    private fun setupInputListener() {
        input.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // не требуется
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                updateButtonsState()
            }

            override fun afterTextChanged(s: android.text.Editable?) {
                // не требуется
            }
        })
    }

    private fun setupButtons() {
        photoBtn.setOnClickListener {
            cameraLauncher.launch(
                Intent(this, CameraActivity::class.java)
            )
        }

        confirmBtn.setOnClickListener {
            confirmStep()
        }
    }

    private fun showHintStep(
        stepId: String?,
        idEquipment: Int,
        inspectionType: String?
    ) {
        val dialogView = layoutInflater.inflate(R.layout.modal_hint, null)

        val hintImage: ImageView = dialogView.findViewById(R.id.hintImageView)
        val backBtn: Button = dialogView.findViewById(R.id.backBtn)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        backBtn.setOnClickListener {
            dialog.dismiss()
        }

        Glide.with(this)
            .load(
                HintUrlBuilder.build(
                    idEquipment,
                    inspectionType,
                    stepId
                )
            )
            .placeholder(R.drawable.bg_btn)
            .error(R.drawable.bg_grad)
            .into(hintImage)

        dialog.show()
    }

    private fun updateButtonsState() {
        val value = input.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val hasInput = value.isNotEmpty()
        val currentStep = step ?: return

        if (currentStep.type == "scan") {
            photoBtn.isEnabled = false
            return
        }

        photoBtn.isEnabled = hasInput
    }

    private fun createStepIndicator(): ImageView {
        val indicator = ImageView(this)

        val params = LinearLayout.LayoutParams(40, 40)
        params.setMargins(0, 0, 12, 0)
        indicator.layoutParams = params

        val circle = GradientDrawable()
        circle.shape = GradientDrawable.OVAL
        circle.setSize(40, 40)
        circle.setColor(Color.GRAY)

        indicator.background = circle

        return indicator
    }

    private fun renderSteps(count: Int) {
        stepsContainer.removeAllViews()
        stepViews.clear()
        stepResults.clear()
        errorStepNumbers.clear()

        for (i in 0 until count) {
            stepResults.add(StepState.StepResult.NONE)

            val stepRow = LinearLayout(this)
            stepRow.orientation = LinearLayout.HORIZONTAL
            stepRow.setPadding(8, 12, 8, 12)
            stepRow.gravity = Gravity.CENTER_VERTICAL

            val indicator = createStepIndicator()

            val tv = TextView(this)
            tv.text = "Шаг ${i + 1}"
            tv.setTextColor(Color.WHITE)
            tv.textSize = 14f
            tv.setTypeface(null, Typeface.BOLD)

            stepRow.addView(indicator)
            stepRow.addView(tv)

            val index = i
            stepRow.setOnClickListener {
                toast("Шаг ${index + 1}")
            }

            stepsContainer.addView(stepRow)
            stepViews.add(tv)
        }

        setCurrentStep(0)
    }

    private fun setIndicatorColor(
        indicator: ImageView,
        color: Int
    ) {
        val bg = indicator.background as? GradientDrawable
        bg?.setColor(color)
    }

    private fun saveErrorStep(index: Int) {
        val stepNumber = index + 1

        if (!errorStepNumbers.contains(stepNumber)) {
            errorStepNumbers.add(stepNumber)
        }
    }

    private fun removeErrorStep(index: Int) {
        errorStepNumbers.remove(index + 1)
    }

    private fun addStepNumberToMessage(message: String): String {
        return "Шаг ${currentIndex + 1}: $message"
    }

    private fun markStep(
        index: Int,
        result: StepState.StepResult
    ) {
        if (index < 0 || index >= stepsContainer.childCount) {
            return
        }

        val stepRow = stepsContainer.getChildAt(index) as? LinearLayout ?: return
        val indicator = stepRow.getChildAt(0) as? ImageView ?: return

        when (result) {
            StepState.StepResult.SUCCESS -> setIndicatorColor(indicator, Color.GREEN)
            StepState.StepResult.WARNING -> setIndicatorColor(indicator, Color.RED)
            StepState.StepResult.NONE -> setIndicatorColor(indicator, Color.GRAY)
        }

        if (index in stepResults.indices) {
            stepResults[index] = result
        }
    }

    private fun setCurrentStep(index: Int) {
        for (i in stepViews.indices) {
            val stepRow = stepsContainer.getChildAt(i) as? LinearLayout ?: continue
            val indicator = stepRow.getChildAt(0) as? ImageView ?: continue

            val result = stepResults.getOrNull(i) ?: StepState.StepResult.NONE

            when (result) {
                StepState.StepResult.SUCCESS -> setIndicatorColor(indicator, Color.GREEN)
                StepState.StepResult.WARNING -> setIndicatorColor(indicator, Color.RED)
                StepState.StepResult.NONE -> setIndicatorColor(indicator, Color.GRAY)
            }
        }

        if (index < stepViews.size) {
            val stepRow = stepsContainer.getChildAt(index) as? LinearLayout
            val indicator = stepRow?.getChildAt(0) as? ImageView
            if (indicator != null) {
                setIndicatorColor(indicator, Color.WHITE)
            }
        }

        currentIndex = index
    }

    private fun confirmStep() {
        val currentStep = step ?: return

        val value = input.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val comment = commentField.text
            ?.toString()
            ?.trim()
            .orEmpty()

        if (value.isEmpty() && currentStep.type != "scan") {
            toast("Введите данные")
            return
        }

        viewModel.setInput(value)
        viewModel.setComment(comment)

        if (currentStep.type == "scan") {
            startNfc()
            return
        }

        if (currentStep.requiresPhoto) {
            handlePhoto()
        } else {
            startNfc()
        }
    }

    private fun handlePhoto() {
        val path = photoPath

        if (path == null || !File(path).exists()) {
            toast("Сделайте фото")
            return
        }

        viewModel.uploadPhoto()
    }

    private fun handlePhotoResult(result: StepResult?) {
        if (result == null) {
            toast("Ошибка фото")
            return
        }

        if (!result.success) {
            toast(result.error ?: "Ошибка фото")
            return
        }

        startNfc()
    }

    private fun startNfc() {
        waitingNfc = true
        toast("Поднесите NFC")
    }

    override fun onResume() {
        super.onResume()

        val adapter = nfcAdapter ?: return

        val intent = Intent(this, javaClass)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE
        } else {
            0
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            flags
        )

        adapter.enableForegroundDispatch(
            this,
            pendingIntent,
            null,
            null
        )
    }

    override fun onPause() {
        super.onPause()

        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (!waitingNfc) return

        val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }

        if (tag == null) {
            return
        }

        viewModel.setTag(bytesToHex(tag.id))
        waitingNfc = false

        finishStep()
    }

    private fun finishStep() {
        val currentStep = step ?: return

        val req = StepRequest(
            sessionId = sessionId,
            stepId = currentStep.id,
            tagId = viewModel.getTag(),
            value = viewModel.getInput(),
            comment = commentField.text.toString()
        )

        viewModel.checkStep(req)
    }

    private fun handleStepResult(result: StepResult?) {
        if (result == null || !result.success) {
            saveErrorStep(currentIndex)

            val message = result?.error ?: "Ошибка проверки шага"
            val messageWithStep = addStepNumberToMessage(message)

            Log.e(TAG, messageWithStep)

            markStep(
                currentIndex,
                StepState.StepResult.WARNING
            )

            toast(messageWithStep)
            return
        }

        if (result.warning) {
            saveErrorStep(currentIndex)

            markStep(
                currentIndex,
                StepState.StepResult.WARNING
            )
        } else {
            removeErrorStep(currentIndex)

            markStep(
                currentIndex,
                StepState.StepResult.SUCCESS
            )
        }

        if (result.finished) {
            openFinishScreen()
            return
        }

        val nextStep = result.nextStep

        if (nextStep == null) {
            toast("Следующий шаг не найден")
            return
        }

        currentIndex += 1
        setCurrentStep(currentIndex)
        openNext(nextStep)
    }

    private fun openFinishScreen() {
        finishedNormally = true

        val hasErrors = stepResults.any {
            it == StepState.StepResult.WARNING
        }

        val intent = Intent(this, FinishActivity::class.java).apply {
            putExtra("hasErrors", hasErrors)
            putIntegerArrayListExtra("errorSteps", errorStepNumbers)
            putExtra("totalSteps", stepViews.size)
            putExtra("equipmentId", idEquipment)
        }

        startActivity(intent)
        finish()
    }

    private fun openNext(nextStep: Step) {
        step = nextStep
        viewModel.setStep(nextStep)

        runOnUiThread {
            renderStep(nextStep)

            input.setText("")
            commentField.setText("")
            photoPath = null

            viewModel.setPhoto(null)
            viewModel.setComment(null)

            updateButtonsState()
            waitingNfc = false
        }
    }

    private fun renderStep(step: Step) {
        titleText.text = step.title
        descText.text = step.description

        val scan = step.type == "scan"

        if (scan) {
            input.visibility = View.GONE
            photoBtn.visibility = View.GONE
        } else {
            input.visibility = View.VISIBLE

            photoBtn.visibility = if (step.requiresPhoto) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    override fun onDestroy() {
        if (
            isFinishing &&
            !finishedNormally &&
            !cancelRequested &&
            sessionId != null
        ) {
            cancelRequested = true
            stepRepository?.cancelCheck(sessionId)
        }

        super.onDestroy()
    }

    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") {
            "%02X".format(it)
        }
    }

    private fun toast(msg: String?) {
        if (msg == null) {
            return
        }

        runOnUiThread {
            Toast.makeText(
                this,
                msg,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val TAG = "STEP_FLOW"
    }
}