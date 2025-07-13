package com.virtuous.network.adapter

import android.util.Log
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TraceCallAdapterFactory @Inject constructor() : CallAdapter.Factory() {
    override fun get(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val wrapperType = getParameterUpperBound(0, type as ParameterizedType)
        if (getRawType(wrapperType) != Result::class.java) return null

        val resultType = getParameterUpperBound(0, wrapperType as ParameterizedType)
        return TraceCallAdapter(resultType)
    }
}

private class TraceCallAdapter(
    private val resultType: Type,
) : CallAdapter<Type, Call<Result<Type>>> {
    override fun responseType(): Type = resultType

    override fun adapt(call: Call<Type>): Call<Result<Type>> = TraceCall(call)
}

private class TraceCall<T : Any>(
    private val delegate: Call<T>
) : Call<Result<T>> {

    override fun enqueue(callback: Callback<Result<T>>) {
        delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    Log.d("traceResponse", "${response.raw()} body ${body}")
                    callback.onResponse(
                        this@TraceCall,
                        Response.success(Result.success(body))

                    )
                } else {
                    callback.onResponse(
                        this@TraceCall,
                        Response.success(Result.failure(RuntimeException("HTTP ${response.code()}: ${response.message()} body: ${response.errorBody()?.string()}")))
                    )
                }
            }

            override fun onFailure(call: Call<T>, throwable: Throwable) {
                Log.d("traceResponseFailure", "$throwable")

                callback.onResponse(
                    this@TraceCall,
                    Response.success(Result.failure(throwable as Exception))
                )
            }

        })
    }

    override fun clone(): Call<Result<T>> = TraceCall(delegate.clone())
    override fun execute(): Response<Result<T>> = throw NotImplementedError("TraceCall doesn't support execute()")
    override fun isExecuted(): Boolean = delegate.isExecuted
    override fun cancel() = delegate.cancel()
    override fun isCanceled(): Boolean = delegate.isCanceled
    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout = delegate.timeout()
}

