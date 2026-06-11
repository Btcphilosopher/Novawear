package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.UiChatMessage
import kotlinx.coroutines.flow.Flow

// --- Professional Polish Theme Colors ---
val ProfessionalPolishBg = Color(0xFFFDF8F6)        // bg-[#fdf8f6]
val ProfessionalPolishText = Color(0xFF201A18)      // text-[#201a18]
val ProfessionalPolishTerracotta = Color(0xFF9C4300)  // #9c4300
val ProfessionalPolishDeepTerracotta = Color(0xFF512400) // #512400
val ProfessionalPolishPeach = Color(0xFFFFDCC0)     // #ffdcc0
val ProfessionalPolishSoftRose = Color(0xFFF3DFD7)  // #f3dfd7

// --- Custom ASOS/SSENSE Noir Theme Elements mapped to Professional Polish ---
val BrandDarkBg = ProfessionalPolishText
val BrandMatedBlack = Color(0xFF1A1412)
val BoarderColor = ProfessionalPolishSoftRose
val GoldLogoColor = ProfessionalPolishTerracotta
val SoftGrayBg = ProfessionalPolishBg
val PrimaryBrandColor = ProfessionalPolishTerracotta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaWearApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val wishlistItems by viewModel.wishlistProducts.collectAsState()
    val profile by viewModel.styleProfile.collectAsState()

    var showQuizDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (currentScreen != "checkout" && currentScreen != "order_success" && currentScreen != "product_detail") {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "N O V A W E A R",
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Serif,
                            fontSize = 24.sp,
                            letterSpacing = 4.sp,
                            modifier = Modifier.testTag("brand_logo_title")
                        )
                    },
                    navigationIcon = {
                        if (currentScreen == "seller_portal" || currentScreen == "admin_panel" || currentScreen == "quiz") {
                            IconButton(onClick = { viewModel.navigateTo("me") }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.navigateTo("shop") }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.testTag("search_icon_btn"))
                        }
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(containerColor = ProfessionalPolishTerracotta, contentColor = Color.White) {
                                        Text(cartCount.toString())
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clickable { viewModel.navigateTo("checkout") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Bag",
                                modifier = Modifier.testTag("cart_bag_btn")
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = ProfessionalPolishBg,
                        titleContentColor = ProfessionalPolishText
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreen == "home" || currentScreen == "shop" || currentScreen == "stylist" || currentScreen == "me") {
                NavigationBar(
                    containerColor = ProfessionalPolishBg,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .border(BorderStroke(1.dp, ProfessionalPolishSoftRose.copy(alpha = 0.8f)))
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = currentScreen == "home",
                        onClick = { viewModel.navigateTo("home") },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Discover", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ProfessionalPolishDeepTerracotta,
                            selectedTextColor = ProfessionalPolishDeepTerracotta,
                            unselectedIconColor = ProfessionalPolishDeepTerracotta.copy(alpha = 0.6f),
                            unselectedTextColor = ProfessionalPolishDeepTerracotta.copy(alpha = 0.6f),
                            indicatorColor = ProfessionalPolishPeach
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )
                    NavigationBarItem(
                        selected = currentScreen == "shop",
                        onClick = { viewModel.navigateTo("shop") },
                        icon = { Icon(Icons.Default.List, contentDescription = "Shop") },
                        label = { Text("Shop", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ProfessionalPolishDeepTerracotta,
                            selectedTextColor = ProfessionalPolishDeepTerracotta,
                            unselectedIconColor = ProfessionalPolishDeepTerracotta.copy(alpha = 0.6f),
                            unselectedTextColor = ProfessionalPolishDeepTerracotta.copy(alpha = 0.6f),
                            indicatorColor = ProfessionalPolishPeach
                        ),
                        modifier = Modifier.testTag("nav_shop")
                    )
                    NavigationBarItem(
                        selected = currentScreen == "stylist",
                        onClick = { viewModel.navigateTo("stylist") },
                        icon = { Icon(Icons.Default.Star, contentDescription = "Nova Assistant", tint = if (currentScreen == "stylist") ProfessionalPolishTerracotta else Color.Gray) },
                        label = { Text("AI Stylist", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ProfessionalPolishDeepTerracotta,
                            selectedTextColor = ProfessionalPolishDeepTerracotta,
                            unselectedIconColor = ProfessionalPolishDeepTerracotta.copy(alpha = 0.6f),
                            unselectedTextColor = ProfessionalPolishDeepTerracotta.copy(alpha = 0.6f),
                            indicatorColor = ProfessionalPolishPeach
                        ),
                        modifier = Modifier.testTag("nav_stylist")
                    )
                    NavigationBarItem(
                        selected = currentScreen == "me" || currentScreen == "seller_portal" || currentScreen == "admin_panel",
                        onClick = { viewModel.navigateTo("me") },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Me") },
                        label = { Text("Me", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ProfessionalPolishDeepTerracotta,
                            selectedTextColor = ProfessionalPolishDeepTerracotta,
                            unselectedIconColor = ProfessionalPolishDeepTerracotta.copy(alpha = 0.6f),
                            unselectedTextColor = ProfessionalPolishDeepTerracotta.copy(alpha = 0.6f),
                            indicatorColor = ProfessionalPolishPeach
                        ),
                        modifier = Modifier.testTag("nav_me")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ProfessionalPolishBg)
        ) {
            when (currentScreen) {
                "home" -> HomeScreen(viewModel)
                "shop" -> ShopScreen(viewModel)
                "stylist" -> StylistScreen(viewModel)
                "me" -> MeScreen(viewModel, onStartQuiz = { showQuizDialog = true })
                "product_detail" -> ProductDetailScreen(viewModel)
                "checkout" -> CheckoutScreen(viewModel)
                "order_success" -> OrderSuccessScreen(viewModel)
                "seller_portal" -> SellerPortalScreen(viewModel)
                "admin_panel" -> AdminPanelScreen(viewModel)
            }
        }
    }

    if (showQuizDialog) {
        QuizOnboardingDialog(
            onDismiss = { showQuizDialog = false },
            onSave = { gender, styles, sizes, budget ->
                viewModel.saveStyleProfile(gender, styles, sizes, budget)
                showQuizDialog = false
            }
        )
    }
}

// ==================== 1. HOME & DISCOVERY ====================
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val products by viewModel.filteredProducts.collectAsState()
    val trendingProducts = products.filter { it.isTrending }
    val newArrivals = products.filter { it.isNewIn }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_layout")
    ) {
        // High Fashion Editorial Campaign Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(ProfessionalPolishDeepTerracotta)
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=800&q=80",
                    contentDescription = "Summer Campaign",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Overlay gradient for elite magazine look
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.65f), Color.Transparent),
                                startX = 0f
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(24.dp)
                ) {
                    Text(
                        text = "NEW IN: FALL '24",
                        color = ProfessionalPolishPeach,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Urban\nMinimalist",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 32.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.clearFilters()
                            viewModel.selectedCategory.value = "tops"
                            viewModel.navigateTo("shop")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ProfessionalPolishPeach, contentColor = ProfessionalPolishDeepTerracotta),
                        shape = RoundedCornerShape(18.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp)
                    ) {
                        Text("SHOP THE DROP", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
            }
        }

        // Section: Live Drop Promo ticker
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ProfessionalPolishText)
                    .padding(vertical = 10.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.Red, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LIVE DROP: LIMITED NUMBERS",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "EXTRA 15% WITH CODE: NOVA15",
                    color = ProfessionalPolishPeach,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Section: Trending Categories
        item {
            Column(modifier = Modifier.padding(top = 20.dp)) {
                Text(
                    text = "TRENDING CATEGORIES",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = ProfessionalPolishText,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    val categories = listOf(
                        "tops" to "Streetwear",
                        "trousers" to "Cargo & Denim",
                        "dresses" to "Editorial Dresses",
                        "shoes" to "Footwear Drop",
                        "accessories" to "Accessories"
                    )
                    items(categories) { pair ->
                        val catKey = pair.first
                        val catTitle = pair.second
                        val index = categories.indexOf(pair)
                        val containerBg = if (index % 2 == 0) ProfessionalPolishPeach else ProfessionalPolishSoftRose
                        Card(
                            modifier = Modifier
                                .width(135.dp)
                                .height(72.dp)
                                .padding(horizontal = 4.dp)
                                .clickable {
                                    viewModel.clearFilters()
                                    viewModel.selectedCategory.value = catKey
                                    viewModel.navigateTo("shop")
                                },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, ProfessionalPolishSoftRose),
                            colors = CardDefaults.cardColors(containerColor = containerBg)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = catTitle,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = ProfessionalPolishDeepTerracotta,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: "Trending Now" Scroll Row
        item {
            Column(modifier = Modifier.padding(top = 28.dp)) {
                Text(
                    text = "TRENDING NOW",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = ProfessionalPolishText,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                if (trendingProducts.isEmpty()) {
                    Text("No trending items currently", modifier = Modifier.padding(16.dp), color = Color.Gray)
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        items(trendingProducts) { product ->
                            ProductCardHorizontal(product = product, onClick = {
                                viewModel.navigateTo("product_detail", product.id)
                            })
                        }
                    }
                }
            }
        }

        // Section: "New In"
        item {
            Column(modifier = Modifier.padding(top = 24.dp, bottom = 40.dp)) {
                Text(
                    text = "NEW RELEASE DROPS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = ProfessionalPolishText,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
                if (newArrivals.isEmpty()) {
                    Text("No releases today", modifier = Modifier.padding(16.dp), color = Color.Gray)
                } else {
                    // Render in responsive vertical list style for editorial weight
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        newArrivals.forEach { product ->
                            NewArrivalItemRow(product = product, onClick = {
                                viewModel.navigateTo("product_detail", product.id)
                            })
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCardHorizontal(product: ProductEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(165.dp)
            .padding(horizontal = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ProfessionalPolishSoftRose.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Rating badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .background(ProfessionalPolishPeach.copy(alpha = 0.95f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = ProfessionalPolishDeepTerracotta, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(product.rating.toString(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ProfessionalPolishDeepTerracotta)
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.brand.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfessionalPolishText.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Text(
                    text = product.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = ProfessionalPolishText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${product.price}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ProfessionalPolishTerracotta
                    )
                    // Low stock warn
                    if (product.stock < 12) {
                        Text("LOW STOCK", color = Color.Red, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun NewArrivalItemRow(product: ProductEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(BorderStroke(1.dp, ProfessionalPolishSoftRose.copy(alpha = 0.5f)), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.title,
            modifier = Modifier
                .size(75.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.brand.uppercase(),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = ProfessionalPolishTerracotta,
                letterSpacing = 1.sp
            )
            Text(
                text = product.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ProfessionalPolishText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sustainability Eco Indicator
                if (product.sustainabilityScore > 90) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("ECO ${product.sustainabilityScore}%", color = Color(0xFF2E7D32), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = "Styles: ${product.styleTags.replace(",", " | ")}",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$${product.price}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = ProfessionalPolishText
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .background(ProfessionalPolishTerracotta, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("VIEW", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==================== 2. PRODUCT CATALOGUE ====================
@Composable
fun ShopScreen(viewModel: MainViewModel) {
    val products by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeCategory by viewModel.selectedCategory.collectAsState()
    val activeBrand by viewModel.selectedBrand.collectAsState()
    val activeStyleTag by viewModel.selectedStyleTag.collectAsState()
    val activeSize by viewModel.selectedSize.collectAsState()
    val activeColor by viewModel.selectedColor.collectAsState()
    val maxPriceLimit by viewModel.maxPrice.collectAsState()

    val brands by viewModel.availableBrands.collectAsState()
    val styles by viewModel.availableStyleTags.collectAsState()

    var showFiltersSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("shop_screen_layout")
    ) {
        // Search & Filter Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                placeholder = { Text("Search brands, jackets, styles...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("catalog_search_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = ProfessionalPolishTerracotta,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(ProfessionalPolishTerracotta, RoundedCornerShape(16.dp))
                    .clickable { showFiltersSheet = !showFiltersSheet }
                    .testTag("filter_trigger_btn"),
                contentAlignment = Alignment.Center
            ) {
                BadgedBox(
                    badge = {
                        val activeFilterCount = (if (activeCategory != null) 1 else 0) +
                                (if (activeBrand != null) 1 else 0) +
                                (if (activeStyleTag != null) 1 else 0) +
                                (if (activeSize != null) 1 else 0) +
                                (if (activeColor != null) 1 else 0) +
                                (if (maxPriceLimit < 300f) 1 else 0)
                        if (activeFilterCount > 0) {
                            Badge(containerColor = ProfessionalPolishPeach, contentColor = ProfessionalPolishDeepTerracotta) {
                                Text(activeFilterCount.toString(), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.List, contentDescription = "Filters", tint = Color.White)
                }
            }
        }

        // Active filter pills
        if (activeCategory != null || activeBrand != null || activeStyleTag != null || activeSize != null || activeColor != null || maxPriceLimit < 300f) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Clear all btn
                InputChip(
                    selected = false,
                    onClick = { viewModel.clearFilters() },
                    label = { Text("Clear All", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(12.dp)) }
                )
                activeCategory?.let {
                    InputChip(
                        selected = true,
                        onClick = { viewModel.selectedCategory.value = null },
                        label = { Text("Cat: $it", fontSize = 10.sp) }
                    )
                }
                activeBrand?.let {
                    InputChip(
                        selected = true,
                        onClick = { viewModel.selectedBrand.value = null },
                        label = { Text("Brand: $it", fontSize = 10.sp) }
                    )
                }
                activeStyleTag?.let {
                    InputChip(
                        selected = true,
                        onClick = { viewModel.selectedStyleTag.value = null },
                        label = { Text("Style: $it", fontSize = 10.sp) }
                    )
                }
                activeSize?.let {
                    InputChip(
                        selected = true,
                        onClick = { viewModel.selectedSize.value = null },
                        label = { Text("Size: $it", fontSize = 10.sp) }
                    )
                }
                activeColor?.let {
                    InputChip(
                        selected = true,
                        onClick = { viewModel.selectedColor.value = null },
                        label = { Text("Color: $it", fontSize = 10.sp) }
                    )
                }
                if (maxPriceLimit < 300f) {
                    InputChip(
                        selected = true,
                        onClick = { viewModel.maxPrice.value = 300f },
                        label = { Text("Max: $${maxPriceLimit.toInt()}", fontSize = 10.sp) }
                    )
                }
            }
        }

        if (showFiltersSheet) {
            FilterPanel(
                brands = brands,
                styles = styles,
                activeCategory = activeCategory,
                activeBrand = activeBrand,
                activeStyleTag = activeStyleTag,
                activeSize = activeSize,
                activeColor = activeColor,
                maxPriceLimit = maxPriceLimit,
                onCategorySelect = { viewModel.selectedCategory.value = it },
                onBrandSelect = { viewModel.selectedBrand.value = it },
                onStyleSelect = { viewModel.selectedStyleTag.value = it },
                onSizeSelect = { viewModel.selectedSize.value = it },
                onColorSelect = { viewModel.selectedColor.value = it },
                onPriceChange = { viewModel.maxPrice.value = it },
                onDismiss = { showFiltersSheet = false },
                onClear = { viewModel.clearFilters(); showFiltersSheet = false }
            )
        }

        // Product Catalog Grid
        Spacer(modifier = Modifier.height(4.dp))
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No products align with your current filters", color = Color.Gray, fontSize = 13.sp)
                    TextButton(onClick = { viewModel.clearFilters() }) {
                        Text("Reset filters", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(products) { product ->
                    CatalogProductGridCard(product = product, onClick = {
                        viewModel.navigateTo("product_detail", product.id)
                    }, onWishlistToggle = {
                        viewModel.toggleWishlist(product.id)
                    }, isWishlistedFlow = viewModel.isWishlisted(product.id))
                }
            }
        }
    }
}

@Composable
fun CatalogProductGridCard(
    product: ProductEntity,
    onClick: () -> Unit,
    onWishlistToggle: () -> Unit,
    isWishlistedFlow: Flow<Boolean>
) {
    val isWishlisted by isWishlistedFlow.collectAsState(initial = false)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("catalog_product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ProfessionalPolishSoftRose.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Wishlist Icon (floating)
                IconButton(
                    onClick = { onWishlistToggle() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape)
                        .testTag("wish_toggle_${product.id}")
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Save and wishlist",
                        tint = if (isWishlisted) Color.Red else Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Sustainability indicator overlay
                if (product.sustainabilityScore > 90) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp)
                            .background(Color(0xFF2E7D32), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("SUSTAINABLE", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.brand.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProfessionalPolishText.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = product.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = ProfessionalPolishText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${product.price}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = ProfessionalPolishTerracotta
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = ProfessionalPolishTerracotta, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(product.rating.toString(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ProfessionalPolishDeepTerracotta)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterPanel(
    brands: List<String>,
    styles: List<String>,
    activeCategory: String?,
    activeBrand: String?,
    activeStyleTag: String?,
    activeSize: String?,
    activeColor: String?,
    maxPriceLimit: Float,
    onCategorySelect: (String?) -> Unit,
    onBrandSelect: (String?) -> Unit,
    onStyleSelect: (String?) -> Unit,
    onSizeSelect: (String?) -> Unit,
    onColorSelect: (String?) -> Unit,
    onPriceChange: (Float) -> Unit,
    onDismiss: () -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
            .padding(12.dp)
            .testTag("filter_options_panel"),
        colors = CardDefaults.cardColors(containerColor = SoftGrayBg)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("REFINE SELECTION", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Dismiss") }
            }

            LazyColumn(
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                // Category list
                item {
                    Text("CATEGORY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        val categories = listOf("tops", "trousers", "dresses", "shoes", "accessories")
                        categories.forEach { cat ->
                            FilterSelectionButton(text = cat, isSelected = activeCategory == cat) {
                                onCategorySelect(if (activeCategory == cat) null else cat)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Brand selection
                item {
                    Text("BRAND", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        brands.forEach { brand ->
                            FilterSelectionButton(text = brand, isSelected = activeBrand == brand) {
                                onBrandSelect(if (activeBrand == brand) null else brand)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Style tag selection
                item {
                    Text("STYLE ACCENT vibe", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        styles.forEach { st ->
                            FilterSelectionButton(text = st, isSelected = activeStyleTag == st) {
                                onStyleSelect(if (activeStyleTag == st) null else st)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Size selection
                item {
                    Text("SIZE PREFERENCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        val sizes = listOf("XS", "S", "M", "L", "XL", "One Size")
                        sizes.forEach { size ->
                            FilterSelectionButton(text = size, isSelected = activeSize == size) {
                                onSizeSelect(if (activeSize == size) null else size)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Price Slider
                item {
                    Text("MAX TARGET PRICE ($${maxPriceLimit.toInt()})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Slider(
                        value = maxPriceLimit,
                        onValueChange = onPriceChange,
                        valueRange = 20f..300f,
                        colors = SliderDefaults.colors(thumbColor = Color.Black, activeTrackColor = Color.Black)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClear,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(2.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) {
                    Text("RESET")
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
                ) {
                    Text("APPLY FILTERS")
                }
            }
        }
    }
}

@Composable
fun FilterSelectionButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .background(
                color = if (isSelected) Color.Black else Color.White,
                shape = RoundedCornerShape(2.dp)
            )
            .border(1.dp, Color.Black, RoundedCornerShape(2.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

// ==================== 3. PRODUCT PAGES & FIT SUGGESTION ====================
@Composable
fun ProductDetailScreen(viewModel: MainViewModel) {
    val product by viewModel.selectedProduct.collectAsState(initial = null)
    val relatedProducts by viewModel.relatedProducts.collectAsState(initial = emptyList())
    val styleProfile by viewModel.styleProfile.collectAsState()

    var selectedSize by remember { mutableStateOf<String?>(null) }
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var showAddedMessage by remember { mutableStateOf(false) }

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.Black)
        }
        return
    }

    val item = product!!

    // Initialize defaults on draw once
    LaunchedEffect(item) {
        val sizeList = item.sizes.split(",")
        if (sizeList.isNotEmpty()) selectedSize = sizeList.first().trim()

        val colorList = item.colors.split(",")
        if (colorList.isNotEmpty()) selectedColor = colorList.first().trim()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("product_detail_screen")
    ) {
        // Back toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo("shop") }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(item.brand.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { viewModel.toggleWishlist(item.id) }) {
                val isWish by viewModel.isWishlisted(item.id).collectAsState(initial = false)
                Icon(
                    imageVector = if (isWish) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Save",
                    tint = if (isWish) Color.Red else Color.Black
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Full-bleed Image + Try on banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Video Try-On simulated indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(2.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = GoldLogoColor, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("VIDEO TRY-ON ACTIVE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Standard product metadata
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = item.brand.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldLogoColor,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = item.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$${item.price}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = GoldLogoColor, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${item.rating} (${item.reviewCount} customer reviews)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = item.description, fontSize = 13.sp, color = Color.Black.copy(alpha = 0.8f), lineHeight = 20.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                }
            }

            // Size guide selector
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("SELECT SIZE", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item.sizes.split(",").forEach { sz ->
                            val szTrim = sz.trim()
                            val selected = selectedSize == szTrim
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(if (selected) Color.Black else Color.White, RoundedCornerShape(2.dp))
                                    .border(1.dp, Color.Black, RoundedCornerShape(2.dp))
                                    .clickable { selectedSize = szTrim }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(szTrim, color = if (selected) Color.White else Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📐 Size Guide: standard fit measured per global metrics.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            // AI-powered "Fit Suggestion" Tool
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftGrayBg),
                    border = BorderStroke(1.dp, GoldLogoColor)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, "AI", tint = GoldLogoColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AI STYLIST FIT SUGGESTION", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        // Display customizable tip based on user profile preferences
                        Text(
                            text = "Matching with your style profile (Preferred size: ${styleProfile.preferredSizes}, fits ${styleProfile.genderPreference}). " +
                                    "Based on 140+ user feedback on this item, customer consensus indicates this runs true-to-size. " +
                                    "We suggest selecting size **${styleProfile.preferredSizes}** for standard comfort look.",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = Color.Black.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Colors selector
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("SELECT COLOR", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item.colors.split(",").forEach { col ->
                            val colTrim = col.trim()
                            val selected = selectedColor == colTrim
                            Box(
                                modifier = Modifier
                                    .background(if (selected) Color.Black else SoftGrayBg, RoundedCornerShape(2.dp))
                                    .border(1.dp, Color.Black, RoundedCornerShape(2.dp))
                                    .clickable { selectedColor = colTrim }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(colTrim, color = if (selected) Color.White else Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                }
            }

            // Complete the outfit bundles
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("COMPLETE THE LOOK BUNDLE", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SoftGrayBg)
                            .border(1.dp, Color.LightGray.copy(alpha = 0.5f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = GoldLogoColor)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Add [p3] platform shoes and [p7] crossbody bag. Take 15% bundle reduction at discount panel!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Section: Related products
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        text = "YOU MIGHT ALSO LOVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(contentPadding = PaddingValues(horizontal = 12.dp)) {
                        items(relatedProducts) { prod ->
                            ProductCardHorizontal(product = prod) {
                                viewModel.navigateTo("product_detail", prod.id)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Added message dynamic bar
        AnimatedVisibility(
            visible = showAddedMessage,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Surface(
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ADDED TO CART BAG", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    TextButton(onClick = { viewModel.navigateTo("checkout") }) {
                        Text("GO TO BAG", color = GoldLogoColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Sticky bottom shopping actions bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo("stylist") },
                    modifier = Modifier
                        .size(52.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(2.dp))
                ) {
                    Icon(Icons.Default.Star, contentDescription = "Style advice", tint = GoldLogoColor)
                }

                Button(
                    onClick = {
                        val selSize = selectedSize ?: "M"
                        val selCol = selectedColor ?: "All"
                        viewModel.addToCart(item.id, selSize, selCol, 1)
                        // Trigger animations
                        showAddedMessage = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("add_to_bag_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text("ADD TO BAG Drop", fontSize = 14.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                }
            }
        }
    }
}

// ==================== 4. AI STYLIST ASSISTANT ====================
@Composable
fun StylistScreen(viewModel: MainViewModel) {
    val chatHistory by viewModel.chatMessages.collectAsState()
    val isSending by viewModel.isChatLoading.collectAsState()
    val listState = rememberLazyListState()

    var textInput by remember { mutableStateOf("") }

    // Scroll to latest chat item automatically
    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("stylist_chat_screen")
    ) {
        // Assistant Editorial Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ProfessionalPolishDeepTerracotta)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(ProfessionalPolishPeach, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = ProfessionalPolishDeepTerracotta, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("NOVA EDITORIAL AI", color = ProfessionalPolishPeach, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 2.sp)
                Text("Curation mapping styling advisor", color = ProfessionalPolishPeach.copy(alpha = 0.85f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { viewModel.clearChat() }) {
                Icon(Icons.Default.Delete, contentDescription = "Clear Chat", tint = ProfessionalPolishPeach)
            }
        }

        // Chat conversation log
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(chatHistory) { msg ->
                ChatBubble(msg = msg, onProductBadgeClick = { id ->
                    viewModel.navigateTo("product_detail", id)
                })
                Spacer(modifier = Modifier.height(10.dp))
            }
            if (isSending) {
                item {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = ProfessionalPolishTerracotta, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Nova is drafting look suggestions...", fontSize = 11.sp, color = ProfessionalPolishDeepTerracotta, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Preset Prompt pills row for easy testing
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ProfessionalPolishSoftRose.copy(alpha = 0.5f))
                .padding(vertical = 8.dp)
        ) {
            Text(
                "PRESETS FOR NOVAS LOOKS BOOK:",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = ProfessionalPolishDeepTerracotta,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
                letterSpacing = 1.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val presets = listOf(
                    "Suggest a streetwear look",
                    "Minimal luxury outfit",
                    "Complete outfit with pants",
                    "What's good for raining drop?"
                )
                presets.forEach { pr ->
                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, ProfessionalPolishSoftRose, RoundedCornerShape(12.dp))
                            .clickable { viewModel.sendChatMessage(pr) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(pr, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ProfessionalPolishDeepTerracotta)
                    }
                }
            }
        }

        // Chat writing container
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Ask Nova for style advice...", fontSize = 13.sp, color = ProfessionalPolishDeepTerracotta.copy(alpha = 0.6f)) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stylist_chat_text_input"),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ProfessionalPolishBg,
                        unfocusedContainerColor = ProfessionalPolishBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendChatMessage(textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(ProfessionalPolishTerracotta, CircleShape)
                        .testTag("stylist_send_btn"),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: UiChatMessage, onProductBadgeClick: (String) -> Unit) {
    val alignment = if (msg.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bg = if (msg.isUser) ProfessionalPolishDeepTerracotta else ProfessionalPolishPeach
    val txtCol = if (msg.isUser) Color.White else ProfessionalPolishDeepTerracotta
    val shape = if (msg.isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 0.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 16.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(bg, shape)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = msg.text,
                        color = txtCol,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    // If it is dynamic bot, parse catalog references [p1..p11]
                    if (!msg.isUser) {
                        val modelMatcher = "\\[p(\\d+)\\]".toRegex()
                        val matches = modelMatcher.findAll(msg.text).map { it.value }.toList()
                        if (matches.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = ProfessionalPolishSoftRose)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("TAP TO INSTANT VIEW CLOTHING:", color = ProfessionalPolishTerracotta, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            matches.distinct().forEach { token ->
                                val cleanId = token.replace("[", "").replace("]", "")
                                // Show badge text link
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(Color.White, RoundedCornerShape(12.dp))
                                        .border(1.dp, ProfessionalPolishTerracotta, RoundedCornerShape(12.dp))
                                        .clickable { onProductBadgeClick(cleanId) }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.ShoppingCart, null, tint = ProfessionalPolishDeepTerracotta, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "View Product Catalog #$cleanId",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ProfessionalPolishDeepTerracotta
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.ArrowForward, null, tint = ProfessionalPolishDeepTerracotta, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== 5. USER ACCOUNTS & STYLE PROFILE ====================
@Composable
fun MeScreen(viewModel: MainViewModel, onStartQuiz: () -> Unit) {
    val profile by viewModel.styleProfile.collectAsState()
    val orders by viewModel.ordersList.collectAsState()
    val wishlist by viewModel.wishlistProducts.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("me_screen_layout"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // User profile Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandDarkBg)
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(GoldLogoColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ME",
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            fontSize = 24.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("NOVA COUTURE ENTHUSIAST", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "LOYALTY TIER: ${if (profile.loyaltyPoints > 300) "PLATINUM LEVEL" else "GOLD STANDARD CLUB"}",
                        color = GoldLogoColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Loyalty counter card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(containerColor = SoftGrayBg),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ACTIVE LOYALTY BALANCE", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                        Icon(Icons.Default.Star, null, tint = GoldLogoColor)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${profile.loyaltyPoints} PTS", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Earn 10 points per $10 spent on drops! Completion quiz grants bonus 50 pts.", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { (profile.loyaltyPoints % 500) / 500f },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black,
                        trackColor = Color.LightGray.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // Personal Style Profile (Quiz-based onboarding)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("MY STYLE PROFILE ACCENTS", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.7f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Gender alignment:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(profile.genderPreference, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Curated size context:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(profile.preferredSizes, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Loved design aesthetics:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(profile.preferredStyles, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Budget scale indexing:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(profile.budgetRange, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onStartQuiz,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                            shape = RoundedCornerShape(2.dp)
                        ) {
                            Text("RE-TAKE DESIGN STYLE QUIZ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section: Order history
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ORDER HISTORY TRACKING", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (orders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SoftGrayBg)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No orders completed yet", fontSize = 11.sp, color = Color.Gray)
                    }
                } else {
                    orders.forEach { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(2.dp),
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(order.id, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Black, RoundedCornerShape(2.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(order.status, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Items: ${order.itemsJson}", fontSize = 11.sp, color = Color.Black.copy(alpha = 0.7f))
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = java.text.SimpleDateFormat("MMM dd, yyyy").format(java.util.Date(order.date)),
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "$${order.totalAmount}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Saved items / wishlist
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("SAVED WISHLIST ITEMS (${wishlist.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (wishlist.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SoftGrayBg)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No items on wishlist yet", fontSize = 11.sp, color = Color.Gray)
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        wishlist.forEach { p ->
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .background(SoftGrayBg)
                                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f))
                                    .clickable { viewModel.navigateTo("product_detail", p.id) }
                                    .padding(8.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    AsyncImage(
                                        model = p.imageUrl,
                                        contentDescription = p.title,
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(p.title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Text("$${p.price}", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Switch to Brand portal & Admin options
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Divider()
                Spacer(modifier = Modifier.height(20.dp))
                Text("BUSINESS & PLATFORM ACTIONS", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { viewModel.navigateTo("seller_portal") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray, contentColor = Color.White),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("ENTER BRAND SELLER PORTAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.navigateTo("admin_panel") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Icon(Icons.Default.Settings, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("ENTER SYSTEM ADMIN CONTROL PANEL", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
    }
}

// ==================== 6. CHECKOUT & PAYMENTS ====================
@Composable
fun CheckoutScreen(viewModel: MainViewModel) {
    val items by viewModel.cartProducts.collectAsState()
    val total by viewModel.cartTotal.collectAsState()

    var billingAddress by remember { mutableStateOf("112 Fashion Boulevard, Suite 9B, London") }
    var promoCodeInput by remember { mutableStateOf("") }
    var activeDiscountPercent by remember { mutableStateOf(0) }
    var selectedServiceOption by remember { mutableStateOf("standard") } // standard, express, express_priority
    var shippingCost by remember { mutableStateOf(5.0) }

    var paymentCardNumber by remember { mutableStateOf("4532 9811 0212 9011") }
    var paymentCardExpiry by remember { mutableStateOf("09/29") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("checkout_page_layout")
    ) {
        // Simple back toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo("home") }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("SECURE DROP CHECKOUT", fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 2.sp)
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Cart editing lists
            item {
                Text("YOUR BAG ITEMS (${items.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (items.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp), contentAlignment = Alignment.Center
                    ) {
                        Text("Cart bags empty! Return to shop.", color = Color.Gray)
                    }
                } else {
                    items.forEach { cartProd ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.dp, Color.LightGray.copy(alpha = 0.5f))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = cartProd.product?.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cartProd.product?.title ?: "Item", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Size: ${cartProd.cartItem.size} | Color: ${cartProd.cartItem.color}", fontSize = 10.sp, color = Color.Gray)
                            }
                            // Quantity modifier buttons
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = { viewModel.modifyCartQuantity(cartProd.cartItem.id, cartProd.cartItem.quantity - 1) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SoftGrayBg, contentColor = Color.Black),
                                    shape = RoundedCornerShape(2.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text("-", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(cartProd.cartItem.quantity.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { viewModel.modifyCartQuantity(cartProd.cartItem.id, cartProd.cartItem.quantity + 1) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SoftGrayBg, contentColor = Color.Black),
                                    shape = RoundedCornerShape(2.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text("+", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Divider()
            }

            // Promo code engine
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text("PROMO DISCOUNTS", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextField(
                            value = promoCodeInput,
                            onValueChange = { promoCodeInput = it },
                            placeholder = { Text("Enter NOVA20 or NOVA50", fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("promo_input_field"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SoftGrayBg,
                                unfocusedContainerColor = SoftGrayBg,
                                focusedIndicatorColor = Color.Black,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (promoCodeInput.trim().equals("NOVA20", ignoreCase = true)) {
                                    activeDiscountPercent = 20
                                } else if (promoCodeInput.trim().equals("NOVA50", ignoreCase = true)) {
                                    activeDiscountPercent = 50
                                }
                            },
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
                        ) {
                            Text("APPLY")
                        }
                    }
                    if (activeDiscountPercent > 0) {
                        Text(
                            text = "Promo applied successfully! $activeDiscountPercent% discount subtraction calculated below.",
                            color = Color(0xFF2E7D32),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                Divider()
            }

            // Shipping Selector Options
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text("SHIPPING CARRIER OPTIONS", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedServiceOption = "standard"; shippingCost = 5.0 }
                                .border(1.dp, if (selectedServiceOption == "standard") Color.Black else Color.Transparent),
                            colors = CardDefaults.cardColors(containerColor = SoftGrayBg)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Standard", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("3-5 Days", fontSize = 10.sp, color = Color.Gray)
                                Text("$5.00", fontWeight = FontWeight.Black, fontSize = 11.sp)
                            }
                        }
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedServiceOption = "express"; shippingCost = 15.0 }
                                .border(1.dp, if (selectedServiceOption == "express") Color.Black else Color.Transparent),
                            colors = CardDefaults.cardColors(containerColor = SoftGrayBg)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Express Mail", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Next-Day", fontSize = 10.sp, color = Color.Gray)
                                Text("$15.00", fontWeight = FontWeight.Black, fontSize = 11.sp)
                            }
                        }
                    }
                }
                Divider()
            }

            // Address Contact Form
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text("SHIPPING AND DESTINATION", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    TextField(
                        value = billingAddress,
                        onValueChange = { billingAddress = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SoftGrayBg,
                            unfocusedContainerColor = SoftGrayBg,
                            focusedIndicatorColor = Color.Black,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
                Divider()
            }

            // Card configuration Details panel
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text("CARD SETTINGS (SECURE CHECKOUT)", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    TextField(
                        value = paymentCardNumber,
                        onValueChange = { paymentCardNumber = it },
                        label = { Text("Debit/Credit Card Stripe") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SoftGrayBg,
                            unfocusedContainerColor = SoftGrayBg,
                            focusedIndicatorColor = Color.Black,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row {
                        TextField(
                            value = paymentCardExpiry,
                            onValueChange = { paymentCardExpiry = it },
                            label = { Text("Expiry (MM/YY)") },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SoftGrayBg,
                                unfocusedContainerColor = SoftGrayBg,
                                focusedIndicatorColor = Color.Black,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = "901",
                            onValueChange = { },
                            label = { Text("CVV Secure") },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = SoftGrayBg,
                                unfocusedContainerColor = SoftGrayBg,
                                focusedIndicatorColor = Color.Black,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }
                }
                Divider()
            }

            // Total billing computations
            item {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    val discountAmount = (total * activeDiscountPercent) / 100.0
                    val finalCalculatedTotal = (total - discountAmount) + shippingCost
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cart Subtotal:", fontSize = 12.sp, color = Color.Gray)
                        Text("$${"%.2f".format(total)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    if (activeDiscountPercent > 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Discount Applied ($activeDiscountPercent%):", fontSize = 12.sp, color = Color(0xFF2E7D32))
                            Text("-$${"%.2f".format(discountAmount)}", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Shipping costs:", fontSize = 12.sp, color = Color.Gray)
                        Text("$${"%.2f".format(shippingCost)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TOTAL BILLING DUE:", fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Text("$${"%.2f".format(finalCalculatedTotal)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }
                }
            }
        }

        // Sticky buy button bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(16.dp)
            ) {
                val discountAmount = (total * activeDiscountPercent) / 100.0
                val finalCalculatedTotal = (total - discountAmount) + shippingCost
                Button(
                    onClick = {
                        if (items.isNotEmpty()) {
                            viewModel.handleCheckout(billingAddress)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("checkout_place_order"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                    shape = RoundedCornerShape(2.dp),
                    enabled = items.isNotEmpty()
                ) {
                    Text("AUTHORIZE PAYMENT: $${"%.2f".format(finalCalculatedTotal)}", fontSize = 13.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                }
            }
        }
    }
}

// ==================== 7. ORDER SUCCESS ====================
@Composable
fun OrderSuccessScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("order_success_page"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = "Success", tint = GoldLogoColor, modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "DROP PAYMENT AUTHORIZED",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 3.sp,
            fontFamily = FontFamily.Serif
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Your order ticket was successfully submitted into the local Room database index. " +
                    "Your shipping tracking metrics will be live shortly.",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(2.dp),
            colors = CardDefaults.cardColors(containerColor = SoftGrayBg)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("LOYALTY DROP BOOST!", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GoldLogoColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Points earned from this drop were successfully credited to your personal loyalty standard profile!", fontSize = 11.sp, textAlign = TextAlign.Center)
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = { viewModel.navigateTo("home") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(2.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
        ) {
            Text("RETURN TO DISCOVERY FEED")
        }
    }
}

// ==================== 8. SELLER/BRAND PORTAL ====================
@Composable
fun SellerPortalScreen(viewModel: MainViewModel) {
    val products by viewModel.filteredProducts.collectAsState()
    val orders by viewModel.ordersList.collectAsState()

    var productTitle by remember { mutableStateOf("") }
    var productBrand by remember { mutableStateOf("NovaIndependent") }
    var productPrice by remember { mutableStateOf("99.0") }
    var productCategory by remember { mutableStateOf("tops") } // tops, shoes, accessories
    var productDescription by remember { mutableStateOf("") }
    var productImage by remember { mutableStateOf("https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=600&q=80") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("seller_portal_screen")
    ) {
        // Portal header tabs
        TabRow(selectedTabIndex = 0, contentColor = Color.Black) {
            Tab(selected = true, onClick = {}, text = { Text("Brand Seller Studio", fontWeight = FontWeight.Bold) })
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Analytical cards dashboard
            item {
                Text("INTELLIGENCE ANALYTICS", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(2.dp), colors = CardDefaults.cardColors(containerColor = SoftGrayBg)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Total Live Product Drops", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(products.size.toString(), fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(2.dp), colors = CardDefaults.cardColors(containerColor = SoftGrayBg)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Orders Fulfillment Count", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(orders.size.toString(), fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Divider()
            }

            // Product uploading Form
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text("DROP AN INDEPENDENT CLOTHING ITEM", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        value = productTitle,
                        onValueChange = { productTitle = it },
                        label = { Text("Product Drop Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = SoftGrayBg, unfocusedContainerColor = SoftGrayBg)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row {
                        TextField(
                            value = productBrand,
                            onValueChange = { productBrand = it },
                            label = { Text("Brand label") },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(focusedContainerColor = SoftGrayBg, unfocusedContainerColor = SoftGrayBg)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        TextField(
                            value = productPrice,
                            onValueChange = { productPrice = it },
                            label = { Text("Price Drop ($)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.colors(focusedContainerColor = SoftGrayBg, unfocusedContainerColor = SoftGrayBg)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    TextField(
                        value = productCategory,
                        onValueChange = { productCategory = it },
                        label = { Text("Category (tops, trousers, dresses, shoes, accessories)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = SoftGrayBg, unfocusedContainerColor = SoftGrayBg)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    TextField(
                        value = productDescription,
                        onValueChange = { productDescription = it },
                        label = { Text("Editorial Copy Description") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = SoftGrayBg, unfocusedContainerColor = SoftGrayBg)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    TextField(
                        value = productImage,
                        onValueChange = { productImage = it },
                        label = { Text("Catalog Unsplash Image Link URL") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = SoftGrayBg, unfocusedContainerColor = SoftGrayBg)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (productTitle.isNotBlank() && productPrice.isNotBlank()) {
                                val priceDbl = productPrice.toDoubleOrNull() ?: 50.0
                                viewModel.uploadProductBySeller(
                                    id = "p-custom-" + (1000..9999).random().toString(),
                                    title = productTitle,
                                    brand = productBrand,
                                    description = productDescription,
                                    category = productCategory,
                                    price = priceDbl,
                                    imageUrl = productImage,
                                    styleTags = "streetwear,minimalist",
                                    sizes = "S,M,L",
                                    colors = "Black,White"
                                )
                                // Reset fields
                                productTitle = ""
                                productDescription = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
                    ) {
                        Text("LAUNCH DROP INTO DATABASE SITE")
                    }
                }
                Divider()
            }

            // Inventory & stocks manager list
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text("ACTIVE INVENTORY STOCK MANAGEMENT", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    products.forEach { prod ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.dp, Color.LightGray.copy(alpha = 0.5f))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = prod.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(prod.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("Price: $${prod.price} | Stock: ${prod.stock}", fontSize = 10.sp, color = Color.Gray)
                            }
                            // Adjust Stock Buttons
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = { viewModel.adjustProductStock(prod.id, (prod.stock - 5).coerceAtLeast(0)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SoftGrayBg, contentColor = Color.Black),
                                    shape = RoundedCornerShape(2.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text("-", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = { viewModel.adjustProductStock(prod.id, prod.stock + 5) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SoftGrayBg, contentColor = Color.Black),
                                    shape = RoundedCornerShape(2.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text("+", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(onClick = { viewModel.deleteProductByAdminOrSeller(prod.id) }) {
                                    Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== 9. ADMIN PANEL ====================
@Composable
fun AdminPanelScreen(viewModel: MainViewModel) {
    val products by viewModel.filteredProducts.collectAsState()
    val salesVal by viewModel.adminTotalSales.collectAsState(initial = 0.0)
    val orderCount by viewModel.adminOrderCount.collectAsState(initial = 0)
    val categorySales by viewModel.adminCategorySales.collectAsState(initial = emptyMap())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_panel_screen")
    ) {
        TabRow(selectedTabIndex = 0, contentColor = Color.Black) {
            Tab(selected = true, onClick = {}, text = { Text("System Administration Center", fontWeight = FontWeight.Bold) })
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Sales dashboard metrics
            item {
                Text("PLATFORM MACRO METRICS", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("TOTAL PLATFORM VOLUME SALES", fontSize = 9.sp, color = GoldLogoColor, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("$${"%.2f".format(salesVal)}", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Calculated live per database orders. Orders count checked: $orderCount items.", fontSize = 11.sp, color = Color.LightGray)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Divider()
            }

            // Category density charts list
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text("CATEGORY CONVERSIONS METRIC", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    categorySales.forEach { (cat, count) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(cat.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
                            // Simple responsive custom progress bar chart representation
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(12.dp)
                                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(fraction = (count.toFloat() / 15f).coerceAtMost(1f))
                                        .background(Color.Black, RoundedCornerShape(6.dp))
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("$count items", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Divider()
            }

            // Moderation list
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text("PRODUCT MODERATION SEEDING ENGINE", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    products.forEach { prod ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.dp, Color.LightGray.copy(alpha = 0.5f))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = prod.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(prod.title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Rating: ${prod.rating} | Price: $${prod.price}", fontSize = 10.sp, color = Color.Gray)
                            }
                            // Delete Moderation button
                            IconButton(onClick = { viewModel.deleteProductByAdminOrSeller(prod.id) }) {
                                Icon(Icons.Default.Delete, null, tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== INTERACTIVE QUIZ DIALOG ====================
@Composable
fun QuizOnboardingDialog(
    onDismiss: () -> Unit,
    onSave: (gender: String, styles: String, sizes: String, budget: String) -> Unit
) {
    var genderSelection by remember { mutableStateOf("Women's Wear") }
    var preferredSize by remember { mutableStateOf("M") }
    var selectedStylesText by remember { mutableStateOf("Streetwear, Minimalist") }
    var selectedBudget by remember { mutableStateOf("Medium Luxury scale") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "NOVA STYLE ONBOARDING",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    letterSpacing = 2.sp,
                    color = Color.Black,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Configure your aesthetic filters to customized styling feeds automatically.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Step 1: Gender Pref
                Text("1. FASHION GENDER ALIGNMENT:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val genders = listOf("Women's Wear", "Men's Wear", "Unisex")
                    genders.forEach { gen ->
                        val active = genderSelection == gen
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color.Black)
                                .background(if (active) Color.Black else Color.White)
                                .clickable { genderSelection = gen }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(gen, color = if (active) Color.White else Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                // Step 2: Size Pref
                Text("2. TYPICAL SHIRT SIZE FIT:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val sizes = listOf("XS", "S", "M", "L", "XL")
                    sizes.forEach { sz ->
                        val active = preferredSize == sz
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .border(1.dp, Color.Black)
                                .background(if (active) Color.Black else Color.White)
                                .clickable { preferredSize = sz }
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(sz, color = if (active) Color.White else Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                // Step 3: Preferred style
                Text("3. CORE AESTHETIC VIBE:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = selectedStylesText,
                    onValueChange = { selectedStylesText = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = SoftGrayBg, unfocusedContainerColor = SoftGrayBg)
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Step 4: Budget
                Text("4. TARGET PRICE INDEX BUDGET LEVEL:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val budgets = listOf("Aspirational High-End", "Medium Luxury scale", "Street Budget")
                    budgets.forEach { bd ->
                        val active = selectedBudget == bd
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color.Black)
                                .background(if (active) Color.Black else Color.White)
                                .clickable { selectedBudget = bd }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(bd, color = if (active) Color.White else Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Text("CANCEL")
                    }
                    Button(
                        onClick = {
                            onSave(genderSelection, selectedStylesText, preferredSize, selectedBudget)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Text("SAVE & CLAIM 50 PTS")
                    }
                }
            }
        }
    }
}
