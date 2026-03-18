package com.vitaltrack.app.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.TransitionBuilder.validate
import androidx.fragment.app.Fragment
import com.vitaltrack.app.R
import com.vitaltrack.app.databinding.FragmentOnboardingStep1Binding
import com.vitaltrack.app.databinding.FragmentOnboardingStep2Binding
import com.vitaltrack.app.ui.activity.OnboardingActivity

class StepTwoFragment : Fragment() {

    private var _binding: FragmentOnboardingStep2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            if (validate()) {
                val activity = requireActivity() as OnboardingActivity
                activity.weight = binding.etWeight.text.toString().toFloat()
                activity.height = binding.etHeight.text.toString().toFloat()
                activity.goToNextStep()
            }
        }

        binding.btnBack.setOnClickListener {
            val activity = requireActivity() as OnboardingActivity
            activity.goToPreviousStep()
        }
    }

    private fun validate(): Boolean {
        var isValid = true

        val weight = binding.etWeight.text.toString().trim()
        val height = binding.etHeight.text.toString().trim()

        if (weight.isEmpty()) {
            binding.tilWeight.error = getString(R.string.error_field_required)
        }

        else if (weight.toFloatOrNull() == null || weight.toFloat() < 20f || weight.toFloat() > 300f) {
            binding.tilWeight.error = getString(R.string.error_invalid_weight)
            isValid = false
        }

        else {
            binding.tilWeight.error = null
        }

        if (height.isEmpty()) {
            binding.tilHeight.error = getString(R.string.error_field_required)
            isValid = false
        }

        else if (height.toFloatOrNull() == null || height.toFloat() < 50f || height.toFloat() > 250f) {
            binding.tilHeight.error = getString(R.string.error_invalid_height)
            isValid = false
        }

        else {
            binding.tilHeight.error = null
        }

        return isValid

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}