package io.github.bkmioa.nexusrss.ui.viewModel

import android.text.format.Formatter
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseEpoxyHolder
import io.github.bkmioa.nexusrss.base.GlideApp
import io.github.bkmioa.nexusrss.model.Item
import kotlinx.android.synthetic.main.item_view.view.*

@EpoxyModelClass(layout = R.layout.item_view)
abstract class ItemViewModel(private val item: Item) : EpoxyModelWithHolder<ItemViewModel.ViewHolder>() {

    @EpoxyAttribute lateinit var onClickListener: View.OnClickListener

    init {
        id(item.link)
    }

    override fun bind(holder: ViewHolder) {
        super.bind(holder)

        with(holder) {
            textViewTitle.text = item.subTitle ?: item.title
            textViewSubTitle.text = "[${item.sizeText}] ${if (item.subTitle == null) "" else item.title}"

            GlideApp.with(imageView.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.place_holder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(imageView)

            itemView.setOnClickListener(onClickListener)
        }
    }

    class ViewHolder : BaseEpoxyHolder() {
        val imageView: ImageView by lazy { itemView.imageView }
        val textViewTitle: TextView by lazy { itemView.textViewTitle }
        val textViewSubTitle: TextView by lazy { itemView.textViewSubTitle }

    }
}