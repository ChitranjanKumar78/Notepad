package com.chitranjank.notepad

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.chitranjank.notepad.R.id
import com.chitranjank.notepad.R.layout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    lateinit var list: ArrayList<Note>
    lateinit var auth: FirebaseAuth
    var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    lateinit var listView: ListView
    lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        auth = FirebaseAuth.getInstance()

        listView = findViewById(id.list_view)

        list = ArrayList()

        //firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val db = FirebaseDatabase.getInstance().getReference("Notes")
        firebaseUser?.let {
            db.child(it.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for (ds: DataSnapshot in snapshot.children) {
                        val note = ds.getValue(Note::class.java)
                        if (note != null) {
                            list.add(note)
                        }
                    }

                    list.sortByDescending { it.getTime() }
                    adapter = NoteAdapter(this@MainActivity, list)
                    listView.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
        listView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                show_dialog(position)
            }
        })

    }

    fun addNewNotes(view: View) {
        if (firebaseUser != null) {
            val intent = Intent(this, NewNoteActivity::class.java)
            intent.putExtra("id", "Hello")
            startActivity(intent)
        }
    }

    private fun show_dialog(i: Int) {
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setTitle("Choose an action!")
        dialog.setContentView(R.layout.update_dialog)
        dialog.show()

        val updateBtn = dialog.findViewById<TextView>(R.id.update_note)
        val deleteBtn = dialog.findViewById<TextView>(R.id.delete_note)
        val cancelBtn = dialog.findViewById<TextView>(R.id.cancel)
        val viewBtn = dialog.findViewById<TextView>(R.id.view_note)

        cancelBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dialog.dismiss()
            }
        })

        updateBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(
                    Intent(this@MainActivity, NewNoteActivity::class.java)
                        .putExtra("flag", "update")
                        .putExtra("time", list.get(i).getTime())
                        .putExtra("title", list.get(i).getTitle())
                        .putExtra("des", list.get(i).getDescription())
                )
                dialog.dismiss()
            }
        })

        viewBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(
                    Intent(this@MainActivity, NewNoteActivity::class.java)
                        .putExtra("flag", "view")
                        .putExtra("time", list.get(i).getTime())
                        .putExtra("title", list.get(i).getTitle())
                        .putExtra("des", list.get(i).getDescription())
                )
                dialog.dismiss()
            }
        })

        deleteBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                delete_notes(
                    list.get(i).getTime(),
                    list.get(i).getTitle(),
                    list.get(i).getDescription()
                )
                dialog.dismiss()
            }
        })

    }

    private fun delete_notes(lastTime: String?, lastTitle: String?, lastDes: String?) {

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
                                ds.ref.removeValue()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Note deleted",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.log_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finishAffinity()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}

