package com.example.marvelapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private lateinit var marvelList: MutableList<MarvelCharacter>
    private lateinit var rvMarvel: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val button = findViewById<Button>(R.id.marvelButton)
//        val imageView = findViewById<ImageView>(R.id.marvelImage)

        marvelList = mutableListOf()
        rvMarvel = findViewById(R.id.marvel_list)

        val adapter = MarvelAdapter(marvelList)
        rvMarvel.adapter = adapter
        rvMarvel.layoutManager = LinearLayoutManager(this@MainActivity)

        getNextImage()
    }

    private fun getNextImage() {
//            val randomCharacterId = 1011334 //(19000..20000).random()
//            getMarvelImageURL(randomCharacterId)

        for (characterId in 1011334 until 1011345) {
            getMarvelImageURL(characterId)
        }
    }

    private fun getMarvelImageURL(characterId: Int) {
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
                val dataObject = json.jsonObject.getJSONObject("data")

                val resultsArray = dataObject.getJSONArray("results")
                for (i in 0 until resultsArray.length()) {
                    val characterData = resultsArray.getJSONObject(i)
                    marvelImagePath = characterData.getJSONObject("thumbnail").getString("path")
                    imageExt = characterData.getJSONObject("thumbnail").getString("extension")

                    // Construct the full image URL
                    val imageUrl = "$marvelImagePath.$imageExt"

                    val name = characterData.getString("name")
                    val date = characterData.getString("modified")

                    val marvelCharacter = MarvelCharacter(imageUrl, name, date)

                    // Log the image URL
                    Log.d("Marvel", "Image URL: $imageUrl")

                    // Add the image URL to your marvelList for the RecyclerView
                    marvelList.add(marvelCharacter)
                }

                rvMarvel.adapter?.notifyDataSetChanged()

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

//    private fun loadImage(imageUrl: String, imageView: ImageView) {
//        Glide.with(this)
//            .load(imageUrl)
//            .fitCenter()
//            .into(imageView)
//    }
}
