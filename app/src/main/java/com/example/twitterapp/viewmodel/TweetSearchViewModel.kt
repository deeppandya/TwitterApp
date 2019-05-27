package com.example.twitterapp.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.Handler
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.twitterapp.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class TweetSearchViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    internal var allSearchTweets: MutableLiveData<JSONArray> = MutableLiveData()
    internal var emptyData: MutableLiveData<Boolean> = MutableLiveData()

    private val requestQueue = Volley.newRequestQueue(getApplication())

    private val handler = Handler()

    companion object {
        const val TAG = "tag"
    }

    fun loadTweetData(query: String, language: String) {

        uiScope.launch {
            val searchApi =
                "${BuildConfig.TWITTER_SEARCH_ENDPOINT}?q=$query&count=100&lang=$language"

            callApi(searchApi)
        }
    }

    private fun callApi(twitterApi: String) {
        val req = object : JsonObjectRequest(Method.GET, twitterApi,
            null, Response.Listener<JSONObject> { response ->
                try {
                    val tweets: JSONArray = response.getJSONArray("statuses")

                    if (tweets.length() <= 0) {
                        emptyData.value = true
                    }

                    allSearchTweets.value = tweets

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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        requestQueue.cancelAll(TAG)
        handler.removeCallbacksAndMessages(null)
    }
}