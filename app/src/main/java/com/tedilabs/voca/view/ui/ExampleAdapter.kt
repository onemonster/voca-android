package com.tedilabs.voca.view.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tedilabs.voca.R
import com.tedilabs.voca.model.Example
import kotlinx.android.synthetic.main.item_example.view.*

class ExampleAdapter(private val onSoundClick: (sentence: String) -> Unit) : RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder>() {

    var examples: List<Example> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_example, parent, false)

        return ExampleViewHolder(view).apply {
            itemView.sound_button.setOnClickListener {
                adapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let {
                    onSoundClick(examples[it].sentence)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        examples.getOrNull(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = examples.size

    class ExampleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(example: Example) {
            itemView.sentence_text.text = example.sentence
            itemView.translation_text.text = example.translation
        }
    }
}
