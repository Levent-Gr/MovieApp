package com.leventgorgu.inviousgchallenge.model.movies

data class Movies(
    val Response: String,
    var Search: List<Search>,
    val totalResults: String
)