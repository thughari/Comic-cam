package com.example.comiccam.ml

import com.example.comiccam.gesture.Point2

class ExpSmoother(private val alpha: Float = 0.35f) { private var last: Point2? = null; fun smooth(next: Point2): Point2 { val prev = last; val out = if (prev == null) next else Point2(prev.x + (next.x - prev.x) * alpha, prev.y + (next.y - prev.y) * alpha); last = out; return out } }
class LandmarkSmoothing { private val filters = mutableMapOf<String, ExpSmoother>(); fun smooth(id: String, point: Point2): Point2 = filters.getOrPut(id) { ExpSmoother() }.smooth(point); fun clear() = filters.clear() }
