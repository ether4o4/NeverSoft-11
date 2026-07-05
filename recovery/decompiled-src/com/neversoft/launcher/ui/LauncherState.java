package com.neversoft.launcher.ui;

import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt__SnapshotStateKt;
import androidx.compose.ui.graphics.Color;
import com.neversoft.launcher.ui.theme.NsColor;
import com.neversoft.launcher.ui.theme.NsDim;
import kotlin.Metadata;

/* compiled from: LauncherState.kt */
@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R+\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u00058F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u000b\u0010\f\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR+\u0010\u000e\u001a\u00020\r2\u0006\u0010\u0004\u001a\u00020\r8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0013\u0010\f\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R+\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0004\u001a\u00020\u00148F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u001a\u0010\f\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u0011\u0010\u001b\u001a\u00020\u001c8F¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u001e¨\u0006\u001f"}, d2 = {"Lcom/neversoft/launcher/ui/LauncherState;", "", "<init>", "()V", "<set-?>", "Landroidx/compose/ui/graphics/Color;", "accent", "getAccent-0d7_KjU", "()J", "setAccent-8_81llA", "(J)V", "accent$delegate", "Landroidx/compose/runtime/MutableState;", "", "wallpaperIndex", "getWallpaperIndex", "()I", "setWallpaperIndex", "(I)V", "wallpaperIndex$delegate", "", "taskbarSmall", "getTaskbarSmall", "()Z", "setTaskbarSmall", "(Z)V", "taskbarSmall$delegate", "taskbarHeight", "Landroidx/compose/ui/unit/Dp;", "getTaskbarHeight-D9Ej5fM", "()F", "app_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class LauncherState {
    public static final int $stable = 0;
    public static final LauncherState INSTANCE = new LauncherState();

    /* renamed from: accent$delegate, reason: from kotlin metadata */
    private static final MutableState accent = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(Color.m3830boximpl(NsColor.INSTANCE.m6646getAccent0d7_KjU()), null, 2, null);

    /* renamed from: wallpaperIndex$delegate, reason: from kotlin metadata */
    private static final MutableState wallpaperIndex = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0, null, 2, null);

    /* renamed from: taskbarSmall$delegate, reason: from kotlin metadata */
    private static final MutableState taskbarSmall = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(false, null, 2, null);

    private LauncherState() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: getAccent-0d7_KjU, reason: not valid java name */
    public final long m6621getAccent0d7_KjU() {
        return ((Color) accent.getValue()).m3850unboximpl();
    }

    /* renamed from: getTaskbarHeight-D9Ej5fM, reason: not valid java name */
    public final float m6622getTaskbarHeightD9Ej5fM() {
        return getTaskbarSmall() ? NsDim.INSTANCE.m6674getTaskbarHeightSmallD9Ej5fM() : NsDim.INSTANCE.m6673getTaskbarHeightD9Ej5fM();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final boolean getTaskbarSmall() {
        return ((Boolean) taskbarSmall.getValue()).booleanValue();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final int getWallpaperIndex() {
        return ((Number) wallpaperIndex.getValue()).intValue();
    }

    /* renamed from: setAccent-8_81llA, reason: not valid java name */
    public final void m6623setAccent8_81llA(long j) {
        accent.setValue(Color.m3830boximpl(j));
    }

    public final void setTaskbarSmall(boolean z) {
        taskbarSmall.setValue(Boolean.valueOf(z));
    }

    public final void setWallpaperIndex(int i) {
        wallpaperIndex.setValue(Integer.valueOf(i));
    }
}
