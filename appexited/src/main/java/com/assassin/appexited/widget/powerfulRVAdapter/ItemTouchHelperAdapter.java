package com.assassin.appexited.widget.powerfulRVAdapter;

/**
 * @author 作者：Shay-Patrick-Cormac
 * @datetime 创建时间：2016-12-14 15:48 GMT+8
 * @email 邮箱： 574583006@qq.com
 * @content 说明：两个是onMove()和onSwiped()，用于通知底层数据的更新。
 */
public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
