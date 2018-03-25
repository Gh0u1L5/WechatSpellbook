package com.gh0u1l5.wechatmagician.spellbook.interfaces

import android.app.Activity
import android.os.Bundle
import android.view.Menu

interface IActivityHook {

    /**
     * Called when a Wechat MMActivity has created a options menu.
     *
     * @param activity the activity shown in foreground.
     * @param menu the options menu just created by the activity.
     */
    fun onMMActivityOptionsMenuCreated(activity: Activity, menu: Menu) { }

    /**
     * Called when an Activity is going to invoke [Activity.onCreate] method.
     *
     * @param activity the activity object that is creating.
     * @param savedInstanceState the saved instance state for restoring the state.
     */
    fun onActivityCreating(activity: Activity, savedInstanceState: Bundle?) { }

    /**
     * Called when an activity is going to invoke [Activity.onStart] method.
     *
     * @param activity the activity object that is starting.
     */
    fun onActivityStarting(activity: Activity) { }

    /**
     * Called when an activity is going to invoke [Activity.onResume] method.
     *
     * @param activity the activity object that is resuming.
     */
    fun onActivityResuming(activity: Activity) { }
}