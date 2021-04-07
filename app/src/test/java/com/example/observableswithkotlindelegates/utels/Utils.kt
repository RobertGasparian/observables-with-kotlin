package com.example.observableswithkotlindelegates.utels

const val ID = "id"

class TestDummy(
    private val testId: String
) {
    override fun equals(other: Any?): Boolean {
        return (other as? TestDummy)?.let { it.testId == testId } == true
    }

    override fun hashCode(): Int {
        return testId.hashCode()
    }
}

fun getTestDummy(id: String? = null) = TestDummy(id ?: ID)