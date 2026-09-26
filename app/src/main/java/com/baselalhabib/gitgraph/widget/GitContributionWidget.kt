package com.baselalhabib.gitgraph.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.baselalhabib.gitgraph.MainActivity
import com.baselalhabib.gitgraph.data.model.ContributionLevel

class GitContributionWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent(context)
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun WidgetContent(context: Context) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF0D1117))
                .padding(12.dp)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GitGraph",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFFFFFFF)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Render 16 weeks of placeholder/cached cells for Widget
            val weeks = 16
            val daysPerWeek = 7

            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                repeat(weeks) { weekIdx ->
                    Column {
                        repeat(daysPerWeek) { dayIdx ->
                            val level = when ((weekIdx + dayIdx) % 5) {
                                1 -> ContributionLevel.LIGHT
                                2 -> ContributionLevel.MEDIUM
                                3 -> ContributionLevel.HIGH
                                4 -> ContributionLevel.INTENSE
                                else -> ContributionLevel.NONE
                            }
                            Box(
                                modifier = GlanceModifier
                                    .size(10.dp)
                                    .padding(1.dp)
                                    .background(getWidgetColor(level))
                            ) {}
                        }
                    }
                    Spacer(modifier = GlanceModifier.width(2.dp))
                }
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            Text(
                text = "Tap to open app",
                style = TextStyle(
                    color = ColorProvider(Color(0xFF8B949E)),
                    fontSize = 10.sp
                )
            )
        }
    }

    private fun getWidgetColor(level: ContributionLevel): Color {
        return when (level) {
            ContributionLevel.NONE -> Color(0xFF161B22)
            ContributionLevel.LIGHT -> Color(0xFF0E4429)
            ContributionLevel.MEDIUM -> Color(0xFF006D32)
            ContributionLevel.HIGH -> Color(0xFF26A641)
            ContributionLevel.INTENSE -> Color(0xFF39D353)
        }
    }
}
