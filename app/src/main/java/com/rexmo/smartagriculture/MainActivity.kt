package com.rexmo.smartagriculture

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.ekn.gruzer.gaugelibrary.HalfGauge
import com.ekn.gruzer.gaugelibrary.Range
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
    private lateinit var halfGauge: HalfGauge
    private val range = Range()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etSalary = findViewById(R.id.eSalary)
        btn = findViewById(R.id.button)
        btnFetch = findViewById(R.id.button)*/
        halfGauge=findViewById(R.id.halfGauge)
        dRef = FirebaseDatabase.getInstance().getReference("Employees")
        /*btn.setOnClickListener {
            uploadEmployeeData()

        }
        btn.setOnClickListener {
           loadData()

        }*/
        range.color = Color.parseColor("#9BEDFF")//R.color.blue   green #14D94B blue #0C90F1
        range.from = 0.0
        range.to = 100.0

        /* val range2 = Range()
         range2.color = Color.parseColor("#E3E500")
         range2.from = 50.0
         range2.to = 100.0

         val range3 = Range()
         range3.color = Color.parseColor("#00b20b")
         range3.from = 100.0
         range3.to = 150.0*/

        //add color ranges to gauge
        halfGauge.addRange(range)
        /*halfGauge.addRange(range2)
        halfGauge.addRange(range3)*/

        //set min max and current value
        halfGauge.minValue = 0.0
        halfGauge.maxValue = 100.0
        halfGauge.value = 0.0
        halfGauge.setNeedleColor(Color.RED)

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