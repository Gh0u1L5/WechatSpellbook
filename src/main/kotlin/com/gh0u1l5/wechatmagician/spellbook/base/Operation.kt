package com.gh0u1l5.wechatmagician.spellbook.base

class Operation<out T>(
        val value: T? = null,
        val error: Throwable? = null,
        val priority: Int = 0,
        val returnEarly: Boolean = false
) {
    companion object {
        /**
         * Returns an empty operation that indicates no operation (no interruption, no replacement).
         */
        fun <T>nop(priority: Int = 0): Operation<T> {
            return Operation(priority = priority)
        }

        /**
         * Returns an operation that indicates throwing a throwable without actually executing the
         * original method.
         * @param error the error thrown to the caller.
         * @param priority the non-negative priority of this error. The framework will eventually
         * throw an error or return a value which has the highest priority.
         */
        fun <T>interruption(error: Throwable, priority: Int = 0): Operation<T> {
            return Operation(error = error, priority = priority, returnEarly = true)
        }

        /**
         * Returns an operation that indicates returning a value without actually executing the original
         * method.
         * @param value the return value given to the caller.
         * @param priority the non-negative priority of this return value. The framework will eventually
         * throw an error or return a value which has the highest priority.
         */
        fun <T>replacement(value: T, priority: Int = 0): Operation<T> {
            return Operation(value = value, priority = priority, returnEarly = true)
        }
    }
}