package com.assassin.appexited.widget.powerfulRVAdapter;

import android.graphics.Canvas;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * @author 作者：Shay-Patrick-Cormac
 * @datetime 创建时间：2016-12-14 14:37 GMT+8
 * @email 邮箱： 574583006@qq.com
 * @content 说明：官方已经支持RecyclerView拖动排序与滑动删除，那就是ItemTouchHelper。
 */
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback 
{
    private ItemTouchHelperAdapter itemTouchHelperAdapter;
    private float ALPHA_FULL = 1.0f;

    public ItemTouchHelperCallback(ItemTouchHelperAdapter itemTouchHelperAdapter) 
    {
        this.itemTouchHelperAdapter = itemTouchHelperAdapter;
    }

    //指定可以支持的拖放和滑动的方向，上下为拖动（drag），左右为滑动（swipe）
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
    {
        int dragFlags;
        int swipeFlags;
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        //瀑布流或者网格时，禁止左右滑动删除
        if (manager instanceof GridLayoutManager || manager instanceof StaggeredGridLayoutManager)
        {
             dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            //禁止左右滑动
             swipeFlags = 0;
        }else 
        {
             dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
             swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        }
        return makeMovementFlags(dragFlags,swipeFlags);
    }
//滑动操作
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
    {
        //不同类型的两个交换位置不支持。
        if (viewHolder.getItemViewType()!=target.getItemViewType())
            return false;
        //通知底层更换位置
        itemTouchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }
//删除操作
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
    {
      //回调出去，删除操作
        itemTouchHelperAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
    
    //override的两个方法
//RecyclerView item支持长按进入拖动操作
    @Override
    public boolean isLongPressDragEnabled() 
    {
        return true;
    }
//RecyclerView item任意位置触发启用滑动操作
    @Override
    public boolean isItemViewSwipeEnabled() 
    {
        return true;
    }
    
    //删除加上自定义动画
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //如果横向删除的话，使用自定义动画（渐显）
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            
            float alpha = ALPHA_FULL - Math.abs(dX) / viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            //x轴上渐变
            viewHolder.itemView.setTranslationX(dX);
        } else
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
    //选择改变


    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        //只让当前活动的item改变
        if (actionState!= ItemTouchHelper.ACTION_STATE_IDLE)
        {
            //通知item被选中
            if (viewHolder instanceof ItemTouchHelperViewHolder) 
            {
                ItemTouchHelperViewHolder itemTouchHelperViewHolder = (ItemTouchHelperViewHolder) viewHolder;
                //选中状态回调
                itemTouchHelperViewHolder.onItemSelected();
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    //清除view
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //设置渐变度
        viewHolder.itemView.setAlpha(ALPHA_FULL);
        if (viewHolder instanceof ItemTouchHelperViewHolder) {
            // Tell the view holder it's time to restore the idle state
            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            //未选中状态回调
            itemViewHolder.onItemClear();
            }
        }

}
   

