package com.project.diacheck.ui.news

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.diacheck.data.Result
import com.project.diacheck.data.local.entity.NewsEntity
import com.project.diacheck.databinding.FragmentNewsBinding
import com.project.diacheck.ui.ViewModelFactory
import com.project.diacheck.ui.adapter.NewsAdapter

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

        observerNews()
        setupRecylerView()
        setupSearchView()
    }

    private fun setupSearchView() {
        binding.searchView.apply {
            visibility = View.VISIBLE
            setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
//                    if (query != null) {
//                        observerNews()
//                    }
                    observeSearchNews(query.orEmpty())
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    observeSearchNews(newText.orEmpty())
                    return true
                }
            })
        }
    }

    private fun observeSearchNews(query: String) {
        newsViewModel.searchNews(query).observe(viewLifecycleOwner) { result ->
            handleResult(result)
        }
    }

    private fun setupRecylerView() {
        newsAdapter = NewsAdapter { navigateToDetailNews(it) }
        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }
    }

    private fun navigateToDetailNews(news: NewsEntity) {
        val intent = Intent(requireContext(), DetailNewsActivity::class.java).apply {
            putExtra(DetailNewsActivity.EXTRA_NEWS_ID, news.id.toString())
        }
        startActivity(intent)
    }

    private fun observerNews() {
        newsViewModel.findNews().observe(viewLifecycleOwner) { result ->
            handleResult(result)
        }
    }

    private fun handleResult(result: Result<List<NewsEntity>>) {
        when (result) {
            is Result.Loading -> showLoading(true)
            is Result.Success -> {
                showLoading(false)
                updateNewsList(result.data)
            }

            is Result.Error -> showError()
        }
    }

    private fun updateNewsList(newsList: List<NewsEntity>) {
        if (newsList.isEmpty()) {
            binding.tvNoEvent.visibility = View.VISIBLE
            binding.rvNews.visibility = View.GONE
        } else {
            binding.tvNoEvent.visibility = View.GONE
            binding.rvNews.visibility = View.VISIBLE
            newsAdapter.submitList(newsList)
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