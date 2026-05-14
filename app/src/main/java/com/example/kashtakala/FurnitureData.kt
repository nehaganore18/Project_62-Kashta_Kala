package com.example.kashtakala

// ═══════════════════════════════════════════════════════════════════
//  FurnitureData.kt  —  ALL DUMMY DATA IN ONE PLACE
// ═══════════════════════════════════════════════════════════════════
//
//  WHY A SEPARATE FILE?
//  Keeping all dummy data here (instead of scattered across Activities)
//  means you only need to edit ONE file when you:
//    • Add a new furniture item
//    • Change a price or description
//    • Swap in a real image
//
//  HOW IMAGES WORK:
//  ┌─────────────────────────────────────────────────────────────────┐
//  │  1. You put  img_sofa.jpg  inside  res/drawable/               │
//  │  2. Android Studio generates  R.drawable.img_sofa  (an Int ID) │
//  │  3. You write  imageResId = R.drawable.img_sofa  here          │
//  │  4. The Adapter calls  imageView.setImageResource(imageResId)  │
//  │  5. Android loads the correct image automatically              │
//  └─────────────────────────────────────────────────────────────────┘
//
//  IMAGE FILENAMES TO CREATE (place in  app/src/main/res/drawable/):
//  ┌──────────────────────────┬────────────────────────────────────┐
//  │  Filename                │  Used for                          │
//  ├──────────────────────────┼────────────────────────────────────┤
//  │  img_sofa.jpg            │  Teak Wood 3-Seater Sofa           │
//  │  img_bed.jpg             │  King-Size Sheesham Bed            │
//  │  img_dining_table.jpg    │  6-Seater Dining Table             │
//  │  img_wardrobe.jpg        │  3-Door Sliding Wardrobe           │
//  │  img_office_chair.jpg    │  Ergonomic Office Chair            │
//  │  img_tv_unit.jpg         │  Mango Wood TV Unit                │
//  └──────────────────────────┴────────────────────────────────────┘
//
//  HOW TO ADD YOUR OWN IMAGES:
//  Step 1 → Find or download a furniture image (jpg or png)
//  Step 2 → Rename it using the filename from the table above
//           Rules: lowercase, no spaces, use underscores
//           ✅ img_sofa.jpg     ❌ Sofa Image.jpg    ❌ img-sofa.jpg
//  Step 3 → In Android Studio:
//           Right-click  res/drawable  → Open in Explorer (Windows)
//                                        or Reveal in Finder (Mac)
//  Step 4 → Copy/paste your renamed file into that folder
//  Step 5 → Switch back to Android Studio — it appears automatically
//  Step 6 → Rebuild: Build → Rebuild Project
//
//  RECOMMENDED FREE IMAGE SOURCES:
//  • https://unsplash.com   (search "sofa", "bed", etc.)
//  • https://www.pexels.com (free, high quality)
//  • Any image you photograph yourself
// ═══════════════════════════════════════════════════════════════════

object FurnitureData {

    // ── CATALOG DATA (used by FurnitureCatalogActivity) ────────────────
    // Returns a List of Furniture objects — one per card in the catalog.
    fun getCatalogItems(): List<Furniture> = listOf(

        // ── 1. SOFA ────────────────────────────────────────────────────
        Furniture(
            name       = "Teak Wood 3-Seater Sofa",
            price      = "₹32,000",
            category   = "Seating",
            imageResId = R.drawable.img_sofa
            // Place file:  res/drawable/img_sofa.jpg
        ),

        // ── 2. BED ─────────────────────────────────────────────────────
        Furniture(
            name       = "King-Size Sheesham Bed",
            price      = "₹45,500",
            category   = "Bedroom",
            imageResId = R.drawable.img_bed
            // Place file:  res/drawable/img_bed.jpg
        ),

        // ── 3. DINING TABLE ────────────────────────────────────────────
        Furniture(
            name       = "6-Seater Dining Table",
            price      = "₹38,000",
            category   = "Dining",
            imageResId = R.drawable.img_dining_table
            // Place file:  res/drawable/img_dining_table.jpg
        ),

        // ── 4. WARDROBE ────────────────────────────────────────────────
        Furniture(
            name       = "3-Door Sliding Wardrobe",
            price      = "₹52,000",
            category   = "Bedroom",
            imageResId = R.drawable.img_wardrobe
            // Place file:  res/drawable/img_wardrobe.jpg
        ),

        // ── 5. OFFICE CHAIR ────────────────────────────────────────────
        Furniture(
            name       = "Ergonomic Office Chair",
            price      = "₹12,500",
            category   = "Office",
            imageResId = R.drawable.img_office_chair
            // Place file:  res/drawable/img_office_chair.jpg
        ),

        // ── 6. TV UNIT ─────────────────────────────────────────────────
        Furniture(
            name       = "Mango Wood TV Unit",
            price      = "₹18,000",
            category   = "Living Room",
            imageResId = R.drawable.img_tv_unit
            // Place file:  res/drawable/img_tv_unit.jpg
        )
    )

