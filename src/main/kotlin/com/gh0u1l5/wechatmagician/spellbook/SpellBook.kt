package com.gh0u1l5.wechatmagician.spellbook

import android.content.Context
import com.gh0u1l5.wechatmagician.spellbook.base.EventCenter
import com.gh0u1l5.wechatmagician.spellbook.base.HookerProvider
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.hookers.*
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryAsynchronously
import com.gh0u1l5.wechatmagician.spellbook.util.XposedUtil
import de.robv.android.xposed.XposedBridge.log
import de.robv.android.xposed.XposedHelpers.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File

/**
 * This is the main class of Wechat Magician SpellBook. It implements most of the functions that
 * should be called when you load the package in Xposed. For more details, please check the [tutorial
 * documents](https://github.com/Gh0u1L5/WechatSpellbook/wiki) for developers.
 */
object SpellBook {
    /**
     * A list holding the event centers that actually notify the plugins.
     */
    private val centers: List<EventCenter> = listOf(
            Activities,
            Adapters,
            Database,
            FileSystem,
            MenuAppender,
            Notifications,
            SearchBar,
            Storage,
            UriRouter,
            XLog,
            XmlParser
    )

    /**
     * Returns whether the current process seems to be an important process in Wechat. Currently,
     * "important process" refers to the main process and :tools process of Wechat.
     *
     * @param lpparam the LoadPackageParam object that describes the current process, which should
     * be the same one passed to [de.robv.android.xposed.IXposedHookLoadPackage.handleLoadPackage].
     * @return `true` if the process is an important Wechat process, `false` otherwise.
     */
    fun isImportantWechatProcess(lpparam: XC_LoadPackage.LoadPackageParam): Boolean {
        val processName = lpparam.processName
        when {
            !processName.contains(':') -> {
                // Found main process, continue
            }
            processName.endsWith(":tools") -> {
                // Found :tools process, continue
            }
            else -> return false
        }
        val features = listOf (
                "libwechatcommon.so",
                "libwechatmm.so",
                "libwechatnetwork.so",
                "libwechatsight.so",
                "libwechatxlog.so"
        )
        return try {
            val libraryDir = File(lpparam.appInfo.nativeLibraryDir)
            features.filter { filename ->
                File(libraryDir, filename).exists()
            }.size >= 3
        } catch (t: Throwable) { false }
    }

    /**
     * Returns the system context that can be used for some other operations.
     */
    fun getSystemContext(): Context {
        val activityThreadClass = findClass("android.app.ActivityThread", null)
        val activityThread = callStaticMethod(activityThreadClass, "currentActivityThread")
        val context = callMethod(activityThread, "getSystemContext") as Context?
        return context ?: throw Error("Failed to get system context.")
    }

    /**
     * Finds out the APK path of a specific application.
     *
     * @param packageName the package name of the specific application.
     */
    fun getApplicationApkPath(packageName: String): String {
        val pm = getSystemContext().packageManager
        val apkPath = pm.getApplicationInfo(packageName, 0)?.publicSourceDir
        return apkPath ?: throw Error("Failed to get the APK path of $packageName")
    }

    /**
     * Finds out the version of a specific application.
     *
     * @param packageName the package name of the specific application.
     * @return a [Version] object that contains the current version information.
     */
    fun getApplicationVersion(packageName: String): Version {
        val pm = getSystemContext().packageManager
        val versionName = pm.getPackageInfo(packageName, 0)?.versionName
        return Version(versionName
                ?: throw Error("Failed to get the version of $packageName"))
    }

    /**
     * Initializes and starts up the SpellBook engine.
     *
     * @param lpparam the LoadPackageParam object that describes the current process, which should
     * be the same one passed to [de.robv.android.xposed.IXposedHookLoadPackage.handleLoadPackage].
     * @param plugins the list of custom plugins written by the developer, which should implement
     * one or more interfaces in [com.gh0u1l5.wechatmagician.spellbook.interfaces] or override the
     * method [HookerProvider.provideStaticHookers].
     */
    fun startup(lpparam: XC_LoadPackage.LoadPackageParam, plugins: List<Any>?, extra: Boolean = true) {
        log("Wechat SpellBook: ${plugins?.size ?: 0} plugins.")
        WechatGlobal.init(lpparam)
        registerPlugins(plugins)
        registerHookers(plugins, extra)
    }

    /**
     * Registers the given list of plugins asynchronously to all the event centers.
     */
    private fun registerPlugins(plugins: List<Any>?) {
        val observers = plugins?.filter { it !is HookerProvider } ?: listOf()
        centers.forEach { center ->
            tryAsynchronously {
                center.interfaces.forEach { `interface` ->
                    observers.forEach { plugin ->
                        val assignable = `interface`.isAssignableFrom(plugin::class.java)
                        if (assignable) {
                            center.register(`interface`, plugin)
                        }
                    }
                }
            }
        }
    }

    /**
     * Registers all the custom hookers to the Xposed framework using [XposedUtil.postHooker].
     */
    private fun registerHookers(plugins: List<Any>?, extra: Boolean = true) {
        var providers = plugins?.filter { it is HookerProvider } ?: listOf()
        if (extra) {
            providers += listOf(ListViewHider, MenuAppender)
        }
        providers.forEach { provider ->
            (provider as HookerProvider).provideStaticHookers()?.forEach { hooker ->
                if (!hooker.hasHooked) {
                    XposedUtil.postHooker(hooker)
                }
            }
        }
    }
}