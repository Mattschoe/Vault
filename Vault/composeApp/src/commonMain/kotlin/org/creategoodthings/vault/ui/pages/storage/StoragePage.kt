package org.creategoodthings.vault.ui.pages.storage

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import org.creategoodthings.vault.domain.Container
import org.creategoodthings.vault.domain.Product
import org.creategoodthings.vault.ui.components.AddProductDialog
import org.creategoodthings.vault.ui.components.AddProductFAB
import org.creategoodthings.vault.ui.components.DragState
import org.creategoodthings.vault.ui.components.DraggableProductCard
import org.creategoodthings.vault.ui.components.DropZone
import org.creategoodthings.vault.ui.components.ProductCard
import org.creategoodthings.vault.ui.pages.PageShell
import org.creategoodthings.vault.ui.pages.home.StorageUIState.*
import org.creategoodthings.vault.ui.pages.storage.ProductListData.Flat
import org.creategoodthings.vault.ui.pages.storage.ProductListData.Grouped
import org.creategoodthings.vault.ui.pages.storage.SortOption.ALPHABET
import org.creategoodthings.vault.ui.pages.storage.SortOption.BEST_BEFORE
import org.creategoodthings.vault.ui.pages.storage.SortOption.CONTAINER
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import vault.composeapp.generated.resources.Res
import vault.composeapp.generated.resources.alphabet_icon
import vault.composeapp.generated.resources.calendar_icon
import vault.composeapp.generated.resources.category_icon
import vault.composeapp.generated.resources.check_icon
import vault.composeapp.generated.resources.edit
import vault.composeapp.generated.resources.edit_icon
import vault.composeapp.generated.resources.place_item_icon
import vault.composeapp.generated.resources.sorted_alphabetically
import vault.composeapp.generated.resources.sorted_bb
import vault.composeapp.generated.resources.sorted_containers
import vault.composeapp.generated.resources.trashcan_icon
import vault.composeapp.generated.resources.unorganized
import kotlin.math.roundToInt

