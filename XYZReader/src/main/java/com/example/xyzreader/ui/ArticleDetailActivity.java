package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private long mStartId;

    private CollapsingToolbarLayout mToolbarLayout;
    private TextView mBodyTextView;
    private TextView mSubtitleTextView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null && getIntent().getData() != null) {
            mStartId = ItemsContract.Items.getItemId(getIntent().getData());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_article_detail);
        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(ArticleDetailActivity.this);
            }
        });

        getLoaderManager().initLoader(0, null, this);

        mSubtitleTextView = (TextView) findViewById(R.id.article_byline);
        mBodyTextView = (TextView) findViewById(R.id.article_body);
        mImageView = (ImageView) findViewById(R.id.article_image);

        findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(ArticleDetailActivity.this)
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getApplicationContext(), mStartId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isFinishing()) {
            cursor.moveToFirst();
            bindViews(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    private void bindViews(Cursor cursor) {
        mToolbarLayout.setTitle(cursor.getString(ArticleLoader.Query.TITLE));
        mSubtitleTextView.setText(Html.fromHtml(
                DateUtils.getRelativeTimeSpanString(
                        cursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                        System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL).toString() + " by "
                        + cursor.getString(ArticleLoader.Query.AUTHOR)));
        mBodyTextView.setText(Html.fromHtml(cursor.getString(ArticleLoader.Query.BODY)));
        loadImage(cursor.getString(ArticleLoader.Query.PHOTO_URL));
    }

    private void loadImage(String url) {
        Picasso.with(this)
                .load(url)
                .fit()
                .centerCrop()
                .noPlaceholder()
                .into(mImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette palette) {
                                applyPalette(palette);
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(ViewUtils.getThemeAttribute(getTheme(), R.attr.colorPrimaryDark));
        int primary = getResources().getColor(ViewUtils.getThemeAttribute(getTheme(), R.attr.colorPrimary));
        mToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        mToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
    }
}
