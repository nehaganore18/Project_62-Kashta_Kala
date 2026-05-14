package com.example.kashtakala

// ═══════════════════════════════════════════════════════════════════════
//  MaterialCalculatorActivity.kt
// ═══════════════════════════════════════════════════════════════════════
//
//  WHAT THIS SCREEN DOES:
//  ┌──────────────────────────────────────────────────────────────────┐
//  │  1. User types Length and Width (in feet)                        │
//  │  2. Taps "Calculate Area"                                        │
//  │  3. App validates inputs (not empty, not zero, not negative)     │
//  │  4. Calculates:  Area = Length × Width                           │
//  │  5. Animates the result card into view                           │
//  │  6. Displays area in sq ft + echoes Length and Width used        │
//  │  "Clear" button resets everything back to empty state            │
//  └──────────────────────────────────────────────────────────────────┘
//
//  KEY CONCEPTS FOR BEGINNERS:
//  ┌─────────────────┬────────────────────────────────────────────────┐
//  │ TextInputLayout │ Wrapper around EditText; shows error messages  │
//  │ Double          │ Kotlin number type that handles decimals       │
//  │ toDoubleOrNull()│ Safely converts text → number (null if fails) │
//  │ View.VISIBLE    │ Makes a hidden view appear on screen           │
//  │ View.GONE       │ Hides view and removes its space in layout     │
//  │ animate()       │ Built-in Kotlin/Android animation shortcut     │
//  └─────────────────┴────────────────────────────────────────────────┘
// ═══════════════════════════════════════════════════════════════════════

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.button.MaterialButton
import android.widget.TextView

class MaterialCalculatorActivity : AppCompatActivity() {

    // ── 1. VIEW REFERENCES ────────────────────────────────────────────────────
    // 'lateinit var' = declared now, assigned in onCreate.
    // We reference every interactive view we need to read or update.
    private lateinit var tilLength       : TextInputLayout
    private lateinit var tilWidth        : TextInputLayout
    private lateinit var etLength        : TextInputEditText
    private lateinit var etWidth         : TextInputEditText
    private lateinit var btnCalculate    : MaterialButton
    private lateinit var btnClear        : MaterialButton
    private lateinit var cardResult      : MaterialCardView
    private lateinit var tvResult        : TextView
    private lateinit var tvFormulaSummary: TextView
    private lateinit var tvUsedLength    : TextView
    private lateinit var tvUsedWidth     : TextView

    // ── 2. onCreate ───────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material_calculator)

        // Enable the back arrow on the toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ── 3. Bind views ─────────────────────────────────────────────────────
        tilLength        = findViewById(R.id.tilLength)
        tilWidth         = findViewById(R.id.tilWidth)
        etLength         = findViewById(R.id.etLength)
        etWidth          = findViewById(R.id.etWidth)
        btnCalculate     = findViewById(R.id.btnCalculate)
        btnClear         = findViewById(R.id.btnClear)
        cardResult       = findViewById(R.id.cardResult)
        tvResult         = findViewById(R.id.tvResult)
        tvFormulaSummary = findViewById(R.id.tvFormulaSummary)
        tvUsedLength     = findViewById(R.id.tvUsedLength)
        tvUsedWidth      = findViewById(R.id.tvUsedWidth)

        // ── 4. Live error clearing ────────────────────────────────────────────
        // As soon as the user starts typing, clear the red error message.
        // This gives immediate positive feedback — "you're fixing it!"
        etLength.addTextChangedListener { tilLength.error = null }
        etWidth.addTextChangedListener  { tilWidth.error  = null }

