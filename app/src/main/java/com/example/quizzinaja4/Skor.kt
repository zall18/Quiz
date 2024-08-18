package com.example.quizzinaja4

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.createBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream

class Skor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_skor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var tb = intent.getStringExtra("tb")
        var ts = intent.getStringExtra("ts")
        var tq = intent.getStringExtra("tq")
        var file: File
        var f: File

        var soal: TextView = findViewById(R.id.tq)
        var benar: TextView = findViewById(R.id.tb)
        var salah: TextView = findViewById(R.id.ts)
        var content: LinearLayout = findViewById(R.id.content)
        soal.text = "Total Soal : $tq"
        benar.text = "Total Benar : $tb"
        salah.text = "Total Salah : $ts"




        var simpan: AppCompatButton = findViewById(R.id.simpan)
        simpan.setOnClickListener {
            var bitmap = createBitmap(content.width, content.height, Bitmap.Config.ARGB_8888)
            var canvas = Canvas(bitmap)
            content.draw(canvas)

            if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            {
                file = File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "skorNilai")
                if (!file.exists())
                {
                    file.mkdir()
                }

                f = File(file.absoluteFile.toString() + "/skor.png")
                var fileOutputStream = FileOutputStream(f)
                bitmap.compress(Bitmap.CompressFormat.PNG, 10, fileOutputStream)
                Toast.makeText(applicationContext, "Skor berhasil disimpan!", Toast.LENGTH_SHORT).show()
            }

        }

        var kembali: AppCompatButton = findViewById(R.id.kembali)
        kembali.setOnClickListener {
            startActivity(Intent(applicationContext, MainQuiz::class.java))
        }


    }
}