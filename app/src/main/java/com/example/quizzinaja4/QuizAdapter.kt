package com.example.quizzinaja4

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView

class QuizAdapter(var context: Context, var data: MutableList<QuizModel>): BaseAdapter() {

    var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView ?: inflater.inflate(R.layout.quizitem, null, false)

        var name = view.findViewById<TextView>(R.id.name_quiz)
        var time = view.findViewById<TextView>(R.id.time_quiz)
        var duration = view.findViewById<TextView>(R.id.duration_quiz)
        var nilai = view.findViewById<TextView>(R.id.nilai_quiz)
        var item = view.findViewById<LinearLayout>(R.id.quiz_item)
        var session = context.getSharedPreferences("session", Context.MODE_PRIVATE)

        var quiz = getItem(position) as QuizModel

        name.text = quiz.title
        time.text = "Waktu dimulai : " + quiz.time
        duration.text = "Durasi Pengerjaan : " + quiz.duration
        if (!session.getString("quiz_" + quiz.id , "").isNullOrEmpty())
        {
            nilai.text = "Nilai : " + session.getString("quiz_" + quiz.id , "")
        }
        item.setOnClickListener {

            var intent = Intent(context, Quiz::class.java)
            intent.putExtra("id", quiz.id)
            intent.putExtra("name", quiz.title)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        return  view

    }
}