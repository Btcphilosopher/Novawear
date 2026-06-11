package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID

data class CartProduct(
    val cartItem: CartItemEntity,
    val product: ProductEntity?
)

class MainRepository(private val db: AppDatabase) {
    private val productDao = db.productDao()
    private val cartDao = db.cartDao()
    private val wishlistDao = db.wishlistDao()
    private val orderDao = db.orderDao()
    private val styleProfileDao = db.styleProfileDao()

    // Retrieve all products
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()

    // Prepopulate if empty
    suspend fun initialiseIfNeeded() {
        // Simple check
        val list = PrepopulationData.PRODUCTS
        // Check if DB is empty
        val currentProducts = db.productDao().getProductById(list.first().id)
        if (currentProducts == null) {
            db.productDao().insertProducts(list)
            // Save a default style profile if null
            db.styleProfileDao().insertProfile(StyleProfileEntity(
                id = "me",
                loyaltyPoints = 150
            ))
        }
    }

    suspend fun getProductById(id: String): ProductEntity? {
        return productDao.getProductById(id)
    }

    // Combined Flow for Cart Product details
    val cartProducts: Flow<List<CartProduct>> = combine(
        cartDao.getCartItems(),
        productDao.getAllProducts()
    ) { cartItems, products ->
        cartItems.map { cartItem ->
            CartProduct(cartItem, products.find { it.id == cartItem.productId })
        }
    }

    // Combined Flow for Wishlist Products
    val wishlistProducts: Flow<List<ProductEntity>> = combine(
        wishlistDao.getWishlistItems(),
        productDao.getAllProducts()
    ) { wishlistItems, products ->
        val wishlistIds = wishlistItems.map { it.productId }.toSet()
        products.filter { it.id in wishlistIds }
    }

    fun isWishlistedFlow(productId: String): Flow<Boolean> = wishlistDao.isWishlistedFlow(productId)

    suspend fun isWishlisted(productId: String): Boolean = wishlistDao.isWishlisted(productId)

    suspend fun toggleWishlist(productId: String) {
        if (wishlistDao.isWishlisted(productId)) {
            wishlistDao.deleteWishlistItem(productId)
        } else {
            wishlistDao.insertWishlistItem(WishlistItemEntity(productId))
        }
    }

    suspend fun addToCart(productId: String, size: String, color: String, quantity: Int) {
        val existingItems = cartDao.getCartItemsList()
        val match = existingItems.find { it.productId == productId && it.size == size && it.color == color }
        if (match != null) {
            match.quantity += quantity
            cartDao.updateCartItem(match)
        } else {
            cartDao.insertCartItem(CartItemEntity(productId = productId, size = size, color = color, quantity = quantity))
        }
    }

    suspend fun updateCartQuantity(cartId: Int, quantity: Int) {
        val existingItems = cartDao.getCartItemsList()
        val match = existingItems.find { it.id == cartId }
        if (match != null) {
            if (quantity <= 0) {
                cartDao.deleteCartItem(match)
            } else {
                match.quantity = quantity
                cartDao.updateCartItem(match)
            }
        }
    }

    suspend fun deleteCartItem(cartItem: CartItemEntity) {
        cartDao.deleteCartItem(cartItem)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    // Orders
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()

    suspend fun placeOrder(items: List<CartProduct>, totalAmount: Double, address: String) {
        val itemsStr = items.joinToString("; ") { "${it.product?.title ?: "Item"} (${it.cartItem.size}, ${it.cartItem.color}) x${it.cartItem.quantity}" }
        val order = OrderEntity(
            id = "NV-" + (100000..999999).random().toString(),
            date = System.currentTimeMillis(),
            totalAmount = totalAmount,
            status = "Processing",
            itemsJson = itemsStr
        )
        orderDao.insertOrder(order)
        cartDao.clearCart()

        // Award loyalty points - 1 point per dollar spent
        val earnedPoints = totalAmount.toInt()
        val currentProfile = getProfileImmediate()
        val newPoints = (currentProfile?.loyaltyPoints ?: 0) + earnedPoints
        styleProfileDao.insertProfile(
            currentProfile?.copy(loyaltyPoints = newPoints) ?: StyleProfileEntity(loyaltyPoints = newPoints)
        )
    }

    // Style Profile
    val styleProfile: Flow<StyleProfileEntity> = styleProfileDao.getProfile().map { it ?: StyleProfileEntity() }

    suspend fun getProfileImmediate(): StyleProfileEntity? {
        return styleProfileDao.getProfileImmediate()
    }

    suspend fun updateStyleProfile(profile: StyleProfileEntity) {
        styleProfileDao.insertProfile(profile)
    }

    // Moderation/Admin and Seller actions
    suspend fun addCustomProduct(product: ProductEntity) {
        productDao.insertProduct(product)
    }

    suspend fun updateProductStock(productId: String, newStock: Int) {
        val p = productDao.getProductById(productId)
        if (p != null) {
            productDao.insertProduct(p.copy(stock = newStock))
        }
    }

    suspend fun deleteProduct(productId: String) {
        val p = productDao.getProductById(productId)
        if (p != null) {
            productDao.deleteProduct(p)
        }
    }
}
