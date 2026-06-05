package com.example.zavod.ui.dashboard

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zavod.adapters.ScheduleAdapter
import com.example.zavod.adapters.WeekAdapter
import com.example.zavod.databinding.FragmentDashboardBinding
import com.example.zavod.repository.ScheduleRepository
import com.example.zavod.ui.StartActivity
import com.example.zavod.util.CheckTypeMapper

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding: FragmentDashboardBinding
        get() = _binding!!

    private lateinit var viewModel: DashboardViewModel

    private lateinit var weekAdapter: WeekAdapter
    private lateinit var scheduleAdapter: ScheduleAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(
            this,
            DashboardViewModelFactory(
                ScheduleRepository(requireContext())
            )
        )[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        setupRecyclerViews()
        observeViewModel()
        loadSchedule()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadSchedule() {
        val passId = requireContext()
            .getSharedPreferences("auth", 0)
            .getString("pass_id", "")

        viewModel.loadSchedule(passId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupRecyclerViews() {
        weekAdapter = WeekAdapter(
            days = ArrayList(),
            listener = object : WeekAdapter.OnDayClickListener {
                override fun onDayClick(
                    day: com.example.zavod.model.DayItem,
                    position: Int
                ) {
                    viewModel.selectDay(position)
                }
            }
        )

        binding.recyclerViewDays.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        binding.recyclerViewDays.adapter = weekAdapter

        scheduleAdapter = ScheduleAdapter(
            object : ScheduleAdapter.OnScheduleClickListener {
                override fun onScheduleClick(schedule: com.example.zavod.model.Schedule) {
                    val intent = Intent(requireContext(), StartActivity::class.java)

                    val checkType = CheckTypeMapper.normalizeForSchedule(schedule.checkType)

                    intent.putExtra("equipment_id", schedule.equipmentId)
                    intent.putExtra("equipment_name", schedule.equipmentName)
                    intent.putExtra("inspection_type", checkType)
                    intent.putExtra("template_id", schedule.templateId)
                    intent.putExtra("schedule_id", schedule.id)

                    startActivity(intent)
                }
            }
        )

        binding.recyclerViewSchedules.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewSchedules.adapter = scheduleAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeViewModel() {
        viewModel.daysList.observe(viewLifecycleOwner) { days ->
            weekAdapter.updateData(days)

            val todayPos = viewModel.findTodayPosition()
            if (todayPos != -1) {
                binding.recyclerViewDays.scrollToPosition(todayPos)
            }
        }

        viewModel.dayInfoText.observe(viewLifecycleOwner) { text ->
            binding.tvDayInfo.text = text
        }

        viewModel.selectedDaySchedules.observe(viewLifecycleOwner) { schedules ->
            scheduleAdapter.setData(schedules)
            binding.tvNoTasks.visibility = if (schedules.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            toast(message)
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}