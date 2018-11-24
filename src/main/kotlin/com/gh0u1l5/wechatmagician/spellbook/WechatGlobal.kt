package com.gh0u1l5.wechatmagician.spellbook

import android.widget.Adapter
import android.widget.BaseAdapter
import com.gh0u1l5.wechatmagician.spellbook.SpellBook.getApplicationVersion
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.base.WaitChannel
import com.gh0u1l5.wechatmagician.spellbook.parser.ApkFile
import com.gh0u1l5.wechatmagician.spellbook.parser.ClassTrie
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryAsynchronously
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.ref.WeakReference

/**
 * 用于记录所有关于 Wechat 的关键全局变量
 */
object WechatGlobal {

    /**
     * 若初始化操作耗费2秒以上, 视作初始化失败, 直接让微信开始正常运行
     */
    @Suppress("MemberVisibilityCanBePrivate")
    const val INIT_TIMEOUT = 2000L // ms

    /**
     * 用于防止其他线程在初始化完成之前访问 WechatGlobal的变量
     */
    private val initChannel = WaitChannel()

    /**
     * 微信版本
     *
     * 如果初始化还未完成的话, 访问该对象的线程会自动阻塞 [INIT_TIMEOUT] ms
     */
    @Volatile var wxVersion: Version? = null
        get() {
            if (!wxUnitTestMode) {
                initChannel.wait(INIT_TIMEOUT)
                initChannel.done()
            }
            return field
        }

    /**
     * 微信包名（用于处理多开的情况）
     *
     * 如果初始化还未完成的话, 访问该对象的线程会自动阻塞 [INIT_TIMEOUT] ms
     */
    @Volatile var wxPackageName: String = ""
        get() {
            if (!wxUnitTestMode) {
                initChannel.wait(INIT_TIMEOUT)
                initChannel.done()
            }
            return field
        }

    /**
     * 微信 APK 所使用的 ClassLoader, 用于加载 Class 对象
     *
     * 如果初始化还未完成的话, 访问该对象的线程会自动阻塞 [INIT_TIMEOUT] ms
     */
    @Volatile var wxLoader: ClassLoader? = null
        get() {
            if (!wxUnitTestMode) {
                initChannel.wait(INIT_TIMEOUT)
                initChannel.done()
            }
            return field
        }

    /**
     * 微信 APK 所包含的全部类名, 依据 package 结构组织在一起, 用于动态适配不同的微信版本
     *
     * 如果初始化还未完成的话, 访问该对象的线程会自动阻塞 [INIT_TIMEOUT] ms
     */
    @Volatile var wxClasses: ClassTrie? = null
        get() {
            if (!wxUnitTestMode) {
                initChannel.wait(INIT_TIMEOUT)
                initChannel.done()
            }
            return field
        }

    /**
     * 单元测试模式的开关, 只应该在单元测试中打开
     */
    @Volatile var wxUnitTestMode: Boolean = false

    // 缓存一些重要的微信全局对象
    @Volatile var AddressAdapterObject: WeakReference<BaseAdapter?> = WeakReference(null)
    @Volatile var ConversationAdapterObject: WeakReference<BaseAdapter?> = WeakReference(null)
    @Volatile var SnsUserUIAdapterObject: WeakReference<Adapter?> = WeakReference(null)
    @Volatile var MsgStorageObject: Any? = null
    @Volatile var ImgStorageObject: Any? = null
    @Volatile var MainDatabaseObject: Any? = null
    @Volatile var SnsDatabaseObject: Any? = null

    /**
     * 创建一个惰性求值对象, 只有被用到的时候才会自动求值
     *
     * 当单元测试模式开启的时候, 会使用不同的 Lazy Implementation 辅助测试
     *
     * @param name 对象名称, 打印错误日志的时候会用到
     * @param initializer 用来求值的回调函数
     */
    inline fun <T> wxLazy(name: String, crossinline initializer: () -> T?): Lazy<T> {
        return if (wxUnitTestMode) {
            UnitTestLazyImpl {
                initializer() ?: throw Error("Failed to evaluate $name")
            }
        } else {
            lazy(LazyThreadSafetyMode.PUBLICATION) {
                when (null) {
                    wxVersion     -> throw Error("Invalid wxVersion")
                    wxPackageName -> throw Error("Invalid wxPackageName")
                    wxLoader      -> throw Error("Invalid wxLoader")
                    wxClasses     -> throw Error("Invalid wxClasses")
                }
                initializer() ?: throw Error("Failed to evaluate $name")
            }
        }
    }

    /**
     * 用来帮助单元测试的一个 Lazy Implementation, 允许开发者多次初始化一个惰性求值对象
     */
    class UnitTestLazyImpl<out T>(private val initializer: () -> T): Lazy<T>, java.io.Serializable {
        @Volatile private var lazyValue: Lazy<T> = lazy(initializer)

        fun refresh() {
            lazyValue = lazy(initializer)
        }

        override val value: T
            get() = lazyValue.value

        override fun toString(): String = lazyValue.toString()

        override fun isInitialized(): Boolean = lazyValue.isInitialized()
    }

    /**
     * 初始化当前的 [WechatGlobal]
     *
     * @param lpparam 通过重载 [IXposedHookLoadPackage.handleLoadPackage] 方法拿到的
     * [XC_LoadPackage.LoadPackageParam] 对象
     */
    @JvmStatic fun init(lpparam: XC_LoadPackage.LoadPackageParam) {
        tryAsynchronously {
            if (initChannel.isDone()) {
                return@tryAsynchronously
            }

            try {
                wxVersion = getApplicationVersion(lpparam.packageName)
                wxPackageName = lpparam.packageName
                wxLoader = lpparam.classLoader

                ApkFile(lpparam.appInfo.sourceDir).use {
                    wxClasses = it.classTypes
                }
            } finally {
                initChannel.done()
            }
        }
    }
}