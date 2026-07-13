package com.neversoft.launcher

import android.app.Application
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LauncherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        installCrashLogger()
    }

    // Persist uncaught crashes so a force-close can be diagnosed without adb.
    // The stack trace is appended to files/crash_log.txt (also copied to the
    // external files dir so it's reachable in File Explorer), then the default
    // handler runs so the OS still reports the crash normally.
    private fun installCrashLogger() {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching {
                val ts = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
                val text = buildString {
                    append("=== NeverSoft 11 crash @ $ts (thread ${thread.name}) ===\n")
                    append(android.util.Log.getStackTraceString(throwable))
                    append("\n\n")
                }
                File(filesDir, "crash_log.txt").appendText(text)
                getExternalFilesDir(null)?.let { File(it, "crash_log.txt").appendText(text) }
                // Also drop it in Downloads (app holds all-files access) so it's
                // easy to find and share when diagnosing a force-close.
                runCatching {
                    val downloads = android.os.Environment.getExternalStoragePublicDirectory(
                        android.os.Environment.DIRECTORY_DOWNLOADS,
                    )
                    File(downloads, "neversoft11_crash_log.txt").appendText(text)
                }
            }
            previous?.uncaughtException(thread, throwable)
        }
    }
}
