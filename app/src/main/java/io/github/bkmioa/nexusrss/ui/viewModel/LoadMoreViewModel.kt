package io.github.bkmioa.nexusrss.ui.viewModel

import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.SimpleEpoxyModel
import io.github.bkmioa.nexusrss.R

@EpoxyModelClass
abstract class LoadMoreViewModel : SimpleEpoxyModel(R.layout.load_more) {
    init {
        id(layout)
    }

    override fun getSpanSize(totalSpanCount: Int, position: Int, itemCount: Int): Int {
        return totalSpanCount
    }
}
