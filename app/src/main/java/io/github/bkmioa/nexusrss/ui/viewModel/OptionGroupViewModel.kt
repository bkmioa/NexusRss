package io.github.bkmioa.nexusrss.ui.viewModel

import android.widget.CheckBox
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import io.github.bkmioa.nexusrss.R2
import io.github.bkmioa.nexusrss.base.BaseEpoxyHolder
import io.github.bkmioa.nexusrss.databinding.OptionGroupBinding
import io.github.bkmioa.nexusrss.model.Option

@EpoxyModelClass(layout = R2.layout.option_group)
abstract class OptionGroupViewModel(
    @EpoxyAttribute var name: String,
    @EpoxyAttribute var allChecked: Boolean,
    private val options: List<Option>
) : EpoxyModelWithHolder<OptionGroupViewModel.ViewHolder>() {

    @EpoxyAttribute(hash = false)
    lateinit var onGroupCheckedListener: OnGroupCheckedListener

    init {
        id(name)
    }

    override fun bind(holder: ViewHolder) {
        super.bind(holder)
        with(holder) {
            checkBox.setOnCheckedChangeListener(null)
            textView.text = name
            checkBox.isChecked = allChecked
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onGroupCheckedListener.onGroupChecked(options, isChecked)
            }
        }
    }

    override fun getSpanSize(totalSpanCount: Int, position: Int, itemCount: Int): Int {
        return totalSpanCount
    }

    interface OnGroupCheckedListener {
        fun onGroupChecked(options: List<Option>, isChecked: Boolean)
    }

    class ViewHolder : BaseEpoxyHolder() {
        val viewBinding by lazy { OptionGroupBinding.bind(itemView) }
        val textView: TextView by lazy { viewBinding.textViewName }
        val checkBox: CheckBox by lazy { viewBinding.checkBox }
    }
}
