package io.github.bkmioa.nexusrss.ui.viewModel

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import io.github.bkmioa.nexusrss.R2
import io.github.bkmioa.nexusrss.base.BaseEpoxyHolder
import io.github.bkmioa.nexusrss.databinding.ItemListColumnBinding

@EpoxyModelClass(layout = R2.layout.item_list_column)
abstract class ListColumnViewModel(
    @EpoxyAttribute var title: String,
    @EpoxyAttribute var selectedIndex: Int,
    @EpoxyAttribute var data: List<String>
) : EpoxyModelWithHolder<ListColumnViewModel.ViewHolder>() {

    @EpoxyAttribute(hash = false)
    var onItemClickListener: OnItemSelectedListener? = null


    override fun bind(holder: ViewHolder) {
        super.bind(holder)
        with(holder) {
            textView.text = title
            spinner.adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_dropdown_item, data)
            spinner.setSelection(selectedIndex)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    onItemClickListener?.onItemSelected(position)
                }
            }
        }
    }

    override fun getSpanSize(totalSpanCount: Int, position: Int, itemCount: Int): Int {
        return totalSpanCount
    }

    interface OnItemSelectedListener {
        fun onItemSelected(index: Int)
    }

    class ViewHolder : BaseEpoxyHolder() {
        val viewBinding by lazy { ItemListColumnBinding.bind(itemView) }
        val textView: TextView by lazy { viewBinding.textViewName }
        val spinner: Spinner by lazy { viewBinding.spinner }
    }
}
