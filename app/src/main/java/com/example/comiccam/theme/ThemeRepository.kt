package com.example.comiccam.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

private val Context.themeDataStore by preferencesDataStore("suit_up_settings")

class ThemeRepository(private val context: Context) {
    private val selectedKey = stringPreferencesKey("selected_theme")
    fun loadThemes(): List<ThemeManifest> = context.assets.list("themes").orEmpty().filter { it.endsWith(".json") }.map { file -> context.assets.open("themes/$file").bufferedReader().use { parse(JSONObject(it.readText())) } }
    val selectedThemeId: Flow<String?> = context.themeDataStore.data.map { it[selectedKey] }
    suspend fun selectTheme(id: String) { context.themeDataStore.edit { it[selectedKey] = id } }
    private fun parse(json: JSONObject) = ThemeManifest(json.getString("id"), json.getString("displayName"), json.getString("costumeTexture"), json.getString("maskTexture"), json.optString("normalMap", null), json.optString("thumbnail", null), json.getString("accentColorHex"), json.getString("glowColorHex"), json.getString("particleColorHex"), json.optString("transformSoundFile", null), json.optString("idleAnimation", "none"), json.optString("artStyle", "generic"))
}
