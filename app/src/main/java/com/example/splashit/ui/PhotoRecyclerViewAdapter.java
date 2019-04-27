package com.example.splashit.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.splashit.R;
import com.example.splashit.data.model.Photo;
import com.example.splashit.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.ViewHolder> {

    public static final String TAG = PhotoRecyclerViewAdapter.class.getSimpleName();

    private final List<Photo> values;
    private Context context;

    public PhotoRecyclerViewAdapter(Context context, List<Photo> items) {
        this.context = context;
        this.values = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.photo = values.get(position);
        Picasso.with(context).load(Uri.parse(holder.photo.getUrls().getRegular())).into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PhotoDetailActivity.class);
            intent.putExtra(Constants.PHOTO_ID, holder.photo.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;
        public Photo photo;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image);
        }
    }
}
