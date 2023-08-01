package com.sg.app

import org.springframework.context.annotation.Configuration
import kotlinx.coroutines.Dispatchers
import org.springframework.context.annotation.Bean
import kotlinx.coroutines.CoroutineDispatcher
@Configuration
public class ApplicationConfig {
    @Bean
    fun coroutineDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
