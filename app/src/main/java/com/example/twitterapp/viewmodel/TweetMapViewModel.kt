package com.example.twitterapp.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.twitterapp.BuildConfig
import com.example.twitterapp.Utils
import com.example.twitterapp.model.TweetModel
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class TweetMapViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    internal var allRecentTweets: MutableLiveData<JSONArray> = MutableLiveData()

    internal var emptyData: MutableLiveData<Boolean> = MutableLiveData()

    private val requestQueue = Volley.newRequestQueue(getApplication())

    val handler = Handler()

    companion object {
        const val TAG = "tag"
    }

    init {
        allRecentTweets.value = JSONArray()
    }

    fun loadRecentTweetData(currentLatLng: LatLng, radius: Int, language: String) {

        uiScope.launch {
            val searchApi =
                "${BuildConfig.TWITTER_SEARCH_ENDPOINT}?geocode=${currentLatLng.latitude},${currentLatLng.longitude},${radius}km&result_type=recent&count=100&lang=$language"

            callApi(searchApi)
        }
    }

    fun loadRecentTweetDataPages(query: String) {
        uiScope.launch {
            val searchApi =
                "${BuildConfig.TWITTER_SEARCH_ENDPOINT}$query"

            callApi(searchApi)
        }
    }

    private fun callApi(twitterApi: String) {
        val req = object : JsonObjectRequest(Method.GET, twitterApi,
            null, Response.Listener<JSONObject> { response ->
                try {
                    var tweets: JSONArray = response.getJSONArray(Utils.STATUSES)

                    tweets = sort(tweets)

                    val searchMetadata: JSONObject? = response.getJSONObject(Utils.SEARCH_METADATA)
                    allRecentTweets.value = tweets

                    if (tweets.length() <= 0) {
                        emptyData.value = true
                    }

                    if (searchMetadata?.getString(Utils.NEXT_RESULTS) != null) {
                        handler.postDelayed({
                            searchMetadata.getString(Utils.NEXT_RESULTS)?.let {
                                loadRecentTweetDataPages(it)
                                Log.i("next_results", it)
                            }
                        }, 5000)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                VolleyLog.d("Error", "Error: " + error.message)
                Toast.makeText(getApplication(), "" + error.message, Toast.LENGTH_SHORT).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] =
                    "Bearer ${BuildConfig.TWITTER_BEARER_ACCESS_TOKEN}"
                return headers
            }
        }

        req.tag = TAG

        /* Add your Requests to the RequestQueue to execute */
        requestQueue?.add(req)
    }

    private fun sort(tweets: JSONArray): JSONArray {


        val type = object : TypeToken<ArrayList<TweetModel>>() {}.type
        val tweetList: ArrayList<TweetModel> = Gson().fromJson(tweets.toString(), type)

        tweetList.sortWith(compareBy({ it.createdAt }, { it.createdAt }))

        val tweetsJsonArray = Gson().toJsonTree(tweetList).asJsonArray
        return JSONArray(tweetsJsonArray.toString())
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        requestQueue.cancelAll(TAG)
        handler.removeCallbacksAndMessages(null)
    }
}