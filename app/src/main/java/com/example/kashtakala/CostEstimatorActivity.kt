package com.example.kashtakala

// ═══════════════════════════════════════════════════════════════════════
//  CostEstimatorActivity.kt
// ═══════════════════════════════════════════════════════════════════════
//
//  WHAT THIS SCREEN DOES — step by step:
//  ┌──────────────────────────────────────────────────────────────────┐
//  │  1. User types Material Cost  (e.g. ₹15,000)                    │
//  │  2. User types Labor Cost     (e.g. ₹8,000)                     │
//  │  3. Taps "Calculate Total Cost"                                  │
//  │  4. App validates both inputs (not blank, valid number, > 0)     │
//  │  5. Calculates:  Total = Material + Labor                        │
//  │  6. Shows result card with:                                      │
//  │       • Big total amount  (₹23,000.00)                          │
//  │       • Two breakdown chips (material / labor)                   │
//  │       • Animated split bar  (purple = material %, grey = labor%) │
//  │  7. "Clear" resets everything with a smooth fade-out             │
//  └──────────────────────────────────────────────────────────────────┘
//
//  BEGINNER CONCEPTS USED:
//  ┌────────────────────────┬─────────────────────────────────────────┐
//  │  Double                │ Kotlin decimal number type              │
//  │  toDoubleOrNull()      │ Safe text → number, returns null on fail│
//  │  String.format()       │ Formats numbers with commas + decimals  │
//  │  View.VISIBLE/GONE     │ Shows / completely hides a view         │
//  │  ValueAnimator         │ Smoothly animates a number from A to B  │
//  │  ViewGroup.LayoutParams│ Changes a view's width at runtime       │
//  │  addTextChangedListener│ Fires a block every time text changes   │
//  └────────────────────────┴─────────────────────────────────────────┘
// ═══════════════════════════════════════════════════════════════════════

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import java.util.Locale

class CostEstimatorActivity : AppCompatActivity() {

    // ── 1. VIEW REFERENCES ────────────────────────────────────────────────────
    // Declared here so every function in the class can use them.
    // Assigned in onCreate() after setContentView() loads the XML.
    private lateinit var tilMaterialCost    : TextInputLayout
    private lateinit var tilLaborCost       : TextInputLayout
    private lateinit var etMaterialCost     : TextInputEditText
    private lateinit var etLaborCost        : TextInputEditText
    private lateinit var btnEstimate        : MaterialButton
    private lateinit var btnClear           : MaterialButton

    // Result card and its child views
    private lateinit var cardResult         : MaterialCardView
    private lateinit var tvTotalCost        : TextView
    private lateinit var tvBreakdownFormula : TextView
    private lateinit var tvMaterialChip     : TextView
    private lateinit var tvLaborChip        : TextView
    private lateinit var tvMaterialPct      : TextView
    private lateinit var tvLaborPct         : TextView
    private lateinit var viewMaterialBar    : View   // the purple fill strip

    // ── 2. onCreate ───────────────────────────────────────────────────────────
    // Android calls this when the screen is first created.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cost_estimator)

        // Enable the ← back arrow in the toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ── 3. Bind all views ─────────────────────────────────────────────────
        tilMaterialCost    = findViewById(R.id.tilMaterialCost)
        tilLaborCost       = findViewById(R.id.tilLaborCost)
        etMaterialCost     = findViewById(R.id.etMaterialCost)
        etLaborCost        = findViewById(R.id.etLaborCost)
        btnEstimate        = findViewById(R.id.btnEstimate)
        btnClear           = findViewById(R.id.btnClear)
        cardResult         = findViewById(R.id.cardResult)
        tvTotalCost        = findViewById(R.id.tvTotalCost)
        tvBreakdownFormula = findViewById(R.id.tvBreakdownFormula)
        tvMaterialChip     = findViewById(R.id.tvMaterialChip)
        tvLaborChip        = findViewById(R.id.tvLaborChip)
        tvMaterialPct      = findViewById(R.id.tvMaterialPct)
        tvLaborPct         = findViewById(R.id.tvLaborPct)
        viewMaterialBar    = findViewById(R.id.viewMaterialBar)

