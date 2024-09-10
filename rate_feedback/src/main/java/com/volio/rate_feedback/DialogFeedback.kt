package com.volio.rate_feedback

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.doAfterTextChanged
import com.volio.rate_feedback.adapter.FeedbackAdapter
import com.volio.rate_feedback.databinding.DialogFeedbackBinding
import com.volio.rate_feedback.model.DataFeedback
import com.volio.rate_feedback.utils.BaseDialogRateFeedback
import com.volio.rate_feedback.utils.Constant.EMAIL_FEEDBACK
import com.volio.rate_feedback.utils.Utils
import com.volio.rate_feedback.utils.setBackgroundTintInt

class DialogFeedback(context: Context,
                     private val isDarkTheme: Boolean,
                     private val fontResTitle: Int,
                     private val fontRes: Int,
                     private val buttonColor: Int,
                     private val feedbackContent: List<String>,
                     private val forceDisplay: Boolean,

                     private val rateFeedbackListener: RateFeedbackListener) :
    BaseDialogRateFeedback(context) {
    private val binding = DialogFeedbackBinding.inflate(LayoutInflater.from(context))

    private var isSendMail: Boolean = false

    private var content: String = ""

    private var feedbackPosition: Int = -1

    private val adapter by lazy {
        FeedbackAdapter(context,isDarkTheme,fontRes,buttonColor) { position, isLast, data ->
            selectItem(position, isLast, data)
        }
    }



    override val contentView: View = binding.root
    override fun onViewReady() {
        initRVFeedback()
        initListener()

    }

    override fun show() {
        super.show()
        binding.isDarkTheme = isDarkTheme
        binding.buttonColor = buttonColor!!
        binding.fontRes = fontRes
        binding.fontResTitle = fontResTitle
        rateFeedbackListener.onShowFeedback()
    }

    private fun initListener() {
        binding.edFeedback.doAfterTextChanged {
            if (isSendMail) {
                content = it.toString()
            }
        }
        binding.tvFeedback.setOnClickListener {
            if (feedbackPosition >= 0) {
                if (feedbackPosition != 0) {
                    sendMail()
                }
                dismiss()
                rateFeedbackListener.onFeedback(
                    feedbackPosition+1,
                    content,
                    feedbackPosition == adapter.itemCount - 1
                )
            }
        }
        binding.imgCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun initRVFeedback() {
        adapter.setData(feedbackContent)
        binding.rvFeedback.adapter = adapter
    }

    private fun selectItem(position: Int, isLast: Boolean, data: DataFeedback) {
        feedbackPosition = position
        isSendMail = isLast
        content = data.content
        binding.tvFeedback.isClickable = true
        binding.tvFeedback.alpha = 1f
        binding.tvFeedback.setBackgroundTintInt(buttonColor)

        if (isLast) {
            binding.edFeedback.visibility = View.VISIBLE
            content = binding.edFeedback.text.toString()
        } else {
            binding.edFeedback.visibility = View.GONE
        }
    }

    private fun getAppName(): String {
        val packageManager = context.packageManager
        var applicationInfo: ApplicationInfo? = null
        runCatching {
            applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)

        }.onFailure {
            it.printStackTrace()
        }
        return (if (applicationInfo != null) packageManager.getApplicationLabel(applicationInfo!!) else "") as String
    }

    private fun sendMail() {
        var version = ""
        runCatching {
            val info: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            version = info.versionName
        }.onFailure {
            it.printStackTrace()
        }
        val title = "${getAppName()} version $version Feedback"
        Utils.sendEmail(context, arrayOf(EMAIL_FEEDBACK), title, content)
    }
}