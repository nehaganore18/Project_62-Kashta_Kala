package com.example.kashtakala

// ═══════════════════════════════════════════════════════════════════
//  PortfolioActivity.kt  —  UPDATED
// ═══════════════════════════════════════════════════════════════════
//
//  WHAT CHANGED:
//  Before → long getDummyPortfolioList() was inside this Activity
//  After  → calls FurnitureData.getPortfolioItems() instead
//
//  The portfolio uses the SAME 6 image files as the catalog,
//  so no extra images are needed.
// ═══════════════════════════════════════════════════════════════════

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

class PortfolioActivity : AppCompatActivity() {

    private lateinit var recyclerView     : RecyclerView
    private lateinit var portfolioAdapter : PortfolioAdapter
    
    private var selectedImageUri: Uri? = null
    private var ivSelectedPreview: ImageView? = null

    // ── Image Pickers ───────────────────────────────────────────────
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            ivSelectedPreview?.setImageURI(it)
            ivSelectedPreview?.setPadding(0, 0, 0, 0)
            ivSelectedPreview?.imageTintList = null
        }
    }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            // In a real app, you'd save this to a file and get a URI
            // For this demo, we'll just show the bitmap in the preview
            ivSelectedPreview?.setImageBitmap(it)
            ivSelectedPreview?.setPadding(0, 0, 0, 0)
            ivSelectedPreview?.imageTintList = null
            // For simplicity in this demo, we won't handle saving the camera bitmap to FurnitureData
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recyclerViewPortfolio)
        recyclerView.layoutManager    = LinearLayoutManager(this)
        recyclerView.isNestedScrollingEnabled = false

        // ── Load portfolio items from the central data file ────────────
        val portfolioList = FurnitureData.getPortfolioItems()

        portfolioAdapter = PortfolioAdapter(
            itemList = portfolioList,
            onItemClick = { tappedItem -> showDetailSheet(tappedItem) },
            onDeleteClick = if (UserManager.isAdmin(this)) { tappedItem ->
                FurnitureData.removePortfolioItem(tappedItem)
                portfolioAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Project deleted", Toast.LENGTH_SHORT).show()
            } else null
        )

        recyclerView.adapter = portfolioAdapter

        // ── Admin: Show "Add Work" button ─────────────────────────────
        val btnAddWork = findViewById<android.view.View>(R.id.btnAddWork)
        if (UserManager.isAdmin(this)) {
            btnAddWork.visibility = android.view.View.VISIBLE
            btnAddWork.setOnClickListener {
                showAddWorkSheet()
            }
        }
    }

    private fun showAddWorkSheet() {
        val sheet = BottomSheetDialog(this, R.style.BottomSheet_KashtaKala)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_add_work, null)

        val btnPickImage = view.findViewById<View>(R.id.btnPickImage)
        ivSelectedPreview = view.findViewById(R.id.ivSelectedImage)
        selectedImageUri = null // Reset

        btnPickImage.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery")
            AlertDialog.Builder(this)
                .setTitle("Select Image")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> takePhotoLauncher.launch(null)
                        1 -> pickImageLauncher.launch("image/*")
                    }
                }
                .show()
        }

        val etProject   = view.findViewById<EditText>(R.id.etProjectName)
        val etCategory  = view.findViewById<EditText>(R.id.etCategory)
        val etClient    = view.findViewById<EditText>(R.id.etClientName)
        val etDate      = view.findViewById<EditText>(R.id.etDate)
        val etDesc      = view.findViewById<EditText>(R.id.etDescription)
        val btnSubmit   = view.findViewById<MaterialButton>(R.id.btnSubmitWork)

        btnSubmit.setOnClickListener {
            val name = etProject.text.toString()
            val cat  = etCategory.text.toString()
            val cli  = etClient.text.toString()
            val date = etDate.text.toString()
            val desc = etDesc.text.toString()

            if (name.isNotEmpty() && cat.isNotEmpty() && cli.isNotEmpty() && date.isNotEmpty() && desc.isNotEmpty()) {
                val newItem = PortfolioItem(
                    projectName = name,
                    description = desc,
                    category = cat,
                    completionDate = date,
                    clientName = cli,
                    imageResId = if (selectedImageUri == null) R.drawable.img_placeholder else 0,
                    imageUri = selectedImageUri?.toString()
                )
                
                FurnitureData.addPortfolioItem(newItem)
                portfolioAdapter.notifyItemInserted(0)
                recyclerView.scrollToPosition(0)
                
                Toast.makeText(this, "Project added successfully!", Toast.LENGTH_SHORT).show()
                sheet.dismiss()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        sheet.setContentView(view)
        sheet.show()
    }

    // ── Bottom sheet: slides up with full project details on card tap ──
    private fun showDetailSheet(item: PortfolioItem) {
        val sheet     = BottomSheetDialog(this,
            R.style.BottomSheet_KashtaKala)
        val sheetView = layoutInflater.inflate(
            R.layout.bottom_sheet_portfolio_detail, null)

        // Fill sheet views with the tapped item's data
        val imageView = sheetView.findViewById<ImageView>(R.id.ivSheetImage)
        if (item.imageResId != 0) {
            imageView.setImageResource(item.imageResId)
        } else if (item.imageUri != null) {
            imageView.setImageURI(Uri.parse(item.imageUri))
        }

        sheetView.findViewById<TextView>(R.id.tvSheetProjectName).text = item.projectName
        sheetView.findViewById<TextView>(R.id.tvSheetCategory).text    = item.category
        sheetView.findViewById<TextView>(R.id.tvSheetDescription).text = item.description
        sheetView.findViewById<TextView>(R.id.tvSheetClient).text      = "👤  ${item.clientName}"
        sheetView.findViewById<TextView>(R.id.tvSheetDate).text        = "📅  ${item.completionDate}"

        sheet.setContentView(sheetView)
        sheet.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
