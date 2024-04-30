package io.github.bkmioa.nexusrss.download.ui

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.R2
import io.github.bkmioa.nexusrss.base.BaseEpoxyHolder
import io.github.bkmioa.nexusrss.databinding.ItemDownloadNodeBinding
import io.github.bkmioa.nexusrss.model.DownloadNodeModel

@EpoxyModelClass(layout = R2.layout.item_download_node)
abstract class DownloadNodeItemViewModel(
    @EpoxyAttribute
    var item: DownloadNodeModel
) : EpoxyModelWithHolder<DownloadNodeItemViewModel.ViewHolder>() {

    init {
        id(item.id)
    }

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    protected lateinit var onClickListener: View.OnClickListener

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    protected lateinit var onMoreClickListener: View.OnClickListener

    override fun bind(holder: ViewHolder) {
        super.bind(holder)
        holder.text1.text = item.name
        holder.text2.text = item.host
        holder.itemView.setOnClickListener(onClickListener)
        holder.more.setOnClickListener(onMoreClickListener)
    }

    class ViewHolder : BaseEpoxyHolder() {
        val viewBinding by lazy { ItemDownloadNodeBinding.bind(itemView) }
        val text1: TextView by lazy { viewBinding.text1 }
        val text2: TextView by lazy { viewBinding.text2 }
        val more: View by lazy { viewBinding.more }
    }
}