package com.yaglei.bluetoothservice

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.yaglei.bluetoothservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), BluetoothChatService.AcceptBluetoothListener {
    private val TAG = MainActivity::class.java.name
    private lateinit var binding: ActivityMainBinding
    private var bluetoothChatService: BluetoothChatService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.txt.setText(
            "{\n" +
                    "\"name\":\"01\",\n" +
                    "\"time\":\"2022年09月27日\",\n" +
                    "\"dianya\":\"3.3V\",\n" +
                    "\"yql\":\"300方\",\n" +
                    "\"kg\":1\n" +
                    "}"
        )
        requestPermission()
        BluetoothUtils.instance.initBluetooth(this)
        BluetoothUtils.instance.enable()
        binding.bt1.setOnClickListener {
            if (bluetoothChatService == null) {
                val bluetoothManager =
                    getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                bluetoothChatService = BluetoothChatService(bluetoothManager.adapter)
                bluetoothChatService!!.start(this)
                Toast.makeText(this, "已开启服务器", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "已经开启服务器，请勿重复开启", Toast.LENGTH_SHORT).show()
            }
        }

        binding.bt2.setOnClickListener {
            val txt = binding.txt.text.toString()
            if (txt.isNotEmpty()) {
                bluetoothChatService?.write(txt.toByteArray())
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            1
        )
    }

    override fun changeStatus(state: Int) {
        Log.e(TAG, "changeStatus: ${state}")
        runOnUiThread {
            if (state == 1) {
                binding.bt1.visibility = View.GONE
                binding.bt2.visibility = View.VISIBLE
            } else if (state == 0) {
                binding.bt2.visibility = View.GONE
                binding.bt1.visibility = View.VISIBLE
                binding.bt1.text = "服务已断开，点击重新开启服务"
                bluetoothChatService?.stop()
                bluetoothChatService = null
                Log.e(TAG, "changeStatus: 刷新按钮状态${binding.bt2.visibility}", )
            }
        }

    }

    override fun message(msg: String) {
        Log.e(TAG, "message: ${msg}")
    }

}