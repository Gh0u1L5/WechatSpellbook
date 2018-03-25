package com.gh0u1l5.wechatmagician.spellbook.base

class Operation<out T>(val value: T, val priority: Int = 0, val returnEarly: Boolean) {
    companion object {
        /**
         * Returns an empty object that indicates no operation (no interruption, no replacement).
         */
        fun <T>nop(): Operation<T?> {
            return Operation(value = null, returnEarly = false)
        }

        /**
         * Returns an empty object that indicates bypassing the original method.
         */
        fun <T>interruption(): Operation<T?> {
            return Operation(value = null, returnEarly = true)
        }

        /**
         * Returns an object that indicates replacing the return value of original method (and
         * bypassing the original method).
         * @param value the return value given to caller.
         * @param priority the non-negative priority of this return value. The framework will eventually
         * return the value with the highest priority.
         */
        fun <T>replacement(value: T, priority: Int = 0): Operation<T?> {
            return Operation(value, priority, returnEarly = true)
        }
    }
}