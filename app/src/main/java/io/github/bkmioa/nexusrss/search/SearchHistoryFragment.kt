package io.github.bkmioa.nexusrss.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyTouchHelper
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseFragment
import io.github.bkmioa.nexusrss.databinding.SearchHistoryFragmentBinding

class SearchHistoryFragment : BaseFragment(R.layout.search_history_fragment) {
    private val searchHistoryViewModel: SearchHistoryViewModel by activityViewModels()

    private val listController = ListController()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = SearchHistoryFragmentBinding.bind(view)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = listController.adapter

        searchHistoryViewModel.historyLiveData.observe(viewLifecycleOwner) {
            listController.setData(it)
        }

        EpoxyTouchHelper.initSwiping(binding.recyclerView)
            .leftAndRight()
            .withTarget(SearchHistoryItemViewModel::class.java)
            .andCallbacks(object : EpoxyTouchHelper.SwipeCallbacks<SearchHistoryItemViewModel>() {
                override fun onSwipeCompleted(model: SearchHistoryItemViewModel, itemView: View, position: Int, direction: Int) {
                    searchHistoryViewModel.remove(model.text)
                }
            })
    }

    inner class ListController : EpoxyController() {
        private var list: List<String> = emptyList()

        fun setData(list: List<String>) {
            this.list = list
            requestModelBuild()
        }

        override fun buildModels() {
            list.forEach { item ->
                SearchHistoryItemViewModel_(item)
                    .onItemClick {
                        searchHistoryViewModel.onSelected(item, true)
                    }
                    .onShiftClick {
                        searchHistoryViewModel.onSelected(item, false)
                    }
                    .addTo(this)
            }
        }
    }
}