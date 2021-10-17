package com.example.top10downloaderapp

import AppsAdapter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.lang.Runnable
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"

    private var appsList = mutableListOf<App>()
    lateinit var recyclerView: RecyclerView

    lateinit var progressBar: ProgressBar
    lateinit var progressBarTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialize recyclerView
        recyclerView = findViewById(R.id.recycler_view)

        progressBar = findViewById(R.id.progressBar)
        progressBarTextView = findViewById(R.id.progressBarText)
        setProgressBar(false)
        title = "Top Apps"
        CoroutineScope(IO).launch {
            //get xml data
            fetchData(10)
        }
    }

    private suspend fun fetchData(number: Int) {
        title = "Top 10 Apps"

        setProgressBar(true)
        try {
            val parser = XmlParser()
            val url =
                URL("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=$number/xml")
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            appsList = withContext(Dispatchers.Default) {
                parser.parse(
                    urlConnection.inputStream
                )
            } as MutableList<App>
            withContext(Main) {
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                recyclerView.adapter = AppsAdapter(appsList, this@MainActivity)
                recyclerView.adapter?.notifyDataSetChanged()
            }
            setProgressBar(false)
        } catch (e: Exception) {
            setProgressBar(false)
            this@MainActivity.runOnUiThread(Runnable {
                Toast.makeText(
                    this@MainActivity,
                    "Something went wrong, try again",
                    Toast.LENGTH_SHORT
                ).show()
            })
            Log.d(tag, e.message.toString())

        }
    }

    private fun setProgressBar(visibility: Boolean) {
        progressBar.isVisible = visibility
        progressBarTextView.isVisible = visibility
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_top_10 -> {
//                fetchData(10)
//                Toast.makeText(this, "10", Toast.LENGTH_SHORT).show()
//                return true
//            }
//            R.id.menu_top_100 -> {
//                fetchData(100)
//                Toast.makeText(this, "100", Toast.LENGTH_SHORT).show()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }


}