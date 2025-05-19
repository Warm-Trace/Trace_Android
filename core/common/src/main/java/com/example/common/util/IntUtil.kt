package com.example.common.util

fun Int.formatCount(): String {
    return when {
        this >= 10_000 -> {
            val value = this / 10_000.0
            String.format("%.1f만", value)
        }
        else -> {
            String.format("%,d", this)
        }
    }
}