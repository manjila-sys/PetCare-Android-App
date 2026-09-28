package com.manjila.petcare

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MyPetsActivity : AppCompatActivity() {

    private lateinit var database: UserDatabaseHelper
    private lateinit var petContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pets)

        database = UserDatabaseHelper(this)
        petContainer = findViewById(R.id.petContainer)

        val addPetButton = findViewById<Button>(R.id.btnAddPet)

        addPetButton.setOnClickListener {
            val intent = Intent(
                this,
                PetProfileActivity::class.java
            )
            startActivity(intent)
        }

        loadPets()
    }

    override fun onResume() {
        super.onResume()

        if (::database.isInitialized) {
            loadPets()
        }
    }

    private fun loadPets() {

        petContainer.removeAllViews()

        val pets = database.getAllPets()

        if (pets.isEmpty()) {

            val emptyMessage = TextView(this)

            emptyMessage.text =
                "No pets added yet.\nAdd your first pet! 🐾"

            emptyMessage.textSize = 17f
            emptyMessage.gravity = Gravity.CENTER
            emptyMessage.setTextColor(
                Color.rgb(100, 110, 105)
            )

            emptyMessage.setPadding(
                20,
                60,
                20,
                60
            )

            petContainer.addView(emptyMessage)

            return
        }

        for (pet in pets) {

            // =========================
            // PET CARD
            // =========================

            val card = LinearLayout(this)

            card.orientation = LinearLayout.VERTICAL

            card.setPadding(
                16,
                16,
                16,
                16
            )

            val cardBackground = GradientDrawable()

            cardBackground.setColor(Color.WHITE)

            cardBackground.cornerRadius = 24f

            cardBackground.setStroke(
                2,
                Color.rgb(224, 231, 227)
            )

            card.background = cardBackground


            // =========================
            // TOP ROW
            // =========================

            val topRow = LinearLayout(this)

            topRow.orientation =
                LinearLayout.HORIZONTAL

            topRow.gravity =
                Gravity.CENTER_VERTICAL


            // Pet icon

            val petIcon = TextView(this)

            petIcon.text = when {

                pet.type.equals("dog", true) ->
                    "🐶"

                pet.type.equals("cat", true) ->
                    "🐱"

                pet.type.equals("bird", true) ->
                    "🐦"

                pet.type.equals("fish", true) ->
                    "🐟"

                else ->
                    "🐾"
            }

            petIcon.textSize = 32f
            petIcon.gravity = Gravity.CENTER

            val iconBackground =
                GradientDrawable()

            iconBackground.setColor(
                Color.rgb(232, 243, 238)
            )

            iconBackground.cornerRadius = 22f

            petIcon.background = iconBackground

            val iconParams =
                LinearLayout.LayoutParams(
                    62,
                    62
                )

            topRow.addView(
                petIcon,
                iconParams
            )


            // =========================
            // PET DETAILS
            // =========================

            val detailsLayout =
                LinearLayout(this)

            detailsLayout.orientation =
                LinearLayout.VERTICAL

            val detailsParams =
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )

            detailsParams.setMargins(
                15,
                0,
                0,
                0
            )


            val nameText =
                TextView(this)

            nameText.text = pet.name
            nameText.textSize = 21f

            nameText.setTextColor(
                Color.rgb(33, 79, 67)
            )

            nameText.setTypeface(
                null,
                Typeface.BOLD
            )


            val infoText =
                TextView(this)

            infoText.text =
                "${pet.type}  •  ${pet.age} years old"

            infoText.textSize = 14f

            infoText.setTextColor(
                Color.rgb(105, 113, 109)
            )

            infoText.setPadding(
                0,
                5,
                0,
                0
            )


            detailsLayout.addView(nameText)
            detailsLayout.addView(infoText)

            topRow.addView(
                detailsLayout,
                detailsParams
            )

            card.addView(topRow)


            val routineButton = Button(this)

            routineButton.text = "Care Routine"
            routineButton.setAllCaps(false)
            routineButton.textSize = 14f
            routineButton.setTextColor(Color.WHITE)
            routineButton.gravity = Gravity.CENTER
            routineButton.setTypeface(null, Typeface.BOLD)

            routineButton.backgroundTintList =
                ColorStateList.valueOf(Color.rgb(45, 102, 87))

            val routineParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                52
            )

            routineParams.setMargins(0, 16, 0, 10)

            card.addView(routineButton, routineParams)

            routineButton.setOnClickListener {
                val intent = Intent(this, CareRoutineActivity::class.java)
                intent.putExtra("pet_id", pet.id)
                intent.putExtra("pet_name", pet.name)
                startActivity(intent)
            }


            // =========================
            // EDIT + DELETE ROW
            // =========================

            val buttonRow =
                LinearLayout(this)

            buttonRow.orientation =
                LinearLayout.HORIZONTAL

            buttonRow.gravity =
                Gravity.CENTER_VERTICAL


            // =========================