        // ── 4. Live error clearing ────────────────────────────────────────────
        // As soon as the user types anything, clear the red error beneath
        // that field — gives immediate reassurance that they're fixing it.
        etMaterialCost.addTextChangedListener { tilMaterialCost.error = null }
        etLaborCost.addTextChangedListener    { tilLaborCost.error    = null }

        // ── 5. Button click listeners ─────────────────────────────────────────
        btnEstimate.setOnClickListener { handleEstimate() }
        btnClear.setOnClickListener    { handleClear()    }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  handleEstimate()
    //  Orchestrates: read → validate → calculate → display
    // ══════════════════════════════════════════════════════════════════════════
    private fun handleEstimate() {

        // 1. Dismiss keyboard so the result card is fully visible
        hideKeyboard()

        // 2. Read text from both fields, stripping surrounding whitespace
        val materialText = etMaterialCost.text.toString().trim()
        val laborText    = etLaborCost.text.toString().trim()

        // 3. Validate — stop here if anything fails
        if (!validateInputs(materialText, laborText)) return

        // 4. Parse text → Double
        //    Safe because validateInputs() already confirmed they're valid numbers
        val materialCost = materialText.toDouble()
        val laborCost    = laborText.toDouble()

        // 5. THE FORMULA ───────────────────────────────────────────────────────
        //    Total Cost = Material Cost + Labor Cost
        val totalCost = materialCost + laborCost

        // 6. Show the result
        displayResult(materialCost, laborCost, totalCost)
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  validateInputs()
    //  Returns TRUE if both inputs are valid, FALSE otherwise.
    //  Sets appropriate error messages directly on the TextInputLayouts.
    //
    //  VALIDATION RULES:
    //   1. Not empty
    //   2. Parseable as a positive Double
    //   3. Greater than zero  (0-cost project makes no sense)
    //   4. Reasonable upper bound  (₹10 crore max)
    // ══════════════════════════════════════════════════════════════════════════
    private fun validateInputs(materialText: String, laborText: String): Boolean {
        var isValid = true

        // ── MATERIAL COST ──────────────────────────────────────────────────────
        when {
            materialText.isEmpty() -> {
                tilMaterialCost.error = "⚠ Material cost is required"
                isValid = false
            }
            materialText.toDoubleOrNull() == null || materialText.toDouble() <= 0 -> {
                tilMaterialCost.error = "⚠ Enter a valid amount greater than zero"
                isValid = false
            }
            materialText.toDouble() > 10_000_000 -> {           // ₹1 crore max
                tilMaterialCost.error = "⚠ Amount exceeds maximum limit (₹1,00,00,000)"
                isValid = false
            }
        }

        // ── LABOR COST ─────────────────────────────────────────────────────────
        when {
            laborText.isEmpty() -> {
                tilLaborCost.error = "⚠ Labor cost is required"
                isValid = false
            }
            laborText.toDoubleOrNull() == null || laborText.toDouble() <= 0 -> {
                tilLaborCost.error = "⚠ Enter a valid amount greater than zero"
                isValid = false
            }
            laborText.toDouble() > 10_000_000 -> {
                tilLaborCost.error = "⚠ Amount exceeds maximum limit (₹1,00,00,000)"
                isValid = false
            }
        }

        return isValid
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  displayResult()
    //  Fills the result card and animates it into view.
    //
    //  Extra features beyond just showing the total:
    //  • Breakdown chips: individual material and labor amounts
    //  • Percentage labels: "Material 65%  Labor 35%"
    //  • Animated split bar: purple width proportional to material %
    //  • Count-up animation on the total amount (feels dynamic)
    // ══════════════════════════════════════════════════════════════════════════
    private fun displayResult(material: Double, labor: Double, total: Double) {

        // ── Format currency with Indian Rupee locale ───────────────────────────
        // NumberFormat.getCurrencyInstance gives "₹1,23,456.00" style formatting
        val inr = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

        val totalFormatted    = inr.format(total)
        val materialFormatted = inr.format(material)
        val laborFormatted    = inr.format(labor)

        // ── Calculate percentages ──────────────────────────────────────────────
        val materialPct = ((material / total) * 100).toInt()  // e.g. 65
        val laborPct    = 100 - materialPct                   // e.g. 35

        // ── Fill TextViews ─────────────────────────────────────────────────────
        tvBreakdownFormula.text = "$materialFormatted  +  $laborFormatted"
        tvMaterialChip.text     = materialFormatted
        tvLaborChip.text        = laborFormatted
        tvMaterialPct.text      = "Material $materialPct%"
        tvLaborPct.text         = "Labor $laborPct%"

        // ── Animate count-up on total ──────────────────────────────────────────
        // ValueAnimator counts from 0 → total over 800 ms.
        // On every tick it re-formats the current animated value and
        // sets it as the text, creating a rolling-counter effect.
        ValueAnimator.ofFloat(0f, total.toFloat()).apply {
            duration = 800
            addUpdateListener { animator ->
                val current = animator.animatedValue as Float
                tvTotalCost.text = inr.format(current.toDouble())
            }
            start()
        }

        // ── Animate split bar ──────────────────────────────────────────────────
        // The bar container's width is only known AFTER layout, so we use
        // post {} to run our code after the first layout pass.
        viewMaterialBar.post {
            val parentWidth  = (viewMaterialBar.parent as View).width
            val targetWidth  = ((materialPct / 100f) * parentWidth).toInt()

            // Animate bar width from 0 → targetWidth over 900 ms
            ValueAnimator.ofInt(0, targetWidth).apply {
                duration = 900
                addUpdateListener { animator ->
                    val params = viewMaterialBar.layoutParams
                    params.width = animator.animatedValue as Int
                    viewMaterialBar.layoutParams = params
                }
                start()
            }
        }

        // ── Show result card ───────────────────────────────────────────────────
        if (cardResult.visibility == View.GONE) {
            // First time: fade + slide up into view
            cardResult.visibility   = View.VISIBLE
            cardResult.alpha        = 0f
            cardResult.translationY = 60f
            cardResult.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .start()
        } else {
            // Already visible: pulse the total to signal update
            tvTotalCost.animate()
                .scaleX(1.1f).scaleY(1.1f).setDuration(140)
                .withEndAction {
                    tvTotalCost.animate().scaleX(1f).scaleY(1f).setDuration(160).start()
                }
                .start()
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  handleClear()
    //  Resets every input field, error, and result back to initial state.
    // ══════════════════════════════════════════════════════════════════════════
    private fun handleClear() {

        // Clear text fields
        etMaterialCost.text?.clear()
        etLaborCost.text?.clear()

        // Remove error messages
        tilMaterialCost.error = null
        tilLaborCost.error    = null

        // Reset split bar width to 0
        val params   = viewMaterialBar.layoutParams
        params.width = 0
        viewMaterialBar.layoutParams = params

        // Animate result card out, then hide it
        if (cardResult.visibility == View.VISIBLE) {
            cardResult.animate()
                .alpha(0f)
                .translationY(40f)
                .setDuration(280)
                .withEndAction {
                    cardResult.visibility   = View.GONE
                    cardResult.alpha        = 1f    // reset for next show
                    cardResult.translationY = 0f
                }
                .start()
        }

        // Return focus to first field
        etMaterialCost.requestFocus()
    }

    // ── hideKeyboard() ────────────────────────────────────────────────────────
    // Dismisses the on-screen keyboard when the user taps Calculate.
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let { imm.hideSoftInputFromWindow(it.windowToken, 0) }
    }

    // ── Back arrow handler ────────────────────────────────────────────────────
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
