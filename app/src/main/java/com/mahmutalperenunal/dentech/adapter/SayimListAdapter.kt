package com.mahmutalperenunal.dentech.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.mahmutalperenunal.dentech.R
import com.mahmutalperenunal.dentech.model.SayimModel

class SayimListAdapter(private var sayimList2: ArrayList<SayimModel>):
    RecyclerView.Adapter<SayimListAdapter.ViewHolder>() {

    private var sayimList = emptyList<SayimModel>()
    private var sayimListFiltered = emptyList<SayimModel>()

    private var clickCount = 0


    //click
    private lateinit var sayimListListener: OnItemClickListener

    interface OnItemClickListener { fun onItemClick(position: Int) }

    fun setOnItemClickListener(listener: OnItemClickListener) { sayimListListener = listener }


    //long click
    private lateinit var sayimListListenerLong: OnItemLongClickListener

    interface OnItemLongClickListener { fun onItemLongClick(position: Int) }

    fun setOnItemLongClickListener(listenerLong: OnItemLongClickListener) { sayimListListenerLong = listenerLong }


    inner class ViewHolder(view: View, listener: OnItemClickListener, listenerLong: OnItemLongClickListener) : RecyclerView.ViewHolder(view) {
        val sayimNo: TextView
        val materialBarcode: TextView
        val lotBatchNo: TextView
        val configurationNo: TextView
        val serialNo: TextView
        val locationBarcode: TextView
        val amount: TextView
        private val checkBox: CheckBox
        val date: TextView

        //set sharedPreferences and editor for selected item counter
        private val preferences = view.context.getSharedPreferences("Item Counter", Context.MODE_PRIVATE)
        private val editor = preferences.edit()

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

            //checkbox visibility
            if (clickCount >= 1) {
                checkBox.visibility = View.VISIBLE
            } else {
                checkBox.visibility = View.GONE
            }

            //clear sharedPreferences data
            editor.clear()
            editor.commit()
            editor.putInt("count", 0)
            editor.apply()

            view.setOnClickListener {

                if (clickCount >= 1) {

                    if (sayimList2[adapterPosition].isSelected) {

                        clickCount--

                        editor.putInt("count", clickCount)
                        editor.apply()

                        view.setBackgroundResource(R.drawable.shape_unselected_background)

                        sayimList2[adapterPosition].isSelected = false

                        checkBox.isChecked = false

                        checkBox.visibility = View.GONE

                        listener.onItemClick(adapterPosition)

                    } else {

                        clickCount++

                        editor.putInt("count", clickCount)
                        editor.apply()

                        view.setBackgroundResource(R.drawable.shape_selected_background)

                        sayimList2[adapterPosition].isSelected = true

                        checkBox.isChecked = true

                        checkBox.visibility = View.VISIBLE

                        listener.onItemClick(adapterPosition)

                    }

                }

            }

            //long click listener
            view.setOnLongClickListener {

                if (sayimList2[adapterPosition].isSelected) {

                    clickCount--

                    editor.putInt("count", clickCount)
                    editor.apply()

                    view.setBackgroundResource(R.drawable.shape_unselected_background)

                    sayimList2[adapterPosition].isSelected = false

                    checkBox.isChecked = false

                    checkBox.visibility = View.GONE

                    listenerLong.onItemLongClick(adapterPosition)

                } else {

                    clickCount++

                    editor.putInt("count", clickCount)
                    editor.apply()

                    view.setBackgroundResource(R.drawable.shape_selected_background)

                    sayimList2[adapterPosition].isSelected = true

                    checkBox.isChecked = true

                    checkBox.visibility = View.VISIBLE

                    listenerLong.onItemLongClick(adapterPosition)

                }

                true

            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.sayim_item, viewGroup, false)
        return ViewHolder(view, sayimListListener, sayimListListenerLong)
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
    fun setData(newList: ArrayList<SayimModel>){
        sayimList2 = newList
        sayimList = newList
        sayimListFiltered = newList
        notifyDataSetChanged()
    }

}