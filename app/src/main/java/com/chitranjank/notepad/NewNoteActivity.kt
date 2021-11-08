package com.chitranjank.notepad

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NewNoteActivity : AppCompatActivity() {
    lateinit var title: EditText
    lateinit var des: EditText
    var firebaseUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = findViewById(R.id.edt_title)
        des = findViewById(R.id.edt_des)
        val tvDes = findViewById<TextView>(R.id.content_des)
        val tvTitle = findViewById<TextView>(R.id.content_title)

        val savebtn = findViewById<Button>(R.id.save_notes)
        val flag = intent.getStringExtra("flag")
        val time = intent.getStringExtra("time")
        val lastTitle = intent.getStringExtra("title")
        val lastDes = intent.getStringExtra("des")

        if (flag.equals("update")) {
            supportActionBar?.title = "Update Note"

            savebtn.visibility = View.VISIBLE
            tvDes.visibility = View.GONE
            tvTitle.visibility = View.GONE

            savebtn.setText("Update note")

            des.visibility = View.VISIBLE
            title.visibility = View.VISIBLE

            title.setText(lastTitle)
            des.setText(lastDes)

        } else if (flag.equals("view")) {
            supportActionBar?.title = "View Note"

            tvDes.visibility = View.VISIBLE
            tvTitle.visibility = View.VISIBLE

            savebtn.visibility = View.GONE
            des.visibility = View.GONE
            title.visibility = View.GONE

            tvDes.setText(lastDes)
            tvTitle.setText(lastTitle)

        } else {
            supportActionBar?.title = "Add New Note"

            des.visibility = View.VISIBLE
            title.visibility = View.VISIBLE
            savebtn.visibility = View.VISIBLE
            savebtn.setText("Save note")
        }

        savebtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (flag.equals("update")) {
                    if (!title.text.toString().trim().isEmpty() || !des.text.toString().trim()
                            .isEmpty()
                    ) {
                        update_notes(time, lastTitle, lastDes)
                    }

                } else {
                    save_notes()
                }
            }
        })

    }

    private fun save_notes() {
        val notes =
            Note(title.text.toString(), des.text.toString(), System.currentTimeMillis().toString())
        val db = FirebaseDatabase.getInstance().getReference("Notes")
        firebaseUser?.let { db.child(it.uid).push().setValue(notes) }
        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun update_notes(lastTime: String?, lastTitle: String?, lastDes: String?) {

        val db = FirebaseDatabase.getInstance().getReference("Notes")
        firebaseUser?.let {
            db.child(it.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds: DataSnapshot in snapshot.children) {
                        val note = ds.getValue(Note::class.java)

                        if (note != null) {
                            if (note.getTime().equals(lastTime) && note.getTitle()
                                    .equals(lastTitle) && note.getDescription().equals(lastDes)
                            ) {
                                val notes =
                                    Note(
                                        title.text.toString(),
                                        des.text.toString(),
                                        System.currentTimeMillis().toString()
                                    )
                                ds.ref.setValue(notes)
                                Toast.makeText(this@NewNoteActivity, "Note updated", Toast.LENGTH_SHORT)
                                    .show()
                                finish()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}