package moe.xzr.fivegtile

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.telephony.SubscriptionManager
import com.topjohnwu.superuser.ipc.RootService

class ExternalControlActivity : Activity() {


    private var fivegController: IFivegController? = null

    private fun runWithFivegController(what: (IFivegController?) -> Unit) {
        if (!Utils.isRootGranted()) {
            what(null)
            return
        }
        if (fivegController != null) {
            what(fivegController)
        } else {
            RootService.bind(
                Intent(this@ExternalControlActivity, FivegControllerService::class.java),
                object : ServiceConnection {
                    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                        fivegController = IFivegController.Stub.asInterface(service)
                        what(fivegController)
                    }

                    override fun onServiceDisconnected(name: ComponentName?) {

                    }
                })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.action == "ACTION_ENABLE_5G") {
            runWithFivegController {
                val subId = SubscriptionManager.getDefaultDataSubscriptionId()
                it?.setFivegEnabled(subId, true)
            }
        }
        if (intent.action == "ACTION_DISABLE_5G") {
            runWithFivegController {
                val subId = SubscriptionManager.getDefaultDataSubscriptionId()
                it?.setFivegEnabled(subId, false)
            }
        }

        finish()
    }
}
