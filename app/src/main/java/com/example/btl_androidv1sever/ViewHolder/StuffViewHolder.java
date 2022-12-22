package com.example.btl_androidv1sever.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_androidv1sever.Common.Common;
import com.example.btl_androidv1sever.Interface.ItemClick;
import com.example.btl_androidv1sever.R;

public class StuffViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener{
    public TextView stuff_name;
    public ImageView stuff_image;
    public ItemClick itemClick;

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public StuffViewHolder(View view){
        super(view);

        stuff_name = (TextView) view.findViewById(R.id.stuff_name);
        stuff_image = (ImageView) view.findViewById(R.id.stuff_image);
        view.setOnCreateContextMenuListener(this);
        view.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        itemClick.OnClick(view,getAbsoluteAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select action");
        contextMenu.add(0,0,getAbsoluteAdapterPosition(), Common.Update  );
        contextMenu.add(0,1,getAbsoluteAdapterPosition(),Common.Delete);
    }
}
