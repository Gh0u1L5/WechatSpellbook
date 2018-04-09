package com.gh0u1l5.wechatmagician.spellbook.mirror

import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import com.gh0u1l5.wechatmagician.spellbook.util.MirrorUtil.clearUnitTestLazyFields
import com.gh0u1l5.wechatmagician.spellbook.util.MirrorUtil.collectFields
import com.gh0u1l5.wechatmagician.spellbook.util.MirrorUtil.generateReport
import com.gh0u1l5.wechatmagician.spellbook.util.MirrorUtil.generateReportWithForceEval
import org.junit.Assert.assertEquals
import org.junit.Test

class ReflectionUnitTest {
    companion object {
        const val TestObject1Name =
                "com.gh0u1l5.wechatmagician.spellbook.mirror.ReflectionUnitTest.TestObject1"
        const val TestObject2Name =
                "com.gh0u1l5.wechatmagician.spellbook.mirror.ReflectionUnitTest.TestObject2"
    }

    object TestObject1 {
        val var1 = 1
        val var2 = 2
        val var3 = 3
    }

    object TestObject2 {
        val var1 by lazy { 1 }
        val var2 by lazy { 2 }
    }

    object TestObject3 {
        val var1 by WechatGlobal.UnitTestLazyImpl{ 1 }
        val var2 by WechatGlobal.UnitTestLazyImpl{ 2 }
    }

    @Test fun testCollectFields() {
        val fields = collectFields(TestObject1)
        assertEquals(3, fields.size)
        fields.forEach {
            when (it.first) {
                "var1" -> assertEquals(1, it.second)
                "var2" -> assertEquals(2, it.second)
                "var3" -> assertEquals(3, it.second)
                else -> throw Exception("Unknown key: ${it.first}")
            }
        }
    }

    @Test fun testGenerateReport() {
        val report1 = generateReport(listOf(TestObject1, TestObject2))
        assertEquals(5, report1.size)
        report1.forEach {
            when (it.first) {
                "$TestObject1Name.var1" -> assertEquals("1", it.second)
                "$TestObject1Name.var2" -> assertEquals("2", it.second)
                "$TestObject1Name.var3" -> assertEquals("3", it.second)
                "$TestObject2Name.var1" -> assertEquals(lazy { 1 }.toString(), it.second)
                "$TestObject2Name.var2" -> assertEquals(lazy { 2 }.toString(), it.second)
                else -> throw Exception("Unknown key: ${it.first}")
            }
        }

        val report2 = generateReportWithForceEval(listOf(TestObject1, TestObject2))
        assertEquals(5, report2.size)
        report2.forEach {
            when (it.first) {
                "$TestObject1Name.var1" -> assertEquals("1", it.second)
                "$TestObject1Name.var2" -> assertEquals("2", it.second)
                "$TestObject1Name.var3" -> assertEquals("3", it.second)
                "$TestObject2Name.var1" -> assertEquals("1", it.second)
                "$TestObject2Name.var2" -> assertEquals("2", it.second)
                else -> throw Exception("Unknown key: ${it.first}")
            }
        }
    }

    @Test fun testClearUnitTestLazyFields() {
        assertEquals(1, TestObject3.var1)
        assertEquals(2, TestObject3.var2)

        clearUnitTestLazyFields(TestObject3)

        val fields = collectFields(TestObject3)
        assertEquals(2, fields.size)
        fields.forEach {
            when (it.first) {
                "var1" -> assertEquals(lazy { 1 }.toString(), it.second.toString())
                "var2" -> assertEquals(lazy { 2 }.toString(), it.second.toString())
                else -> throw Exception("Unknown key: ${it.first}")
            }
        }

        assertEquals(1, TestObject3.var1)
        assertEquals(2, TestObject3.var2)
    }
}