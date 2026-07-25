package com.coffeehub.pos.utils

object Constants {
    const val BASE_URL = "https://api.brewpoint.com/"
    const val DATABASE_NAME = "brewpoint_db"
    const val DATASTORE_NAME = "brewpoint_prefs"
    const val DEFAULT_TAX_RATE = 0.14
    const val LOYALTY_POINTS_PER_DOLLAR = 1
    const val EXTRA_SHOT_PRICE = 0.50
    const val CURRENCY_SYMBOL = "$"
    const val APP_NAME = "BrewPoint POS"
    
    // DataStore Keys
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_NAME = "user_name"
    const val PREF_USER_ROLE = "user_role"
    const val PREF_IS_LOGGED_IN = "is_logged_in"
    const val PREF_DARK_THEME = "dark_theme"
    const val PREF_TAX_RATE = "tax_rate"
    const val PREF_SHOP_NAME = "shop_name"
    const val PREF_CURRENCY = "currency"
    const val PREF_LANGUAGE = "language"
}
