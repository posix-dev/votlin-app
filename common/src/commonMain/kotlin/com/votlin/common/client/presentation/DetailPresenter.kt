package com.votlin.common.client.presentation

import com.votlin.common.client.domain.error.ErrorHandler
import com.votlin.common.client.domain.executor.Executor
import com.votlin.common.client.domain.repository.Repository
import com.votlin.common.client.domain.usecase.getTalkDetail
import com.votlin.common.client.domain.usecase.getTalkRate
import com.votlin.common.client.domain.usecase.rateTalk
import com.votlin.common.model.Rate
import com.votlin.common.model.Talk
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DetailPresenter(private val repository: Repository, private val executor: Executor, errorHandler: ErrorHandler, view: DetailView)
    : Presenter<DetailView>(errorHandler, view) {

    override fun initialize() {
        view.showProgress()
        GlobalScope.launch(executor.main) {
            val talk = getTalkDetail(view.getTalkId(), repository)

            view.showTalk(talk)
            view.hideProgress()
        }

        val rate = getTalkRate(view.getTalkId(), repository)
        view.showRate(rate)
    }

    override fun destroy() {
        // Nothing to do yet
    }

    fun onBackClicked() {
        view.navigateToList()
    }

    fun onRateChange(rate: Int) {
        GlobalScope.launch(executor.main) {
            rateTalk(Rate(id = view.getTalkId(), value = rate), repository)
            view.showRate(rate)
        }
    }

}

interface DetailView : Presenter.View {
    fun getTalkId(): Int
    fun showTalk(talk: Talk)
    fun navigateToList()
    fun showRate(rate: Int)
}