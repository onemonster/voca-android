package com.tedilabs.voca.model

import android.content.Context
import com.tedilabs.voca.R

internal enum class PartOfSpeech {
    EXCLAMATION,
    DETERMINER,
    PRONOUN,
    VERB,
    NOUN,
    ADVERB,
    NUMERALS,
    BOUND_NOUN,
    AFFIX,
    ADJECTIVE,
}

fun String.toPartOfSpeechString(context: Context): String = when (this) {
    PartOfSpeech.EXCLAMATION.name -> context.getString(R.string.exclamation)
    PartOfSpeech.DETERMINER.name -> context.getString(R.string.determiner)
    PartOfSpeech.PRONOUN.name -> context.getString(R.string.pronoun)
    PartOfSpeech.VERB.name -> context.getString(R.string.verb)
    PartOfSpeech.NOUN.name -> context.getString(R.string.noun)
    PartOfSpeech.ADVERB.name -> context.getString(R.string.adverb)
    PartOfSpeech.NUMERALS.name -> context.getString(R.string.numerals)
    PartOfSpeech.BOUND_NOUN.name -> context.getString(R.string.bound_noun)
    PartOfSpeech.AFFIX.name -> context.getString(R.string.affix)
    PartOfSpeech.ADJECTIVE.name -> context.getString(R.string.adjective)
    else -> this
}
