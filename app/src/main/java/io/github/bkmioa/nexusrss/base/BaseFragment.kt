package io.github.bkmioa.nexusrss.base

import androidx.fragment.app.Fragment

open class BaseFragment : Fragment {
    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)
}