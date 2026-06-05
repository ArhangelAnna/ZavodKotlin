package com.example.zavod.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zavod.R
import com.example.zavod.model.Equipment
import com.example.zavod.util.InspectionStatusMapper

class EquipmentAdapter(
    private val equipmentList: List<Equipment>?,
    private val listener: OnEquipmentClickListener?
) : RecyclerView.Adapter<EquipmentAdapter.ViewHolder>() {

    interface OnEquipmentClickListener {
        fun onEquipmentClick(equipment: Equipment)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equipment, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = equipmentList?.get(position) ?: return

        holder.name.text = item.name
        holder.location.text = item.location

        val status = InspectionStatusMapper.normalize(item.dailyInspectionStatus)

        holder.status.text = status
        holder.status.setTextColor(getStatusColor(status))

        val canOpen = canOpenInspection(status)

        holder.itemView.isEnabled = canOpen
        holder.itemView.alpha = if (canOpen) 1f else 0.65f

        if (canOpen) {
            holder.itemView.setOnClickListener {
                listener?.onEquipmentClick(item)
            }
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int {
        return equipmentList?.size ?: 0
    }

    private fun canOpenInspection(status: String): Boolean {
        return status == STATUS_NOT_CHECKED || status == STATUS_IN_PROGRESS
    }

    private fun getStatusColor(status: String): Int {
        return when (status) {
            STATUS_NOT_CHECKED -> Color.parseColor("#F44336")
            STATUS_IN_PROGRESS -> Color.parseColor("#FF9800")
            STATUS_WITH_REMARKS -> Color.parseColor("#FF9800")
            STATUS_CHECKED -> Color.parseColor("#4CAF50")
            else -> Color.parseColor("#757575")
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvEquipmentName)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val location: TextView = itemView.findViewById(R.id.tvLocation)
    }

    companion object {
        private const val STATUS_NOT_CHECKED = "не проверено"
        private const val STATUS_IN_PROGRESS = "выполняется"
        private const val STATUS_CHECKED = "проверено"
        private const val STATUS_WITH_REMARKS = "проверено с замечаниями"
    }
}