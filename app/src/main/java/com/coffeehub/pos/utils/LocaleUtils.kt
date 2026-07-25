package com.coffeehub.pos.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleUtils {
    fun applyLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }

    fun restartActivity(activity: Activity) {
        activity.recreate()
    }
}
