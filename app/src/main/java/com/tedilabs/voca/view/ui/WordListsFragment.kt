package com.tedilabs.voca.view.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tedilabs.voca.R
import com.tedilabs.voca.util.unwrapOptional
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_word_lists.*
import timber.log.Timber

class WordListsFragment : BaseFragment(R.layout.fragment_word_lists) {

    companion object {
        const val TAG = "WordLists"
        fun create() = WordListsFragment()
    }

    private val wordViewModel: WordViewModel by activityViewModels()

    private lateinit var wordListAdapter: WordListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { context ->
            initializeViews(context)
            observeViewModels(context)
        }
    }

    private fun initializeViews(context: Activity) {
        back_button.setOnClickListener {
            context.onBackPressed()
        }

        word_list_list.layoutManager = LinearLayoutManager(context)
        wordListAdapter = WordListAdapter {
            wordViewModel.setWordList(it)
        }
        word_list_list.adapter = wordListAdapter
    }

    private fun observeViewModels(context: Activity) {
        wordViewModel.observeWordList()
            .observeOn(AndroidSchedulers.mainThread())
            .unwrapOptional()
            .subscribe({
                wordListAdapter.wordList = it
            }, {
                Timber.e(it)
            })
            .disposeOnDestroyView()

        wordViewModel.observeWordLists()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                wordListAdapter.wordLists = it
            }, {
                Timber.e(it)
            })
            .disposeOnDestroyView()
    }
}
