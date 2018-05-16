package io.github.bkmioa.nexusrss.ui

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.epoxy.EpoxyController
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseFragment
import io.github.bkmioa.nexusrss.model.Option
import io.github.bkmioa.nexusrss.model.Tab
import io.github.bkmioa.nexusrss.ui.viewModel.OptionGroupViewModel
import io.github.bkmioa.nexusrss.ui.viewModel.OptionGroupViewModel_
import io.github.bkmioa.nexusrss.ui.viewModel.OptionViewModel
import io.github.bkmioa.nexusrss.ui.viewModel.OptionViewModel_
import kotlinx.android.synthetic.main.fragment_option.*

class OptionFragment : BaseFragment(),
        OptionViewModel.OnOptionCheckedListener,
        OptionGroupViewModel.OnGroupCheckedListener {
    companion object {
        fun newInstance(tab: Tab?): OptionFragment {
            val fragment = OptionFragment()
            val args = Bundle()
            args.putParcelable("tab", tab)
            fragment.arguments = args
            return fragment
        }
    }

    private val optionController = OptionController()

    val selected: MutableSet<String> = HashSet()

    private var tab: Tab? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tab = arguments!!.getParcelable("tab")
        if (tab != null) {
            selected.addAll(tab!!.options)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = optionController.adapter

        val gridLayoutManager = GridLayoutManager(activity, 4)
        gridLayoutManager.spanSizeLookup = optionController.spanSizeLookup
        optionController.spanCount = gridLayoutManager.spanCount

        recyclerView.layoutManager = gridLayoutManager

        optionController.requestModelBuild()
    }

    override fun onChecked(option: Option, isChecked: Boolean) {
        if (isChecked) selected.add(option.key) else selected.remove(option.key)
        optionController.requestModelBuild()
    }

    override fun onGroupChecked(options: Array<Option>, isChecked: Boolean) {
        if (isChecked) {
            selected.addAll(options.map { it.key })
        } else {
            selected.removeAll(options.map { it.key })
        }
        optionController.requestModelBuild()
    }

    private fun isAllChecked(options: Array<Option>): Boolean {
        return selected.containsAll(options.map { it.key })
    }

    inner class OptionController : EpoxyController() {
        override fun buildModels() {
            add(OptionGroupViewModel_("類型", isAllChecked(Option.CATEGORY), Option.CATEGORY)
                    .onGroupCheckedListener(this@OptionFragment))
            Option.CATEGORY.forEach {
                add(OptionViewModel_(it, selected.contains(it.key))
                        .onOptionCheckedListener(this@OptionFragment))
            }

            add(OptionGroupViewModel_("解析度", isAllChecked(Option.RESOLUTION), Option.RESOLUTION)
                    .onGroupCheckedListener(this@OptionFragment))
            Option.RESOLUTION.forEach {
                add(OptionViewModel_(it, selected.contains(it.key))
                        .onOptionCheckedListener(this@OptionFragment))
            }

            add(OptionGroupViewModel_("編碼", isAllChecked(Option.CODE), Option.CODE)
                    .onGroupCheckedListener(this@OptionFragment))
            Option.CODE.forEach {
                add(OptionViewModel_(it, selected.contains(it.key))
                        .onOptionCheckedListener(this@OptionFragment))
            }

            add(OptionGroupViewModel_("處理", isAllChecked(Option.PROCESS), Option.PROCESS)
                    .onGroupCheckedListener(this@OptionFragment))
            Option.PROCESS.forEach {
                add(OptionViewModel_(it, selected.contains(it.key))
                        .onOptionCheckedListener(this@OptionFragment))
            }

            add(OptionGroupViewModel_("製作組", isAllChecked(Option.TEAM), Option.TEAM)
                    .onGroupCheckedListener(this@OptionFragment))
            Option.TEAM.forEach {
                add(OptionViewModel_(it, selected.contains(it.key))
                        .onOptionCheckedListener(this@OptionFragment))
            }


        }


    }

}
