package com.Sanleone.progress.dataHandler

import android.app.AlertDialog
import android.content.Context
import com.Sanleone.progress.R

object ReleaseLogHandler {

    fun ShowReleaseLogWhenNewVersion(applicationContext: Context){
        val sharedPref = applicationContext.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val packageInfo = applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0)
        val currentVersion = packageInfo.versionName
        val lastVersionCode = sharedPref.getString("last_version", "0.0.0")
        if (lastVersionCode != currentVersion) {
            println("releaseLog: " + applicationContext.getString(R.string.releaseLog))
            // Die App wurde aktualisiert, zeigen Sie das Releaselog an oder führen Sie andere erforderliche Aktionen aus
            with(sharedPref.edit()) {
                putString("last_version", currentVersion)
                apply()
            }

            val builder = AlertDialog.Builder(applicationContext)
            builder.setTitle(applicationContext.getString(R.string.releaseLogTitle))
                .setMessage(applicationContext.getString(R.string.releaseLog))
                .setPositiveButton(applicationContext.getString(R.string.ok)){ dialogInterfact, it ->
                }
                .show()

        } else {
            // Die App wurde nicht aktualisiert
        }

    }
}