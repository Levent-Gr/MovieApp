package com.leventgorgu.inviousgchallenge.view

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.leventgorgu.inviousgchallenge.R
import com.leventgorgu.inviousgchallenge.api.CinemaAPI
import com.leventgorgu.inviousgchallenge.databinding.FragmentDetailBinding
import com.leventgorgu.inviousgchallenge.databinding.FragmentFeedBinding
import com.leventgorgu.inviousgchallenge.utils.Util
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var movieTitle = ""
    private lateinit var retrofitCinemaAPI : CinemaAPI


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retrofitCinemaAPI = Retrofit.Builder()
            .baseUrl(Util.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CinemaAPI::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        arguments?.let {
            movieTitle = DetailFragmentArgs.fromBundle(it).movieTitle
        }
        val activity = activity as AppCompatActivity?
        val actionBar: ActionBar? = activity!!.supportActionBar
        actionBar!!.title = movieTitle

        return view
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO + handler).launch {
            val response = retrofitCinemaAPI.getMovie(movieTitle,Util.API_KEY)
            response.body()!!.let {
                withContext(Dispatchers.Main){
                    binding.movieDetail = it
                }
            }
        }

    }
    private val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}