package com.assassin.appexited.recyclerView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * @author: Shay-Patrick-Cormac
 * @email: fang47881@126.com
 * @ltd: 金螳螂企业（集团）有限公司
 * @date: 2017/11/15 09:18
 * @version:
 * @description:
 */

public class GridItemDecoration extends RecyclerView.ItemDecoration 
{
    /**
     * 间距
     */
    private int padPx;

    /**
     * 颜色
     */
    private @ColorInt int color;

    /**
     * 画笔
     * @param padPx
     * @param color
     */
    private Paint paint;
    public GridItemDecoration(int padPx,@ColorInt int color) 
    {
        super();
        this.padPx = padPx;
        this.color = color;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
    }

    //重新绘制
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) 
    {
        super.onDraw(c, parent, state);

        // 获取RecyclerView的ChildView的个数(是屏幕内的哦，不是所有的哦)
        int childCount = parent.getChildCount();

        // 遍历每个Item，分别获取它们的位置信息，然后再绘制对应的分割线
        for ( int i = 0; i < childCount; i++ ) 
        {
            // 获取每个Item的位置
            final View child = parent.getChildAt(i);
            int index = parent.getChildAdapterPosition(child);
             //列数
            int spanCount = getSpanCount(parent);
            //判断是否最后一列

            // 获取布局参数
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            // 根据子视图的位置 & 间隔区域，设置矩形（分割线）的2个顶点坐标(左上 & 右下)

            // 矩形左上顶点 = (ItemView的左边界,ItemView的下边界)
            // ItemView的左边界 = RecyclerView 的左边界 + paddingLeft距离 后的位置
          //  final int left = parent.getPaddingLeft();
            // ItemView的下边界：ItemView 的 bottom坐标 + 距离RecyclerView底部距离 +translationY
          //  final int top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));

            // 矩形右下顶点 = (ItemView的右边界,矩形的下边界)
            // ItemView的右边界 = RecyclerView 的右边界减去 paddingRight 后的坐标位置
          //  final int right = parent.getWidth() - parent.getPaddingRight();
            // 绘制分割线的下边界 = ItemView的下边界+分割线的高度
          //  final int bottom = top + padPx;
            
            boolean isLastColumn = isLastColumn(parent, index, spanCount, childCount);
            if (isLastColumn)
            {
                //仅仅绘制底部的即可
                int left =child.getLeft();
                int right = child.getRight();
                final int top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
                final int bottom = top + padPx;
                c.drawRect(left,top,right,bottom,paint);
                
            }else 
            {
                //绘制底部和右面。
                int left =child.getLeft();
                int right = child.getRight();
                 int top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
                 int bottom = top + padPx;
                c.drawRect(left,top,right,bottom,paint);
                //绘制右面
                left=child.getRight()+params.rightMargin+Math.round(ViewCompat.getTranslationX(child));
                right = left+padPx;
                top=child.getTop();
                c.drawRect(left,top,right,bottom,paint);
            }
            
            // 通过Canvas绘制矩形（分割线）
           
        }
        
    }

    //最后绘制
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    //每个item的偏移距离
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) 
    {
        super.getItemOffsets(outRect, view, parent, state);

        // 获取RecyclerView的ChildView的个数(是屏幕内的哦，不是所有的哦)
        int childCount = parent.getChildCount();
        //列数
        int spanCount = getSpanCount(parent);
        //获取当前的位置
        int itemPosition = parent.getChildAdapterPosition(view);
        
        //判断是否最后一列
        boolean isLastColumn = isLastColumn(parent, itemPosition, spanCount, childCount);
        if (isLastColumn)
        {
            //只向下偏移
            outRect.set(0,0,0,padPx);
            
        }else 
        {
            //只偏移右下
            outRect.set(0,0,padPx,padPx);
        }
        
    }


    /**
     * 获取当前布局管理器的列数。
     * @param parent
     * @return
     */
    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    /**
     * 是否是最后一列
     * @param parent
     * @param pos
     * @param spanCount
     * @param childCount
     * @return
     */
    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount,
                                 int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {// 如果是最后一列，则不需要绘制右边
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    /**
     * 是否最后一行
     * @param parent
     * @param pos
     * @param spanCount
     * @param childCount
     * @return
     */
    private boolean isLastRow(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            // childCount = childCount - childCount % spanCount;
            int lines = childCount % spanCount == 0 ? childCount / spanCount : childCount / spanCount + 1;
            return lines == pos / spanCount + 1;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否是第一行
     * @param parent
     * @param pos
     * @param spanCount
     * @param childCount
     * @return
     */
    private boolean isfirstRow(RecyclerView parent, int pos, int spanCount,
                               int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            // childCount = childCount - childCount % spanCount;
            int lines = childCount % spanCount == 0 ? childCount / spanCount : childCount / spanCount + 1;
            //如是第一行则返回true
            if ((pos / spanCount + 1) == 1) {
                return true;
            } else {
                return false;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
