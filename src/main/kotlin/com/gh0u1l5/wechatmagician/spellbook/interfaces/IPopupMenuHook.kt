package com.gh0u1l5.wechatmagician.spellbook.interfaces

import com.gh0u1l5.wechatmagician.spellbook.hookers.MenuAppender

interface IPopupMenuHook {

    /**
     * Called when the popup menu for a contact is going to be created.
     *
     * @param username the username of the contact that has been long clicking.
     * @return to add your own menu items, return a list of [MenuAppender.PopupMenuItem], otherwise
     * return null.
     */
    fun onPopupMenuForContactsCreating(username: String): List<MenuAppender.PopupMenuItem>? = null

    /**
     * Called when the popup menu for a conversation is going to be created.
     *
     * @param username the username of the conversation that has been long clicking.
     * @return to add your own menu items, return a list of [MenuAppender.PopupMenuItem], otherwise
     * return null.
     */
    fun onPopupMenuForConversationsCreating(username: String): List<MenuAppender.PopupMenuItem>? = null
}