package com.assassin.appexited.widget.powerfulRVAdapter;

import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * @author 作者：Shay-Patrick-Cormac
 * @datetime 创建时间：2016-12-14 15:49 GMT+8
 * @email 邮箱： 574583006@qq.com
 * @content 说明：通知viewHolder每个Item的选择和清除。
 */
public interface ItemTouchHelperViewHolder 
{
    /**

     * Called when the {@link ItemTouchHelper} first registers an item as being moved or swiped.

     * Implementations should update the item view to indicate it's active state.

     */

    void onItemSelected();

    /**

     * Called when the {@link ItemTouchHelper} has completed the move or swipe, and the active item

     * state should be cleared.

     */
    void onItemClear();
}
