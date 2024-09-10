package com.volio.rate_feedback

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.volio.rate_feedback.databinding.DialogRateBinding
import com.volio.rate_feedback.utils.Utils
import com.volio.rate_feedback.utils.getScreenWidth

public class RateDialog private constructor(
    private val context: Context,
    private val isDarkTheme: Boolean,
    private val fontResTitle: Int,
    private val fontRes: Int,
    private val buttonColor: Int,
    private val listImageRate: List<Int>,
    private val feedbackContent: List<String>,
    private val forceDisplay: Boolean,
    private val isOnlyShowFeedback: Boolean,
    private val rateAtNumberStar: Int,
) {
    private var rate: Int = 0
    private var listRate: List<Int> = listOf(
        R.string.how_do_you_feel_about_the_app_your_feedback_is_important_to_us,
        R.string.rate_title_s1,
        R.string.rate_title_s2,
        R.string.rate_title_s3,
        R.string.rate_title_s4,
        R.string.rate_title_s5
    )

    class Builder(private val context: Context) {
        private var isDarkTheme: Boolean = false
        private var fontRes: Int = R.font.poppins_regular
        private var rateAtNumberStar: Int = 5
        private var isOnlyShowFeedback = false
        private var fontResTitle: Int = R.font.poppins_bold
        private var buttonColor: Int = R.color.select_color
        private var feedbackContent: List<String> = emptyList()
        private var listImageRate: List<Int> = listOf(
            R.drawable.ic_start,
            R.drawable.ic_1_star,
            R.drawable.ic_2_star,
            R.drawable.ic_3_star,
            R.drawable.ic_4_star,
            R.drawable.ic_5_star
        )
        private var forceDisplay: Boolean = true


        fun setDarkTheme(isDarkTheme: Boolean) = apply { this.isDarkTheme = isDarkTheme }
        fun setRateAtNumberStar(rateAtNumberStar: Int) = apply { this.rateAtNumberStar = rateAtNumberStar }
        fun setOnlyShowFeedback(isOnlyShowFeedback: Boolean) = apply { this.isOnlyShowFeedback = isOnlyShowFeedback }
        fun setFontRes(fontRes: Int) = apply { this.fontRes = fontRes }
        fun setFontResTitle(fontRes: Int) = apply { this.fontResTitle = fontRes }
        fun setButtonColor(buttonColor: Int) = apply { this.buttonColor = buttonColor }
        fun setFeedbackContent(feedbackContent: List<String>) =
            apply { this.feedbackContent = feedbackContent }

        fun setForceDisplay(forceDisplay: Boolean) = apply { this.forceDisplay = forceDisplay }

        fun build(): RateDialog {
            return RateDialog(
                context = context,
                isDarkTheme = isDarkTheme,
                fontResTitle = fontResTitle,
                fontRes = fontRes,
                buttonColor = buttonColor,
                feedbackContent = feedbackContent,
                forceDisplay = forceDisplay,
                listImageRate = listImageRate,
                isOnlyShowFeedback = isOnlyShowFeedback,
                rateAtNumberStar = rateAtNumberStar
            )
        }
    }


    fun show() {
        if (isOnlyShowFeedback){
            DialogFeedback(context = context,
                isDarkTheme = isDarkTheme,
                fontResTitle = fontResTitle,
                fontRes = fontRes,
                buttonColor = buttonColor,
                feedbackContent = feedbackContent,
                forceDisplay = forceDisplay,
                rateFeedbackListener = object : RateFeedbackListener {
                    override fun onRate(start: Int) {
                    }

                    override fun onFeedback(
                        position: Int, contentFeedback: String, isLast: Boolean
                    ) {
                    }

                }).show()
            return
        }

        val dialogBuilder = Dialog(context)
        dialogBuilder.setCanceledOnTouchOutside(false)
        dialogBuilder.setCancelable(false)
        val binding = DialogRateBinding.inflate(LayoutInflater.from(context))
        binding.isDarkTheme = isDarkTheme
        binding.buttonColor = buttonColor ?: R.color.select_color
        binding.fontRes = fontRes ?: R.font.poppins_regular
        binding.fontResTitle = fontResTitle ?: R.font.poppins_bold
        dialogBuilder.setContentView(binding.root)
        binding.apply {
            Glide.with(context).load(listImageRate[0]).into(imgRate)
            imgCancel.setOnClickListener {
                dialogBuilder.dismiss()
            }
            starView.onChangeStar = { index ->
                rate = index
                if (index >= 0 && index < listImageRate.size) {
                    Glide.with(context).load(listImageRate[index]).into(imgRate)
                    binding.tvContent.text = context.getString(listRate[index])
                }
                if (index > 0) {
                    binding.tvRate.alpha = 1f
//                    binding.tvRate.backgroundTintList = null
                }
            }
            binding.tvRate.setOnClickListener {
                if (rate > 0) {
                    if (rate >= rateAtNumberStar) {
                        Utils.openMarket(context)
                    } else {
                        //show feedback
                        DialogFeedback(context = context,
                            isDarkTheme = this.isDarkTheme!!,
                            fontResTitle = this.fontResTitle,
                            fontRes = this.fontRes,
                            buttonColor = this.buttonColor,
                            feedbackContent = feedbackContent,
                            forceDisplay = forceDisplay,
                            rateFeedbackListener = object : RateFeedbackListener {
                                override fun onRate(start: Int) {
                                }

                                override fun onFeedback(
                                    position: Int, contentFeedback: String, isLast: Boolean
                                ) {
                                }

                            }).show()
                    }
                    dialogBuilder.dismiss()
                }
            }

        }


//        feedbackContent.forEachIndexed { index, feedback ->
//            val textView = dialogView.findViewById<TextView>(
//                context.resources.getIdentifier("feedback_text_$index", "id", context.packageName)
//            )
//            textView?.text = feedback
//            font?.let { textView?.typeface = it }
//        }

//        val rateButton: Button = dialogView.findViewById(R.id.rate_button)
//        buttonColor?.let { rateButton.setBackgroundColor(ContextCompat.getColor(context, it)) }
//
//        rateButton.setOnClickListener {
//            dialogBuilder.dismiss()
//        }

        dialogBuilder.show()
        val width = getScreenWidth() * 0.85
        dialogBuilder.window?.setLayout(width.toInt(), -2)
        dialogBuilder.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
