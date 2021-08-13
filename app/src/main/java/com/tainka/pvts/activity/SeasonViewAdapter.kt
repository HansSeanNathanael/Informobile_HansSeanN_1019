package com.tainka.pvts.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tainka.pvts.data.DataSeasons
import com.tainka.pvts.databinding.SeasonCardBinding

class SeasonViewAdapter : RecyclerView.Adapter<SeasonViewAdapter.SeasonViewHolder>() {

    private val listSeasonCard = ArrayList<DataSeasons>()

    private lateinit var seasonPageActivity : SeasonPageActivity

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

        fun bind(data : DataSeasons)
        {
            dataSeasons = data
            binding.episodeTitle.text = dataSeasons.title

            binding.root.setOnClickListener {
                seasonPageActivity.apply {
                    this.processPage(dataSeasons)
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
        holder.bind(dataSeasons)
    }

    override fun getItemCount(): Int {
        return listSeasonCard.size
    }
}