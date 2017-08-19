package io.github.bkmioa.nexusrss.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import io.github.bkmioa.nexusrss.R
import kotterknife.bindView

class OptionViewHolder(parent: ViewGroup)
    : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.option_item, parent, false)) {

    val checkBox: CheckBox by bindView(R.id.checkBox)
    val imageView: ImageView by bindView(R.id.imageView)


}
