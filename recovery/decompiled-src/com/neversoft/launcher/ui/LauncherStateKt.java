package com.neversoft.launcher.ui;

import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import com.neversoft.launcher.ui.theme.NsColor;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;

/* compiled from: LauncherState.kt */
@Metadata(d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\"\u0017\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u0003\u0010\u0004\"\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u0004¨\u0006\b"}, d2 = {"AccentOptions", "", "Landroidx/compose/ui/graphics/Color;", "getAccentOptions", "()Ljava/util/List;", "Wallpapers", "Landroidx/compose/ui/graphics/Brush;", "getWallpapers", "app_release"}, k = 2, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class LauncherStateKt {
    private static final List<Color> AccentOptions = CollectionsKt.listOf((Object[]) new Color[]{Color.m3830boximpl(NsColor.INSTANCE.m6646getAccent0d7_KjU()), Color.m3830boximpl(ColorKt.Color(4283220735L)), Color.m3830boximpl(ColorKt.Color(4288503039L)), Color.m3830boximpl(ColorKt.Color(4294925450L)), Color.m3830boximpl(ColorKt.Color(4294937896L)), Color.m3830boximpl(ColorKt.Color(4282372218L)), Color.m3830boximpl(ColorKt.Color(4294956554L)), Color.m3830boximpl(ColorKt.Color(4284602070L))});
    private static final List<Brush> Wallpapers = CollectionsKt.listOf((Object[]) new Brush[]{Brush.Companion.m3793radialGradientP_VxKs$default(Brush.INSTANCE, CollectionsKt.listOf((Object[]) new Color[]{Color.m3830boximpl(ColorKt.Color(4280974817L)), Color.m3830boximpl(ColorKt.Color(4279516279L)), Color.m3830boximpl(ColorKt.Color(4278719014L))}), 0, 0.0f, 0, 14, (Object) null), Brush.Companion.m3793radialGradientP_VxKs$default(Brush.INSTANCE, CollectionsKt.listOf((Object[]) new Color[]{Color.m3830boximpl(ColorKt.Color(4284164742L)), Color.m3830boximpl(ColorKt.Color(4280947792L)), Color.m3830boximpl(ColorKt.Color(4278912791L))}), 0, 0.0f, 0, 14, (Object) null), Brush.Companion.m3793radialGradientP_VxKs$default(Brush.INSTANCE, CollectionsKt.listOf((Object[]) new Color[]{Color.m3830boximpl(ColorKt.Color(4279204459L)), Color.m3830boximpl(ColorKt.Color(4278861133L)), Color.m3830boximpl(ColorKt.Color(4278589478L))}), 0, 0.0f, 0, 14, (Object) null), Brush.Companion.m3791linearGradientmHitzGk$default(Brush.INSTANCE, CollectionsKt.listOf((Object[]) new Color[]{Color.m3830boximpl(ColorKt.Color(4294934111L)), Color.m3830boximpl(ColorKt.Color(4289809038L)), Color.m3830boximpl(ColorKt.Color(4282066535L))}), 0, 0, 0, 14, (Object) null)});

    public static final List<Color> getAccentOptions() {
        return AccentOptions;
    }

    public static final List<Brush> getWallpapers() {
        return Wallpapers;
    }
}
