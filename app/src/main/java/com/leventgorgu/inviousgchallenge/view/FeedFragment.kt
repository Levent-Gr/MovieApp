package com.leventgorgu.inviousgchallenge.view

import android.app.AlertDialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leventgorgu.inviousgchallenge.R
import com.leventgorgu.inviousgchallenge.adapter.MovieRecyclerAdapter
import com.leventgorgu.inviousgchallenge.api.CinemaAPI
import com.leventgorgu.inviousgchallenge.databinding.FragmentFeedBinding
import com.leventgorgu.inviousgchallenge.model.movies.Movies
import com.leventgorgu.inviousgchallenge.model.movies.Search
import com.leventgorgu.inviousgchallenge.utils.Util
import com.leventgorgu.inviousgchallenge.viewmodel.FeedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var movieRecyclerAdapter : MovieRecyclerAdapter
    private var movieTitle = ""
    private val feedViewModel : FeedViewModel by viewModels()
    private var pageNumber = 1
    private var totalItem = 0
    private var visibleThreshold = 2
    private lateinit var gridLayoutManager:GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieRecyclerAdapter = MovieRecyclerAdapter()
        showLoadingAlert()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root
        val activity = activity as AppCompatActivity?
        val actionBar: ActionBar? = activity!!.supportActionBar
        actionBar!!.title = "Cinema"
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridLayoutManager = GridLayoutManager(context,2, GridLayoutManager.VERTICAL,false)
        binding.movieFeedRecycler.layoutManager = gridLayoutManager
        binding.movieFeedRecycler.adapter = movieRecyclerAdapter
    }

    override fun onResume() {
        super.onResume()
        feedViewModel.searchMoviesFromAPI(movieTitle,pageNumber.toString(),Util.API_KEY)
        subscribeObserve()

        binding.searchView.clearFocus()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                pageNumber=1
                movieTitle=newText!!
                feedViewModel.searchMoviesFromAPI(movieTitle,pageNumber.toString(),Util.API_KEY)
                return false
            }
        })
    }

    private fun searchMovies(movies: Movies){
        if(movies.Response=="True"){
            binding.responseMessageTextView.visibility= View.INVISIBLE
            if (pageNumber==1){
                totalItem= movies.Search.size
                feedViewModel.setMovies()
                movieRecyclerAdapter.searchList = movies.Search
                pageNumber=2
                recyclerScrollListener(movies)
            }else if(pageNumber>1){
                feedViewModel.setMovies()
                movieRecyclerAdapter.updateMoviesSearchData(movies.Search)
                recyclerScrollListener(movies)
            }
        }else if (movies.Response=="False" && movieTitle.isNotEmpty() && movieRecyclerAdapter.searchList.isEmpty()){
            binding.responseMessageTextView.visibility= View.VISIBLE}
    }

    private fun recyclerScrollListener(movies:Movies) {
        binding.movieFeedRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                var lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition()
                if (totalItem < (lastVisibleItem + visibleThreshold)) {
                    feedViewModel.searchMoviesFromAPI(movieTitle,pageNumber.toString(),Util.API_KEY)
                    pageNumber++
                    totalItem += movies.Search.size
                }
            }
        })
    }


    private fun showLoadingAlert() {
        val loadingDialog = LayoutInflater.from(context).inflate(R.layout.loading,null)
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setView(loadingDialog)
        val progressBar = loadingDialog.findViewById<ProgressBar>(R.id.progressBar)
        CoroutineScope(Dispatchers.IO).launch {
            for (i in 1..100 step 1) {
                delay(10)
                withContext(Dispatchers.Main) {
                    progressBar.progress = i
                    if (progressBar.progress==100)
                        alertDialog.dismiss()
                }
            }
        }
        alertDialog.show()
    }

    private fun subscribeObserve(){
        feedViewModel.movies.observe(viewLifecycleOwner, Observer {
            searchMovies(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}