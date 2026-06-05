package com.example.zavod.adapters

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.zavod.R
import com.example.zavod.model.DayItem
import java.time.format.DateTimeFormatter

class WeekAdapter(
    private var days: List<DayItem>?,
    private val listener: OnDayClickListener?
) : RecyclerView.Adapter<WeekAdapter.ViewHolder>() {

    interface OnDayClickListener {
        fun onDayClick(day: DayItem, position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day, parent, false)

        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val day = days?.get(position) ?: return

        holder.tvDayOfWeek.text = day.getDayOfWeek()

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        holder.tvFullDate.text = day.date.format(formatter)

        when (day.status) {
            1 -> {
                holder.ivStatus.visibility = View.VISIBLE
                holder.ivStatus.setImageResource(R.drawable.correct)
                holder.ivStatus.setColorFilter(Color.parseColor("#4CAF50"))
            }

            2 -> {
                holder.ivStatus.visibility = View.VISIBLE
                holder.ivStatus.setImageResource(R.drawable.warning)
                holder.ivStatus.setColorFilter(Color.parseColor("#F44336"))
            }

            else -> {
                holder.ivStatus.visibility = View.INVISIBLE
            }
        }

        if (day.isSelected) {
            holder.itemView.setBackgroundResource(R.drawable.select_day_item)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_card2)
        }

        holder.itemView.setOnClickListener {
            listener?.onDayClick(day, position)
        }
    }

    override fun getItemCount(): Int {
        return days?.size ?: 0
    }

    fun updateData(newDays: List<DayItem>?) {
        days = newDays
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDayOfWeek: TextView = itemView.findViewById(R.id.tvDayOfWeek)
        val tvFullDate: TextView = itemView.findViewById(R.id.tvFullDay)
        val ivStatus: ImageView = itemView.findViewById(R.id.ivDayStatus)
    }
}