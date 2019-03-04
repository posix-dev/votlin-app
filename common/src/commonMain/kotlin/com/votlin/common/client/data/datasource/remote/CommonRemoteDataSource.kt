package com.votlin.common.client.data.datasource.remote

import com.votlin.common.model.Rate
import com.votlin.common.model.Talk
import com.votlin.common.model.TalksResponse
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.takeFrom

class CommonRemoteDataSource : RemoteDataSource {

    private val endPoint: String = "http://sergiocasero.es:10000"

    private val client: HttpClient = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer().apply {
                // setMapper(Talk::class, Talk.serializer())
                // setMapper(TalksResponse::class, TalksResponse.serializer())
                // setMapper(Speaker::class, Speaker.serializer())
                // setMapper(Time::class, Time.serializer())
                // setMapper(Rate::class, Rate.serializer())
            }
        }
    }

    override suspend fun getTalks(): List<Talk> = client.get<TalksResponse> { apiUrl("talk") }.talks

    override suspend fun getTalk(talkId: Int): Talk = client.get { apiUrl("talk/$talkId") }

    override suspend fun getTalksByTrack(track: String): List<Talk> =
        client.get<TalksResponse> { apiUrl("talk/$track") }.talks

    override suspend fun rateTalk(rate: Rate): Unit = client.post {
        apiUrl("talk/rate")
        body = rate
    }

    private fun HttpRequestBuilder.apiUrl(path: String) {
        url {
            takeFrom(endPoint)
            encodedPath = path
        }
    }
}