package com.coffeehub.pos.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.coffeehub.pos.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore(name = Constants.DATASTORE_NAME)

@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val USER_ID = intPreferencesKey(Constants.PREF_USER_ID)
    private val USER_NAME = stringPreferencesKey(Constants.PREF_USER_NAME)
    private val USER_ROLE = stringPreferencesKey(Constants.PREF_USER_ROLE)
    private val IS_LOGGED_IN = booleanPreferencesKey(Constants.PREF_IS_LOGGED_IN)
    private val DARK_THEME = booleanPreferencesKey(Constants.PREF_DARK_THEME)
    private val TAX_RATE = doublePreferencesKey(Constants.PREF_TAX_RATE)
    private val SHOP_NAME = stringPreferencesKey(Constants.PREF_SHOP_NAME)
    private val LANGUAGE = stringPreferencesKey(Constants.PREF_LANGUAGE)

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    val userId: Flow<Int> = context.dataStore.data.map { it[USER_ID] ?: -1 }
    val userName: Flow<String> = context.dataStore.data.map { it[USER_NAME] ?: "" }
    val userRole: Flow<String> = context.dataStore.data.map { it[USER_ROLE] ?: "CASHIER" }
    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { it[DARK_THEME] ?: true }
    val taxRate: Flow<Double> = context.dataStore.data.map { it[TAX_RATE] ?: Constants.DEFAULT_TAX_RATE }
    val shopName: Flow<String> = context.dataStore.data.map { it[SHOP_NAME] ?: Constants.APP_NAME }
    // "en" = English, "ar" = Arabic
    val language: Flow<String> = context.dataStore.data.map { it[LANGUAGE] ?: "en" }

    suspend fun saveUserSession(userId: Int, userName: String, userRole: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = userId
            prefs[USER_NAME] = userName
            prefs[USER_ROLE] = userRole
            prefs[IS_LOGGED_IN] = true
        }
    }

    suspend fun clearUserSession() {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = false
            prefs[USER_ID] = -1
            prefs[USER_NAME] = ""
            prefs[USER_ROLE] = ""
        }
    }

    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { it[DARK_THEME] = isDark }
    }

    suspend fun setTaxRate(rate: Double) {
        context.dataStore.edit { it[TAX_RATE] = rate }
    }

    suspend fun setShopName(name: String) {
        context.dataStore.edit { it[SHOP_NAME] = name }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[LANGUAGE] = lang }
    }
}

