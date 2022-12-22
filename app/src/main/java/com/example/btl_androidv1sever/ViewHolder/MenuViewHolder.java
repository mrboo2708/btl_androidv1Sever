package com.example.btl_androidv1sever.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_androidv1sever.Common.Common;
import com.example.btl_androidv1sever.Interface.ItemClick;
import com.example.btl_androidv1sever.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener{
    public TextView txtMenuView;
    public ImageView imageView;
    public ItemClick itemClick;

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public MenuViewHolder(View view){
        super(view);

        txtMenuView = (TextView) view.findViewById(R.id.menu_name);
        imageView = (ImageView) view.findViewById(R.id.menu_image);
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
