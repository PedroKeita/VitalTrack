package com.vitaltrack.app.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vitaltrack.app.R
import com.vitaltrack.app.databinding.FragmentProfileBinding
import com.vitaltrack.app.worker.HydrationReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        val prefs = requireContext().getSharedPreferences("vital_prefs", Context.MODE_PRIVATE)
        binding.switchReminders.isChecked = prefs.getBoolean("reminders_enabled", true)
        binding.etSleepHour.setText(prefs.getInt("sleep_hour", 22).toString())
        binding.etWakeHour.setText(prefs.getInt("wake_hour", 7).toString())

        binding.switchReminders.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("reminders_enabled", isChecked).apply()
            if (isChecked) {
                HydrationReminderScheduler.schedule(requireContext())
            } else {
                HydrationReminderScheduler.cancel(requireContext())
            }
        }

        binding.btnSave.setOnClickListener {
            if (validate()) {
                val sleepHour = binding.etSleepHour.text.toString().toIntOrNull() ?: 22
                val wakeHour = binding.etWakeHour.text.toString().toIntOrNull() ?: 7
                prefs.edit()
                    .putInt("sleep_hour", sleepHour)
                    .putInt("wake_hour", wakeHour)
                    .apply()
                showConfirmDialog()
            }
        }
    }



    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ProfileUiState.Success -> populateFields(state)
                    is ProfileUiState.Saved -> showSavedFeedback()
                    else -> {}
                }
            }
        }
    }

    private fun populateFields(state: ProfileUiState.Success) {
        val user = state.user
        binding.etName.setText(user.name)
        binding.etAge.setText(user.age.toString())
        binding.etWeight.setText(user.weight.toString())
        binding.etHeight.setText(user.height.toString())

        // seleciona o radio button correto
        val radioId = when (user.activityLevel) {
            "LIGHT"        -> binding.radioLight.id
            "MODERATE"     -> binding.radioModerate.id
            "ACTIVE"       -> binding.radioActive.id
            "VERY_ACTIVE"  -> binding.radioVeryActive.id
            else           -> binding.radioSedentary.id
        }
        binding.radioGroupActivity.check(radioId)

        // exibe metas calculadas
        binding.tvWaterGoal.text = "Meta de água: ${user.dailyWaterGoalMl}ml/dia"
        binding.tvStepsGoal.text = "Meta de passos: ${user.dailyStepsGoal} passos/dia"
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            if (validate()) {
                showConfirmDialog()
            }
        }
    }

    private fun showConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Salvar alterações")
            .setMessage("Deseja salvar as alterações no seu perfil?")
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Salvar") { _, _ -> saveUser() }
            .show()
    }

    private fun saveUser() {
        val activityLevel = when (binding.radioGroupActivity.checkedRadioButtonId) {
            binding.radioLight.id       -> "LIGHT"
            binding.radioModerate.id    -> "MODERATE"
            binding.radioActive.id      -> "ACTIVE"
            binding.radioVeryActive.id  -> "VERY_ACTIVE"
            else                        -> "SEDENTARY"
        }

        viewModel.updateUser(
            name = binding.etName.text.toString().trim(),
            age = binding.etAge.text.toString().toInt(),
            weight = binding.etWeight.text.toString().toFloat(),
            height = binding.etHeight.text.toString().toFloat(),
            activityLevel = activityLevel
        )
    }

    private fun validate(): Boolean {
        var isValid = true

        if (binding.etName.text.toString().trim().isEmpty()) {
            binding.tilName.error = getString(R.string.error_field_required)
            isValid = false
        } else binding.tilName.error = null

        if (binding.etAge.text.toString().trim().isEmpty()) {
            binding.tilAge.error = getString(R.string.error_field_required)
            isValid = false
        } else binding.tilAge.error = null

        if (binding.etWeight.text.toString().trim().isEmpty()) {
            binding.tilWeight.error = getString(R.string.error_field_required)
            isValid = false
        } else binding.tilWeight.error = null

        if (binding.etHeight.text.toString().trim().isEmpty()) {
            binding.tilHeight.error = getString(R.string.error_field_required)
            isValid = false
        } else binding.tilHeight.error = null

        return isValid
    }

    private fun showSavedFeedback() {
        com.google.android.material.snackbar.Snackbar
            .make(binding.root, "Perfil atualizado com sucesso!", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}