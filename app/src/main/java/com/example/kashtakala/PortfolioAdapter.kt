package com.example.kashtakala

// ═══════════════════════════════════════════════════════════════════
//  PortfolioAdapter.kt  —  RECYCLERVIEW ADAPTER  (no logic changes)
// ═══════════════════════════════════════════════════════════════════
//
//  IMAGE LOADING:
//  holder.image.setImageResource(item.imageResId)
//  This single line loads the local drawable.
//  imageResId is an Int like R.drawable.img_sofa — Android resolves
//  it to the actual file res/drawable/img_sofa.jpg at runtime.
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class PortfolioAdapter(
    private val itemList      : List<PortfolioItem>,
    private val onItemClick   : (PortfolioItem) -> Unit,
    private val onDeleteClick : ((PortfolioItem) -> Unit)? = null
) : RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {

    inner class PortfolioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card           : MaterialCardView = itemView.findViewById(R.id.cardPortfolioItem)
        val image          : ImageView        = itemView.findViewById(R.id.ivPortfolioImage)
        val projectName    : TextView         = itemView.findViewById(R.id.tvProjectName)
        val description    : TextView         = itemView.findViewById(R.id.tvDescription)
        val category       : TextView         = itemView.findViewById(R.id.tvPortfolioCategory)
        val completionDate : TextView         = itemView.findViewById(R.id.tvCompletionDate)
        val clientName     : TextView         = itemView.findViewById(R.id.tvClientName)
        val btnDetails     : MaterialButton   = itemView.findViewById(R.id.btnViewDetails)
        val btnDelete      : MaterialButton   = itemView.findViewById(R.id.btnDeletePortfolioItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_portfolio, parent, false)
        return PortfolioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        val item = itemList[position]

        // ── Load local drawable image ──────────────────────────────────
        // setImageResource() is Android's built-in method for local images.
        // No library, no internet, no cache — just fast, offline display.
        if (item.imageResId != 0) {
            holder.image.setImageResource(item.imageResId)
        } else if (item.imageUri != null) {
            holder.image.setImageURI(android.net.Uri.parse(item.imageUri))
        }

        // Fill text fields
        holder.projectName.text    = item.projectName
        holder.description.text    = item.description
        holder.category.text       = item.category
        holder.completionDate.text = "📅 ${item.completionDate}"
        holder.clientName.text     = item.clientName

        // Entrance animation
        holder.itemView.alpha        = 0f
        holder.itemView.translationY = 55f
        holder.itemView.animate()
            .alpha(1f).translationY(0f).setDuration(420)
            .setStartDelay(position.coerceAtMost(5) * 75L).start()

        // Tap handlers
        holder.card.setOnClickListener      { bounceView(holder.card); onItemClick(item) }
        holder.btnDetails.setOnClickListener { bounceView(holder.card); onItemClick(item) }

        // ── Admin: Delete ──────────────────────────────────────────────
        if (onDeleteClick != null) {
            holder.btnDelete.visibility = View.VISIBLE
            holder.btnDelete.setOnClickListener {
                onDeleteClick.invoke(item)
            }
        } else {
            holder.btnDelete.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = itemList.size

    private fun bounceView(view: View) {
        val downX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.96f).apply { duration = 80 }
        val downY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.96f).apply { duration = 80 }
        val upX   = ObjectAnimator.ofFloat(view, "scaleX", 0.96f, 1f).apply {
            duration = 340; interpolator = OvershootInterpolator(2f) }
        val upY   = ObjectAnimator.ofFloat(view, "scaleY", 0.96f, 1f).apply {
            duration = 340; interpolator = OvershootInterpolator(2f) }
        AnimatorSet().apply {
            play(downX).with(downY); play(upX).with(upY).after(downX); start()
        }
    }
}
