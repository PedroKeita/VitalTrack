package com.vitaltrack.app.ui.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vitaltrack.app.ui.onboarding.StepOneFragment
import com.vitaltrack.app.ui.onboarding.StepThreeFragment
import com.vitaltrack.app.ui.onboarding.StepTwoFragment

class OnboardingAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StepOneFragment()
            1 -> StepTwoFragment()
            2 -> StepThreeFragment()
            else -> StepOneFragment()
        }
    }
}