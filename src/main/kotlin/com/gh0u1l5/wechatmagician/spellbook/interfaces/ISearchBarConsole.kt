package com.gh0u1l5.wechatmagician.spellbook.interfaces

import android.content.Context

interface ISearchBarConsole {
    /**
     * Called when the user entered a command in the search bar.
     *
     * @param context a [Context] that can be used for later operations.
     * @param command the commend entered by the user.
     * @return true if the command should be consumed, otherwise return false.
     */
    fun onHandleCommand(context: Context, command: String) = false
}
