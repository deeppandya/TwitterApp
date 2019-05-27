package com.example.twitterapp.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.widget.ContentLoadingProgressBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.example.twitterapp.R
import com.example.twitterapp.adapter.TweetSearchAdapter
import com.example.twitterapp.model.TweetModel
import com.example.twitterapp.viewmodel.TweetSearchViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class TweetSearchActivity : AppCompatActivity(), TextView.OnEditorActionListener, TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        Log.i("TweetSearchActivity", "afterTextChanged")
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        Log.i("TweetSearchActivity", "beforeTextChanged")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        Log.i("TweetSearchActivity", "onTextChanged")
        if (s != null) {
            if (s.length > 3) {

                var query = s.toString()

                if (query.startsWith('#')) {
                    query = query.replace("#", "%23")
                }

                tweetSearchViewModel.loadTweetData(query)
            }
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        return when (actionId) {
            EditorInfo.IME_ACTION_SEARCH -> {
                loadingProgress?.visibility = View.VISIBLE
                tweetSearchViewModel.loadTweetData(etSearchView.text.toString())
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(etSearchView.windowToken, 0)
                true
            }
            else -> {
                false
            }
        }
    }

    private var loadingProgress: ContentLoadingProgressBar? = null
    private lateinit var tweetSearchAdapter: TweetSearchAdapter
    private lateinit var tweetSearchViewModel: TweetSearchViewModel
    private lateinit var rvTweets: RecyclerView
    private lateinit var etSearchView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweet_search)

        tweetSearchViewModel = ViewModelProviders.of(this).get(TweetSearchViewModel::class.java)

        tweetSearchAdapter = TweetSearchAdapter(this)

        findViews()
        setUpViews()

        tweetSearchViewModel.allSearchTweets.observe(this, Observer {

            val type = object : TypeToken<ArrayList<TweetModel>>() {}.type
            val tweetList: ArrayList<TweetModel> = Gson().fromJson(it.toString(), type)

            tweetSearchAdapter.tweetList = tweetList
            tweetSearchAdapter.notifyDataSetChanged()
            loadingProgress?.visibility = View.GONE
        })
    }

    private fun findViews() {
        etSearchView = findViewById(R.id.etSearchView)
        rvTweets = findViewById(R.id.recyclerView)
        loadingProgress = findViewById(R.id.loadingProgress)
    }

    private fun setUpViews() {
        etSearchView.setOnEditorActionListener(this)
//        etSearchView.addTextChangedListener(this)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        rvTweets.setHasFixedSize(true)
        rvTweets.itemAnimator = DefaultItemAnimator()
        rvTweets
            .addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter = tweetSearchAdapter
    }
}
