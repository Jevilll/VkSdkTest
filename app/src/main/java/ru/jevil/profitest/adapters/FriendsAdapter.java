package ru.jevil.profitest.adapters;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.jevil.profitest.R;
import ru.jevil.profitest.cache.BitmapCache;
import ru.jevil.profitest.download.BitmapDownloadCallback;
import ru.jevil.profitest.download.BitmapWorkerTask;
import ru.jevil.profitest.pojo.Friend;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private List<Friend> friendsList;
    private BitmapCache mBitmapCache;

    private OnFriendClickListener listener;

    public FriendsAdapter(OnFriendClickListener listener) {
        friendsList = new ArrayList<>();
        mBitmapCache = BitmapCache.getInstance();
        this.listener = listener;
    }

    public void update(List<Friend> newList) {
        if (newList != null && !newList.isEmpty()) {
            friendsList.clear();
            friendsList.addAll(newList);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Friend friend = friendsList.get(position);
        holder.tvName.setText(friend.getName());
        holder.ivThumb.setImageBitmap(null);
        holder.pb.setVisibility(View.VISIBLE);
        final Bitmap cacheBitmap = mBitmapCache.getBitmapFromMemory(friend.getPhotoThumbnail());
        if (cacheBitmap != null) {
            holder.ivThumb.setImageBitmap(cacheBitmap);
            holder.pb.setVisibility(View.GONE);
        } else {
            holder.bitmapWorkerTask = new BitmapWorkerTask(new BitmapDownloadCallback() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    holder.ivThumb.setImageBitmap(bitmap);
                    holder.pb.setVisibility(View.GONE);
                    holder.tvError.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    holder.tvError.setVisibility(View.VISIBLE);
                    holder.pb.setVisibility(View.GONE);
                }
            });

            holder.bitmapWorkerTask.executeOnExecutor(BitmapWorkerTask.THREAD_POOL_EXECUTOR, friend.getPhotoThumbnail());
        }

        holder.itemView.setOnClickListener(v -> {
            holder.pb.setVisibility(View.VISIBLE);
            listener.onClick(holder, friend.getPhotoMax(), friend.getPhotoId());
        });

        holder.tvOnline.setText(friend.getOnline());
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.bitmapWorkerTask != null) holder.bitmapWorkerTask.cancel(true);
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvOnline, tvError;
        public ImageView ivThumb;
        public ProgressBar pb;
        BitmapWorkerTask bitmapWorkerTask;

        ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            ivThumb = itemView.findViewById(R.id.ivThumbnail);
            tvError = itemView.findViewById(R.id.tvError);
            pb = itemView.findViewById(R.id.pb);
            tvOnline = itemView.findViewById(R.id.tvOnline);
        }
    }

    public interface OnFriendClickListener {
        void onClick(ViewHolder holder, String bigUrl, String photoId);
    }

}
