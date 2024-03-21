package edu.put.inf151883

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SingleGameActivity : ComponentActivity() {
    var idString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_game)

        val nameText = findViewById<TextView>(R.id.gameTitle)
        val typeText = findViewById<TextView>(R.id.gameType)
        val yearText = findViewById<TextView>(R.id.gameYear)
        val idText = findViewById<TextView>(R.id.gameID)

        idString = getIntent().getStringExtra("id")
        val id = idString?.toInt()

        val dbHandler = MyDBHandler(this,null,null,1)
        val currentGame = dbHandler.findGame(id)

        nameText.text = currentGame?.name
        typeText.text = "gra"
        yearText.text = currentGame?.year.toString()
        idText.text = idString

        displayPictures()

        var mGetContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
                result ->
            if(result!=null) {
                var newFile = idString?.let { newImage(it) }
                newFile?.createNewFile()
                contentResolver.openInputStream(result)?.use { input ->
                    FileOutputStream(newFile).use { output ->
                        input.copyTo(output)
                    }
                }
                Toast.makeText(this,"Zapisano zdjęcie z galerii",Toast.LENGTH_SHORT)
            }
        }

        val photoButton: Button = findViewById(R.id.cameraButton)
        val galleryButton: Button = findViewById(R.id.galleryButton)
        val newImage = idString?.let { newImage(it) }
        val newImageURI = newImage?.let {
            FileProvider.getUriForFile(applicationContext,getString(R.string.authorities), it)
        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            Toast.makeText(this,"Zrobiono zdjęcie",Toast.LENGTH_SHORT)
        }

        photoButton.setOnClickListener {
            resultLauncher.launch(newImageURI)
        }

        galleryButton.setOnClickListener {
            mGetContent.launch("image/*")
        }
    }

    object BitmapScaler {
        fun scaleToFitWidth(b: Bitmap, width: Int): Bitmap {
            val factor = width / b.width.toFloat()
            return Bitmap.createScaledBitmap(b, width, (b.height * factor).toInt(), true)
        }
        fun scaleToFitHeight(b: Bitmap, height: Int): Bitmap {
            val factor = height / b.height.toFloat()
            return Bitmap.createScaledBitmap(b, (b.width * factor).toInt(), height, true)
        }
    }

    private fun getScaledBitmap(file: File): Bitmap? {
        var newBitmap: Bitmap? = null
        if(file.exists()) {
            newBitmap = BitmapFactory.decodeFile(file.absolutePath)
            newBitmap = BitmapScaler.scaleToFitWidth(newBitmap,300)
            newBitmap = BitmapScaler.scaleToFitHeight(newBitmap,300)
        }
        return newBitmap
    }

    fun displayPictures() {
        val imagesDirectory = File(applicationContext.filesDir,getString(R.string.temp_images_dir))
        if(!imagesDirectory.exists()) imagesDirectory.mkdir()

        val currentDirectory = File("$imagesDirectory/$idString")
        if(currentDirectory.exists()) {
            val photoList = currentDirectory.listFiles()
            val photoLayout: LinearLayout = findViewById(R.id.PhotoLayout)

            for(photo in photoList) {
                val imageView = ImageView(this)
                imageView.setImageBitmap(getScaledBitmap(photo))
                imageView.setPadding(0,30,0,30)
                photoLayout.addView(imageView)
            }
        }
    }

    fun newImage(directoryName: String): File {
        val imagesDirectory = File(applicationContext.filesDir,getString(R.string.temp_images_dir))
        if(!imagesDirectory.exists()) imagesDirectory.mkdir()

        val currentDirectory = File("$imagesDirectory/$directoryName")
        if(!currentDirectory.exists()) currentDirectory.mkdir()

        val current = LocalDateTime.now()
        val formatted = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss"))
        return File(currentDirectory,"$formatted.jpg")
    }

    fun deleteClick(v: View) {
        val imagesDirectory = File(applicationContext.filesDir,getString(R.string.temp_images_dir))
        val currentDirectory = File("$imagesDirectory/$idString")
        currentDirectory.deleteRecursively()
    }

    fun backClick(v: View) {
        finish()
    }
}