package com.gh0u1l5.wechatmagician.spellbook.util

import java.util.concurrent.Executors
import kotlin.concurrent.thread

object ParallelUtil {
    val processors = Runtime.getRuntime().availableProcessors()

    /**
     * Generates a ExecutorService with fixed threads.
     *
     * @param nThread the maximum number of threads, which by default is the number of available
     * processors.
     */
    fun createThreadPool(nThread: Int = processors) = Executors.newFixedThreadPool(processors)

    /**
     * Returns a list containing the results of applying the given [transform] function
     * to each element in the original collection.
     */
    inline fun <T, R> List<T>.parallelMap(crossinline transform: (T) -> R): List<R> {
        val sectionSize = size / processors

        val main = List(processors) { mutableListOf<R>() }
        (0 until processors).map { section ->
            thread(start = true) {
                for (offset in 0 until sectionSize) {
                    val idx = section * sectionSize + offset
                    main[section].add(transform(this[idx]))
                }
            }
        }.forEach { it.join() }

        val rest = (0 until size % processors).map { offset ->
            val idx = processors * sectionSize + offset
            transform(this[idx])
        }

        return main.flatten() + rest
    }

    /**
     * Performs the given [action] on each element.
     */
    inline fun <T> Iterable<T>.parallelForEach(crossinline action: (T) -> Unit) {
        val pool = createThreadPool()
        val iterator = iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            pool.execute { action(item) }
        }
    }
}