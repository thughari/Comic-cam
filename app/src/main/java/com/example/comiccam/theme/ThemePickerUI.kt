package com.example.comiccam.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ThemePickerUI(themes: List<ThemeManifest>, selectedTheme: ThemeManifest?, onThemeSelected: (ThemeManifest) -> Unit) {
    LazyRow(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) { items(themes) { theme -> val selected = theme.id == selectedTheme?.id; Column(Modifier.background(if (selected) Color.White.copy(alpha=.22f) else Color.Black.copy(alpha=.38f)).clickable { onThemeSelected(theme) }.padding(12.dp)) { Text(theme.displayName, color = Color.White); Text(theme.artStyle, color = Color.LightGray) } } }
}
