package com.example.kashtakala

// ═══════════════════════════════════════════════════════════════
//  Furniture.kt  —  DATA MODEL
// ═══════════════════════════════════════════════════════════════
//
//  WHAT IS A DATA MODEL?
//  Think of it as a blueprint / template for one piece of furniture.
//  Every item in the catalog will be one "Furniture" object that holds:
//    • a name        (String  = text)
//    • a price       (String  = text like "₹12,500")
//    • a category    (String  = e.g. "Seating", "Storage")
//    • an imageResId (Int     = a reference number pointing to a
//                               drawable image stored in res/drawable/)
//
//  WHY "data class"?
//  Kotlin's `data class` automatically gives you:
//    ✔ equals()   – compare two objects
//    ✔ toString() – print objects nicely for debugging
//    ✔ copy()     – duplicate with small changes
//  Perfect for simple containers like this one.
// ═══════════════════════════════════════════════════════════════

data class Furniture(
    val name       : String,   // e.g. "Teak Wood Sofa"
    val price      : String,   // e.g. "₹28,000"
    val category   : String,   // e.g. "Seating"
    val imageResId : Int       // e.g. R.drawable.img_sofa
)
