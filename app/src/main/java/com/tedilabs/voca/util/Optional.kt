package com.tedilabs.voca.util

import io.reactivex.rxjava3.core.Observable

sealed class Optional<out T> {
    object None : Optional<Nothing>()
    data class Some<out T>(val _value: T) : Optional<T>()

    fun value(): T? = when (this) {
        None -> null
        is Some -> this._value
    }

    fun hasSome(): Boolean = this != None

    companion object {
        fun <T> from(value: T?): Optional<T> =
            value?.let { Some(value) } ?: None

        fun <T> empty(): Optional<T> = None
    }
}

fun <T> Observable<Optional<T>>.unwrapOptional(): Observable<T> {
    return this
        .filter { it.hasSome() }
        .map { it.value()!! }
}
