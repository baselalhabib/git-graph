package com.baselalhabib.gitgraph.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baselalhabib.gitgraph.data.model.ContributionDay
import com.baselalhabib.gitgraph.data.model.ContributionLevel
import com.baselalhabib.gitgraph.ui.theme.ContributionDarkLevel0
import com.baselalhabib.gitgraph.ui.theme.ContributionDarkLevel1
import com.baselalhabib.gitgraph.ui.theme.ContributionDarkLevel2
import com.baselalhabib.gitgraph.ui.theme.ContributionDarkLevel3
import com.baselalhabib.gitgraph.ui.theme.ContributionDarkLevel4
import com.baselalhabib.gitgraph.ui.theme.ContributionLightLevel0
import com.baselalhabib.gitgraph.ui.theme.ContributionLightLevel1
import com.baselalhabib.gitgraph.ui.theme.ContributionLightLevel2
import com.baselalhabib.gitgraph.ui.theme.ContributionLightLevel3
import com.baselalhabib.gitgraph.ui.theme.ContributionLightLevel4
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ContributionGraph(
    contributions: List<ContributionDay>,
    modifier: Modifier = Modifier,
    cellSize: Dp = 12.dp,
    cellSpacing: Dp = 3.dp,
    weeksToShow: Int = 20,
    onDaySelected: ((ContributionDay) -> Unit)? = null
) {
    val darkTheme = isSystemInDarkTheme()
    val contributionMap = remember(contributions) {
        contributions.associateBy { it.date }
    }

    val today = remember { LocalDate.now() }
    val daysInWeek = 7

    // Build columns of dates (weeks) ending at today
    val gridWeeks = remember(today, weeksToShow) {
        val endDate = today
        val totalDays = weeksToShow * daysInWeek
        val startDate = endDate.minusDays((totalDays - 1).toLong())

        val weeks = mutableListOf<List<LocalDate>>()
        var currentWeek = mutableListOf<LocalDate>()

        var cursor = startDate
        while (!cursor.isAfter(endDate)) {
            currentWeek.add(cursor)
            if (currentWeek.size == daysInWeek) {
                weeks.add(currentWeek)
                currentWeek = mutableListOf()
            }
            cursor = cursor.plusDays(1)
        }
        if (currentWeek.isNotEmpty()) {
            weeks.add(currentWeek)
        }
        weeks
    }

    val scrollState = rememberScrollState()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            // Day Labels Column (Mon, Wed, Fri)
            Column(
                modifier = Modifier.padding(end = 6.dp),
                verticalArrangement = Arrangement.spacedBy(cellSpacing)
            ) {
                Spacer(modifier = Modifier.height(16.dp)) // Offset for Month Header
                val dayNames = listOf("Mon", "", "Wed", "", "Fri", "", "")
                dayNames.forEach { dayName ->
                    Box(
                        modifier = Modifier.size(width = 24.dp, height = cellSize),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = dayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Weeks Columns
            gridWeeks.forEachIndexed { weekIndex, weekDays ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(cellSpacing)
                ) {
                    // Month Label above first week of that month
                    val firstDayOfWeek = weekDays.firstOrNull()
                    val showMonth = firstDayOfWeek != null && (weekIndex == 0 || firstDayOfWeek.dayOfMonth <= 7)
                    Box(
                        modifier = Modifier.height(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        if (showMonth && firstDayOfWeek != null) {
                            Text(
                                text = firstDayOfWeek.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 7 days in this week column
                    weekDays.forEach { date ->
                        val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        val contribution = contributionMap[dateStr] ?: ContributionDay(
                            date = dateStr,
                            count = 0,
                            level = ContributionLevel.NONE
                        )
                        val cellColor = getContributionColor(contribution.level, darkTheme)

                        Box(
                            modifier = Modifier
                                .size(cellSize)
                                .clip(RoundedCornerShape(2.dp))
                                .background(cellColor)
                                .clickable(enabled = onDaySelected != null) {
                                    onDaySelected?.invoke(contribution)
                                }
                        )
                    }
                }
                Spacer(modifier = Modifier.width(cellSpacing))
            }
        }

        // Legend Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Less",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 4.dp)
            )

            ContributionLevel.entries.forEach { level ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(getContributionColor(level, darkTheme))
                )
                Spacer(modifier = Modifier.width(2.dp))
            }

            Text(
                text = "More",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun getContributionColor(level: ContributionLevel, darkTheme: Boolean): Color {
    return if (darkTheme) {
        when (level) {
            ContributionLevel.NONE -> ContributionDarkLevel0
            ContributionLevel.LIGHT -> ContributionDarkLevel1
            ContributionLevel.MEDIUM -> ContributionDarkLevel2
            ContributionLevel.HIGH -> ContributionDarkLevel3
            ContributionLevel.INTENSE -> ContributionDarkLevel4
        }
    } else {
        when (level) {
            ContributionLevel.NONE -> ContributionLightLevel0
            ContributionLevel.LIGHT -> ContributionLightLevel1
            ContributionLevel.MEDIUM -> ContributionLightLevel2
            ContributionLevel.HIGH -> ContributionLightLevel3
            ContributionLevel.INTENSE -> ContributionLightLevel4
        }
    }
}
