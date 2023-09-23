package com.rexmo.smartagriculture.soilMoistureActivity

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ekn.gruzer.gaugelibrary.HalfGauge
import com.ekn.gruzer.gaugelibrary.Range
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rexmo.smartagriculture.R
import com.rexmo.smartagriculture.databinding.ActivitySoilMoistureBinding

class SoilMoisture : AppCompatActivity() {
    private lateinit var dRef: DatabaseReference
    var moisture=ArrayList<MoistureLevel>()
    //var s:String=""
    var moistureIn:String?=null
    var humidity:String="20"
    var temperature:String="20"
    private val min=33
    private val max=102
    private val diff=max-min
    var state=0
    private lateinit var halfGauge:HalfGauge
    private lateinit var binding:ActivitySoilMoistureBinding
    private val range = Range()

    //val btnBlk="#1E1E2C"
    private val btnBlack: Int = Color.parseColor("#1E1E2D")
    private val rangeBlue: Int = Color.parseColor("#73E6FF")


    private lateinit var mProgressBar: Dialog
   // com.ekn.gruzer.gaugelibrary.Range
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.
        setContentView(this,R.layout.activity_soil_moisture)
        //etMoisture=findViewById(R.id.etMoisture)
        //cdTvMoisture = findViewById(R.id.cdTvMoisture)
        //btn=findViewById(R.id.buttonFetchMoisture)
        halfGauge=findViewById(R.id.halfGauge)
        dRef=FirebaseDatabase.getInstance().getReference("IOT")
        loadData()
            //halfGauge.value=40.56


        binding.btnMotor.setOnClickListener{
            if (state==0){
                turnON()
            }
            else{
                turnOff()
            }
        }


        //for the meter gauge

        range.color = rangeBlue//R.color.blue   green #14D94B blue #0C90F1
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
        halfGauge.maxValueTextColor=Color.BLACK
        halfGauge.minValueTextColor=Color.BLACK
        halfGauge.valueColor = Color.BLACK
        /*halfGauge.addRange(range2)
        halfGauge.addRange(range3)*/

        //set min max and current value
        halfGauge.minValue = 0.0
        halfGauge.maxValue = 100.0
        halfGauge.value = 0.0
        halfGauge.setNeedleColor(Color.RED)


    }

    private fun loadData() {


        moisture.clear()
                dRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (mois in snapshot.children) {
                        val most = mois.getValue(MoistureLevel::class.java)
                        moisture.add(most!!)
                        //Toast.makeText(this@SoilMoisture, "$moisture", Toast.LENGTH_SHORT).show()
                    }

                }
                //Toast.makeText(this@SoilMoisture, "${moisture[0]}", Toast.LENGTH_SHORT).show()
                moistureIn =
                    if (moisture[0].soilMoisture != null) moisture[0].soilMoisture.toString() else "102"
                //state = if (moisture[0].state != null) moisture[0].state!!.toInt() else 0
                humidity =
                    if (moisture[0].humidity != null) moisture[0].humidity!!.toString() else "0"
                temperature =
                    if (moisture[0].temperature != null) moisture[0].temperature!!.toString() else "0"
                showPercentage()
                //Toast.makeText(this@SoilMoisture, "${moisture[0].soilMoisture}", Toast.LENGTH_SHORT).show()



            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SoilMoisture, "$error", Toast.LENGTH_SHORT).show()
            }

        })

    }
    private fun showPercentage(){
        val m= moistureIn?.toInt()
        //val k= ((m?.minus(min))?.times(100) ?:100 ) /diff
        val k= m?.minus(min)?.times(100)?.div(diff)
        var f=100- k!!
        if(f>100)
            f=100
        //binding.etMoisture.setText("soil moisture is $f%")
        //cdTvMoisture.text = "$f%"
        halfGauge.value= f.toDouble()
        binding.apply{
            btnTemperature.text=temperature+"%"
            btnHumidity.text=humidity+"%"
        }
        if (state==0){
            binding.btnMotor.text="OFF"
        }
        else{
            Onn()
        }
        //loadData()
        
    }


    private fun turnON() {
        state=1

        val details = State("1")
        dRef.child("hand").setValue(details).addOnCompleteListener {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
            Onn()
        }.addOnFailureListener {
            Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
        }



    }

    private fun turnOff() {
        state=0
        val empId = dRef.push().key!!
        val details = State("0")
        dRef.child("hand").setValue(details).addOnCompleteListener {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
            Off()
        }.addOnFailureListener {
            Toast.makeText(this, "${it.stackTrace}", Toast.LENGTH_SHORT).show()
        }

    }
    private fun Onn(){
        binding.btnMotor.text="ON"
        binding.btnMotor.setTextColor(Color.RED)
        binding.btnMotor.setBackgroundColor(Color.GREEN)
    }
    private fun Off(){

        binding.btnMotor.text="OFF"
        binding.btnMotor.setBackgroundColor(btnBlack)
        binding.btnMotor.setTextColor(Color.WHITE)


    }

    fun showProgressBar(){
        mProgressBar=Dialog(this)
        mProgressBar.setContentView(R.layout.layout_progress_bar)
        mProgressBar.setCancelable(false)
        mProgressBar.setCanceledOnTouchOutside(false)
        mProgressBar.show()
    }
    fun stopProgress()
    {
        mProgressBar.dismiss()
    }
}