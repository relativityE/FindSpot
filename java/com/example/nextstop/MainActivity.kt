package com.example.nextstop

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.facebook.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONException


class MainActivity : AppCompatActivity() {


    //set intent for results on new activity page
    private lateinit var sendintent : Intent

    //get reference to recycle view Id
    private lateinit var recycleViewId : RecyclerView

    //for some reason, you have to use a linear layout manager, set vertical,
    //to inject into recycle view
    private lateinit var layoutManager : LinearLayoutManager
    //val layoutManager = GridLayoutManager(this, 2)

    //access to data class
    private lateinit var dataHandle : DataStore

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ak_onCreate", "onCreate")
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        this.recycleViewId = findViewById(R.id.recycleViewId)
        this.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        this.sendintent = Intent(this, ResultsActivity::class.java)
        //set layout manager for recycler viewId
        recycleViewId.layoutManager = layoutManager

        dataHandle = DataStore.getInstance(this)
        Log.d("ak_Datastore","instantiated!!")

        //fb debug hooks
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.DEVELOPER_ERRORS)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_RAW_RESPONSES)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.GRAPH_API_DEBUG_INFO)

        //***********
        //LISTENER 1
        //***********
        //verify there's internet connection
        testbutton.setOnClickListener {
            //test connection
            //this.internetConnection()
            if (editTextLocation.text.isNotEmpty()) {
                //access facebook graph
                callFacebookAPI()
                Log.d("ak_search", "clear search box")
                editTextLocation.setText("")
            }
            else{
                Toast.makeText(this, "Please enter location", Toast.LENGTH_LONG ).show()
            }
        }

        //***********
        //LISTENER 2
        //***********
        getInfobutton.setOnClickListener {
            if (this.editTextLocation.text.isNotEmpty()) {
                Log.d("ak_input", "sendbtn pressed ${editTextLocation.text}")
                Toast.makeText(this, editTextLocation.text, Toast.LENGTH_LONG).show()
                Snackbar.make(myconstraintlayout, editTextLocation.text, Snackbar.LENGTH_LONG)
                    .show()
                //test w/ local data
                useTestData()
            }
            else{
                Toast.makeText(this, "Please enter location", Toast.LENGTH_LONG ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("ak_onStart", "OnStart")
    }

    override fun onPause() {
        super.onPause()
        Log.d("ak_onPause", "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d("ak_onResume", "onResume")
    }


    //======================
    //app specific functions
    //======================
    private fun internetConnection(){
        try {
            if (AppSupport.isNetworkConnected(this)) {
                Toast.makeText(this, "Connection Successful!", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Connection Failed!", Toast.LENGTH_LONG).show()
            }
        }
        catch (err: Exception){
            Toast.makeText(this, "Connection Failed!, Exception: $err", Toast.LENGTH_LONG).show()
        }
    }

    private fun useTestData(){

        //clear contents of class in case it was populated
        dataHandle.clearContent()

        dataHandle.apidata.apiFirst.addAll(dataHandle.countries)
        dataHandle.apidata.apiSecond.addAll(dataHandle.capitals)
        val stockimg = arrayOfNulls<String>(dataHandle.countries.size)
        stockimg.fill("stockphoto")
        stockimg.forEach {
            dataHandle.apidata.apiThird.add(it!!)
        }

        //instantiate recycle view adapter
        val recyclerAdapter = RecycleAdapter(
            ctx = this,
            apiData = dataHandle.apidata
        )
        recycleViewId.adapter = recyclerAdapter

        //consider moving this to separate class, instantiate new request queue from Volley Http library
        val queue = Volley.newRequestQueue(this)
        val urlMock = "http://www.mocky.io/v2/5dd4d8682f00007100d4fc54"

        //create http request
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, urlMock, null,
            Response.Listener { response ->
                Log.d("ak_mockyio","Response: $response")
                val param = "Response: %s".format(response.toString())
                sendintent.putExtra("result", param)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Error: %s".format(error.toString()), Toast.LENGTH_LONG).show()
            })
        //add new request to queue
        queue.add(jsonArrayRequest)
    }

    private fun callFacebookAPI() {
        //clear contents of class in case it was populated
        dataHandle.clearContent()

        //create empty arraylist to request permissions
        val permissions = ArrayList<String>(2)
        //get client token from developer portal (this token does not require user login)
        //can make requests to Facebook's PlaceManager after token set
        FacebookSdk.setClientToken(this@MainActivity.getString(R.string.fb_client_token))

        //log fb sdk status
        Log.d("ak_fb_init", "${FacebookSdk.isInitialized()}")
        val accessToken = AccessToken(
            this@MainActivity.getString(R.string.fb_access_token),
            this@MainActivity.getString(R.string.fb_app_id),
            this@MainActivity.getString(R.string.fb_app_uid), null,
            null, null, null,
            null,null, null)
        Log.d("ak_access_token", "Expired? ".format(accessToken.isExpired.toString()))
        Log.d("ak_access_token_uid", accessToken.userId.toString())

        val request = GraphRequest.newGraphPathRequest(
            accessToken,
            "/search"
        ) { response: GraphResponse? ->
            if (response?.error != null) {
                val errResponseMsg = response.error.errorMessage
                Toast.makeText(
                    this@MainActivity,
                    "response Error: %s".format(errResponseMsg),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val responseArr  = response!!.jsonObject.getJSONArray("data")
                Log.d("ak_response items", "${responseArr.length()}")
                for (index in 0 until responseArr.length()-1) {

                    //store api results in data store
                    dataHandle.apidata.apiFirst.add(responseArr.getJSONObject(index).getString("name"))
                    dataHandle.apidata.apiSecond.add(responseArr.getJSONObject(index).getString("checkins"))

                    try {
                        val photoUrl = responseArr.getJSONObject(index)
                            .getJSONObject("cover")
                            .getString("source")
                        dataHandle.apidata.apiThird.add(photoUrl)
                        }
                    catch (e: JSONException) {
                        Log.d("ak_imageStream", e.message.toString())
                        //image content not found
                        dataHandle.apidata.apiThird.add(resources.getString(R.string.stockphoto))
                        }
                    Log.d("ak_searchName", dataHandle.apidata.apiFirst[index])
                }

                //instantiate recycle view adapter
                val recyclerAdapter = RecycleAdapter(ctx = this@MainActivity, apiData = dataHandle.apidata)
                recycleViewId.adapter = recyclerAdapter
            }
        }

        val categories = ArrayList<String>()
        categories.add("food_beverage")
        //set search parameters
        val params: Bundle = Bundle().also { it: Bundle ->
            it.putString("q", "${editTextLocation.text}")
            it.putString("type", "place")
            it.putString("fields", "name,link,cover,checkins")
            //it.putString("categories", "[\"food_beverage\"]")
        }
        request.parameters = params
        Log.d("ak_request", "$request")
        //send request
        request.executeAsync()
    }
}
