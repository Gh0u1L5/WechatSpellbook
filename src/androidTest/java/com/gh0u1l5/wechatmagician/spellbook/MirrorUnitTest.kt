package com.gh0u1l5.wechatmagician.spellbook

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.base.ApkSnapshot
import com.gh0u1l5.wechatmagician.spellbook.mirror.MirrorClasses
import com.gh0u1l5.wechatmagician.spellbook.util.FileUtil
import com.gh0u1l5.wechatmagician.spellbook.util.MirrorUtil.generateReportWithForceEval
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil
import dalvik.system.PathClassLoader
import net.dongliu.apk.parser.ApkFile
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class MirrorUnitTest {
//    class FakeClassLoader: ClassLoader() {
//        object FakeClass
//
//        override fun findClass(name: String?): Class<*> {
//            return FakeClass::class.java
//        }
//
//        override fun loadClass(name: String?): Class<*>? {
//            return loadClass(name, false)
//        }
//
//        override fun loadClass(name: String?, resolve: Boolean): Class<*>? {
//            return FakeClass::class.java
//        }
//    }
//
//    private fun convertPackagesToSnapshots(apkDir: String) {
//        val apkPaths = File(apkDir).list { _, name -> name.endsWith(".apk") } ?: arrayOf()
//        for (apkPath in apkPaths.map { "$apkDir/$it" }) {
//            val snapshot = ApkSnapshot.convertPackageToSnapshot(apkPath)
//            val snapshotPath = apkPath.removeSuffix(".apk") + ".snapshot"
//            FileUtil.writeObjectToDisk(snapshotPath, snapshot)
//        }
//    }
//
//    @Test fun convertDomesticPackagesToSnapshots() {
//        convertPackagesToSnapshots("./wechat/domestic/")
//    }
//
//    @Test fun convertPlayStorePackagesToSnapshots() {
//        convertPackagesToSnapshots("./wechat/play-store/")
//    }
//
//    private fun verifySnapshot(snapshot: ApkSnapshot): Boolean {
//        WechatGlobal.wxUnitTestMode = true
//        WechatGlobal.wxVersion = Version(snapshot.versionName)
//        WechatGlobal.wxPackageName = snapshot.packageName
//        WechatGlobal.wxLoader = FakeClassLoader()
//        WechatGlobal.wxClasses = snapshot.classes.values.toTypedArray()
//
//        generateReportWithForceEval(MirrorClasses).forEach {
//            println(it.second)
//        }
//
//        return true
//    }
//
//    private fun verifySnapshots(snapshotDir: String) {
//        val snapshotPaths = File(snapshotDir).list { _, name -> name.endsWith(".snapshot") } ?: arrayOf()
//        for (snapshotPath in snapshotPaths.map { snapshotDir + it }) {
//            val snapshot = FileUtil.readObjectFromDisk(snapshotPath)
//            assertTrue(verifySnapshot(snapshot as ApkSnapshot))
//        }
//    }

    private fun verifyApk(apkPath: String): Boolean {
        ApkFile(apkPath).use {
            WechatGlobal.wxVersion = Version(it.apkMeta.versionName)
            WechatGlobal.wxPackageName = it.apkMeta.packageName
            WechatGlobal.wxLoader = PathClassLoader(apkPath, ClassLoader.getSystemClassLoader())
            WechatGlobal.wxClasses = it.dexClasses.map { clazz ->
                ReflectionUtil.getClassName(clazz)
            }

            generateReportWithForceEval(MirrorClasses).forEach {
                println(it.second)
            }

            return true
        }
    }

    private fun verifyApks(apkDir: String) {
        val apkPaths = File(apkDir).list { _, name -> name.endsWith(".apk") } ?: arrayOf()
        for (apkPath in apkPaths.map { "$apkDir/$it" }) {
            assertTrue(verifyApk(apkPath))
        }
    }

    @Test fun testDomesticApkSnapshots() {
        val appContext = InstrumentationRegistry.getContext()
        verifyApks("${appContext.filesDir}/wechat/domestic/")
    }

    @Test fun testPlayStoreApkSnapshots() {
        val appContext = InstrumentationRegistry.getContext()
        verifyApks("${appContext.filesDir}/wechat/play-store/")
    }
}