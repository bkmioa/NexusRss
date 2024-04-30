package io.github.bkmioa.nexusrss.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import io.github.bkmioa.nexusrss.R
import io.github.bkmioa.nexusrss.base.BaseFragment
import io.github.bkmioa.nexusrss.databinding.FragmentOptionBinding
import io.github.bkmioa.nexusrss.model.Category
import io.github.bkmioa.nexusrss.model.Option
import io.github.bkmioa.nexusrss.ui.viewModel.*
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class OptionFragment : BaseFragment(),
    OptionViewModel.OnOptionCheckedListener,
    OptionGroupViewModel.OnGroupCheckedListener {
    companion object {
        private const val KEY_INIT_PATH = "init_path"
        private const val KEY_INIT_SELECTED = "init_selected"
        private const val KEY_WITH_LIST_STYLE = "with_list_style"
        private const val KEY_INIT_COLUMN_COUNT = "init_column_count"

        fun newInstance(
            path: String?,
            initSelected: Array<String>? = null,
            withListStyle: Boolean = false,
            initColumnCount: Int = 1
        ): OptionFragment {
            val fragment = OptionFragment()
            val args = Bundle()
            args.putString(KEY_INIT_PATH, path)
            args.putStringArray(KEY_INIT_SELECTED, initSelected)
            args.putBoolean(KEY_WITH_LIST_STYLE, withListStyle)
            args.putInt(KEY_INIT_COLUMN_COUNT, initColumnCount)
            fragment.arguments = args
            return fragment
        }
    }

    private val optionController = OptionController()

    var selectedCategory: Category = Category.NORMAL

    val selected: MutableSet<String> = HashSet()

    var columnCount = 1

    var withListStyle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getString(KEY_INIT_PATH)?.also { path ->
                selectedCategory = Category.ALL_CATEGORY.find { it.path == path } ?: Category.NORMAL
            }
            getStringArray(KEY_INIT_SELECTED)?.also {
                selected.addAll(it.toSet())
            }
            withListStyle = getBoolean(KEY_WITH_LIST_STYLE, false)

            columnCount = getInt(KEY_INIT_COLUMN_COUNT, 1)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = FragmentOptionBinding.bind(view)

        viewBinding.recyclerView.adapter = optionController.adapter

        val gridLayoutManager = GridLayoutManager(activity, 4)
        gridLayoutManager.spanSizeLookup = optionController.spanSizeLookup
        optionController.spanCount = gridLayoutManager.spanCount

        viewBinding.recyclerView.layoutManager = gridLayoutManager

        optionController.update()

    }

    override fun onChecked(option: Option, isChecked: Boolean) {
        if (isChecked) selected.add(option.key) else selected.remove(option.key)
        optionController.update()
    }

    override fun onGroupChecked(options: List<Option>, isChecked: Boolean) {
        if (isChecked) {
            selected.addAll(options.map { it.key })
        } else {
            selected.removeAll(options.map { it.key })
        }
        optionController.update()
    }

    private fun isAllChecked(options: List<Option>): Boolean {
        return selected.containsAll(options.map { it.key })
    }

    inner class OptionController : EpoxyController() {
        private val models = ArrayList<EpoxyModel<*>>()
        private var disposable: Disposable? = null

        fun update() {
            disposable?.dispose()
            disposable = Completable.fromRunnable(::buildModelsInternal)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::requestModelBuild, Throwable::printStackTrace)
        }

        override fun buildModels() {
            add(*models.toTypedArray())
        }

        @Synchronized
        @WorkerThread
        private fun buildModelsInternal() {
            models.clear()

            models.apply {
                if (withListStyle) {
                    val columnList = resources.getStringArray(R.array.list_columns).toList()
                    add(
                        ListColumnViewModel_(getString(R.string.list_style), columnCount - 1, columnList).id("list_column")
                            .onItemClickListener(object : ListColumnViewModel.OnItemSelectedListener {
                                override fun onItemSelected(index: Int) {
                                    columnCount = index + 1
                                }
                            })
                    )
                }

                val allCategory = Category.ALL_CATEGORY
                val categoryList = allCategory.map { it.des }
                val indexOf = allCategory.indexOfFirst { it.path == selectedCategory.path }
                add(
                    ListColumnViewModel_("類型", indexOf, categoryList).id("category")
                        .onItemClickListener(object : ListColumnViewModel.OnItemSelectedListener {
                            override fun onItemSelected(index: Int) {
                                selectedCategory = allCategory[index]
                                optionController.update()
                            }
                        })
                )

                add(
                    OptionGroupViewModel_("類型", isAllChecked(selectedCategory.options), selectedCategory.options)
                        .onGroupCheckedListener(this@OptionFragment)
                )
                selectedCategory.options.takeIf { it.isNotEmpty() }?.forEach {
                    add(
                        OptionViewModel_(it, selected.contains(it.key))
                            .onOptionCheckedListener(this@OptionFragment)
                    )
                }

                add(
                    OptionGroupViewModel_("解析度", isAllChecked(Option.RESOLUTION), Option.RESOLUTION)
                        .onGroupCheckedListener(this@OptionFragment)
                )
                Option.RESOLUTION.forEach {
                    add(
                        OptionViewModel_(it, selected.contains(it.key))
                            .onOptionCheckedListener(this@OptionFragment)
                    )
                }

                add(
                    OptionGroupViewModel_("處理", isAllChecked(Option.PROCESS), Option.PROCESS)
                        .onGroupCheckedListener(this@OptionFragment)
                )

                Option.PROCESS.forEach {
                    add(
                        OptionViewModel_(it, selected.contains(it.key))
                            .onOptionCheckedListener(this@OptionFragment)
                    )
                }
            }
        }
    }
}
