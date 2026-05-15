package com.nineeditcloud.editletterchat

class Greeting {
    private val platform =getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}



interface Platform {
    val name: String
}
expect fun getPlatform(): Platform