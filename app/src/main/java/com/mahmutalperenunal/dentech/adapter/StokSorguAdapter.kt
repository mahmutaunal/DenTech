package com.mahmutalperenunal.dentech.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahmutalperenunal.dentech.R
import com.mahmutalperenunal.dentech.model.SayimModel

class StokSorguAdapter(private var sayimList2: ArrayList<SayimModel>):
    RecyclerView.Adapter<StokSorguAdapter.ViewHolder>(), Filterable {

    private var sayimList = emptyList<SayimModel>()
    private var sayimListFiltered = emptyList<SayimModel>()


    inner class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {
        val sayimNo: TextView
        val materialBarcode: TextView
        val lotBatchNo: TextView
        val configurationNo: TextView
        val serialNo: TextView
        val locationBarcode: TextView
        val amount: TextView
        val date: TextView
        private val checkBox: CheckBox

        init {
            sayimNo = view.findViewById(R.id.sayim_item_sayimNo_textView)
            materialBarcode = view.findViewById(R.id.sayim_item_materialBarcode_textView)
            lotBatchNo = view.findViewById(R.id.sayim_item_lotBatchNo_textView)
            configurationNo = view.findViewById(R.id.sayim_item_configurationNo_textView)
            serialNo = view.findViewById(R.id.sayim_item_serialNo_textView)
            locationBarcode = view.findViewById(R.id.sayim_item_locationBarcode_textView)
            amount = view.findViewById(R.id.sayim_item_amountNo_textView)
            checkBox = view.findViewById(R.id.sayim_item_checkBox)
            date = view.findViewById(R.id.sayim_item_date_textView)

            checkBox.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.sayim_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val item = sayimList2[position]

        viewHolder.sayimNo.text = item.sayimNo
        viewHolder.materialBarcode.text = item.materialBarcode
        viewHolder.lotBatchNo.text = item.lotBatchNo
        viewHolder.configurationNo.text = item.configurationNo
        viewHolder.serialNo.text = item.serialNo
        viewHolder.locationBarcode.text = item.locationBarcode
        viewHolder.amount.text = item.amount
        viewHolder.date.text = item.date

    }

    override fun getItemCount() = sayimList2.size


    //set data
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: ArrayList<SayimModel>) {
        sayimList2 = newList
        sayimList = newList
        sayimListFiltered = newList
        notifyDataSetChanged()
    }


    //search filter
    override fun getFilter(): Filter {
        val filter = object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {

                val filterResults = FilterResults()
                if (p0 == null || p0.isEmpty()) {
                    filterResults.values = sayimListFiltered
                    filterResults.count = sayimListFiltered.size
                } else {
                    val searchChar = p0.toString().toLowerCase()
                    val filteredResults = ArrayList<SayimModel>()

                    for (manager in sayimListFiltered) {
                        if (manager.date.toLowerCase().contains(searchChar)
                            || manager.sayimNo.toLowerCase().contains(searchChar)
                            || manager.materialBarcode.toLowerCase().contains(searchChar)
                            || manager.locationBarcode.toLowerCase().contains(searchChar)
                        ) {
                            filteredResults.add(manager)
                        }
                    }

                    filterResults.values = filteredResults
                    filterResults.count = filteredResults.size

                }

                return filterResults

            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                sayimList = p1!!.values as ArrayList<SayimModel>
                //sayimList = p1!!.values as List<SayimModel>
                notifyDataSetChanged()
            }

        }

        return filter

    }

}