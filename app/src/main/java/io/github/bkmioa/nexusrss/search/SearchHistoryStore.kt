package io.github.bkmioa.nexusrss.search

import android.content.SharedPreferences
import com.chibatching.kotpref.KotprefModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object SearchHistoryStore : KotprefModel() {
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

    fun getAllFlow(): Flow<List<String>> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            trySend(getAll())
        }

        preferences.registerOnSharedPreferenceChangeListener(listener)

        trySend(getAll())

        awaitClose {
            preferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    fun getAll(): List<String> {
        val all: Map<String, Long> = preferences.all as Map<String, Long>

        return all.keys.sortedWith(compareByDescending { all[it] })
    }
}