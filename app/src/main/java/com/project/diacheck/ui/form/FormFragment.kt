package com.project.diacheck.ui.form

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.diacheck.data.Result
import com.project.diacheck.data.local.entity.HistoryEntity
import com.project.diacheck.data.remote.response.ListFormItem
import com.project.diacheck.databinding.FragmentFormBinding
import com.project.diacheck.ui.ViewModelFactory
import com.project.diacheck.ui.adapter.FormAdapter
import com.project.diacheck.ui.detail.AddFormActivity
import com.project.diacheck.ui.detail.DetailActivity

class FormFragment : Fragment() {

    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!
    private val formAdapter: FormAdapter by lazy {
        FormAdapter { navigateToDetailForm(it) }
    }
    private val formViewModel: FormViewModel by viewModels {
        ViewModelFactory.getInstance(
            requireActivity()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddFormActivity::class.java)
            startActivity(intent)
        }
        formViewModel.getSession().observe(viewLifecycleOwner) { user ->
            val userId = user.id_users
            observerFormsByUserId(userId)
            setupRecyclerView()
        }
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = formAdapter
        }
    }

    private fun navigateToDetailForm(form: ListFormItem) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_FORM_ID, form.id_history)
            putExtra("input_age", form.age)
            putExtra("input_gender", form.gender)
            putExtra("input_hypertension", form.hypertension)
            putExtra("input_heartDisease", form.heart_disease)
            putExtra("input_bmi", form.bmi)
            putExtra("input_hbA1c", form.hbA1c)
            putExtra("input_bloodGlucose", form.blood_glucose)
            putExtra("prediction", form.result)
            putExtra("prediction_message", form.history)
        }
        startActivity(intent)
    }


    private fun observerFormsByUserId(userId: Int) {
        formViewModel.findFormByUserId(userId)
        formViewModel.formResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    updateFormList(result.data)
                }
                is Result.Error -> showError(result.error)
            }
        }
    }

    private fun updateFormList(formData: List<ListFormItem>) {
        if (formData.isEmpty()) {
            binding.tvNoEvent.visibility = View.VISIBLE
            binding.rvStory.visibility = View.GONE
        } else {
            binding.tvNoEvent.visibility = View.GONE
            binding.rvStory.visibility = View.VISIBLE
            formAdapter.submitList(formData)
        }
    }

    private fun showError(error: String) {
        binding.linearProgressBar.visibility = View.GONE
        binding.tvNoEvent.visibility = View.VISIBLE
        binding.tvNoEvent.text = error
    }

    private fun showLoading(isLoading: Boolean) {
        binding.linearProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvStory.visibility = if (isLoading) View.GONE else binding.rvStory.visibility
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
