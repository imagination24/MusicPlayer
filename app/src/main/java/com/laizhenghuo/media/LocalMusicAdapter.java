package com.laizhenghuo.media;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.LocalMusicViewHolder> {
    Context context;
    List<LocalMusicBean>mDatas;

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        public void OnItemClick(View view,int position);
    }

    public LocalMusicAdapter(Context context, List<LocalMusicBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @NonNull
    @Override
    public LocalMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_local_music,parent,false);

        LocalMusicViewHolder holder = new LocalMusicViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocalMusicViewHolder holder, int position) {
        LocalMusicBean localMusicBean = mDatas.get(position);
        holder.idtv.setText(localMusicBean.getId());
        holder.songtv.setText(localMusicBean.getSong());
        holder.singertv.setText(localMusicBean.getSinger());
        holder.albumtv.setText(localMusicBean.getAlbum());
        holder.timetv.setText(localMusicBean.getDuration());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onItemClickListener.OnItemClick(view,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class LocalMusicViewHolder extends RecyclerView.ViewHolder{
        TextView idtv,songtv,singertv,albumtv,timetv;
        public LocalMusicViewHolder(View itemView) {
            super(itemView);
            idtv=itemView.findViewById(R.id.item_local_music_num);
            songtv=itemView.findViewById(R.id.item_local_music_song);
            singertv=itemView.findViewById(R.id.item_local_music_singer);
            albumtv=itemView.findViewById(R.id.item_local_music_album);
            timetv=itemView.findViewById(R.id.item_local_music_durtion);
        }
    }
}
