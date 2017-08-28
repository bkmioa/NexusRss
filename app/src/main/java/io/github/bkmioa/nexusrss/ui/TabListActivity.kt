package io.github.bkmioa.nexusrss.ui

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyTouchHelper
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseActivity
import io.github.bkmioa.nexusrss.di.Injectable
import io.github.bkmioa.nexusrss.dp2px
import io.github.bkmioa.nexusrss.model.Tab
import io.github.bkmioa.nexusrss.ui.viewModel.TabItemViewModel
import io.github.bkmioa.nexusrss.ui.viewModel.TabItemViewModel_
import io.github.bkmioa.nexusrss.viewmodel.TabListViewModel
import kotlinx.android.synthetic.main.activity_tab_list.*
import javax.inject.Inject

class TabListActivity : BaseActivity(), Injectable, TabItemViewModel.OnTabVisibilityChangeListener {
    @Inject internal lateinit
    var tabListViewModel: TabListViewModel

    private val tabs: MutableList<Tab> = ArrayList()

    private val listController = ListController()

    companion object {
        const val REQUEST_CODE_ADD = 0x1
        fun createIntent(context: Context) = Intent(context, TabListActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_list)

        setSupportActionBar(toolBar)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = listController.adapter

        EpoxyTouchHelper.initDragging(listController)
                .withRecyclerView(recyclerView)
                .forVerticalList()
                .withTarget(TabItemViewModel::class.java)
                .andCallbacks(object : EpoxyTouchHelper.DragCallbacks<TabItemViewModel>() {

                    override fun onDragStarted(model: TabItemViewModel, itemView: View, adapterPosition: Int) {
                        super.onDragStarted(model, itemView, adapterPosition)
                        itemView.animate().translationZBy(application.dp2px(4).toFloat())
                    }

                    override fun onDragReleased(model: TabItemViewModel, itemView: View) {
                        super.onDragReleased(model, itemView)
                        itemView.animate().translationZ(0f)
                    }

                    override fun onModelMoved(fromPosition: Int, toPosition: Int,
                                              modelBeingMoved: TabItemViewModel, itemView: View) {
                        tabs.add(toPosition, tabs.removeAt(fromPosition))
                    }

                    override fun clearView(model: TabItemViewModel?, itemView: View?) {
                        super.clearView(model, itemView)
                        for (i in 0 until tabs.size) {
                            tabs[i].order = i
                        }
                        tabListViewModel.update(*tabs.toTypedArray())
                    }
                })

        EpoxyTouchHelper.initSwiping(recyclerView)
                .leftAndRight()
                .withTarget(TabItemViewModel::class.java)
                .andCallbacks(object : EpoxyTouchHelper.SwipeCallbacks<TabItemViewModel>() {
                    override fun onSwipeCompleted(model: TabItemViewModel, itemView: View, position: Int, direction: Int) {
                        tabListViewModel.removeTab(tabs.removeAt(position))
                    }
                })


        tabListViewModel.tabs().observe(this, Observer<Array<Tab>> {
            tabs.clear()
            tabs.addAll(it!!.sorted())

            listController.requestModelBuild()
        })


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val item = menu.add("Add")
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        item.setOnMenuItemClickListener {
            startActivityForResult(TabEditActivity.createIntent(this), REQUEST_CODE_ADD)
            true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD && resultCode == Activity.RESULT_OK) {
            val tab: Tab = data?.getParcelableExtra("tab")!!
            tab.order = tabs.last().order + 1
            tabListViewModel.addTab(tab)
        }
    }

    override fun finish() {
        setResult(Activity.RESULT_OK)
        super.finish()
    }

    override fun onVisibilityChange(tab: Tab, isChecked: Boolean) {
        tab.isShow = isChecked
        tabListViewModel.update(tab)
    }

    inner class ListController : EpoxyController() {
        override fun buildModels() {
            tabs.map {
                TabItemViewModel_(it)
                        .onTabVisibilityChangeListener(this@TabListActivity)
                        .addTo(this)
            }
        }
    }

}
