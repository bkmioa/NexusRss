package io.github.bkmioa.nexusrss.ui

import android.app.Activity
import androidx.lifecycle.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyTouchHelper
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseActivity
import io.github.bkmioa.nexusrss.dp2px
import io.github.bkmioa.nexusrss.model.Tab
import io.github.bkmioa.nexusrss.ui.viewModel.TabItemViewModel
import io.github.bkmioa.nexusrss.ui.viewModel.TabItemViewModel_
import io.github.bkmioa.nexusrss.viewmodel.TabListViewModel
import kotlinx.android.synthetic.main.activity_tab_list.*

class TabListActivity : BaseActivity(), TabItemViewModel.OnTabVisibilityChangeListener {

    private val tabListViewModel: TabListViewModel by viewModels()

    private val tabs: MutableList<Tab> = ArrayList()

    private val listController = ListController()

    companion object {
        const val REQUEST_CODE_ADD = 0x1
        const val REQUEST_CODE_EDIT = 0x2
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
                        val tab = tabs.removeAt(position)
                        tabListViewModel.removeTab(tab)

                        Snackbar.make(findViewById(android.R.id.content), R.string.tab_deleted, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.undo_action) {
                                    tabs.add(position, tab)
                                    tabListViewModel.addTab(tab)
                                }
                                .show()
                    }
                })

        tabListViewModel.tabs().observe(this, Observer<Array<Tab>> {
            it ?: throw IllegalStateException()

            tabs.clear()
            tabs.addAll(it.sorted())

            listController.requestModelBuild()
        })


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val item = menu.add("Add")
        item.setIcon(R.drawable.ic_menu_add)
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        item.setOnMenuItemClickListener {
            startActivityForResult(TabEditActivity.createIntent(this), REQUEST_CODE_ADD)
            true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_ADD -> {
                    val tab: Tab = data?.getParcelableExtra("tab") ?: throw IllegalStateException()
                    tab.order = tabs.last().order + 1
                    tabListViewModel.addTab(tab)
                }
                REQUEST_CODE_EDIT -> {
                    val tab: Tab = data?.getParcelableExtra("tab") ?: throw IllegalStateException()
                    tabListViewModel.update(tab)
                }
            }
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

    private fun onItemClick(tab: Tab) {
        startActivityForResult(TabEditActivity.createIntent(this, tab), REQUEST_CODE_EDIT)
    }

    inner class ListController : EpoxyController() {
        override fun buildModels() {
            tabs.map {
                TabItemViewModel_(it)
                        .onTabVisibilityChangeListener(this@TabListActivity)
                        .onClickListener { model, _, _, _ ->
                            onItemClick(model.tab())
                        }
                        .addTo(this)
            }
        }
    }

}
