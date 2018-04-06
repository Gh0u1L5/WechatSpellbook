package com.gh0u1l5.wechatmagician.spellbook.hookers

import android.content.ContentValues
import com.gh0u1l5.wechatmagician.spellbook.C
import com.gh0u1l5.wechatmagician.spellbook.base.EventCenter
import com.gh0u1l5.wechatmagician.spellbook.base.Hooker
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IDatabaseHook
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.wcdb.Classes.SQLiteErrorHandler
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.wcdb.database.Classes.SQLiteCursorFactory
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.wcdb.database.Classes.SQLiteDatabase
import com.gh0u1l5.wechatmagician.spellbook.mirror.com.tencent.wcdb.support.Classes.SQLiteCancellationSignal
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers.findAndHookMethod

object Database : EventCenter() {

    override val interfaces: List<Class<*>>
        get() = listOf(IDatabaseHook::class.java)

    override fun provideEventHooker(event: String): Hooker? {
        return when (event) {
            "onDatabaseOpening", "onDatabaseOpened"     -> onOpenHooker
            "onDatabaseQuerying", "onDatabaseQueried"   -> onQueryHooker
            "onDatabaseInserting", "onDatabaseInserted" -> onInsertHooker
            "onDatabaseUpdating", "onDatabaseUpdated"   -> onUpdateHooker
            "onDatabaseDeleting", "onDatabaseDeleted"   -> onDeleteHooker
            "onDatabaseExecuting", "onDatabaseExecuted" -> onExecuteHooker
            else -> throw IllegalArgumentException("Unknown event: $event")
        }
    }

