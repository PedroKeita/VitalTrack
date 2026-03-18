package com.vitaltrack.app.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vitaltrack.app.databinding.FragmentOnboardingStep3Binding
import com.vitaltrack.app.ui.activity.OnboardingActivity

class StepThreeFragment : Fragment() {

    private var _binding: FragmentOnboardingStep3Binding? = null
    private val binding get() = _binding!!

    private var selectedLevel = "SEDENTARY"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingStep3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // detecta qual radio button foi selecionado
        binding.radioGroupActivity.setOnCheckedChangeListener { _, checkedId ->
            selectedLevel = when (checkedId) {
                binding.radioSedentary.id  -> "SEDENTARY"
                binding.radioLight.id      -> "LIGHT"
                binding.radioModerate.id   -> "MODERATE"
                binding.radioActive.id     -> "ACTIVE"
                binding.radioVeryActive.id -> "VERY_ACTIVE"
                else                       -> "SEDENTARY"
            }
        }

        binding.btnFinish.setOnClickListener {
            val activity = requireActivity() as OnboardingActivity
            activity.activityLevel = selectedLevel
            activity.finishOnboarding()
        }

        binding.btnBack.setOnClickListener {
            val activity = requireActivity() as OnboardingActivity
            activity.goToPreviousStep()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}