@Composable
fun StoragePage(
    navController: NavController,
    viewModel:  StoragePageViewModel,
    modifier: Modifier = Modifier,
) {
    var showAddProductDialog by remember { mutableStateOf(false) }
    val storage2Containers by viewModel.storages.collectAsState()
    val selectedStorage by viewModel.selectedStorage.collectAsState()

    val sortOption by viewModel.sortOption.collectAsState()
    val storageName by viewModel.storageName.collectAsState()
    val state by viewModel.products.collectAsState()

    var hasInitialFocus by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var editStorageName by remember { mutableStateOf(false) }
    var newStorageName by remember(storageName) { mutableStateOf(storageName) }

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

    PageShell(
        floatingActionButton = {
            AddProductFAB(
                onClick = { showAddProductDialog = true }
            )
        },
        modifier = modifier,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            //region LIST
            LazyColumn(
                contentPadding = padding,
            ) {
                //region TITEL + SORT ORDER
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (editStorageName) {
                                OutlinedTextField(
                                    value = newStorageName,
                                    onValueChange = { newStorageName = it },
                                    textStyle = MaterialTheme.typography.headlineLarge,
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            editStorageName = false
                                            hasInitialFocus = false
                                            viewModel.updateStorageName(newStorageName)
                                            focusManager.clearFocus()
                                        }
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = MaterialTheme.colorScheme.primary,
                                    ),
                                    trailingIcon = {
                                        IconButton(
                                            onClick = {
                                                editStorageName = false
                                                hasInitialFocus = false
                                                viewModel.updateStorageName(newStorageName)
                                                focusManager.clearFocus()
                                            }
                                        ) {
                                            Icon(
                                                imageVector = vectorResource(Res.drawable.check_icon),
                                                contentDescription = stringResource(Res.string.edit),
                                                modifier = Modifier
                                                    .size(48.dp)
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .focusRequester(focusRequester)
                                        .onFocusChanged { focusState ->
                                            if (focusState.isFocused) {
                                                hasInitialFocus = true
                                            } else if (hasInitialFocus) {
                                                editStorageName = false
                                                hasInitialFocus = false
                                                viewModel.updateStorageName(newStorageName)
                                            }
                                        }
                                )
                                LaunchedEffect(Unit) {
                                    delay(50)
                                    focusRequester.requestFocus()
                                }
                            } else {
                                Text(
                                    text = storageName,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.headlineLarge,
                                    modifier = Modifier
                                        .clickable {
                                            editStorageName = true
                                            hasInitialFocus = false
                                        }
                                )
                                IconButton(
                                    onClick = {
                                        editStorageName = true
                                        hasInitialFocus = false
                                    },
                                ) {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.edit_icon),
                                        contentDescription = stringResource(Res.string.edit),
                                    )
                                }
                            }
                        }
                        Icon(
                            imageVector = vectorResource(
                                when (sortOption) {
                                    ALPHABET -> Res.drawable.alphabet_icon
                                    BEST_BEFORE -> Res.drawable.calendar_icon
                                    CONTAINER -> Res.drawable.category_icon
                                }
                            ),
                            contentDescription = stringResource(
                                when (sortOption) {
                                    ALPHABET -> Res.string.sorted_alphabetically
                                    BEST_BEFORE -> Res.string.sorted_bb
                                    CONTAINER -> Res.string.sorted_containers
                                }
                            ),
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { viewModel.toggleBetweenSortOption() }
                        )
                    }
                }
                //endregion

                item {
                    Spacer(Modifier.height(24.dp))
                }

                when (val state = state) {
                    is Flat -> {
                        items(
                            items = state.products,
                            key = { it.ID }
                        ) { product ->
                            ProductCard(
                                product = product,
                                modifier = Modifier
                                    .animateItem()
                            )
                        }
                    }


                    is Grouped -> {
                        state.groups.forEach { (container, products) ->

                            item(key = container.ID) {
                                DroppableContainerSection(
                                    isHovered = hoveredContainerID == container.ID && hoveredContainerID != dragState.draggedProduct?.containerID,
                                    isDragging = dragState.isDragging,
                                    onBoundsChanged = { bounds ->
                                        dropZones[container.ID] = DropZone(
                                            zoneID = container.ID,
                                            container = container,
                                            bounds = bounds
                                        )
                                    },
                                    label = container.name
                                )
                            }
                            items(
                                items = products,
                                key = { it.ID }
                            ) { product ->
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
                                        onDrag = { dragState = dragState.copy(dragOffset = dragState.dragOffset + it) },
                                        onDragEnd = {
                                            hoveredContainerID?.let { containerID ->
                                                val dropZone = dropZones[containerID]
                                                dragState.draggedProduct?.let { product ->
                                                    if (containerID == "trashcan") {
                                                        viewModel.deleteProduct(product)
                                                    } else {
                                                        viewModel.changeProductContainer(
                                                            product,
                                                            dropZone?.container
                                                        )
                                                    }
                                                }
                                            }
                                            dragState = DragState()
                                        }
                                    )
                                }
                            }
                            item {
                                Spacer(Modifier.height(24.dp))
                            }
                        }

                        if (state.unorganizedProducts.isNotEmpty()) {
                            item(key = "unorganized_container") {
                                DroppableContainerSection(
                                    isHovered = hoveredContainerID == "unorganized"  && hoveredContainerID != dragState.draggedProduct?.containerID,
                                    isDragging = dragState.isDragging,
                                    onBoundsChanged = { bounds ->
                                        dropZones["unorganized"] = DropZone(
                                            zoneID = "unorganized",
                                            container = null,
                                            bounds = bounds
                                        )
                                    },
                                    label = stringResource(Res.string.unorganized)
                                )
                            }

                            items(
                                items = state.unorganizedProducts,
                                key = { it.ID }
                            ) { product ->
                                val isBeingDragged = dragState.draggedProduct?.ID == product.ID
                                var myItemPosition by remember { mutableStateOf(Offset.Zero) }
                                var draggedItemSize by remember { mutableStateOf(IntSize.Zero) }

                                Box(
                                    modifier = Modifier
                                        .animateItem()
                                        .alpha(if (isBeingDragged) 0f else 1f)
                                        .onGloballyPositioned { coords ->
                                            myItemPosition = coords.positionInRoot()
                                            draggedItemSize = coords.size
                                        }
                                ) {
                                    DraggableProductCard(
                                        product = product,
                                        onDragStart = {
                                            dragState = DragState(
                                                draggedProduct = product,
                                                dragOffset = myItemPosition,
                                                itemSize = draggedItemSize,
                                                isDragging = true
                                            )
                                        },
                                        onDrag = { change ->
                                            dragState = dragState.copy(dragOffset = dragState.dragOffset + change)
                                        },
                                        onDragEnd = {
                                            hoveredContainerID?.let { containerID ->
                                                val dropZone = dropZones[containerID]
                                                dragState.draggedProduct?.let { product ->
                                                    if (containerID == "trashcan") {
                                                        viewModel.deleteProduct(product)
                                                    } else {
                                                        viewModel.changeProductContainer(
                                                            product,
                                                            dropZone?.container
                                                        )
                                                    }
                                                }
                                            }
                                            dragState = DragState()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            //endregion
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

    //region DIALOGS
    if (showAddProductDialog && selectedStorage is Success) {
        AddProductDialog(
            onClick = {
                viewModel.addProduct(it)
                showAddProductDialog = false
            },
            onDismiss = { showAddProductDialog = false },
            storage2Containers = storage2Containers,
            onAddStorage = { viewModel.addStorage(it) },
            onAddContainer = { viewModel.addContainer(it) },
            selectedStorage = (selectedStorage as Success).data.storage
        )
    }
    //endregion
}



@Composable
fun DroppableContainerSection(
    isHovered: Boolean,
    isDragging: Boolean,
    onBoundsChanged: (Rect) -> Unit,
    modifier: Modifier = Modifier,
    label: String
) {
    val outlineShape = remember { RoundedCornerShape(24.dp) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coords ->
                val positionInRoot = coords.positionInRoot()
                val size = coords.size
                onBoundsChanged(
                    Rect(
                        offset = positionInRoot,
                        size = Size(size.width.toFloat(), size.height.toFloat())
                    )
                )
            }
            .background(
                color = if (isHovered && isDragging) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else Color.Transparent,
                shape = outlineShape
            )
            .border(
                width = 2.dp,
                color = if (isHovered && isDragging) MaterialTheme.colorScheme.primary
                        else Color.Transparent,
                shape = outlineShape
            )
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                color = if (isHovered && isDragging) MaterialTheme.colorScheme.primary
                        else Color.Gray,
                fontWeight = if (isHovered && isDragging) FontWeight.ExtraBold
                             else FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )

            if (isHovered && isDragging) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = vectorResource(Res.drawable.place_item_icon),
                    contentDescription = null, //TODO: Change to "Drop here"
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(20.dp)
                        .alpha(if (isHovered && isDragging) 1f else 0f)
                )
            }
        }
    }
}