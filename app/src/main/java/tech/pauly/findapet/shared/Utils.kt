package tech.pauly.findapet.shared

import tech.pauly.findapet.BuildConfig

fun isDebug(): Boolean {
    return BuildConfig.DEBUG
}