    // ── PORTFOLIO DATA (used by PortfolioActivity) ─────────────────────
    // Returns a List of PortfolioItem objects — one per portfolio card.
    // Uses the SAME image files — no need to create extra images!
    private val portfolioList = mutableListOf(

        PortfolioItem(
            projectName    = "Royal Teak 3-Seater Sofa",
            description    = "Hand-crafted solid teak wood sofa with deep-set cushions and traditional carved armrests. Finished with Danish oil for a warm natural look that lasts decades.",
            category       = "Seating",
            completionDate = "March 2024",
            clientName     = "Sharma Family, Bengaluru",
            imageResId     = R.drawable.img_sofa
        ),

        PortfolioItem(
            projectName    = "Sheesham King Bed",
            description    = "A king-size bed in premium Sheesham (Indian Rosewood) with a hand-carved headboard featuring floral motifs. Soft-close storage drawers below the base.",
            category       = "Bedroom",
            completionDate = "January 2024",
            clientName     = "Reddy Household, Hyderabad",
            imageResId     = R.drawable.img_bed
        ),

        PortfolioItem(
            projectName    = "Solid Oak Dining Set",
            description    = "A 6-seater dining table and matching chairs in solid oak with a walnut stain. Mortise-and-tenon joinery ensures it handles family gatherings for a lifetime.",
            category       = "Dining",
            completionDate = "November 2023",
            clientName     = "Mehta Residence, Pune",
            imageResId     = R.drawable.img_dining_table
        ),

        PortfolioItem(
            projectName    = "3-Door Sliding Wardrobe",
            description    = "Floor-to-ceiling wardrobe in teak-veneer plywood with a full-length mirror on the centre door, soft-close hinges, and a velvet-lined jewellery section inside.",
            category       = "Bedroom",
            completionDate = "September 2023",
            clientName     = "Gupta Family, Jaipur",
            imageResId     = R.drawable.img_wardrobe
        ),

        PortfolioItem(
            projectName    = "Executive Office Chair",
            description    = "Ergonomic high-back chair with lumbar support cushion, breathable mesh back, adjustable armrests, and a solid aluminium star base. Designed for 8-hour workdays.",
            category       = "Office",
            completionDate = "July 2023",
            clientName     = "Nair & Co., Kochi",
            imageResId     = R.drawable.img_office_chair
        ),

        PortfolioItem(
            projectName    = "Mango Wood TV Unit",
            description    = "A 5-foot TV unit in solid mango wood with two open shelves, two closed cabinets with rattan-panel doors, and cable management slots at the back.",
            category       = "Living Room",
            completionDate = "May 2023",
            clientName     = "Iyer Residence, Chennai",
            imageResId     = R.drawable.img_tv_unit
        )
    )

    fun getPortfolioItems(): List<PortfolioItem> = portfolioList

    fun addPortfolioItem(item: PortfolioItem) {
        portfolioList.add(0, item)
    }

    fun removePortfolioItem(item: PortfolioItem) {
        portfolioList.remove(item)
    }
}
