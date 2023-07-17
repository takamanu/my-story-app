package com.example.mystory.ui.stories

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mystory.vm.DetailViewModel
import com.example.mystory.R
import com.example.mystory.databinding.ActivityDetailStoryBinding
import com.example.mystory.data.ListStoryItem

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var story: ListStoryItem
    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var detailViewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        overridePendingTransition(R.anim.fade_in, 0)

        detailViewModel = ViewModelProvider(this).get(DetailViewModel::class.java)


        story = intent.getParcelableExtra(EXTRA_STORY)!!
        detailViewModel.setDetailStory(story)

        binding.backButton.setOnClickListener {
            val mainActivity = Intent(this, MainActivity::class.java)
            startActivity(mainActivity)
        }

        with(binding) {
            tvDetailName.text = detailViewModel.storyItem.name
//            tvDate.text = getString(
//                R.string.date, Utils.formatDate(
//                    detailViewModel.storyItem.createdAt,
//                    TimeZone.getDefault().id
//                )
//            )
            itvDetailDesc.text = detailViewModel.storyItem.description

            Glide.with(ivStory)
                .load(detailViewModel.storyItem.photoUrl)
                .into(ivStory)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Apply fade-out animation
        overridePendingTransition(0, R.anim.fade_out)
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}
