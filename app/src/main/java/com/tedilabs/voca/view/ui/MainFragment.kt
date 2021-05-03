package com.tedilabs.voca.view.ui

import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tedilabs.voca.R
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var tts: TextToSpeech

    companion object {
        const val TAG = "Main"
        fun create() = MainFragment()
    }

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

        // Temporary
        subject_word_text.text = "사과"
        subject_type_text.text = "NOUN"
        subject_pronunciation_text.text = "[사:과]"
        mother_word_text.text = "APPLE"
        mother_example_text.text = "There is a shop which sells delicious apple."
    }
}
