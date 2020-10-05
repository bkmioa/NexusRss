package io.github.bkmioa.nexusrss.download.ui

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseEpoxyHolder
import io.github.bkmioa.nexusrss.model.DownloadNodeModel
import kotlinx.android.synthetic.main.item_download_node.view.*

@EpoxyModelClass(layout = R.layout.item_download_node)
abstract class DownloadNodeItemViewModel(
    @EpoxyAttribute @JvmField
    val item: DownloadNodeModel
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
        val text1: TextView by lazy { itemView.text1 }
        val text2: TextView by lazy { itemView.text2 }
        val more: View by lazy { itemView.more }
    }
}