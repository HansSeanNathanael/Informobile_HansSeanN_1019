package com.tainka.pvts.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tainka.pvts.data.DataSeasons
import com.tainka.pvts.databinding.SeasonCardBinding

class SeasonViewAdapter(var seasonPageActivity : SeasonPageActivity) : RecyclerView.Adapter<SeasonViewAdapter.SeasonViewHolder>() {

    private val listSeasonCard = ArrayList<DataSeasons>()

    fun setListItem(list : List<DataSeasons>)
    {
        listSeasonCard.clear()
        listSeasonCard.addAll(list)

        notifyDataSetChanged()
    }

    fun setParentActivity(activity: SeasonPageActivity)
    {
        seasonPageActivity = activity
    }

    inner class SeasonViewHolder(private val binding : SeasonCardBinding) : RecyclerView.ViewHolder(binding.root)
    {
        private lateinit var dataSeasons : DataSeasons
        var pos = 0

        fun bind(data : DataSeasons, position: Int)
        {
            pos = position + 1
            dataSeasons = data
            binding.seasonTitle.text = dataSeasons.title

            binding.root.setOnClickListener {
                seasonPageActivity.apply {
                    this.processPage(dataSeasons, pos)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {
        var itemBinding = SeasonCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SeasonViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
        var dataSeasons = listSeasonCard[position]
        holder.bind(dataSeasons, position)
    }

    override fun getItemCount(): Int {
        return listSeasonCard.size
    }
}