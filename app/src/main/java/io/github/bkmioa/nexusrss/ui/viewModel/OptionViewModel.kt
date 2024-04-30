package io.github.bkmioa.nexusrss.ui.viewModel

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.github.bkmioa.nexusrss.R2
import io.github.bkmioa.nexusrss.Settings
import io.github.bkmioa.nexusrss.base.BaseEpoxyHolder
import io.github.bkmioa.nexusrss.databinding.OptionItemBinding
import io.github.bkmioa.nexusrss.model.Option

@EpoxyModelClass(layout = R2.layout.option_item)
abstract class OptionViewModel(
    @EpoxyAttribute var option: Option,
    @EpoxyAttribute var selected: Boolean
) : EpoxyModelWithHolder<OptionViewModel.ViewHolder>() {

    init {
        id(option.key, option.des)
    }

    @EpoxyAttribute(hash = false)
    lateinit
    var onOptionCheckedListener: OnOptionCheckedListener

    override fun bind(holder: ViewHolder) {
        super.bind(holder)

        with(holder) {
            checkBox.text = option.des
            if (option.img != null) {
                checkBox.text = null
                imageView.visibility = View.VISIBLE
                Glide.with(holder.itemView.context)
                    .load(Settings.BASE_URL + option.img)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView)
            } else {
                imageView.visibility = View.GONE
            }

            itemView.setOnClickListener { checkBox.performClick() }
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = selected
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onOptionCheckedListener.onChecked(option, isChecked)
            }
        }
    }

    class ViewHolder : BaseEpoxyHolder() {
        val viewBinding by lazy { OptionItemBinding.bind(itemView) }
        val checkBox: CheckBox by lazy { viewBinding.checkBox }
        val imageView: ImageView by lazy { viewBinding.imageView }
    }

    interface OnOptionCheckedListener {
        fun onChecked(option: Option, isChecked: Boolean)
    }

}

