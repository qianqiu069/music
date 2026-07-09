package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.model.Song;

import java.util.List;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    /** 点击事件回调接口 */
    public interface OnItemClickListener {
        void onItemClick(int position, Song song);
    }

    // 数据源
    private final List<Song> data;
    // 外部传入的点击监听器
    private OnItemClickListener listener;

    public SongAdapter(List<Song> data) {
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }

    /**
     * 创建一行的 View（ViewHolder）
     */
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 把 item_song.xml 转换成 View 对象
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    /**
     * 绑定数据到指定位置的 View
     */
    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        final Song song = data.get(position);

        holder.tvIndex.setText(String.valueOf(position + 1));
        holder.tvSongName.setText(song.name == null ? "" : song.name);
        holder.tvArtist.setText(song.getArtistNames());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(holder.getAdapterPosition(), song);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex;
        TextView tvSongName;
        TextView tvArtist;

        SongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tv_index);
            tvSongName = itemView.findViewById(R.id.tv_song_name);
            tvArtist = itemView.findViewById(R.id.tv_artist);
        }
    }
}
