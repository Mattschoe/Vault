package org.creategoodthings.vault.ui.pages

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.datetime.LocalDate
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.ui.components.ProductCard
import org.creategoodthings.vault.ui.navigation.PageNavigation
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.choose_storage
import vault.composeapp.generated.resources.dropdown_closed_icon
import vault.composeapp.generated.resources.expires_next
import vault.composeapp.generated.resources.products
import vault.composeapp.generated.resources.settings
import vault.composeapp.generated.resources.settings_icon
import vault.composeapp.generated.resources.total
import vault.composeapp.generated.resources.welcome
import kotlin.math.min

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val user = remember { "Matthias" }
    val products = listOf(
        Product("Mælk", LocalDate(2025, 11, 30), 5),
        Product("Mælk", LocalDate(2025, 11, 30), 5),
        Product("Mælk", LocalDate(2025, 11, 30), 5),
        Product("Mælk", LocalDate(2025, 11, 30), 5),
        Product("Mælk", LocalDate(2025, 11, 30), 5),
        Product("Mælk", LocalDate(2025, 11, 30), 5),
        Product("Mælk", LocalDate(2025, 11, 30), 5),
        Product("Mælk", LocalDate(2025, 11, 30), 5),
        Product("Mælk", LocalDate(2025, 11, 30), 5),
        Product("Mælk", LocalDate(2025, 11, 30), 5),
    )

    PageShell(
        modifier = modifier,
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            //region TITEL
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(Res.string.welcome) + ",",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            text = user,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }

                    Icon(
                        imageVector = vectorResource(Res.drawable.settings_icon),
                        contentDescription = stringResource(Res.string.settings),
                        modifier = Modifier
                            .clickable { navController.navigate(PageNavigation.Settings) }
                    )
                }
            }
            //endregion

            //region STATUS
            item {
                StorageStatusCard(
                    "Skab under trappe",
                )
            }
            //endregion

            //region EXPIRES SOON
            item {
                Text(
                    text = stringResource(Res.string.expires_next),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(top = 12.dp)
                )
            }
            items(products) { product ->
                ProductCard(product)
            }
            //endregion
        }
    }
}

@Composable
fun StorageStatusCard(
    selectedStorage: String,
    modifier: Modifier = Modifier
) {
    var chooseStorageExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(

        ) {
            //region CHOOSE STORAGE
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(0.5f)
            ) {
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { chooseStorageExpanded = true }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = selectedStorage,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f, false)
                        )
                        Icon(
                            vectorResource(Res.drawable.dropdown_closed_icon),
                            contentDescription = stringResource(Res.string.choose_storage),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
            //endregion

            //region STATUS CHART
            val infos = listOf(
                StatusInfo(StatusInfo.Category.SOON, MaterialTheme.colorScheme.secondary, 5),
                StatusInfo(StatusInfo.Category.EXPIRED, Color.Red, 2),
                StatusInfo(StatusInfo.Category.FRESH, MaterialTheme.colorScheme.tertiary, 12),
            )
            StatusChart(
                data = infos,
                totalAmount = 19,
                centerItem = infos[2],
            )
            //endregion
        }
    }
}

data class StatusInfo(
    val category: Category,
    val color: Color,
    val amount: Int
) {
    enum class Category {
        EXPIRED,
        SOON,
        FRESH
    }
}

data class ChartSlice(
    val info: StatusInfo,
    val startAngle: Float,
    val sweepAngle: Float
)

@Composable
fun StatusChart(
    data: List<StatusInfo>,
    totalAmount: Int,
    centerItem: StatusInfo,
    modifier: Modifier = Modifier,
    chartThickness: Dp = 30.dp,
    animationDuration: Int = 1000
) {
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            )
        )
    }

    val chartData = remember(data, totalAmount) {
        var currentStartAngle = -90f //Starts at 12
        data.map { info ->
            val sweepAngle = if (totalAmount > 0) {
                (info.amount.toFloat() / totalAmount.toFloat()) * 360f
            } else 0f

            val slice = ChartSlice(
                info = info,
                startAngle = currentStartAngle,
                sweepAngle = sweepAngle
            )
            currentStartAngle += sweepAngle
            slice
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
        ) {
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                val currentMaxAngle = 360f * animationProgress.value //Amount of degrees visible based on animationprogress
                chartData.forEach { slice ->
                    val sliceStartRelative = slice.startAngle + 90f
                    //Only draws when animation curser has reached slice
                    if (currentMaxAngle > sliceStartRelative) {
                        val visibleSweep = min(
                            slice.sweepAngle,
                            currentMaxAngle - sliceStartRelative
                        )

                        val strokeWidthPx = chartThickness.toPx()
                        val halfStroke = strokeWidthPx / 2f

                        drawArc(
                            color = slice.info.color,
                            startAngle = slice.startAngle,
                            sweepAngle = visibleSweep,
                            useCenter = false,
                            topLeft = Offset(halfStroke, halfStroke),
                            size = Size(
                                width = size.width - strokeWidthPx,
                                height = size.height - strokeWidthPx
                            ),
                            style = Stroke(width = chartThickness.toPx(), cap = StrokeCap.Butt)
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = centerItem.amount.toString(),
                    style = MaterialTheme.typography.displayMedium,
                    color = centerItem.color,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = centerItem.category.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach {
                ChartLegend(
                    color = it.color,
                    label = it.category.toString(),
                    subLabel = "${it.amount} " + stringResource(Res.string.products)
                )
            }

            ChartLegend(
                color = MaterialTheme.colorScheme.primary,
                label = stringResource(Res.string.total),
                subLabel = "$totalAmount " + stringResource(Res.string.products)
            )
        }
    }
}

@Composable
fun ChartLegend(
    color: Color,
    label: String,
    subLabel: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = subLabel,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

