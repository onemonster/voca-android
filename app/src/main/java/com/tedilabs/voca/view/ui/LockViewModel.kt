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

    private val lockScreenOnSubject = BehaviorSubject.createDefault(appPreference.lockScreenOn)
    private val isLockScreenSubject = BehaviorSubject.createDefault(false)

    fun observeLockScreenOn(): Observable<Boolean> = lockScreenOnSubject.distinctUntilChanged()
    fun observeIsLockScreen(): Observable<Boolean> = isLockScreenSubject.distinctUntilChanged()

    val isLockScreen: Boolean = isLockScreenSubject.value

    fun turnLockScreenOn() {
        appPreference.lockScreenOn = true
        lockScreenOnSubject.onNext(true)
    }

    fun turnLockScreenOff() {
        appPreference.lockScreenOn = false
        lockScreenOnSubject.onNext(false)
    }

    fun setIsLockScreen(isLockScreen: Boolean) {
        isLockScreenSubject.onNext(isLockScreen)
    }

    fun toggleLockScreen() {
        if (lockScreenOnSubject.value) turnLockScreenOff() else turnLockScreenOn()
    }
}
