package ro.pub.cs.systems.eim.practicaltest01var05

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView

    private var str1: String = ""
    private var totalClicks: Int = 0

    companion object {
        private const val KEY_RESULT = "KEY_RESULT"
        private const val KEY_TOTAL_CLICKS = "KEY_TOTAL_CLICKS"

        private const val REQUEST_CODE_SECONDARY = 1
        const val INTENT1 = "INTENT1"

        // prag pentru pornirea serviciului (îl poți schimba după cerință)
        private const val THRESHOLD = 4
    }

    // D.2: BroadcastReceiver care primește datele din serviciu
    private val messageBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            val timestamp = intent?.getStringExtra("TIMESTAMP")
            val arit = intent?.getDoubleExtra("ARITH_MEAN", 0.0)
            val geom = intent?.getDoubleExtra("GEOM_MEAN", 0.0)

            Log.d(
                "PracticalTest01Receiver",
                "Action=$action | Time=$timestamp | Arit=$arit | Geom=$geom"
            )

            Toast.makeText(
                this@MainActivity,
                "[$action] Arit: $arit, Geom: $geom at $timestamp",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test01_var05_main)

        tvResult = findViewById(R.id.tvResult)

        val btnTopLeft: Button = findViewById(R.id.btnTopLeft)
        val btnTopRight: Button = findViewById(R.id.btnTopRight)
        val btnBottomLeft: Button = findViewById(R.id.btnBottomLeft)
        val btnBottomRight: Button = findViewById(R.id.btnBottomRight)
        val btnCenter: Button = findViewById(R.id.btnCenter)
        val btnNavigate: Button = findViewById(R.id.btnNavigate)

        //  (B.3)
        if (savedInstanceState != null) {
            str1 = savedInstanceState.getString(KEY_RESULT, "")
            totalClicks = savedInstanceState.getInt(KEY_TOTAL_CLICKS, 0)
            tvResult.text = str1

            Toast.makeText(
                this,
                "Screen rotated. Total clicks: $totalClicks",
                Toast.LENGTH_LONG
            ).show()
        }

        val clickListener = { button: Button ->
            val label = button.text.toString()
            val current = tvResult.text.toString()
            tvResult.text = if (current.isEmpty()) label else "$current, $label"
            totalClicks++
        }

        btnTopLeft.setOnClickListener { clickListener(btnTopLeft) }
        btnTopRight.setOnClickListener { clickListener(btnTopRight) }
        btnBottomLeft.setOnClickListener { clickListener(btnBottomLeft) }
        btnBottomRight.setOnClickListener { clickListener(btnBottomRight) }
        btnCenter.setOnClickListener { clickListener(btnCenter) }

        // C.1: SecondaryActivity
        btnNavigate.setOnClickListener {
            val intent = Intent(this, SecondaryActivity::class.java)
            intent.putExtra(INTENT1, tvResult.text.toString())
            startActivityForResult(intent, REQUEST_CODE_SECONDARY)

            // D.1:prornim serviciul
            checkStartService()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_RESULT, tvResult.text.toString())
        outState.putInt(KEY_TOTAL_CLICKS, totalClicks)
    }

    @Deprecated("startActivityForResult is deprecated, but used for this lab/test.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SECONDARY) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Toast.makeText(this, "Secondary returned: OK", Toast.LENGTH_LONG).show()
                }
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(this, "Secondary returned: CANCEL", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkStartService() {
        if (totalClicks > THRESHOLD) {
            val serviceIntent = Intent(this, PracticalTest01Var05Service::class.java).apply {
                putExtra("TOTAL_CLICKS", totalClicks)
            }
            startService(serviceIntent)
        }
    }

    /*
     * D.2
     */
    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction("ro.pub.cs.systems.eim.practicaltest01.action.MATH1")
            addAction("ro.pub.cs.systems.eim.practicaltest01.action.MATH2")
            addAction("ro.pub.cs.systems.eim.practicaltest01.action.MATH3")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(messageBroadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(messageBroadcastReceiver, filter)
        }
    }


    override fun onPause() {
        unregisterReceiver(messageBroadcastReceiver)
        super.onPause()
    }


    override fun onDestroy() {
        val serviceIntent = Intent(this, PracticalTest01Var05Service::class.java)
        stopService(serviceIntent)
        super.onDestroy()
    }
}