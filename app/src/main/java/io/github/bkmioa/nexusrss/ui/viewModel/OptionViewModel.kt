package io.github.bkmioa.nexusrss.ui.viewModel

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseEpoxyHolder
import io.github.bkmioa.nexusrss.base.GlideApp
import io.github.bkmioa.nexusrss.model.Option
import kotterknife.bindView

@EpoxyModelClass(layout = R.layout.option_item)
abstract class OptionViewModel(
        @EpoxyAttribute @JvmField val option: Option,
        @EpoxyAttribute @JvmField val selected: Boolean)
    : EpoxyModelWithHolder<OptionViewModel.ViewHolder>() {

    init {
        id(option.key)
    }

    @EpoxyAttribute(hash = false) lateinit
    var onOptionCheckedListener: OnOptionCheckedListener

    override fun bind(holder: ViewHolder) {
        super.bind(holder)

        with(holder) {
            checkBox.text = option.des
            if (option.img != null) {
                checkBox.text = null
                imageView.visibility = View.VISIBLE
                GlideApp.with(holder.itemView.context)
                        .load(option.img)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView)
            } else {
                imageView.visibility = View.GONE
            }

            itemView.setOnClickListener { checkBox.performClick() }
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onOptionCheckedListener.onChecked(option, isChecked)
            }

            checkBox.isChecked = selected
        }
    }

    class ViewHolder : BaseEpoxyHolder() {
        val checkBox: CheckBox by bindView(R.id.checkBox)
        val imageView: ImageView by bindView(R.id.imageView)
    }

    interface OnOptionCheckedListener {
        fun onChecked(option: Option, isChecked: Boolean)
    }

}

