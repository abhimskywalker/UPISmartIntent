package com.abhimskywalker.upismartintent

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
            1.1 Defining variables with UPI Apps package names
        */
        val BHIM_UPI = "in.org.npci.upiapp"
        val GOOGLE_PAY = "com.google.android.apps.nbu.paisa.user"
        val PHONE_PE = "com.phonepe.app"
        val PAYTM = "net.one97.paytm"
        /*
            1.2 Combining the UPI app package name variables in a list
        */
        val upiApps = listOf<String>(PAYTM, GOOGLE_PAY, PHONE_PE, BHIM_UPI)

        /*
            2.1 Defining button elements for generic UPI OS intent and specific UPI Apps
        */
        val upiButton = findViewById<Button>(R.id.upi)
        val paytmButton = findViewById<Button>(R.id.paytm)
        val gpayButton = findViewById<Button>(R.id.gpay)
        val phonepeButton = findViewById<Button>(R.id.phonepe)
        val bhimButton = findViewById<Button>(R.id.bhim)
        /*
            2.2 Combining button elements of specific UPI Apps in a list
            in the same order as the above upiApps list of UPI app package names
        */
        val upiAppButtons = listOf<Button>(paytmButton, gpayButton, phonepeButton, bhimButton)

        /*
            3. Defining a UPI intent with a Paytm merchant UPI spec deeplink
        */
        val uri = "upi://pay?pa=paytmqr2810050501011ooqggb29a01@paytm&pn=Paytm%20Merchant&mc=5499&mode=02&orgid=000000&paytmqr=2810050501011OOQGGB29A01&am=11&sign=MEYCIQDq96qhUnqvyLsdgxtfdZ11SQP//6F7f7VGJ0qr//lF/gIhAPgTMsopbn4Y9DiE7AwkQEPPnb2Obx5Fcr0HJghd4gzo"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.data = Uri.parse(uri)

        /*
            4.1 Defining an on click action for the UPI generic OS intent chooser
            - This will display a list of all apps available to respond to the UPI intent
            in a chooser tray by the Android OS
        */
        upiButton.setOnClickListener{
            val chooser = Intent.createChooser(intent, "Pay with...")
            startActivityForResult(chooser, REQUEST_CODE)
        }

        /*
            4.2 Defining an on click action for the UPI intent to be carried out by specific apps
            - Clicking on the respective buttons will invoke those specific UPI apps (whenever available)
            - The buttons for specific UPI apps will be displayed when following conditions are met:
                1. App is installed
                2. App is in the list of apps ready to respond to a UPI intent
                    -> This is how the SMART INTENT will work
                    -> The button will only be visible when the app has a UPI ready user

            This will also log the results of the above two check in debug logs
        */
        for(i in upiApps.indices){
            val b = upiAppButtons[i]
            val p = upiApps[i]
            Log.d("UpiAppVisibility", p + " | " + isAppInstalled(p).toString() + " | " + isAppUpiReady(p))
            if(isAppInstalled(p)&&isAppUpiReady(p)) {
                b.visibility = View.VISIBLE
                b.setOnClickListener{
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.data = Uri.parse(uri)
                    intent.setPackage(p)
                    startActivityForResult(intent, REQUEST_CODE)
                }
            }
            else{
                b.visibility = View.INVISIBLE
            }
        }
    }

    /*
        This function is to log the returned results of the transaction.
        - One can replace this with the standard UPI intent result handler code.
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            // Process based on the data in response.
            Log.d("result", data.toString())
            data?.getStringExtra("Status")?.let { Log.d("result", it) }
            data?.getStringExtra("Status")?.let { Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show() }
        }
    }

    /*
        This function checks if the app with this package name is installed on the phone
    */
    fun isAppInstalled(packageName: String): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    /*
        This function checks if the app with this package name is responding to UPI intent
        - i.e. the app has a ready UPI user (as per the NPCI recommended implementation)
        - Circular: https://www.npci.org.in/sites/default/files/circular/Circular-73-Payer_App_behaviour_for_Intent_based_transaction_on_UPI.pdf
    */
    fun isAppUpiReady(packageName: String): Boolean {
        var appUpiReady = false
        val upiIntent = Intent(Intent.ACTION_VIEW, Uri.parse("upi://pay"))
        val pm = packageManager
        val upiActivities: List<ResolveInfo> = pm.queryIntentActivities(upiIntent, 0)
        for (a in upiActivities){
            if (a.activityInfo.packageName == packageName) appUpiReady = true
        }
        return appUpiReady
    }
}