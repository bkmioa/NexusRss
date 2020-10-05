package io.github.bkmioa.nexusrss.ui.viewModel

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseEpoxyHolder
import kotlinx.android.synthetic.main.item_list_column.view.*

@EpoxyModelClass(layout = R.layout.item_list_column)
abstract class ListColumnViewModel(@EpoxyAttribute @JvmField val initColumn: Int)
    : EpoxyModelWithHolder<ListColumnViewModel.ViewHolder>() {

    @EpoxyAttribute(hash = false)
    var onItemClickListener: OnItemSelectedListener? = null


    override fun bind(holder: ViewHolder) {
        super.bind(holder)
        with(holder) {
            spinner.adapter = ArrayAdapter<String>(itemView.context, android.R.layout.simple_spinner_dropdown_item,
                    itemView.context.resources.getStringArray(R.array.list_columns))
            spinner.setSelection(initColumn - 1)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    onItemClickListener?.onItemSelected(position + 1)
                }
            }
        }
    }

    override fun getSpanSize(totalSpanCount: Int, position: Int, itemCount: Int): Int {
        return totalSpanCount
    }

    interface OnItemSelectedListener {
        fun onItemSelected(column: Int)
    }

    class ViewHolder : BaseEpoxyHolder() {
        val spinner: Spinner by lazy { itemView.spinner }
    }
}
