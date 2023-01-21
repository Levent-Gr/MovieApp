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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leventgorgu.inviousgchallenge.R
import com.leventgorgu.inviousgchallenge.adapter.MovieRecyclerAdapter
import com.leventgorgu.inviousgchallenge.api.CinemaAPI
import com.leventgorgu.inviousgchallenge.databinding.FragmentFeedBinding
import com.leventgorgu.inviousgchallenge.utils.Util
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var movieRecyclerAdapter : MovieRecyclerAdapter
    private val screenSizeHeight = getScreenHeight()
    private var movieTitle = ""
    private lateinit var retrofitCinemaAPI : CinemaAPI
    private var pageNumber = 1
    private var yPosition = 0F
    private var optimalYPosition = screenSizeHeight/4F
    private var screenPageNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retrofitCinemaAPI = Retrofit.Builder()
            .baseUrl(Util.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CinemaAPI::class.java)

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
        binding.movieFeedRecycler.layoutManager = GridLayoutManager(context,2, GridLayoutManager.VERTICAL,false)
        binding.movieFeedRecycler.adapter = movieRecyclerAdapter
        searchMovies(retrofitCinemaAPI)
    }

    override fun onResume() {
        super.onResume()
        binding.searchView.clearFocus()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                pageNumber=1
                movieTitle=newText!!
                searchMovies(retrofitCinemaAPI)
                return false
            }
        })
    }

    private fun searchMovies(retrofit: CinemaAPI) {
        CoroutineScope(Dispatchers.IO + handler).launch {
            val response = retrofit.searchMovies(movieTitle, pageNumber.toString(),Util.API_KEY)
            response.body()?.let { movies ->
                withContext(Dispatchers.Main + handler){
                    if(movies.Response=="True"){
                        binding.responseMessageTextView.visibility= View.INVISIBLE
                        if (pageNumber>1){
                            movieRecyclerAdapter.updateMoviesSearchData(movies.Search)
                        }else{
                            movieRecyclerAdapter.searchList = movies.Search
                            binding.movieFeedRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                    super.onScrolled(recyclerView, dx, dy)
                                    yPosition+=dy
                                    screenPageNumber = Math.round(yPosition/optimalYPosition)
                                    if(screenPageNumber>pageNumber){
                                        pageNumber++
                                        searchMovies(retrofit)
                                    }
                                }
                            })
                        }
                    }else if (movies.Response=="False" && movieTitle.isNotEmpty() && movieRecyclerAdapter.searchList.isEmpty())
                        binding.responseMessageTextView.visibility= View.VISIBLE
                }
            }
        }
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
    private val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }
    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}