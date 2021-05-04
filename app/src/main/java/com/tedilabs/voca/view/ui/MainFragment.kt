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
import com.tedilabs.voca.model.Example
import kotlinx.android.synthetic.main.fragment_main.*
import timber.log.Timber
import java.util.*

class MainFragment : BaseFragment(R.layout.fragment_main) {

    private lateinit var tts: TextToSpeech

    companion object {
        const val TAG = "Main"

        private const val LOCK_SCREEN_KEY = "lock-screen"

        fun create(lockScreenOn: Boolean) = MainFragment().apply {
            arguments = Bundle().apply { putBoolean(LOCK_SCREEN_KEY, lockScreenOn) }
        }
    }

    private val lockViewModel: LockViewModel by activityViewModels()

    private val isLockScreen: Boolean
        get() = arguments?.getBoolean(LOCK_SCREEN_KEY) ?: false

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
            tts.speak("사과", TextToSpeech.QUEUE_FLUSH, null, "사과")
        }

        unlock_button.visibility = if (isLockScreen) View.VISIBLE else View.GONE

        use_as_lock_screen_button.setOnClickListener {
            if (Settings.canDrawOverlays(activity)) {
                lockViewModel.turnLockScreenOn()
            } else {
                (activity as MainActivity).requestOverlayPermission()
            }
        }

        // Temporary
        subject_word_text.text = "사과"
        subject_type_text.text = "NOUN"
        subject_pronunciation_text.text = "[사:과]"

        definition_list.layoutManager = object : LinearLayoutManager(activity) {
            override fun canScrollVertically(): Boolean = false
        }
        definition_list.adapter = DefinitionAdapter().apply {
            definitions = listOf("Apple", "Red Apple")
        }

        example_list.layoutManager = LinearLayoutManager(activity)
        example_list.adapter = ExampleAdapter {
            tts.speak(it, TextToSpeech.QUEUE_FLUSH, null, it)
        }.apply {
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
        lockViewModel.observeLockScreenOn()
            .subscribe({ lockScreenOn ->
                use_as_lock_screen_button.visibility = if (lockScreenOn) View.GONE else View.VISIBLE
            }, {
                Timber.e(it)
            })
            .disposeOnDestroyView()
    }
}
