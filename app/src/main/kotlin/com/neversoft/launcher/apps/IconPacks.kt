package com.neversoft.launcher.apps

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

// Loads third-party icon packs. Packs from any launcher ecosystem work:
// they all ship an appfilter.xml mapping activity components to drawables,
// discoverable through the standard theme intents (ADW, Nova, Apex, GO,
// Smart Launcher, ...).
object IconPacks {
    data class PackInfo(val packageName: String, val label: String)

    private val THEME_INTENTS = listOf(
        "org.adw.launcher.THEMES",
        "com.novalauncher.THEME",
        "com.anddoes.launcher.THEME",
        "com.gau.go.launcherex.theme",
        "ginlemon.smartlauncher.THEMES",
        "com.fede.launcher.THEME_ICONPACK",
        "org.adw.launcher.icons.ACTION_PICK_ICON",
    )

    fun listPacks(context: Context): List<PackInfo> {
        val pm = context.packageManager
        return THEME_INTENTS
            .flatMap { action -> runCatching { pm.queryIntentActivities(Intent(action), 0) }.getOrDefault(emptyList()) }
            .map { it.activityInfo.packageName }
            .distinct()
            .mapNotNull { pkg ->
                runCatching {
                    PackInfo(pkg, pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString())
                }.getOrNull()
            }
            .sortedBy { it.label.lowercase() }
    }

    // Cache of the active pack's component->drawable map
    private var cachedPackPkg: String? = null
    private var cachedMap: Map<String, String> = emptyMap()
    private var cachedRes: Resources? = null

    @Synchronized
    fun getIcon(context: Context, packPkg: String, appPkg: String, activityName: String?): Bitmap? {
        if (packPkg.isEmpty()) return null
        ensureLoaded(context, packPkg)
        val res = cachedRes ?: return null
        if (cachedMap.isEmpty()) return null

        val exactKey = activityName?.let { "ComponentInfo{$appPkg/$it}" }
        val drawableName = (exactKey?.let { cachedMap[it] })
            ?: cachedMap.entries.firstOrNull { it.key.startsWith("ComponentInfo{$appPkg/") }?.value
            ?: return null
        val id = res.getIdentifier(drawableName, "drawable", packPkg)
        if (id == 0) return null
        return runCatching {
            androidx.core.content.res.ResourcesCompat.getDrawable(res, id, null)
                ?.let { renderPadded(it, 168, ICON_CONTENT_FRACTION) }
        }.getOrNull()
    }

    // Icon-pack drawables are usually full-bleed (edge-to-edge) squares, which
    // render larger than typical app icons (those carry transparent margin).
    // Draw the pack icon inset on a transparent canvas so it sits inside the
    // tile with the same visual weight instead of overflowing the app space.
    private const val ICON_CONTENT_FRACTION = 0.82f

    private fun renderPadded(
        drawable: android.graphics.drawable.Drawable,
        sizePx: Int,
        contentFraction: Float,
    ): Bitmap {
        val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bmp)
        val inset = (sizePx * (1f - contentFraction) / 2f).toInt()
        drawable.setBounds(inset, inset, sizePx - inset, sizePx - inset)
        drawable.draw(canvas)
        return bmp
    }

    private fun ensureLoaded(context: Context, packPkg: String) {
        if (cachedPackPkg == packPkg && cachedRes != null) return
        cachedPackPkg = packPkg
        cachedMap = emptyMap()
        cachedRes = null
        runCatching {
            val res = context.packageManager.getResourcesForApplication(packPkg)
            cachedRes = res
            cachedMap = parseAppFilter(res, packPkg)
        }
    }

    private fun parseAppFilter(res: Resources, packPkg: String): Map<String, String> {
        val map = HashMap<String, String>()
        val parser: XmlPullParser = run {
            val xmlId = res.getIdentifier("appfilter", "xml", packPkg)
            if (xmlId != 0) {
                res.getXml(xmlId)
            } else {
                // Some packs ship appfilter.xml in assets instead of res/xml
                val factory = XmlPullParserFactory.newInstance()
                factory.newPullParser().apply {
                    setInput(res.assets.open("appfilter.xml"), "utf-8")
                }
            }
        }
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG && parser.name == "item") {
                var component: String? = null
                var drawable: String? = null
                for (i in 0 until parser.attributeCount) {
                    when (parser.getAttributeName(i)) {
                        "component" -> component = parser.getAttributeValue(i)
                        "drawable" -> drawable = parser.getAttributeValue(i)
                    }
                }
                if (component != null && drawable != null) {
                    map.putIfAbsent(component, drawable)
                }
            }
            event = parser.next()
        }
        return map
    }
}
