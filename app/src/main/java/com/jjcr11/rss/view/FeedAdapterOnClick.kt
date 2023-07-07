package com.jjcr11.rss.view

import com.jjcr11.rss.data.model.Feed

interface FeedAdapterOnClick {
    fun onClick(feeds: List<Feed>)
}