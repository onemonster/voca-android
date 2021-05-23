package com.tedilabs.voca.view.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tedilabs.voca.R
import com.tedilabs.voca.model.WordList
import kotlinx.android.synthetic.main.item_word_list.view.*

class WordListAdapter(private val onItemClick: (WordList) -> Unit) :
    RecyclerView.Adapter<WordListAdapter.WordListViewHolder>() {

    var wordLists: List<WordList> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var wordList: WordList? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordListViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_word_list, parent, false)

        return WordListViewHolder(view).apply {
            itemView.setOnClickListener {
                adapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let {
                    onItemClick(wordLists[it])
                }
            }
        }
    }

    override fun onBindViewHolder(holder: WordListViewHolder, position: Int) {
        wordLists.getOrNull(position)?.let { holder.bind(it, it.name == wordList?.name) }
    }

    override fun getItemCount(): Int = wordLists.size

    class WordListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(wordList: WordList, isSelected: Boolean) {
            itemView.word_list_name_text.text = wordList.name
            itemView.word_list_checkbox.isActivated = isSelected
        }
    }
}
