package com.neversoft.launcher;

import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import com.neversoft.launcher.ui.ShellKt;
import com.neversoft.launcher.ui.theme.ThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

/* compiled from: MainActivity.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class ComposableSingletons$MainActivityKt {
    public static final ComposableSingletons$MainActivityKt INSTANCE = new ComposableSingletons$MainActivityKt();

    /* renamed from: lambda-1, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f64lambda1 = ComposableLambdaKt.composableLambdaInstance(-2125513219, false, new Function2<Composer, Integer, Unit>() { // from class: com.neversoft.launcher.ComposableSingletons$MainActivityKt$lambda-1$1
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
                ComposerKt.traceEventStart(-2125513219, i, -1, "com.neversoft.launcher.ComposableSingletons$MainActivityKt.lambda-1.<anonymous> (MainActivity.kt:32)");
            }
            ShellKt.Shell(composer, 0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-2, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f65lambda2 = ComposableLambdaKt.composableLambdaInstance(1511782621, false, new Function2<Composer, Integer, Unit>() { // from class: com.neversoft.launcher.ComposableSingletons$MainActivityKt$lambda-2$1
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
                ComposerKt.traceEventStart(1511782621, i, -1, "com.neversoft.launcher.ComposableSingletons$MainActivityKt.lambda-2.<anonymous> (MainActivity.kt:31)");
            }
            ThemeKt.NeverSoftTheme(ComposableSingletons$MainActivityKt.INSTANCE.m6600getLambda1$app_release(), composer, 6);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: getLambda-1$app_release, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m6600getLambda1$app_release() {
        return f64lambda1;
    }

    /* renamed from: getLambda-2$app_release, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m6601getLambda2$app_release() {
        return f65lambda2;
    }
}
