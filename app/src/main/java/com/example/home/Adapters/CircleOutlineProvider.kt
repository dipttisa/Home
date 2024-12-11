package com.example.home.Adapters

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

class CircleOutlineProvider : ViewOutlineProvider() {

        override fun getOutline(view: View, outline: Outline) {
            val size = Math.min(view.width, view.height)
            outline.setOval(0, 0, size, size)
        }
    }

