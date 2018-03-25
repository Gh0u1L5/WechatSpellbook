package com.gh0u1l5.wechatmagician.spellbook.interfaces

interface IMessageStorageHook {

    /**
     * Called when an MsgInfoStorage object has been created.
     *
     * @param storage the MsgInfoStorage object created by a constructor.
     */
    fun onMessageStorageCreated(storage: Any) { }

    /**
     * Called when an MsgInfoStorage is inserting a new message.
     *
     * @param msgId the internal ID of the message.
     * @param msgObject the message object stored into storage.
     * @return to bypass the original method, return `true`, otherwise return `false`.
     */
    fun onMessageStorageInserting(msgId: Long, msgObject: Any) = false

    /**
     * Called when an MsgInfoStorage has inserted a new message.
     *
     * @param msgId the internal ID of the message.
     * @param msgObject the message object stored into storage.
     */
    fun onMessageStorageInserted(msgId: Long, msgObject: Any) { }
}