    private val onOpenHooker = Hooker {
        findAndHookMethod(
                SQLiteDatabase, "openDatabase",
                C.String, SQLiteCursorFactory, C.Int, SQLiteErrorHandler, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val path     = param.args[0] as String
                val factory  = param.args[1]
                val flags    = param.args[2] as Int
                val handler  = param.args[3]
                notifyWithOperation("onDatabaseOpening", param) { plugin ->
                    (plugin as IDatabaseHook).onDatabaseOpening(path, factory, flags, handler)
                }
            }
            override fun afterHookedMethod(param: MethodHookParam) {
                val path     = param.args[0] as String
                val factory  = param.args[1]
                val flags    = param.args[2] as Int
                val handler  = param.args[3]
                val result   = param.result
                notifyWithOperation("onDatabaseOpened", param) { plugin ->
                    (plugin as IDatabaseHook).onDatabaseOpened(path, factory, flags, handler, result)
                }
            }
        })
    }

    private val onQueryHooker = Hooker {
        findAndHookMethod(
                SQLiteDatabase, "rawQueryWithFactory",
                SQLiteCursorFactory, C.String, C.StringArray, C.String, SQLiteCancellationSignal, object : XC_MethodHook() {
            @Suppress("UNCHECKED_CAST")
            override fun beforeHookedMethod(param: MethodHookParam) {
                val thisObject    = param.thisObject
                val factory       = param.args[0]
                val sql           = param.args[1] as String
                val selectionArgs = param.args[2] as Array<String>?
                val editTable     = param.args[3] as String?
                val cancellation  = param.args[4]
                notifyWithOperation("onDatabaseQuerying", param) { plugin ->
                    (plugin as IDatabaseHook).onDatabaseQuerying(
                            thisObject, factory, sql, selectionArgs, editTable, cancellation)
                }
            }
            @Suppress("UNCHECKED_CAST")
            override fun afterHookedMethod(param: MethodHookParam) {
                val thisObject    = param.thisObject
                val factory       = param.args[0]
                val sql           = param.args[1] as String
                val selectionArgs = param.args[2] as Array<String>?
                val editTable     = param.args[3] as String?
                val cancellation  = param.args[4]
                val result        = param.result
                notifyWithOperation("onDatabaseQueried", param) { plugin ->
                    (plugin as IDatabaseHook).onDatabaseQueried(
                            thisObject, factory, sql, selectionArgs, editTable, cancellation, result)
                }
            }
        })
    }

    private val onInsertHooker = Hooker {
        findAndHookMethod(
                SQLiteDatabase, "insertWithOnConflict",
                C.String, C.String, C.ContentValues, C.Int, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val thisObject        = param.thisObject
                val table             = param.args[0] as String
                val nullColumnHack    = param.args[1] as String?
                val initialValues     = param.args[2] as ContentValues?
                val conflictAlgorithm = param.args[3] as Int
                notifyWithOperation("onDatabaseInserting", param) { plugin ->
                    (plugin as IDatabaseHook).onDatabaseInserting(
                            thisObject, table, nullColumnHack, initialValues, conflictAlgorithm)
                }
            }
            override fun afterHookedMethod(param: MethodHookParam) {
                val thisObject        = param.thisObject
                val table             = param.args[0] as String
                val nullColumnHack    = param.args[1] as String?
                val initialValues     = param.args[2] as ContentValues?
                val conflictAlgorithm = param.args[3] as Int
                val result            = param.result as Long
                notifyWithOperation("onDatabaseInserted", param) { plugin ->
                    (plugin as IDatabaseHook).onDatabaseInserted(
                            thisObject, table, nullColumnHack, initialValues, conflictAlgorithm, result)
                }
            }
        })
    }

    private val onUpdateHooker = Hooker {
        findAndHookMethod(
                SQLiteDatabase, "updateWithOnConflict",
                C.String, C.ContentValues, C.String, C.StringArray, C.Int, object : XC_MethodHook() {
            @Suppress("UNCHECKED_CAST")
            override fun beforeHookedMethod(param: MethodHookParam) {
                val thisObject        = param.thisObject
                val table             = param.args[0] as String
                val values            = param.args[1] as ContentValues
                val whereClause       = param.args[2] as String?
                val whereArgs         = param.args[3] as Array<String>?
                val conflictAlgorithm = param.args[4] as Int
                notifyWithOperation("onDatabaseUpdating", param) { plugin ->
                    (plugin as IDatabaseHook).onDatabaseUpdating(
                            thisObject, table, values, whereClause, whereArgs, conflictAlgorithm)
                }
            }
            @Suppress("UNCHECKED_CAST")
            override fun afterHookedMethod(param: MethodHookParam) {
                val thisObject        = param.thisObject
                val table             = param.args[0] as String
                val values            = param.args[1] as ContentValues
                val whereClause       = param.args[2] as String?
                val whereArgs         = param.args[3] as Array<String>?
                val conflictAlgorithm = param.args[4] as Int
                val result            = param.result as Int
                notifyWithOperation("onDatabaseUpdated", param) { plugin ->
                    (plugin as IDatabaseHook).onDatabaseUpdated(
                            thisObject, table, values, whereClause, whereArgs, conflictAlgorithm, result)
                }
            }
        })
    }

    private val onDeleteHooker = Hooker {
        findAndHookMethod(
                SQLiteDatabase, "delete",
                C.String, C.String, C.StringArray, object : XC_MethodHook() {
            @Suppress("UNCHECKED_CAST")
            override fun beforeHookedMethod(param: MethodHookParam) {
                val thisObject  = param.thisObject
                val table       = param.args[0] as String
                val whereClause = param.args[1] as String?
                val whereArgs   = param.args[2] as Array<String>?
                notifyWithOperation("onDatabaseDeleting", param) { plugin ->
                    (plugin as IDatabaseHook).onDatabaseDeleting(thisObject, table, whereClause, whereArgs)
                }
            }
            @Suppress("UNCHECKED_CAST")
            override fun afterHookedMethod(param: MethodHookParam) {
                val thisObject  = param.thisObject
                val table       = param.args[0] as String
                val whereClause = param.args[1] as String?
                val whereArgs   = param.args[2] as Array<String>?
                val result      = param.result as Int
                notifyWithOperation("onDatabaseDeleted", param) { plugin ->
                    (plugin as IDatabaseHook).onDatabaseDeleted(thisObject, table, whereClause, whereArgs, result)
                }
            }
        })
    }

    private val onExecuteHooker = Hooker {
        findAndHookMethod(
                SQLiteDatabase, "executeSql",
                C.String, C.ObjectArray, SQLiteCancellationSignal, object : XC_MethodHook() {
            @Suppress("UNCHECKED_CAST")
            override fun beforeHookedMethod(param: MethodHookParam) {
                val thisObject   = param.thisObject
                val sql          = param.args[0] as String
                val bindArgs     = param.args[1] as Array<Any?>?
                val cancellation = param.args[2]
                notifyWithInterrupt("onDatabaseExecuting", param) { plugin ->
                    (plugin as IDatabaseHook).onDatabaseExecuting(thisObject, sql, bindArgs, cancellation)
                }
            }
            @Suppress("UNCHECKED_CAST")
            override fun afterHookedMethod(param: MethodHookParam) {
                val thisObject   = param.thisObject
                val sql          = param.args[0] as String
                val bindArgs     = param.args[1] as Array<Any?>?
                val cancellation = param.args[2]
                notify("onDatabaseExecuted") { plugin ->
                    (plugin as IDatabaseHook).onDatabaseExecuted(thisObject, sql, bindArgs, cancellation)
                }
            }
        })
    }
}