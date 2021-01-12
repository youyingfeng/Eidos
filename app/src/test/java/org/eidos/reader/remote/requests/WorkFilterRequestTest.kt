package org.eidos.reader.remote.requests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WorkFilterRequestTest {
    companion object {
        private val NORMAL_FANDOM_NAME = "Marvel"
        private val TAG_WITH_SLASH_CHARACTER = "Pepper Potts/Tony Stark"
        private val TAG_WITH_AMPERSAND_CHARACTER = "Pepper Potts & Tony Stark"
    }
    // JVM tests
    @Test
    fun `URL should contain no additional arguments when no methods are called`() {
        val workFilterRequest = WorkFilterRequest("Marvel")
        assertEquals("/tags/Marvel/works?utf8=✓&commit=Sort+and+Filter", workFilterRequest.queryString)
    }

    @Test
    fun `Special characters in the main tag should be formatted correctly in the URL`() {
        var workFilterRequest = WorkFilterRequest("Pepper Potts/Tony Stark")
        assertEquals("/tags/Pepper Potts*s*Tony Stark/works?utf8=✓&commit=Sort+and+Filter",
                     workFilterRequest.queryString)

        workFilterRequest = WorkFilterRequest("Pepper Potts & Tony Stark")
        assertEquals("/tags/Pepper Potts *a* Tony Stark/works?utf8=✓&commit=Sort+and+Filter",
                     workFilterRequest.queryString)
    }

    @Test
    fun `Special characters in included and excluded tags should be formatted correctly in the URL`() {
        /* Included tags*/
        var workFilterRequest = WorkFilterRequest("Marvel")
            .includeTags(TAG_WITH_SLASH_CHARACTER)
        assertEquals("/tags/Marvel/works?utf8=✓&commit=Sort+and+Filter&work_search[other_tag_names]=Pepper+Potts%2FTony+Stark",
                     workFilterRequest.queryString)

        workFilterRequest = WorkFilterRequest(NORMAL_FANDOM_NAME)
            .includeTags(TAG_WITH_AMPERSAND_CHARACTER)
        assertEquals("/tags/Marvel/works?utf8=✓&commit=Sort+and+Filter&work_search[other_tag_names]=Pepper+Potts+%26+Tony+Stark",
                     workFilterRequest.queryString)

        workFilterRequest = WorkFilterRequest(NORMAL_FANDOM_NAME)
                .excludeTags(TAG_WITH_SLASH_CHARACTER)
        assertEquals("/tags/Marvel/works?utf8=✓&commit=Sort+and+Filter&work_search[excluded_tag_names]=Pepper+Potts%2FTony+Stark",
                workFilterRequest.queryString)

        workFilterRequest = WorkFilterRequest(NORMAL_FANDOM_NAME)
                .excludeTags(TAG_WITH_AMPERSAND_CHARACTER)
        assertEquals("/tags/Marvel/works?utf8=✓&commit=Sort+and+Filter&work_search[excluded_tag_names]=Pepper+Potts+%26+Tony+Stark",
                workFilterRequest.queryString)

    }

    @Test
    fun `Multiple included and excluded tags should be represented correctly in the URL`() {
        // TODO
    }

    @Test
    fun `Main tag should not be blank`() {
        // TODO: add in code to throw an error if main tag is blank
    }

    @Test
    fun `Statistic ranges should be represented properly`() {
        // TODO: Test each of the ranges
    }
}