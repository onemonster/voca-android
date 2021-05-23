package com.tedilabs.voca.model

data class Word(
    val id: Int,
    val word: String,
    val partOfSpeech: String,
    val definitions: List<String>,
    val pronunciation: String,
    val examples: List<Example>
) {
    companion object {
        val default = Word(
            id = 0,
            word = "VOCA",
            partOfSpeech = "proper noun",
            definitions = emptyList(),
            pronunciation = "VOCA",
            examples = emptyList()
        )
    }
}
