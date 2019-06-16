package com.siminfo

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        detect_sim.setOnClickListener {
            sim_state.text = if (isDualSimAvailable(this@MainActivity)) {
                "Dual sim available"
            } else {
                "Single/no sim"
            }
        }

    }

    private val simStatusMethodNames = arrayOf("getSimStateGemini", "getSimState")


    fun isDualSimAvailable(context: Context): Boolean {
        var first: Boolean
        var second: Boolean
        for (methodName in simStatusMethodNames) {
            // try with sim 0 first
            try {
                first = getSIMStateBySlot(context, methodName, 0)
                // no exception thrown, means method exists
                second = getSIMStateBySlot(context, methodName, 1)
                return first && second
            } catch (e: Exception) {
                // method does not exist, nothing to do but test the next
            }

        }
        return false
    }

    private fun getSIMStateBySlot(context: Context, predictedMethodName: String, slotID: Int): Boolean {

        var isReady = false

        val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        try {
            val telephonyClass = Class.forName(telephony.javaClass.name)
            val parameter = arrayOfNulls<Class<*>>(1)
            parameter[0] = Int::class.javaPrimitiveType
            val getSimStateGemini = telephonyClass.getMethod(predictedMethodName, *parameter)

            val obPhone = getSimStateGemini.invoke(telephony, slotID)

            if (obPhone != null) {
                val simState = Integer.parseInt(obPhone.toString())
                if (simState == TelephonyManager.SIM_STATE_READY) {
                    isReady = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception(predictedMethodName)
        }

        return isReady
    }

}
