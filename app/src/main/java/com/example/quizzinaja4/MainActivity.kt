package com.example.quizzinaja4

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var session: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        session = getSharedPreferences("session", Context.MODE_PRIVATE)
        var editor = session.edit()
        var connection =Connection()

        var username: EditText = findViewById(R.id.username)
        var password: EditText = findViewById(R.id.password)
        var login: AppCompatButton = findViewById(R.id.login_button)
        var progress: ProgressBar = findViewById(R.id.progress)

        login.setOnClickListener {
            if (username.text.isNullOrEmpty()){
                username.setError("Username wajib diisi")
            }else if (password.text.isNullOrEmpty())
            {
                password.setError("Password wajib disiisi")
            }else{

                var jsonObject = JSONObject().apply {
                    put("username", username.text.toString())
                    put("password", password.text.toString())
                }

                progress.visibility = View.VISIBLE
                lifecycleScope.launch {
                    var result = postRequest(connection.connection + "auth/login", jsonObject, null)

                    result.fold(
                        onSuccess = {
                            response -> var jsonObject2 = JSONObject(response)
                            if (!jsonObject2.getString("token").isNullOrEmpty())
                            {
                                var jsonObject3 = JSONObject(jsonObject2.getString("user"))

                                editor.putString("token", jsonObject2.getString("token"))
                                editor.putString("id", jsonObject3.getString("id"))
                                editor.commit()

                                progress.visibility = View.GONE
                                Toast.makeText(applicationContext, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(applicationContext, MainQuiz::class.java))

                            }
                        },
                        onFailure = {
                            error -> error.printStackTrace()
                            Toast.makeText(applicationContext, "Login Gagal!", Toast.LENGTH_SHORT).show()
                            progress.visibility = View.GONE
                        }
                    )
                }

            }
        }
    }
}