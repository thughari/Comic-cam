package com.example.comiccam.ui

import android.view.Surface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.comiccam.camera.CameraController
import com.example.comiccam.camera.FrameDispatcher
import com.example.comiccam.ml.*
import com.example.comiccam.render.SuitUpGlView
import com.example.comiccam.theme.ThemePickerUI

@Composable
fun CameraScreen(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current; val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.gestureState.collectAsState(); val selected by viewModel.selectedTheme.collectAsState()
    var error by remember { mutableStateOf<String?>(null) }
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(factory = { ctx -> SuitUpGlView(ctx).also { gl ->
            selected?.let(gl.compositeRenderer::updateTheme)
            gl.postDelayed({ gl.compositeRenderer.surfaceTexture?.let { st -> CameraController(context, lifecycleOwner).bind(Surface(st), FrameDispatcher(HandGestureTracker(context), PoseTracker(context), SegmentationEngine(context))) { error = it } } }, 350)
        } }, update = { gl -> gl.compositeRenderer.updateState(state); selected?.let(gl.compositeRenderer::updateTheme) }, modifier = Modifier.fillMaxSize())
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text("SUIT-UP • ${state.state}", color = Color.White); Text("${(state.revealProgress * 100).toInt()}%", color = Color.Cyan) }
            Column { ThemePickerUI(viewModel.themes, selected, viewModel::selectTheme); Button(onClick = viewModel::resetReveal, modifier = Modifier.padding(16.dp)) { Text("Reset") } }
        }
        error?.let { Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha=.72f)), Alignment.Center) { Text(it, color = Color.White) } }
    }
}
