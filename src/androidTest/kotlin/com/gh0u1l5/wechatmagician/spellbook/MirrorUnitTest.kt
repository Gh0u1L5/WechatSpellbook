package com.gh0u1l5.wechatmagician.spellbook

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.mirror.MirrorClasses
import com.gh0u1l5.wechatmagician.spellbook.mirror.MirrorFields
import com.gh0u1l5.wechatmagician.spellbook.mirror.MirrorMethods
import com.gh0u1l5.wechatmagician.spellbook.parser.ApkFile
import com.gh0u1l5.wechatmagician.spellbook.util.FileUtil
import com.gh0u1l5.wechatmagician.spellbook.util.MirrorUtil
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil
import dalvik.system.PathClassLoader
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.lang.ClassLoader.getSystemClassLoader
import kotlin.system.measureTimeMillis

@ExperimentalUnsignedTypes
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
        // Parse the version of the apk
        val regex = Regex("wechat-v(.*)\\.apk")
        val match = regex.find(apkPath) ?: throw Exception("Unexpected path format")
        val version = match.groupValues[1]

        // Store APK file to cache directory.
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

        // Ensure the apk is presented, and start the test
        assertTrue(apkFile.exists())
        ApkFile(apkFile).use {
            // Benchmark the APK parser
            val timeParseDex = measureTimeMillis { it.classTypes }
            Log.d("MirrorUnitTest", "Benchmark: Parse DexClasses takes $timeParseDex ms.")

            // Initialize WechatGlobal
            WechatGlobal.wxUnitTestMode = true
            WechatGlobal.wxVersion = Version(version)
            WechatGlobal.wxPackageName = "com.tencent.mm"
            WechatGlobal.wxLoader = PathClassLoader(apkFile.absolutePath, getSystemClassLoader())
            WechatGlobal.wxClasses = it.classTypes

            // Clear cached lazy evaluations
            val objects = MirrorClasses + MirrorMethods + MirrorFields
            ReflectionUtil.clearClassCache()
            ReflectionUtil.clearMethodCache()
            objects.forEach { instance ->
                MirrorUtil.clearUnitTestLazyFields(instance)
            }

            // Test each lazy evaluation and generate result.
            var result: List<Pair<String, String>>? = null
            val timeSearch = measureTimeMillis {
                result = MirrorUtil.generateReportWithForceEval(objects)
            }
            Log.d("MirrorUnitTest", "Benchmark: Search over classes takes $timeSearch ms.")
            result?.forEach { entry ->
                Log.d("MirrorUnitTest", "Verified: ${entry.first} -> ${entry.second}")
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