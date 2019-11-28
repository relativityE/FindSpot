package com.example.nextstop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


class ListCustomAdapter(
    private val ctx: Context,
    capitals: ArrayList<String>,
    countries: ArrayList<String>
) : BaseAdapter() {

    private val capitals: ArrayList<String>  = ArrayList()
    private val countries: ArrayList<String> = ArrayList()
    private val myInflate: LayoutInflater = LayoutInflater.from(ctx)

    init {
        this.countries.addAll(countries)
        this.capitals.addAll(capitals)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val myView : View
        val myViewHolder : CustomViewHolder

        if (convertView == null) {
            //inflate view with my individual row layout
            myView = myInflate.inflate(R.layout.inflate_view, parent, false)
            myViewHolder = CustomViewHolder(myView)
            myView.tag = myViewHolder
        }
        else {
            myView = convertView
            myViewHolder = myView.tag as CustomViewHolder
        }

        //now that we have the row we want, populate with correct info
        myViewHolder.textCountry.text = countries[position]
        myViewHolder.textCapital.text = capitals[position]

        return myView
    }

    override fun getItem(position: Int): Any {
        return countries[position]
    }

    override fun getItemId(position: Int): Long {
        return countries[position].toLong()
    }

    override fun getCount(): Int {
        return countries.size
    }

    private class CustomViewHolder(view: View) {
        val textCountry : TextView = view.findViewById(R.id.MajorTextView)
        val textCapital : TextView = view.findViewById(R.id.MinorTextView)
    }
}