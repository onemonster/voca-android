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
import com.tedilabs.voca.model.Example
import dagger.hilt.android.AndroidEntryPoint
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
        }

        prev_button.setOnClickListener {
            eventLogger.logClickPrevWord(wordViewModel.word, lockViewModel.isLockScreen)
            wordViewModel.showPrevWord()
        }

        definition_list.layoutManager = object : LinearLayoutManager(activity) {
            override fun canScrollVertically(): Boolean = false
        }
        definition_list.adapter = DefinitionAdapter().apply {
            // Temporary
            definitions = listOf("Apple", "Red Apple")
        }

        example_list.layoutManager = LinearLayoutManager(activity)
        example_list.adapter = ExampleAdapter {
            tts.speak(it, TextToSpeech.QUEUE_FLUSH, null, it)
        }.apply {
            // Temporary
            examples = listOf(
                Example(
                    "나는 공부를 열심히 하는 학생입니다. 이것은 한국말 예문입니다.",
                    "I am a student so I study hard."
                ),
                Example(
                    "나는 공부를 열심히 하는 학생입니다. 이것은 한국말 예문입니다.",
                    "I am a student so I study hard."
                ),
                Example(
                    "나는 공부를 열심히 하는 학생입니다. 이것은 한국말 예문입니다.",
                    "I am a student so I study hard."
                ),
                Example(
                    "나는 공부를 열심히 하는 학생입니다. 이것은 한국말 예문입니다.",
                    "I am a student so I study hard."
                ),
                Example(
                    "나는 공부를 열심히 하는 학생입니다. 이것은 한국말 예문입니다.",
                    "I am a student so I study hard."
                ),
                Example(
                    "나는 공부를 열심히 하는 학생입니다. 이것은 한국말 예문입니다.",
                    "I am a student so I study hard."
                ),
                Example(
                    "나는 공부를 열심히 하는 학생입니다. 이것은 한국말 예문입니다.",
                    "I am a student so I study hard."
                ),
                Example(
                    "나는 공부를 열심히 하는 학생입니다. 이것은 한국말 예문입니다.",
                    "I am a student so I study hard."
                ),
            )
        }
    }

    private fun observeViewModels() {
        lockViewModel.observeUseOnLockScreen()
            .subscribe({ lockScreenOn ->
                use_as_lock_screen_button.visibility = if (lockScreenOn) View.GONE else View.VISIBLE
            }, {
                Timber.e(it)
            })
            .disposeOnDestroyView()

        lockViewModel.observeIsLockScreen()
            .subscribe({ isLockScreen ->
                unlock_button.visibility = if (isLockScreen) View.VISIBLE else View.GONE
            }, {
                Timber.e(it)
            })
            .disposeOnDestroyView()

        wordViewModel.observeWord()
            .subscribe({ word ->
                // Temporary
                subject_word_text.text = word.word
                subject_type_text.text = word.partOfSpeech
                subject_pronunciation_text.text = word.pronunciation
            }, {
                Timber.e(it)
            })
            .disposeOnDestroyView()
    }
}
