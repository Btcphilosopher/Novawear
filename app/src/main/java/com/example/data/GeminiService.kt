package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// --- Moshi Gemini API Request Models ---

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

// --- Retrofit Setup ---

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

// --- Stylist Service Implementation ---

object GeminiStylistService {
    private const val SYSTEM_ROLE = """
You are Nova, the AI Stylist Assistant for NovaWear - the premium modern fashion marketplace for ages 16-35. 
Users will ask you for styling tips, outfit combinations (based on occasion, weather, size, or vibe), and style matching. 
Keep your tone highly fashionable, enthusiastic, inspiring, and concise. Act like an assistant editor at a high-end streetwear and fashion house.

CRITICAL: You have access to the NovaWear clothing catalog described below:
- 'p1': Aero Cargo Puff Parka by 'Studio Nova' ($145, Category: tops, Style: streetwear, techwear, minimalist, Colors: Matte Black, Sage Green)
- 'p2': Tailored Wool Trench Coat by 'Zara Atelier' ($220, Category: tops, Style: minimalist, formal, luxury, Colors: Camel Tan, Charcoal Gray)
- 'p3': Nova-Flex Chunky Platform Runners by 'NovaWear Sport' ($110, Category: shoes, Style: Y2K, streetwear, athleisure, Colors: Cyber White, Neon Pop)
- 'p4': Acid Wash Multi-Pocket Utility Cargos by 'SND Denim' ($89, Category: trousers, Style: Y2K, streetwear, vintage, Colors: Vintage Slate, Washed Indigo)
- 'p5': Whisper Satin Silk Slip Dress by 'Luna & Rose' ($160, Category: dresses, Style: minimalist, formal, luxury, Colors: Emerald Green, Champagne Shimmer)
- 'p6': Knit Retro Distressed Jumper by 'Studio Nova' ($78, Category: tops, Style: vintage, grunge, minimalist, Colors: Ash Beige, Chocolate Brown)
- 'p7': Hardware Chained Crossbody by 'Koren Design' ($65, Category: accessories, Style: minimalist, Y2K, grunge, Colors: Stealth Black, Chrome Silver)
- 'p8': NovaActive Ribbed Yoga Set by 'NovaWear Sport' ($95, Category: tops, Style: athleisure, minimalist, Colors: Dusty Rose, Onyx Slate)
- 'p9': Aesthetic Retro Rimless Shades by 'Koren Design' ($35, Category: accessories, Style: Y2K, streetwear, vintage, Colors: Sunset Fade, Midnight Smoke)
- 'p10': Linen Wide-Leg Summer Trousers by 'Zara Atelier' ($120, Category: trousers, Style: minimalist, formal, luxury, Colors: Plain Flax, Oyster White)
- 'p11': Heavyweight Tactical Denim Shacket by 'SND Denim' ($98, Category: tops, Style: vintage, streetwear, Colors: Washed Indigo, Jet Black)

When suggesting outfits, you MUST recommend actual items from our catalog using their exact product ID in square brackets, like [p1], [p4], or [p9]. The app automatically converts these tokens into beautiful interactive product links. 
Suggest full looks combining tops, trousers/dresses, shoes, and accessories from the catalogs.
For example: 'For a perfect streetwear vibe, combine our [p1] cargo parka with [p4] tactical denim cargos, finish with [p3] platform runners and the [p7] silver crossbody bag!'
Keep responses concise, typically 2 or 3 short paragraphs.
"""

    suspend fun chatWithStylist(conversation: List<Content>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Simulated backup response if API key is not configured yet
            return@withContext getMockStylistResponse(conversation.lastOrNull()?.parts?.firstOrNull()?.text ?: "")
        }

        val request = GenerateContentRequest(
            contents = conversation,
            generationConfig = GenerationConfig(temperature = 0.7f),
            systemInstruction = Content(parts = listOf(Part(text = SYSTEM_ROLE)))
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Hey there! I didn't catch that correctly. Could you please specify your preferred occasion or style vibe?"
        } catch (e: Exception) {
            // Backup styling response on check fail
            getMockStylistResponse(conversation.lastOrNull()?.parts?.firstOrNull()?.text ?: "")
        }
    }

    private fun getMockStylistResponse(userInput: String): String {
        val input = userInput.lowercase()
        return when {
            input.contains("streetwear") || input.contains("techwear") || input.contains("cargo") -> {
                "Hey styled tribe! If streetwear or techwear is your vibe, you've got to experience this ultimate combo. Start with our signature [p1] Aero Cargo Puff Parka for that structural volume, layer with [p11] heavy denim shacket underneath, pair with the distressed [p4] Acid Wash Multi-Pocket Cargos, and complete the aesthetic with [p3] Chunky Platform Runners. This is pure concrete edge. 🔥"
            }
            input.contains("formal") || input.contains("dress") || input.contains("luxury") || input.contains("minimalist") -> {
                "Minimalist luxury is the ultimate chic statement! I suggest starting with our fluid bias-cut [p5] Whisper Satin Silk Slip Dress in elegant Emerald. Throw over our beautifully tailored [p2] Wool Trench Coat in Camel for that editorial outerwear frame, and tie it together with the [p7] Hardware Chained Crossbody bag in Stealth Black. Pure premium energy. ✨"
            }
            input.contains("summer") || input.contains("hot") || input.contains("sun") || input.contains("beach") -> {
                "Keeping cool without losing high-density style is an art! I recommend combining the organic [p10] Linen Wide-Leg Summer Trousers with [p8] NovaActive top in Dusty Rose. Frame the face with our 90s butterfly-hinge [p9] Aesthetic Rimless Shades and drape the structured [p7] Crossbody bag on top. Effortless breezy cool!"
            }
            input.contains("shoes") || input.contains("runner") || input.contains("sneaker") -> {
                "Footwear defines the look! For high-contrast Y2K energy, the [p3] Nova-Flex Chunky Platform Runners are absolutely unmatched. Style them with [p4] Acid Wash denim cargos and a tucked-in distressed sweater like [p6] Knit Retro Distressed Jumper for a modern streetwear silhouette. 🙌"
            }
            else -> {
                "Welcome to NovaWear styling headquarters! I am Nova, your personal stylist. What fashion vibe or special occasion are we curation-mapping today?\n\nTell me if you are looking for *streetwear*, *minimalist*, *Y2K*, *vintage*, or *formal* outfits!"
            }
        }
    }
}
