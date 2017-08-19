package io.github.bkmioa.nexusrss.ui.viewModel

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseEpoxyHolder
import io.github.bkmioa.nexusrss.base.GlideApp
import io.github.bkmioa.nexusrss.model.Item
import kotterknife.bindView

@EpoxyModelClass(layout = R.layout.item_view)
abstract class ItemViewModel(private val item: Item) : EpoxyModelWithHolder<ItemViewModel.ViewHolder>() {

    @EpoxyAttribute lateinit var onClickListener: View.OnClickListener

    init {
        id(item.link)
    }

    override fun bind(holder: ViewHolder) {
        super.bind(holder)

        with(holder) {
            textViewTitle.text = item.title
            textViewSubTitle.text = item.subTitle

            GlideApp.with(imageView.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.place_holder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                            backgroundImage.reuse()
                            return false
                        }

                    })
                    .into(imageView)

            itemView.setOnClickListener(onClickListener)
        }
    }

    class ViewHolder : BaseEpoxyHolder() {
        val imageView: ImageView by bindView(R.id.imageView)
        val textViewTitle: TextView by bindView(R.id.textViewTitle)
        val textViewSubTitle: TextView by bindView(R.id.textViewSubTitle)

    }
}