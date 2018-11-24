package com.gh0u1l5.wechatmagician.spellbook

import com.gh0u1l5.wechatmagician.spellbook.base.EventCenter

/**
 * 用来记录各个 [EventCenter] 运行状态的单例对象
 */
object WechatStatus {

    /**
     * 目前支持的所有功能的标识
     */
    enum class StatusFlag {
        STATUS_FLAG_ACTIVITIES,
        STATUS_FLAG_ADAPTERS,
        STATUS_FLAG_BASE_ADAPTER,
        STATUS_FLAG_COMMAND,
        STATUS_FLAG_CONTACT_POPUP,
        STATUS_FLAG_CONVERSATION_POPUP,
        STATUS_FLAG_DATABASE,
        STATUS_FLAG_FILESYSTEM,
        STATUS_FLAG_IMG_STORAGE,
        STATUS_FLAG_MSG_STORAGE,
        STATUS_FLAG_NOTIFICATIONS,
        STATUS_FLAG_RESOURCES,
        STATUS_FLAG_URI_ROUTER,
        STATUS_FLAG_XML_PARSER
    }

    /**
     * 用于记录所有成功启动的功能
     */
    private var valid: IntArray = intArrayOf()

    /**
     * 报告当前活跃的功能
     */
    @Synchronized fun report(): IntArray = valid

    /**
     * 记录某功能启动完成
     */
    @Synchronized fun toggle(flag: StatusFlag) { valid += flag.ordinal }
}