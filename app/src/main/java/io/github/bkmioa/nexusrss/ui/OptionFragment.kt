package io.github.bkmioa.nexusrss.ui

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseFragment
import io.github.bkmioa.nexusrss.base.GlideApp
import io.github.bkmioa.nexusrss.model.Option
import kotlinx.android.synthetic.main.fragment_option.*

class OptionFragment : BaseFragment() {
    companion object {
        lateinit var instance: OptionFragment
    }

    private val data: MutableList<Option> = ArrayList()
    val selected: MutableSet<String> = HashSet()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_option, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        data.addAll(Option.CATEGORY)
        data.addAll(Option.RESOLUTION)
        data.addAll(Option.CODE)
        data.addAll(Option.TEAM)
        data.addAll(Option.PROCESS)

        recyclerView.adapter = Adapter()
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
    }

    inner class Adapter : RecyclerView.Adapter<OptionViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
            return OptionViewHolder(parent)
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
            val option = data[position]

            with(holder) {
                checkBox.text = option.des
                if (option.img != null) {
                    checkBox.text = null
                    imageView.visibility = View.VISIBLE
                    GlideApp.with(this@OptionFragment)
                            .load(option.img)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(imageView)
                } else {
                    imageView.visibility = View.GONE
                }
                itemView.setOnClickListener { checkBox.performClick() }
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selected.add(option.key) else selected.remove(option.key)
                }

                checkBox.isChecked = selected.contains(option.key)
            }
        }

    }
}
