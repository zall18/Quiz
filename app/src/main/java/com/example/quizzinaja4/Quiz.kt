package com.example.quizzinaja4

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class Quiz : AppCompatActivity() {

    lateinit var session : SharedPreferences
    lateinit var numberAdapter: NumberAdapter
    lateinit var data: MutableList<NumberModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        session = getSharedPreferences("session", Context.MODE_PRIVATE)
        var editor = session.edit()
        data = mutableListOf<NumberModel>()
        var index = 0
        var connection = Connection()
        var id = intent.getStringExtra("id")
        var name = intent.getStringExtra("name")
        val question: TextView = findViewById(R.id.question)
        val optionA: RadioButton = findViewById(R.id.optionA)
        var optionB: RadioButton = findViewById(R.id.optionB)
        var optionC: RadioButton = findViewById(R.id.optionC)
        var optionD: RadioButton = findViewById(R.id.optionD)
        var gridView = findViewById<GridView>(R.id.gridview)
        var sidebar = findViewById<ImageView>(R.id.sidebar_button)
        var main = findViewById<DrawerLayout>(R.id.main)
        var next = findViewById<LinearLayout>(R.id.next)
        var back = findViewById<LinearLayout>(R.id.prev)
        var selesai = findViewById<AppCompatButton>(R.id.finish)

        var questionsId = mutableListOf<String>()
        var questions = mutableListOf<String>()
        var choices = mutableListOf<MutableList<String>>()
        var choicesId = mutableListOf<MutableList<String>>()
        var rightAnswer = mutableListOf<String>()
        var answers = mutableListOf<String?>()
        var answersId = mutableListOf<String?>()

        lifecycleScope.launch {

            var result = getRequest(connection.connection + "quiz/$id/questions", session.getString("token", ""))

            result.fold(
                onSuccess = {response -> var jsonObject = JSONObject(response)
                    if (!jsonObject.getString("questions").isNullOrEmpty()){
                        var jsonArray = jsonObject.getJSONArray("questions")

                        for(i in 0 until jsonArray.length())
                        {
                            var jsonObject2 = jsonArray.getJSONObject(i)
                            questionsId.add(jsonObject2.getString("id_question"))
                            data.add(NumberModel(i.toString()))
                        }

                        for (i in 0 until questionsId.size)
                        {
                            var result2 = getRequest(connection.connection + "quiz/$id/questions/" + questionsId[i], session.getString("token", ""))

                            result2.fold(
                                onSuccess = {response2 -> var jsonObject3 = JSONObject(response2)
                                    if (!jsonObject3.getString("question").isNullOrEmpty())
                                    {
                                        var jsonObject4 = JSONObject(jsonObject3.getString("question"))
                                        questions.add(jsonObject4.getString("text_question"))
                                        answers.add(null)
                                        answersId.add(null)

                                        var jsonArray2 = jsonObject4.getJSONArray("choices")
                                        var choice = mutableListOf<String>()
                                        var choiceId = mutableListOf<String>()
                                        for (a in 0 until jsonArray2.length())
                                        {
                                            var jsonObject5 = jsonArray2.getJSONObject(a)
                                            choice.add(jsonObject5.getString("choice"))
                                            choiceId.add(jsonObject5.getString("id"))
                                            if (jsonObject5.getInt("is_true") == 1){
                                                rightAnswer.add(jsonObject5.getString("choice"))
                                            }
                                        }
                                        choices.add(choice)
                                        choicesId.add(choiceId)
                                    }

                                },
                                onFailure = {
                                        error -> error.printStackTrace()
                                }
                            )

                        }
                    }


                },
                onFailure = {
                        error2 -> error2.printStackTrace()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                }
            )

            numberAdapter = NumberAdapter(this@Quiz, applicationContext, data, questions, questionsId, choicesId, answersId, rightAnswer, choices, answers, index, question, optionA, optionB, optionC, optionD, next, main, selesai, id.toString())
            gridView.adapter = numberAdapter
            question.text = questions[0]
            if (choices[index].size > 2)
            {
                optionA.text = choices[0][0]
                optionB.text = choices[0][1]
                optionC.text = choices[0][2]
                optionD.text = choices[0][3]
            }else{
                optionA.text = choices[0][0]
                optionB.text = choices[0][1]
                optionC.isVisible = false
                optionD.isVisible = false
            }

        }

        sidebar.setOnClickListener {
            main.openDrawer(GravityCompat.START)
        }

        back.setOnClickListener {
            if (index < 1)
            {
                back.isEnabled = false
            }else{
                index = numberAdapter.getIndexPosition() - 1
                numberAdapter.setIndexPosition(index)
                for (i in 0 until answersId.size)
                {
                    if (answersId[i] == null)
                    {
                        selesai.isEnabled = false
                        break
                    }else{
                        selesai.isEnabled = true
                    }
                }

                if (choices[index].size > 2)
                {
                    if (answersId[index].toString() == choicesId[index][0].toString())
                    {
                        optionA.isChecked = true
                    }else if(answersId[index].toString() == choicesId[index][1].toString())
                    {
                        optionB.isChecked = true;

                    }else if(answersId[index].toString() == choicesId[index][2].toString())
                    {

                        optionC.isChecked = true;
                    }else if(answersId[index].toString() == choicesId[index][3].toString())
                    {
                        optionD.isChecked = true;
                    }else{
                        optionA.isChecked = false
                        optionB.isChecked = false;
                        optionC.isChecked = false;
                        optionD.isChecked = false;
                    }

                    question.text = questions[index]

                    optionA.text = choices[index][0]
                    optionB.text = choices[index][1]
                    optionC.text = choices[index][2]
                    optionD.text = choices[index][3]
                }else{

                    if (answersId[index].toString() == choicesId[index][0].toString())
                    {
                        optionA.isChecked = true
                    }else if(answersId[index].toString() == choicesId[index][1].toString())
                    {
                        optionB.isChecked = true;

                    }else{
                        optionA.isChecked = false
                        optionB.isChecked = false;
                    }


                    optionA.text = choices[index][0]
                    optionB.text = choices[index][1]
                    optionC.isVisible = false
                    optionD.isVisible = false
                }
            }
        }

        next.setOnClickListener {
            if (index + 1 == questions.size)
            {
                next.isEnabled = false
            }else{
                index = numberAdapter.getIndexPosition() + 1
                numberAdapter.setIndexPosition(index)
                for (i in 0 until answersId.size)
                {
                    if (answersId[i] == null)
                    {
                        selesai.isEnabled = false
                        break
                    }else{
                        selesai.isEnabled = true
                    }
                }

                if (choices[index].size > 2)
                {
                    if (answersId[index].toString() == choicesId[index][0].toString())
                    {
                        optionA.isChecked = true
                    }else if(answersId[index].toString() == choicesId[index][1].toString())
                    {
                        optionB.isChecked = true;

                    }else if(answersId[index].toString() == choicesId[index][2].toString())
                    {

                        optionC.isChecked = true;
                    }else if(answersId[index].toString() == choicesId[index][3].toString())
                    {
                        optionD.isChecked = true;
                    }else{
                        optionA.isChecked = false
                        optionB.isChecked = false;
                        optionC.isChecked = false;
                        optionD.isChecked = false;
                    }

                    question.text = questions[index]

                    optionA.text = choices[index][0]
                    optionB.text = choices[index][1]
                    optionC.text = choices[index][2]
                    optionD.text = choices[index][3]
                }else{

                    if (answersId[index].toString() == choicesId[index][0].toString())
                    {
                        optionA.isChecked = true
                    }else if(answersId[index].toString() == choicesId[index][1].toString())
                    {
                        optionB.isChecked = true;

                    }else{
                        optionA.isChecked = false
                        optionB.isChecked = false;
                    }


                    optionA.text = choices[index][0]
                    optionB.text = choices[index][1]
                    optionC.isVisible = false
                    optionD.isVisible = false
                }
            }

        }

        optionA.setOnClickListener {
            answers[index] = optionA.text.toString()
            answersId[index] = choicesId[index][0]
            Log.d("Answer", "getView: $answersId")

        }

        optionB.setOnClickListener {
            answers[index] = optionB.text.toString()
            answersId[index] = choicesId[index][1]
            Log.d("Answer", "getView: $answersId")
        }

        optionC.setOnClickListener {
            answers[index] = optionC.text.toString()
            answersId[index] = choicesId[index][2]
            Log.d("Answer", "getView: $answersId")
        }

        optionD.setOnClickListener {
            answers[index] = optionD.text.toString()
            answersId[index] = choicesId[index][3]
            Log.d("Answer", "getView: $answersId")
        }
        
        selesai.setOnClickListener {
            Toast.makeText(applicationContext, "Selesai!", Toast.LENGTH_SHORT).show()
            for (i in 0 until questions.size)
            {
                lifecycleScope.launch {
                    var jsonObject = JSONObject().apply {
                        put("id_choice", answersId[i])
                    }

                    var result = postRequest(connection.connection + "quiz/$id/questions/" + questionsId[i] + "/answer", jsonObject, session.getString("token", ""))

                    result.fold(
                        onSuccess = {response -> Log.d("hasil", "onCreate: $response")},
                        onFailure = {
                                error -> error.printStackTrace()
                        }
                    )
                }
            }
            var ra = 0
            for (i in 0 until answers.size)
            {
                if (answers[i].toString() == rightAnswer[i].toString())
                {
                    ra++;
                }
            }

            var hasil = ra * 100
            hasil = hasil / questions.size

            editor.putString("quiz_$id", hasil.toString())
            Log.d("hasil", "onCreate: $hasil")
            editor.commit()

            startActivity(Intent(applicationContext, MainQuiz::class.java))
        }


    }

}