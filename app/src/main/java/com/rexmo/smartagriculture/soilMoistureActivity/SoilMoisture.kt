package com.rexmo.smartagriculture.soilMoistureActivity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.ekn.gruzer.gaugelibrary.HalfGauge
import com.ekn.gruzer.gaugelibrary.Range
import com.google.firebase.database.*
import com.rexmo.smartagriculture.EmployeeModel
import com.rexmo.smartagriculture.R

class SoilMoisture : AppCompatActivity() {
    private lateinit var etMoisture: EditText
    private lateinit var cdTvMoisture: TextView
    private lateinit var btn: Button
    lateinit var dRef: DatabaseReference
    var moisture=ArrayList<MoistureLevel>()
    //var s:String=""
    var moistureIn:String?=null
    val min=30
    val max=120
    val diff=max-min
    private lateinit var halfGauge:HalfGauge
    private val range = Range()
   // com.ekn.gruzer.gaugelibrary.Range
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soil_moisture)
        etMoisture=findViewById(R.id.etMoisture)
        cdTvMoisture = findViewById(R.id.cdTvMoisture)
        btn=findViewById(R.id.buttonFetchMoisture)
        halfGauge=findViewById(R.id.halfGauge)
        dRef=FirebaseDatabase.getInstance().getReference("IOT")
        btn.setOnClickListener {
            loadData()
            //halfGauge.value=40.56

        }

        //for the meter gauge

        range.color = Color.parseColor("#0C90F1")//R.color.blue   green #14D94B blue #0C90F1
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

    }

    private fun loadData() {

        moisture.clear()
                dRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for(mois in snapshot.children){
                        val most=mois.getValue(MoistureLevel::class.java)
                        moisture.add(most!!)
                        //Toast.makeText(this@SoilMoisture, "$moisture", Toast.LENGTH_SHORT).show()
                    }

                }
                moistureIn=moisture[0].soilMoisture.toString()
                showPercentage()
                //Toast.makeText(this@SoilMoisture, "${moisture[0].soilMoisture}", Toast.LENGTH_SHORT).show()


            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SoilMoisture, "$error", Toast.LENGTH_SHORT).show()
            }

        })
    }
    fun showPercentage(){
        val m= moistureIn?.toInt()
        //val k= ((m?.minus(min))?.times(100) ?:100 ) /diff
        val k= m?.minus(min)?.times(100)?.div(diff)
        val f=100- k!!
        etMoisture.setText("soil moisture is $f%")
        cdTvMoisture.text = "$f%"
        halfGauge.value= f.toDouble()
        
    }
}