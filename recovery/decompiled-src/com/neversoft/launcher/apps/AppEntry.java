package com.neversoft.launcher.apps;

import androidx.compose.ui.graphics.ImageBitmap;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: AppRepository.kt */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0006HÆ\u0003J)\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006HÆ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0015\u001a\u00020\u0016HÖ\u0001J\t\u0010\u0017\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u0018"}, d2 = {"Lcom/neversoft/launcher/apps/AppEntry;", "", "label", "", "packageName", "icon", "Landroidx/compose/ui/graphics/ImageBitmap;", "<init>", "(Ljava/lang/String;Ljava/lang/String;Landroidx/compose/ui/graphics/ImageBitmap;)V", "getLabel", "()Ljava/lang/String;", "getPackageName", "getIcon", "()Landroidx/compose/ui/graphics/ImageBitmap;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "app_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes2.dex */
public final /* data */ class AppEntry {
    public static final int $stable = 8;
    private final ImageBitmap icon;
    private final String label;
    private final String packageName;

    public AppEntry(String label, String packageName, ImageBitmap imageBitmap) {
        Intrinsics.checkNotNullParameter(label, "label");
        Intrinsics.checkNotNullParameter(packageName, "packageName");
        this.label = label;
        this.packageName = packageName;
        this.icon = imageBitmap;
    }

    public static /* synthetic */ AppEntry copy$default(AppEntry appEntry, String str, String str2, ImageBitmap imageBitmap, int i, Object obj) {
        if ((i & 1) != 0) {
            str = appEntry.label;
        }
        if ((i & 2) != 0) {
            str2 = appEntry.packageName;
        }
        if ((i & 4) != 0) {
            imageBitmap = appEntry.icon;
        }
        return appEntry.copy(str, str2, imageBitmap);
    }

    /* renamed from: component1, reason: from getter */
    public final String getLabel() {
        return this.label;
    }

    /* renamed from: component2, reason: from getter */
    public final String getPackageName() {
        return this.packageName;
    }

    /* renamed from: component3, reason: from getter */
    public final ImageBitmap getIcon() {
        return this.icon;
    }

    public final AppEntry copy(String label, String packageName, ImageBitmap icon) {
        Intrinsics.checkNotNullParameter(label, "label");
        Intrinsics.checkNotNullParameter(packageName, "packageName");
        return new AppEntry(label, packageName, icon);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AppEntry)) {
            return false;
        }
        AppEntry appEntry = (AppEntry) other;
        return Intrinsics.areEqual(this.label, appEntry.label) && Intrinsics.areEqual(this.packageName, appEntry.packageName) && Intrinsics.areEqual(this.icon, appEntry.icon);
    }

    public final ImageBitmap getIcon() {
        return this.icon;
    }

    public final String getLabel() {
        return this.label;
    }

    public final String getPackageName() {
        return this.packageName;
    }

    public int hashCode() {
        int hashCode = ((this.label.hashCode() * 31) + this.packageName.hashCode()) * 31;
        ImageBitmap imageBitmap = this.icon;
        return hashCode + (imageBitmap == null ? 0 : imageBitmap.hashCode());
    }

    public String toString() {
        return "AppEntry(label=" + this.label + ", packageName=" + this.packageName + ", icon=" + this.icon + ")";
    }
}
