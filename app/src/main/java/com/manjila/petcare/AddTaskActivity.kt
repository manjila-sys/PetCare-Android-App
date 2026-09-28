package com.manjila.petcare

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddTaskActivity : AppCompatActivity() {

    private lateinit var database: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        database = UserDatabaseHelper(this)

        val taskName = findViewById<EditText>(R.id.etTaskName)
        val frequency = findViewById<Spinner>(R.id.spFrequency)
        val taskTime = findViewById<EditText>(R.id.etTaskTime)
        val supplies = findViewById<EditText>(R.id.etSupplies)
        val notes = findViewById<EditText>(R.id.etNotes)
        val saveButton = findViewById<Button>(R.id.btnSaveTask)

        // Daily / Weekly options
        val frequencyOptions = arrayOf(
            "Daily",
            "Weekly"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            frequencyOptions
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        frequency.adapter = adapter

        // Receive pet ID
        val petId = intent.getIntExtra("pet_id", -1)

        // Check whether this screen is being used for editing
        val taskId = intent.getIntExtra("task_id", -1)

        if (taskId != -1) {

            // Change title/button for edit mode
            saveButton.text = "Update Care Task"

            // Fill existing task information
            taskName.setText(
                intent.getStringExtra("task_name") ?: ""
            )

            taskTime.setText(
                intent.getStringExtra("task_time") ?: ""
            )

            supplies.setText(
                intent.getStringExtra("supplies") ?: ""
            )

            notes.setText(
                intent.getStringExtra("notes") ?: ""
            )

            val savedFrequency =
                intent.getStringExtra("frequency") ?: "Daily"

            val frequencyPosition =
                frequencyOptions.indexOf(savedFrequency)

            if (frequencyPosition >= 0) {
                frequency.setSelection(frequencyPosition)
            }
        }

        saveButton.setOnClickListener {

            val name = taskName.text.toString().trim()
            val selectedFrequency = frequency.selectedItem.toString()
            val time = taskTime.text.toString().trim()
            val supplyText = supplies.text.toString().trim()
            val noteText = notes.text.toString().trim()

            if (petId == -1) {
                Toast.makeText(
                    this,
                    "No pet selected",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (name.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter a task name",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // EDIT existing task
            if (taskId != -1) {

                val updated = database.updateTask(
                    taskId,
                    name,
                    selectedFrequency,
                    time,
                    supplyText,
                    noteText
                )

                if (updated) {

                    Toast.makeText(
                        this,
                        "Care task updated successfully! 🐾",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Failed to update care task",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {

                // ADD new task
                val saved = database.addTask(
                    petId,
                    name,
                    selectedFrequency,
                    time,
                    supplyText,
                    noteText
                )

                if (saved) {

                    Toast.makeText(
                        this,
                        "Care task saved successfully! 🐾",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Failed to save care task",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}