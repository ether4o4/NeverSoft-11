package com.neversoft.launcher.ui;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.GridViewKt;
import androidx.compose.material.icons.filled.SearchKt;
import androidx.compose.material.icons.filled.ViewModuleKt;
import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import com.neversoft.launcher.ui.theme.NsColor;
import com.neversoft.launcher.ui.theme.NsDim;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

/* compiled from: Taskbar.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class ComposableSingletons$TaskbarKt {
    public static final ComposableSingletons$TaskbarKt INSTANCE = new ComposableSingletons$TaskbarKt();

    /* renamed from: lambda-1, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f70lambda1 = ComposableLambdaKt.composableLambdaInstance(108085888, false, new Function2<Composer, Integer, Unit>() { // from class: com.neversoft.launcher.ui.ComposableSingletons$TaskbarKt$lambda-1$1
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
                ComposerKt.traceEventStart(108085888, i, -1, "com.neversoft.launcher.ui.ComposableSingletons$TaskbarKt.lambda-1.<anonymous> (Taskbar.kt:95)");
            }
            IconKt.m1830Iconww6aTOc(GridViewKt.getGridView(Icons.Filled.INSTANCE), "Start", SizeKt.m726size3ABfNKs(Modifier.INSTANCE, NsDim.INSTANCE.m6672getTaskbarGlyphD9Ej5fM()), LauncherState.INSTANCE.m6621getAccent0d7_KjU(), composer, 432, 0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-2, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f71lambda2 = ComposableLambdaKt.composableLambdaInstance(1631513897, false, new Function2<Composer, Integer, Unit>() { // from class: com.neversoft.launcher.ui.ComposableSingletons$TaskbarKt$lambda-2$1
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
                ComposerKt.traceEventStart(1631513897, i, -1, "com.neversoft.launcher.ui.ComposableSingletons$TaskbarKt.lambda-2.<anonymous> (Taskbar.kt:101)");
            }
            IconKt.m1830Iconww6aTOc(SearchKt.getSearch(Icons.Filled.INSTANCE), "Search", SizeKt.m726size3ABfNKs(Modifier.INSTANCE, NsDim.INSTANCE.m6672getTaskbarGlyphD9Ej5fM()), NsColor.INSTANCE.m6664getText0d7_KjU(), composer, 3504, 0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-3, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f72lambda3 = ComposableLambdaKt.composableLambdaInstance(-567617848, false, new Function2<Composer, Integer, Unit>() { // from class: com.neversoft.launcher.ui.ComposableSingletons$TaskbarKt$lambda-3$1
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
                ComposerKt.traceEventStart(-567617848, i, -1, "com.neversoft.launcher.ui.ComposableSingletons$TaskbarKt.lambda-3.<anonymous> (Taskbar.kt:107)");
            }
            IconKt.m1830Iconww6aTOc(ViewModuleKt.getViewModule(Icons.Filled.INSTANCE), "Task view", SizeKt.m726size3ABfNKs(Modifier.INSTANCE, NsDim.INSTANCE.m6672getTaskbarGlyphD9Ej5fM()), NsColor.INSTANCE.m6664getText0d7_KjU(), composer, 3504, 0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: lambda-4, reason: not valid java name */
    public static Function2<Composer, Integer, Unit> f73lambda4 = ComposableLambdaKt.composableLambdaInstance(1528217703, false, new Function2<Composer, Integer, Unit>() { // from class: com.neversoft.launcher.ui.ComposableSingletons$TaskbarKt$lambda-4$1
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
                ComposerKt.traceEventStart(1528217703, i, -1, "com.neversoft.launcher.ui.ComposableSingletons$TaskbarKt.lambda-4.<anonymous> (Taskbar.kt:113)");
            }
            IconKt.m1830Iconww6aTOc(androidx.compose.material.icons.filled.WidgetsKt.getWidgets(Icons.Filled.INSTANCE), "Widgets", SizeKt.m726size3ABfNKs(Modifier.INSTANCE, NsDim.INSTANCE.m6672getTaskbarGlyphD9Ej5fM()), NsColor.INSTANCE.m6664getText0d7_KjU(), composer, 3504, 0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
    });

    /* renamed from: getLambda-1$app_release, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m6607getLambda1$app_release() {
        return f70lambda1;
    }

    /* renamed from: getLambda-2$app_release, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m6608getLambda2$app_release() {
        return f71lambda2;
    }

    /* renamed from: getLambda-3$app_release, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m6609getLambda3$app_release() {
        return f72lambda3;
    }

    /* renamed from: getLambda-4$app_release, reason: not valid java name */
    public final Function2<Composer, Integer, Unit> m6610getLambda4$app_release() {
        return f73lambda4;
    }
}
