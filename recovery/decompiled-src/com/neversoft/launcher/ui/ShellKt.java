package com.neversoft.launcher.ui;

import android.content.Context;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.ProvidableCompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.SnapshotStateKt__SnapshotStateKt;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.snapshots.SnapshotStateList;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import com.neversoft.launcher.apps.AppRepository;
import com.neversoft.launcher.ui.apps.SettingsAppKt;
import com.neversoft.launcher.ui.window.AppWindowKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;

/* compiled from: Shell.kt */
@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\u001a\r\u0010\u0000\u001a\u00020\u0001H\u0007¢\u0006\u0002\u0010\u0002¨\u0006\u0003²\u0006\n\u0010\u0004\u001a\u00020\u0005X\u008a\u008e\u0002"}, d2 = {"Shell", "", "(Landroidx/compose/runtime/Composer;I)V", "app_release", "overlay", "Lcom/neversoft/launcher/ui/Overlay;"}, k = 2, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class ShellKt {

    /* compiled from: Shell.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[Overlay.values().length];
            try {
                iArr[Overlay.Start.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[Overlay.QuickSettings.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[Overlay.Notifications.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                iArr[Overlay.TaskView.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                iArr[Overlay.Widgets.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                iArr[Overlay.None.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public static final void Shell(Composer composer, final int i) {
        Composer composer2;
        Composer startRestartGroup = composer.startRestartGroup(1688995389);
        if (i == 0 && startRestartGroup.getSkipping()) {
            startRestartGroup.skipToGroupEnd();
            composer2 = startRestartGroup;
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1688995389, i, -1, "com.neversoft.launcher.ui.Shell (Shell.kt:24)");
            }
            ProvidableCompositionLocal<Context> localContext = AndroidCompositionLocals_androidKt.getLocalContext();
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, 2023513938, "CC:CompositionLocal.kt#9igjgp");
            Object consume = startRestartGroup.consume(localContext);
            ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
            final Context context = (Context) consume;
            startRestartGroup.startReplaceGroup(-1559156085);
            Object rememberedValue = startRestartGroup.rememberedValue();
            if (rememberedValue == Composer.INSTANCE.getEmpty()) {
                rememberedValue = AppRepository.INSTANCE.loadApps(context);
                startRestartGroup.updateRememberedValue(rememberedValue);
            }
            List list = (List) rememberedValue;
            startRestartGroup.endReplaceGroup();
            startRestartGroup.startReplaceGroup(-1559154040);
            Object rememberedValue2 = startRestartGroup.rememberedValue();
            if (rememberedValue2 == Composer.INSTANCE.getEmpty()) {
                rememberedValue2 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(Overlay.None, null, 2, null);
                startRestartGroup.updateRememberedValue(rememberedValue2);
            }
            final MutableState mutableState = (MutableState) rememberedValue2;
            startRestartGroup.endReplaceGroup();
            startRestartGroup.startReplaceGroup(-1559152115);
            Object rememberedValue3 = startRestartGroup.rememberedValue();
            if (rememberedValue3 == Composer.INSTANCE.getEmpty()) {
                rememberedValue3 = SnapshotStateKt.mutableStateListOf();
                startRestartGroup.updateRememberedValue(rememberedValue3);
            }
            final SnapshotStateList<LauncherApp> snapshotStateList = (SnapshotStateList) rememberedValue3;
            startRestartGroup.endReplaceGroup();
            Modifier background$default = BackgroundKt.background$default(SizeKt.fillMaxSize$default(Modifier.INSTANCE, 0.0f, 1, null), LauncherStateKt.getWallpapers().get(RangesKt.coerceIn(LauncherState.INSTANCE.getWallpaperIndex(), 0, LauncherStateKt.getWallpapers().size() - 1)), null, 0.0f, 6, null);
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, 733328855, "CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
            MeasurePolicy maybeCachedBoxMeasurePolicy = BoxKt.maybeCachedBoxMeasurePolicy(Alignment.INSTANCE.getTopStart(), false);
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            int currentCompositeKeyHash = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
            CompositionLocalMap currentCompositionLocalMap = startRestartGroup.getCurrentCompositionLocalMap();
            Modifier materializeModifier = ComposedModifierKt.materializeModifier(startRestartGroup, background$default);
            Function0<ComposeUiNode> constructor = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(startRestartGroup.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            startRestartGroup.startReusableNode();
            if (startRestartGroup.getInserting()) {
                startRestartGroup.createNode(constructor);
            } else {
                startRestartGroup.useNode();
            }
            Composer m3333constructorimpl = Updater.m3333constructorimpl(startRestartGroup);
            Updater.m3340setimpl(m3333constructorimpl, maybeCachedBoxMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m3340setimpl(m3333constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
            if (m3333constructorimpl.getInserting() || !Intrinsics.areEqual(m3333constructorimpl.rememberedValue(), Integer.valueOf(currentCompositeKeyHash))) {
                m3333constructorimpl.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash));
                m3333constructorimpl.apply(Integer.valueOf(currentCompositeKeyHash), setCompositeKeyHash);
            }
            Updater.m3340setimpl(m3333constructorimpl, materializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -2146769399, "C73@3429L9:Box.kt#2w3rfo");
            BoxScopeInstance boxScopeInstance = BoxScopeInstance.INSTANCE;
            startRestartGroup.startReplaceGroup(-1420530305);
            Object rememberedValue4 = startRestartGroup.rememberedValue();
            if (rememberedValue4 == Composer.INSTANCE.getEmpty()) {
                rememberedValue4 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda0
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        Unit Shell$lambda$46$lambda$6$lambda$5;
                        Shell$lambda$46$lambda$6$lambda$5 = ShellKt.Shell$lambda$46$lambda$6$lambda$5(SnapshotStateList.this, mutableState);
                        return Shell$lambda$46$lambda$6$lambda$5;
                    }
                };
                startRestartGroup.updateRememberedValue(rememberedValue4);
            }
            Function0 function0 = (Function0) rememberedValue4;
            startRestartGroup.endReplaceGroup();
            startRestartGroup.startReplaceGroup(-1420528356);
            Object rememberedValue5 = startRestartGroup.rememberedValue();
            if (rememberedValue5 == Composer.INSTANCE.getEmpty()) {
                rememberedValue5 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda2
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        Unit Shell$lambda$46$lambda$8$lambda$7;
                        Shell$lambda$46$lambda$8$lambda$7 = ShellKt.Shell$lambda$46$lambda$8$lambda$7(SnapshotStateList.this, mutableState);
                        return Shell$lambda$46$lambda$8$lambda$7;
                    }
                };
                startRestartGroup.updateRememberedValue(rememberedValue5);
            }
            Function0 function02 = (Function0) rememberedValue5;
            startRestartGroup.endReplaceGroup();
            startRestartGroup.startReplaceGroup(-1420526363);
            boolean changedInstance = startRestartGroup.changedInstance(context);
            Object rememberedValue6 = startRestartGroup.rememberedValue();
            if (changedInstance || rememberedValue6 == Composer.INSTANCE.getEmpty()) {
                rememberedValue6 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda4
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        Unit Shell$lambda$46$lambda$10$lambda$9;
                        Shell$lambda$46$lambda$10$lambda$9 = ShellKt.Shell$lambda$46$lambda$10$lambda$9(context);
                        return Shell$lambda$46$lambda$10$lambda$9;
                    }
                };
                startRestartGroup.updateRememberedValue(rememberedValue6);
            }
            startRestartGroup.endReplaceGroup();
            DesktopKt.Desktop(function0, function02, (Function0) rememberedValue6, startRestartGroup, 54);
            startRestartGroup.startReplaceGroup(-1420521906);
            for (final LauncherApp launcherApp : snapshotStateList) {
                startRestartGroup.startMovableGroup(-91277626, launcherApp);
                String title = launcherApp.getTitle();
                startRestartGroup.startReplaceGroup(-91275788);
                boolean changed = startRestartGroup.changed(launcherApp);
                Object rememberedValue7 = startRestartGroup.rememberedValue();
                if (changed || rememberedValue7 == Composer.INSTANCE.getEmpty()) {
                    rememberedValue7 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda5
                        @Override // kotlin.jvm.functions.Function0
                        public final Object invoke() {
                            Unit Shell$lambda$46$lambda$13$lambda$12$lambda$11;
                            Shell$lambda$46$lambda$13$lambda$12$lambda$11 = ShellKt.Shell$lambda$46$lambda$13$lambda$12$lambda$11(SnapshotStateList.this, launcherApp);
                            return Shell$lambda$46$lambda$13$lambda$12$lambda$11;
                        }
                    };
                    startRestartGroup.updateRememberedValue(rememberedValue7);
                }
                startRestartGroup.endReplaceGroup();
                AppWindowKt.AppWindow(title, (Function0) rememberedValue7, ComposableLambdaKt.rememberComposableLambda(-1023881860, true, new Function2<Composer, Integer, Unit>() { // from class: com.neversoft.launcher.ui.ShellKt$Shell$1$4$2

                    /* compiled from: Shell.kt */
                    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
                    public /* synthetic */ class WhenMappings {
                        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

                        static {
                            int[] iArr = new int[LauncherApp.values().length];
                            try {
                                iArr[LauncherApp.Settings.ordinal()] = 1;
                            } catch (NoSuchFieldError unused) {
                            }
                            try {
                                iArr[LauncherApp.About.ordinal()] = 2;
                            } catch (NoSuchFieldError unused2) {
                            }
                            $EnumSwitchMapping$0 = iArr;
                        }
                    }

                    @Override // kotlin.jvm.functions.Function2
                    public /* bridge */ /* synthetic */ Unit invoke(Composer composer3, Integer num) {
                        invoke(composer3, num.intValue());
                        return Unit.INSTANCE;
                    }

                    public final void invoke(Composer composer3, int i2) {
                        if ((i2 & 3) == 2 && composer3.getSkipping()) {
                            composer3.skipToGroupEnd();
                            return;
                        }
                        if (ComposerKt.isTraceInProgress()) {
                            ComposerKt.traceEventStart(-1023881860, i2, -1, "com.neversoft.launcher.ui.Shell.<anonymous>.<anonymous>.<anonymous>.<anonymous> (Shell.kt:57)");
                        }
                        int i3 = WhenMappings.$EnumSwitchMapping$0[LauncherApp.this.ordinal()];
                        if (i3 == 1) {
                            composer3.startReplaceGroup(639812072);
                            SettingsAppKt.SettingsApp(composer3, 0);
                            composer3.endReplaceGroup();
                        } else {
                            if (i3 != 2) {
                                composer3.startReplaceGroup(639810259);
                                composer3.endReplaceGroup();
                                throw new NoWhenBranchMatchedException();
                            }
                            composer3.startReplaceGroup(639813957);
                            SettingsAppKt.AboutApp(composer3, 0);
                            composer3.endReplaceGroup();
                        }
                        if (ComposerKt.isTraceInProgress()) {
                            ComposerKt.traceEventEnd();
                        }
                    }
                }, startRestartGroup, 54), startRestartGroup, 384);
                startRestartGroup.endMovableGroup();
            }
            startRestartGroup.endReplaceGroup();
            switch (WhenMappings.$EnumSwitchMapping$0[Shell$lambda$2(mutableState).ordinal()]) {
                case 1:
                    startRestartGroup.startReplaceGroup(-1420509332);
                    startRestartGroup.startReplaceGroup(-1420507493);
                    boolean changedInstance2 = startRestartGroup.changedInstance(context);
                    Object rememberedValue8 = startRestartGroup.rememberedValue();
                    if (changedInstance2 || rememberedValue8 == Composer.INSTANCE.getEmpty()) {
                        rememberedValue8 = new Function1() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda6
                            @Override // kotlin.jvm.functions.Function1
                            public final Object invoke(Object obj) {
                                Unit Shell$lambda$46$lambda$15$lambda$14;
                                Shell$lambda$46$lambda$15$lambda$14 = ShellKt.Shell$lambda$46$lambda$15$lambda$14(context, mutableState, (String) obj);
                                return Shell$lambda$46$lambda$15$lambda$14;
                            }
                        };
                        startRestartGroup.updateRememberedValue(rememberedValue8);
                    }
                    Function1 function1 = (Function1) rememberedValue8;
                    startRestartGroup.endReplaceGroup();
                    startRestartGroup.startReplaceGroup(-1420504387);
                    boolean changedInstance3 = startRestartGroup.changedInstance(context);
                    Object rememberedValue9 = startRestartGroup.rememberedValue();
                    if (changedInstance3 || rememberedValue9 == Composer.INSTANCE.getEmpty()) {
                        rememberedValue9 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda7
                            @Override // kotlin.jvm.functions.Function0
                            public final Object invoke() {
                                Unit Shell$lambda$46$lambda$17$lambda$16;
                                Shell$lambda$46$lambda$17$lambda$16 = ShellKt.Shell$lambda$46$lambda$17$lambda$16(context, mutableState);
                                return Shell$lambda$46$lambda$17$lambda$16;
                            }
                        };
                        startRestartGroup.updateRememberedValue(rememberedValue9);
                    }
                    Function0 function03 = (Function0) rememberedValue9;
                    startRestartGroup.endReplaceGroup();
                    startRestartGroup.startReplaceGroup(-1420501281);
                    Object rememberedValue10 = startRestartGroup.rememberedValue();
                    if (rememberedValue10 == Composer.INSTANCE.getEmpty()) {
                        rememberedValue10 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda8
                            @Override // kotlin.jvm.functions.Function0
                            public final Object invoke() {
                                Unit Shell$lambda$46$lambda$19$lambda$18;
                                Shell$lambda$46$lambda$19$lambda$18 = ShellKt.Shell$lambda$46$lambda$19$lambda$18(SnapshotStateList.this, mutableState);
                                return Shell$lambda$46$lambda$19$lambda$18;
                            }
                        };
                        startRestartGroup.updateRememberedValue(rememberedValue10);
                    }
                    Function0 function04 = (Function0) rememberedValue10;
                    startRestartGroup.endReplaceGroup();
                    startRestartGroup.startReplaceGroup(-1420499272);
                    Object rememberedValue11 = startRestartGroup.rememberedValue();
                    if (rememberedValue11 == Composer.INSTANCE.getEmpty()) {
                        rememberedValue11 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda9
                            @Override // kotlin.jvm.functions.Function0
                            public final Object invoke() {
                                Unit Shell$lambda$46$lambda$21$lambda$20;
                                Shell$lambda$46$lambda$21$lambda$20 = ShellKt.Shell$lambda$46$lambda$21$lambda$20(MutableState.this);
                                return Shell$lambda$46$lambda$21$lambda$20;
                            }
                        };
                        startRestartGroup.updateRememberedValue(rememberedValue11);
                    }
                    startRestartGroup.endReplaceGroup();
                    StartMenuKt.StartMenu(list, function1, function03, function04, (Function0) rememberedValue11, startRestartGroup, 27648);
                    startRestartGroup.endReplaceGroup();
                    Unit unit = Unit.INSTANCE;
                    break;
                case 2:
                    startRestartGroup.startReplaceGroup(-1420496679);
                    startRestartGroup.startReplaceGroup(-1420495688);
                    Object rememberedValue12 = startRestartGroup.rememberedValue();
                    if (rememberedValue12 == Composer.INSTANCE.getEmpty()) {
                        rememberedValue12 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda10
                            @Override // kotlin.jvm.functions.Function0
                            public final Object invoke() {
                                Unit Shell$lambda$46$lambda$23$lambda$22;
                                Shell$lambda$46$lambda$23$lambda$22 = ShellKt.Shell$lambda$46$lambda$23$lambda$22(MutableState.this);
                                return Shell$lambda$46$lambda$23$lambda$22;
                            }
                        };
                        startRestartGroup.updateRememberedValue(rememberedValue12);
                    }
                    startRestartGroup.endReplaceGroup();
                    FlyoutsKt.QuickSettingsFlyout((Function0) rememberedValue12, startRestartGroup, 6);
                    startRestartGroup.endReplaceGroup();
                    Unit unit2 = Unit.INSTANCE;
                    break;
                case 3:
                    startRestartGroup.startReplaceGroup(-1420493576);
                    startRestartGroup.startReplaceGroup(-1420492616);
                    Object rememberedValue13 = startRestartGroup.rememberedValue();
                    if (rememberedValue13 == Composer.INSTANCE.getEmpty()) {
                        rememberedValue13 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda12
                            @Override // kotlin.jvm.functions.Function0
                            public final Object invoke() {
                                Unit Shell$lambda$46$lambda$25$lambda$24;
                                Shell$lambda$46$lambda$25$lambda$24 = ShellKt.Shell$lambda$46$lambda$25$lambda$24(MutableState.this);
                                return Shell$lambda$46$lambda$25$lambda$24;
                            }
                        };
                        startRestartGroup.updateRememberedValue(rememberedValue13);
                    }
                    startRestartGroup.endReplaceGroup();
                    FlyoutsKt.NotificationFlyout((Function0) rememberedValue13, startRestartGroup, 6);
                    startRestartGroup.endReplaceGroup();
                    Unit unit3 = Unit.INSTANCE;
                    break;
                case 4:
                    startRestartGroup.startReplaceGroup(-1420490505);
                    List list2 = snapshotStateList.toList();
                    startRestartGroup.startReplaceGroup(-1420488115);
                    Object rememberedValue14 = startRestartGroup.rememberedValue();
                    if (rememberedValue14 == Composer.INSTANCE.getEmpty()) {
                        rememberedValue14 = new Function1() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda11
                            @Override // kotlin.jvm.functions.Function1
                            public final Object invoke(Object obj) {
                                Unit Shell$lambda$46$lambda$27$lambda$26;
                                Shell$lambda$46$lambda$27$lambda$26 = ShellKt.Shell$lambda$46$lambda$27$lambda$26(SnapshotStateList.this, mutableState, (LauncherApp) obj);
                                return Shell$lambda$46$lambda$27$lambda$26;
                            }
                        };
                        startRestartGroup.updateRememberedValue(rememberedValue14);
                    }
                    Function1 function12 = (Function1) rememberedValue14;
                    startRestartGroup.endReplaceGroup();
                    startRestartGroup.startReplaceGroup(-1420486732);
                    Object rememberedValue15 = startRestartGroup.rememberedValue();
                    if (rememberedValue15 == Composer.INSTANCE.getEmpty()) {
                        rememberedValue15 = new Function1() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda13
                            @Override // kotlin.jvm.functions.Function1
                            public final Object invoke(Object obj) {
                                Unit Shell$lambda$46$lambda$29$lambda$28;
                                Shell$lambda$46$lambda$29$lambda$28 = ShellKt.Shell$lambda$46$lambda$29$lambda$28(SnapshotStateList.this, (LauncherApp) obj);
                                return Shell$lambda$46$lambda$29$lambda$28;
                            }
                        };
                        startRestartGroup.updateRememberedValue(rememberedValue15);
                    }
                    Function1 function13 = (Function1) rememberedValue15;
                    startRestartGroup.endReplaceGroup();
                    startRestartGroup.startReplaceGroup(-1420485064);
                    Object rememberedValue16 = startRestartGroup.rememberedValue();
                    if (rememberedValue16 == Composer.INSTANCE.getEmpty()) {
                        rememberedValue16 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda14
                            @Override // kotlin.jvm.functions.Function0
                            public final Object invoke() {
                                Unit Shell$lambda$46$lambda$31$lambda$30;
                                Shell$lambda$46$lambda$31$lambda$30 = ShellKt.Shell$lambda$46$lambda$31$lambda$30(MutableState.this);
                                return Shell$lambda$46$lambda$31$lambda$30;
                            }
                        };
                        startRestartGroup.updateRememberedValue(rememberedValue16);
                    }
                    startRestartGroup.endReplaceGroup();
                    TaskViewKt.TaskView(list2, function12, function13, (Function0) rememberedValue16, startRestartGroup, 3504);
                    startRestartGroup.endReplaceGroup();
                    Unit unit4 = Unit.INSTANCE;
                    break;
                case 5:
                    startRestartGroup.startReplaceGroup(-1420482670);
                    startRestartGroup.startReplaceGroup(-1420481896);
                    Object rememberedValue17 = startRestartGroup.rememberedValue();
                    if (rememberedValue17 == Composer.INSTANCE.getEmpty()) {
                        rememberedValue17 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda15
                            @Override // kotlin.jvm.functions.Function0
                            public final Object invoke() {
                                Unit Shell$lambda$46$lambda$33$lambda$32;
                                Shell$lambda$46$lambda$33$lambda$32 = ShellKt.Shell$lambda$46$lambda$33$lambda$32(MutableState.this);
                                return Shell$lambda$46$lambda$33$lambda$32;
                            }
                        };
                        startRestartGroup.updateRememberedValue(rememberedValue17);
                    }
                    startRestartGroup.endReplaceGroup();
                    WidgetsKt.WidgetsBoard((Function0) rememberedValue17, startRestartGroup, 6);
                    startRestartGroup.endReplaceGroup();
                    Unit unit5 = Unit.INSTANCE;
                    break;
                case 6:
                    startRestartGroup.startReplaceGroup(-1420480126);
                    startRestartGroup.endReplaceGroup();
                    Unit unit6 = Unit.INSTANCE;
                    break;
                default:
                    startRestartGroup.startReplaceGroup(-1420510186);
                    startRestartGroup.endReplaceGroup();
                    throw new NoWhenBranchMatchedException();
            }
            boolean z = Shell$lambda$2(mutableState) == Overlay.Start;
            boolean z2 = Shell$lambda$2(mutableState) == Overlay.QuickSettings;
            boolean z3 = Shell$lambda$2(mutableState) == Overlay.Notifications;
            boolean z4 = Shell$lambda$2(mutableState) == Overlay.TaskView;
            boolean z5 = Shell$lambda$2(mutableState) == Overlay.Widgets;
            startRestartGroup.startReplaceGroup(-1420468201);
            Object rememberedValue18 = startRestartGroup.rememberedValue();
            if (rememberedValue18 == Composer.INSTANCE.getEmpty()) {
                rememberedValue18 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda16
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        Unit Shell$lambda$46$lambda$35$lambda$34;
                        Shell$lambda$46$lambda$35$lambda$34 = ShellKt.Shell$lambda$46$lambda$35$lambda$34(MutableState.this);
                        return Shell$lambda$46$lambda$35$lambda$34;
                    }
                };
                startRestartGroup.updateRememberedValue(rememberedValue18);
            }
            Function0 function05 = (Function0) rememberedValue18;
            startRestartGroup.endReplaceGroup();
            startRestartGroup.startReplaceGroup(-1420466433);
            Object rememberedValue19 = startRestartGroup.rememberedValue();
            if (rememberedValue19 == Composer.INSTANCE.getEmpty()) {
                rememberedValue19 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda17
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        Unit Shell$lambda$46$lambda$37$lambda$36;
                        Shell$lambda$46$lambda$37$lambda$36 = ShellKt.Shell$lambda$46$lambda$37$lambda$36(MutableState.this);
                        return Shell$lambda$46$lambda$37$lambda$36;
                    }
                };
                startRestartGroup.updateRememberedValue(rememberedValue19);
            }
            Function0 function06 = (Function0) rememberedValue19;
            startRestartGroup.endReplaceGroup();
            startRestartGroup.startReplaceGroup(-1420464417);
            Object rememberedValue20 = startRestartGroup.rememberedValue();
            if (rememberedValue20 == Composer.INSTANCE.getEmpty()) {
                rememberedValue20 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda18
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        Unit Shell$lambda$46$lambda$39$lambda$38;
                        Shell$lambda$46$lambda$39$lambda$38 = ShellKt.Shell$lambda$46$lambda$39$lambda$38(MutableState.this);
                        return Shell$lambda$46$lambda$39$lambda$38;
                    }
                };
                startRestartGroup.updateRememberedValue(rememberedValue20);
            }
            Function0 function07 = (Function0) rememberedValue20;
            startRestartGroup.endReplaceGroup();
            startRestartGroup.startReplaceGroup(-1420462310);
            Object rememberedValue21 = startRestartGroup.rememberedValue();
            if (rememberedValue21 == Composer.INSTANCE.getEmpty()) {
                rememberedValue21 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda19
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        Unit Shell$lambda$46$lambda$41$lambda$40;
                        Shell$lambda$46$lambda$41$lambda$40 = ShellKt.Shell$lambda$46$lambda$41$lambda$40(MutableState.this);
                        return Shell$lambda$46$lambda$41$lambda$40;
                    }
                };
                startRestartGroup.updateRememberedValue(rememberedValue21);
            }
            Function0 function08 = (Function0) rememberedValue21;
            startRestartGroup.endReplaceGroup();
            startRestartGroup.startReplaceGroup(-1420460391);
            Object rememberedValue22 = startRestartGroup.rememberedValue();
            if (rememberedValue22 == Composer.INSTANCE.getEmpty()) {
                rememberedValue22 = new Function0() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda20
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        Unit Shell$lambda$46$lambda$43$lambda$42;
                        Shell$lambda$46$lambda$43$lambda$42 = ShellKt.Shell$lambda$46$lambda$43$lambda$42(MutableState.this);
                        return Shell$lambda$46$lambda$43$lambda$42;
                    }
                };
                startRestartGroup.updateRememberedValue(rememberedValue22);
            }
            Function0 function09 = (Function0) rememberedValue22;
            startRestartGroup.endReplaceGroup();
            startRestartGroup.startReplaceGroup(-1420458717);
            boolean changedInstance4 = startRestartGroup.changedInstance(context);
            Object rememberedValue23 = startRestartGroup.rememberedValue();
            if (changedInstance4 || rememberedValue23 == Composer.INSTANCE.getEmpty()) {
                rememberedValue23 = new Function1() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda1
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        Unit Shell$lambda$46$lambda$45$lambda$44;
                        Shell$lambda$46$lambda$45$lambda$44 = ShellKt.Shell$lambda$46$lambda$45$lambda$44(context, (String) obj);
                        return Shell$lambda$46$lambda$45$lambda$44;
                    }
                };
                startRestartGroup.updateRememberedValue(rememberedValue23);
            }
            startRestartGroup.endReplaceGroup();
            composer2 = startRestartGroup;
            TaskbarKt.Taskbar(list, z, z2, z3, z4, z5, function05, function06, function07, function08, function09, (Function1) rememberedValue23, boxScopeInstance.align(Modifier.INSTANCE, Alignment.INSTANCE.getBottomCenter()), startRestartGroup, 920125440, 6, 0);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            composer2.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        ScopeUpdateScope endRestartGroup = composer2.endRestartGroup();
        if (endRestartGroup != null) {
            endRestartGroup.updateScope(new Function2() { // from class: com.neversoft.launcher.ui.ShellKt$$ExternalSyntheticLambda3
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    Unit Shell$lambda$47;
                    Shell$lambda$47 = ShellKt.Shell$lambda$47(i, (Composer) obj, ((Integer) obj2).intValue());
                    return Shell$lambda$47;
                }
            });
        }
    }

    private static final Overlay Shell$lambda$2(MutableState<Overlay> mutableState) {
        return mutableState.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$10$lambda$9(Context context) {
        AppRepository.INSTANCE.launchTermux(context);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$13$lambda$12$lambda$11(SnapshotStateList snapshotStateList, LauncherApp launcherApp) {
        snapshotStateList.remove(launcherApp);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$15$lambda$14(Context context, MutableState mutableState, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        mutableState.setValue(Overlay.None);
        AppRepository.INSTANCE.launch(context, it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$17$lambda$16(Context context, MutableState mutableState) {
        mutableState.setValue(Overlay.None);
        AppRepository.INSTANCE.launchTermux(context);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$19$lambda$18(SnapshotStateList snapshotStateList, MutableState mutableState) {
        Shell$openApp(snapshotStateList, mutableState, LauncherApp.Settings);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$21$lambda$20(MutableState mutableState) {
        mutableState.setValue(Overlay.None);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$23$lambda$22(MutableState mutableState) {
        mutableState.setValue(Overlay.None);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$25$lambda$24(MutableState mutableState) {
        mutableState.setValue(Overlay.None);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$27$lambda$26(SnapshotStateList snapshotStateList, MutableState mutableState, LauncherApp it) {
        Intrinsics.checkNotNullParameter(it, "it");
        Shell$openApp(snapshotStateList, mutableState, it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$29$lambda$28(SnapshotStateList snapshotStateList, LauncherApp it) {
        Intrinsics.checkNotNullParameter(it, "it");
        snapshotStateList.remove(it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$31$lambda$30(MutableState mutableState) {
        mutableState.setValue(Overlay.None);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$33$lambda$32(MutableState mutableState) {
        mutableState.setValue(Overlay.None);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$35$lambda$34(MutableState mutableState) {
        Shell$toggle(mutableState, Overlay.Start);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$37$lambda$36(MutableState mutableState) {
        Shell$toggle(mutableState, Overlay.QuickSettings);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$39$lambda$38(MutableState mutableState) {
        Shell$toggle(mutableState, Overlay.Notifications);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$41$lambda$40(MutableState mutableState) {
        Shell$toggle(mutableState, Overlay.TaskView);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$43$lambda$42(MutableState mutableState) {
        Shell$toggle(mutableState, Overlay.Widgets);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$45$lambda$44(Context context, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        AppRepository.INSTANCE.launch(context, it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$6$lambda$5(SnapshotStateList snapshotStateList, MutableState mutableState) {
        Shell$openApp(snapshotStateList, mutableState, LauncherApp.Settings);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$46$lambda$8$lambda$7(SnapshotStateList snapshotStateList, MutableState mutableState) {
        Shell$openApp(snapshotStateList, mutableState, LauncherApp.About);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Shell$lambda$47(int i, Composer composer, int i2) {
        Shell(composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    private static final void Shell$openApp(SnapshotStateList<LauncherApp> snapshotStateList, MutableState<Overlay> mutableState, LauncherApp launcherApp) {
        snapshotStateList.remove(launcherApp);
        snapshotStateList.add(launcherApp);
        mutableState.setValue(Overlay.None);
    }

    private static final void Shell$toggle(MutableState<Overlay> mutableState, Overlay overlay) {
        if (Shell$lambda$2(mutableState) == overlay) {
            overlay = Overlay.None;
        }
        mutableState.setValue(overlay);
    }
}
