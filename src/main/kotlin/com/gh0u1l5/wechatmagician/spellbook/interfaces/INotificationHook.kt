package com.gh0u1l5.wechatmagician.spellbook.interfaces

interface INotificationHook {

    data class Message(
            val talker: String,
            val content: String,
            val type: Int,
            val tipsFlag: Int
    )

    fun onMessageHandling(message: Message) = false

    fun onMessageHandled(message: Message) { }
}