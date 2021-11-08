package com.chitranjank.notepad

class Note() {
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var time: String

    constructor(title: String, description: String, time: String) : this() {
        this.title = title
        this.description = description
        this.time = time
    }

    fun getTitle(): String {
        return title
    }

    fun getDescription(): String {
        return description
    }

    fun getTime(): String {
        return time
    }
}