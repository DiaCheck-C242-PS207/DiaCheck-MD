package com.project.diacheck.ui.news

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.diacheck.data.Result
import com.project.diacheck.data.remote.response.ListNewsItem
import com.project.diacheck.databinding.FragmentNewsBinding
import com.project.diacheck.ui.ViewModelFactory
import com.project.diacheck.ui.adapter.NewsAdapter
import kotlinx.coroutines.launch

class NewsFragment : Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter
    private val newsViewModel: NewsViewModel by viewModels {
        ViewModelFactory.getInstance(
            requireActivity()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchView.isIconified = false
        observerNews()
        setupRecylerView()
        setupSearchView()
    }

    private fun setupSearchView() {

        binding.searchView.apply {
            setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrEmpty()) {
                        lifecycleScope.launch {
                            observeSearchNews(query)
                        }
                    } else {
                        lifecycleScope.launch {
                            observerNews()
                            setupRecylerView()
                        }
                    }
                    clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty()) {
                        lifecycleScope.launch {
                            observerNews()
                        }
                    }
                    return true
                }
            })
        }

    }

    private suspend fun observeSearchNews(query: String) {
        newsViewModel.searchNews(query).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    result.data.let { newsAdapter.submitList(it) } ?: showError()
                    binding.rvNews.visibility = View.VISIBLE
                }

                is Result.Error -> {
                    showLoading(false)
                    showError()
                    binding.tvNoEvent.visibility = View.VISIBLE
                    binding.rvNews.visibility = View.GONE
                }
            }
        }
    }

    private fun setupRecylerView() {
        newsAdapter = NewsAdapter { navigateToDetailNews(it) }
        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }
    }

    private fun navigateToDetailNews(news: ListNewsItem) {
        if (news.id_article > 0) {
            val intent = Intent(requireContext(), DetailNewsActivity::class.java).apply {
                putExtra(DetailNewsActivity.EXTRA_NEWS_ID, news.id_article)
            }
            startActivity(intent)
        } else {
            Log.e("NewsFragment", "Invalid news ID: ${news.id_article}")
        }
    }


    private fun observerNews() {
        newsViewModel.findNews().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    result.data.let { newsAdapter.submitList(it) } ?: showError()
                }

                is Result.Error -> {
                    showLoading(false)
                    showError()
                }
            }
        }
    }

    private fun showError() {
        binding.linearProgressBar.visibility = View.GONE
        binding.rvNews.visibility = View.GONE
        binding.tvNoEvent.visibility = View.VISIBLE
    }

    private fun showLoading(isLoading: Boolean) {
        binding.linearProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tvNoEvent.visibility = if (isLoading) View.GONE else binding.tvNoEvent.visibility
    }
}