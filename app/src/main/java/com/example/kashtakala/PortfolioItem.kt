package com.example.kashtakala

// ═══════════════════════════════════════════════════════════════════
//  PortfolioItem.kt  ─  DATA MODEL
// ═══════════════════════════════════════════════════════════════════
//
//  WHAT IS A DATA MODEL?
//  A blueprint / template describing ONE completed furniture project.
//  Every card shown in the Portfolio RecyclerView is ONE PortfolioItem.
//
//  FIELDS:
//  ┌──────────────────┬───────────────────────────────────────────────┐
//  │ projectName      │ Bold heading on the card  e.g. "Teak Sofa"   │
//  │ description      │ Short story about the project (1–2 sentences)│
//  │ category         │ Badge label  e.g. "Seating", "Bedroom"       │
//  │ completionDate   │ When finished  e.g. "March 2024"             │
//  │ clientName       │ Who it was made for  e.g. "Sharma Family"    │
//  │ imageResId       │ Points to a drawable image in res/drawable/  │
//  └──────────────────┴───────────────────────────────────────────────┘
//
//  WHY "data class"?
//  Kotlin auto-generates equals(), toString(), copy() — perfect for
//  simple objects that just hold data values.
// ═══════════════════════════════════════════════════════════════════

data class PortfolioItem(
    val projectName    : String,
    val description    : String,
    val category       : String,
    val completionDate : String,
    val clientName     : String,
    val imageResId     : Int = 0,        // e.g. R.drawable.img_sofa
    val imageUri       : String? = null  // For user-picked images
)
