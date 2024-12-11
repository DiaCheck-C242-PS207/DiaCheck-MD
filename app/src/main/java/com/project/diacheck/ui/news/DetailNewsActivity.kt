package com.project.diacheck.ui.news

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.project.diacheck.R
import com.project.diacheck.data.Result
import com.project.diacheck.data.remote.response.News
import com.project.diacheck.databinding.ActivityDetailNewsBinding
import com.project.diacheck.ui.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class DetailNewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailNewsBinding
    private val newsModel: NewsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
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
            title = getString(R.string.detail_news)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val newsId = intent.getIntExtra(EXTRA_NEWS_ID, -1)
        if (newsId > 0) {
            observeNewsDetails(newsId)
        } else {
            Toast.makeText(this, "Invalid news ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeNewsDetails(newsId: Int) {
        newsModel.getNewsById(newsId).observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(true)
                    Log.d("DetailNewsActivity", "Data received: ${result.data}")
                    val news = result.data
                    if (news != null) {
                        showNewsDetails(news)
                        showLoading(false)
                    } else {
                        Toast.makeText(this, "News not found", Toast.LENGTH_SHORT).show()
                    }
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                    Log.e("DetailNewsActivity", "Error: ${result.error}")
                }
            }
        }
    }

    fun formatDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date ?: "")
        } catch (e: Exception) {
            inputDate
        }
    }

    private fun showNewsDetails(news: News) {
        Glide.with(binding.ivImage.context)
            .load(news.thumbnail)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.ic_error)
            .into(binding.ivImage)
        binding.tvTime.text = formatDate(news.created_at)
        binding.tvTitle.text = news.title
        binding.tvBody.text = HtmlCompat.fromHtml(news.body, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.linearProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_NEWS_ID = "extra_news_id"
    }
}
