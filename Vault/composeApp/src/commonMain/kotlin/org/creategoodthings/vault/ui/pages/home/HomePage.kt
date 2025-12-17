package org.creategoodthings.vault.ui.pages.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.domain.Storage
import org.creategoodthings.vault.domain.calculateDaysRemaining
import org.creategoodthings.vault.ui.components.AddProductDialog
import org.creategoodthings.vault.ui.components.AddProductFAB
import org.creategoodthings.vault.ui.components.AddStorageDialog
import org.creategoodthings.vault.ui.components.DragState
import org.creategoodthings.vault.ui.components.DraggableProductCard
import org.creategoodthings.vault.ui.components.DropZone
import org.creategoodthings.vault.ui.components.ProductCard
import org.creategoodthings.vault.ui.components.WelcomeDialog
import org.creategoodthings.vault.ui.navigation.PageNavigation
import org.creategoodthings.vault.ui.pages.PageShell
import org.creategoodthings.vault.ui.pages.home.StorageUIState.Loading
import org.creategoodthings.vault.ui.pages.home.StorageUIState.NoneSelected
import org.creategoodthings.vault.ui.pages.home.StorageUIState.Success
import org.creategoodthings.vault.ui.theme.MustardWarning
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.add_icon
import vault.composeapp.generated.resources.add_storage
import vault.composeapp.generated.resources.check_icon
import vault.composeapp.generated.resources.choose_storage
import vault.composeapp.generated.resources.dropdown_closed_icon
import vault.composeapp.generated.resources.dropdown_open_icon
import vault.composeapp.generated.resources.expires_next
import vault.composeapp.generated.resources.ok
import vault.composeapp.generated.resources.products
import vault.composeapp.generated.resources.settings
import vault.composeapp.generated.resources.settings_icon
import vault.composeapp.generated.resources.trashcan_icon
import vault.composeapp.generated.resources.welcome
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: HomePageViewModel
) {
    val user = remember { "Matthias" }
    val dataState by viewModel.uiState.collectAsState()

    //DRAG STATE
    var dragState by remember { mutableStateOf(DragState()) }
    val dropZones = remember { mutableStateMapOf<String, DropZone>() }
    var hoveredContainerID by remember { mutableStateOf<String?>(null) }
    val density = LocalDensity.current
    LaunchedEffect(dragState.dragOffset, dragState.isDragging) {
        hoveredContainerID = if (dragState.isDragging) {
            val cardCenterX = dragState.dragOffset.x + (dragState.itemSize.width / 2f)
            val cardCenterY = dragState.dragOffset.y + (dragState.itemSize.height / 2f)
            val cardCenter = Offset(cardCenterX, cardCenterY)

            //Check trashcan collision first
            val trashZone = dropZones["trashcan"]
            val distToTrash = if (trashZone != null) {
                (trashZone.center - cardCenter).getDistance()
            } else {
                Float.MAX_VALUE
            }
            val acceptedRadius = with(density) { 75.dp.toPx() }
            if (distToTrash < acceptedRadius) "trashcan"
            else {
                dropZones.values
                    .filter { it.zoneID != "trashcan" }
                    .firstOrNull { zone ->
                        zone.bounds.contains(cardCenter)
                    }?.zoneID
            }
        } else {
            null
        }
    }

    when (val dataState = dataState) {
        is DataUIState.Loading -> {
            PageShell { padding ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(72.dp)
                    )
                }
            }
        }
        is DataUIState.Ready -> {
            val products = dataState.products
            val storage2Containers = dataState.storages
            val selectedStorage = dataState.selectedStorage
            var showAddProductDialog by remember { mutableStateOf(false) }

            //region PAGESHELL UI
            PageShell(
                floatingActionButton = {
                    AddProductFAB(
                        onClick = { showAddProductDialog = true }
                    )
                },
                modifier = modifier,
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        contentPadding = padding,
                    ) {
                        when (selectedStorage) {
                            Loading -> {
                                item {
                                    CircularProgressIndicator()
                                }
                            }
                            NoneSelected -> {
                                navController.navigate(PageNavigation.Suggestions)
                            }
                            is Success -> {
                                //region SUCCESS
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
                                        uiState = selectedStorage,
                                        storages = storage2Containers.keys.toList(),
                                        onStorageChosen = { viewModel.changeStorage(it) },
                                        onStorageAdded = {
                                            viewModel.addStorage(it, true)
                                        },
                                        modifier = Modifier
                                            .clickable {
                                                navController.navigate(
                                                    PageNavigation.Storage(
                                                        selectedStorage.data.storage.ID
                                                    )
                                                )
                                            }
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
                                    val isBeingDragged = dragState.draggedProduct?.ID == product.ID
                                    var draggedItemPositionInRoot by remember { mutableStateOf(Offset.Zero) }
                                    var draggedItemSize by remember { mutableStateOf(IntSize.Zero) }

                                    Box(
                                        modifier = Modifier
                                            .animateItem()
                                            .alpha(if (isBeingDragged) 0f else 1f)
                                            .onGloballyPositioned { coords ->
                                                draggedItemPositionInRoot = coords.positionInRoot()
                                                draggedItemSize = coords.size
                                            }
                                    ) {
                                        DraggableProductCard(
                                            product = product,
                                            onDragStart = {
                                                dragState = DragState(
                                                    draggedProduct = product,
                                                    dragOffset = draggedItemPositionInRoot,
                                                    itemSize = draggedItemSize,
                                                    isDragging = true
                                                )
                                            },
                                            onDrag = { change ->
                                                dragState =
                                                    dragState.copy(dragOffset = dragState.dragOffset + change)
                                            },
                                            onDragEnd = {
                                                hoveredContainerID?.let { containerID ->
                                                    dragState.draggedProduct?.let { product ->
                                                        if (containerID == "trashcan") {
                                                            viewModel.deleteProduct(product)
                                                        }
                                                    }
                                                }
                                                dragState = DragState()
                                            },
                                            modifier = Modifier
                                                .clickable {
                                                    navController.navigate(
                                                        PageNavigation.Storage(
                                                            product.storageID
                                                        )
                                                    )
                                                }
                                        )
                                    }
                                }
                                //endregion
                                //endregion
                            }
                        }
                    }
                    //region DRAGGING
                    if (dragState.isDragging) {
                        //region PRODUCT CARD
                        dragState.draggedProduct?.let { product ->
                            val scale by animateFloatAsState(
                                targetValue = 1.05f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )

                            ProductCard(
                                product = product,
                                modifier = Modifier
                                    .offset {
                                        IntOffset(
                                            dragState.dragOffset.x.roundToInt(),
                                            dragState.dragOffset.y.roundToInt()
                                        )
                                    }
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                        rotationZ = 1f
                                    }
                                    .width(300.dp)
                                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                            )
                        }
                        //endregion

                        //region TRASHCAN UI
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 72.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .onGloballyPositioned { coords ->
                                        val positionInRoot = coords.positionInRoot()
                                        val size = coords.size
                                        val center = positionInRoot + Offset(size.width/2f, size.height/2f)
                                        dropZones["trashcan"] = DropZone(
                                            zoneID = "trashcan",
                                            bounds = Rect(
                                                offset = positionInRoot,
                                                size = Size(size.width.toFloat(), size.height.toFloat())
                                            ),
                                            center = center
                                        )
                                    }
                                    .size(96.dp)
                                    .zIndex(0.9f)
                            ) {
                                val targetScale = if (hoveredContainerID == "trashcan") 1.12f else 1f
                                val scaleAnimation by animateFloatAsState(targetScale)
                                Icon(
                                    imageVector = vectorResource(Res.drawable.trashcan_icon),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .fillMaxSize()
                                        .graphicsLayer {
                                            scaleX = scaleAnimation
                                            scaleY = scaleAnimation
                                        }
                                )
                            }
                        }
                        //endregion
                    }
                    //endregion
                }
            }
            //endregion

            //region DIALOGS
            if (showAddProductDialog) {
                AddProductDialog(
                    onClick = {
                        viewModel.addProduct(it)
                        showAddProductDialog = false
                    },
                    onDismiss = { showAddProductDialog = false },
                    storage2Containers = storage2Containers,
                    onAddStorage = { viewModel.addStorage(it) },
                    onAddContainer = { viewModel.addContainer(it) },
                    selectedStorage = (selectedStorage as? Success)?.data?.storage
                )
            }
            //endregion
        }
    }
}

