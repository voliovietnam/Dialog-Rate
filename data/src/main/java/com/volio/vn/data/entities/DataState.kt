package com.volio.vn.data.entities

sealed class DataState<out T> {
    data class Loading<T>(val data: T? = null) : DataState<T>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Error<T>(val error: Exception, val data: T? = null) : DataState<T>()
}

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
class Event<T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content

    override fun toString(): String {
        return "Event(content=$content, hasBeenHandled=$hasBeenHandled)"
    }

    companion object {

        fun createErrorEvent(message: String): Event<String> {
            return Event(message)

        }

        fun createErrorEvent(code: Int): Event<Int> {
            return Event(code)

        }
    }

}
