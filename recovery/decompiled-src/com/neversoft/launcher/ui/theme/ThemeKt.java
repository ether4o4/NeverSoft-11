package com.neversoft.launcher.ui.theme;

import androidx.compose.material3.ColorSchemeKt;
import androidx.compose.material3.MaterialThemeKt;
import androidx.compose.material3.Typography;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.layout.LayoutKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Theme.kt */
@Metadata(d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a \u0010\u0000\u001a\u00020\u00012\u0011\u0010\u0002\u001a\r\u0012\u0004\u0012\u00020\u00010\u0003¢\u0006\u0002\b\u0004H\u0007¢\u0006\u0002\u0010\u0005¨\u0006\u0006"}, d2 = {"NeverSoftTheme", "", "content", "Lkotlin/Function0;", "Landroidx/compose/runtime/Composable;", "(Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;I)V", "app_release"}, k = 2, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class ThemeKt {
    public static final void NeverSoftTheme(final Function2<? super Composer, ? super Integer, Unit> content, Composer composer, final int i) {
        int i2;
        Intrinsics.checkNotNullParameter(content, "content");
        Composer startRestartGroup = composer.startRestartGroup(1405311169);
        if ((i & 6) == 0) {
            i2 = (startRestartGroup.changedInstance(content) ? 4 : 2) | i;
        } else {
            i2 = i;
        }
        if ((i2 & 3) == 2 && startRestartGroup.getSkipping()) {
            startRestartGroup.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1405311169, i2, -1, "com.neversoft.launcher.ui.theme.NeverSoftTheme (Theme.kt:12)");
            }
            MaterialThemeKt.MaterialTheme(ColorSchemeKt.m1611darkColorSchemeCXl9yA$default(NsColor.INSTANCE.m6646getAccent0d7_KjU(), NsColor.INSTANCE.m6664getText0d7_KjU(), 0L, 0L, 0L, NsColor.INSTANCE.m6647getAccentLight0d7_KjU(), 0L, 0L, 0L, 0L, 0L, 0L, 0L, NsColor.INSTANCE.m6659getSolid0d7_KjU(), NsColor.INSTANCE.m6664getText0d7_KjU(), NsColor.INSTANCE.m6656getLayer0d7_KjU(), NsColor.INSTANCE.m6664getText0d7_KjU(), NsColor.INSTANCE.m6657getMica0d7_KjU(), NsColor.INSTANCE.m6665getTextSecondary0d7_KjU(), 0L, 0L, 0L, NsColor.INSTANCE.m6655getDanger0d7_KjU(), 0L, 0L, 0L, NsColor.INSTANCE.m6661getStrokeStrong0d7_KjU(), 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, -71819300, 15, null), null, new Typography(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, LayoutKt.LargeDimension, null), content, startRestartGroup, ((i2 << 9) & 7168) | 384, 2);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        ScopeUpdateScope endRestartGroup = startRestartGroup.endRestartGroup();
        if (endRestartGroup != null) {
            endRestartGroup.updateScope(new Function2() { // from class: com.neversoft.launcher.ui.theme.ThemeKt$$ExternalSyntheticLambda0
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    Unit NeverSoftTheme$lambda$0;
                    NeverSoftTheme$lambda$0 = ThemeKt.NeverSoftTheme$lambda$0(Function2.this, i, (Composer) obj, ((Integer) obj2).intValue());
                    return NeverSoftTheme$lambda$0;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit NeverSoftTheme$lambda$0(Function2 function2, int i, Composer composer, int i2) {
        NeverSoftTheme(function2, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }
}
