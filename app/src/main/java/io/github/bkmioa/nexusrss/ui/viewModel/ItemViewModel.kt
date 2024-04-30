package io.github.bkmioa.nexusrss.ui.viewModel

import android.text.format.Formatter
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.R2
import io.github.bkmioa.nexusrss.base.BaseEpoxyHolder
import io.github.bkmioa.nexusrss.databinding.ItemViewBinding
import io.github.bkmioa.nexusrss.model.Item

@EpoxyModelClass(layout = R2.layout.item_view)
abstract class ItemViewModel(private val item: Item) : EpoxyModelWithHolder<ItemViewModel.ViewHolder>() {

    @EpoxyAttribute
    lateinit var onClickListener: View.OnClickListener

    init {
        id(item.id)
    }

    override fun bind(holder: ViewHolder) {
        super.bind(holder)

        with(holder) {
            textViewTitle.text = item.subTitle ?: item.title
            val size = Formatter.formatShortFileSize(itemView.context, item.size)

            textViewSubTitle.text = "[${size}] ${if (item.subTitle == null) "" else item.title}"

            Glide.with(imageView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.place_holder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(imageView)

            itemView.setOnClickListener(onClickListener)
        }
    }

    class ViewHolder : BaseEpoxyHolder() {
        val viewBinding by lazy { ItemViewBinding.bind(itemView) }

        val imageView: ImageView by lazy { viewBinding.imageView }
        val textViewTitle: TextView by lazy { viewBinding.textViewTitle }
        val textViewSubTitle: TextView by lazy { viewBinding.textViewSubTitle }

    }
}