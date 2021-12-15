package com.example.plugins

import com.example.routes.getAllHeroes
import com.example.routes.root
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.http.content.*

fun Application.configureRouting() {

    routing {
        root()
        getAllHeroes()

        static("/images") {
            resources("images")
        }
    }
}
