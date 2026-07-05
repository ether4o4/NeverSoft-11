package com.neversoft.launcher.ui;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.SettingsKt;
import androidx.compose.material.icons.filled.TerminalKt;
import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import com.neversoft.launcher.ui.theme.NsColor;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

/* compiled from: StartMenu.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class ComposableSingletons$StartMenuKt {
    public static final ComposableSingletons$StartMenuKt INSTANCE = new ComposableSingletons$StartMenuKt();

    /* renamed from: lambda-1, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f68lambda1 = ComposableLambdaKt.composableLambdaInstance(553813403, false, new Function2<Composer, Integer, Unit>() { // from class: com.neversoft.launcher.ui.ComposableSingletons$StartMenuKt$lambda-1$1
        @Override // kotlin.jvm.functions.Function2
        public /* bridge */ /* synthetic */ Unit invoke(Composer composer, Integer num) {
            invoke(composer, num.intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer composer, int i) {
            if ((i & 3) == 2 && composer.getSkipping()) {
                composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(553813403, i, -1, "com.neversoft.launcher.ui.ComposableSingletons$StartMenuKt.lambda-1.<anonymous> (StartMenu.kt:100)");
            }
            IconKt.m1830Iconww6aTOc(TerminalKt.getTerminal(Icons.Filled.INSTANCE), (String) null, SizeKt.m726size3ABfNKs(Modifier.INSTANCE, Dp.m6300constructorimpl(30)), LauncherState.INSTANCE.m6621getAccent0d7_KjU(), composer, 432, 0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-2, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f69lambda2 = ComposableLambdaKt.composableLambdaInstance(-1495957102, false, new Function2<Composer, Integer, Unit>() { // from class: com.neversoft.launcher.ui.ComposableSingletons$StartMenuKt$lambda-2$1
        @Override // kotlin.jvm.functions.Function2
        public /* bridge */ /* synthetic */ Unit invoke(Composer composer, Integer num) {
            invoke(composer, num.intValue());
            return Unit.INSTANCE;
        }

        public final void invoke(Composer composer, int i) {
            if ((i & 3) == 2 && composer.getSkipping()) {
                composer.skipToGroupEnd();
                return;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1495957102, i, -1, "com.neversoft.launcher.ui.ComposableSingletons$StartMenuKt.lambda-2.<anonymous> (StartMenu.kt:108)");
            }
            IconKt.m1830Iconww6aTOc(SettingsKt.getSettings(Icons.Filled.INSTANCE), (String) null, SizeKt.m726size3ABfNKs(Modifier.INSTANCE, Dp.m6300constructorimpl(30)), NsColor.INSTANCE.m6664getText0d7_KjU(), composer, 3504, 0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: getLambda-1$app_release, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m6605getLambda1$app_release() {
        return f68lambda1;
    }

    /* renamed from: getLambda-2$app_release, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m6606getLambda2$app_release() {
        return f69lambda2;
    }
}
