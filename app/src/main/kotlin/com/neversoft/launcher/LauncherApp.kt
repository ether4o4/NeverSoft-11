package com.neversoft.launcher

import android.app.Application
import android.content.Intent
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LauncherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        installCrashHandler()
    }

    // On an uncaught crash: log the full stack trace, show a recovery screen
    // (in a separate process) with the real exception + Copy/Restart, then kill
    // the crashed process. A fatal error becomes recoverable and reportable
    // instead of freezing the home screen.
    private fun installCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val trace = buildTrace(thread, throwable)
            runCatching {
                File(filesDir, "crash_log.txt").appendText(trace + "\n\n")
                getExternalFilesDir(null)?.let { File(it, "crash_log.txt").appendText(trace + "\n\n") }
                // Also drop it in Downloads (app holds all-files access) so it's
                // easy to find and share when diagnosing a force-close.
                runCatching {
                    val downloads = android.os.Environment.getExternalStoragePublicDirectory(
                        android.os.Environment.DIRECTORY_DOWNLOADS,
                    )
                    File(downloads, "neversoft11_crash_log.txt").appendText(trace + "\n\n")
                }
            }
            runCatching {
                startActivity(
                    Intent(this, CrashActivity::class.java)
                        .putExtra(CrashActivity.EXTRA_TRACE, trace)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK),
                )
            }
            // Tear down the crashed process; the crash screen runs in :crash
            android.os.Process.killProcess(android.os.Process.myPid())
            kotlin.system.exitProcess(10)
        }
    }

    private fun buildTrace(thread: Thread, throwable: Throwable): String {
        val ts = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        return buildString {
            append("=== NeverSoft 11 crash @ $ts ===\n")
            append("versionName ${runCatching { packageManager.getPackageInfo(packageName, 0).versionName }.getOrNull()}\n")
            append("device ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}, Android ${android.os.Build.VERSION.RELEASE}\n")
            append("thread ${thread.name}\n\n")
            append(android.util.Log.getStackTraceString(throwable))
        }
    }
}
