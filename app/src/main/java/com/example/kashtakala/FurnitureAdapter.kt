package com.example.kashtakala

// ═══════════════════════════════════════════════════════════════════
//  FurnitureAdapter.kt  —  RECYCLERVIEW ADAPTER  (no logic changes)
// ═══════════════════════════════════════════════════════════════════
//
//  HOW IMAGES ARE LOADED (local drawables — no internet needed):
//
//  1. FurnitureData.kt stores  imageResId = R.drawable.img_sofa
//  2. This adapter receives that Int in onBindViewHolder
//  3. It calls:  holder.image.setImageResource(furniture.imageResId)
//  4. Android finds  res/drawable/img_sofa.jpg  and displays it
//
//  setImageResource()  is the standard Android method for loading
//  local drawable images — no library, no internet, works offline.
// ═══════════════════════════════════════════════════════════════════

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class FurnitureAdapter(
    private val furnitureList : List<Furniture>,
    private val onItemClick   : (Furniture) -> Unit
) : RecyclerView.Adapter<FurnitureAdapter.FurnitureViewHolder>() {

    // ViewHolder caches every view reference for fast scrolling
    inner class FurnitureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card     : MaterialCardView = itemView.findViewById(R.id.cardFurniture)
        val image    : ImageView        = itemView.findViewById(R.id.ivFurnitureImage)
        val name     : TextView         = itemView.findViewById(R.id.tvFurnitureName)
        val price    : TextView         = itemView.findViewById(R.id.tvPrice)
        val category : TextView         = itemView.findViewById(R.id.tvCategory)
    }

    // Step 1 — inflate the card XML into real View objects
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FurnitureViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_furniture, parent, false)
        return FurnitureViewHolder(view)
    }

    // Step 2 — fill each card with data when it scrolls into view
    override fun onBindViewHolder(holder: FurnitureViewHolder, position: Int) {
        val furniture = furnitureList[position]

        // ── Load the LOCAL drawable image ──────────────────────────────
        // setImageResource() looks up the integer ID in res/drawable/
        // and displays the matching .jpg / .png file — no network needed.
        holder.image.setImageResource(furniture.imageResId)

        // Fill text fields
        holder.name.text     = furniture.name
        holder.price.text    = furniture.price
        holder.category.text = furniture.category

        // Entrance animation: fade + slide up
        holder.itemView.alpha        = 0f
        holder.itemView.translationY = 40f
        holder.itemView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(380)
            .setStartDelay(position.coerceAtMost(5) * 60L)
            .start()

        // Card tap → bounce animation + callback to Activity
        holder.card.setOnClickListener {
            bounceView(holder.card)
            onItemClick(furniture)
        }
    }

    // Step 3 — tell RecyclerView how many items to draw
    override fun getItemCount(): Int = furnitureList.size

    // Satisfying press-and-spring animation on tap
    private fun bounceView(view: View) {
        val downX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.96f).apply { duration = 80 }
        val downY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.96f).apply { duration = 80 }
        val upX   = ObjectAnimator.ofFloat(view, "scaleX", 0.96f, 1f).apply {
            duration = 320; interpolator = OvershootInterpolator(2f) }
        val upY   = ObjectAnimator.ofFloat(view, "scaleY", 0.96f, 1f).apply {
            duration = 320; interpolator = OvershootInterpolator(2f) }
        AnimatorSet().apply {
            play(downX).with(downY); play(upX).with(upY).after(downX); start()
        }
    }
}