        // ── 5. Button listeners ───────────────────────────────────────────────
        btnCalculate.setOnClickListener { handleCalculate() }
        btnClear.setOnClickListener     { handleClear()     }
    }

    // ── handleCalculate() ─────────────────────────────────────────────────────
    // Central function: validate → calculate → display.
    // Called every time the user taps "Calculate Area".
    private fun handleCalculate() {

        // Step A: Hide the keyboard so the result isn't obscured
        hideKeyboard()

        // Step B: Read raw text from both fields (trim removes leading/trailing spaces)
        val lengthText = etLength.text.toString().trim()
        val widthText  = etWidth.text.toString().trim()

        // Step C: Validate — returns false and shows errors if anything is wrong
        if (!validateInputs(lengthText, widthText)) return

        // Step D: Convert text → Double (decimal number)
        //         toDouble() is safe here because validateInputs() already
        //         confirmed the text is a valid positive number.
        val length = lengthText.toDouble()
        val width  = widthText.toDouble()

        // Step E: THE FORMULA  ───────────────────────────────────────────────
        //         Area = Length × Width
        //         Simple multiplication — the core of the whole screen!
        val area = length * width

        // Step F: Format and display the result
        displayResult(length, width, area)
    }

    // ── validateInputs() ──────────────────────────────────────────────────────
    // Checks every possible bad input before we try to calculate.
    // Returns TRUE if everything is fine, FALSE if there's a problem.
    //
    // WHY VALIDATE?
    //   Without validation, a blank field would crash the app because
    //   you can't convert "" (empty string) to a number.
    //
    // VALIDATION RULES:
    //   Rule 1 – Not empty            : field must not be blank
    //   Rule 2 – Valid number         : must convert to a Double successfully
    //   Rule 3 – Greater than zero    : 0 and negative dimensions make no sense
    //   Rule 4 – Reasonable max       : prevents absurd inputs like 999999 ft
    private fun validateInputs(lengthText: String, widthText: String): Boolean {

        var isValid = true  // assume valid; flip to false on any rule failure

        // ── VALIDATE LENGTH ───────────────────────────────────────────────────
        when {
            // Rule 1: Empty check
            lengthText.isEmpty() -> {
                tilLength.error = "⚠ Length is required"
                isValid = false
            }
            // Rule 2 + 3: Number check and zero/negative check
            // toDoubleOrNull() returns null if text isn't a valid number
            lengthText.toDoubleOrNull() == null || lengthText.toDouble() <= 0 -> {
                tilLength.error = "⚠ Enter a valid positive number"
                isValid = false
            }
            // Rule 4: Maximum reasonable size (1000 ft)
            lengthText.toDouble() > 1000 -> {
                tilLength.error = "⚠ Value seems too large (max 1000 ft)"
                isValid = false
            }
        }

        // ── VALIDATE WIDTH ────────────────────────────────────────────────────
        when {
            widthText.isEmpty() -> {
                tilWidth.error = "⚠ Width is required"
                isValid = false
            }
            widthText.toDoubleOrNull() == null || widthText.toDouble() <= 0 -> {
                tilWidth.error = "⚠ Enter a valid positive number"
                isValid = false
            }
            widthText.toDouble() > 1000 -> {
                tilWidth.error = "⚠ Value seems too large (max 1000 ft)"
                isValid = false
            }
        }

        return isValid
    }

    // ── displayResult() ───────────────────────────────────────────────────────
    // Populates the result card and animates it into view.
    //
    // PARAMETERS:
    //   length – the validated length value
    //   width  – the validated width value
    //   area   – calculated area (length × width)
    private fun displayResult(length: Double, width: Double, area: Double) {

        // Format numbers:
        //   "%.2f".format(x) → rounds to 2 decimal places
        //   e.g. 12.3456 → "12.35"
        val areaFormatted   = "%.2f sq ft".format(area)
        val lengthFormatted = "%.2f ft".format(length)
        val widthFormatted  = "%.2f ft".format(width)

        // Fill the result TextViews with formatted values
        tvResult.text         = areaFormatted
        tvFormulaSummary.text = "%.2f ft  ×  %.2f ft".format(length, width)
        tvUsedLength.text     = lengthFormatted
        tvUsedWidth.text      = widthFormatted

        // ── ANIMATE RESULT CARD IN ────────────────────────────────────────────
        // If card was hidden (GONE), show it with a smooth slide-up + fade-in.
        if (cardResult.visibility == View.GONE) {
            cardResult.visibility  = View.VISIBLE
            cardResult.alpha       = 0f       // start transparent
            cardResult.translationY = 50f     // start 50dp below final position
            cardResult.animate()
                .alpha(1f)                    // fade to fully visible
                .translationY(0f)             // slide up to normal position
                .setDuration(450)
                .start()
        } else {
            // Card is already visible — just pulse the result text to signal update
            tvResult.animate()
                .scaleX(1.12f).scaleY(1.12f)
                .setDuration(150)
                .withEndAction {
                    tvResult.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                }
                .start()
        }
    }

    // ── handleClear() ─────────────────────────────────────────────────────────
    // Resets the entire screen back to initial empty state.
    // Called when the user taps the "Clear" button.
    private fun handleClear() {

        // Clear all text fields
        etLength.text?.clear()
        etWidth.text?.clear()

        // Remove any lingering error messages
        tilLength.error = null
        tilWidth.error  = null

        // Hide the result card with a fade-out animation
        if (cardResult.visibility == View.VISIBLE) {
            cardResult.animate()
                .alpha(0f)
                .translationY(30f)
                .setDuration(250)
                .withEndAction {
                    cardResult.visibility   = View.GONE
                    cardResult.alpha        = 1f   // reset for next show
                    cardResult.translationY = 0f
                }
                .start()
        }

        // Move focus back to length field and show keyboard
        etLength.requestFocus()
    }

    // ── hideKeyboard() ────────────────────────────────────────────────────────
    // Dismisses the soft keyboard when Calculate is tapped so the result
    // card isn't hidden behind it.
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let { imm.hideSoftInputFromWindow(it.windowToken, 0) }
    }

    // ── Back arrow handler ────────────────────────────────────────────────────
    // Called when the user taps the ← back arrow in the toolbar.
    override fun onSupportNavigateUp(): Boolean {
        finish()   // close this Activity and return to the previous screen
        return true
    }
}
