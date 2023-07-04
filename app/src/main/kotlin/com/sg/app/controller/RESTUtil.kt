package com.sg.app.controller

import com.sg.app.config.WebConfigurer

class RESTUtil {
    companion object {
        inline fun <reified T> receiveAsValue(s: String): T {
            val mapper = WebConfigurer.mapper
            return mapper.readValue(s, T::class.java)
        }
    }
}
