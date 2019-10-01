/**
 * https://github.com/thefuntasty/hauler
 *
 * MIT License
 *
 * Copyright (c) 2018 The FUNTASTY
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package at.florianschuster.playables.detail

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.animation.PathInterpolatorCompat
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.min

enum class DragDirection {
    NONE, UP, DOWN
}

class DraggableFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // configurable attributes
    private var dragDismissDistance = 500
    private var dragDismissFraction = -1f
    private var dragDismissScale = 0.95f
    private var shouldScale = true
    private var dragElasticity = 0.75f

    // state
    private var totalDrag: Float = 0.toFloat()
    private var draggingDown = false
    private var draggingUp = false
    private var mLastActionEvent: Int = 0

    var onDismissed: ((direction: DragDirection) -> Unit) = { }
    var onDragOffset: ((direction: DragDirection, percent: Float) -> Unit) = { _, _ -> }
    var isDragEnabled = true
    var dragUpEnabled = false

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean =
        nestedScrollAxes and View.SCROLL_AXIS_VERTICAL != 0

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (isDragEnabled.not()) {
            return super.onNestedPreScroll(target, dx, dy, consumed)
        }
        // if we're in a drag gesture and the user reverses up the we should take those events
        val draggingDownInProgress = draggingDown && dy > 0
        val draggingUpInProgress = draggingUp && dy < 0
        if (draggingDownInProgress || draggingUpInProgress) {
            dragScale(dy)
            consumed[1] = dy
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        if (isDragEnabled.not()) {
            return super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        }
        dragScale(dyUnconsumed)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        mLastActionEvent = ev.action
        return super.onInterceptTouchEvent(ev)
    }

    override fun onStopNestedScroll(child: View) {
        if (isDragEnabled.not()) {
            return super.onStopNestedScroll(child)
        }

        val totalDragNormalized = if (dragUpEnabled) Math.abs(totalDrag) else -totalDrag
        val dragDirection = if (totalDrag > 0) DragDirection.UP else DragDirection.DOWN

        if (totalDragNormalized >= dragDismissDistance) {
            onDismissed(dragDirection)
        } else { // settle back to natural position
            if (mLastActionEvent == MotionEvent.ACTION_DOWN) {
                // this is a 'defensive cleanup for new gestures',
                // don't animate here
                // see also https://github.com/nickbutcher/plaid/issues/185
                translationY = 0f
                scaleX = 1f
                scaleY = 1f
            } else {
                animate()
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200L)
                    .setInterpolator(PathInterpolatorCompat.create(0.4f, 0f, 0.2f, 1f))
                    .setListener(null)
                    .start()
            }
            totalDrag = 0f
            draggingUp = false
            draggingDown = draggingUp
            onDragOffset(DragDirection.NONE, 0f)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (dragDismissFraction > 0f) {
            dragDismissDistance = h * dragDismissFraction.toInt()
        }
    }

    private fun dragScale(scroll: Int) {
        if (scroll == 0) return

        totalDrag += scroll.toFloat()

        // track the direction & set the pivot point for scaling
        // don't double track i.e. if start dragging down and then reverse, keep tracking as
        // dragging down until they reach the 'natural' position
        if (scroll < 0 && !draggingUp && !draggingDown) {
            draggingDown = true
            if (shouldScale) pivotY = height.toFloat()
        } else if (scroll > 0 && !draggingDown && !draggingUp) {
            draggingUp = true
            if (shouldScale) {
                pivotY = 0f
            }
        }
        // how far have we dragged relative to the distance to perform a dismiss
        // (0–1 where 1 = dismiss distance). Decreasing logarithmically as we approach the limit
        var dragFraction =
            log10((1 + abs(totalDrag) / dragDismissDistance).toDouble()).toFloat()

        // calculate the desired translation given the drag fraction
        var dragTo = dragFraction * dragDismissDistance * dragElasticity

        if (draggingUp) {
            // as we use the absolute magnitude when calculating the drag fraction, need to
            // re-apply the drag direction
            dragTo *= -1f
        }
        translationY = dragTo

        if (shouldScale) {
            val scale = 1 - (1 - dragDismissScale) * dragFraction
            scaleX = scale
            scaleY = scale
        }

        // if we've reversed direction and gone past the settle point then clear the flags to
        // allow the list to get the scroll events & reset any transforms
        val downSettlePointReached = draggingDown && totalDrag >= 0
        val upSettlePointReached = draggingUp && totalDrag <= 0
        if (downSettlePointReached || upSettlePointReached) {
            dragFraction = 0f
            dragTo = dragFraction
            totalDrag = dragTo
            draggingUp = false
            draggingDown = draggingUp
            translationY = 0f
            scaleX = 1f
            scaleY = 1f
        }
        onDragOffset(
            if (draggingDown) DragDirection.DOWN else DragDirection.UP,
            min(1f, abs(totalDrag) / dragDismissDistance)
        )
    }

}