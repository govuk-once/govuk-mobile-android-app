package uk.gov.govuk.search.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import retrofit2.HttpException
import retrofit2.Response
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.Error
import uk.gov.govuk.data.model.Result.ServiceNotResponding
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.search.data.local.SearchLocalDataSource
import uk.gov.govuk.search.data.local.model.LocalSearchItem
import uk.gov.govuk.search.data.remote.AutocompleteApi
import uk.gov.govuk.search.data.remote.SearchApi
import uk.gov.govuk.search.data.remote.model.AutocompleteResponse
import uk.gov.govuk.search.data.remote.model.SearchResponse
import uk.gov.govuk.search.data.remote.model.SearchResult
import uk.gov.govuk.search.domain.SearchConfig
import java.net.UnknownHostException

@RunWith(Enclosed::class)
class SearchRepoTest {
    class SearchTest {
        private val searchApi = mockk<SearchApi>(relaxed = true)
        private val autocompleteApi = mockk<AutocompleteApi>(relaxed = true)
        private val localDataSource = mockk<SearchLocalDataSource>(relaxed = true)
        private val searchTerm = "search term"
        private val response = mockk<Response<SearchResponse>>()
        private val responseWithNoSearchResults = SearchResponse(total = 0, results = emptyList())
        private val responseWithOneSearchResult = SearchResponse(
            total = 1,
            results = listOf(
                SearchResult(
                    contentId = "contentId",
                    title = "title",
                    description = "description",
                    link = "link"
                )
            )
        )
        private lateinit var searchRepo: SearchRepo

        @Before
        fun setup() {
            searchRepo = SearchRepo(searchApi, autocompleteApi, localDataSource)
        }

        @Test
        fun `Previous searches returns previous searches`() {
            every { localDataSource.previousSearches } returns flowOf(
                listOf(
                    LocalSearchItem().apply { searchTerm = "dog" },
                    LocalSearchItem().apply { searchTerm = "cat" },
                    LocalSearchItem().apply { searchTerm = "tax" }
                )
            )

            val expected = listOf("dog", "cat", "tax")

            searchRepo = SearchRepo(searchApi, autocompleteApi, localDataSource)

            runTest {
                val actual = searchRepo.previousSearches.first()
                assertEquals(expected, actual)
            }
        }

        @Test
        fun `Remove previous search updates local data source`() {
            runTest {
                searchRepo.removePreviousSearch("searchTerm")
            }

            coVerify {
                localDataSource.removePreviousSearch("searchTerm")
            }
        }

        @Test
        fun `Remove all previous searches updates local data source`() {
            runTest {
                searchRepo.removeAllPreviousSearches()
            }

            coVerify {
                localDataSource.removeAllPreviousSearches()
            }
        }

        @Test
        fun `Perform search returns Success status when results are found`() {
            coEvery {
                searchApi.getSearchResults(
                    searchTerm,
                    SearchConfig.DEFAULT_RESULTS_PER_PAGE
                )
            } returns response
            every {  response.isSuccessful } returns true
            every { response.body() } returns responseWithOneSearchResult

            val expected = Success(responseWithOneSearchResult)

            runTest {
                val actual = searchRepo.performSearch(searchTerm, 10)
                assertEquals(expected, actual)
            }
        }

        @Test
        fun `Perform search returns Empty status when no results are found`() {
            coEvery {
                searchApi.getSearchResults(
                    searchTerm,
                    SearchConfig.DEFAULT_RESULTS_PER_PAGE
                )
            } returns response
            every {  response.isSuccessful } returns true
            every { response.body() } returns responseWithNoSearchResults

            val expected = Success(responseWithNoSearchResults)

            runTest {
                val actual = searchRepo.performSearch(searchTerm, 10)
                assertEquals(expected, actual)
            }
        }

        @Test
        fun `Perform search returns DeviceOffline status when the device is offline`() {
            coEvery {
                searchApi.getSearchResults(
                    searchTerm,
                    SearchConfig.DEFAULT_RESULTS_PER_PAGE
                )
            } throws UnknownHostException()

            runTest {
                val result = searchRepo.performSearch(searchTerm, 10)
                assertTrue(result is DeviceOffline)
            }
        }

