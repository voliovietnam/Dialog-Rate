package com.volio.rate_feedback.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.volio.rate_feedback.DataRateFeedback
import com.volio.rate_feedback.R
import com.volio.rate_feedback.databinding.ItemFeedbackBinding
import com.volio.rate_feedback.model.DataFeedback

class FeedbackAdapter(
    context: Context,
    private val isDarkTheme: Boolean,
    private val fontRes : Int,
    private val buttonColor : Int,
    val onSelectItem: (position: Int, isLast: Boolean, data: DataFeedback) -> Unit
) : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {
    private var listData = DataRateFeedback.getFeedback(context)
    private var selectPosition: Int = -1

    fun setData(list: List<String>) {
        val listNew = list.reversed()
        listNew.forEach {
            listData.add(0,DataFeedback(it))
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        return FeedbackViewHolder(ItemFeedbackBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        holder.bindView(position)
    }

    override fun onBindViewHolder(
        holder: FeedbackViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        holder.bindView(position)
    }

    inner class FeedbackViewHolder(val binding: ItemFeedbackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(position: Int) {
            binding.fontRes = fontRes
            binding.buttonColor = buttonColor ?: R.color.select_color
            binding.isDarkTheme = isDarkTheme
            if (binding.isSelect != (selectPosition == position)) {
                binding.isSelect = selectPosition == position
            }
            if (binding.title != listData[position].content) {
                binding.title = listData[position].content
            }
            binding.root.setOnClickListener {
                val oldPosition = selectPosition
                selectPosition = position
                onSelectItem.invoke(position, position == listData.size - 1, listData[position])
                notifyItemChanged(position, Any())
                if (oldPosition >= 0) {
                    notifyItemChanged(oldPosition, Any())
                }
            }
        }
    }
}




