package com.gh0u1l5.wechatmagician.spellbook.interfaces

import android.app.Activity
import android.content.Context
import android.net.Uri

interface IUriRouterHook {
    /**
     * Called when the URI router get a new request
     *
     * @param activity an activity that can be used as a [Context].
     * @param uri the uri sent from the other applications. It should start with "weixin://magician/".
     */
    fun onReceiveUri(activity: Activity, uri: Uri) { }
}