package com.project.diacheck.ui.form

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.diacheck.data.Result
import com.project.diacheck.data.local.entity.HistoryEntity
import com.project.diacheck.databinding.FragmentFormBinding
import com.project.diacheck.ui.ViewModelFactory
import com.project.diacheck.ui.adapter.FormAdapter
import com.project.diacheck.ui.detail.AddFormActivity
import com.project.diacheck.ui.detail.DetailActivity

class FormFragment : Fragment() {

    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var formAdapter: FormAdapter
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
            observerFormsByUserId(userId.toString())
            setupRecylerView()
        }
    }

    private fun setupRecylerView() {
        formAdapter = FormAdapter { navigateToDetailForm(it) }
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = formAdapter
        }
    }

    private fun navigateToDetailForm(form: HistoryEntity) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_FORM_ID, form.id.toString())
        }
        startActivity(intent)
    }

    private fun observerFormsByUserId(userId: String) {
        formViewModel.findFormByUserId(userId).observe(viewLifecycleOwner) { result ->
            handleResult(result)
        }
    }


    private fun handleResult(result: Result<List<HistoryEntity>>) {
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Success -> {
                showLoading(false)
                updateFormList(result.data)
            }

            is Result.Error -> showError()
        }
    }

    private fun showError() {
        binding.linearProgressBar.visibility = View.GONE
        binding.rvStory.visibility = View.GONE
        binding.tvNoEvent.visibility = View.VISIBLE
    }

    private fun updateFormList(formData: List<HistoryEntity>) {
        if (formData.isEmpty()) {
            binding.tvNoEvent.visibility = View.VISIBLE
            binding.rvStory.visibility = View.GONE
        } else {
            binding.tvNoEvent.visibility = View.GONE
            binding.rvStory.visibility = View.VISIBLE
            formAdapter.submitList(formData)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.linearProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvStory.visibility = if (isLoading) View.GONE else binding.rvStory.visibility
    }

}
