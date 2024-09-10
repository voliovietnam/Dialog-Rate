package com.volio.rate_feedback

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.volio.rate_feedback.databinding.DialogRateBinding
import com.volio.rate_feedback.utils.BaseDialogRateFeedback
import com.volio.rate_feedback.utils.Utils

class DialogRate(context: Context, private val rateFeedbackListener: RateFeedbackListener) :
    BaseDialogRateFeedback(context) {
    private var rate: Int = 0

    private val binding = DialogRateBinding.inflate(LayoutInflater.from(context))


    override val contentView: View = binding.root
    override fun show() {
        super.show()
        rateFeedbackListener.onShowRate()
    }
    override fun onViewReady() {
        val listRate = DataRateFeedback.getRate(context)
        Glide.with(context).load(listRate[0].imgRes).into(binding.imgRate)
        binding.starView.onChangeStar = { index ->
            rate = index
            if (index >= 0 && index < listRate.size) {
                Glide.with(context).load(listRate[index].imgRes).into(binding.imgRate)
                binding.tvContent.text = listRate[index].title
            }
            if (index > 0) {
                binding.tvRate.alpha = 1f
                binding.tvRate.backgroundTintList = null
            }
        }
        binding.tvRate.setOnClickListener {
            if (rate > 0) {
                if (rate == 5) {
                    Utils.openMarket(context)
                } else {
//                    DialogFeedback(context,rateFeedbackListener).show()
                }
                dismiss()
                rateFeedbackListener.onRate(rate)
            }
        }
        binding.imgCancel.setOnClickListener {
            dismiss()
        }
    }
}