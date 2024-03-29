package com.jjcr11.rss.data.network

import com.jjcr11.rss.data.model.Rss
import com.jjcr11.rss.data.model.RssContent
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun getChannel(@Url url: String): Response<Rss>

    @GET
    suspend fun getChannelContent(@Url url: String): Response<RssContent>
}