package com.example.comiccam.render

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.example.comiccam.gesture.RevealGestureUiState
import com.example.comiccam.theme.ThemeManifest
import java.util.concurrent.atomic.AtomicReference
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CompositeRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val state = AtomicReference(RevealGestureUiState())
    private val theme = AtomicReference<ThemeManifest?>(null)
    private var oesTexture = 0
    var surfaceTexture: SurfaceTexture? = null; private set
    val particles = ParticleSystem()
    fun updateState(next: RevealGestureUiState) { if (next.fullRevealPulse != state.get().fullRevealPulse) particles.burst(); state.set(next) }
    fun updateTheme(next: ThemeManifest) { theme.set(next) }
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) { val tex = IntArray(1); GLES20.glGenTextures(1, tex, 0); oesTexture = tex[0]; GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, oesTexture); GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR); surfaceTexture = SurfaceTexture(oesTexture) }
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) { GLES20.glViewport(0, 0, width, height) }
    override fun onDrawFrame(gl: GL10?) { surfaceTexture?.updateTexImage(); val s = state.get(); val t = theme.get(); val glow = android.graphics.Color.parseColor(t?.glowColorHex ?: "#ff2a2a"); GLES20.glClearColor(((glow shr 16) and 255) / 255f * s.revealProgress * .18f, ((glow shr 8) and 255) / 255f * s.revealProgress * .18f, (glow and 255) / 255f * s.revealProgress * .18f, 1f); GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT) }
}

class SuitUpGlView(context: Context) : GLSurfaceView(context) { val compositeRenderer = CompositeRenderer(context); init { setEGLContextClientVersion(3); setRenderer(compositeRenderer); renderMode = RENDERMODE_CONTINUOUSLY } }
