import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.base.Version
import com.gh0u1l5.wechatmagician.spellbook.mirror.ApkSnapshot
import com.gh0u1l5.wechatmagician.spellbook.util.FileUtil
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class MirrorUnitTest {
    private fun convertPackagesToSnapshots(apkDir: String) {
        val apkPaths = File(apkDir).list { _, name -> name.endsWith(".apk") } ?: arrayOf()
        for (apkPath in apkPaths.map { apkDir + it }) {
            val snapshot = ApkSnapshot.convertPackageToSnapshot(apkPath)
            val snapshotPath = apkPath.removeSuffix(".apk") + ".snapshot"
            FileUtil.writeObjectToDisk(snapshotPath, snapshot)
        }
    }

    @Test fun convertPlayStorePackagesToSnapshots() {
        convertPackagesToSnapshots("./wechat/play-store/")
    }

    @Test fun convertDomesticPackagesToSnapshots() {
        convertPackagesToSnapshots("./wechat/domestic/")
    }

    private fun verifySnapshot(snapshot: ApkSnapshot): Boolean {
        WechatGlobal.wxUnitTestMode = true
        WechatGlobal.wxVersion = Version(snapshot.versionName)
        WechatGlobal.wxPackageName = snapshot.packageName
        WechatGlobal.wxLoader = ApkSnapshot.ApkSnapshotClassLoader(snapshot)
        WechatGlobal.wxClasses = snapshot.classes.keys.toList()



        return true
    }

    private fun verifySnapshots(snapshotDir: String) {
        val snapshotPaths = File(snapshotDir).list { _, name -> name.endsWith(".snapshot") } ?: arrayOf()
        for (snapshotPath in snapshotPaths.map { snapshotDir + it }) {
            val snapshot = FileUtil.readObjectFromDisk(snapshotPath)
            assertTrue(verifySnapshot(snapshot as ApkSnapshot))
        }
    }

    @Test fun testPlayStoreApkSnapshots() {
        verifySnapshots("./wechat/play-store/")
    }

    @Test fun testDomesticApkSnapshots() {
        verifySnapshots("./wechat/play-store/")
    }
}