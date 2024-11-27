package com.capstone.diacheck.ui.news

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.diacheck.R
import com.capstone.diacheck.data.Result
import com.capstone.diacheck.data.local.entity.NewsEntity
import com.capstone.diacheck.databinding.ActivityDetailNewsBinding
import com.capstone.diacheck.ui.ViewModelFactory

class DetailNewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailNewsBinding
    private var currentEvent: NewsEntity? = null
    private val detailViewModel by viewModels<DetailNewsViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.white))

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(
                ContextCompat.getDrawable(
                    this@DetailNewsActivity,
                    R.drawable.ic_arrow_back
                )
            )
            title = getString(R.string.title_news)
        }

        val newsId = intent.getStringExtra(EXTRA_NEWS_ID)

        if (newsId != null) {
            observeNewsDetails(newsId)
        }

    }

    private fun observeNewsDetails(newsId: String) {
        detailViewModel.getDetailNews(newsId).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.linearProgressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.linearProgressBar.visibility = View.GONE
                    val news = result.data
                    currentEvent = news
                    populateEventDetails(news)
                }

                is Result.Error -> {
                    binding.linearProgressBar.visibility = View.GONE
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateEventDetails(news: NewsEntity) {
        binding.tvTitle.text = news.title
        binding.tvDate.text = news.date
        binding.tvBody.text = HtmlCompat.fromHtml(news.body, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_NEWS_ID = "extra_news_id"
    }
}