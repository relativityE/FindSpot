package com.example.nextstop

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_results.*
import kotlinx.android.synthetic.main.content_results.*

class ResultsActivity : AppCompatActivity() {

    private var outstring = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        setSupportActionBar(toolbar)

        resultstextView.movementMethod = ScrollingMovementMethod()
        //add data from another activity for display
        val maindata = requireNotNull(intent?.getStringExtra("maindata")) { "maindata content is null" }
        val minordata = requireNotNull(intent?.getStringExtra("minordata")) { "maindata content is null" }
        val imgdata = requireNotNull(intent?.getStringExtra("imgdata")) { "maindata content is null" }
        textViewInfo.text = maindata + "\n" + minordata + "\n"
        Glide.with(resultsimageView)
            .load(imgdata)
            .placeholder(R.drawable.thinkbox)
            .into(resultsimageView)


        //*********
        //LISTENERS
        //*********
        backbutton.setOnClickListener {
            //end activity, nothing returned
            finish()
        }

        resultsbutton.setOnClickListener {
            val appdata = DataStore.getInstance(this)
            if (appdata.apidata.apiFirst.size > 0) {
                outstring = ""
                appdata.apidata.apiFirst.forEach { outstring += it + "\n" }
                resultstextView.text = outstring
            }
            else {
                Toast.makeText(this, "No data available", Toast.LENGTH_LONG).show()
                outstring = ""
                appdata.countries.forEach { outstring += it + "\n" }
                resultstextView.text = outstring
            }
        }
    }
}
