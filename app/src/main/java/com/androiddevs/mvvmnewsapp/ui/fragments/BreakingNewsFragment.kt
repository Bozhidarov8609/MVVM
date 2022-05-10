package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.util.Resource

import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= (activity as NewsActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }
        Log.d("qwe", "1")
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { responce->
            when(responce){
                is Resource.Success->{
                    higeProgresBar()
                    responce.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                        Log.d("qwe", "2")
                    }
                }
                is Resource.Error->{
                    higeProgresBar()
                    responce.message?.let { message->
                        Log.e("M","$message")
                    }
                }
                is Resource.Loading->{
                    showProgresBar()
                }
            }

        })
        Log.d("qwe", "3")
    }
    private fun higeProgresBar(){
        paginationProgressBar.visibility=View.INVISIBLE
        Log.d("qwe", "4")
    }
    private fun showProgresBar(){
        paginationProgressBar.visibility=View.VISIBLE
        Log.d("qwe", "5")
    }
    private fun setupRecyclerView(){
        newsAdapter= NewsAdapter()


        rvBreakingNews.apply {
            adapter=newsAdapter
            layoutManager=LinearLayoutManager(activity)
        }
        Log.d("qwe", "6")
    }
}