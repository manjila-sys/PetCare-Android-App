package com.manjila.petcare

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.graphics.drawable.GradientDrawable
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

class CareRoutineActivity : AppCompatActivity() {

    private lateinit var database: UserDatabaseHelper
    private lateinit var taskContainer: LinearLayout

    private val darkGreen = Color.rgb(45, 102, 87)
    private val mediumGreen = Color.rgb(79, 124, 107)
    private val lightGreen = Color.rgb(227, 240, 235)
    private val textDark = Color.rgb(41, 45, 43)
    private val textGrey = Color.rgb(110, 118, 113)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_care_routine)

        database = UserDatabaseHelper(this)
        taskContainer = findViewById(R.id.taskContainer)

        val petId = intent.getIntExtra("pet_id", -1)
        val petName = intent.getStringExtra("pet_name") ?: "Your Pet"

        val petNameText = findViewById<TextView>(R.id.tvPetName)
        val addTaskButton = findViewById<Button>(R.id.btnAddTask)
        val delegateSmsButton = findViewById<Button>(R.id.btnDelegateSms)
        val delegateEmailButton = findViewById<Button>(R.id.btnDelegateEmail)

        petNameText.text = "Care tasks for $petName"

        // ---------------- ADD CARE TASK ----------------

        addTaskButton.setOnClickListener {

            val intent = Intent(
                this,
                AddTaskActivity::class.java
            )

            intent.putExtra("pet_id", petId)

            startActivity(intent)
        }
        // ---------------- TODAY'S CHECKLIST ----------------

        val checklistButton = Button(this)

        checklistButton.text = "☑  Today's Checklist"
        checklistButton.isAllCaps = false
        checklistButton.textSize = 14f
        checklistButton.setTextColor(Color.WHITE)
        checklistButton.setTypeface(null, Typeface.BOLD)

        checklistButton.setPadding(
            16,
            0,
            16,
            0
        )

