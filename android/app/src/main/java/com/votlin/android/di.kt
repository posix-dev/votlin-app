package com.votlin.android

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import com.votlin.android.storage.AndroidLocalDataSource
import com.votlin.client.data.datasource.local.LocalDataSource
import com.votlin.client.data.datasource.remote.CommonRemoteDataSource
import com.votlin.client.data.datasource.remote.RemoteDataSource
import com.votlin.client.domain.repository.CommonRepository
import com.votlin.client.domain.repository.Repository

fun app(context: Context) = Kodein.Module {
    bind<Context>() with singleton { context }
    bind<LocalDataSource>() with singleton { AndroidLocalDataSource() }
    bind<RemoteDataSource>() with singleton { CommonRemoteDataSource() }
    bind<Repository>() with singleton { CommonRepository(remote = instance(), local = instance()) }
}