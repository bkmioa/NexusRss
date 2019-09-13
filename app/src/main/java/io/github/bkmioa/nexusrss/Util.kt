package io.github.bkmioa.nexusrss

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.Observer
import io.github.bkmioa.nexusrss.db.AppDatabase
import io.github.bkmioa.nexusrss.model.Tab
import org.koin.android.ext.android.get

fun Context.dp2px(dp: Number) = (resources.displayMetrics.density * dp.toFloat() + 0.5f).toInt()

object Util {
    fun buildDynamicShortcutsForTabs(context: Context, newTabs: List<Tab>) {
        val shortcuts = newTabs.map { tab ->
            ShortcutInfoCompat.Builder(context, tab.id.toString())
                .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_home))
                .setShortLabel(tab.title)
                .setIntent(Intent(Intent.ACTION_VIEW))
                .build()
        }
        ShortcutManagerCompat.addDynamicShortcuts(context, shortcuts)
    }

    fun buildDynamicShortcuts(app: Application) {
        val allTab = app.get<AppDatabase>().appDao().getAllTab()
        allTab.observeForever(object : Observer<Array<Tab>> {
            override fun onChanged(tabs: Array<Tab>) {
                buildDynamicShortcutsForTabs(app, tabs.toList())
                allTab.removeObserver(this)
            }
        })
    }
}