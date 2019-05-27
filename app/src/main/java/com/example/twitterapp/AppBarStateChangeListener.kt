package com.example.twitterapp

import android.support.design.widget.AppBarLayout

abstract class AppBarStateChangeListener : AppBarLayout.OnOffsetChangedListener {

    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }

    private var mCurrentState = State.IDLE
    private var mCurrentOffset = 0f

    override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
        when {
            i == 0 -> {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED)
                }
                mCurrentState = State.EXPANDED
            }
            Math.abs(i) >= appBarLayout.totalScrollRange -> {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED)
                }
                mCurrentState = State.COLLAPSED
            }
            else -> {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE)
                }
                mCurrentState = State.IDLE
            }
        }
        val offset = Math.abs(i / appBarLayout.totalScrollRange.toFloat())
        if (offset != mCurrentOffset) {
            mCurrentOffset = offset
            onOffsetChanged(mCurrentState, offset)
        }
    }

    fun getCurrentOffset(): Float {
        return mCurrentOffset
    }

    abstract fun onStateChanged(appBarLayout: AppBarLayout, state: State)

    abstract fun onOffsetChanged(state: State, offset: Float)
}
