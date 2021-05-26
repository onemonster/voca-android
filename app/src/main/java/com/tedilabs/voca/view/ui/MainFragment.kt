package com.tedilabs.voca.view.ui

import android.app.Activity
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tedilabs.voca.R
import com.tedilabs.voca.analytics.EventLogger
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.fragment_main.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : BaseFragment(R.layout.fragment_main) {

    private lateinit var tts: TextToSpeech

    companion object {
        const val TAG = "Main"

        fun create() = MainFragment()
    }

    @Inject
    lateinit var eventLogger: EventLogger

    private val lockViewModel: LockViewModel by activityViewModels()
    private val wordViewModel: WordViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(requireContext()) { status ->
            when (status) {
                TextToSpeech.ERROR ->
                    Toast.makeText(context, "Text to speech error", Toast.LENGTH_SHORT).show()
                else -> tts.language = Locale.KOREA
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(requireActivity())
        observeViewModels()
    }

    override fun onPause() {
        super.onPause()
        tts.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }

    private fun initializeViews(activity: Activity) {
        setting_button.setOnClickListener {
            (activity as MainActivity).showSettings()
        }

        word_list_button.setOnClickListener {
            (activity as MainActivity).showWordLists()
        }

        sound_button.setOnClickListener {
            tts.speak(
                wordViewModel.word.word,
                TextToSpeech.QUEUE_FLUSH,
                null,
                wordViewModel.word.word
            )
        }

        use_as_lock_screen_button.setOnClickListener {
            if (Settings.canDrawOverlays(activity)) {
                lockViewModel.turnLockScreenOn()
            } else {
                (activity as MainActivity).requestOverlayPermission()
            }
        }

        unlock_button.setOnClickListener {
            activity.finish()
        }

        next_button.setOnClickListener {
            eventLogger.logClickNextWord(wordViewModel.word, lockViewModel.isLockScreen)
            wordViewModel.showNextWord()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, { Timber.e(it) })
        }

        prev_button.setOnClickListener {
            eventLogger.logClickPrevWord(wordViewModel.word, lockViewModel.isLockScreen)
            wordViewModel.showPrevWord()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, { Timber.e(it) })
        }

        definition_list.layoutManager = object : LinearLayoutManager(activity) {
            override fun canScrollVertically(): Boolean = false
        }
        definition_list.adapter = DefinitionAdapter()

        example_list.layoutManager = LinearLayoutManager(activity)
        example_list.adapter = ExampleAdapter {
            tts.speak(it, TextToSpeech.QUEUE_FLUSH, null, it)
        }
    }

    private fun observeViewModels() {
        lockViewModel.observeUseOnLockScreen()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ lockScreenOn ->
                use_as_lock_screen_button.visibility = if (lockScreenOn) View.GONE else View.VISIBLE
            }, {
                Timber.e(it)
            })
            .disposeOnDestroyView()

        lockViewModel.observeIsLockScreen()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ isLockScreen ->
                unlock_button.visibility = if (isLockScreen) View.VISIBLE else View.GONE
            }, {
                Timber.e(it)
            })
            .disposeOnDestroyView()

        wordViewModel.observeWord()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ word ->
                subject_word_text.text = word.word
                subject_type_text.text = word.partOfSpeech
                subject_pronunciation_text.text = word.pronunciation
                (definition_list.adapter as DefinitionAdapter).definitions = word.definitions
                (example_list.adapter as ExampleAdapter).examples = word.examples
                prev_button.visibility = if (word.id == 1) View.INVISIBLE else View.VISIBLE
            }, {
                Timber.e(it)
            })
            .disposeOnDestroyView()

        wordViewModel.observeWordList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                word_list_button.text = it.value()?.name ?: ""
            }, {
                Timber.e(it)
            })
            .disposeOnDestroyView()

        // Check if word is last in list
        Observable.combineLatest(
            wordViewModel.observeWordCount(),
            wordViewModel.observeWord(),
            { count, word -> count == word.id }
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                next_button.visibility = if (it) View.INVISIBLE else View.VISIBLE
            }, {
                Timber.e(it)
            })

    }
}
