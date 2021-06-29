package com.epa.coroutines.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.preference.PreferenceManager
import com.epa.coroutines.domain.LocalStorage

class LocalStorageImpl(appContext: Context) : LocalStorage {

    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(appContext)

    @SuppressLint("ApplySharedPref")
    override suspend fun saveInt(key: String, value: Int) {
        sharedPrefs.edit().putInt(key, value).commit()
    }

    override suspend fun getInt(key: String): Int? =
        if (sharedPrefs.contains(key)) sharedPrefs.getInt(key, -1) else null
}