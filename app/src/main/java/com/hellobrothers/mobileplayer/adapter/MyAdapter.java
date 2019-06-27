package com.hellobrothers.mobileplayer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hellobrothers.mobileplayer.R;
import com.hellobrothers.mobileplayer.domain.MediaItem;
import com.hellobrothers.mobileplayer.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private final Context context;
    private final List<MediaItem> medias;

    public MyAdapter(Context context, List<MediaItem> medias) {
        this.context = context;
        this.medias = medias;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, viewGroup, false);
        ButterKnife.bind(this, view);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder viewHolder, int i) {
        MediaItem media = medias.get(i);
        viewHolder.tv_name.setText(media.getName());
        viewHolder.tv_size.setText(Formatter.formatFileSize(context, media.getSize()));
        viewHolder.tv_time.setText(new Utils().stringForTime(media.getDuration().intValue()));
    }

    @Override
    public int getItemCount() {
        return medias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_size)
        TextView tv_size;
        @BindView(R.id.tv_time)
        TextView tv_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
