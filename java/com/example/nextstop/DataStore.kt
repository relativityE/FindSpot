package com.example.nextstop

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log


// using singleton design pattern to ensure common class
// with same data is accessible via only handle
class DataStore private constructor (val ctx: Context)  {

    val countries: ArrayList<String> = ArrayList()
    val capitals : ArrayList<String> = ArrayList()
    val apidata  : ModelContent      = ModelContent()

    init {
        println(message = "initializing DataStore")
        Log.d("ak_DataStore", "initializing canned data")
        countries.addAll(ctx.resources.getStringArray(R.array.Countries))
        capitals.addAll(ctx.resources.getStringArray(R.array.Capitals))
    }

    //static object
    companion object {
        //make sure any thread updates it's local copy
        @Volatile private var mInstance : DataStore? = null

        fun getInstance(ctx: Context) : DataStore {
            if (mInstance == null) {
                mInstance = createInstance(ctx)
            }
            return this.mInstance!!
        }

        //double-check locking
        @Synchronized
        private fun createInstance(ctx: Context) : DataStore { //make thread-safe
            return DataStore(ctx)
        }
    }

    //clear content as needed
    fun clearContent() {
        this.apidata.apiFirst.clear()
        this.apidata.apiSecond.clear()
        this.apidata.apiThird.clear()
    }

    //data class requires at least one parameter
    //Model for each view in Recycler view
    data class ModelContent(val apiFirst : ArrayList<String> = ArrayList(),
                            val apiSecond: ArrayList<String> = ArrayList(),
                            val apiThird : ArrayList<String> = ArrayList()
    ) : Parcelable {

        constructor(parcel: Parcel) : this(
            apiFirst = parcel.createStringArrayList() as ArrayList<String>,
            apiSecond = parcel.createStringArrayList() as ArrayList<String>,
            apiThird = parcel.createStringArrayList() as ArrayList<String>
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeStringList(apiFirst)
            parcel.writeStringList(apiSecond)
            parcel.writeStringList(apiThird)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ModelContent> {
            override fun createFromParcel(parcel: Parcel): ModelContent {
                return ModelContent(parcel)
            }

            override fun newArray(size: Int): Array<ModelContent?> {
                return arrayOfNulls(size)
            }
        }
    }
}

