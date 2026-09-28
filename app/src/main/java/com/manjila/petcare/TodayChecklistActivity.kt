package com.manjila.petcare

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TodayChecklistActivity : AppCompatActivity() {

    private lateinit var database: UserDatabaseHelper
    private lateinit var checklistContainer: LinearLayout
    private lateinit var progressText: TextView

    private var petId: Int = -1
    private var petName: String = "Your Pet"

    private val darkGreen = Color.rgb(45, 102, 87)
    private val textDark = Color.rgb(41, 45, 43)
    private val textGrey = Color.rgb(110, 118, 113)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_today_checklist)

        database = UserDatabaseHelper(this)

        petId = intent.getIntExtra("pet_id", -1)
        petName = intent.getStringExtra("pet_name") ?: "Your Pet"

        val petText = findViewById<TextView>(R.id.tvChecklistPet)
        val dateText = findViewById<TextView>(R.id.tvChecklistDate)

        progressText = findViewById(R.id.tvChecklistProgress)
        checklistContainer = findViewById(R.id.checklistContainer)

        val backButton = findViewById<Button>(R.id.btnBackToRoutine)

        petText.text = "Care checklist for $petName"

        val today = SimpleDateFormat(
            "EEEE, dd MMMM yyyy",
            Locale.getDefault()
        ).format(Date())

        dateText.text = today

        backButton.setOnClickListener {
            finish()
        }

        if (petId != -1) {
            loadChecklist()
        } else {
            showEmptyMessage("No pet selected.")
        }
    }

    override fun onResume() {
        super.onResume()

        if (::database.isInitialized && petId != -1) {
            loadChecklist()
        }
    }

    private fun loadChecklist() {

        checklistContainer.removeAllViews()

        val tasks = database.getTasksByPet(petId)

        if (tasks.isEmpty()) {

            progressText.text = "0 tasks completed"

            showEmptyMessage(
                "No care tasks added yet.\n\nAdd tasks from Care Routine! 🐾"
            )

            return
        }

        val completedCount = tasks.count { it.completed }

        progressText.text =
            "$completedCount of ${tasks.size} tasks completed"

        for (task in tasks) {

            val card = MaterialCardView(this)

            card.radius = 22f
            card.cardElevation = 2f
            card.setCardBackgroundColor(Color.WHITE)
            card.strokeWidth = 1
            card.strokeColor = Color.rgb(225, 230, 227)

            val cardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            cardParams.setMargins(0, 0, 0, 14)

            card.layoutParams = cardParams

            val row = LinearLayout(this)

            row.orientation = LinearLayout.HORIZONTAL
            row.gravity = Gravity.CENTER_VERTICAL

            row.setPadding(16, 16, 16, 16)

            val checkBox = CheckBox(this)

            checkBox.isChecked = task.completed

            checkBox.buttonTintList =
                ColorStateList.valueOf(darkGreen)

            val checkParams = LinearLayout.LayoutParams(
                48,
                48
            )

            row.addView(checkBox, checkParams)

            val details = LinearLayout(this)

            details.orientation = LinearLayout.VERTICAL

            val detailsParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

            detailsParams.setMargins(8, 0, 0, 0)

            val taskName = TextView(this)

            taskName.text = task.taskName
            taskName.textSize = 18f
            taskName.setTextColor(darkGreen)
            taskName.setTypeface(null, Typeface.BOLD)

            val taskInfo = TextView(this)

            val timeInfo =
                if (!task.taskTime.isNullOrEmpty()) {
                    "Time: ${task.taskTime}"
                } else {
                    "Time: Not specified"
                }

            taskInfo.text =
                "${task.frequency}  •  $timeInfo"

            taskInfo.textSize = 13.5f
            taskInfo.setTextColor(textGrey)
            taskInfo.setPadding(0, 5, 0, 0)

            details.addView(taskName)
            details.addView(taskInfo)

            if (!task.supplies.isNullOrEmpty()) {

                val suppliesText = TextView(this)

                suppliesText.text =
                    "Supplies: ${task.supplies}"

                suppliesText.textSize = 13f
                suppliesText.setTextColor(textGrey)
                suppliesText.setPadding(0, 4, 0, 0)

                details.addView(suppliesText)
            }

            row.addView(details, detailsParams)

            card.addView(row)

            checkBox.setOnCheckedChangeListener { _, isChecked ->

                val updated =
                    database.setTaskCompleted(
                        task.id,
                        isChecked
                    )

                if (updated) {
                    loadChecklist()
                }
            }

            checklistContainer.addView(card)
        }
    }

    private fun showEmptyMessage(message: String) {

        val emptyText = TextView(this)

        emptyText.text = message
        emptyText.textSize = 16f
        emptyText.gravity = Gravity.CENTER
        emptyText.setTextColor(textGrey)

        emptyText.setPadding(
            20,
            60,
            20,
            60
        )

        checklistContainer.addView(emptyText)
    }
}