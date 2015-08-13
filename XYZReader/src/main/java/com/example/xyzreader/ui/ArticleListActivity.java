package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleAdapter;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, ArticleAdapter.OnItemClickListener {

    private static final String KEY_LAYOUT_MANAGER_STATE = "ArticleListActivity.KEY_LAYOUT_MANAGER_STATE";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ArticleAdapter mAdapter;

    private Button mEmptyViewAction;
    private ImageView mEmptyImageView;
    private TextView mEmptyViewText;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        mAdapter = new ArticleAdapter();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        initializeViewReferences();
        setSupportActionBar(mToolbar);
        setEventListeners();
        configureRecycleView(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
        if(savedInstanceState == null){
            refresh();
        }
    }

    private void initializeViewReferences(){
        mRecyclerView = (RecyclerView) findViewById(R.id.articleList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mEmptyImageView = (ImageView) findViewById(R.id.emptyImage);
        mEmptyViewText = (TextView) findViewById(R.id.emptyText);
        mEmptyViewAction = (Button) findViewById(R.id.emptyButton);
        mEmptyView = findViewById(R.id.emptyView);
    }

    private void setEventListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void configureRecycleView(Bundle savedInstanceState){
        RecyclerView.LayoutManager layoutManager = createLayoutManager();
        if(savedInstanceState != null){
            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_LAYOUT_MANAGER_STATE));
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private RecyclerView.LayoutManager createLayoutManager(){
        int columnCountNeeded = getResources().getInteger(R.integer.list_column_count);
        return new StaggeredGridLayoutManager(columnCountNeeded, StaggeredGridLayoutManager.VERTICAL);
    }

    private void refresh() {
        configureEmptyViewForNoItems();
        toggleEmptyView(false);
        startService(new Intent(this, UpdaterService.class));
        getLoaderManager().restartLoader(0,null, this).forceLoad();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void onPause() {
        mAdapter.setOnItemClickListener(null);
        super.onPause();
    }

    private boolean mIsRefreshing = false;

    private final BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();

                boolean connectionIsDown = intent.getBooleanExtra(UpdaterService.EXTRA_NO_INTERNET, false);
                if(connectionIsDown){
                    Snackbar.make(
                            findViewById(R.id.coordinatorLayout),
                            R.string.message_no_internet,
                            Snackbar.LENGTH_LONG)
                            .show();
                }

                boolean errorOccurred = intent.getBooleanExtra(UpdaterService.EXTRA_ERROR_OCCURRED, false);
                if (errorOccurred){
                    configureEmptyViewForRefreshError();
                }

                toggleEmptyView(mAdapter.getItemCount() == 0);
            }
        }
    };

    private void configureEmptyViewForNoItems() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        };
        configureEmptyView(
                0,
                R.string.empty_view_no_items,
                R.string.action_refresh,
                listener);
    }

    private void configureEmptyViewForRefreshError() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        };
        configureEmptyView(
                R.drawable.error_illustration,
                R.string.empty_view_error_refreshing,
                R.string.action_retry,
                listener);
    }

    private void configureEmptyView(
            @DrawableRes int imageId,
            @StringRes int messageId,
            @StringRes int buttonTextId,
            View.OnClickListener clickListener){
        mEmptyImageView.setImageResource(imageId);
        mEmptyImageView.setVisibility(imageId != 0 ? View.VISIBLE : View.GONE);
        mEmptyViewText.setText(messageId);
        mEmptyViewAction.setText(buttonTextId);
        mEmptyViewAction.setOnClickListener(clickListener);
    }

    private void toggleEmptyView(boolean show){
        int visibility = show? View.VISIBLE : View.INVISIBLE;
        mEmptyView.setVisibility(visibility);
    }

    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        toggleEmptyView(mAdapter.getItemCount() == 0);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(int position) {
        Uri articleUri = ItemsContract.Items.buildItemUri(mAdapter.getItemId(position));
        startActivity(new Intent(Intent.ACTION_VIEW, articleUri));
    }
}
