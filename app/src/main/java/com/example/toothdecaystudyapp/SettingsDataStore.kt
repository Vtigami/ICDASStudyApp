package com.example.toothdecaystudyapp

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        private val CONFIRMATION_ON_SELECTION =
            booleanPreferencesKey("confirmation_on_selection")

        private val VOLUME = floatPreferencesKey("volume")

        private val CONFIGS_RUN_KEY =
            stringPreferencesKey("configs")

        private val TEST_RESULT_KEY =
            stringPreferencesKey("test_result")

        private val CONFIG_SELECTED = intPreferencesKey("config_selected")


    }

    private val gson = Gson()

    private val type =
        object : TypeToken<MutableList<SavedRunConfigs>>() {}.type

    private val typeTestResult =
        object : TypeToken<MutableList<TestResult>>() {}.type

    val volume: Flow<Float> =
        context.dataStore.data.map { preferences ->
            preferences[VOLUME] ?: 0f
        }

    suspend fun setVolume(volume:Float){
        context.dataStore.edit { preferences->
            preferences[VOLUME] = volume
        }
    }

    val confirmationOnSelection: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[CONFIRMATION_ON_SELECTION] ?: false
        }

    suspend fun setConfirmationOnSelection(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CONFIRMATION_ON_SELECTION] = enabled
        }
    }

    suspend fun loadConfigs():
            MutableList<SavedRunConfigs> {

        val prefs = context.dataStore.data.first()

        val json = prefs[CONFIGS_RUN_KEY]
            ?: return mutableListOf()

        return gson.fromJson(json, type)
    }

    // SAVE ENTIRE LIST
    private suspend fun saveConfigs(
        list: MutableList<SavedRunConfigs>
    ) {

        val json = gson.toJson(list)

        context.dataStore.edit { prefs ->

            prefs[CONFIGS_RUN_KEY] = json
        }
    }

    // ADD
    suspend fun addConfig(
        config: SavedRunConfigs
    ) {

        val list = loadConfigs()
        Log.d("teste","salvo")

        if (list.size >= 10) return

        list.add(config)

        saveConfigs(list)
    }

    // DELETE
    suspend fun deleteConfig(
        index: Int
    ) {

        val list = loadConfigs()

        if (index !in list.indices) return

        list.removeAt(index)

        saveConfigs(list)
    }

    // UPDATE
    suspend fun updateConfig(
        index: Int,
        config: SavedRunConfigs
    ) {

        val list = loadConfigs()

        if (index !in list.indices) return

        list[index] = config

        saveConfigs(list)
    }

    suspend fun saveConfigSelected(value: Int) {
        context.dataStore.edit { prefs ->
            prefs[CONFIG_SELECTED] = value
        }
    }

    suspend fun getConfigSelected(): Int {
        return context.dataStore.data
            .first()[CONFIG_SELECTED] ?: 0
    }



    suspend fun loadTestResults():
            MutableList<TestResult> {

        val prefs = context.dataStore.data.first()

        val json = prefs[TEST_RESULT_KEY]
            ?: return mutableListOf()

        return gson.fromJson(json, typeTestResult)
    }

    // SAVE ENTIRE LIST
    suspend fun saveTestResults(
        list: MutableList<TestResult>
    ) {

        val json = gson.toJson(list)

        context.dataStore.edit { prefs ->

            prefs[TEST_RESULT_KEY] = json
        }
    }

    // ADD
    suspend fun addTestResult(
        testResult: TestResult
    ) {
        val list = loadTestResults()

        if (list.size >= 32) {
            list.removeAt(0) // remove oldest
        }

        list.add(testResult) // add newest

        saveTestResults(list)
    }

    // DELETE
    suspend fun deleteTestResult(
        index: Int
    ) {

        val list = loadTestResults()

        if (index !in list.indices) return

        list.removeAt(index)

        saveTestResults(list)
    }

    // UPDATE
    suspend fun updateTestResult(
        index: Int,
        config: TestResult
    ) {

        val list = loadTestResults()

        if (index !in list.indices) return

        list[index] = config

        saveTestResults(list)
    }

}