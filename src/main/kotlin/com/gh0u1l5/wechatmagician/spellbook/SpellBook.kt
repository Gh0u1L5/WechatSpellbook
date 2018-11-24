package com.gh0u1l5.wechatmagician.spellbook

import android.content.Context
import com.gh0u1l5.wechatmagician.spellbook.base.EventCenter
import com.gh0u1l5.wechatmagician.spellbook.base.HookerProvider
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.hookers.*
import com.gh0u1l5.wechatmagician.spellbook.util.ParallelUtil.parallelForEach
import com.gh0u1l5.wechatmagician.spellbook.util.XposedUtil
import de.robv.android.xposed.XposedBridge.log
import de.robv.android.xposed.XposedHelpers.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.IXposedHookLoadPackage
import java.io.File

/**
 * Wechat Magician SpellBook的核心引擎部分
 *
 * Refer: https://github.com/Gh0u1L5/WechatSpellbook/wiki
 */
object SpellBook {
    /**
     * 目前支持的 [EventCenter] 列表
     *
     * Refer: https://github.com/Gh0u1L5/WechatSpellbook/wiki/事件机制
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
     * 判断当前进程是否为微信的重要进程, 目前会被判定为重要进程的只有主进程和 :tools 进程
     *
     * @param lpparam 通过重载 [IXposedHookLoadPackage.handleLoadPackage] 方法拿到的
     * [XC_LoadPackage.LoadPackageParam] 对象
     */
    fun isImportantWechatProcess(lpparam: XC_LoadPackage.LoadPackageParam): Boolean {
        // 检查进程名
        val processName = lpparam.processName
        when {
            !processName.contains(':') -> {
                // 找到主进程 继续
            }
            processName.endsWith(":tools") -> {
                // 找到 :tools 进程 继续
            }
            else -> return false
        }
        // 检查微信依赖的JNI库是否存在, 以此判断当前应用是不是微信
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
     * 利用 Reflection 获取当前的系统 Context
     */
    fun getSystemContext(): Context {
        val activityThreadClass = findClass("android.app.ActivityThread", null)
        val activityThread = callStaticMethod(activityThreadClass, "currentActivityThread")
        val context = callMethod(activityThread, "getSystemContext") as Context?
        return context ?: throw Error("Failed to get system context.")
    }

    /**
     * 获取指定应用的 APK 路径
     */
    fun getApplicationApkPath(packageName: String): String {
        val pm = getSystemContext().packageManager
        val apkPath = pm.getApplicationInfo(packageName, 0)?.publicSourceDir
        return apkPath ?: throw Error("Failed to get the APK path of $packageName")
    }

    /**
     * 获取指定应用的版本号
     */
    fun getApplicationVersion(packageName: String): Version {
        val pm = getSystemContext().packageManager
        val versionName = pm.getPackageInfo(packageName, 0)?.versionName
        return Version(versionName
                ?: throw Error("Failed to get the version of $packageName"))
    }

    /**
     * 启动 SpellBook 框架, 注册相关插件
     *
     * @param lpparam 通过重载 [IXposedHookLoadPackage.handleLoadPackage] 方法拿到的
     * [XC_LoadPackage.LoadPackageParam] 对象
     * @param plugins 由开发者编写的 SpellBook 插件, 这些插件应当实现 [HookerProvider.provideStaticHookers]
     * 方法, 或 interfaces 包中提供的标准接口
     *
     * Refer: https://github.com/Gh0u1L5/WechatSpellbook/wiki/事件机制
     */
    fun startup(lpparam: XC_LoadPackage.LoadPackageParam, plugins: List<Any>?) {
        log("Wechat SpellBook: ${plugins?.size ?: 0} plugins.")
        WechatGlobal.init(lpparam)
        registerPlugins(plugins)
        registerHookers(plugins)
    }

    /**
     * 检查插件是否实现了标准化的接口, 并将它们注册到对应的 [EventCenter] 中
     */
    private fun registerPlugins(plugins: List<Any>?) {
        val observers = plugins?.filter { it !is HookerProvider } ?: listOf()
        centers.parallelForEach { center ->
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

    /**
     * 检查插件中是否存在自定义的事件, 将它们直接注册到 Xposed 框架上
     */
    private fun registerHookers(plugins: List<Any>?) {
        val providers = plugins?.filter { it is HookerProvider } ?: listOf()
        (providers + listOf(ListViewHider, MenuAppender)).parallelForEach { provider ->
            (provider as HookerProvider).provideStaticHookers()?.forEach { hooker ->
                if (!hooker.hasHooked) {
                    XposedUtil.postHooker(hooker)
                }
            }
        }
    }
}