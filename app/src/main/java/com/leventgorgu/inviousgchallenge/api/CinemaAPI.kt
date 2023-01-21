package com.leventgorgu.inviousgchallenge.api

import com.leventgorgu.inviousgchallenge.model.movieDetail.Movie
import com.leventgorgu.inviousgchallenge.model.movies.Movies
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CinemaAPI {
    @GET("/")
    suspend fun getMovie(
        @Query("t") movieTitle :String,
        @Query("apikey") apikey :String
    ): Response<Movie>

    @GET("/")
    suspend fun searchMovies(
        @Query("s") movieTitle :String,
        @Query("page") page:String,
        @Query("apikey") apikey :String
    ): Response<Movies>

}