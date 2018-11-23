package com.gh0u1l5.wechatmagician.spellbook

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.mirror.MirrorClasses
import com.gh0u1l5.wechatmagician.spellbook.mirror.MirrorFields
import com.gh0u1l5.wechatmagician.spellbook.mirror.MirrorMethods
import com.gh0u1l5.wechatmagician.spellbook.util.FileUtil
import com.gh0u1l5.wechatmagician.spellbook.util.MirrorUtil
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil
import dalvik.system.PathClassLoader
import net.dongliu.apk.parser.ApkFile
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.lang.ClassLoader.getSystemClassLoader

@RunWith(AndroidJUnit4::class)
class MirrorUnitTest {
    companion object {
        private const val DOMESTIC_DIR = "apks/domestic"
        private const val PLAY_STORE_DIR = "apks/play-store"
    }

    private var context: Context? = null

    @Before fun initialize() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    private fun verifyPackage(apkPath: String) {
        val cacheDir = context!!.cacheDir

        val apkFile = File(cacheDir, apkPath)
        try {
            javaClass.classLoader!!.getResourceAsStream(apkPath).use {
                FileUtil.writeInputStreamToDisk(apkFile.absolutePath, it)
            }
        } catch (t: Throwable) {
            Log.w("MirrorUnitTest", t)
            return // ignore if the apk isn't accessible
        }

        assertTrue(apkFile.exists())
        ApkFile(apkFile).use {
            WechatGlobal.wxUnitTestMode = true
            WechatGlobal.wxVersion = Version(it.apkMeta.versionName)
            WechatGlobal.wxPackageName = it.apkMeta.packageName
            WechatGlobal.wxLoader = PathClassLoader(apkFile.absolutePath, getSystemClassLoader())
            WechatGlobal.wxClasses = it.dexClasses.map { clazz ->
                ReflectionUtil.ClassName(clazz.classType)
            }

            val objects = MirrorClasses + MirrorMethods + MirrorFields
            ReflectionUtil.clearClassCache()
            ReflectionUtil.clearMethodCache()
            objects.forEach { instance ->
                MirrorUtil.clearUnitTestLazyFields(instance)
            }

            MirrorUtil.generateReportWithForceEval(objects).forEach {
                Log.d("MirrorUnitTest", "Verified ${it.first} -> ${it.second}")
            }
        }

        apkFile.delete()
    }

    @Test fun verifyDomesticPackage6_6_0() {
        verifyPackage("$DOMESTIC_DIR/wechat-v6.6.0.apk")
    }

    @Test fun verifyDomesticPackage6_6_1() {
        verifyPackage("$DOMESTIC_DIR/wechat-v6.6.1.apk")
    }

    @Test fun verifyDomesticPackage6_6_2() {
        verifyPackage("$DOMESTIC_DIR/wechat-v6.6.2.apk")
    }

    @Test fun verifyDomesticPackage6_6_3() {
        verifyPackage("$DOMESTIC_DIR/wechat-v6.6.3.apk")
    }

    @Test fun verifyDomesticPackage6_6_5() {
        verifyPackage("$DOMESTIC_DIR/wechat-v6.6.5.apk")
    }

    @Test fun verifyDomesticPackage6_6_6() {
        verifyPackage("$DOMESTIC_DIR/wechat-v6.6.6.apk")
    }

    @Test fun verifyDomesticPackage6_6_7() {
        verifyPackage("$DOMESTIC_DIR/wechat-v6.6.7.apk")
    }

    @Test fun verifyDomesticPackage6_7_2() {
        verifyPackage("$DOMESTIC_DIR/wechat-v6.7.2.apk")
    }

    @Test fun verifyDomesticPackage6_7_3() {
        verifyPackage("$DOMESTIC_DIR/wechat-v6.7.3.apk")
    }

    @Test fun verifyPlayStorePackage6_6_1() {
        verifyPackage("$PLAY_STORE_DIR/wechat-v6.6.1.apk")
    }

    @Test fun verifyPlayStorePackage6_6_2() {
        verifyPackage("$PLAY_STORE_DIR/wechat-v6.6.2.apk")
    }

    @Test fun verifyPlayStorePackage6_6_6() {
        verifyPackage("$PLAY_STORE_DIR/wechat-v6.6.6.apk")
    }

    @Test fun verifyPlayStorePackage6_6_7() {
        verifyPackage("$PLAY_STORE_DIR/wechat-v6.6.7.apk")
    }

    @Test fun verifyPlayStorePackage6_7_3() {
        verifyPackage("$PLAY_STORE_DIR/wechat-v6.7.3.apk")
    }
}