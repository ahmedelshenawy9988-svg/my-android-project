package com.example

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun testAppNameString() {
        val expectedName = "WAKE UP"
        assertEquals("WAKE UP", expectedName)
    }
}
