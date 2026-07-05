package com.neversoft.launcher.ui;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* compiled from: Shell.kt */
@Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\t¨\u0006\n"}, d2 = {"Lcom/neversoft/launcher/ui/Overlay;", "", "<init>", "(Ljava/lang/String;I)V", "None", "Start", "QuickSettings", "Notifications", "TaskView", "Widgets", "app_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class Overlay {
    private static final /* synthetic */ EnumEntries $ENTRIES;
    private static final /* synthetic */ Overlay[] $VALUES;
    public static final Overlay None = new Overlay("None", 0);
    public static final Overlay Start = new Overlay("Start", 1);
    public static final Overlay QuickSettings = new Overlay("QuickSettings", 2);
    public static final Overlay Notifications = new Overlay("Notifications", 3);
    public static final Overlay TaskView = new Overlay("TaskView", 4);
    public static final Overlay Widgets = new Overlay("Widgets", 5);

    private static final /* synthetic */ Overlay[] $values() {
        return new Overlay[]{None, Start, QuickSettings, Notifications, TaskView, Widgets};
    }

    static {
        Overlay[] $values = $values();
        $VALUES = $values;
        $ENTRIES = EnumEntriesKt.enumEntries($values);
    }

    private Overlay(String str, int i) {
    }

    public static EnumEntries<Overlay> getEntries() {
        return $ENTRIES;
    }

    public static Overlay valueOf(String str) {
        return (Overlay) Enum.valueOf(Overlay.class, str);
    }

    public static Overlay[] values() {
        return (Overlay[]) $VALUES.clone();
    }
}
