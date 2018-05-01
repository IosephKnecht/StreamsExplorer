package com.project.iosephknecht.streamsexplorer

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigInteger
import java.net.InetAddress
import java.net.URI
import java.util.concurrent.TimeUnit
import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private val CLIP = "/text"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        wifiEdit.setText(readSharedPreference())
        tv_wifi.setText(parseWifiAddress())
    }

    override fun onStart() {
        super.onStart()
        RxTextView
                .afterTextChangeEvents(wifiEdit)
                .debounce(500, TimeUnit.MICROSECONDS)
                .subscribe {
                    writeSharedPreference(it.editable().toString())
                }
        try {
            linkEdit.setText(intent.getStringExtra(Intent.EXTRA_TEXT))
        } catch (e: Exception) {

        }

        btn_link.setOnClickListener {
            val link = linkEdit.text.toString()
            RxRequest.openSocket(readSharedPreference(), 11111)
                    .subscribeOn(Schedulers.io())
                    .flatMap { socket -> RxRequest.postLink(URI(link), socket) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({}, { Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show() })
        }

        tv_wifi.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(CLIP, (it as TextView).text)
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(this, "Ip was copied...", Toast.LENGTH_LONG).show()
        }

    }

    private fun writeSharedPreference(wifiAddress: String) {
        val pref = getPreferences(Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString("WIFI_IP", wifiAddress)
        editor.commit()
    }

    private fun readSharedPreference(): String {
        val pref = getPreferences(Context.MODE_PRIVATE)
        val wifiAddress = pref.getString("WIFI_IP", "")
        return wifiAddress
    }

    private fun parseWifiAddress(): String {
        try {
            val wifiManager = getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ip = wifiInfo.ipAddress
            val bytes = BigInteger.valueOf(ip.toLong()).toByteArray()
            var ipAddress = InetAddress.getByAddress(bytes).toString()
            ipAddress = ipAddress.replace("/", "")
            val decades = ipAddress.split(".")
            ipAddress = ""
            for (i in (decades.size - 1) downTo 1) {
                ipAddress += decades[i] + "."
            }
            return ipAddress + "1"
        } catch (e: Exception) {
            return "You not connected a wifi"
        }
    }
}
