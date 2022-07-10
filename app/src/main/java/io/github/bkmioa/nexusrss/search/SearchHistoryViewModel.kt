package io.github.bkmioa.nexusrss.search

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.chibatching.kotpref.KotprefModel
import io.github.bkmioa.nexusrss.base.BaseViewModel


class SearchHistoryViewModel(app: Application) : BaseViewModel(app) {
    private object Store : KotprefModel() {
        private const val MAX_SIZE = 20

        override val kotprefName: String
            get() = "recent_search"

        fun put(keywords: String) {
            val all = getAll()

            val editor = preferences.edit()
                .putLong(keywords, System.currentTimeMillis())

            if (all.size > MAX_SIZE) {
                editor.remove(all.last())
            }

            editor.apply()
        }

        fun remove(keywords: String) {
            preferences.edit()
                .remove(keywords)
                .apply()
        }

        fun getAllLiveData(): LiveData<List<String>> {
            return object : MutableLiveData<List<String>>() {
                val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    value = getAll()
                }

                override fun onActive() {
                    super.onActive()
                    value = getAll()
                    preferences.registerOnSharedPreferenceChangeListener(listener)
                }

                override fun onInactive() {
                    super.onInactive()
                    preferences.unregisterOnSharedPreferenceChangeListener(listener)
                }
            }
        }

        fun getAll(): List<String> {
            val all: Map<String, Long> = preferences.all as Map<String, Long>

            return all.keys.sortedWith(compareByDescending { all[it] })
        }
    }

    private val filterLivaData = MutableLiveData<String>()
    val historyLiveData: LiveData<List<String>> = initLiveData()

    private fun initLiveData(): LiveData<List<String>> {
        return MediatorLiveData<List<String>>().apply {
            var all: List<String> = emptyList()
            var filter: String? = null

            fun update() {
                value = if (filter.isNullOrBlank()) {
                    all
                } else {
                    all.filter { it.contains(filter!!, false) }
                }
            }

            addSource(Store.getAllLiveData()) {
                all = it
                update()
            }
            addSource(filterLivaData) {
                filter = it
                update()
            }
        }
    }

    private val _selectedKeywordLiveData = MutableLiveData<Pair<String?, Boolean>>()
    val selectedKeywordLiveData: LiveData<Pair<String?, Boolean>> = _selectedKeywordLiveData

    fun add(keywords: String) {
        Store.put(keywords)
    }

    fun remove(keywords: String) {
        Store.remove(keywords)
    }

    fun onQuery(keywords: String?) {
        filterLivaData.value = keywords
    }

    fun onSelected(text: String?, submit: Boolean) {
        _selectedKeywordLiveData.value = Pair(text, submit)
    }
}