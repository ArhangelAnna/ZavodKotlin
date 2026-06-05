package com.example.zavod.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zavod.R
import com.example.zavod.adapters.EquipmentAdapter
import com.example.zavod.model.Equipment
import com.example.zavod.model.ProfileResponse
import com.example.zavod.repository.ProfileRepository
import com.example.zavod.viewmodel.ProfileViewModel
import com.example.zavod.viewmodel.ProfileViewModelFactory

class ProfileFragment : Fragment() {

    private lateinit var btnBack: TextView
    private lateinit var tvName: TextView
    private lateinit var tvWork: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
            R.layout.activity_profile,
            container,
            false
        )

        bindViews(view)
        setupRecyclerView()
        setupViewModel()
        observeViewModel()
        loadProfile()
        setupBackButton()

        return view
    }

    private fun bindViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        tvName = view.findViewById(R.id.name_tv)
        tvWork = view.findViewById(R.id.work_tv)
        recyclerView = view.findViewById(R.id.rvEquipment)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory(
                ProfileRepository(requireContext())
            )
        )[ProfileViewModel::class.java]
    }

    private fun observeViewModel() {
        viewModel.profile.observe(viewLifecycleOwner) { data ->
            renderProfile(data)
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            toast(message)
        }
    }

    private fun loadProfile() {
        val prefs = requireContext().getSharedPreferences(
            "auth",
            Context.MODE_PRIVATE
        )

        val passId = prefs.getString("pass_id", "")
        viewModel.loadProfile(passId)
    }

    private fun setupBackButton() {
        btnBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun renderProfile(data: ProfileResponse?) {
        val employee = data?.employee ?: return

        btnBack.text = "← №${employee.passId}"
        tvName.text = employee.fullName
        tvWork.text = employee.position

        val adapter = EquipmentAdapter(
            data.equipment,
            object : EquipmentAdapter.OnEquipmentClickListener {
                override fun onEquipmentClick(equipment: Equipment) {
                    val intent = Intent(requireContext(), StartActivity::class.java).apply {
                        putExtra("equipment_id", equipment.id ?: 0)
                        putExtra("equipment_name", equipment.name)
                        putExtra("inspection_type", "daily")
                    }

                    startActivity(intent)
                }
            }
        )

        recyclerView.adapter = adapter
    }

    private fun toast(msg: String?) {
        if (context == null || msg == null) {
            return
        }

        Toast.makeText(
            context,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }
}