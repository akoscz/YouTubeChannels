package com.akoscz.youtubechannels.data.db

import android.content.Context
import com.akoscz.youtubechannels.di.AppModule
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class FeatureToggleManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("feature_toggles", Context.MODE_PRIVATE)

    fun isMockDataEnabled(): Boolean {
        return sharedPreferences.getBoolean("use_mock_data", true) // Default to true (mock data)
    }

    fun setMockDataEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("use_mock_data", enabled).apply()
    }
}

object FeatureToggleHelper {
    fun getInstance(context: Context): FeatureToggleManager {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context,
            AppModule.FeatureToggleEntryPoint::class.java
        )
        return hiltEntryPoint.getFeatureToggleManager()
    }
}