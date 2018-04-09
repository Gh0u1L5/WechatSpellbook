package com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.wcdb

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal.wxVersion
import com.gh0u1l5.wechatmagician.spellbook.base.Version

object Package {
    val WECHAT_PACKAGE_SQLITE: String
            get() = when {
                wxVersion!! >= Version("6.5.8") -> "com.tencent.wcdb"
                else -> "com.tencent.mmdb"
            }
}