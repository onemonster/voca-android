package com.tedilabs.voca.view.ui


import androidx.annotation.ContentView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseFragment : Fragment {

    @ContentView
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)
    constructor() : super()

    private val onPauseCompositeDisposable = CompositeDisposable()
    private val onDestroyCompositeDisposable = CompositeDisposable()
    private val onDestroyViewCompositeDisposable = CompositeDisposable()
    private val onDetachCompositeDisposable = CompositeDisposable()

    override fun onPause() {
        super.onPause()
        onPauseCompositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroyCompositeDisposable.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestroyViewCompositeDisposable.clear()
    }

    override fun onDetach() {
        super.onDetach()
        onDetachCompositeDisposable.clear()
    }

    protected fun Disposable.disposeOnPause(): Disposable = this.apply {
        onPauseCompositeDisposable.add(this)
    }

    protected fun Disposable.disposeOnDestroy(): Disposable = this.apply {
        onDestroyCompositeDisposable.add(this)
    }

    protected fun Disposable.disposeOnDestroyView(): Disposable = this.apply {
        onDestroyViewCompositeDisposable.add(this)
    }

    protected fun Disposable.disposeOnDetach(): Disposable = this.apply {
        onDetachCompositeDisposable.add(this)
    }
}
