package com.neversoft.launcher.apps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import androidx.compose.ui.graphics.AndroidImageBitmap_androidKt;
import androidx.compose.ui.graphics.ImageBitmap;
import androidx.core.graphics.drawable.DrawableKt;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;

/* compiled from: AppRepository.kt */
@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\nJ\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u0005J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lcom/neversoft/launcher/apps/AppRepository;", "", "<init>", "()V", "TERMUX_PACKAGE", "", "loadApps", "", "Lcom/neversoft/launcher/apps/AppEntry;", "context", "Landroid/content/Context;", "launch", "", "packageName", "launchTermux", "", "app_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class AppRepository {
    public static final int $stable = 0;
    public static final AppRepository INSTANCE = new AppRepository();
    public static final String TERMUX_PACKAGE = "com.termux";

    private AppRepository() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final AppEntry loadApps$lambda$2(Context context, PackageManager packageManager, ResolveInfo resolveInfo) {
        String str;
        Object m6682constructorimpl;
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        if (activityInfo == null || (str = activityInfo.packageName) == null || Intrinsics.areEqual(str, context.getPackageName())) {
            return null;
        }
        CharSequence loadLabel = resolveInfo.loadLabel(packageManager);
        String obj = loadLabel != null ? loadLabel.toString() : null;
        if (obj == null) {
            obj = "";
        }
        String str2 = obj;
        if (StringsKt.isBlank(str2)) {
            str2 = str;
        }
        String str3 = str2;
        try {
            Result.Companion companion = Result.INSTANCE;
            Drawable loadIcon = resolveInfo.loadIcon(packageManager);
            Intrinsics.checkNotNullExpressionValue(loadIcon, "loadIcon(...)");
            m6682constructorimpl = Result.m6682constructorimpl(AndroidImageBitmap_androidKt.asImageBitmap(DrawableKt.toBitmap$default(loadIcon, 96, 96, null, 4, null)));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            m6682constructorimpl = Result.m6682constructorimpl(ResultKt.createFailure(th));
        }
        return new AppEntry(str3, str, (ImageBitmap) (Result.m6688isFailureimpl(m6682constructorimpl) ? null : m6682constructorimpl));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String loadApps$lambda$3(AppEntry it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return it.getPackageName();
    }

    public final void launch(Context context, String packageName) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(packageName, "packageName");
        Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntentForPackage == null) {
            Toast.makeText(context, "Can't open this app", 0).show();
        } else {
            launchIntentForPackage.addFlags(268435456);
            context.startActivity(launchIntentForPackage);
        }
    }

    public final boolean launchTermux(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(TERMUX_PACKAGE);
        if (launchIntentForPackage == null) {
            Toast.makeText(context, "Install Termux to use the Command Prompt", 1).show();
            return false;
        }
        launchIntentForPackage.addFlags(268435456);
        context.startActivity(launchIntentForPackage);
        return true;
    }

    public final List<AppEntry> loadApps(final Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        final PackageManager packageManager = context.getPackageManager();
        Intent addCategory = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER");
        Intrinsics.checkNotNullExpressionValue(addCategory, "addCategory(...)");
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(addCategory, 0);
        Intrinsics.checkNotNullExpressionValue(queryIntentActivities, "queryIntentActivities(...)");
        return SequencesKt.toList(SequencesKt.sortedWith(SequencesKt.distinctBy(SequencesKt.mapNotNull(CollectionsKt.asSequence(queryIntentActivities), new Function1() { // from class: com.neversoft.launcher.apps.AppRepository$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                AppEntry loadApps$lambda$2;
                loadApps$lambda$2 = AppRepository.loadApps$lambda$2(context, packageManager, (ResolveInfo) obj);
                return loadApps$lambda$2;
            }
        }), new Function1() { // from class: com.neversoft.launcher.apps.AppRepository$$ExternalSyntheticLambda1
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                String loadApps$lambda$3;
                loadApps$lambda$3 = AppRepository.loadApps$lambda$3((AppEntry) obj);
                return loadApps$lambda$3;
            }
        }), new Comparator() { // from class: com.neversoft.launcher.apps.AppRepository$loadApps$$inlined$sortedBy$1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Comparator
            public final int compare(T t, T t2) {
                String lowerCase = ((AppEntry) t).getLabel().toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
                String lowerCase2 = ((AppEntry) t2).getLabel().toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue(lowerCase2, "toLowerCase(...)");
                return ComparisonsKt.compareValues(lowerCase, lowerCase2);
            }
        }));
    }
}
