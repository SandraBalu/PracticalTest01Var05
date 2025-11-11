package ro.pub.cs.systems.eim.practicaltest01var05

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test01_var05_secondary)

        val tvInfo: TextView = findViewById(R.id.tvInfo)
        val btnOk: Button = findViewById(R.id.btnOk)
        val btnCancel: Button = findViewById(R.id.btnCancel)

        val sablon = intent.getStringExtra(MainActivity.INTENT1)
        tvInfo.text = sablon ?: ""

        // C.2
        btnOk.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
