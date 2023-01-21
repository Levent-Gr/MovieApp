package com.leventgorgu.inviousgchallenge.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leventgorgu.inviousgchallenge.api.CinemaAPI
import com.leventgorgu.inviousgchallenge.model.movies.Movies
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(private var cinemaAPI: CinemaAPI):ViewModel() {

    private val _movies = MutableLiveData<Movies>()
    val movies:LiveData<Movies> = _movies

     fun searchMoviesFromAPI(movieTitle:String,page:String,apiKey:String){
        viewModelScope.launch(handler) {
            val response = cinemaAPI.searchMovies(movieTitle,page,apiKey)
            response.body()?.let { movies ->
                _movies.value = movies
            }
        }
    }

    fun setMovies(){
        _movies.value = Movies("true", listOf(),"0")
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }
}