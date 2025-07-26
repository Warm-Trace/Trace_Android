package com.virtuous.common.util

import java.util.Locale


fun Int.formatCount(): String {
    return when {
        this >= 10_000 -> {
            val value = this / 10_000.0
            String.format(Locale.getDefault(), "%.1f만", value)
        }

        else -> {
            String.format(Locale.getDefault(), "%,d", this)
        }
    }
}