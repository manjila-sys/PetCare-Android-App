package com.manjila.petcare

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "PetCare.db", null, 4) {

    override fun onCreate(db: SQLiteDatabase) {

        // User table
        db.execSQL(
            """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Pet table
        db.execSQL(
            """
            CREATE TABLE pets (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ownerEmail TEXT NOT NULL,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                age INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Task table
        db.execSQL(
            """
            CREATE TABLE tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                petId INTEGER NOT NULL,
                taskName TEXT NOT NULL,
                frequency TEXT NOT NULL,
                taskTime TEXT,
                supplies TEXT,
                notes TEXT,
                completed INTEGER DEFAULT 0
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        if (oldVersion < 2) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS pets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    ownerEmail TEXT NOT NULL,
                    name TEXT NOT NULL,
                    type TEXT NOT NULL,
                    age INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }

        if (oldVersion < 3) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    petId INTEGER NOT NULL,
                    taskName TEXT NOT NULL,
                    frequency TEXT NOT NULL,
                    taskTime TEXT,
                    supplies TEXT,
                    notes TEXT,
                    completed INTEGER DEFAULT 0
                )
                """.trimIndent()
            )
        }
    }

    // ---------------- USER FUNCTIONS ----------------

    // ---------------- USER FUNCTIONS ----------------

    fun registerUser(
        name: String,
        email: String,
        password: String
    ): Boolean {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("name", name)
            put("email", email)
            put("password", password)
        }

        return try {
            db.insertOrThrow("users", null, values)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun checkLogin(
        email: String,
        password: String
    ): Boolean {

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE email = ? AND password = ?",
            arrayOf(email, password)
        )

        val exists = cursor.count > 0

        cursor.close()

        return exists
    }

    // ---------------- PET FUNCTIONS ----------------

    fun addPet(
        ownerEmail: String,
        name: String,
        type: String,
        age: Int
    ): Boolean {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("ownerEmail", ownerEmail)
            put("name", name)
            put("type", type)
            put("age", age)
        }

        val result = db.insert("pets", null, values)

        return result != -1L
    }

    fun updatePet(
        petId: Int,
        name: String,
        type: String,
        age: Int
    ): Boolean {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("name", name)
            put("type", type)
            put("age", age)
        }

        val result = db.update(
            "pets",
            values,
            "id = ?",
            arrayOf(petId.toString())
        )

        return result > 0
    }

    fun deletePet(petId: Int): Boolean {

        val db = writableDatabase

        val result = db.delete(
            "pets",
            "id = ?",
            arrayOf(petId.toString())
        )

        return result > 0
    }

    fun getAllPets(): ArrayList<Pet> {

        val petList = ArrayList<Pet>()

        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM pets ORDER BY id DESC",
            null
        )

        while (cursor.moveToNext()) {

            val id = cursor.getInt(
                cursor.getColumnIndexOrThrow("id")
            )

            val ownerEmail = cursor.getString(
                cursor.getColumnIndexOrThrow("ownerEmail")
            )

            val name = cursor.getString(
                cursor.getColumnIndexOrThrow("name")
            )

            val type = cursor.getString(
                cursor.getColumnIndexOrThrow("type")
            )

            val age = cursor.getInt(
                cursor.getColumnIndexOrThrow("age")
            )

            petList.add(
                Pet(
                    id,
                    ownerEmail,
                    name,
                    type,
                    age
                )
            )
        }

        cursor.close()

        return petList
    }


    // Add a new care task
    fun addTask(
        petId: Int,
        taskName: String,
        frequency: String,
        taskTime: String?,
        supplies: String?,
        notes: String?
    ): Boolean {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("petId", petId)
            put("taskName", taskName)
            put("frequency", frequency)
            put("taskTime", taskTime)
            put("supplies", supplies)
            put("notes", notes)
            put("completed", 0)
        }

        val result = db.insert("tasks", null, values)

        db.close()

        return result != -1L
    }


    // Get all tasks for one pet
    fun getTasksByPet(petId: Int): List<Task> {

        val taskList = mutableListOf<Task>()
        val db = readableDatabase

        val cursor = db.query(
            "tasks",
            null,
            "petId = ?",
            arrayOf(petId.toString()),
            null,
            null,
            "id DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    petId = cursor.getInt(cursor.getColumnIndexOrThrow("petId")),
                    taskName = cursor.getString(cursor.getColumnIndexOrThrow("taskName")),
                    frequency = cursor.getString(cursor.getColumnIndexOrThrow("frequency")),
                    taskTime = cursor.getString(cursor.getColumnIndexOrThrow("taskTime")),
                    supplies = cursor.getString(cursor.getColumnIndexOrThrow("supplies")),
                    notes = cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                    completed = cursor.getInt(
                        cursor.getColumnIndexOrThrow("completed")
                    ) == 1
                )

                taskList.add(task)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return taskList
    }


    // Update an existing task
    fun updateTask(
        taskId: Int,
        taskName: String,
        frequency: String,
        taskTime: String?,
        supplies: String?,
        notes: String?
    ): Boolean {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("taskName", taskName)
            put("frequency", frequency)
            put("taskTime", taskTime)
            put("supplies", supplies)
            put("notes", notes)
        }

        val result = db.update(
            "tasks",
            values,
            "id = ?",
            arrayOf(taskId.toString())
        )

        db.close()

        return result > 0
    }


    // Delete a task
    fun deleteTask(taskId: Int): Boolean {

        val db = writableDatabase

        val result = db.delete(
            "tasks",
            "id = ?",
            arrayOf(taskId.toString())
        )

        db.close()

        return result > 0
    }


    // Mark task as completed or incomplete
    fun setTaskCompleted(
        taskId: Int,
        completed: Boolean
    ): Boolean {

        val db = writableDatabase

        val values = ContentValues().apply {
            put("completed", if (completed) 1 else 0)
        }

        val result = db.update(
            "tasks",
            values,
            "id = ?",
            arrayOf(taskId.toString())
        )

        db.close()

        return result > 0
    }
}