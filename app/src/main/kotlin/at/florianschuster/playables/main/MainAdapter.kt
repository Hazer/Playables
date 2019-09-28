package at.florianschuster.playables.main

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = MainPages.values().size

    override fun createFragment(position: Int): Fragment =
        MainPages.values()[position].creator()
}