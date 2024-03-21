package edu.put.inf151883

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.ComponentActivity

class ExpansionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expansion)

        val dbHandler = MyDBHandler(this,null,null,1)
        val games = dbHandler.showExpansions()
        val table: TableLayout = findViewById(R.id.ExpansionTable)

        displayHeader(table)
        displayList(table,games)
    }
    fun displayHeader(table: TableLayout) {
        val dbHandler = MyDBHandler(this,null,null,1)
        val row = TableRow(this)
        row.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT)

        val id = TextView(this)
        id.text = "lp"
        id.setPadding(5,15,5,15)
        row.addView(id)

        val name: TextView = TextView(this)
        name.setPadding(5,15,5,15)
        name.text = "nazwa"
        row.addView(name)
        name.setOnClickListener() {
            table.removeAllViews()
            displayHeader(table)
            val sortedGames = dbHandler.sortExpansions("name")
            displayList(table,sortedGames)
        }

        val year: TextView = TextView(this)
        year.setPadding(5,15,5,15)
        year.text = "rok"
        row.addView(year)
        year.setOnClickListener() {
            table.removeAllViews()
            displayHeader(table)
            val sortedGames = dbHandler.sortExpansions("year")
            displayList(table,sortedGames)
        }

        table.setColumnShrinkable(1,true)
        table.addView(row)
    }
    fun displayList(table: TableLayout, games: MutableList<Game>) {
        for (i in 0..games.size-1) {
            val row = TableRow(this)
            row.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT)
            val id = TextView(this)
            val number = i+1
            id.text = number.toString()
            id.setPadding(5,15,5,15)
            row.addView(id)
            val name: TextView = TextView(this)
            name.text = games[i].name
            name.setPadding(5,15,5,15)
            row.addView(name)
            val year: TextView = TextView(this)
            year.text = games[i].year.toString()
            year.setPadding(5,15,5,15)
            row.addView(year)

            table.setColumnShrinkable(1,true)
            table.addView(row)

            row.setOnClickListener() {
                val intent = Intent(this, SingleExpansionActivity::class.java)
                intent.putExtra("id", games[i].id.toString())
                startActivity(intent)
            }
        }
    }

    fun backClick(v: View) {
        finish()
    }
}