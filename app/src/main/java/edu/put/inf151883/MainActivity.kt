package edu.put.inf151883

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import edu.put.inf151883.ui.theme.BoardGameCollectorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileWriter
import java.net.MalformedURLException
import java.net.URL
import java.nio.file.Files
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val xmlDirectory = File("$filesDir/XML")
        if(!xmlDirectory.exists()) xmlDirectory.mkdir()
        val fileName = "$xmlDirectory/games.xml"
        val file = File(fileName)

        if(!file.exists()) {
            val i = Intent(this, ConfigureActivity::class.java)
            startActivityForResult(i,123)
        }
        else {
            retrieveUserData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==123 && resultCode== Activity.RESULT_OK) {
            if(data!=null) {
                if(data.hasExtra("returnUsername")) {
                    val usernameText = findViewById<TextView>(R.id.showUsername)
                    usernameText.text = data.extras?.getString("returnUsername")
                    setup(usernameText.text.toString())
                }
            }
        }
        if(requestCode==1000 && resultCode== Activity.RESULT_OK) {
            if(data!=null) {
                if(data.hasExtra("decision")) {
                    val userDecision = data.extras?.getString("decision")
                    if(userDecision=="yes") {
                        val usernameText = findViewById<TextView>(R.id.showUsername)
                        setup(usernameText.text.toString())
                    }
                }
            }
        }
    }

    fun setup(username: String) {
        val syncDateText = findViewById<TextView>(R.id.syncDate)
        val current = LocalDateTime.now()
        val formatted = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss"))
        syncDateText.text = formatted

        val xmlDirectory = File("$filesDir/XML")

        val gamesURL = "https://www.boardgamegeek.com/xmlapi2/collection?username=" + username + "&stats=1&own=1&excludesubtype=boardgameexpansion"
        val gamesFileName = "$xmlDirectory/games.xml"
        downloadFile(gamesURL, gamesFileName, "games")

        val expansionsURL = "https://www.boardgamegeek.com/xmlapi2/collection?username=" + username + "&stats=1&own=1&subtype=boardgameexpansion"
        val expansionsFileName = "$xmlDirectory/expansions.xml"
        downloadFile(expansionsURL, expansionsFileName, "expansions")
    }

    fun downloadFile(urlString: String, fileName: String, type: String) {
        val url = URL(urlString)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reader = url.openStream().bufferedReader()
                val downloadFile = File(fileName).also { it.createNewFile() }
                val writer = FileWriter(downloadFile).buffered()
                var line: String
                while (reader.readLine().also { line = it?.toString() ?: "" } != null) writer.write(line)
                reader.close()
                writer.close()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Pobrano plik", Toast.LENGTH_SHORT).show()
                    checkValidFile(urlString, fileName, type)
                    loadData(fileName, type)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    when (e) {
                        is MalformedURLException ->
                            print("Malformed URL")
                        else ->
                            print("Error")
                    }
                    val incompleteFile = File(fileName)
                    if (incompleteFile.exists()) incompleteFile.delete()
                }
            }
        }
    }
    fun checkValidFile(urlString: String, fileName: String, type: String) {
        val file = File(fileName)
        Toast.makeText(this,"Sprawdzam plik...",Toast.LENGTH_LONG).show()
        val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
        xmlDoc.documentElement.normalize()
        val items: NodeList = xmlDoc.getElementsByTagName("message")
        if(items.length > 0 && items.item(0).toString().contains("Your request for this collection has been accepted and will be processed")) {
            Toast.makeText(this,"Czekam na dane...",Toast.LENGTH_LONG).show()
            file.delete()
            Thread.sleep(3500)
            downloadFile(urlString, fileName, type)
        }
    }

    fun loadData(fileName: String, type: String) {
        val gameNumberText = findViewById<TextView>(R.id.gameNumber)
        val expansionNumberText = findViewById<TextView>(R.id.expansionNumber)

        val dbHandler = MyDBHandler(this,null,null,1)
        val file = File(fileName)

        if(file.exists()) {
            val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
            xmlDoc.documentElement.normalize()

            val header: NodeList = xmlDoc.getElementsByTagName("items")
            if(header.length>0) {
                val number = header.item(0).attributes.getNamedItem("totalitems").nodeValue
                if (type == "games") gameNumberText.text = number
                else if (type == "expansions") expansionNumberText.text = number
            }

            val items: NodeList = xmlDoc.getElementsByTagName("item")

            for(i in 0..items.length-1) {
                val itemNode: Node = items.item(i)
                val id = itemNode.attributes.getNamedItem("objectid").nodeValue.toInt()

                if(itemNode.nodeType == Node.ELEMENT_NODE) {
                    val elem = itemNode as Element
                    val children = elem.childNodes

                    var currentName: String? = null
                    var currentYear: Int? = null

                    for (j in 0..children.length - 1) {
                        val node = children.item(j)
                        if (node is Element) {
                            when (node.nodeName) {
                                "name" -> currentName = node.textContent
                                "yearpublished" -> currentYear = node.textContent.toInt()
                            }
                        }
                    }

                    if (currentName != null && currentYear != null && id!=null) {
                        val newGame = Game(id, currentName, currentYear)
                        if (type == "games") dbHandler.addGame(newGame)
                        else if (type == "expansions") dbHandler.addExpansion(newGame)
                    }
                }
            }
        }
        Toast.makeText(this,"Utworzono bazę danych",Toast.LENGTH_SHORT).show()
        saveUserData()
    }
    fun saveUserData() {
        val usernameText = findViewById<TextView>(R.id.showUsername)
        val gameNumberText = findViewById<TextView>(R.id.gameNumber)
        val expansionNumberText = findViewById<TextView>(R.id.expansionNumber)
        val syncDateText = findViewById<TextView>(R.id.syncDate)

        val username = usernameText.text.toString()
        val gameNumber = gameNumberText.text.toString()
        val expansionNumber = expansionNumberText.text.toString()
        val syncDate = syncDateText.text.toString()

        val sharedPreferences = getPreferences(MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username",username)
        editor.putString("gameNumber",gameNumber)
        editor.putString("expansionNumber",expansionNumber)
        editor.putString("syncDate",syncDate)
        editor.commit()
    }
    fun retrieveUserData() {
        val usernameText = findViewById<TextView>(R.id.showUsername)
        val gameNumberText = findViewById<TextView>(R.id.gameNumber)
        val expansionNumberText = findViewById<TextView>(R.id.expansionNumber)
        val syncDateText = findViewById<TextView>(R.id.syncDate)

        val sharedPreferences = getPreferences(MODE_PRIVATE)
        usernameText.text = sharedPreferences.getString("username","")
        gameNumberText.text = sharedPreferences.getString("gameNumber","0")
        expansionNumberText.text = sharedPreferences.getString("expansionNumber","0")
        syncDateText.text = sharedPreferences.getString("syncDate","date")
    }

    fun gameClick(v: View) {
        val i = Intent(this, GameActivity::class.java)
        startActivity(i)
    }

    fun expansionClick(v: View) {
        val i = Intent(this, ExpansionActivity::class.java)
        startActivity(i)
    }

    fun syncClick(v: View) {
        val i = Intent(this, SyncActivity::class.java)
        val syncDateText = findViewById<TextView>(R.id.syncDate)
        i.putExtra("passCurrentDate",syncDateText.text)
        startActivityForResult(i,1000)
    }

    fun clearClick(v: View) {
        val dbHandler = MyDBHandler(this,null,null,1)
        val xmlDirectory = File("$filesDir/XML/")
        val imagesDirectory = File(applicationContext.filesDir,getString(R.string.temp_images_dir))

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Czy na pewno chcesz usunąć dane użytkownika?")
            .setPositiveButton("Tak") {alertDialog, id ->
                dbHandler.clearDatabase()
                xmlDirectory.deleteRecursively()
                imagesDirectory.deleteRecursively()
                System.exit(0)
            }
            .setNegativeButton("Nie") {alertDialog, id ->
                alertDialog.dismiss()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}