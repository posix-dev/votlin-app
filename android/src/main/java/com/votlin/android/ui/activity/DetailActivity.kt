package com.votlin.android.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.WindowManager
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.votlin.android.R
import com.votlin.android.extensions.hideMe
import com.votlin.android.extensions.showMe
import com.votlin.android.navigator.openUri
import com.votlin.android.ui.adapter.SpeakersAdapter
import com.votlin.common.client.presentation.DetailPresenter
import com.votlin.common.client.presentation.DetailView
import com.votlin.common.model.Talk
import com.votlin.common.model.Track
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : RootActivity<DetailView>(), DetailView {

    companion object {
        fun getCallingIntent(context: Context, talkId: Int): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(TALK_ID, talkId)
            return intent
        }

        const val TALK_ID = "TALK_ID"
    }

    override val presenter: DetailPresenter by instance()

    override val layoutResourceId: Int = R.layout.activity_detail

    override val activityModule: Kodein.Module = Kodein.Module {
        bind<DetailPresenter>() with provider {
            DetailPresenter(
                    view = this@DetailActivity,
                    errorHandler = instance(),
                    executor = instance(),
                    repository = instance()
            )
        }
    }

    private val adapter = SpeakersAdapter(
            onLinkedInClicked = { openUri(context = this, url = it) },
            onTwitterClicked = { openUri(context = this, url = it) }
    )

    override fun showProgress() {
        progressView.showMe()
    }

    override fun hideProgress() {
        progressView.hideMe()
    }

    override fun initializeUI() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        speakers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        speakers.adapter = adapter
    }

    override fun registerListeners() {
        rate.setOnRatingBarChangeListener { _, rate, _ ->
            presenter.onRateChange(rate = rate.toInt())
        }
    }

    override fun getTalkId(): Int {
        return intent.extras.getInt(TALK_ID)
    }

    override fun navigateToList() {
        finish()
    }

    override fun showRate(value: Int) {
        rate.rating = value.toFloat()
    }

    override fun showTalk(talk: Talk) {
        val color = when (talk.track) {
            Track.BUSINESS -> R.color.track_business
            Track.DEVELOPMENT -> {
                val darkColor = ContextCompat.getColor(this, R.color.dark_title)
                toolbar.setTitleTextColor(darkColor)
                toolbar.navigationIcon?.setColorFilter(darkColor, PorterDuff.Mode.MULTIPLY)
                toolbar.setSubtitleTextColor(ContextCompat.getColor(this, R.color.dark_subtitle))
                R.color.track_development
            }
            Track.MAKER -> R.color.track_maker
            Track.ALL -> R.color.track_all
        }
        val compatColor = ContextCompat.getColor(this, color)
        supportActionBar?.title = talk.track.toString().toLowerCase().capitalize()
        supportActionBar?.subtitle = talk.name
        supportActionBar?.setBackgroundDrawable(ColorDrawable(compatColor))
        description.text = talk.description


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, color)
        }

        adapter.replace(talk.speakers.toMutableList())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}