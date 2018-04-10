package com.gh0u1l5.wechatmagician.spellbook

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.mirror.MirrorClasses
import com.gh0u1l5.wechatmagician.spellbook.mirror.MirrorFields
import com.gh0u1l5.wechatmagician.spellbook.mirror.MirrorMethods
import com.gh0u1l5.wechatmagician.spellbook.util.MirrorUtil.clearUnitTestLazyFields
import com.gh0u1l5.wechatmagician.spellbook.util.MirrorUtil.generateReportWithForceEval
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil
import dalvik.system.PathClassLoader
import net.dongliu.apk.parser.ApkFile
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class MirrorUnitTest {
    companion object {
        private const val DOMESTIC_DIR = "wechat/domestic/"
        private const val PLAY_STORE_DIR = "wechat/play-store/"
    }

    private var context: Context? = null

    @Before fun initialize() {
        context = InstrumentationRegistry.getContext()
    }

    private fun verifyPackage(apkPath: String): Boolean {
        if (!File(apkPath).exists()) {
            return true // ignore if the apk isn't accessible
        }
        ApkFile(apkPath).use {
            WechatGlobal.wxUnitTestMode = true
            WechatGlobal.wxVersion = Version(it.apkMeta.versionName)
            WechatGlobal.wxPackageName = it.apkMeta.packageName
            WechatGlobal.wxLoader = PathClassLoader(apkPath, ClassLoader.getSystemClassLoader())
            WechatGlobal.wxClasses = it.dexClasses.map { clazz ->
                ReflectionUtil.ClassName(clazz.classType)
            }

            val objects = MirrorClasses + MirrorMethods + MirrorFields
            ReflectionUtil.clearClassCache()
            ReflectionUtil.clearMethodCache()
            objects.forEach { instance ->
                clearUnitTestLazyFields(instance)
            }
            generateReportWithForceEval(objects).forEach {
                Log.d("MirrorUnitTest", "Verified ${it.first} -> ${it.second}")
            }

            return true
        }
    }

    @Test fun verifyDomesticPackage6_5_3() {
        val appDir = "${context!!.filesDir}/$DOMESTIC_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.5.3.apk"))
    }

    @Test fun verifyDomesticPackage6_5_4() {
        val appDir = "${context!!.filesDir}/$DOMESTIC_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.5.4.apk"))
    }

    @Test fun verifyDomesticPackage6_5_7() {
        val appDir = "${context!!.filesDir}/$DOMESTIC_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.5.7.apk"))
    }

    @Test fun verifyDomesticPackage6_5_8() {
        val appDir = "${context!!.filesDir}/$DOMESTIC_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.5.8.apk"))
    }

    @Test fun verifyDomesticPackage6_5_10() {
        val appDir = "${context!!.filesDir}/$DOMESTIC_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.5.10.apk"))
    }

    @Test fun verifyDomesticPackage6_5_13() {
        val appDir = "${context!!.filesDir}/$DOMESTIC_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.5.13.apk"))
    }

    @Test fun verifyDomesticPackage6_5_16() {
        val appDir = "${context!!.filesDir}/$DOMESTIC_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.5.16.apk"))
    }

    @Test fun verifyDomesticPackage6_5_19() {
        val appDir = "${context!!.filesDir}/$DOMESTIC_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.5.19.apk"))
    }

    @Test fun verifyDomesticPackage6_5_22() {
        val appDir = "${context!!.filesDir}/$DOMESTIC_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.5.22.apk"))
    }

    @Test fun verifyDomesticPackage6_5_23() {
        val appDir = "${context!!.filesDir}/$DOMESTIC_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.5.23.apk"))
    }

    @Test fun verifyDomesticPackage6_6_0() {
        val appDir = "${context!!.filesDir}/$DOMESTIC_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.6.0.apk"))
    }

    @Test fun verifyDomesticPackage6_6_1() {
        val appDir = "${context!!.filesDir}/$DOMESTIC_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.6.1.apk"))
    }

    @Test fun verifyDomesticPackage6_6_2() {
        val appDir = "${context!!.filesDir}/$DOMESTIC_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.6.2.apk"))
    }

    @Test fun verifyDomesticPackage6_6_3() {
        val appDir = "${context!!.filesDir}/$DOMESTIC_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.6.3.apk"))
    }

    @Test fun verifyDomesticPackage6_6_5() {
        val appDir = "${context!!.filesDir}/$DOMESTIC_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.6.5.apk"))
    }

    @Test fun verifyPlayStorePackage6_5_8() {
        val appDir = "${context!!.filesDir}/$PLAY_STORE_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.5.8.apk"))
    }

    @Test fun verifyPlayStorePackage6_5_10() {
        val appDir = "${context!!.filesDir}/$PLAY_STORE_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.5.10.apk"))
    }

    @Test fun verifyPlayStorePackage6_5_23() {
        val appDir = "${context!!.filesDir}/$PLAY_STORE_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.5.23.apk"))
    }

    @Test fun verifyPlayStorePackage6_6_1() {
        val appDir = "${context!!.filesDir}/$PLAY_STORE_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.6.1.apk"))
    }

    @Test fun verifyPlayStorePackage6_6_2() {
        val appDir = "${context!!.filesDir}/$PLAY_STORE_DIR"
        assertTrue(verifyPackage("$appDir/wechat-v6.6.2.apk"))
    }
}