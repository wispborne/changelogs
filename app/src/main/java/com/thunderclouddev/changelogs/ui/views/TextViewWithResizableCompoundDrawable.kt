/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui.views

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import com.thunderclouddev.changelogs.R

class TextViewWithResizableCompoundDrawable : AppCompatTextView {
    private var mDrawableWidth: Int = 0
    private var mDrawableHeight: Int = 0

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    //    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    //    public TextViewWithResizableCompoundDrawable(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    //        super(context, attrs, defStyleAttr, defStyleRes);
    //        init(context, attrs, defStyleAttr, defStyleRes);
    //    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.TextViewWithResizableCompoundDrawable, defStyleAttr, defStyleRes)

        try {
            mDrawableWidth = array.getDimensionPixelSize(R.styleable.TextViewWithResizableCompoundDrawable_compoundDrawableWidth, -1)
            mDrawableHeight = array.getDimensionPixelSize(R.styleable.TextViewWithResizableCompoundDrawable_compoundDrawableHeight, -1)
        } finally {
            array.recycle()
        }

        if (mDrawableWidth > 0 || mDrawableHeight > 0) {
            initCompoundDrawableSize()
        }
    }

    private fun initCompoundDrawableSize() {
        val drawables =
                if (compoundDrawables.any { it != null })
                    compoundDrawables
                else
                    compoundDrawablesRelative

        for (drawable in drawables) {
            if (drawable == null) {
                continue
            }

            val realBounds = drawable.bounds
            val scaleFactor = realBounds.height() / realBounds.width().toFloat()

            var drawableWidth = realBounds.width().toFloat()
            var drawableHeight = realBounds.height().toFloat()

            if (mDrawableWidth > 0) {
                // save scale factor of image
                if (drawableWidth > mDrawableWidth) {
                    drawableWidth = mDrawableWidth.toFloat()
                    drawableHeight = drawableWidth * scaleFactor
                }
            }
            if (mDrawableHeight > 0) {
                // save scale factor of image

                if (drawableHeight > mDrawableHeight) {
                    drawableHeight = mDrawableHeight.toFloat()
                    drawableWidth = drawableHeight / scaleFactor
                }
            }

            realBounds.right = realBounds.left + Math.round(drawableWidth)
            realBounds.bottom = realBounds.top + Math.round(drawableHeight)

            drawable.bounds = realBounds
        }

        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3])
    }
}
