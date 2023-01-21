package com.leventgorgu.inviousgchallenge.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.leventgorgu.inviousgchallenge.databinding.FeedMovieRowBinding
import com.leventgorgu.inviousgchallenge.model.movies.Movies
import com.leventgorgu.inviousgchallenge.model.movies.Search
import androidx.recyclerview.widget.ListAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.leventgorgu.inviousgchallenge.view.FeedFragmentDirections


class MovieRecyclerAdapter:RecyclerView.Adapter<MovieRecyclerAdapter.MovieViewHolder>() {

    class MovieViewHolder(val feedMovieRowBinding: FeedMovieRowBinding):RecyclerView.ViewHolder(feedMovieRowBinding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Search>() {
        override fun areItemsTheSame(oldItem: Search, newItem: Search): Boolean {
            return oldItem.imdbID == newItem.imdbID
        }

        override fun areContentsTheSame(oldItem: Search, newItem: Search): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerDiffer = AsyncListDiffer(this,diffCallback)


    var searchList:List<Search>
        get()= recyclerDiffer.currentList
        set(value) = recyclerDiffer.submitList(value)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {

        val feedMovieRowBinding = FeedMovieRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MovieViewHolder(feedMovieRowBinding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.feedMovieRowBinding.movieSearch = searchList[position]

        holder.itemView.setOnClickListener {
            val action = FeedFragmentDirections.actionFeedFragmentToDetailFragment()
            action.movieTitle = searchList[position].Title
            Navigation.findNavController(holder.itemView).navigate(action)
        }
    }


    override fun getItemCount(): Int {
        return searchList.size
    }

    fun updateMoviesSearchData(searchList:List<Search>){
        this.searchList = this.searchList + searchList
        notifyDataSetChanged()
    }
}