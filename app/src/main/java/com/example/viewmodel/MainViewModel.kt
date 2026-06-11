package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    val repository = MainRepository(db)

    init {
        viewModelScope.launch {
            repository.initialiseIfNeeded()
        }
    }

    // --- Navigation ---
    private val _currentScreen = MutableStateFlow("home") // home, shop, stylist, me, product_detail, checkout, order_success, seller_portal, admin_panel, quiz
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _selectedProductId = MutableStateFlow<String?>(null)
    val selectedProductId: StateFlow<String?> = _selectedProductId.asStateFlow()

    fun navigateTo(screen: String, productId: String? = null) {
        if (productId != null) {
            _selectedProductId.value = productId
        }
        _currentScreen.value = screen
    }

    // --- Product detail holder ---
    val selectedProduct: Flow<ProductEntity?> = _selectedProductId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else repository.allProducts.map { list -> list.find { it.id == id } }
    }

    // Related products (same brand or same style tags, up to 3 elements)
    val relatedProducts: Flow<List<ProductEntity>> = selectedProduct.flatMapLatest { current ->
        if (current == null) flowOf(emptyList())
        else repository.allProducts.map { products ->
            products.filter { 
                it.id != current.id && (it.category == current.category || it.brand == current.brand)
            }.take(3)
        }
    }

    // --- Catalog Filtering State ---
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<String?>(null) // tops, trousers, dresses, shoes, accessories
    val selectedBrand = MutableStateFlow<String?>(null)
    val selectedSize = MutableStateFlow<String?>(null)
    val selectedColor = MutableStateFlow<String?>(null)
    val selectedStyleTag = MutableStateFlow<String?>(null)
    val maxPrice = MutableStateFlow(300f)

    // Dynamic Options gathered from database
    val availableBrands: StateFlow<List<String>> = repository.allProducts.map { list ->
        list.map { it.brand }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableStyleTags: StateFlow<List<String>> = repository.allProducts.map { list ->
        list.flatMap { it.styleTags.split(",") }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Products
    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        repository.allProducts,
        searchQuery,
        selectedCategory,
        selectedBrand,
        selectedSize,
        selectedColor,
        selectedStyleTag,
        maxPrice
    ) { array ->
        val products = array[0] as List<ProductEntity>
        val search = array[1] as String
        val cat = array[2] as String?
        val brand = array[3] as String?
        val size = array[4] as String?
        val color = array[5] as String?
        val style = array[6] as String?
        val priceLimit = array[7] as Float

        products.filter { p ->
            val matchSearch = search.isEmpty() || p.title.contains(search, ignoreCase = true) || p.brand.contains(search, ignoreCase = true) || p.description.contains(search, ignoreCase = true)
            val matchCat = cat == null || p.category.equals(cat, ignoreCase = true)
            val matchBrand = brand == null || p.brand.equals(brand, ignoreCase = true)
            val matchSize = size == null || p.sizes.split(",").map { it.trim() }.contains(size)
            val matchColor = color == null || p.colors.split(",").map { it.trim() }.contains(color)
            val matchStyle = style == null || p.styleTags.split(",").map { it.trim() }.contains(style)
            val matchPrice = p.price <= priceLimit

            matchSearch && matchCat && matchBrand && matchSize && matchColor && matchStyle && matchPrice
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearFilters() {
        searchQuery.value = ""
        selectedCategory.value = null
        selectedBrand.value = null
        selectedSize.value = null
        selectedColor.value = null
        selectedStyleTag.value = null
        maxPrice.value = 300f
    }

    // --- Cart Management ---
    val cartProducts: StateFlow<List<CartProduct>> = repository.cartProducts.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val cartTotal: StateFlow<Double> = cartProducts.map { items ->
        items.sumOf { (it.product?.price ?: 0.0) * it.cartItem.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartCount: StateFlow<Int> = cartProducts.map { items ->
        items.sumOf { it.cartItem.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addToCart(productId: String, size: String, color: String, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(productId, size, color, quantity)
        }
    }

    fun modifyCartQuantity(cartId: Int, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartId, newQuantity)
        }
    }

    fun removeFromCart(cartId: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartId, 0)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // --- Wishlist Management ---
    val wishlistProducts: StateFlow<List<ProductEntity>> = repository.wishlistProducts.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun isWishlisted(productId: String): Flow<Boolean> = repository.isWishlistedFlow(productId)

    fun toggleWishlist(productId: String) {
        viewModelScope.launch {
            repository.toggleWishlist(productId)
        }
    }

    // --- Onboarding / Quiz / Style Profile ---
    val styleProfile: StateFlow<StyleProfileEntity> = repository.styleProfile.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), StyleProfileEntity()
    )

    fun saveStyleProfile(gender: String, styles: String, sizes: String, budget: String) {
        viewModelScope.launch {
            val currentPoints = styleProfile.value.loyaltyPoints
            repository.updateStyleProfile(StyleProfileEntity(
                genderPreference = gender,
                preferredStyles = styles,
                preferredSizes = sizes,
                budgetRange = budget,
                loyaltyPoints = currentPoints + 50 // Reward 50 pts on profile completion!
            ))
            _currentScreen.value = "me"
        }
    }

    // --- Checkout state ---
    private val _checkoutSuccessOrder = MutableStateFlow<String?>(null)
    val checkoutSuccessOrder: StateFlow<String?> = _checkoutSuccessOrder.asStateFlow()

    fun handleCheckout(address: String) {
        viewModelScope.launch {
            val total = cartTotal.value
            val cartList = cartProducts.value
            if (cartList.isNotEmpty()) {
                repository.placeOrder(cartList, total, address)
                _checkoutSuccessOrder.value = "Order placed successfully!"
                _currentScreen.value = "order_success"
            }
        }
    }

    val ordersList: StateFlow<List<OrderEntity>> = repository.allOrders.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // --- AI Stylist Chat State ---
    private val _chatMessages = MutableStateFlow<List<UiChatMessage>>(listOf(
        UiChatMessage(
            text = "Welcome to NovaWear styling headquarters! 🕶️ I'm Nova, your personal stylist.\n\nTell me if you need help finding outfits for a particular occasion, completing a look, or discovering Y2K, street, or minimal luxury vibes!",
            isUser = false
        )
    ))
    val chatMessages: StateFlow<List<UiChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = UiChatMessage(text = text, isUser = true)
        _chatMessages.update { it + userMsg }

        _isChatLoading.value = true
        viewModelScope.launch {
            // Build conversation history for API
            val apiHistory = _chatMessages.value.map { msg ->
                Content(parts = listOf(Part(text = msg.text)))
            }
            // Request Stylist Response
            val stylistText = GeminiStylistService.chatWithStylist(apiHistory)
            
            _chatMessages.update { it + UiChatMessage(text = stylistText, isUser = false) }
            _isChatLoading.value = false
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            UiChatMessage(
                text = "Chat history cleared! What are we designing today? ✨",
                isUser = false
            )
        )
    }

    // --- Brand/Seller Portal ---
    fun uploadProductBySeller(
        id: String, title: String, brand: String, description: String,
        category: String, price: Double, imageUrl: String, styleTags: String, sizes: String, colors: String
    ) {
        viewModelScope.launch {
            val newProduct = ProductEntity(
                id = id,
                title = title,
                brand = brand,
                description = description,
                category = category,
                price = price,
                imageUrl = imageUrl,
                videoUrl = null,
                sizes = sizes,
                colors = colors,
                styleTags = styleTags,
                rating = 5.0,
                reviewCount = 1,
                stock = 10,
                isTrending = false,
                isNewIn = true,
                sustainabilityScore = 88
            )
            repository.addCustomProduct(newProduct)
        }
    }

    fun deleteProductByAdminOrSeller(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

    fun adjustProductStock(productId: String, newStock: Int) {
        viewModelScope.launch {
            repository.updateProductStock(productId, newStock)
        }
    }

    // --- Admin Dashboard Calculations ---
    val adminTotalSales: Flow<Double> = ordersList.map { list -> list.sumOf { it.totalAmount } }
    val adminOrderCount: Flow<Int> = ordersList.map { list -> list.size }
    
    val adminCategorySales: Flow<Map<String, Int>> = repository.allProducts.map { products ->
        // Dynamically compute category sizes based on current stocks
        products.groupBy { it.category }.mapValues { entry -> entry.value.size }
    }
}
