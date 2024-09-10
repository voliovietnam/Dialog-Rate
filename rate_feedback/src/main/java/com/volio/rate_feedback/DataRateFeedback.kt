package com.volio.rate_feedback

import android.content.Context
import com.volio.rate_feedback.R
import com.volio.rate_feedback.model.DataFeedback
import com.volio.rate_feedback.model.DataRate

object DataRateFeedback {
    fun getRate(context: Context): List<DataRate> {
        val listRate: MutableList<DataRate> = mutableListOf()
        listRate.add(
            DataRate(
                R.drawable.ic_start,
                context.getString(R.string.how_do_you_feel_about_the_app_your_feedback_is_important_to_us)
            )
        )
        listRate.add(
            DataRate(
                R.drawable.ic_1_star,
                context.getString(R.string.oh_no_please_leave_us_some_feedback)
            )
        )
        listRate.add(
            DataRate(
                R.drawable.ic_2_star,
                context.getString(R.string.oh_no_please_leave_us_some_feedback)
            )
        )
        listRate.add(
            DataRate(
                R.drawable.ic_3_star,
                context.getString(R.string.oh_no_please_leave_us_some_feedback)
            )
        )
        listRate.add(
            DataRate(
                R.drawable.ic_4_star,
                context.getString(R.string.we_like_you_too_thanks_for_your_feedback)
            )
        )
        listRate.add(
            DataRate(
                R.drawable.ic_5_star,
                context.getString(R.string.we_like_you_too_thanks_for_your_feedback)
            )
        )
        return listRate
    }

    fun getFeedback(context: Context): MutableList<DataFeedback> {
        val listFeedback: MutableList<DataFeedback> = mutableListOf()
        listFeedback.add(DataFeedback(context.getString(R.string.i_don_t_want_to_see_ads)))
        listFeedback.add(DataFeedback(context.getString(R.string.i_don_t_know_how_to_use)))
        listFeedback.add(DataFeedback(context.getString(R.string.other)))
        return listFeedback
    }
}