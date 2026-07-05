package com.neversoft.launcher.ui;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* compiled from: LauncherState.kt */
@Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007j\u0002\b\bj\u0002\b\t¨\u0006\n"}, d2 = {"Lcom/neversoft/launcher/ui/LauncherApp;", "", "title", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;)V", "getTitle", "()Ljava/lang/String;", "Settings", "About", "app_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class LauncherApp {
    private static final /* synthetic */ EnumEntries $ENTRIES;
    private static final /* synthetic */ LauncherApp[] $VALUES;
    private final String title;
    public static final LauncherApp Settings = new LauncherApp("Settings", 0, "Settings");
    public static final LauncherApp About = new LauncherApp("About", 1, "About this PC");

    private static final /* synthetic */ LauncherApp[] $values() {
        return new LauncherApp[]{Settings, About};
    }

    static {
        LauncherApp[] $values = $values();
        $VALUES = $values;
        $ENTRIES = EnumEntriesKt.enumEntries($values);
    }

    private LauncherApp(String str, int i, String str2) {
        this.title = str2;
    }

    public static EnumEntries<LauncherApp> getEntries() {
        return $ENTRIES;
    }

    public static LauncherApp valueOf(String str) {
        return (LauncherApp) Enum.valueOf(LauncherApp.class, str);
    }

    public static LauncherApp[] values() {
        return (LauncherApp[]) $VALUES.clone();
    }

    public final String getTitle() {
        return this.title;
    }
}
