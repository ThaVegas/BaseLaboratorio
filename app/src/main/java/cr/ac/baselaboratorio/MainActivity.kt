package cr.ac.baselaboratorio

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var buttonPlay: Button
    private lateinit var buttonPause: Button
    private lateinit var buttonNext: Button
    private lateinit var buttonPrevious: Button

    private lateinit var textView: TextView

    private lateinit var mediaPlayer: MediaPlayer
    private var Posiciones: Int = 0
    private var pausado = true

    companion object {
        var OPEN_DIRECTORY_REQUEST_CODE = 1
    }

    private var files: MutableList<DocumentFile> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonPrevious = findViewById(R.id.buttonPrevious)
        buttonPlay = findViewById(R.id.buttonPlay)
        buttonPause = findViewById(R.id.buttonPause)
        buttonNext = findViewById(R.id.buttonNext)
        textView = findViewById(R.id.textView)
        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
        setOnClickListeners(this)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                var directoryUri = data?.data ?: return
                val rootTree = DocumentFile.fromTreeUri(this, directoryUri)
                for (file in rootTree!!.listFiles()) {
                    try {
                        file.name?.let { Log.e("Archivo", it) }
                        files.add(file)
                    } catch (e: Exception) {
                        Log.e("Error", "No pude ejecutar el archivo" + file.uri) }
                }
                mediaPlayer = MediaPlayer.create(this, files[Posiciones].uri)
            }
        }
    }
    private fun setOnClickListeners(context: Context) {
        buttonPlay.setOnClickListener {
            if (pausado) {
                pausado = false
                mediaPlayer.start()
                Toast.makeText(context, "Reproduciendo...", Toast.LENGTH_SHORT).show()
                textView.text = files[Posiciones].name.toString()
            }
        }
        buttonPause.setOnClickListener {
            pausado = true
            mediaPlayer.pause()
            Toast.makeText(context, "Parando...", Toast.LENGTH_SHORT).show()
            mediaPlayer = MediaPlayer.create(this, files[Posiciones].uri)
            textView.text = files[Posiciones].name.toString()
        }
        buttonNext.setOnClickListener {
            if (Posiciones + 1 > files.size - 1) {
                mediaPlayer.stop()
                Posiciones = 0
                mediaPlayer = MediaPlayer.create(context, files[Posiciones].uri)
                mediaPlayer.start()
                textView.text = files[Posiciones].name.toString()
            } else {
                mediaPlayer.stop()
                Posiciones++
                mediaPlayer = MediaPlayer.create(context, files[Posiciones].uri)
                mediaPlayer.start()
                textView.text = files[Posiciones].name.toString()}
        }
        buttonPrevious.setOnClickListener {
            if (Posiciones - 1 < 0) {
                mediaPlayer.stop()
                Posiciones = files.size - 1
                mediaPlayer = MediaPlayer.create(context, files[Posiciones].uri)
                mediaPlayer.start()
                textView.text = files[Posiciones].name.toString()
            } else {
                mediaPlayer.stop()
                Posiciones--
                mediaPlayer = MediaPlayer.create(context, files[Posiciones].uri)
                mediaPlayer.start()
                textView.text = files[Posiciones].name.toString()}
        }
    }
}