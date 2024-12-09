package com.project.diacheck.ui.news

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.project.diacheck.R
import com.project.diacheck.data.Result
import com.project.diacheck.data.local.entity.NewsEntity
import com.project.diacheck.data.remote.response.ListNewsItem
import com.project.diacheck.databinding.ActivityDetailNewsBinding

class DetailNewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailNewsBinding
    private lateinit var newsModel: NewsViewModel

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
            title = getString(R.string.detail_news)
        }

        val newsId = intent.getIntExtra(EXTRA_NEWS_ID, -1)
        if (newsId != -1) {
            observeNewsDetails(newsId)
        } else {
            Toast.makeText(this, "Invalid news ID", Toast.LENGTH_SHORT).show()
        }

    }

    private fun observeNewsDetails(newsId: Int) {
        newsModel.getNewsById(newsId).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.linearProgressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.linearProgressBar.visibility = View.GONE
                    val news = result.data
                    showNewsDetails(news)
                }

                is Result.Error -> {
                    binding.linearProgressBar.visibility = View.GONE
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showNewsDetails(news: ListNewsItem) {
        binding.tvTitle.text = news.title
        binding.tvBody.text = HtmlCompat.fromHtml(news.content, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    companion object {
        const val EXTRA_NEWS_ID = "extra_news_id"
    }
}