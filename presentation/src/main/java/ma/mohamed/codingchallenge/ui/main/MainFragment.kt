package ma.mohamed.codingchallenge.ui.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.android.support.DaggerFragment
import ma.mohamed.codingchallenge.R
import ma.mohamed.codingchallenge.data.model.LoadingState
import ma.mohamed.codingchallenge.extensions.hide
import ma.mohamed.codingchallenge.extensions.lazyUnSync
import ma.mohamed.codingchallenge.extensions.show
import ma.mohamed.codingchallenge.ui.main.adapter.RepoAdapter
import timber.log.Timber
import javax.inject.Inject

class MainFragment : DaggerFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by lazyUnSync { ViewModelProviders.of(this, factory)[MainViewModel::class.java] }

    private lateinit var emptyView: View
    private lateinit var emptyViewError: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var repoAdapter: RepoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        emptyView = view.findViewById(R.id.empty_view)
        emptyViewError = view.findViewById(R.id.error)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        recyclerView = view.findViewById(R.id.recycler_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repoAdapter = RepoAdapter({
            val cti = CustomTabsIntent.Builder()
                .enableUrlBarHiding()
                .setShowTitle(true)
                .build()
            cti.launchUrl(activity, Uri.parse(it.url))
        }, viewModel::retry)

        recyclerView.apply {
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = repoAdapter
        }

        swipeRefresh.setOnRefreshListener(viewModel::refresh)
        watchData()
    }

    private fun watchData() {
        viewModel.repos.observe(this, Observer {
            Timber.i("Got ${it.size} results")
            repoAdapter.submitList(it)
        })
        viewModel.loadingState.observe(this, Observer {
            Timber.i(it.state.name)
            repoAdapter.setLoadingState(it)
            swipeRefresh.isRefreshing = it == LoadingState.INITIAL_LOADING

            if (it.state == LoadingState.State.INITIAL_LOADING_ERROR) {
                emptyView.show()
                recyclerView.hide()
                emptyViewError.text = it.msg ?: activity?.getString(R.string.unknown_error)
            } else {
                emptyView.hide()
                recyclerView.show()
            }
        })
    }
}