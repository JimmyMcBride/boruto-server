package com.example

import com.example.models.ApiResponse
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.example.plugins.*
import com.example.repository.HeroRepository
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject

class ApplicationTest {
    private val heroRepository: HeroRepository by inject(HeroRepository::class.java)

    @Test
    fun `access root endpoint, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                assertEquals(
                    expected = "Welcome to Boruto API!",
                    actual = response.content
                )
            }
        }
    }

    @ExperimentalSerializationApi
    @Test
    fun `access all heroes endpoint, query all pages, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            val pages = 1..5
            val heroes = listOf(
                heroRepository.page1,
                heroRepository.page2,
                heroRepository.page3,
                heroRepository.page4,
                heroRepository.page5,
            )

            pages.forEach { page ->
                handleRequest(HttpMethod.Get, "/boruto/heroes?page=$page").apply {
                    assertEquals(
                        expected = HttpStatusCode.OK,
                        actual = response.status()
                    )

                    val expected = ApiResponse(
                        success = true,
                        message = "okay",
                        previousPage = calculatePage(page)["previousPage"],
                        nextPage = calculatePage(page)["nextPage"],
                        heroes = heroes[page - 1],
                    )
                    val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    assertEquals(
                        expected,
                        actual
                    )
                }
            }
        }
    }

    private fun calculatePage(page: Int): Map<String, Int?> {
        var previousPage: Int? = page
        var nextPage: Int? = page

        if (page in 1..4) {
            nextPage = nextPage?.plus(1)
        }

        if (page in 2..5) {
            previousPage = previousPage?.minus(1)
        }

        if (page == 1) {
            previousPage = null
        }

        if (page == 5) {
            nextPage = null
        }

        return mapOf("previousPage" to previousPage, "nextPage" to nextPage)
    }
}