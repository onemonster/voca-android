package com.tedilabs.voca.view.ui

import androidx.annotation.ContentView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseActivity : AppCompatActivity {

    @ContentView
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)
    constructor() : super()

    private val onPauseCompositeDisposable = CompositeDisposable()
    private val onDestroyCompositeDisposable = CompositeDisposable()

    override fun onPause() {
        super.onPause()
        onPauseCompositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroyCompositeDisposable.clear()
    }

    protected fun Disposable.disposeOnPause(): Disposable = this.apply {
        onPauseCompositeDisposable.add(this)
    }

    protected fun Disposable.disposeOnDestroy(): Disposable = this.apply {
        onDestroyCompositeDisposable.add(this)
    }
}
