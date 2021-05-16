package com.tedilabs.voca.model

data class WordList(
    val name: String,
    val sourceLanguage: String,
    val targetLanguage: String,
    val version: String,
    val url: String
) {
    val key = WordListKey(
        name = name,
        version = version
    )

    companion object {
        val default = WordList(
            name = "",
            sourceLanguage = "",
            targetLanguage = "",
            version = "",
            url = ""
        )
    }
}

data class WordListKey(
    val name: String,
    val version: String
)
