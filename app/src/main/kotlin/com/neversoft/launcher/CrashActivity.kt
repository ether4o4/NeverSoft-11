package com.neversoft.launcher

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

/**
 * Shown when the launcher hits an uncaught exception. Runs in its own
 * process (:crash) so it survives the crashed main process. Displays the real
 * stack trace with Copy + Restart, so a fatal error is recoverable and
 * reportable instead of freezing the home screen.
 */
class CrashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val trace = intent.getStringExtra(EXTRA_TRACE) ?: "No details were captured."

        val pad = (16 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#FF141414"))
            setPadding(pad, pad, pad, pad)
        }

        root.addView(TextView(this).apply {
            text = "NeverSoft 11 stopped"
            setTextColor(Color.WHITE)
            textSize = 20f
        })
        root.addView(TextView(this).apply {
            text = "The launcher hit an error and recovered. Copy the details below and send them so it can be fixed."
            setTextColor(Color.parseColor("#C9FFFFFF"))
            textSize = 13f
            setPadding(0, pad / 2, 0, pad)
        })

        val traceView = TextView(this).apply {
            text = trace
            setTextColor(Color.parseColor("#FFFF8A80"))
            textSize = 11f
            setTextIsSelectable(true)
            typeface = android.graphics.Typeface.MONOSPACE
        }
        root.addView(
            ScrollView(this).apply { addView(traceView) },
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f,
            ),
        )

        val buttons = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            setPadding(0, pad, 0, 0)
        }
        buttons.addView(Button(this).apply {
            text = "Copy details"
            setOnClickListener {
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("NeverSoft 11 crash", trace))
                Toast.makeText(this@CrashActivity, "Copied", Toast.LENGTH_SHORT).show()
            }
        })
        buttons.addView(Button(this).apply {
            text = "Restart"
            setOnClickListener { restartLauncher() }
        })
        root.addView(buttons)

        setContentView(root)
    }

    private fun restartLauncher() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    companion object {
        const val EXTRA_TRACE = "trace"
    }
}
