package com.example.musicapp;



import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.sql.Array;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    private Context mContext;
   static  ArrayList<MusicFile> mFiles;
    MusicAdapter(Context mContext,ArrayList<MusicFile> mFiles){
        this.mContext=mContext;
        this.mFiles=mFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.music_items,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        byte[] image = getAlbumArt(mFiles.get(position).getPath());
        if(image!= null){
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_art);
        }
        else {
            Glide.with(mContext)
                    .load(R.drawable.song)
                    .into(holder.album_art);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext,PlayerActivity.class);
                intent.putExtra("position",position);
                mContext.startActivity(intent);
            }
        });
        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu=new PopupMenu(mContext,v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete) {
                                Toast.makeText(mContext, "Đã xóa bài!", Toast.LENGTH_SHORT).show();
                                deleteFile(position,v);
                        }
                        return true;
                    }
                });
            }
        });
    }
private void deleteFile(int position, View v)
{
    Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.
            EXTERNAL_CONTENT_URI,Long.parseLong(mFiles.get(position).getId())); //content://

    File file = new File(mFiles.get(position).getPath());
    boolean deleted = file.delete(); //delete files
    if (deleted){
        mContext.getContentResolver().delete(contentUri,null,null);
        mFiles.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position,mFiles.size());
        Snackbar.make(v,"Bài đã xóa ",Snackbar.LENGTH_LONG).show();
    }
    else{
        //may be file in sd card, unable to delete
        Snackbar.make(v,"File Can't Be Deleted: ",Snackbar.LENGTH_LONG).show();
    }


}
    @Override
    public int getItemCount() {
        return mFiles.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView file_name;
        ImageView album_art,menuMore;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name=itemView.findViewById(R.id.music_file_name);
            album_art=itemView.findViewById(R.id.music_img);
            menuMore=itemView.findViewById(R.id.menuMore);
        }
    }
    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        return art;
    }
    void updateList(ArrayList<MusicFile> musicFileArrayList){
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFileArrayList);
        notifyDataSetChanged();
    }
}
