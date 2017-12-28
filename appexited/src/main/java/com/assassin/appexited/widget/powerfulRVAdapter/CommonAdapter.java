package com.assassin.appexited.widget.powerfulRVAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author 作者：Shay-Patrick-Cormac
 * @datetime 创建时间：2016-12-15 10:23 GMT+8
 * @email 邮箱： 574583006@qq.com
 * @content 说明：单种ViewType的通用RecycleViewAdapter。
 */
public abstract class CommonAdapter<T> extends RecyclerView.Adapter<ViewHolder>
{
    protected Context context;
    protected int layOutId;
    protected List<T> listItems;
    protected LayoutInflater inflater;

    public CommonAdapter(Context context, int layOutId, List<T> listItems) {
        this.context = context;
        this.layOutId = layOutId;
        this.listItems = listItems;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) 
    {
        return ViewHolder.createViewHolder(context,parent,layOutId);
    }

    @Override
    public int getItemCount() 
    {
        return listItems.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) 
    {
       convert(holder,listItems.get(position));
    }

    public abstract void convert(ViewHolder holder,T t);
}
