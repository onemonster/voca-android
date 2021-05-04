package com.tedilabs.voca.view.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tedilabs.voca.R
import kotlinx.android.synthetic.main.item_definition.view.*

class DefinitionAdapter : RecyclerView.Adapter<DefinitionAdapter.DefinitionViewHolder>() {

    var definitions: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefinitionViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_definition, parent, false)

        return DefinitionViewHolder(view)
    }

    override fun onBindViewHolder(holder: DefinitionViewHolder, position: Int) {
        definitions.getOrNull(position)?.let { holder.bind(position + 1, it) }
    }

    override fun getItemCount(): Int = definitions.size

    class DefinitionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(order: Int, definition: String) {
            itemView.number_text.text = "$order."
            itemView.definition_text.text = definition
        }
    }
}
