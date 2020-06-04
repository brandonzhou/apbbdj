package com.shshcom.module_base.network

import androidx.lifecycle.liveData
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.*
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * desc:
 * author: zhhli
 * 2020/5/30
 */
open class KNetwork {
//    open suspend fun <T> Call<T>.await(): T {
//        return suspendCoroutine { continuation ->
//            enqueue(object : Callback<T> {
//                override fun onResponse(call: Call<T>, response: Response<T>) {
//                    val body = response.body()
//                    if (body != null) continuation.resume(body)
//                    else continuation.resumeWithException(RuntimeException("response body is null"))
//                }
//                override fun onFailure(call: Call<T>, t: Throwable) {
//                    continuation.resumeWithException(t)
//                }
//            })
//        }
//    }


    suspend fun <T : Any> Call<T>.await(): T {
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                cancel()
            }
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body == null) {
                            val invocation = call.request().tag(Invocation::class.java)!!
                            val method = invocation.method()
                            val e = KotlinNullPointerException("Response from " +
                                    method.declaringClass.name +
                                    '.' +
                                    method.name +
                                    " was null but response body type was declared as non-null")
                            continuation.resumeWithException(e)
                        } else {
                            continuation.resume(body)
                        }
                    } else {
                        continuation.resumeWithException(HttpException(response))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    open fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
            liveData<Result<T>>(context) {
                val result = try {
                    block()
                } catch (e: Exception) {
                    Result.failure<T>(e)
                }
                emit(result)
            }


    fun <T> processApiResponse(response: Response<T>): Results<T> {
        return try {
            val responseCode = response.code()
            val responseMessage = response.message()
            if (response.isSuccessful) {
                Results.success(response.body()!!)
            } else {
                Results.failure(Errors.NetworkError(responseCode, responseMessage))
            }
        } catch (e: IOException) {
            Results.failure(Errors.NetworkError())
        }
    }




}

sealed class Results<out T> {

    companion object {
        fun <T> success(result: T): Results<T> = Success(result)
        fun <T> failure(error: Throwable): Results<T> = Failure(error)
    }

    data class Failure(val error: Throwable) : Results<Nothing>()
    data class Success<out T>(val data: T) : Results<T>()
}

sealed class Errors : Throwable() {
    data class NetworkError(val code: Int = -1, val desc: String = "") : Errors()
    object EmptyInputError : Errors()
    object EmptyResultsError : Errors()
    object SingleError : Errors()
}

