package com.assassin.appexited.widget.powerfulRVAdapter;

/**
 * @author 作者：Shay-Patrick-Cormac
 * @datetime 创建时间：2016-12-15 11:05 GMT+8
 * @email 邮箱： 574583006@qq.com
 * @content 说明：多种ItemViewType。
 * 多种ItemViewType，一般我们的写法是：
   复写getItemViewType，根据我们的bean去返回不同的类型
   onCreateViewHolder中根据itemView去生成不同的ViewHolder
   用户在使用过程中，通过实现上面两个方法，指明不同的Bean返回什么itemViewType,不同的itemView所对应的layoutId.
 */
public interface MultiItemTypeSupport<T> 
{
    int getLayoutId(int itemType);

    int getItemViewType(int position, T t);
}
