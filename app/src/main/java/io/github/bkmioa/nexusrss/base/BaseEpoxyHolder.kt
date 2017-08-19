package io.github.bkmioa.nexusrss.base

import android.support.annotation.CallSuper
import android.view.View
import com.airbnb.epoxy.EpoxyHolder

open class BaseEpoxyHolder : EpoxyHolder() {
    lateinit var itemView: View

    @CallSuper
    override fun bindView(view: View) {
        this.itemView = view
    }

}