@Composable
fun StorageStatusCard(
    uiState: StorageUIState,
    storages: List<Storage>,
    onStorageChosen: (Storage) -> Unit,
    onStorageAdded: (Storage) -> Unit,
    modifier: Modifier = Modifier
) {
    var chosenStorageName by remember { mutableStateOf("") }
    when (uiState) {
        Loading -> CircularProgressIndicator()
        NoneSelected -> { chosenStorageName = stringResource(Res.string.add_storage) }
        is Success -> { chosenStorageName = uiState.data.storage.name }
    }
    var chooseStorageExpanded by remember { mutableStateOf(false) }
    var showAddStorageDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column {
            //region CHOOSE STORAGE + TOTAL Legend
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(1f)
            ) {
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { if (uiState is Success) chooseStorageExpanded = !chooseStorageExpanded else showAddStorageDialog = true }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = chosenStorageName,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f, false)
                        )
                        Icon(
                            if (uiState is NoneSelected) vectorResource(Res.drawable.add_icon)
                            else if (chooseStorageExpanded) vectorResource(Res.drawable.dropdown_open_icon)
                            else vectorResource(Res.drawable.dropdown_closed_icon),
                            contentDescription = stringResource(Res.string.choose_storage),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    DropdownMenu(
                        expanded = chooseStorageExpanded,
                        onDismissRequest = { chooseStorageExpanded = false }
                    ) {
                        storages.forEach {
                            DropdownMenuItem(
                                text = { Text(it.name) },
                                onClick = {
                                    onStorageChosen(it)
                                    chooseStorageExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            //endregion

            //region STATUS CHART
            StatusChart(uiState = uiState)
            //endregion
        }
    }

    //region DIALOGS
    if (showAddStorageDialog) {
        AddStorageDialog(
            onConfirm = {
                onStorageAdded(it)
                showAddStorageDialog = false
            },
            onDismiss = {
                showAddStorageDialog = false
            }
        )
    }
    //endregion
}

@Composable
fun StatusChart(
    uiState: StorageUIState,
    modifier: Modifier = Modifier,
    chartThickness: Dp = 30.dp,
    animationDuration: Int = 1000
) {
    when (uiState) {
        Loading -> { CircularProgressIndicator() }
        NoneSelected -> {

        }
        is Success -> {
            val totalAmount = uiState.data.products.size

            val categoryCounts = uiState.data.products
                .groupingBy { getStatusLevelForItem(it) }
                .eachCount()

            val infos = categoryCounts.map { (category, count) ->
                val color = when (category) {
                    StatusInfo.Category.EXPIRED -> MaterialTheme.colorScheme.error
                    StatusInfo.Category.SOON -> MaterialTheme.colorScheme.secondary
                    StatusInfo.Category.WARNING -> MustardWarning
                    StatusInfo.Category.FRESH -> MaterialTheme.colorScheme.tertiary
                }

                StatusInfo(
                    category = category,
                    color = color,
                    amount = count
                )
            }

            val animationProgress = remember { Animatable(0f) }
            LaunchedEffect(uiState) {
                animationProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = animationDuration,
                        easing = FastOutSlowInEasing
                    )
                )
            }

            val chartData = remember(infos, totalAmount) {
                var currentStartAngle = -90f //Starts at 12
                infos.map { info ->
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
                        val centerItem: StatusInfo? =
                            infos.find { it.category == StatusInfo.Category.EXPIRED }
                            ?: infos.find { it.category == StatusInfo.Category.SOON }
                            ?: infos.find { it.category == StatusInfo.Category.WARNING }


                        if (centerItem != null) {
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
                        } else {
                            Icon(
                                imageVector = vectorResource(Res.drawable.check_icon),
                                contentDescription = stringResource(Res.string.ok),
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier
                                    .size(144.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    infos.forEach {
                        ChartLegend(
                            color = it.color,
                            label = it.category.toString(),
                            subLabel = "${it.amount} " + stringResource(Res.string.products)
                        )
                    }
                }
            }
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

data class StatusInfo(
    val category: Category,
    val color: Color,
    val amount: Int
) {
    enum class Category {
        EXPIRED,
        SOON,
        WARNING,
        FRESH;

        override fun toString(): String {
            return super.toString().lowercase().capitalize(Locale.current)
        }
    }
}

data class ChartSlice(
    val info: StatusInfo,
    val startAngle: Float,
    val sweepAngle: Float
)

fun getStatusLevelForItem(product: Product): StatusInfo.Category {
    //TODO Add ability for user to choose the logic for when stuff should count as Expired, Soon and the others.
    val daysRemaining = product.calculateDaysRemaining()
    return when {
        daysRemaining < 0 -> StatusInfo.Category.EXPIRED
        daysRemaining < 30 -> StatusInfo.Category.SOON
        daysRemaining < 365 -> StatusInfo.Category.WARNING
        else -> StatusInfo.Category.FRESH
    }
}


