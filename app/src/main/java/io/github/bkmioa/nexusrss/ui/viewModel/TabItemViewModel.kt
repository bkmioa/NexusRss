package io.github.bkmioa.nexusrss.ui.viewModel

import androidx.appcompat.widget.SwitchCompat
import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseEpoxyHolder
import io.github.bkmioa.nexusrss.model.Tab
import kotterknife.bindView

@EpoxyModelClass(layout = R.layout.tab_list_item)
abstract class TabItemViewModel(@EpoxyAttribute @JvmField val tab: Tab)
    : EpoxyModelWithHolder<TabItemViewModel.ViewHolder>() {

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
        val textViewTitle: TextView by bindView(R.id.textViewTitle)
        val switchVisibility: SwitchCompat by bindView(R.id.switchVisibility)
    }

    interface OnTabVisibilityChangeListener {
        fun onVisibilityChange(tab: Tab, isChecked: Boolean)
    }

}
