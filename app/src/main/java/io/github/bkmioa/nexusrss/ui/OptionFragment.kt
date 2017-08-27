package io.github.bkmioa.nexusrss.ui

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import com.airbnb.epoxy.EpoxyController
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseFragment
import io.github.bkmioa.nexusrss.model.Option
import io.github.bkmioa.nexusrss.ui.viewModel.OptionGroupViewModel
import io.github.bkmioa.nexusrss.ui.viewModel.OptionGroupViewModel_
import io.github.bkmioa.nexusrss.ui.viewModel.OptionViewModel
import io.github.bkmioa.nexusrss.ui.viewModel.OptionViewModel_
import kotlinx.android.synthetic.main.fragment_option.*

class OptionFragment : BaseFragment(),
        OptionViewModel.OnOptionCheckedListener,
        OptionGroupViewModel.OnGroupCheckedListener {
    companion object {
        lateinit var instance: OptionFragment
    }


    private val optionController = OptionController()

    private val selected: MutableSet<Option> = HashSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_option, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = optionController.adapter

        val gridLayoutManager = GridLayoutManager(activity, 3)
        gridLayoutManager.spanSizeLookup = optionController.spanSizeLookup
        optionController.spanCount = gridLayoutManager.spanCount

        recyclerView.layoutManager = gridLayoutManager

        optionController.requestModelBuild()
    }

    override fun onChecked(option: Option, isChecked: Boolean) {
        if (isChecked) selected.add(option) else selected.remove(option)
    }

    override fun onGroupChecked(options: Array<Option>, isChecked: Boolean) {
        if (isChecked) {
            selected.addAll(options)
        } else {
            selected.removeAll(options)
        }
        optionController.requestModelBuild()
    }

    private fun isAllChecked(options: Array<Option>): Boolean {
        return selected.containsAll(options.toSet())
    }

    inner class OptionController : EpoxyController() {
        override fun buildModels() {
            add(OptionGroupViewModel_("類型", isAllChecked(Option.CATEGORY), Option.CATEGORY)
                    .onGroupCheckedListener(this@OptionFragment))
            Option.CATEGORY.forEach {
                add(OptionViewModel_(it, selected.contains(it))
                        .onOptionCheckedListener(this@OptionFragment))
            }

            add(OptionGroupViewModel_("解析度", isAllChecked(Option.RESOLUTION), Option.RESOLUTION)
                    .onGroupCheckedListener(this@OptionFragment))
            Option.RESOLUTION.forEach {
                add(OptionViewModel_(it, selected.contains(it))
                        .onOptionCheckedListener(this@OptionFragment))
            }

            add(OptionGroupViewModel_("編碼", isAllChecked(Option.CODE), Option.CODE)
                    .onGroupCheckedListener(this@OptionFragment))
            Option.CODE.forEach {
                add(OptionViewModel_(it, selected.contains(it))
                        .onOptionCheckedListener(this@OptionFragment))
            }

            add(OptionGroupViewModel_("處理", isAllChecked(Option.PROCESS), Option.PROCESS)
                    .onGroupCheckedListener(this@OptionFragment))
            Option.PROCESS.forEach {
                add(OptionViewModel_(it, selected.contains(it))
                        .onOptionCheckedListener(this@OptionFragment))
            }

            add(OptionGroupViewModel_("製作組", isAllChecked(Option.TEAM), Option.TEAM)
                    .onGroupCheckedListener(this@OptionFragment))
            Option.TEAM.forEach {
                add(OptionViewModel_(it, selected.contains(it))
                        .onOptionCheckedListener(this@OptionFragment))
            }


        }


    }

}
