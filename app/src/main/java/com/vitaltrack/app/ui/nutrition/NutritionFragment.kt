package com.vitaltrack.app.ui.nutrition

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vitaltrack.app.databinding.FragmentNutritionBinding
import com.vitaltrack.app.ui.activity.ScannerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NutritionFragment : Fragment() {

    private var _binding: FragmentNutritionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NutritionViewModel by viewModels()
    private val adapter = MealAdapter { viewModel.deleteMeal(it) }

    private val scannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val text = result.data?.getStringExtra("scanned_text") ?: ""
            val nutrition = viewModel.parseScannedText(text)
            showConfirmDialog(nutrition)
        } else {
            // fallback para manual
            showManualDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNutritionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMeals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@NutritionFragment.adapter
        }

        binding.btnScan.setOnClickListener {
            scannerLauncher.launch(
                Intent(requireContext(), ScannerActivity::class.java)
            )
        }

        binding.btnManual.setOnClickListener {
            showManualDialog()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                viewModel.meals.collect { adapter.submitList(it) }
            }
            launch {
                viewModel.totalCalories.collect { total ->
                    binding.tvTotalCalories.text = "$total kcal"
                }
            }
        }
    }

    private fun showConfirmDialog(nutrition: ScannedNutrition) {
        val layout = android.widget.LinearLayout(requireContext())
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(48, 24, 48, 0)

        val etDescription = EditText(requireContext()).apply { hint = "Descrição" }
        val etCalories = EditText(requireContext()).apply {
            hint = "Calorias"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(if (nutrition.calories > 0) nutrition.calories.toString() else "")
        }
        val etProtein = EditText(requireContext()).apply {
            hint = "Proteínas (g)"
            inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(if (nutrition.protein > 0) nutrition.protein.toString() else "")
        }
        val etCarbs = EditText(requireContext()).apply {
            hint = "Carboidratos (g)"
            inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(if (nutrition.carbs > 0) nutrition.carbs.toString() else "")
        }
        val etFat = EditText(requireContext()).apply {
            hint = "Gorduras (g)"
            inputType = android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(if (nutrition.fat > 0) nutrition.fat.toString() else "")
        }

        val spinner = Spinner(requireContext())
        val categories = listOf("Café da manhã", "Almoço", "Jantar", "Lanche")
        spinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        layout.addView(etDescription)
        layout.addView(spinner)
        layout.addView(etCalories)
        layout.addView(etProtein)
        layout.addView(etCarbs)
        layout.addView(etFat)

        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar dados nutricionais")
            .setView(layout)
            .setPositiveButton("Salvar") { _, _ ->
                viewModel.saveMeal(
                    description = etDescription.text.toString().ifEmpty { "Refeição" },
                    category = spinner.selectedItem.toString(),
                    calories = etCalories.text.toString().toIntOrNull() ?: 0,
                    protein = etProtein.text.toString().toFloatOrNull() ?: 0f,
                    carbs = etCarbs.text.toString().toFloatOrNull() ?: 0f,
                    fat = etFat.text.toString().toFloatOrNull() ?: 0f
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showManualDialog() {
        showConfirmDialog(ScannedNutrition(0, 0f, 0f, 0f))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}