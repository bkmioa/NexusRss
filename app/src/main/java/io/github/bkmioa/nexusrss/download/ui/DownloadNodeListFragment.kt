package io.github.bkmioa.nexusrss.download.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.google.android.material.snackbar.Snackbar
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseFragment
import io.github.bkmioa.nexusrss.model.DownloadNodeModel
import io.github.bkmioa.nexusrss.ui.TabListActivity
import kotlinx.android.synthetic.main.activity_tab_list.*

class DownloadNodeListFragment : BaseFragment() {
    companion object {
        const val REQUEST_CODE_ADD = 0x1
        const val REQUEST_CODE_EDIT = 0x2
    }

    private val listViewModel: DownloadNodeListViewModel by viewModels()

    private val listController = ListController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_download_node_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = listController.adapter
        listViewModel.getAllLiveData().observe(viewLifecycleOwner, {
            it ?: throw IllegalStateException()

            listController.update(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.add("Add")
            .setIcon(R.drawable.ic_menu_add)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                val intent = DownloadEditActivity.createIntent(requireContext())
                startActivityForResult(intent, REQUEST_CODE_ADD)
                true
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                TabListActivity.REQUEST_CODE_ADD -> {
                    val downloadNode: DownloadNodeModel = data?.getParcelableExtra(DownloadEditActivity.KEY_DOWNLOAD_NODE)
                        ?: throw IllegalStateException()
                    listViewModel.addDownloadNode(downloadNode)
                }
                TabListActivity.REQUEST_CODE_EDIT -> {
                    val downloadNode: DownloadNodeModel = data?.getParcelableExtra(DownloadEditActivity.KEY_DOWNLOAD_NODE)
                        ?: throw IllegalStateException()
                    listViewModel.addDownloadNode(downloadNode)
                }
            }
        }
    }

    private fun onItemClicked(model: DownloadNodeModel) {
        val intent = DownloadEditActivity.createIntent(requireContext(), model)
        startActivityForResult(intent, REQUEST_CODE_EDIT)
    }

    private fun onMoreClicked(clickedView: View, model: DownloadNodeModel) {
        val popupMenu = PopupMenu(requireContext(), clickedView)
        popupMenu.menu
            .add(R.string.action_duplicate).setOnMenuItemClickListener {
                listViewModel.addDownloadNode(model.copy(id = null))
                true
            }
        popupMenu.menu
            .add(R.string.action_delete).setOnMenuItemClickListener {
                deleteNode(model)
                true
            }
        popupMenu.show()
    }

    private fun deleteNode(model: DownloadNodeModel) {
        listViewModel.delete(model)

        Snackbar.make(requireView(), R.string.deleted, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.undo_action) {
                listViewModel.addDownloadNode(model)
            }
            .show()
    }

    inner class ListController : EpoxyController() {
        private var nodeList: List<DownloadNodeModel> = emptyList()

        override fun buildModels() {
            nodeList.forEach { item ->
                DownloadNodeItemViewModel_(item)
                    .onClickListener(View.OnClickListener { onItemClicked(item) })
                    .onMoreClickListener(View.OnClickListener { onMoreClicked(it, item) })
                    .addTo(this)
            }
        }

        fun update(list: List<DownloadNodeModel>) {
            nodeList = list
            requestModelBuild()
        }
    }
}