// Rounded green background
        val checklistBackground = GradientDrawable()
        checklistBackground.setColor(darkGreen)
        checklistBackground.cornerRadius = 18f

        checklistButton.background = checklistBackground

        val checklistParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            54
        )

        checklistParams.setMargins(
            0,
            0,
            0,
            16
        )

        val addButtonParent =
            addTaskButton.parent as? LinearLayout

        if (addButtonParent != null) {

            val addButtonIndex =
                addButtonParent.indexOfChild(addTaskButton)

            addButtonParent.addView(
                checklistButton,
                addButtonIndex + 1,
                checklistParams
            )
        }

        checklistButton.setOnClickListener {

            val checklistIntent =
                Intent(
                    this,
                    TodayChecklistActivity::class.java
                )

            checklistIntent.putExtra(
                "pet_id",
                petId
            )

            checklistIntent.putExtra(
                "pet_name",
                petName
            )

            startActivity(checklistIntent)
        }

        // ---------------- DELEGATE BY SMS ----------------

        delegateSmsButton.setOnClickListener {

            val phoneInput = EditText(this)

            phoneInput.hint = "Enter phone number"
            phoneInput.inputType = InputType.TYPE_CLASS_PHONE

            val padding = 40
            phoneInput.setPadding(padding, 10, padding, 10)

            AlertDialog.Builder(this)
                .setTitle("Delegate Care by SMS")
                .setMessage(
                    "Enter the phone number of the family member or pet sitter."
                )
                .setView(phoneInput)
                .setPositiveButton("Continue") { _, _ ->

                    val phoneNumber =
                        phoneInput.text.toString().trim()

                    if (phoneNumber.isEmpty()) {

                        Toast.makeText(
                            this,
                            "Please enter a phone number",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@setPositiveButton
                    }

                    val smsIntent = Intent(
                        Intent.ACTION_SENDTO,
                        Uri.parse("smsto:$phoneNumber")
                    )

                    val tasks =
                        database.getTasksByPet(petId)

                    val message = StringBuilder()

                    message.append("🐾 PETCARE DELEGATION\n\n")
                    message.append("Pet: $petName\n\n")
                    message.append("CARE ROUTINE\n")
                    message.append("--------------------\n")

                    if (tasks.isEmpty()) {

                        message.append(
                            "No care tasks added yet.\n"
                        )

                    } else {

                        for ((index, task) in tasks.withIndex()) {

                            message.append(
                                "\n${index + 1}. ${task.taskName}\n"
                            )

                            message.append(
                                "Frequency: ${task.frequency}\n"
                            )

                            if (!task.taskTime.isNullOrEmpty()) {
                                message.append(
                                    "Time: ${task.taskTime}\n"
                                )
                            }

                            if (!task.supplies.isNullOrEmpty()) {
                                message.append(
                                    "Supplies: ${task.supplies}\n"
                                )
                            }

                            if (!task.notes.isNullOrEmpty()) {
                                message.append(
                                    "Notes: ${task.notes}\n"
                                )
                            }

                            message.append(
                                "Status: ${
                                    if (task.completed)
                                        "Completed"
                                    else
                                        "Pending"
                                }\n"
                            )
                        }
                    }

                    message.append(
                        "\nPlease follow the care routine. 🐾"
                    )

                    smsIntent.putExtra(
                        "sms_body",
                        message.toString()
                    )

                    try {

                        startActivity(smsIntent)

                    } catch (e: Exception) {

                        Toast.makeText(
                            this,
                            "No SMS application found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // ---------------- DELEGATE BY EMAIL ----------------

        delegateEmailButton.setOnClickListener {

            val emailIntent =
                Intent(Intent.ACTION_SENDTO)

            emailIntent.data =
                Uri.parse("mailto:")

            emailIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Pet Care Instructions - $petName"
            )

            val tasks =
                database.getTasksByPet(petId)

            val message = StringBuilder()

            message.append("Hello,\n\n")
            message.append(
                "Please take care of my pet according to the following routine.\n\n"
            )

            message.append("Pet: $petName\n\n")
            message.append("CARE ROUTINE\n")
            message.append("--------------------\n")

            if (tasks.isEmpty()) {

                message.append(
                    "No care tasks added yet.\n"
                )

            } else {

                for ((index, task) in tasks.withIndex()) {

                    message.append(
                        "${index + 1}. ${task.taskName}\n"
                    )

                    message.append(
                        "Frequency: ${task.frequency}\n"
                    )

                    if (!task.taskTime.isNullOrEmpty()) {

                        message.append(
                            "Time: ${task.taskTime}\n"
                        )
                    }

                    if (!task.supplies.isNullOrEmpty()) {

                        message.append(
                            "Supplies: ${task.supplies}\n"
                        )
                    }

                    if (!task.notes.isNullOrEmpty()) {

                        message.append(
                            "Notes: ${task.notes}\n"
                        )
                    }

                    message.append(
                        "Status: ${
                            if (task.completed)
                                "Completed"
                            else
                                "Pending"
                        }\n\n"
                    )
                }
            }

            message.append(
                "Thank you for taking care of my pet. 🐾"
            )

            emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                message.toString()
            )

            try {

                startActivity(emailIntent)

            } catch (e: Exception) {

                Toast.makeText(
                    this,
                    "No email application found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // ---------------- LOAD TASKS ----------------

        if (petId != -1) {

            loadTasks(petId)

        } else {

            showMessage("No pet selected.")
        }
    }

    // Reload tasks when returning from Add/Edit screen
    override fun onResume() {
        super.onResume()

        if (::database.isInitialized) {

            val petId =
                intent.getIntExtra("pet_id", -1)

            if (petId != -1) {

                loadTasks(petId)
            }
        }
    }

    // ==================================================
    // LOAD AND DISPLAY TASKS
    // ==================================================

    private fun loadTasks(petId: Int) {

        taskContainer.removeAllViews()

        val tasks =
            database.getTasksByPet(petId)

        if (tasks.isEmpty()) {

            showMessage(
                "No care tasks added yet.\n\nAdd your first routine! 🐾"
            )

            return
        }

        for (task in tasks) {

            // ---------------- CARD ----------------

            val card =
                MaterialCardView(this)

            card.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 16)
                }

            card.radius = 22f
            card.cardElevation = 2f
            card.setCardBackgroundColor(Color.WHITE)
            card.strokeWidth = 1
            card.strokeColor = Color.rgb(
                225,
                230,
                227
            )

            // ---------------- CARD CONTENT ----------------

            val cardContent =
                LinearLayout(this)

            cardContent.orientation =
                LinearLayout.VERTICAL


            cardContent.setPadding(
                18,
                18,
                18,
                18
            )


            // ---------------- TASK HEADER ----------------

            val headerRow =
                LinearLayout(this)

            headerRow.orientation =
                LinearLayout.HORIZONTAL

            headerRow.gravity =
                Gravity.CENTER_VERTICAL

            // Icon
            val icon =
                TextView(this)

            icon.text = "🐾"
            icon.textSize = 25f
            icon.gravity = Gravity.CENTER

            icon.setBackgroundColor(
                Color.rgb(231, 242, 237)
            )

            val iconParams =
                LinearLayout.LayoutParams(
                    52,
                    52
                )

            headerRow.addView(
                icon,
                iconParams
            )

            // Task name
            val taskName =
                TextView(this)

            taskName.text =
                task.taskName

            taskName.textSize = 19f
            taskName.setTextColor(darkGreen)
            taskName.setTypeface(
                null,
                Typeface.BOLD
            )

            taskName.setPadding(
                14,
                0,
                0,
                0
            )

            val nameParams =
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )

            headerRow.addView(
                taskName,
                nameParams
            )

            // Status badge
            val statusBadge =
                TextView(this)

            if (task.completed) {

                statusBadge.text =
                    "✓ Done"

                statusBadge.setTextColor(
                    darkGreen
                )

                statusBadge.setBackgroundColor(
                    Color.rgb(220, 239, 230)
                )

            } else {

                statusBadge.text =
                    "Pending"

                statusBadge.setTextColor(
                    Color.rgb(145, 105, 45)
                )

                statusBadge.setBackgroundColor(
                    Color.rgb(250, 240, 218)
                )
            }

            statusBadge.textSize = 12f
            statusBadge.setTypeface(
                null,
                Typeface.BOLD
            )

            statusBadge.gravity =
                Gravity.CENTER

            statusBadge.setPadding(
                14,
                8,
                14,
                8
            )

            headerRow.addView(
                statusBadge
            )

            cardContent.addView(
                headerRow
            )

            // ---------------- DIVIDER ----------------

            val divider =
                TextView(this)

            divider.setBackgroundColor(
                Color.rgb(235, 237, 235)
            )

            val dividerParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
                )

            dividerParams.setMargins(
                0,
                15,
                0,
                12
            )

            cardContent.addView(
                divider,
                dividerParams
            )

            // ---------------- DETAILS ----------------

            val frequency =
                createDetailText(
                    "🔄  Frequency: ${task.frequency}"
                )

            cardContent.addView(
                frequency
            )

            val timeText =
                if (!task.taskTime.isNullOrEmpty()) {
                    "🕐  Time: ${task.taskTime}"
                } else {
                    "🕐  Time: Not specified"
                }

            cardContent.addView(
                createDetailText(timeText)
            )

            if (!task.supplies.isNullOrEmpty()) {

                cardContent.addView(
                    createDetailText(
                        "📦  Supplies: ${task.supplies}"
                    )
                )
            }

            if (!task.notes.isNullOrEmpty()) {

                cardContent.addView(
                    createDetailText(
                        "📝  Notes: ${task.notes}"
                    )
                )
            }

            // ---------------- BUTTON ROW ----------------

            val buttonRow =
                LinearLayout(this)

            buttonRow.orientation =
                LinearLayout.HORIZONTAL

            buttonRow.gravity =
                Gravity.CENTER_VERTICAL

            buttonRow.setPadding(
                0,
                14,
                0,
                0
            )

            // Complete
            val completeButton =
                Button(this)

            completeButton.text =
                if (task.completed)
                    "↩ Undo"
                else
                    "✓ Complete"

            completeButton.textSize = 12f
            completeButton.isAllCaps = false
            completeButton.setTextColor(
                Color.WHITE
            )

            completeButton.backgroundTintList =
                ColorStateList.valueOf(
                    mediumGreen
                )

            completeButton.setOnClickListener {

                val newStatus =
                    !task.completed

                val updated =
                    database.setTaskCompleted(
                        task.id,
                        newStatus
                    )

                if (updated) {

                    loadTasks(petId)

                    Toast.makeText(
                        this,
                        if (newStatus)
                            "Task marked as completed ✓"
                        else
                            "Task marked as pending",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // Edit
            val editButton =
                Button(this)

            editButton.text =
                "✏️ Edit"

            editButton.textSize = 12f
            editButton.isAllCaps = false
            editButton.setTextColor(
                darkGreen
            )

            editButton.backgroundTintList =
                ColorStateList.valueOf(
                    Color.rgb(225, 239, 233)
                )

            editButton.setOnClickListener {

                val intent =
                    Intent(
                        this,
                        AddTaskActivity::class.java
                    )

                intent.putExtra(
                    "task_id",
                    task.id
                )

                intent.putExtra(
                    "pet_id",
                    task.petId
                )

                intent.putExtra(
                    "task_name",
                    task.taskName
                )

                intent.putExtra(
                    "frequency",
                    task.frequency
                )

                intent.putExtra(
                    "task_time",
                    task.taskTime
                )

                intent.putExtra(
                    "supplies",
                    task.supplies
                )

                intent.putExtra(
                    "notes",
                    task.notes
                )

                startActivity(intent)
            }

            // Delete
            val deleteButton =
                Button(this)

            deleteButton.text =
                "🗑️ Delete"

            deleteButton.textSize = 12f
            deleteButton.isAllCaps = false
            deleteButton.setTextColor(
                Color.rgb(150, 70, 70)
            )

            deleteButton.backgroundTintList =
                ColorStateList.valueOf(
                    Color.rgb(250, 232, 232)
                )

            deleteButton.setOnClickListener {

                AlertDialog.Builder(this)
                    .setTitle("Delete Care Task")
                    .setMessage(
                        "Are you sure you want to delete \"${task.taskName}\"?"
                    )
                    .setPositiveButton(
                        "Delete"
                    ) { _, _ ->

                        val deleted =
                            database.deleteTask(
                                task.id
                            )

                        if (deleted) {

                            loadTasks(petId)

                            Toast.makeText(
                                this,
                                "Care task deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .setNegativeButton(
                        "Cancel",
                        null
                    )
                    .show()
            }

            // Equal width buttons
            val buttonParams =
                LinearLayout.LayoutParams(
                    0,
                    50,
                    1f
                )

            buttonParams.setMargins(
                0,
                0,
                6,
                0
            )

            buttonRow.addView(
                completeButton,
                buttonParams
            )

            val editParams =
                LinearLayout.LayoutParams(
                    0,
                    50,
                    0.8f
                )

            editParams.setMargins(
                4,
                0,
                4,
                0
            )

            buttonRow.addView(
                editButton,
                editParams
            )

            val deleteParams =
                LinearLayout.LayoutParams(
                    0,
                    50,
                    0.9f
                )

            deleteParams.setMargins(
                6,
                0,
                0,
                0
            )

            buttonRow.addView(
                deleteButton,
                deleteParams
            )

            cardContent.addView(
                buttonRow
            )

            card.addView(
                cardContent
            )

            taskContainer.addView(
                card
            )
            // ==================================================
// GESTURE CONTROLS
// ==================================================

            setupTaskGestures(
                card,
                task,
                petId
            )
        }
    }


    // ==================================================
    // DETAIL TEXT HELPER
    // ==================================================

    private fun createDetailText(
        text: String
    ): TextView {

        val view =
            TextView(this)

        view.text = text
        view.textSize = 13.5f
        view.setTextColor(textGrey)

        view.setPadding(
            0,
            4,
            0,
            4
        )

        return view
    }

    // ==================================================
    // EMPTY MESSAGE
    // ==================================================

    private fun showMessage(
        message: String
    ) {

        val messageText =
            TextView(this)

        messageText.text =
            message

        messageText.textSize = 16f
        messageText.gravity =
            Gravity.CENTER

        messageText.setTextColor(
            textGrey
        )

        messageText.setPadding(
            20,
            60,
            20,
            60
        )

        taskContainer.addView(
            messageText
        )
    }
/// ==================================================
// GESTURE CONTROLS
// ==================================================

    private fun setupTaskGestures(
        card: MaterialCardView,
        task: Task,
        petId: Int
    ) {

        val gestureDetector = GestureDetector(
            this,
            object : GestureDetector.SimpleOnGestureListener() {

                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }

                // ==========================================
                // SWIPE RIGHT = COMPLETE / UNDO
                // ==========================================

                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {

                    if (e1 == null) {
                        return false
                    }

                    val differenceX = e2.x - e1.x
                    val differenceY = e2.y - e1.y

                    if (abs(differenceX) > abs(differenceY)) {

                        if (differenceX > 100) {

                            val newStatus = !task.completed

                            val updated = database.setTaskCompleted(
                                task.id,
                                newStatus
                            )

                            if (updated) {

                                Toast.makeText(
                                    this@CareRoutineActivity,
                                    if (newStatus)
                                        "Task completed ✓"
                                    else
                                        "Task marked as pending",
                                    Toast.LENGTH_SHORT
                                ).show()

                                loadTasks(petId)
                            }

                            return true
                        }
                    }

                    return false
                }

                // ==========================================
                // DOUBLE TAP = EDIT
                // ==========================================

                override fun onDoubleTap(
                    e: MotionEvent
                ): Boolean {

                    val intent = Intent(
                        this@CareRoutineActivity,
                        AddTaskActivity::class.java
                    )

                    intent.putExtra("task_id", task.id)
                    intent.putExtra("pet_id", task.petId)
                    intent.putExtra("task_name", task.taskName)
                    intent.putExtra("frequency", task.frequency)
                    intent.putExtra("task_time", task.taskTime)
                    intent.putExtra("supplies", task.supplies)
                    intent.putExtra("notes", task.notes)

                    startActivity(intent)

                    Toast.makeText(
                        this@CareRoutineActivity,
                        "Opening task for editing ✏️",
                        Toast.LENGTH_SHORT
                    ).show()

                    return true
                }

                // ==========================================
                // LONG PRESS = DELETE
                // ==========================================

                override fun onLongPress(
                    e: MotionEvent
                ) {

                    AlertDialog.Builder(
                        this@CareRoutineActivity
                    )
                        .setTitle("Delete Care Task")
                        .setMessage(
                            "Are you sure you want to delete \"${task.taskName}\"?"
                        )
                        .setPositiveButton("Delete") { _, _ ->

                            val deleted =
                                database.deleteTask(task.id)

                            if (deleted) {

                                Toast.makeText(
                                    this@CareRoutineActivity,
                                    "Care task deleted",
                                    Toast.LENGTH_SHORT
                                ).show()

                                loadTasks(petId)
                            }
                        }
                        .setNegativeButton(
                            "Cancel",
                            null
                        )
                        .show()
                }
            }
        )

        card.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }
}
