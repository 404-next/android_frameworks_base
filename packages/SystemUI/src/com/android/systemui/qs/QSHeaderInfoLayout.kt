/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.systemui.qs

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.android.systemui.R

/**
 * Container for the Next Alarm and Ringer status texts in [QuickStatusBarHeader].
 *
 * If both elements are visible, it splits the available space according to the following rules:
 * * If both views add up to less than the total space, they take all the space they need.
 * * If both views are larger than half the space, each view takes half the space.
 * * Otherwise, the smaller view takes the space it needs and the larger one takes all remaining
 * space.
 */
class QSHeaderInfoLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyle, defStyleRes) {

    private lateinit var alarmContainer: View
    private val location = Location(0, 0)

    override fun onFinishInflate() {
        super.onFinishInflate()
        alarmContainer = findViewById(R.id.alarm_container)
    }

    private fun View.layoutView(pWidth: Int, pHeight: Int, offset: Int, RTL: Boolean): Int {
        location.setLocationFromOffset(pWidth, offset, this.measuredWidth, RTL)
        layout(location.left, 0, location.right, pHeight)
        return this.measuredWidth
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(
                        MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST),
                heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        // Once we measure the views, using as much space as they need, we need to remeasure them
        // assigning them their final width. This is because TextViews decide whether to MARQUEE
        // after onMeasure.
        setMeasuredDimension(width, measuredHeight)
    }

    private data class Location(var left: Int, var right: Int) {
        /**
         * Sets the [left] and [right] with the correct values for laying out the child, respecting
         * RTL. Only set the variable through here to prevent concurrency issues.
         * This is done to prevent allocation of [Pair] in [onLayout].
         */
        fun setLocationFromOffset(parentWidth: Int, offset: Int, width: Int, RTL: Boolean) {
            if (RTL) {
                left = parentWidth - offset - width
                right = parentWidth - offset
            } else {
                left = offset
                right = offset + width
            }
        }
    }
}
