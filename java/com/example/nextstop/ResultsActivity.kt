package com.example.nextstop

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_results.*
import kotlinx.android.synthetic.main.content_results.*

class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        setSupportActionBar(toolbar)

        //add data from another activity for display
        val sentdata = requireNotNull(intent?.getStringExtra("viewdata")) { "viewdata content is null" }
        resultstextView.text = sentdata

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Snackbar action goes here", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        backbutton.setOnClickListener {
            //end activity, nothing returned
            finish()
        }

        resultsbutton.setOnClickListener {
            val appdata = DataStore.getInstance(this)
            if (appdata.apidata.apiFirst.size > 0) {
                resultstextView.text = appdata.apidata.apiFirst.toString()
            }
            else {
                Toast.makeText(this, "No data available", Toast.LENGTH_LONG).show()
                resultstextView.text = appdata.countries.toArray().toString()
            }
        }
    }
}
