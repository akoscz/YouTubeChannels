package com.akoscz.youtubechannels.data.db

import android.content.Context
import com.akoscz.youtubechannels.di.AppModule
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Helper class to manage application settings.
 *
 * Application settings are stored in SharedPreferences and can be accessed and modified
 * through this class.
 *
 * Note that the setMockDataEnabled() method uses commit() to ensure that the write operation is
 * performed synchronously. This is done to ensure that the settings are updated before the app
 * is restarted.
 *
 */
class AppSettingsManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    fun isMockDataEnabled(): Boolean {
        return sharedPreferences.getBoolean("use_mock_data", true) // Default to true (mock data)
    }

    fun setMockDataEnabled(enabled: Boolean) {
        // we use commit() to ensure that the write operation is performed synchronously
        // otherwise when we restart the app, the shared preferences will not have been updated
        sharedPreferences.edit().putBoolean("use_mock_data", enabled).commit()
    }

    fun setTheme(selectedTheme: String) {
        sharedPreferences.edit().putString("selected_theme", selectedTheme).apply()
    }

    fun getTheme(): String? {
        return sharedPreferences.getString("selected_theme", "system")
    }
}
/**
 * Helper function to get an instance of AppSettingsManager.
 *
 * This uses Hilt's EntryPointAccessors to get the AppSettingsManager instance using the
 * application context.
 */
object AppSettingsHelper {
    fun getInstance(context: Context): AppSettingsManager {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context,
            AppModule.AppSettingsEntryPoint::class.java
        )
        return hiltEntryPoint.getAppSettingsManager()
    }
}