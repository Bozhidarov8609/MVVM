package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.android.synthetic.main.fragment_search_news.paginationProgressBar
import kotlinx.coroutines.*


class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
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
                R.id.action_searchNewsFragment_to_articleFragment,

                bundle
            )
        }

        var job: Job?=null
      etSearch.addTextChangedListener { editable->
          job?.cancel()
          job= MainScope().launch {
              delay(SEARCH_NEWS_TIME_DELAY)
              editable?.let {
                  if(editable.toString().isNotEmpty()){
                      viewModel.getSearchNews(editable.toString())
                  }
              }


          }
      }


        viewModel.searchNews.observe(viewLifecycleOwner, Observer { responce->
            when(responce){
                is Resource.Success->{
                    higeProgresBar()
                    responce.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
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
    }
    private fun higeProgresBar(){
        paginationProgressBar.visibility=View.INVISIBLE
    }
    private fun showProgresBar(){
        paginationProgressBar.visibility=View.VISIBLE
    }
    private fun setupRecyclerView(){
        newsAdapter= NewsAdapter()
        rvSearchNews.apply {
            adapter=newsAdapter
            layoutManager= LinearLayoutManager(activity)
        }
    }
}