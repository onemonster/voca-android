package com.tedilabs.voca.model

data class WordList(
    val name: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val version: String,
    val url: String
)
