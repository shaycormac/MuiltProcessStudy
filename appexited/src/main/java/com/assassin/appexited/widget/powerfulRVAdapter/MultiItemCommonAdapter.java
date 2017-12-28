package com.assassin.appexited.widget.powerfulRVAdapter;

import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author 作者：Shay-Patrick-Cormac
 * @datetime 创建时间：2016-12-15 11:09 GMT+8
 * @email 邮箱： 574583006@qq.com
 * @content 说明：多类型的item。
 */
public abstract class MultiItemCommonAdapter<T> extends CommonAdapter<T> 
{
    protected MultiItemTypeSupport<T> multiItemTypeSupport;
    public MultiItemCommonAdapter(Context context, List<T> listItems, MultiItemTypeSupport multiItemTypeSupport)
    {
        super(context, -1, listItems);
        this.multiItemTypeSupport = multiItemTypeSupport;
    }

    @Override
    public int getItemViewType(int position) 
    {
        return multiItemTypeSupport.getItemViewType(position,listItems.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) 
    {
        int layoutId = multiItemTypeSupport.getLayoutId(viewType);
        return ViewHolder.createViewHolder(context, parent, layoutId);
    }
}
