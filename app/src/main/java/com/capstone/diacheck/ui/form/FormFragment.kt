package com.capstone.diacheck.ui.form

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.diacheck.R
import com.capstone.diacheck.ui.detail.AddFormActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator

class FormFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var addButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_form, container, false)

        // Initialize views
        recyclerView = view.findViewById(R.id.rvStory)
        progressBar = view.findViewById(R.id.linearProgressBar)
        addButton = view.findViewById(R.id.btn_add)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = DummyAdapter() // Replace with your actual adapter

        addButton.setOnClickListener {
            val intent = Intent(requireContext(), AddFormActivity::class.java)
            startActivity(intent)
        }

        // Simulate loading
        showLoading(true)
        // Simulate data load
        recyclerView.postDelayed({
            showLoading(false)
        }, 2000) // Replace with actual data loading logic
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    // Dummy adapter for testing
    inner class DummyAdapter : RecyclerView.Adapter<DummyAdapter.DummyViewHolder>() {
        private val items = List(10) { "Item $it" }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DummyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return DummyViewHolder(view)
        }

        override fun onBindViewHolder(holder: DummyViewHolder, position: Int) {
            (holder.itemView as? android.widget.TextView)?.text = items[position]
        }

        override fun getItemCount(): Int = items.size

        inner class DummyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}
