package com.tedilabs.voca.preference

import android.content.SharedPreferences
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
class PreferenceDelegate<T>(
    private val sharedPreferences: SharedPreferences,
    private val key: PrefKey,
    private val type: KClass<out Any>
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return when (type) {
            String::class -> sharedPreferences.getString(key.name, "")
            Boolean::class -> sharedPreferences.getBoolean(key.name, false)
            Float::class -> sharedPreferences.getFloat(key.name, 0f)
            Int::class -> sharedPreferences.getInt(key.name, 0)
            Long::class -> sharedPreferences.getLong(key.name, 0L)
            else -> throw IllegalStateException("Preference must be either String, Boolean, Float, Int or Long")
        } as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        with(sharedPreferences.edit()) {
            when (type) {
                String::class -> putString(key.name, value as String)
                Boolean::class -> putBoolean(key.name, value as Boolean)
                Float::class -> putFloat(key.name, value as Float)
                Int::class -> putInt(key.name, value as Int)
                Long::class -> putLong(key.name, value as Long)
                else -> throw IllegalStateException("Preference must be either String, Boolean, Float, Int or Long")
            }
            commit()
        }
    }
}

fun prefString(sharedPreferences: SharedPreferences, key: PrefKey) =
    PreferenceDelegate<String>(sharedPreferences, key, String::class)

fun prefBoolean(sharedPreferences: SharedPreferences, key: PrefKey) =
    PreferenceDelegate<Boolean>(sharedPreferences, key, Boolean::class)

fun prefFloat(sharedPreferences: SharedPreferences, key: PrefKey) =
    PreferenceDelegate<Float>(sharedPreferences, key, Float::class)

fun prefInt(sharedPreferences: SharedPreferences, key: PrefKey) =
    PreferenceDelegate<Int>(sharedPreferences, key, Int::class)

fun prefLong(sharedPreferences: SharedPreferences, key: PrefKey) =
    PreferenceDelegate<Long>(sharedPreferences, key, Long::class)
