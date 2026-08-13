package com.example.comiccam.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.comiccam.gesture.*
import com.example.comiccam.theme.ThemeManifest
import com.example.comiccam.theme.ThemeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = ThemeRepository(app)
    private val machine = RevealGestureStateMachine()
    val themes = repository.loadThemes()
    private val _gestureState = MutableStateFlow(RevealGestureUiState())
    val gestureState: StateFlow<RevealGestureUiState> = _gestureState
    val selectedTheme: StateFlow<ThemeManifest?> = repository.selectedThemeId.map { id -> themes.firstOrNull { it.id == id } ?: themes.firstOrNull() }.stateIn(viewModelScope, SharingStarted.Eagerly, themes.firstOrNull())
    fun onGestureFrame(frame: GestureFrame) { _gestureState.value = machine.onFrame(frame) }
    fun resetReveal() { _gestureState.value = machine.reset() }
    fun selectTheme(theme: ThemeManifest) = viewModelScope.launch { repository.selectTheme(theme.id) }
}
