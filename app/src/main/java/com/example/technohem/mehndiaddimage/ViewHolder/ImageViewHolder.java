package com.example.technohem.mehndiaddimage.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.technohem.mehndiaddimage.Interface.ItemClickListener;
import com.example.technohem.mehndiaddimage.R;

public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView imageView;
    public ItemClickListener listener;

    public ImageViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.mehndi_image);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;

    }

    @Override
    public void onClick(View v) {
        listener.onClick(v ,getAdapterPosition(), false);
    }
}
