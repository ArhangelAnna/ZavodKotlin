package com.example.zavod.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zavod.R
import com.example.zavod.model.Schedule
import com.example.zavod.util.InspectionStatusMapper

class ScheduleAdapter(
    private val listener: OnScheduleClickListener?
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    private var items: List<Schedule> = emptyList()

    interface OnScheduleClickListener {
        fun onScheduleClick(schedule: Schedule)
    }

    fun setData(newItems: List<Schedule>?) {
        items = newItems ?: emptyList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_row, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = items[position]

        holder.title.text = item.taskName
        holder.equipment.text = item.equipmentName

        val status = InspectionStatusMapper.normalize(item.status)
        holder.status.text = status

        if (InspectionStatusMapper.NOT_COMPLETED == status) {
            holder.status.setTextColor(Color.parseColor("#F44336"))
            holder.statusIndicator.setImageResource(R.drawable.warning)
        } else if (
            InspectionStatusMapper.IN_PROGRESS == status ||
            InspectionStatusMapper.FAILED == status
        ) {
            holder.status.setTextColor(Color.parseColor("#FF9800"))
            holder.statusIndicator.setImageResource(R.drawable.warning)
        } else {
            holder.status.setTextColor(Color.parseColor("#4CAF50"))
            holder.statusIndicator.setImageResource(R.drawable.correct)
        }

        val canOpen = InspectionStatusMapper.canOpen(status)

        holder.itemView.isEnabled = canOpen
        holder.itemView.alpha = if (canOpen) 1f else 0.65f

        if (canOpen) {
            holder.itemView.setOnClickListener {
                listener?.onScheduleClick(item)
            }
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTaskName)
        val equipment: TextView = view.findViewById(R.id.tvEquipmentName)
        val status: TextView = view.findViewById(R.id.tvStatusText)
        val statusIndicator: ImageView = view.findViewById(R.id.ivStatusIcon)
    }
}