package com.gh0u1l5.wechatmagician.spellbook.interfaces

interface INotificationHook {

    /**
     * The bean class that describes a notification message.
     */
    data class Message(
            val talker: String,
            val content: String,
            val type: Int,
            val tipsFlag: Int
    )

    /**
     * Called when the global message handler is going to handle a [Message].
     *
     * @param message the message received by the handler.
     * @return to prevent showing the message, return `true`, otherwise return `false`
     */
    fun onMessageHandling(message: Message) = false

    /**
     * Called when the global message handler has handled a [Message].
     *
     * @param message the message received by the handler.
     */
    fun onMessageHandled(message: Message) { }
}