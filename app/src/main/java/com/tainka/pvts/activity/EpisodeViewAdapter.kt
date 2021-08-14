package com.tainka.pvts.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tainka.pvts.data.DataEpisodes
import com.tainka.pvts.databinding.EpisodeCardBinding

class EpisodeViewAdapter(var episodePageActivity: EpisodePageActivity) : RecyclerView.Adapter<EpisodeViewAdapter.EpisodeViewHolder>() {

    private val listEpisodeItem : MutableList<DataEpisodes> = mutableListOf()

    fun setListItem(list : List<DataEpisodes>)
    {
        listEpisodeItem.clear()
        listEpisodeItem.addAll(list)

        notifyDataSetChanged()
    }

    fun setParentActivity(parentActivity: EpisodePageActivity)
    {
        episodePageActivity = parentActivity
    }

    inner class EpisodeViewHolder(var binding: EpisodeCardBinding) : RecyclerView.ViewHolder(binding.root)
    {
        lateinit var dataEpisodes : DataEpisodes
        var pos = 0

        fun bind(data: DataEpisodes, position : Int)
        {
            pos = position + 1
            dataEpisodes = data
            binding.episodeTitle.text = dataEpisodes.title

            binding.root.setOnClickListener {
                episodePageActivity.apply {
                    this.processPage(dataEpisodes, pos)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val itemBinding = EpisodeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return EpisodeViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        var itemData = listEpisodeItem[position]

        holder.bind(itemData, position)
    }

    override fun getItemCount(): Int {
        return listEpisodeItem.size
    }
}