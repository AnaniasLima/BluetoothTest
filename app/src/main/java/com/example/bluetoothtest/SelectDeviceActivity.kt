package com.example.bluetoothtest

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.select_device_layout.*
import org.jetbrains.anko.toast
import timber.log.Timber


class SelectDeviceActivity : AppCompatActivity() {

    private var STATUS_REQUEST_INTERVAL = 10000L

    private var bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1

    private var statusRequestHandler = Handler()


    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
    }

//    FUNCTION_NAME = "${this.javaClass.simpleName} / ${ Thread.currentThread().stackTrace[2].methodName}",

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

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        setContentView(R.layout.select_device_layout)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if ( bluetoothAdapter == null ) {
//            Toast.makeText(this, "this device doesn´t suport bluetooth", 20).show()
            toast("this device doesn´t suport bluetooth")
            return
        } else {
            if (  ! bluetoothAdapter!!.isEnabled) {
                Timber.i("bluetoothAdapter Disabled")
                Timber.i("startActivityForResult AAA")
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                Timber.i("startActivityForResult BBB")
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
                Timber.i("startActivityForResult CCC")
            } else {
                Timber.i("bluetoothAdapter Enabled")
            }
        }

        continueStatusDelayed()

        select_device_refresh.setOnClickListener { pairedDeviceList() }
    }

    private fun pairedDeviceList() {

        Timber.i("pairedDeviceList: Montando lista de devices 'bonded'")

        pairedDevices = bluetoothAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()
        if (pairedDevices.isNotEmpty()) {
            for (device: BluetoothDevice in pairedDevices) {
                list.add(device)
                Timber.i("Device name: %-20s address: %s", device.name, device.address)
            }
        } else {
            toast("No paired bluetooh devices found")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        select_device_list.adapter = adapter

        Timber.i("Atualizou adapter")


        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device:BluetoothDevice = list[position]
            val address: String = device.address
            val intent = Intent(this, ControlActivity::class.java)

            Timber.i("Selecionou ${address} ${device.name}")

            intent.putExtra(EXTRA_ADDRESS, address)


            Timber.i("Vai chamar startActivity()...")
            startActivity(intent)
            Timber.i("chamou startActivity()")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.i("----- onActivityResult ---  requestCode: ${requestCode}, resultCode: ${resultCode}, data: ${data}")
        super.onActivityResult(requestCode, resultCode, data)
        if ( requestCode == REQUEST_ENABLE_BLUETOOTH ) {
            if ( resultCode == Activity.RESULT_OK ) {
                if ( bluetoothAdapter!!.isEnabled) {
                    toast("Bluetooh has been enabled")
                } else {
                    toast("Bluetooh has been disabled")
                }
            }
        } else if ( resultCode == Activity.RESULT_CANCELED) {
            toast("Bluetooh enabling has been disabled")
        }
    }


    private fun continueStatusDelayed() {
        Timber.i("Removendo a agendando a proxima")
//        Timber.i("Status de ConnectToDevice = ${ccc.status}" )

        statusRequestHandler.removeCallbacks(statusRunnable)
        statusRequestHandler.postDelayed(statusRunnable, STATUS_REQUEST_INTERVAL)
    }


    private var statusRunnable = Runnable {

        Timber.i("statusRunnable disparar CommunicationThread ")

//        statusThread = CommunicationThread("StatusRequest", Event(eventType = EventType.FW_STATUS_RQ, action = Event.QUESTION))
//        statusThread.start()

        Timber.i("vai chamar continueStatusDelayed")
        continueStatusDelayed()
    }

}
