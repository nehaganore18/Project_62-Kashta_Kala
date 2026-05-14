package com.example.kashtakala

// ═══════════════════════════════════════════════════════════════════
//  FurnitureCatalogActivity.kt  —  UPDATED
// ═══════════════════════════════════════════════════════════════════
//
//  WHAT CHANGED FROM THE PREVIOUS VERSION:
//  ─────────────────────────────────────────────────────────────────
//  Before → dummy data was written INSIDE this Activity
//  After  → this Activity simply calls FurnitureData.getCatalogItems()
//
//  All image references (R.drawable.img_sofa, etc.) live in
//  FurnitureData.kt — one file to edit for all changes.
// ═══════════════════════════════════════════════════════════════════

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FurnitureCatalogActivity : AppCompatActivity() {

    private lateinit var recyclerView     : RecyclerView
    private lateinit var furnitureAdapter : FurnitureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_furniture_catalog)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recyclerViewFurniture)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ── Load furniture list from the central data file ─────────────
        // R.drawable.img_sofa etc. are resolved by Android automatically
        // as long as the image files exist in res/drawable/
        val furnitureList = FurnitureData.getCatalogItems()

        furnitureAdapter = FurnitureAdapter(furnitureList) { tappedItem ->
            Toast.makeText(
                this,
                "${tappedItem.name}  ·  ${tappedItem.price}",
                Toast.LENGTH_SHORT
            ).show()
        }

        recyclerView.adapter = furnitureAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
