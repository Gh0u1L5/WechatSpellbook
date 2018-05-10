package com.gh0u1l5.wechatmagician.spellbook.interfaces

import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.BaseAdapter
import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.base.Operation.Companion.nop

interface IAdapterHook {

    /**
     * Called when a Wechat AddressAdapter has been created. This adapter will be used in the
     * ListView for all the contacts (which is shown in the second tab of Wechat).
     *
     * @param adapter the created AddressAdapter object.
     */
    fun onAddressAdapterCreated(adapter: BaseAdapter) { }

    /**
     * Called when a Wechat ConversationAdapter has been created. This adapter will be used in the
     * ListView for all the conversations (which is shown in the first tab of Wechat).
     *
     * @param adapter the created ConversationAdapter object.
     */
    fun onConversationAdapterCreated(adapter: BaseAdapter) { }

    /**
     * Called when an HeaderViewListAdapter object is going to invoke [Adapter.getView] method.
     *
     * @param adapter the HeaderViewListAdapter object calling the getView().
     * @param position the position of the item whose view we want.
     * @param convertView the old view to reuse, if possible.
     * @param parent the parent that this view will eventually be attached to.
     * @return to bypass the original method, return a View object wrapped by [Operation.replacement]
     * or a throwable wrapped by [Operation.interruption], otherwise return [Operation.nop].
     */
    fun onHeaderViewListAdapterGettingView(adapter: Any, position: Int, convertView: View?, parent: ViewGroup): Operation<View> = nop()

    /**
     * Called when an HeaderViewListAdapter object has returned from [Adapter.getView] method.
     *
     * @param adapter the HeaderViewListAdapter object calling the getView().
     * @param position the position of the item whose view we want.
     * @param convertView the old view to reuse, if possible.
     * @param parent the parent that this view will eventually be attached to.
     * @param result the view that is returned by the original getView() function.
     * @return to replace the original result, return a View object wrapped by [Operation.replacement],
     * otherwise return [Operation.nop].
     */
    fun onHeaderViewListAdapterGotView(adapter: Any, position: Int, convertView: View?, parent: ViewGroup, result: View?): Operation<View> = nop()
}