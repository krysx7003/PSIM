package com.napnap.heartbridge.ui.components

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

object SettingsStore{
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    fun saveS(context: Context,key:String, value:String){
        runBlocking {
            val dataStoreKey = stringPreferencesKey(key)
            context.dataStore.edit{ settings->
                settings[dataStoreKey] = value
            }
        }
    }
    fun saveB(context: Context,key:String, value:Boolean){
        runBlocking {
            val dataStoreKey = booleanPreferencesKey(key)
            context.dataStore.edit{ settings->
                settings[dataStoreKey] = value
            }
        }
    }

    fun readS(context: Context,key:String,default: String): String{
        return runBlocking {
            val dataStoreKey = stringPreferencesKey(key)
            val dataFlow = context.dataStore.data.map { it[dataStoreKey] }
            dataFlow.first() ?: default
        }
    }

    fun readB(context: Context,key:String,default: Boolean): Boolean{
        return runBlocking {
            val dataStoreKey = booleanPreferencesKey(key)
            val dataFlow = context.dataStore.data.map { it[dataStoreKey] }
            dataFlow.first() ?: default
        }
    }
}
