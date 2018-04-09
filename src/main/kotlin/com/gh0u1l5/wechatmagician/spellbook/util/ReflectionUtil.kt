package com.gh0u1l5.wechatmagician.spellbook.util

import android.util.Log
import com.gh0u1l5.wechatmagician.spellbook.WechatGlobal
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookMethod
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

// ReflectionUtil is a helper object for static analysis
object ReflectionUtil {

    class ClassName(classType: String) { /* classType example: Ljava/lang/String; */
        val sections = classType.substring(1, classType.length - 1).split('/')
        val size = sections.size
        val className = sections.joinToString(".")
    }

    class Classes(private val classes: List<Class<*>>) {
        fun filterBySuper(superClass: Class<*>?): Classes {
            return Classes(classes.filter { it.superclass == superClass })
        }

        fun filterByEnclosingClass(enclosingClass: Class<*>?): Classes {
            return Classes(classes.filter { it.enclosingClass == enclosingClass })
        }

        fun filterByMethod(returnType: Class<*>?, methodName: String, vararg parameterTypes: Class<*>): Classes {
            return Classes(classes.filter { clazz ->
                val method = findMethodExactIfExists(clazz, methodName, *parameterTypes)
                method != null && method.returnType == returnType ?: method.returnType
            })
        }

        fun filterByMethod(returnType: Class<*>?, vararg parameterTypes: Class<*>): Classes {
            return Classes(classes.filter { clazz ->
                findMethodsByExactParameters(clazz, returnType, *parameterTypes).isNotEmpty()
            })
        }

        fun filterByField(fieldName: String, fieldType: String): Classes {
            return Classes(classes.filter { clazz ->
                val field = findFieldIfExists(clazz, fieldName)
                field != null && field.type.canonicalName == fieldType
            })
        }

        fun filterByField(fieldType: String): Classes {
            return Classes(classes.filter { clazz ->
                findFieldsWithType(clazz, fieldType).isNotEmpty()
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

    // classCache stores the result of findClassesFromPackage to speed up next search.
    private val classCache: MutableMap<Pair<String, Int>, Classes> = ConcurrentHashMap()

    @JvmStatic fun clearClassCache() {
        classCache.clear()
    }

    // methodCache stores the result of findFieldExact to speed up next search.
    private val methodCache: MutableMap<String, Method?> = ConcurrentHashMap()

    @JvmStatic fun clearMethodCache() {
        methodCache.clear()
    }

    // shadowCopy copy all the fields of the object obj into the object copy.
    @JvmStatic fun shadowCopy(obj: Any, copy: Any, clazz: Class<*>? = obj::class.java) {
        if (clazz == null) {
            return
        }
        shadowCopy(obj, copy, clazz.superclass)
        clazz.declaredFields.forEach {
            it.isAccessible = true
            it.set(copy, it.get(obj))
        }
    }

    // findClassIfExists looks up and returns a class if it exists, otherwise it returns null.
    @JvmStatic fun findClassIfExists(className: String, classLoader: ClassLoader): Class<*>? {
        try {
            return Class.forName(className, false, classLoader)
        } catch (throwable: Throwable) {
            if (WechatGlobal.wxUnitTestMode) {
                throw throwable
            }
        }
        return null
    }

    // findClassesFromPackage returns a list of all the classes contained in the given package.
    @JvmStatic fun findClassesFromPackage(loader: ClassLoader, classes: List<ClassName>, packageName: String, depth: Int = 0): Classes {
        if ((packageName to depth) in classCache) {
            return classCache[packageName to depth]!!
        }

        val sections = packageName.split(".")
        val result = Classes(classes.filter { clazz ->
            val currentSections = clazz.sections.dropLast(1)
            // Check depth
            if (currentSections.size < sections.size) {
                return@filter false
            }
            val currentDepth = currentSections.size - sections.size
            if (depth != -1 && depth != currentDepth) {
                return@filter false
            }
            // Check prefix
            for (i in sections.indices) {
                if (currentSections[i] != sections[i]) {
                    return@filter false
                }
            }
            return@filter true
        }.mapNotNull { findClassIfExists(it.className, loader) })

        classCache[packageName to depth] = result
        return result
    }

    private fun getParametersString(vararg clazzes: Class<*>): String =
            "(${clazzes.joinToString(","){ it.canonicalName }})"

    @JvmStatic fun findMethodExact(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Method {
        val fullMethodName = "${clazz.name}#$methodName${getParametersString(*parameterTypes)}#exact"
        if (fullMethodName in methodCache) {
            return methodCache[fullMethodName] ?: throw NoSuchMethodError(fullMethodName)
        }
        try {
            val method = clazz.getDeclaredMethod(methodName, *parameterTypes).apply {
                isAccessible = true
            }
            methodCache[fullMethodName] = method
            return method
        } catch (e: NoSuchMethodException) {
            methodCache[fullMethodName] = null
            throw NoSuchMethodError(fullMethodName)
        }
    }

    // findMethodExactIfExists looks up and returns a method if it exists, otherwise it returns null.
    @JvmStatic fun findMethodExactIfExists(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Method? =
            try { findMethodExact(clazz, methodName, *parameterTypes) } catch (_: Throwable) { null }

    // findMethodsByExactParameters returns a list of all methods declared/overridden in a class with the specified parameter types.
    @JvmStatic fun findMethodsByExactParameters(clazz: Class<*>, returnType: Class<*>?, vararg parameterTypes: Class<*>): List<Method> {
        return clazz.declaredMethods.filter { method ->
            if (returnType != null && returnType != method.returnType) {
                return@filter false
            }

            val methodParameterTypes = method.parameterTypes
            if (parameterTypes.size != methodParameterTypes.size) {
                return@filter false
            }
            for (i in parameterTypes.indices) {
                if (parameterTypes[i] != methodParameterTypes[i]) {
                    return@filter false
                }
            }

            method.isAccessible = true
            return@filter true
        }
    }

    // findFieldIfExists looks up and returns a field if it exists, otherwise it returns null
    @JvmStatic fun findFieldIfExists(clazz: Class<*>, fieldName: String): Field? =
            try { clazz.getField(fieldName) } catch (_: Throwable) { null }

    // findFieldsWithGenericType finds all the fields of the given type.
    @JvmStatic fun findFieldsWithType(clazz: Class<*>, typeName: String): List<Field> {
        return clazz.declaredFields.filter {
            it.type.name == typeName
        }
    }

    // findFieldsWithGenericType finds all the fields of the given generic type.
    @JvmStatic fun findFieldsWithGenericType(clazz: Class<*>, genericTypeName: String): List<Field> {
        return clazz.declaredFields.filter {
            it.genericType.toString() == genericTypeName
        }
    }

    @JvmStatic fun hookAllMethodsInClass(clazz: Class<*>, callback: XC_MethodHook) {
        clazz.declaredMethods.forEach { method -> hookMethod(method, callback) }
    }
}
