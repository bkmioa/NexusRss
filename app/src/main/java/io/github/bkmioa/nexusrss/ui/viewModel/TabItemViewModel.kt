package io.github.bkmioa.nexusrss.ui.viewModel

import androidx.appcompat.widget.SwitchCompat
import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import io.github.bkmioa.nexusrss.R2
import io.github.bkmioa.nexusrss.base.BaseEpoxyHolder
import io.github.bkmioa.nexusrss.databinding.TabListItemBinding
import io.github.bkmioa.nexusrss.model.Tab

@EpoxyModelClass(layout = R2.layout.tab_list_item)
abstract class TabItemViewModel(@EpoxyAttribute var tab: Tab) : EpoxyModelWithHolder<TabItemViewModel.ViewHolder>() {

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    internal lateinit var onTabVisibilityChangeListener: OnTabVisibilityChangeListener

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    internal lateinit var onClickListener: View.OnClickListener

    init {
        id(tab.hashCode())
    }

    override fun bind(holder: ViewHolder) {
        super.bind(holder)
        with(holder) {
            textViewTitle.setText(tab.title)
            switchVisibility.setOnCheckedChangeListener(null)
            switchVisibility.isChecked = tab.isShow
            switchVisibility.setOnCheckedChangeListener { _, isChecked ->
                onTabVisibilityChangeListener.onVisibilityChange(tab, isChecked)
            }
            textViewTitle.setOnClickListener(onClickListener)
        }
    }

    class ViewHolder : BaseEpoxyHolder() {
        val viewBinding by lazy { TabListItemBinding.bind(itemView) }
        val textViewTitle: TextView by lazy { viewBinding.textViewTitle }
        val switchVisibility: SwitchCompat by lazy { viewBinding.switchVisibility }
    }

    interface OnTabVisibilityChangeListener {
        fun onVisibilityChange(tab: Tab, isChecked: Boolean)
    }

}
