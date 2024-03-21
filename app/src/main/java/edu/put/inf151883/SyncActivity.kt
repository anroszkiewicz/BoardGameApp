package edu.put.inf151883

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class SyncActivity : ComponentActivity() {
    var syncDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        syncDate = getIntent().getStringExtra("passCurrentDate")
        val syncDateText = findViewById<TextView>(R.id.syncDateAgain)
        syncDateText.text = syncDate
    }

    fun dateDifference() : Long {
        val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss")
        val previous = LocalDateTime.parse(syncDate, pattern)
        val current = LocalDateTime.now()

        val seconds = ChronoUnit.SECONDS.between(previous,current)
        return seconds
    }

    fun syncClick(v: View) {
        if(dateDifference()<86400) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Nie minęła jeszcze doba od ostatniej synchronizacji. Czy na pewno chcesz synchronizować dane?")
                .setPositiveButton("Tak") {alertDialog, id ->
                    val data = Intent()
                    data.putExtra("decision","yes")
                    setResult(Activity.RESULT_OK,data)
                    super.finish()
                }
                .setNegativeButton("Nie") {alertDialog, id ->
                    alertDialog.dismiss()
                }
            val alertDialog = builder.create()
            alertDialog.show()
        }
        else {
            val data = Intent()
            data.putExtra("decision","yes")
            setResult(Activity.RESULT_OK,data)
            super.finish()
        }
    }

    fun backClick(v: View) {
        val data = Intent()
        data.putExtra("decision","no")
        setResult(Activity.RESULT_OK,data)
        super.finish()
    }
}