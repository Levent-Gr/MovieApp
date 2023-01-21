package com.leventgorgu.inviousgchallenge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leventgorgu.inviousgchallenge.api.CinemaAPI
import com.leventgorgu.inviousgchallenge.model.movieDetail.Movie
import com.leventgorgu.inviousgchallenge.model.movies.Movies
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val cinemaAPI: CinemaAPI):ViewModel() {

    private val _movie = MutableLiveData<Movie>()
    val movie: LiveData<Movie> = _movie

    fun getMovieFromAPI(movieTitle:String,apiKey:String){
        viewModelScope.launch(handler) {
            val response = cinemaAPI.getMovie(movieTitle,apiKey)
            response.body()?.let { movie ->
                _movie.value = movie
            }
        }
    }
    private val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }
}