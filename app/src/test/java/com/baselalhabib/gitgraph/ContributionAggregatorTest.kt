package com.baselalhabib.gitgraph

import com.baselalhabib.gitgraph.data.model.ContributionDay
import com.baselalhabib.gitgraph.data.model.ContributionLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class ContributionAggregatorTest {

    @Test
    fun `test contribution level threshold mapping`() {
        assertEquals(ContributionLevel.NONE, ContributionLevel.fromCount(0))
        assertEquals(ContributionLevel.NONE, ContributionLevel.fromCount(-5))
        assertEquals(ContributionLevel.LIGHT, ContributionLevel.fromCount(1))
        assertEquals(ContributionLevel.LIGHT, ContributionLevel.fromCount(2))
        assertEquals(ContributionLevel.MEDIUM, ContributionLevel.fromCount(3))
        assertEquals(ContributionLevel.MEDIUM, ContributionLevel.fromCount(5))
        assertEquals(ContributionLevel.HIGH, ContributionLevel.fromCount(6))
        assertEquals(ContributionLevel.HIGH, ContributionLevel.fromCount(9))
        assertEquals(ContributionLevel.INTENSE, ContributionLevel.fromCount(10))
        assertEquals(ContributionLevel.INTENSE, ContributionLevel.fromCount(100))
    }

    @Test
    fun `test contribution day model defaults level based on count`() {
        val day0 = ContributionDay(date = "2026-03-01", count = 0)
        assertEquals(ContributionLevel.NONE, day0.level)

        val day4 = ContributionDay(date = "2026-03-02", count = 4)
        assertEquals(ContributionLevel.MEDIUM, day4.level)

        val day12 = ContributionDay(date = "2026-03-03", count = 12)
        assertEquals(ContributionLevel.INTENSE, day12.level)
    }

    @Test
    fun `test aggregating list of daily counts`() {
        val dailyCounts = listOf(
            "2026-03-01" to 2,
            "2026-03-01" to 3,
            "2026-03-02" to 1
        )

        val aggregated = dailyCounts
            .groupBy { it.first }
            .mapValues { entry -> entry.value.sumOf { it.second } }

        assertEquals(5, aggregated["2026-03-01"])
        assertEquals(1, aggregated["2026-03-02"])
    }
}
