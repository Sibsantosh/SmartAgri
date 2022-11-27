package com.rexmo.smartagriculture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etSalary: EditText
    private lateinit var btn: Button
    private lateinit var btnFetch: Button
    lateinit var dRef: DatabaseReference
    var empList=ArrayList<EmployeeModel>()
    var s:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etSalary = findViewById(R.id.eSalary)
        btn = findViewById(R.id.button)
        btnFetch = findViewById(R.id.button)
        dRef = FirebaseDatabase.getInstance().getReference("Employees")
        btn.setOnClickListener {
            uploadEmployeeData()

        }
        btnFetch.setOnClickListener {
            loadData()

        }
    }

    fun uploadEmployeeData() {
        val name = etName.text.toString().trim()
        val age = etAge.text.toString().trim()
        val salary = etSalary.text.toString().trim()
        val empId = dRef.push().key!!
        val employee = EmployeeModel(empId, name, age, salary)
        dRef.child(empId).setValue(employee).addOnCompleteListener {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
        }
    }
    fun loadData(){
        empList.clear()
        dRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if(snapshot.exists()){
                   for(empSnap in snapshot.children){
                       val empData=empSnap.getValue(EmployeeModel::class.java)
                       empList.add(empData!!)
                   }
               }
                for (i in empList)
                    s+="$i"
                Toast.makeText(this@MainActivity, "$s", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "$error", Toast.LENGTH_SHORT).show()
            }

        })
    }
}