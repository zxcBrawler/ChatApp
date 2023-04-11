package com.example.chatapp

import adapters.MessageAdapter
import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import model.Message
import utils.RecognizerSpeech
import viewmodel.MessageViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val vm: MessageViewModel by viewModels()
    private val permissons = arrayOf(Manifest.permission.RECORD_AUDIO)
    @Inject
    lateinit var recognizerSpeech: RecognizerSpeech
    private var permissionToRecordAudio by Delegates.notNull<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recyclerView: RecyclerView = binding.recycler
        val sendButton: FloatingActionButton = binding.sendMessage
        val postMessage: EditText = binding.postMessage
        supportActionBar?.hide()
        permissionToRecordAudio = checkAudioRecordingPermission()
        if (!permissionToRecordAudio) {
            ActivityCompat.requestPermissions(this, permissons, 0)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MessageAdapter(vm.messages)
        sendButton.setOnClickListener {
            vm.sendMessage(
                Message(
                    postMessage.text.toString(),
                    SimpleDateFormat("HH:mm:ss").format(Date()).toString()
                )
            )
            recyclerView.adapter = MessageAdapter(vm.messages)
        }
        sendButton.setOnLongClickListener {
            recognizerSpeech.startListening()
            binding.sendMessage.setImageResource(R.drawable.ic_baseline_mic_24)
            Toast.makeText(applicationContext, "Speak now", Toast.LENGTH_SHORT).show()
            recognizerSpeech.recognizerSpeech.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                }

                override fun onBeginningOfSpeech() {
                }

                override fun onRmsChanged(rmsdB: Float) {
                }

                override fun onBufferReceived(buffer: ByteArray?) {
                }

                override fun onEndOfSpeech() {
                    Handler(Looper.getMainLooper()).postDelayed({
                        vm.sendMessage(
                            Message(
                                postMessage.text.toString(),
                                SimpleDateFormat("HH:mm:ss").format(Date()).toString()
                            )
                        )
                        recyclerView.adapter = MessageAdapter(vm.messages)
                        binding.sendMessage.setImageResource(R.drawable.ic_baseline_send_24)
                    }, 500)

                }

                override fun onError(error: Int) {
                }

                override fun onResults(results: Bundle?) {
                    if (results != null) {
                        val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        binding.postMessage.setText(data?.get(0))
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    if (partialResults != null) {
                        val data =
                            partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        binding.postMessage.setText(data?.get(0))
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {
                }

            })
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAudioRecordingPermission() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
}