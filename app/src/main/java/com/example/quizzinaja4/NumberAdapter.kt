package com.example.quizzinaja4

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class NumberAdapter(val lifecycleOwner: LifecycleOwner, var context: Context, var data: MutableList<NumberModel>, var questions: MutableList<String>, var questionsId: MutableList<String>, var choicesId: MutableList<MutableList<String>>, var answerId: MutableList<String?>, var rightAnswer: MutableList<String>, var choices: MutableList<MutableList<String>>, var answer: MutableList<String?>, var index : Int, var question: TextView, var optionA: RadioButton, var optionB: RadioButton, var optionC: RadioButton, var optionD: RadioButton, var next: LinearLayout, var main: DrawerLayout, var selesai: AppCompatButton, var id: String): BaseAdapter() {


    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val scope = lifecycleOwner.lifecycleScope

    override fun getCount(): Int {
        return  data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView ?: inflater.inflate(R.layout.numberitem, null, false)

        var num = view.findViewById<LinearLayout>(R.id.number)
        var text = view.findViewById<TextView>(R.id.num)
        var connection = Connection()
        var session = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        var editor = session.edit()

        text.text = (Integer.parseInt(position.toString()) + 1).toString()

        num.setOnClickListener {
            index = Integer.parseInt(position.toString())
            setIndexPosition(index)

            for (i in 0 until answerId.size)
            {
                if (answerId[i] == null)
                {
                    selesai.isEnabled = false
                    break
                }else{
                    selesai.isEnabled = true
                }
            }

            Log.d("index", "getView: $index")

            if (choices[index].size > 2)
            {
                if (answerId[index].toString() == choicesId[index][0])
                {
                    optionA.isChecked = true
                }else if(answerId[index].toString() == choicesId[index][1])
                {
                    optionB.isChecked = true;

                }else if(answerId[index].toString() == choicesId[index][2].toString())
                {

                    optionC.isChecked = true;
                }else if(answerId[index].toString() == choicesId[index][3].toString())
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

                if (answerId[index].toString() == choicesId[index][0].toString())
                {
                    optionA.isChecked = true
                }else if(answerId[index].toString() == choicesId[index][1].toString())
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

            main.closeDrawer(GravityCompat.START)
        }
        optionA.setOnClickListener {
            answer[index] = optionA.text.toString()
            answerId[index] = choicesId[index][0]
            Log.d("Answer", "getView: $answerId")
        }

        optionB.setOnClickListener {
            answer[index] = optionB.text.toString()
            answerId[index] = choicesId[index][1]
            Log.d("Answer", "getView: $answerId")

        }

        optionC.setOnClickListener {
            answer[index] = optionC.text.toString()
            answerId[index] = choicesId[index][2]
            Log.d("Answer", "getView: $answerId")

        }

        optionD.setOnClickListener {
            answer[index] = optionD.text.toString()
            answerId[index] = choicesId[index][3]
            Log.d("Answer", "getView: $answerId")

        }

        selesai.setOnClickListener {
            for (i in 0 until questions.size)
            {
                lifecycleOwner.lifecycleScope.launch {
                    var jsonObject = JSONObject().apply {
                        put("id_choice", answerId[i])
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
            for (i in 0 until answer.size)
            {
                if (answer[i].toString() == rightAnswer[i].toString())
                {
                    ra++;
                }
            }

            var hasil = ra * 100
            hasil = hasil / questions.size

            editor.putString("quiz_$id", hasil.toString())
            Log.d("hasil", "onCreate: $hasil")
            editor.commit()

            var ts = questions.size - ra;
            var intent = Intent(context, Skor::class.java)
            intent.putExtra("tb", ra.toString())
            intent.putExtra("tq", questions.size.toString())
            intent.putExtra("ts", ts.toString())
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        return view
    }

    fun getIndexPosition(): Int
    {
        return index
    }

    fun setIndexPosition(number: Int)
    {
        index = number
    }
}