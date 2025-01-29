package com.sahansachintha.meds

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sahansachintha.meds.helper.SQLiteHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun sqliteInsert() {
        Thread {
            try {
                val sqLiteHelper = SQLiteHelper(this@MainActivity, "MyMed.db", null, 1)

                val sqLiteDatabase = sqLiteHelper.writableDatabase
                sqLiteDatabase.execSQL("INSERT INTO `user`(`name`, `mobile`, `city`)  VALUES('Sahan', '0768701148', 'Tangalle')")
            } catch (e: Exception) {
                Log.e("MyMedLog", e.message, e)
            }
        }.start()
    }

    private fun sqliteUpdate() {
        Thread {
            try {
                val sqLiteHelper = SQLiteHelper(this@MainActivity, "MyMed.db", null, 1)

                val sqLiteDatabase = sqLiteHelper.writableDatabase
                sqLiteDatabase.execSQL("UPDATE `user` SET `name`='Sachin' WHERE `id`='1'")
            } catch (e: Exception) {
                Log.e("MyMedLog", e.message, e)
            }
        }.start()
    }

    private fun sqliteSearch() {
        Thread {
            try {
                val sqLiteHelper = SQLiteHelper(this@MainActivity, "MyMed.db", null, 1)

                val sqLiteDatabase = sqLiteHelper.readableDatabase
                val cursor =
                    sqLiteDatabase.rawQuery("SELECT * FROM `user`", arrayOf())

                while (cursor.moveToNext()) {
                    val name = cursor.getString(1)
                    Log.i("App16Log", name)
                }
            } catch (e: Exception) {
                Log.e("MyMedLog", e.message, e)
            }
        }.start()
    }
}