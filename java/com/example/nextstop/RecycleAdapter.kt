package com.example.nextstop

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class RecycleAdapter : RecyclerView.Adapter<RecycleAdapter.ModelViewHolder> {

    private val ctx: Context
    private var intent : Intent
    private val minordata: ArrayList<String>
    private val maindata: ArrayList<String>
    private val imgdata: ArrayList<String>
    private val mvhInflater: LayoutInflater
    private lateinit var view : View
    private lateinit var modelViewHolder : ModelViewHolder


    constructor(ctx: Context, apiData: DataStore.ModelContent) : super() {
        this.ctx = ctx
        this.maindata  = apiData.apiFirst
        this.minordata = apiData.apiSecond
        this.imgdata   = apiData.apiThird
        this.mvhInflater = LayoutInflater.from(this.ctx)
        this.intent = Intent(this.ctx, ResultsActivity::class.java)
    }

    //viewType allows for heterogeneous view - inflate views here
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        //inflate individual row
        this.view = mvhInflater.inflate(R.layout.inflate_view, parent,false)
        //pass the inflated view to view holder
        this.modelViewHolder = ModelViewHolder(view)

        return this.modelViewHolder
    }

    override fun getItemCount(): Int {
        return maindata.size
    }

    //bind data to the holder
    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        Log.d("ak_onBindViewHolder", "position: $position")

        //set data content for each row that has been inflated
        holder.textmain?.text = maindata[position]
        holder.textminor?.text = minordata[position]

        if (imgdata[position] != "stockphoto") {
            Log.d("ak_img_found", "$imgdata[position]")
            holder.imgavatar?.let { Glide.with(ctx).load(imgdata[position]).into(it) }
        }
        else {
            Log.d("ak_img_not_found", "$imgdata[position]")
            holder.imgavatar?.setImageResource(R.drawable.thinkbox)
        }

        //implement setOnClickListener
        holder.itemView.setOnClickListener {
            Log.d("ak_RecycleAdapter","onBindVH")
            //send selection to new view
            intent.putExtra("viewdata", maindata[position])
            this.ctx.startActivity(intent)
        }
    }

    //static inner class to model each row in a Recycle list view
    class ModelViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
       //create individual row layout
       val textmain : TextView? = itemView?.findViewById(R.id.MajorTextView)
       val textminor: TextView? = itemView?.findViewById(R.id.MinorTextView)
       val imgavatar: ImageView? = itemView?.findViewById(R.id.avatarimageView)

       init {
           Log.d("ak_ModelView row", itemView.toString())
       }
    }
}