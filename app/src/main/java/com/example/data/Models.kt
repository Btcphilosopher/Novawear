package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val title: String,
    val brand: String,
    val description: String,
    val category: String, // tops, trousers, dresses, shoes, accessories
    val price: Double,
    val imageUrl: String,
    val videoUrl: String?,
    val sizes: String, // Comma separated e.g., "S,M,L"
    val colors: String, // Comma separated e.g., "Black,Beige"
    val styleTags: String, // Comma separated e.g., "minimalist,Y2K,grunge"
    val rating: Double,
    val reviewCount: Int,
    val stock: Int,
    val isTrending: Boolean,
    val isNewIn: Boolean,
    val sustainabilityScore: Int = 100 // 1-100 score
)

@Entity(tableName = "cart")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val size: String,
    val color: String,
    var quantity: Int
)

@Entity(tableName = "wishlist")
data class WishlistItemEntity(
    @PrimaryKey val productId: String
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val date: Long,
    val totalAmount: Double,
    val status: String, // "Processing", "Shipped", "Delivered"
    val itemsJson: String // Serialized items list
)

@Entity(tableName = "style_profile")
data class StyleProfileEntity(
    @PrimaryKey val id: String = "me",
    val genderPreference: String = "Unisex",
    val preferredStyles: String = "Minimalist", // Comma separated
    val preferredSizes: String = "M", // Comma separated
    val budgetRange: String = "Medium", // Low, Medium, High
    val loyaltyPoints: Int = 150
)
