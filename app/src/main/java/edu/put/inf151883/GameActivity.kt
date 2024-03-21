package edu.put.inf151883

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val dbHandler = MyDBHandler(this,null,null,1)
        val games = dbHandler.showGames()
        val table: TableLayout = findViewById(R.id.GameTable)

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
            val sortedGames = dbHandler.sortGames("name")
            displayList(table,sortedGames)
        }

        val year: TextView = TextView(this)
        year.setPadding(5,15,5,15)
        year.text = "rok"
        row.addView(year)
        year.setOnClickListener() {
            table.removeAllViews()
            displayHeader(table)
            val sortedGames = dbHandler.sortGames("year")
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
                val intent = Intent(this, SingleGameActivity::class.java)
                intent.putExtra("id", games[i].id.toString())
                startActivity(intent)
            }
        }
    }

    fun backClick(v: View) {
        finish()
    }
}