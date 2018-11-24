package com.gh0u1l5.wechatmagician.spellbook.base

import android.util.Log
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findFieldIfExists
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findFieldsWithType
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findMethodExactIfExists
import com.gh0u1l5.wechatmagician.spellbook.util.ReflectionUtil.findMethodsByExactParameters

/**
 * 一组 Class 对象的集合, 可以通过调用不同的 filter 函数筛选得到想要的结果
 */
class Classes(private val classes: List<Class<*>>) {
    /**
     * @suppress
     */
    private companion object {
        private const val TAG = "Reflection"
    }
    
    fun filterBySuper(superClass: Class<*>?): Classes {
        return Classes(classes.filter { it.superclass == superClass }.also {
            if (it.isEmpty()) {
                Log.w(TAG, "filterBySuper found nothing, super class = ${superClass?.simpleName}")
            }
        })
    }

    fun filterByEnclosingClass(enclosingClass: Class<*>?): Classes {
        return Classes(classes.filter { it.enclosingClass == enclosingClass }.also {
            if (it.isEmpty()) {
                Log.w(TAG, "filterByEnclosingClass found nothing, enclosing class = ${enclosingClass?.simpleName} ")
            }
        })
    }

    fun filterByMethod(returnType: Class<*>?, methodName: String, vararg parameterTypes: Class<*>): Classes {
        return Classes(classes.filter { clazz ->
            val method = findMethodExactIfExists(clazz, methodName, *parameterTypes)
            method != null && method.returnType == returnType ?: method.returnType
        }.also {
            if (it.isEmpty()) {
                Log.w(TAG, "filterByMethod found nothing, returnType = ${returnType?.simpleName}, methodName = $methodName, parameterTypes = ${parameterTypes.joinToString("|") { it.simpleName }}")
            }
        })
    }

    fun filterByMethod(returnType: Class<*>?, vararg parameterTypes: Class<*>): Classes {
        return Classes(classes.filter { clazz ->
            findMethodsByExactParameters(clazz, returnType, *parameterTypes).isNotEmpty()
        }.also {
            if (it.isEmpty()) {
                Log.w(TAG, "filterByMethod found nothing, returnType = ${returnType?.simpleName}, parameterTypes = ${parameterTypes.joinToString("|") { it.simpleName }}")
            }
        })
    }

    fun filterByField(fieldName: String, fieldType: String): Classes {
        return Classes(classes.filter { clazz ->
            val field = findFieldIfExists(clazz, fieldName)
            field != null && field.type.canonicalName == fieldType
        }.also {
            if (it.isEmpty()) {
                Log.w(TAG, "filterByField found nothing, fieldName = $fieldName, fieldType = $fieldType")
            }
        })
    }

    fun filterByField(fieldType: String): Classes {
        return Classes(classes.filter { clazz ->
            findFieldsWithType(clazz, fieldType).isNotEmpty()
        }.also {
            if (it.isEmpty()) {
                Log.w(TAG, "filterByField found nothing, fieldType = $fieldType")
            }
        })
    }

    fun firstOrNull(): Class<*>? {
        if (classes.size > 1) {
            val names = classes.map { it.canonicalName }
            Log.w("Xposed", "found a signature that matches more than one class: $names")
        }
        return classes.firstOrNull()
    }
}