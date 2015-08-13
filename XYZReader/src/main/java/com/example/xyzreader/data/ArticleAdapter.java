package com.example.xyzreader.data;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.ui.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

public class ArticleAdapter extends CursorAdapter<ArticleAdapter.ViewHolder> {

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    private WeakReference<OnItemClickListener> mListenerRef;


    public ArticleAdapter( Cursor cursor) {
        super(cursor);
    }

    public ArticleAdapter() {
        this(null);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
        ViewHolder vh = new ViewHolder(view);
        vh.setOnItemClickListener(mItemListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        holder.onBind(cursor);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListenerRef = listener != null ? new WeakReference<>(listener) : null;
    }

    private final OnItemClickListener mItemListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            OnItemClickListener listener = mListenerRef != null? mListenerRef.get() : null;
            if(listener != null){
                listener.onItemClick(position);
            }
        }
    };

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public AspectRatioImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        private OnItemClickListener listener;

        private ViewHolder(View view) {
            super(view);
            thumbnailView = (AspectRatioImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.primaryTitle);
            subtitleView = (TextView) view.findViewById(R.id.subTitle);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        listener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }

        private void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        private void onBind(Cursor cursor){
            titleView.setText(cursor.getString(ArticleLoader.Query.TITLE));
            subtitleView.setText(
                    DateUtils.getRelativeTimeSpanString(
                            cursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by "
                            + cursor.getString(ArticleLoader.Query.AUTHOR));
            loadThumbnail(thumbnailView, cursor.getString(ArticleLoader.Query.THUMB_URL));
            thumbnailView.setAspectRatio(cursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
        }

        private void loadThumbnail(ImageView view, String imageUrl) {
            if (imageUrl != null) {
                Picasso.with(view.getContext())
                        .load(imageUrl)
                        .fit()
                        .into(view);
            }
        }
    }
}
