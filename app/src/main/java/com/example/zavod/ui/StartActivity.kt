package com.example.zavod.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.zavod.R
import com.example.zavod.api.HintUrlBuilder
import com.example.zavod.domain.StartSession
import com.example.zavod.repository.StartRepository
import com.example.zavod.service.nfcService.NfcService
import com.example.zavod.util.CheckTypeMapper
import com.example.zavod.viewmodel.StartViewModel
import com.example.zavod.viewmodel.StartViewModelFactory
import com.google.gson.Gson

class StartActivity : AppCompatActivity() {

    private var nameEquipment: String? = null
    private var inspectionType: String = CheckTypeMapper.DAILY
    private var idEquipment: Int = 0
    private var templateId: Int? = null
    private var scheduleId: Int? = null

    private lateinit var nfcService: NfcService
    private lateinit var viewModel: StartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        idEquipment = intent.getIntExtra("equipment_id", 0)
        nameEquipment = intent.getStringExtra("equipment_name")

        val rawInspectionType = intent.getStringExtra("inspection_type")
        templateId = getNullableIntExtra("template_id")
        scheduleId = getNullableIntExtra("schedule_id")

        inspectionType = if (scheduleId != null) {
            CheckTypeMapper.SCHEDULED
        } else {
            CheckTypeMapper.normalizeForStart(rawInspectionType)
        }

        val repo = StartRepository(applicationContext)

        viewModel = ViewModelProvider(
            this,
            StartViewModelFactory(repo)
        ).get(StartViewModel::class.java)

        val hintButton: Button = findViewById(R.id.btnHint)
        hintButton.setOnClickListener {
            showHintDialog(
                HintUrlBuilder.build(
                    idEquipment,
                    inspectionType
                )
            )
        }

        nfcService = NfcService(this) { tagId ->
            startCheck(tagId)
        }
    }

    private fun getNullableIntExtra(key: String): Int? {
        val value = intent.getIntExtra(key, -1)
        return if (value > 0) value else null
    }

    private fun showHintDialog(imageUrl: String) {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.modal_hint, null)

        val imageView: ImageView = dialogView.findViewById(R.id.hintImageView)
        val backBtn: Button = dialogView.findViewById(R.id.backBtn)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        backBtn.setOnClickListener {
            dialog.dismiss()
        }

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.bg_btn)
            .error(R.drawable.bg_grad)
            .into(imageView)

        dialog.show()

        val window: Window? = dialog.window

        if (window != null) {
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        val parent = dialogView.parent as? View

        if (parent != null) {
            parent.setPadding(0, 0, 0, 0)
        }
    }

    private fun startCheck(tagId: String) {
        viewModel.start(
            tagId = tagId,
            typeCheck = inspectionType,
            equipmentId = idEquipment,
            templateId = templateId,
            scheduleId = scheduleId,
            callback = object : StartViewModel.Callback {
                override fun onSuccess(session: StartSession) {
                    runOnUiThread {
                        openStepScreen(session)
                    }
                }

                override fun onError(error: String?) {
                    runOnUiThread {
                        Toast.makeText(
                            this@StartActivity,
                            error ?: "Не удалось начать проверку",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        )
    }

    private fun openStepScreen(session: StartSession) {
        Log.d("START_DEBUG", Gson().toJson(session))
        Log.d("START_DEBUG", Gson().toJson(session.firstStep))

        val intent = Intent(this, StepActivity::class.java).apply {
            putExtra("sessionId", session.sessionId)
            putExtra("totalSteps", session.totalSteps)
            putExtra("equipment_id", idEquipment)
            putExtra("equipment_name", nameEquipment)
            putExtra("inspection_type", inspectionType)
            putExtra("step", session.firstStep)
        }

        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        nfcService.enable(this)
    }

    override fun onPause() {
        super.onPause()
        nfcService.disable(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        nfcService.handle(intent)
    }
}