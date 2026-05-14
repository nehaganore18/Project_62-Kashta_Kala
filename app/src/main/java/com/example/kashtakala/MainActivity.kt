package com.example.kashtakala

// ─────────────────────────────────────────────────────────────────────────────
//  MainActivity.kt
//  Home screen controller for Kashta-Kala.
//
//  WHAT THIS FILE DOES:
//  1. Inflates activity_main.xml as the screen.
//  2. Finds each card by its ID.
//  3. Attaches a click listener that navigates to the matching Activity.
//  4. Adds a small scale animation on tap to make the UI feel snappy.
//
//  BEGINNER TIP ──────────────────────────────────────────────────────────────
//  Every section is clearly labelled. Read top-to-bottom; each block builds
//  on the previous one. Comments explain the "why", not just the "what".
// ─────────────────────────────────────────────────────────────────────────────

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    // ── 1. onCreate ───────────────────────────────────────────────────────────
    // Android calls this when the screen is first created.
    // We inflate our XML layout here and wire up all the interactions.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ── 0. Check Login ───────────────────────────────────────────────────
        if (!UserManager.isLoggedIn(this)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)   // link this Kotlin file to activity_main.xml

        // Show welcome toast
        val role = if (UserManager.isAdmin(this)) "Admin" else "Customer"
        Toast.makeText(this, "Welcome! Logged in as $role", Toast.LENGTH_SHORT).show()

        // ── 2. Find views ─────────────────────────────────────────────────────
        // Each variable maps to one MaterialCardView in activity_main.xml.
        val cardFurnitureCatalog   : MaterialCardView = findViewById(R.id.cardFurnitureCatalog)
        val cardMaterialCalculator : MaterialCardView = findViewById(R.id.cardMaterialCalculator)
        val cardCostEstimator      : MaterialCardView = findViewById(R.id.cardCostEstimator)
        val cardPortfolio          : MaterialCardView = findViewById(R.id.cardPortfolio)

        // ── 3. Entrance animation ─────────────────────────────────────────────
        // Fade + slide the cards in one by one when the screen loads.
        // This gives the home screen a polished, alive feeling.
        val cards = listOf(cardFurnitureCatalog, cardMaterialCalculator,
                           cardCostEstimator,    cardPortfolio)
        cards.forEachIndexed { index, card ->
            card.alpha = 0f                   // start invisible
            card.translationY = 60f           // start slightly below final position
            card.animate()
                .alpha(1f)                    // fade to visible
                .translationY(0f)             // slide up to normal position
                .setDuration(400)             // 400 ms per card
                .setStartDelay(index * 100L)  // stagger: 0 ms, 100 ms, 200 ms, 300 ms
                .start()
        }

        // ── 4. Click listeners ────────────────────────────────────────────────
        // Each card has two jobs when tapped:
        //   a) Run a quick "bounce" animation so the user gets visual feedback.
        //   b) Open the target screen via an Intent.
        //
        // BEGINNER TIP: Intent is Android's way of saying "go to another screen".

        cardFurnitureCatalog.setOnClickListener { view ->
            bounceCard(view)
            startActivity(Intent(this, FurnitureCatalogActivity::class.java))
        }

        cardMaterialCalculator.setOnClickListener { view ->
            bounceCard(view)
            startActivity(Intent(this, MaterialCalculatorActivity::class.java))
        }

        cardCostEstimator.setOnClickListener { view ->
            bounceCard(view)
            startActivity(Intent(this, CostEstimatorActivity::class.java))
        }

        cardPortfolio.setOnClickListener { view ->
            bounceCard(view)
            startActivity(Intent(this, PortfolioActivity::class.java))
        }

        // ── 5. Logout (Optional convenience) ──────────────────────────────────
        findViewById<android.view.View>(R.id.headerBanner).setOnLongClickListener {
            UserManager.logout(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            true
        }
    }

    // ── 5. bounceCard() ───────────────────────────────────────────────────────
    // A satisfying press animation:
    //   • Quickly scales the card DOWN to 95% (feels like a physical press)
    //   • Then bounces back to 100% with an overshoot so it springs back
    //
    // Parameters:
    //   view  – the card that was tapped (passed in from the click listener)
    private fun bounceCard(view: View) {
        // Scale down (press)
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f).apply {
            duration = 80
        }
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f).apply {
            duration = 80
        }

        // Scale back up (release with spring)
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f).apply {
            duration = 300
            interpolator = OvershootInterpolator(2f)   // creates the spring overshoot
        }
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f).apply {
            duration = 300
            interpolator = OvershootInterpolator(2f)
        }

        // Play press first, then spring back
        AnimatorSet().apply {
            play(scaleDownX).with(scaleDownY)
            play(scaleUpX).with(scaleUpY).after(scaleDownX)
            start()
        }
    }

    // ── 6. showToast() ────────────────────────────────────────────────────────
    // Simple helper to display a short Toast message at the bottom of the screen.
    // BEGINNER TIP: Toast.LENGTH_SHORT shows for ~2 seconds.
    // Remove this once you connect real screens.
    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}