// EDIT BUTTON
// =========================

            val editButton = Button(this)

            editButton.text = "Edit"
            editButton.isAllCaps = false
            editButton.textSize = 14f
            editButton.setTextColor(Color.rgb(45, 102, 87))
            editButton.setTypeface(null, Typeface.BOLD)
            editButton.gravity = Gravity.CENTER

            editButton.minHeight = 52
            editButton.minimumHeight = 52

            editButton.setPadding(12, 0, 12, 0)

            editButton.backgroundTintList =
                ColorStateList.valueOf(
                    Color.rgb(232, 243, 238)
                )

            editButton.setOnClickListener {

                val intent = Intent(
                    this,
                    PetProfileActivity::class.java
                )

                intent.putExtra(
                    "pet_id",
                    pet.id
                )

                intent.putExtra(
                    "pet_name",
                    pet.name
                )

                intent.putExtra(
                    "pet_type",
                    pet.type
                )

                intent.putExtra(
                    "pet_age",
                    pet.age
                )

                startActivity(intent)
            }


// =========================
// DELETE BUTTON
// =========================

            val deleteButton = Button(this)

            deleteButton.text = "Delete"
            deleteButton.isAllCaps = false
            deleteButton.textSize = 14f
            deleteButton.setTextColor(Color.rgb(175, 72, 72))
            deleteButton.setTypeface(null, Typeface.BOLD)
            deleteButton.gravity = Gravity.CENTER

            deleteButton.minHeight = 52
            deleteButton.minimumHeight = 52

            deleteButton.setPadding(12, 0, 12, 0)

            deleteButton.backgroundTintList =
                ColorStateList.valueOf(
                    Color.rgb(250, 235, 235)
                )

            deleteButton.setOnClickListener {

                showDeleteConfirmation(
                    pet.id,
                    pet.name
                )
            }


            // =========================
// BUTTON WIDTHS
// =========================

            val editParams =
                LinearLayout.LayoutParams(
                    0,
                    52,
                    1f
                )

            editParams.setMargins(
                0,
                0,
                6,
                0
            )

            val deleteParams =
                LinearLayout.LayoutParams(
                    0,
                    52,
                    1f
                )

            deleteParams.setMargins(
                6,
                0,
                0,
                0
            )


            buttonRow.addView(
                editButton,
                editParams
            )

            buttonRow.addView(
                deleteButton,
                deleteParams
            )

            card.addView(buttonRow)


            // =========================
            // ADD CARD
            // =========================

            val cardParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            cardParams.setMargins(
                0,
                0,
                0,
                16
            )

            petContainer.addView(
                card,
                cardParams
            )
        }
    }


    // =========================
    // DELETE CONFIRMATION
    // =========================

    private fun showDeleteConfirmation(
        petId: Int,
        petName: String
    ) {

        AlertDialog.Builder(this)

            .setTitle("Delete Pet")

            .setMessage(
                "Are you sure you want to delete $petName's profile?"
            )

            .setPositiveButton("Delete") { _, _ ->

                val deleted =
                    database.deletePet(petId)

                if (deleted) {
                    loadPets()
                }
            }

            .setNegativeButton(
                "Cancel",
                null
            )

            .show()
    }
}