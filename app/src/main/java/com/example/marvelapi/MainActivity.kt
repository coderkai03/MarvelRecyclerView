package com.example.marvelapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    var marvelImagePath = ""
    var imageExt = ""
    val privateKey = "7205ffc67142c9e88421375ed238dd9b88fec874"
    val publicKey = "e32c8e1389e6a11e495b92a33e7060fd"
    val timestamp = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.marvelButton)
        val imageView = findViewById<ImageView>(R.id.marvelImage)

        getNextImage(button, imageView)
    }

    private fun getNextImage(button: Button, imageView: ImageView) {
        button.setOnClickListener {
            val randomCharacterId = 1011334 //(19000..20000).random()
            getMarvelImageURL(imageView, randomCharacterId)
        }
    }

    private fun getMarvelImageURL(imageView: ImageView, characterId: Int) {
        val characterUrl =
            "https://gateway.marvel.com/v1/public/characters/$characterId?ts=$timestamp&apikey=$publicKey&hash=${generateHash(timestamp)}"

        val client = AsyncHttpClient()

        client[characterUrl, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JsonHttpResponseHandler.JSON
            ) {
                Log.d("Marvel", "response successful$json")
                if (json.jsonObject.has("data")) {
                    val dataObject = json.jsonObject.getJSONObject("data")
                    if (dataObject.has("results")) {
                        val resultsArray = dataObject.getJSONArray("results")
                        if (resultsArray.length() > 0) {
                            val characterData = resultsArray.getJSONObject(0)
                            marvelImagePath = characterData.getJSONObject("thumbnail").getString("path")
                            imageExt = characterData.getJSONObject("thumbnail").getString("extension")

                            val imageUrl = "$marvelImagePath.$imageExt"
                            loadImage(imageUrl, imageView)
                        } else {
                            Log.d("Marvel", "No character data found")
                        }
                    } else {
                        Log.d("Marvel", "No results array in the JSON response")
                    }
                } else {
                    Log.d("Marvel", "No data object in the JSON response")
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Marvel Error", errorResponse)
                // Handle API request failure here (e.g., show an error message to the user)
            }
        }]
    }

    private fun generateHash(timestamp: Long): String {
        val input = "$timestamp$privateKey$publicKey"
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        val result = StringBuilder()

        for (byte in digest) {
            result.append(String.format("%02x", byte))
        }

        return result.toString()
    }

    private fun loadImage(imageUrl: String, imageView: ImageView) {
        Glide.with(this)
            .load(imageUrl)
            .fitCenter()
            .into(imageView)
    }
}
