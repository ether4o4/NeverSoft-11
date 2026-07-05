package com.neversoft.launcher.ui;

import androidx.compose.ui.graphics.vector.ImageVector;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ContextMenu.kt */
@Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0014\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n¢\u0006\u0004\b\f\u0010\rJ\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0018\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0007HÆ\u0003J\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u000b0\nHÆ\u0003JC\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nHÆ\u0001J\u0013\u0010\u001d\u001a\u00020\u00072\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001f\u001a\u00020 HÖ\u0001J\t\u0010!\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\b\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016¨\u0006\""}, d2 = {"Lcom/neversoft/launcher/ui/MenuItem;", "", "label", "", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "danger", "", "separatorAfter", "onClick", "Lkotlin/Function0;", "", "<init>", "(Ljava/lang/String;Landroidx/compose/ui/graphics/vector/ImageVector;ZZLkotlin/jvm/functions/Function0;)V", "getLabel", "()Ljava/lang/String;", "getIcon", "()Landroidx/compose/ui/graphics/vector/ImageVector;", "getDanger", "()Z", "getSeparatorAfter", "getOnClick", "()Lkotlin/jvm/functions/Function0;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "", "toString", "app_release"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes2.dex */
public final /* data */ class MenuItem {
    public static final int $stable = 0;
    private final boolean danger;
    private final ImageVector icon;
    private final String label;
    private final Function0<Unit> onClick;
    private final boolean separatorAfter;

    public MenuItem(String label, ImageVector imageVector, boolean z, boolean z2, Function0<Unit> onClick) {
        Intrinsics.checkNotNullParameter(label, "label");
        Intrinsics.checkNotNullParameter(onClick, "onClick");
        this.label = label;
        this.icon = imageVector;
        this.danger = z;
        this.separatorAfter = z2;
        this.onClick = onClick;
    }

    public /* synthetic */ MenuItem(String str, ImageVector imageVector, boolean z, boolean z2, Function0 function0, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, (i & 2) != 0 ? null : imageVector, (i & 4) != 0 ? false : z, (i & 8) != 0 ? false : z2, function0);
    }

    public static /* synthetic */ MenuItem copy$default(MenuItem menuItem, String str, ImageVector imageVector, boolean z, boolean z2, Function0 function0, int i, Object obj) {
        if ((i & 1) != 0) {
            str = menuItem.label;
        }
        if ((i & 2) != 0) {
            imageVector = menuItem.icon;
        }
        ImageVector imageVector2 = imageVector;
        if ((i & 4) != 0) {
            z = menuItem.danger;
        }
        boolean z3 = z;
        if ((i & 8) != 0) {
            z2 = menuItem.separatorAfter;
        }
        boolean z4 = z2;
        if ((i & 16) != 0) {
            function0 = menuItem.onClick;
        }
        return menuItem.copy(str, imageVector2, z3, z4, function0);
    }

    /* renamed from: component1, reason: from getter */
    public final String getLabel() {
        return this.label;
    }

    /* renamed from: component2, reason: from getter */
    public final ImageVector getIcon() {
        return this.icon;
    }

    /* renamed from: component3, reason: from getter */
    public final boolean getDanger() {
        return this.danger;
    }

    /* renamed from: component4, reason: from getter */
    public final boolean getSeparatorAfter() {
        return this.separatorAfter;
    }

    public final Function0<Unit> component5() {
        return this.onClick;
    }

    public final MenuItem copy(String label, ImageVector icon, boolean danger, boolean separatorAfter, Function0<Unit> onClick) {
        Intrinsics.checkNotNullParameter(label, "label");
        Intrinsics.checkNotNullParameter(onClick, "onClick");
        return new MenuItem(label, icon, danger, separatorAfter, onClick);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MenuItem)) {
            return false;
        }
        MenuItem menuItem = (MenuItem) other;
        return Intrinsics.areEqual(this.label, menuItem.label) && Intrinsics.areEqual(this.icon, menuItem.icon) && this.danger == menuItem.danger && this.separatorAfter == menuItem.separatorAfter && Intrinsics.areEqual(this.onClick, menuItem.onClick);
    }

    public final boolean getDanger() {
        return this.danger;
    }

    public final ImageVector getIcon() {
        return this.icon;
    }

    public final String getLabel() {
        return this.label;
    }

    public final Function0<Unit> getOnClick() {
        return this.onClick;
    }

    public final boolean getSeparatorAfter() {
        return this.separatorAfter;
    }

    public int hashCode() {
        int hashCode = this.label.hashCode() * 31;
        ImageVector imageVector = this.icon;
        return ((((((hashCode + (imageVector == null ? 0 : imageVector.hashCode())) * 31) + Boolean.hashCode(this.danger)) * 31) + Boolean.hashCode(this.separatorAfter)) * 31) + this.onClick.hashCode();
    }

    public String toString() {
        return "MenuItem(label=" + this.label + ", icon=" + this.icon + ", danger=" + this.danger + ", separatorAfter=" + this.separatorAfter + ", onClick=" + this.onClick + ")";
    }
}