        @Test
        fun `Perform search returns ServiceNotResponding status when the Search API is offline`() {
            val httpException = mockk<HttpException>(relaxed = true)
            coEvery { httpException.code() } returns 503
            coEvery { httpException.message() } returns "Service Unavailable"
            coEvery {
                searchApi.getSearchResults(
                    searchTerm,
                    SearchConfig.DEFAULT_RESULTS_PER_PAGE
                )
            } throws httpException

            runTest {
                val result = searchRepo.performSearch(searchTerm, 10)
                assertTrue(result is ServiceNotResponding)
            }
        }

        @Test
        fun `Perform search returns Error status when any unknown error occurs that has a message`() {
            val message = "Something very bad happened"
            coEvery {
                searchApi.getSearchResults(
                    searchTerm,
                    SearchConfig.DEFAULT_RESULTS_PER_PAGE
                )
            } throws Exception(message)

            runTest {
                val result = searchRepo.performSearch(searchTerm, 10)
                assertTrue(result is Error)
            }
        }

        @Test
        fun `Perform search returns Error status when any unknown error occurs that has no message`() {
            coEvery {
                searchApi.getSearchResults(
                    searchTerm,
                    SearchConfig.DEFAULT_RESULTS_PER_PAGE
                )
            } throws Exception()

            runTest {
                val result = searchRepo.performSearch(searchTerm, 10)
                assertTrue(result is Error)
            }
        }
    }

    class AutocompleteTest {
        private val searchApi = mockk<SearchApi>(relaxed = true)
        private val autocompleteApi = mockk<AutocompleteApi>(relaxed = true)
        private val localDataSource = mockk<SearchLocalDataSource>(relaxed = true)
        private val searchTerm = "house"
        private val response = mockk<Response<AutocompleteResponse>>(relaxed = true)
        private val responseWithNoSuggestions = AutocompleteResponse(suggestions = emptyList())
        private val responseWithOneSuggestion =
            AutocompleteResponse(suggestions = listOf("companies house"))
        private lateinit var searchRepo: SearchRepo

        @Before
        fun setup() {
            searchRepo = SearchRepo(searchApi, autocompleteApi, localDataSource)
        }

        @Test
        fun `Perform autocomplete returns Success status when suggestions are found`() {
            coEvery { autocompleteApi.getSuggestions(searchTerm) } returns response
            every { response.isSuccessful } returns true
            every { response.body() } returns responseWithOneSuggestion

            val expected = Success(responseWithOneSuggestion)

            runTest {
                val actual = searchRepo.performLookup(searchTerm)
                assertEquals(expected, actual)
            }
        }

        @Test
        fun `Perform autocomplete returns Empty status when no suggestions are found`() {
            coEvery { autocompleteApi.getSuggestions(searchTerm) } returns response
            every { response.isSuccessful } returns true
            every { response.body() } returns responseWithNoSuggestions

            val expected = Success(responseWithNoSuggestions)

            runTest {
                val actual = searchRepo.performLookup(searchTerm)
                assertEquals(expected, actual)
            }
        }

        @Test
        fun `Perform autocomplete returns DeviceOffline status when the device is offline`() {
            coEvery { autocompleteApi.getSuggestions(searchTerm) } throws UnknownHostException()

            runTest {
                val result = searchRepo.performLookup(searchTerm)
                assertTrue(result is DeviceOffline)
            }
        }

        @Test
        fun `Perform autocomplete returns ServiceNotResponding status when the Autocomplete API is offline`() {
            val httpException = mockk<HttpException>(relaxed = true)
            coEvery { httpException.code() } returns 503
            coEvery { httpException.message() } returns "Service Unavailable"
            coEvery { autocompleteApi.getSuggestions(searchTerm) } throws httpException

            runTest {
                val result = searchRepo.performLookup(searchTerm)
                assertTrue(result is ServiceNotResponding)
            }
        }

        @Test
        fun `Perform autocomplete returns Error status when any unknown error occurs that has a message`() {
            val message = "Something very bad happened"
            coEvery { autocompleteApi.getSuggestions(searchTerm) } throws Exception(message)

            runTest {
                val result = searchRepo.performLookup(searchTerm)
                assertTrue(result is Error)
            }
        }

        @Test
        fun `Perform autocomplete returns Error status when any unknown error occurs that has no message`() {
            coEvery { autocompleteApi.getSuggestions(searchTerm) } throws Exception()

            runTest {
                val result = searchRepo.performLookup(searchTerm)
                assertTrue(result is Error)
            }
        }

        @Test
        fun `Clear clears local data source`() {
            runTest {
                searchRepo.clear()
            }

            coVerify {
                localDataSource.clear()
            }
        }
    }
}
