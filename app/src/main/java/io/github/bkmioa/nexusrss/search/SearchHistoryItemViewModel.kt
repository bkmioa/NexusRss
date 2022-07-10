package io.github.bkmioa.nexusrss.search

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseEpoxyHolder
import io.github.bkmioa.nexusrss.databinding.SearchHistoryItemViewBinding

@EpoxyModelClass(layout = R.layout.search_history_item_view)
abstract class SearchHistoryItemViewModel(
    @EpoxyAttribute var text: String
) : EpoxyModelWithHolder<SearchHistoryItemViewModel.ViewHolder>() {
    init {
        id(text)
    }
    @EpoxyAttribute
    var onItemClick: (() -> Unit)? = null

    @EpoxyAttribute
    var onShiftClick: (() -> Unit)? = null

    override fun bind(holder: ViewHolder) {
        super.bind(holder)
        holder.binding.text.text = text
        holder.binding.shift.setOnClickListener {
            onShiftClick?.invoke()
        }
        holder.itemView.setOnClickListener {
            onItemClick?.invoke()
        }
    }

    class ViewHolder : BaseEpoxyHolder() {
        val binding by lazy { SearchHistoryItemViewBinding.bind(itemView) }
    }
}