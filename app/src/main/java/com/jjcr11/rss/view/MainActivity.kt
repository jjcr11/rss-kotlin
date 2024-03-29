package com.jjcr11.rss.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jjcr11.rss.databinding.ActivityMainBinding

//class MainActivity : AppCompatActivity(), FeedAdapterOnClickListener {
class MainActivity : AppCompatActivity() {

    /*private lateinit var binding: ActivityMainBinding
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var onBoardingBinding: OnBoardingBinding
    private var ready: Boolean = false*/
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        /*installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        onBoardingBinding = OnBoardingBinding.inflate(layoutInflater)

        sharedPreference = getSharedPreferences("settings", Context.MODE_PRIVATE)

        if(sharedPreference.getBoolean("onBoarding", false)) {
            setContentView(binding.root)
        } else {
            setContentView(onBoardingBinding.root)
            val pages = 5
            onBoardingBinding.vp.adapter = OnBoardingAdapter(this, pages)
            onBoardingBinding.vp.isUserInputEnabled = false
            onBoardingBinding.mcvNext.setOnClickListener {
                if (onBoardingBinding.vp.currentItem == pages - 1) {
                    setContentView(binding.root)
                    sharedPreference.edit().putBoolean("onBoarding", true).apply()
                } else {
                    onBoardingBinding.vp.currentItem = onBoardingBinding.vp.currentItem + 1
                }
            }
            onBoardingBinding.mcvPrev.setOnClickListener {
                onBoardingBinding.vp.currentItem = onBoardingBinding.vp.currentItem - 1
            }
        }




        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (ready) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )

        val workRequest = PeriodicWorkRequestBuilder<DeleteOldDataWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager
            .getInstance(this)
            .enqueue(workRequest)


        when(sharedPreference.getInt("theme", 2)) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        binding.mtb.menu.getItem(1).icon = if(sharedPreference.getBoolean("sort", true)) {
            ContextCompat.getDrawable(this, R.drawable.ic_sort)
        } else {
            ContextCompat.getDrawable(this, R.drawable.ic_sort_2)
        }

        feedAdapter = FeedAdapter(mutableListOf(), sharedPreference.getInt("cornerRadius", 0), this)

        linearLayoutManager = LinearLayoutManager(this)

        binding.rv.apply {
            layoutManager = linearLayoutManager
            adapter = feedAdapter
        }

        deleteData()
        getData()

        //If the recycler view scrolls then floating action button extends or shrinks
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.rv.setOnScrollChangeListener { _, _, _, _, oldScrollY ->
                if(oldScrollY >= 0) {
                    binding.fabe.extend()
                } else {
                    binding.fabe.shrink()
                }
            }
        }

        binding.fabe.setOnClickListener {
            startActivity(Intent(this, SourceActivity::class.java))
        }

        binding.mtb.setNavigationOnClickListener {
            binding.dl.openDrawer(GravityCompat.START)
        }

        binding.nv.menu.apply {
            getItem(0).setOnMenuItemClickListener {
                startActivity(Intent(baseContext, SettingsActivity::class.java))
                true
            }

            getItem(1).setOnMenuItemClickListener {
                val savedActivity = Intent(baseContext, SavedActivity::class.java)
                savedActivity.putExtra("theme", binding.fabe.currentTextColor)
                startActivity(savedActivity)
                true
            }

            getItem(2).setOnMenuItemClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jjcr11/rss-kotlin")))
                true
            }

            getItem(3).setOnMenuItemClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.me/jjcr11")))
                true
            }
        }

        binding.mtb.menu.getItem(1).setOnMenuItemClickListener {
            sharedPreference.edit().putBoolean(
                "sort",
                !sharedPreference.getBoolean("sort", true)
            ).apply()
            binding.mtb.menu.getItem(1).icon = if(sharedPreference.getBoolean("sort", true)) {
                ContextCompat.getDrawable(this, R.drawable.ic_sort)
            } else {
                ContextCompat.getDrawable(this, R.drawable.ic_sort_2)
            }
            getFeeds(sharedPreference.getBoolean("sort", true))

            true
        }

        binding.srl.setOnRefreshListener {
            getData()
        }*/
    }
    override fun onResume() {
        super.onResume()
        //getFeeds(sharedPreference.getBoolean("sort", true))
    }

    /*private fun deleteData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val sources = DatabaseApplication.database.dao().getAllSources()
            for(source in sources) {
                val feedsById = DatabaseApplication.database.dao().getFeedsId(source.id)
                if(feedsById.size > source.count) {
                    val subList = feedsById.subList(0, (feedsById.size - source.count))
                    for(sub in subList) {
                        DatabaseApplication.database.dao().deleteFeedById(sub)
                    }
                }
            }
        }
    }

    private fun getData() {
        val connectivityManager = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        if(isConnected) {
            lifecycleScope.launch {
                val sources = withContext(Dispatchers.IO) {
                    DatabaseApplication.database.dao().getAllSources()
                }
                if(sources.size == 0) {
                    ready = true
                    binding.srl.isRefreshing = false
                } else {
                    for (source in sources) {
                        downloadXmlTask(source.url, source.id)
                    }
                }
                getFeeds(sharedPreference.getBoolean("sort", true))
            }
        } else {
            getFeeds(sharedPreference.getBoolean("sort", true))
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun downloadXmlTask(url: String?, id: Int) {
        val feeds = loadXmlFromNetwork(url, id)
        withContext(Dispatchers.IO) {
            DatabaseApplication.database.dao().setSourceCount(id, feeds.size)
            for(feed in feeds) {
                try {
                    DatabaseApplication.database.dao().addFeed(feed)
                } catch (e: SQLiteConstraintException) {

                }
            }
        }
    }


    private suspend fun loadXmlFromNetwork(urlString: String?, sourceId: Int): List<FeedEntity> {
        return withContext(Dispatchers.IO) {
            downloadUrl(urlString)?.use { stream ->
                XmlParser().parse(stream, sourceId)
            }!!
        }
    }

    private fun downloadUrl(urlString: String?): InputStream? {
        val url = URL(urlString)
        return (url.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 15000
            connectTimeout = 20000
            requestMethod = "GET"
            doInput = true
            connect()
            inputStream
        }
    }

    private fun getFeeds(sort: Boolean) {
        lifecycleScope.launch {
            val feeds = withContext(Dispatchers.IO) {
                if(sort) {
                    DatabaseApplication.database.dao().getUnreadFeeds()
                } else {
                    DatabaseApplication.database.dao().getUnreadFeedsDesc()
                }
            }
            binding.srl.isRefreshing = false
            feedAdapter.setFeeds(feeds)
            binding.mtb.menu.getItem(0).title = feedAdapter.itemCount.toString()
            ready = true
        }
    }

    override fun onClick(feed: FullFeedEntity, position: Int) {
        val postActivity = Intent(this, PostActivity::class.java)
        postActivity.putExtra("position", position)
        postActivity.putExtra("sort", sharedPreference.getBoolean("sort", true))
        postActivity.putExtra("theme", binding.fabe.currentTextColor)
        startActivity(postActivity)
    }*/
}