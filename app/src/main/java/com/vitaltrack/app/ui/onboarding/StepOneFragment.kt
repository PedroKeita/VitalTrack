package com.vitaltrack.app.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vitaltrack.app.databinding.FragmentOnboardingStep1Binding
import com.vitaltrack.app.ui.activity.OnboardingActivity
import com.vitaltrack.app.R

class StepOneFragment : Fragment() {

    private var _binding: FragmentOnboardingStep1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE)
                    as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
            binding.etAge.clearFocus()

            if (validate()) {
                val activity = requireActivity() as OnboardingActivity
                activity.name = binding.etName.text.toString().trim()
                activity.age = binding.etAge.text.toString().toInt()
                activity.goToNextStep()
            }
        }
    }

    private fun validate(): Boolean {

        var isValid = true

        val name = binding.etName.text.toString().trim()
        val age = binding.etAge.text.toString().trim()

        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.error_field_required)
            isValid = false
        } else {
            binding.tilName.error = null
        }

        if (age.isEmpty()) {
            binding.tilAge.error = getString(R.string.error_field_required)
            isValid = false
        }

        else if (age.toIntOrNull() == null || age.toInt() < 1 || age.toInt() > 120){
            binding.tilAge.error = getString(R.string.error_field_required)
            isValid = false
        }

        else {
            binding.tilAge.error = null
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}