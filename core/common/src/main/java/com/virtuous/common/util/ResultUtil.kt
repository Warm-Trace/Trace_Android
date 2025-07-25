package com.virtuous.common.util

import kotlin.coroutines.cancellation.CancellationException


suspend inline fun <T, R> T.suspendRunCatching(crossinline block: suspend T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (t: Throwable) {
        println("RunCatching Exception: ${t}")
        Result.failure(t)
    }
}