package com.tedilabs.voca.view.ui

import androidx.lifecycle.ViewModel
import com.tedilabs.voca.preference.AppPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@HiltViewModel
class LockViewModel @Inject constructor(
    private val appPreference: AppPreference
) : ViewModel() {

    private val useOnLockScreenSubject = BehaviorSubject.createDefault(appPreference.lockScreenOn)
    private val isLockScreenSubject = BehaviorSubject.createDefault(false)

    fun observeUseOnLockScreen(): Observable<Boolean> =
        useOnLockScreenSubject.distinctUntilChanged()

    fun observeIsLockScreen(): Observable<Boolean> =
        isLockScreenSubject.distinctUntilChanged()

    val isLockScreen: Boolean get() = isLockScreenSubject.value

    fun turnLockScreenOn() {
        appPreference.lockScreenOn = true
        useOnLockScreenSubject.onNext(true)
    }

    fun turnLockScreenOff() {
        appPreference.lockScreenOn = false
        useOnLockScreenSubject.onNext(false)
    }

    fun setIsLockScreen(isLockScreen: Boolean) {
        isLockScreenSubject.onNext(isLockScreen)
    }

    fun toggleLockScreen() {
        if (useOnLockScreenSubject.value) turnLockScreenOff() else turnLockScreenOn()
    }
}
