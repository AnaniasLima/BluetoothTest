package com.example.bluetoothtest

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.control_layout.*
import timber.log.Timber
import java.io.IOException
import java.util.*

class ControlActivity: AppCompatActivity() {


    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-008055F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
        private lateinit var connectThread: CommunicationThread
    }

    override fun onStart( ) {
        Timber.i("-------------Entrei em onStart ")
        super.onStart()
    }

    override fun onResume() {
        Timber.i("-------------Entrei em onResume ")
        super.onResume()
    }

    override fun onPause() {
        Timber.i("-------------Entrei em onPause ")
        super.onPause()
    }

    override fun onStop() {
        Timber.i("-------------Entrei em onStop ")
        super.onStop()
    }

    override fun onDestroy() {
        Timber.i("-------------Entrei em onDestroy ")
        super.onDestroy()
    }

    override fun onRestart() {
        Timber.i("-------------Entrei em onRestart ")
        super.onRestart()
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        Timber.i("-------------Entrei em onCreate ")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.control_layout)
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        m_address = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS)

    
        Timber.i("onCreate de ControlActivity vai chamar ConnectToDevice" )

        var ccc: ConnectToDevice = ConnectToDevice(this)


        Timber.i("Ops Antes...." )

        ccc.execute()

        Timber.i("Ops Depois...." )
        Timber.i("Status de ConnectToDevice = ${ccc.status}" )




//        ConnectToDevice(this).execute()

        Timber.i("onCreate vai ajustar funções dos botoes" )
        control_led_on.setOnClickListener { sendCommand("a") }
        control_led_off.setOnClickListener { sendCommand("b") }
        control_disconnect.setOnClickListener { disconnect() }
        Timber.i("onCreate Finalizou" )
    }

    private fun sendCommand(input: String) {
        if ( m_bluetoothSocket != null) {
            Timber.i("sendCommand Vai mandar" )
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Timber.i("sendCommand m_bluetoothSocket null" )
        }
    }

    private fun disconnect() {
        Timber.i("disconnect" )
        if ( m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Timber.i("disconnect m_bluetoothSocket null" )
        }
        finish()
    }


    private class CommunicationThread: Thread() {
        override fun run() {
            try {
                Timber.d(" Vai chamar connect")
                ControlActivity.m_bluetoothSocket!!.connect()

            } catch (e: Exception) {
                Timber.d("Ocorreu uma Exception ")
            }
        }
    }


    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String> () {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
            Timber.i("init de ConnectToDevice" )
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Timber.i("Aguardando socket..." )
            m_progress = ProgressDialog.show(context, "Connecting", "Aguardando socket...")
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
                // TODO: ver isso
        }

        override fun doInBackground(vararg params: Void?): String? {
            Timber.i("doInBackground" )
            try {
                // TODO: ver isso              publishProgress(....)
                if ( m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()


                    Timber.i("getRemoteDevice..." )
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)


                    Timber.i("device.fetchUuidsWithSdp() = ${device.fetchUuidsWithSdp()}" )


                    Timber.i("createInsecureRfcommSocketToServiceRecord..." )
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)


                    Timber.i("cancelDiscovery..." )
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()


                    connectThread = CommunicationThread()

                    connectThread.start()

                    Thread.sleep(5000)

                    connectThread.interrupt()

                    Timber.i("connect..." )
//                    m_bluetoothSocket!!.connect()
                    Timber.i("...connect" )
                }

            } catch (e:  IOException) {
                connectSuccess = false
                e.printStackTrace()
            }

            Timber.i("doInBackground return" )
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            Timber.i("onPostExecute" )

            if ( ! connectSuccess ) {
                Timber.i("couldn´t connect")
            } else {
                Timber.i("m_isConnected = true" )
                m_isConnected = true
            }

            Timber.i("Fechando janela. m_progress.dismiss" )
            m_progress.dismiss()
        }

    }

}