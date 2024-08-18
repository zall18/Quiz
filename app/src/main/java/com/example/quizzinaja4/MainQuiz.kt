package com.example.quizzinaja4

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainQuiz : AppCompatActivity() {
    lateinit var session: SharedPreferences
    lateinit var quizAdapter: QuizAdapter
    lateinit var data: MutableList<QuizModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_quiz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        session = getSharedPreferences("session", Context.MODE_PRIVATE)
        var listview: ListView = findViewById(R.id.listview)
        var connection = Connection()
        data = mutableListOf<QuizModel>()

        lifecycleScope.launch {
            var result = getRequest(connection.connection + "quiz", session.getString("token", ""))

            result.fold(
                onSuccess = {
                    response -> var jsonObject = JSONObject(response)
                    if (!jsonObject.getString("quiz").isNullOrEmpty()){
                        var jsonArray = jsonObject.getJSONArray("quiz")
                        for (i in 0 until jsonArray.length()){
                            var jsonObject2 = jsonArray.getJSONObject(i)
                            data.add(QuizModel(jsonObject2.getString("id"), jsonObject2.getString("title"), jsonObject2.getString("time"), jsonObject2.getString("duration")))
                        }

                        quizAdapter = QuizAdapter(applicationContext, data)
                        listview.adapter = quizAdapter
                    }
                },
                onFailure = {
                    error -> error.printStackTrace()
                }
            )
        }
    }
}