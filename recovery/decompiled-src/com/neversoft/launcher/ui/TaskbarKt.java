package com.neversoft.launcher.ui;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.BatteryFullKt;
import androidx.compose.material.icons.filled.VolumeUpKt;
import androidx.compose.material.icons.filled.WifiKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt__SnapshotStateKt;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.text.TextLayoutResult;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontFamily;
import androidx.compose.ui.text.font.FontStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.text.style.TextDecoration;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import androidx.profileinstaller.ProfileVerifier;
import com.neversoft.launcher.apps.AppEntry;
import com.neversoft.launcher.ui.components.AppGlyphKt;
import com.neversoft.launcher.ui.theme.NsColor;
import com.neversoft.launcher.ui.theme.NsDim;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;

/* compiled from: Taskbar.kt */
@Metadata(d1 = {"\u0000B\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\u001a§\u0001\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u00062\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00010\u00122\b\b\u0002\u0010\u0014\u001a\u00020\u0015H\u0007¢\u0006\u0002\u0010\u0016\u001a8\u0010\u0017\u001a\u00020\u00012\b\b\u0002\u0010\u0018\u001a\u00020\u00062\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\u0011\u0010\u001a\u001a\r\u0012\u0004\u0012\u00020\u00010\f¢\u0006\u0002\b\u001bH\u0003¢\u0006\u0002\u0010\u001c\u001a#\u0010\u001d\u001a\u00020\u00012\u0006\u0010\u0018\u001a\u00020\u00062\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00010\fH\u0003¢\u0006\u0002\u0010\u001e\u001a#\u0010\u001f\u001a\u00020\u00012\u0006\u0010\u0018\u001a\u00020\u00062\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00010\fH\u0003¢\u0006\u0002\u0010\u001e¨\u0006 ²\u0006\u0012\u0010!\u001a\n #*\u0004\u0018\u00010\"0\"X\u008a\u008e\u0002"}, d2 = {"Taskbar", "", "apps", "", "Lcom/neversoft/launcher/apps/AppEntry;", "startActive", "", "quickActive", "notifActive", "taskViewActive", "widgetsActive", "onToggleStart", "Lkotlin/Function0;", "onToggleQuick", "onToggleNotif", "onToggleTaskView", "onToggleWidgets", "onLaunch", "Lkotlin/Function1;", "", "modifier", "Landroidx/compose/ui/Modifier;", "(Ljava/util/List;ZZZZZLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;III)V", "TaskbarButton", "active", "onClick", "content", "Landroidx/compose/runtime/Composable;", "(ZLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V", "TrayCluster", "(ZLkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V", "Clock", "app_release", "now", "Ljava/time/LocalDateTime;", "kotlin.jvm.PlatformType"}, k = 2, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class TaskbarKt {
    private static final void Clock(final boolean z, final Function0<Unit> function0, Composer composer, final int i) {
        int i2;
        Composer composer2;
        Composer startRestartGroup = composer.startRestartGroup(-657113447);
        if ((i & 6) == 0) {
            i2 = (startRestartGroup.changed(z) ? 4 : 2) | i;
        } else {
            i2 = i;
        }
        if ((i & 48) == 0) {
            i2 |= startRestartGroup.changedInstance(function0) ? 32 : 16;
        }
        if ((i2 & 19) == 18 && startRestartGroup.getSkipping()) {
            startRestartGroup.skipToGroupEnd();
            composer2 = startRestartGroup;
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-657113447, i2, -1, "com.neversoft.launcher.ui.Clock (Taskbar.kt:188)");
            }
            startRestartGroup.startReplaceGroup(1544333677);
            Object rememberedValue = startRestartGroup.rememberedValue();
            if (rememberedValue == Composer.INSTANCE.getEmpty()) {
                rememberedValue = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(LocalDateTime.now(), null, 2, null);
                startRestartGroup.updateRememberedValue(rememberedValue);
            }
            MutableState mutableState = (MutableState) rememberedValue;
            startRestartGroup.endReplaceGroup();
            Unit unit = Unit.INSTANCE;
            startRestartGroup.startReplaceGroup(1544336101);
            TaskbarKt$Clock$1$1 rememberedValue2 = startRestartGroup.rememberedValue();
            if (rememberedValue2 == Composer.INSTANCE.getEmpty()) {
                rememberedValue2 = new TaskbarKt$Clock$1$1(mutableState, null);
                startRestartGroup.updateRememberedValue(rememberedValue2);
            }
            startRestartGroup.endReplaceGroup();
            EffectsKt.LaunchedEffect(unit, (Function2<? super CoroutineScope, ? super Continuation<? super Unit>, ? extends Object>) rememberedValue2, startRestartGroup, 6);
            int minute = Clock$lambda$13(mutableState).getMinute();
            startRestartGroup.startReplaceGroup(1544339911);
            boolean changed = startRestartGroup.changed(minute);
            Object rememberedValue3 = startRestartGroup.rememberedValue();
            if (changed || rememberedValue3 == Composer.INSTANCE.getEmpty()) {
                rememberedValue3 = Clock$lambda$13(mutableState).format(DateTimeFormatter.ofPattern("h:mm a"));
                startRestartGroup.updateRememberedValue(rememberedValue3);
            }
            String str = (String) rememberedValue3;
            startRestartGroup.endReplaceGroup();
            int dayOfYear = Clock$lambda$13(mutableState).getDayOfYear();
            startRestartGroup.startReplaceGroup(1544342796);
            boolean changed2 = startRestartGroup.changed(dayOfYear);
            Object rememberedValue4 = startRestartGroup.rememberedValue();
            if (changed2 || rememberedValue4 == Composer.INSTANCE.getEmpty()) {
                rememberedValue4 = Clock$lambda$13(mutableState).format(DateTimeFormatter.ofPattern("M/d/yyyy"));
                startRestartGroup.updateRememberedValue(rememberedValue4);
            }
            String str2 = (String) rememberedValue4;
            startRestartGroup.endReplaceGroup();
            Modifier m682paddingVpY3zN4 = PaddingKt.m682paddingVpY3zN4(ClickableKt.m269clickableXHw0xAI$default(BackgroundKt.m236backgroundbw27NRU$default(ClipKt.clip(Modifier.INSTANCE, RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(NsDim.INSTANCE.m6669getRadiusControlD9Ej5fM())), z ? NsColor.INSTANCE.m6653getControlHover0d7_KjU() : Color.INSTANCE.m3875getTransparent0d7_KjU(), null, 2, null), false, null, null, function0, 7, null), Dp.m6300constructorimpl(10), Dp.m6300constructorimpl(4));
            Alignment.Horizontal end = Alignment.INSTANCE.getEnd();
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -483455358, "CC(Column)P(2,3,1)86@4330L61,87@4396L133:Column.kt#2w3rfo");
            MeasurePolicy columnMeasurePolicy = ColumnKt.columnMeasurePolicy(Arrangement.INSTANCE.getTop(), end, startRestartGroup, 48);
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            int currentCompositeKeyHash = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
            CompositionLocalMap currentCompositionLocalMap = startRestartGroup.getCurrentCompositionLocalMap();
            Modifier materializeModifier = ComposedModifierKt.materializeModifier(startRestartGroup, m682paddingVpY3zN4);
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
            Updater.m3340setimpl(m3333constructorimpl, columnMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m3340setimpl(m3333constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
            if (m3333constructorimpl.getInserting() || !Intrinsics.areEqual(m3333constructorimpl.rememberedValue(), Integer.valueOf(currentCompositeKeyHash))) {
                m3333constructorimpl.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash));
                m3333constructorimpl.apply(Integer.valueOf(currentCompositeKeyHash), setCompositeKeyHash);
            }
            Updater.m3340setimpl(m3333constructorimpl, materializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -384784025, "C88@4444L9:Column.kt#2w3rfo");
            ColumnScopeInstance columnScopeInstance = ColumnScopeInstance.INSTANCE;
            Intrinsics.checkNotNull(str);
            composer2 = startRestartGroup;
            TextKt.m2373Text4IGK_g(str, (Modifier) null, NsColor.INSTANCE.m6664getText0d7_KjU(), TextUnitKt.getSp(12), (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, TextUnitKt.getSp(14), 0, false, 0, 0, (Function1<? super TextLayoutResult, Unit>) null, (TextStyle) null, composer2, 3456, 6, 130034);
            Intrinsics.checkNotNull(str2);
            TextKt.m2373Text4IGK_g(str2, (Modifier) null, NsColor.INSTANCE.m6664getText0d7_KjU(), TextUnitKt.getSp(12), (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, TextUnitKt.getSp(14), 0, false, 0, 0, (Function1<? super TextLayoutResult, Unit>) null, (TextStyle) null, composer2, 3456, 6, 130034);
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
            endRestartGroup.updateScope(new Function2() { // from class: com.neversoft.launcher.ui.TaskbarKt$$ExternalSyntheticLambda4
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    Unit Clock$lambda$19;
                    Clock$lambda$19 = TaskbarKt.Clock$lambda$19(z, function0, i, (Composer) obj, ((Integer) obj2).intValue());
                    return Clock$lambda$19;
                }
            });
        }
    }

    private static final LocalDateTime Clock$lambda$13(MutableState<LocalDateTime> mutableState) {
        return mutableState.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Clock$lambda$19(boolean z, Function0 function0, int i, Composer composer, int i2) {
        Clock(z, function0, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:102:0x0565  */
    /* JADX WARN: Removed duplicated region for block: B:105:0x0571  */
    /* JADX WARN: Removed duplicated region for block: B:108:0x059a  */
    /* JADX WARN: Removed duplicated region for block: B:112:0x0609  */
    /* JADX WARN: Removed duplicated region for block: B:114:0x0575  */
    /* JADX WARN: Removed duplicated region for block: B:116:0x03b3  */
    /* JADX WARN: Removed duplicated region for block: B:118:0x023c  */
    /* JADX WARN: Removed duplicated region for block: B:119:0x01bb  */
    /* JADX WARN: Removed duplicated region for block: B:120:0x0181  */
    /* JADX WARN: Removed duplicated region for block: B:126:0x016c  */
    /* JADX WARN: Removed duplicated region for block: B:132:0x0155  */
    /* JADX WARN: Removed duplicated region for block: B:140:0x013d  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x0124  */
    /* JADX WARN: Removed duplicated region for block: B:152:0x010b  */
    /* JADX WARN: Removed duplicated region for block: B:158:0x00f1  */
    /* JADX WARN: Removed duplicated region for block: B:164:0x00d3  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x00cc  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x00ec  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0107  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0120  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0139  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0152  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0169  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x017e  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x019f  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0612  */
    /* JADX WARN: Removed duplicated region for block: B:58:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x01b6  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x01c3  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x022c  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0238  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0261  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x03a3  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x03af  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x03d8  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x048f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void Taskbar(final List<AppEntry> apps, final boolean z, final boolean z2, final boolean z3, final boolean z4, final boolean z5, final Function0<Unit> onToggleStart, final Function0<Unit> onToggleQuick, final Function0<Unit> onToggleNotif, final Function0<Unit> onToggleTaskView, final Function0<Unit> onToggleWidgets, final Function1<? super String, Unit> onLaunch, Modifier modifier, Composer composer, final int i, final int i2, final int i3) {
        int i4;
        int i5;
        int i6;
        Modifier modifier2;
        int currentCompositeKeyHash;
        Composer m3333constructorimpl;
        int currentCompositeKeyHash2;
        Composer m3333constructorimpl2;
        Composer composer2;
        int currentCompositeKeyHash3;
        Composer m3333constructorimpl3;
        ScopeUpdateScope endRestartGroup;
        int i7;
        int i8;
        int i9;
        int i10;
        Intrinsics.checkNotNullParameter(apps, "apps");
        Intrinsics.checkNotNullParameter(onToggleStart, "onToggleStart");
        Intrinsics.checkNotNullParameter(onToggleQuick, "onToggleQuick");
        Intrinsics.checkNotNullParameter(onToggleNotif, "onToggleNotif");
        Intrinsics.checkNotNullParameter(onToggleTaskView, "onToggleTaskView");
        Intrinsics.checkNotNullParameter(onToggleWidgets, "onToggleWidgets");
        Intrinsics.checkNotNullParameter(onLaunch, "onLaunch");
        Composer startRestartGroup = composer.startRestartGroup(-348256995);
        if ((i3 & 1) != 0) {
            i4 = i | 6;
        } else if ((i & 6) == 0) {
            i4 = (startRestartGroup.changedInstance(apps) ? 4 : 2) | i;
        } else {
            i4 = i;
        }
        if ((i3 & 2) != 0) {
            i4 |= 48;
        } else if ((i & 48) == 0) {
            i4 |= startRestartGroup.changed(z) ? 32 : 16;
        }
        if ((i3 & 4) != 0) {
            i4 |= 384;
        } else if ((i & 384) == 0) {
            i4 |= startRestartGroup.changed(z2) ? 256 : 128;
        }
        if ((i3 & 8) != 0) {
            i4 |= 3072;
        } else if ((i & 3072) == 0) {
            i4 |= startRestartGroup.changed(z3) ? 2048 : 1024;
        }
        if ((i3 & 16) != 0) {
            i4 |= 24576;
        } else if ((i & 24576) == 0) {
            i4 |= startRestartGroup.changed(z4) ? 16384 : 8192;
            if ((i3 & 32) == 0) {
                i4 |= ProfileVerifier.CompilationStatus.RESULT_CODE_ERROR_CANT_WRITE_PROFILE_VERIFICATION_RESULT_CACHE_FILE;
            } else if ((i & ProfileVerifier.CompilationStatus.RESULT_CODE_ERROR_CANT_WRITE_PROFILE_VERIFICATION_RESULT_CACHE_FILE) == 0) {
                i4 |= startRestartGroup.changed(z5) ? 131072 : 65536;
            }
            if ((i3 & 64) != 0) {
                i10 = (i & 1572864) == 0 ? startRestartGroup.changedInstance(onToggleStart) ? 1048576 : 524288 : 1572864;
                if ((i3 & 128) == 0) {
                    i9 = (12582912 & i) == 0 ? startRestartGroup.changedInstance(onToggleQuick) ? 8388608 : 4194304 : 12582912;
                    if ((i3 & 256) != 0) {
                        i8 = (100663296 & i) == 0 ? startRestartGroup.changedInstance(onToggleNotif) ? AccessibilityEventCompat.TYPE_VIEW_TARGETED_BY_SCROLL : 33554432 : 100663296;
                        if ((i3 & 512) == 0) {
                            i7 = (805306368 & i) == 0 ? startRestartGroup.changedInstance(onToggleTaskView) ? 536870912 : 268435456 : 805306368;
                            if ((i3 & 1024) == 0) {
                                i5 = i2 | 6;
                            } else if ((i2 & 6) == 0) {
                                i5 = (startRestartGroup.changedInstance(onToggleWidgets) ? 4 : 2) | i2;
                            } else {
                                i5 = i2;
                            }
                            if ((i3 & 2048) == 0) {
                                i5 |= 48;
                            } else if ((i2 & 48) == 0) {
                                i5 |= startRestartGroup.changedInstance(onLaunch) ? 32 : 16;
                            }
                            i6 = i3 & 4096;
                            if (i6 == 0) {
                                i5 |= 384;
                            } else if ((i2 & 384) == 0) {
                                i5 |= startRestartGroup.changed(modifier) ? 256 : 128;
                                if ((i4 & 306783379) != 306783378 && (i5 & 147) == 146 && startRestartGroup.getSkipping()) {
                                    startRestartGroup.skipToGroupEnd();
                                    modifier2 = modifier;
                                    composer2 = startRestartGroup;
                                } else {
                                    Modifier.Companion companion = i6 != 0 ? Modifier.INSTANCE : modifier;
                                    if (ComposerKt.isTraceInProgress()) {
                                        ComposerKt.traceEventStart(-348256995, i4, i5, "com.neversoft.launcher.ui.Taskbar (Taskbar.kt:59)");
                                    }
                                    Modifier m236backgroundbw27NRU$default = BackgroundKt.m236backgroundbw27NRU$default(SizeKt.m712height3ABfNKs(SizeKt.fillMaxWidth$default(companion, 0.0f, 1, null), LauncherState.INSTANCE.m6622getTaskbarHeightD9Ej5fM()), NsColor.INSTANCE.m6651getAcrylicTaskbar0d7_KjU(), null, 2, null);
                                    ComposerKt.sourceInformationMarkerStart(startRestartGroup, 733328855, "CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
                                    MeasurePolicy maybeCachedBoxMeasurePolicy = BoxKt.maybeCachedBoxMeasurePolicy(Alignment.INSTANCE.getTopStart(), false);
                                    modifier2 = companion;
                                    ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                                    currentCompositeKeyHash = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
                                    CompositionLocalMap currentCompositionLocalMap = startRestartGroup.getCurrentCompositionLocalMap();
                                    Modifier materializeModifier = ComposedModifierKt.materializeModifier(startRestartGroup, m236backgroundbw27NRU$default);
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
                                    m3333constructorimpl = Updater.m3333constructorimpl(startRestartGroup);
                                    Updater.m3340setimpl(m3333constructorimpl, maybeCachedBoxMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                                    Updater.m3340setimpl(m3333constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                                    Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                                    if (!m3333constructorimpl.getInserting() || !Intrinsics.areEqual(m3333constructorimpl.rememberedValue(), Integer.valueOf(currentCompositeKeyHash))) {
                                        m3333constructorimpl.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash));
                                        m3333constructorimpl.apply(Integer.valueOf(currentCompositeKeyHash), setCompositeKeyHash);
                                    }
                                    Updater.m3340setimpl(m3333constructorimpl, materializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
                                    ComposerKt.sourceInformationMarkerStart(startRestartGroup, -2146769399, "C73@3429L9:Box.kt#2w3rfo");
                                    BoxScopeInstance boxScopeInstance = BoxScopeInstance.INSTANCE;
                                    BoxKt.Box(BackgroundKt.m236backgroundbw27NRU$default(SizeKt.m712height3ABfNKs(SizeKt.fillMaxWidth$default(boxScopeInstance.align(Modifier.INSTANCE, Alignment.INSTANCE.getTopCenter()), 0.0f, 1, null), Dp.m6300constructorimpl(1)), NsColor.INSTANCE.m6660getStroke0d7_KjU(), null, 2, null), startRestartGroup, 0);
                                    float f = 6;
                                    int i11 = i5;
                                    TextKt.m2373Text4IGK_g("72°  Sunny", PaddingKt.m682paddingVpY3zN4(ClickableKt.m269clickableXHw0xAI$default(ClipKt.clip(PaddingKt.m685paddingqDBjuR0$default(boxScopeInstance.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenterStart()), Dp.m6300constructorimpl(f), 0.0f, 0.0f, 0.0f, 14, null), RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(NsDim.INSTANCE.m6669getRadiusControlD9Ej5fM())), false, null, null, onToggleWidgets, 7, null), Dp.m6300constructorimpl(10), Dp.m6300constructorimpl(f)), NsColor.INSTANCE.m6665getTextSecondary0d7_KjU(), TextUnitKt.getSp(12), (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1<? super TextLayoutResult, Unit>) null, (TextStyle) null, startRestartGroup, 3462, 0, 131056);
                                    Modifier align = boxScopeInstance.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenter());
                                    Alignment.Vertical centerVertically = Alignment.INSTANCE.getCenterVertically();
                                    float f2 = 2;
                                    Arrangement.HorizontalOrVertical m561spacedBy0680j_4 = Arrangement.INSTANCE.m561spacedBy0680j_4(Dp.m6300constructorimpl(f2));
                                    ComposerKt.sourceInformationMarkerStart(startRestartGroup, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
                                    MeasurePolicy rowMeasurePolicy = RowKt.rowMeasurePolicy(m561spacedBy0680j_4, centerVertically, startRestartGroup, 54);
                                    ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                                    currentCompositeKeyHash2 = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
                                    CompositionLocalMap currentCompositionLocalMap2 = startRestartGroup.getCurrentCompositionLocalMap();
                                    Modifier materializeModifier2 = ComposedModifierKt.materializeModifier(startRestartGroup, align);
                                    Function0<ComposeUiNode> constructor2 = ComposeUiNode.INSTANCE.getConstructor();
                                    ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                                    if (!(startRestartGroup.getApplier() instanceof Applier)) {
                                        ComposablesKt.invalidApplier();
                                    }
                                    startRestartGroup.startReusableNode();
                                    if (startRestartGroup.getInserting()) {
                                        startRestartGroup.createNode(constructor2);
                                    } else {
                                        startRestartGroup.useNode();
                                    }
                                    m3333constructorimpl2 = Updater.m3333constructorimpl(startRestartGroup);
                                    Updater.m3340setimpl(m3333constructorimpl2, rowMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                                    Updater.m3340setimpl(m3333constructorimpl2, currentCompositionLocalMap2, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                                    Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash2 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                                    if (!m3333constructorimpl2.getInserting() || !Intrinsics.areEqual(m3333constructorimpl2.rememberedValue(), Integer.valueOf(currentCompositeKeyHash2))) {
                                        m3333constructorimpl2.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash2));
                                        m3333constructorimpl2.apply(Integer.valueOf(currentCompositeKeyHash2), setCompositeKeyHash2);
                                    }
                                    Updater.m3340setimpl(m3333constructorimpl2, materializeModifier2, ComposeUiNode.INSTANCE.getSetModifier());
                                    ComposerKt.sourceInformationMarkerStart(startRestartGroup, -407840262, "C101@5126L9:Row.kt#2w3rfo");
                                    RowScopeInstance rowScopeInstance = RowScopeInstance.INSTANCE;
                                    int i12 = i4 >> 15;
                                    int i13 = i12 & 112;
                                    float f3 = f2;
                                    int i14 = i4;
                                    TaskbarButton(z, onToggleStart, ComposableSingletons$TaskbarKt.INSTANCE.m6607getLambda1$app_release(), startRestartGroup, ((i4 >> 3) & 14) | 384 | i13, 0);
                                    TaskbarButton(false, onToggleStart, ComposableSingletons$TaskbarKt.INSTANCE.m6608getLambda2$app_release(), startRestartGroup, i13 | 384, 1);
                                    TaskbarButton(z4, onToggleTaskView, ComposableSingletons$TaskbarKt.INSTANCE.m6609getLambda3$app_release(), startRestartGroup, ((i14 >> 12) & 14) | 384 | ((i14 >> 24) & 112), 0);
                                    TaskbarButton(z5, onToggleWidgets, ComposableSingletons$TaskbarKt.INSTANCE.m6610getLambda4$app_release(), startRestartGroup, (i12 & 14) | 384 | ((i11 << 3) & 112), 0);
                                    composer2 = startRestartGroup;
                                    composer2.startReplaceGroup(-1916004207);
                                    for (final AppEntry appEntry : CollectionsKt.take(apps, 6)) {
                                        composer2.startReplaceGroup(-668290921);
                                        boolean changedInstance = composer2.changedInstance(appEntry) | ((i11 & 112) == 32);
                                        Object rememberedValue = composer2.rememberedValue();
                                        if (changedInstance || rememberedValue == Composer.INSTANCE.getEmpty()) {
                                            rememberedValue = new Function0() { // from class: com.neversoft.launcher.ui.TaskbarKt$$ExternalSyntheticLambda2
                                                @Override // kotlin.jvm.functions.Function0
                                                public final Object invoke() {
                                                    Unit Taskbar$lambda$5$lambda$3$lambda$2$lambda$1$lambda$0;
                                                    Taskbar$lambda$5$lambda$3$lambda$2$lambda$1$lambda$0 = TaskbarKt.Taskbar$lambda$5$lambda$3$lambda$2$lambda$1$lambda$0(Function1.this, appEntry);
                                                    return Taskbar$lambda$5$lambda$3$lambda$2$lambda$1$lambda$0;
                                                }
                                            };
                                            composer2.updateRememberedValue(rememberedValue);
                                        }
                                        composer2.endReplaceGroup();
                                        TaskbarButton(false, (Function0) rememberedValue, ComposableLambdaKt.rememberComposableLambda(-2058018378, true, new Function2<Composer, Integer, Unit>() { // from class: com.neversoft.launcher.ui.TaskbarKt$Taskbar$1$1$1$2
                                            @Override // kotlin.jvm.functions.Function2
                                            public /* bridge */ /* synthetic */ Unit invoke(Composer composer3, Integer num) {
                                                invoke(composer3, num.intValue());
                                                return Unit.INSTANCE;
                                            }

                                            public final void invoke(Composer composer3, int i15) {
                                                if ((i15 & 3) == 2 && composer3.getSkipping()) {
                                                    composer3.skipToGroupEnd();
                                                    return;
                                                }
                                                if (ComposerKt.isTraceInProgress()) {
                                                    ComposerKt.traceEventStart(-2058018378, i15, -1, "com.neversoft.launcher.ui.Taskbar.<anonymous>.<anonymous>.<anonymous>.<anonymous> (Taskbar.kt:120)");
                                                }
                                                AppGlyphKt.m6641AppGlyphziNgDLE(AppEntry.this, NsDim.INSTANCE.m6672getTaskbarGlyphD9Ej5fM(), composer3, 48);
                                                if (ComposerKt.isTraceInProgress()) {
                                                    ComposerKt.traceEventEnd();
                                                }
                                            }
                                        }, composer2, 54), composer2, 384, 1);
                                        f3 = f3;
                                    }
                                    composer2.endReplaceGroup();
                                    ComposerKt.sourceInformationMarkerEnd(composer2);
                                    composer2.endNode();
                                    ComposerKt.sourceInformationMarkerEnd(composer2);
                                    ComposerKt.sourceInformationMarkerEnd(composer2);
                                    ComposerKt.sourceInformationMarkerEnd(composer2);
                                    Modifier m685paddingqDBjuR0$default = PaddingKt.m685paddingqDBjuR0$default(boxScopeInstance.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenterEnd()), 0.0f, 0.0f, Dp.m6300constructorimpl(4), 0.0f, 11, null);
                                    Alignment.Vertical centerVertically2 = Alignment.INSTANCE.getCenterVertically();
                                    Arrangement.HorizontalOrVertical m561spacedBy0680j_42 = Arrangement.INSTANCE.m561spacedBy0680j_4(Dp.m6300constructorimpl(f3));
                                    ComposerKt.sourceInformationMarkerStart(composer2, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
                                    MeasurePolicy rowMeasurePolicy2 = RowKt.rowMeasurePolicy(m561spacedBy0680j_42, centerVertically2, composer2, 54);
                                    ComposerKt.sourceInformationMarkerStart(composer2, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                                    currentCompositeKeyHash3 = ComposablesKt.getCurrentCompositeKeyHash(composer2, 0);
                                    CompositionLocalMap currentCompositionLocalMap3 = composer2.getCurrentCompositionLocalMap();
                                    Modifier materializeModifier3 = ComposedModifierKt.materializeModifier(composer2, m685paddingqDBjuR0$default);
                                    Function0<ComposeUiNode> constructor3 = ComposeUiNode.INSTANCE.getConstructor();
                                    ComposerKt.sourceInformationMarkerStart(composer2, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                                    if (!(composer2.getApplier() instanceof Applier)) {
                                        ComposablesKt.invalidApplier();
                                    }
                                    composer2.startReusableNode();
                                    if (composer2.getInserting()) {
                                        composer2.createNode(constructor3);
                                    } else {
                                        composer2.useNode();
                                    }
                                    m3333constructorimpl3 = Updater.m3333constructorimpl(composer2);
                                    Updater.m3340setimpl(m3333constructorimpl3, rowMeasurePolicy2, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                                    Updater.m3340setimpl(m3333constructorimpl3, currentCompositionLocalMap3, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                                    Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash3 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                                    if (!m3333constructorimpl3.getInserting() || !Intrinsics.areEqual(m3333constructorimpl3.rememberedValue(), Integer.valueOf(currentCompositeKeyHash3))) {
                                        m3333constructorimpl3.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash3));
                                        m3333constructorimpl3.apply(Integer.valueOf(currentCompositeKeyHash3), setCompositeKeyHash3);
                                    }
                                    Updater.m3340setimpl(m3333constructorimpl3, materializeModifier3, ComposeUiNode.INSTANCE.getSetModifier());
                                    ComposerKt.sourceInformationMarkerStart(composer2, -407840262, "C101@5126L9:Row.kt#2w3rfo");
                                    RowScopeInstance rowScopeInstance2 = RowScopeInstance.INSTANCE;
                                    TrayCluster(z2, onToggleQuick, composer2, ((i14 >> 6) & 14) | ((i14 >> 18) & 112));
                                    Clock(z3, onToggleNotif, composer2, ((i14 >> 9) & 14) | ((i14 >> 21) & 112));
                                    ComposerKt.sourceInformationMarkerEnd(composer2);
                                    composer2.endNode();
                                    ComposerKt.sourceInformationMarkerEnd(composer2);
                                    ComposerKt.sourceInformationMarkerEnd(composer2);
                                    ComposerKt.sourceInformationMarkerEnd(composer2);
                                    ComposerKt.sourceInformationMarkerEnd(composer2);
                                    composer2.endNode();
                                    ComposerKt.sourceInformationMarkerEnd(composer2);
                                    ComposerKt.sourceInformationMarkerEnd(composer2);
                                    ComposerKt.sourceInformationMarkerEnd(composer2);
                                    if (ComposerKt.isTraceInProgress()) {
                                        ComposerKt.traceEventEnd();
                                    }
                                }
                                endRestartGroup = composer2.endRestartGroup();
                                if (endRestartGroup != null) {
                                    final Modifier modifier3 = modifier2;
                                    endRestartGroup.updateScope(new Function2() { // from class: com.neversoft.launcher.ui.TaskbarKt$$ExternalSyntheticLambda3
                                        @Override // kotlin.jvm.functions.Function2
                                        public final Object invoke(Object obj, Object obj2) {
                                            Unit Taskbar$lambda$6;
                                            Taskbar$lambda$6 = TaskbarKt.Taskbar$lambda$6(apps, z, z2, z3, z4, z5, onToggleStart, onToggleQuick, onToggleNotif, onToggleTaskView, onToggleWidgets, onLaunch, modifier3, i, i2, i3, (Composer) obj, ((Integer) obj2).intValue());
                                            return Taskbar$lambda$6;
                                        }
                                    });
                                    return;
                                }
                                return;
                            }
                            if ((i4 & 306783379) != 306783378) {
                            }
                            if (i6 != 0) {
                            }
                            if (ComposerKt.isTraceInProgress()) {
                            }
                            Modifier m236backgroundbw27NRU$default2 = BackgroundKt.m236backgroundbw27NRU$default(SizeKt.m712height3ABfNKs(SizeKt.fillMaxWidth$default(companion, 0.0f, 1, null), LauncherState.INSTANCE.m6622getTaskbarHeightD9Ej5fM()), NsColor.INSTANCE.m6651getAcrylicTaskbar0d7_KjU(), null, 2, null);
                            ComposerKt.sourceInformationMarkerStart(startRestartGroup, 733328855, "CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
                            MeasurePolicy maybeCachedBoxMeasurePolicy2 = BoxKt.maybeCachedBoxMeasurePolicy(Alignment.INSTANCE.getTopStart(), false);
                            modifier2 = companion;
                            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                            currentCompositeKeyHash = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
                            CompositionLocalMap currentCompositionLocalMap4 = startRestartGroup.getCurrentCompositionLocalMap();
                            Modifier materializeModifier4 = ComposedModifierKt.materializeModifier(startRestartGroup, m236backgroundbw27NRU$default2);
                            Function0<ComposeUiNode> constructor4 = ComposeUiNode.INSTANCE.getConstructor();
                            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                            if (!(startRestartGroup.getApplier() instanceof Applier)) {
                            }
                            startRestartGroup.startReusableNode();
                            if (startRestartGroup.getInserting()) {
                            }
                            m3333constructorimpl = Updater.m3333constructorimpl(startRestartGroup);
                            Updater.m3340setimpl(m3333constructorimpl, maybeCachedBoxMeasurePolicy2, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                            Updater.m3340setimpl(m3333constructorimpl, currentCompositionLocalMap4, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                            Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash4 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                            if (!m3333constructorimpl.getInserting()) {
                            }
                            m3333constructorimpl.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash));
                            m3333constructorimpl.apply(Integer.valueOf(currentCompositeKeyHash), setCompositeKeyHash4);
                            Updater.m3340setimpl(m3333constructorimpl, materializeModifier4, ComposeUiNode.INSTANCE.getSetModifier());
                            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -2146769399, "C73@3429L9:Box.kt#2w3rfo");
                            BoxScopeInstance boxScopeInstance2 = BoxScopeInstance.INSTANCE;
                            BoxKt.Box(BackgroundKt.m236backgroundbw27NRU$default(SizeKt.m712height3ABfNKs(SizeKt.fillMaxWidth$default(boxScopeInstance2.align(Modifier.INSTANCE, Alignment.INSTANCE.getTopCenter()), 0.0f, 1, null), Dp.m6300constructorimpl(1)), NsColor.INSTANCE.m6660getStroke0d7_KjU(), null, 2, null), startRestartGroup, 0);
                            float f4 = 6;
                            int i112 = i5;
                            TextKt.m2373Text4IGK_g("72°  Sunny", PaddingKt.m682paddingVpY3zN4(ClickableKt.m269clickableXHw0xAI$default(ClipKt.clip(PaddingKt.m685paddingqDBjuR0$default(boxScopeInstance2.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenterStart()), Dp.m6300constructorimpl(f4), 0.0f, 0.0f, 0.0f, 14, null), RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(NsDim.INSTANCE.m6669getRadiusControlD9Ej5fM())), false, null, null, onToggleWidgets, 7, null), Dp.m6300constructorimpl(10), Dp.m6300constructorimpl(f4)), NsColor.INSTANCE.m6665getTextSecondary0d7_KjU(), TextUnitKt.getSp(12), (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1<? super TextLayoutResult, Unit>) null, (TextStyle) null, startRestartGroup, 3462, 0, 131056);
                            Modifier align2 = boxScopeInstance2.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenter());
                            Alignment.Vertical centerVertically3 = Alignment.INSTANCE.getCenterVertically();
                            float f22 = 2;
                            Arrangement.HorizontalOrVertical m561spacedBy0680j_43 = Arrangement.INSTANCE.m561spacedBy0680j_4(Dp.m6300constructorimpl(f22));
                            ComposerKt.sourceInformationMarkerStart(startRestartGroup, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
                            MeasurePolicy rowMeasurePolicy3 = RowKt.rowMeasurePolicy(m561spacedBy0680j_43, centerVertically3, startRestartGroup, 54);
                            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                            currentCompositeKeyHash2 = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
                            CompositionLocalMap currentCompositionLocalMap22 = startRestartGroup.getCurrentCompositionLocalMap();
                            Modifier materializeModifier22 = ComposedModifierKt.materializeModifier(startRestartGroup, align2);
                            Function0<ComposeUiNode> constructor22 = ComposeUiNode.INSTANCE.getConstructor();
                            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                            if (!(startRestartGroup.getApplier() instanceof Applier)) {
                            }
                            startRestartGroup.startReusableNode();
                            if (startRestartGroup.getInserting()) {
                            }
                            m3333constructorimpl2 = Updater.m3333constructorimpl(startRestartGroup);
                            Updater.m3340setimpl(m3333constructorimpl2, rowMeasurePolicy3, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                            Updater.m3340setimpl(m3333constructorimpl2, currentCompositionLocalMap22, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                            Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash22 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                            if (!m3333constructorimpl2.getInserting()) {
                            }
                            m3333constructorimpl2.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash2));
                            m3333constructorimpl2.apply(Integer.valueOf(currentCompositeKeyHash2), setCompositeKeyHash22);
                            Updater.m3340setimpl(m3333constructorimpl2, materializeModifier22, ComposeUiNode.INSTANCE.getSetModifier());
                            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -407840262, "C101@5126L9:Row.kt#2w3rfo");
                            RowScopeInstance rowScopeInstance3 = RowScopeInstance.INSTANCE;
                            int i122 = i4 >> 15;
                            int i132 = i122 & 112;
                            float f32 = f22;
                            int i142 = i4;
                            TaskbarButton(z, onToggleStart, ComposableSingletons$TaskbarKt.INSTANCE.m6607getLambda1$app_release(), startRestartGroup, ((i4 >> 3) & 14) | 384 | i132, 0);
                            TaskbarButton(false, onToggleStart, ComposableSingletons$TaskbarKt.INSTANCE.m6608getLambda2$app_release(), startRestartGroup, i132 | 384, 1);
                            TaskbarButton(z4, onToggleTaskView, ComposableSingletons$TaskbarKt.INSTANCE.m6609getLambda3$app_release(), startRestartGroup, ((i142 >> 12) & 14) | 384 | ((i142 >> 24) & 112), 0);
                            TaskbarButton(z5, onToggleWidgets, ComposableSingletons$TaskbarKt.INSTANCE.m6610getLambda4$app_release(), startRestartGroup, (i122 & 14) | 384 | ((i112 << 3) & 112), 0);
                            composer2 = startRestartGroup;
                            composer2.startReplaceGroup(-1916004207);
                            while (r4.hasNext()) {
                            }
                            composer2.endReplaceGroup();
                            ComposerKt.sourceInformationMarkerEnd(composer2);
                            composer2.endNode();
                            ComposerKt.sourceInformationMarkerEnd(composer2);
                            ComposerKt.sourceInformationMarkerEnd(composer2);
                            ComposerKt.sourceInformationMarkerEnd(composer2);
                            Modifier m685paddingqDBjuR0$default2 = PaddingKt.m685paddingqDBjuR0$default(boxScopeInstance2.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenterEnd()), 0.0f, 0.0f, Dp.m6300constructorimpl(4), 0.0f, 11, null);
                            Alignment.Vertical centerVertically22 = Alignment.INSTANCE.getCenterVertically();
                            Arrangement.HorizontalOrVertical m561spacedBy0680j_422 = Arrangement.INSTANCE.m561spacedBy0680j_4(Dp.m6300constructorimpl(f32));
                            ComposerKt.sourceInformationMarkerStart(composer2, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
                            MeasurePolicy rowMeasurePolicy22 = RowKt.rowMeasurePolicy(m561spacedBy0680j_422, centerVertically22, composer2, 54);
                            ComposerKt.sourceInformationMarkerStart(composer2, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                            currentCompositeKeyHash3 = ComposablesKt.getCurrentCompositeKeyHash(composer2, 0);
                            CompositionLocalMap currentCompositionLocalMap32 = composer2.getCurrentCompositionLocalMap();
                            Modifier materializeModifier32 = ComposedModifierKt.materializeModifier(composer2, m685paddingqDBjuR0$default2);
                            Function0<ComposeUiNode> constructor32 = ComposeUiNode.INSTANCE.getConstructor();
                            ComposerKt.sourceInformationMarkerStart(composer2, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                            if (!(composer2.getApplier() instanceof Applier)) {
                            }
                            composer2.startReusableNode();
                            if (composer2.getInserting()) {
                            }
                            m3333constructorimpl3 = Updater.m3333constructorimpl(composer2);
                            Updater.m3340setimpl(m3333constructorimpl3, rowMeasurePolicy22, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                            Updater.m3340setimpl(m3333constructorimpl3, currentCompositionLocalMap32, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                            Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash32 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                            if (!m3333constructorimpl3.getInserting()) {
                            }
                            m3333constructorimpl3.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash3));
                            m3333constructorimpl3.apply(Integer.valueOf(currentCompositeKeyHash3), setCompositeKeyHash32);
                            Updater.m3340setimpl(m3333constructorimpl3, materializeModifier32, ComposeUiNode.INSTANCE.getSetModifier());
                            ComposerKt.sourceInformationMarkerStart(composer2, -407840262, "C101@5126L9:Row.kt#2w3rfo");
                            RowScopeInstance rowScopeInstance22 = RowScopeInstance.INSTANCE;
                            TrayCluster(z2, onToggleQuick, composer2, ((i142 >> 6) & 14) | ((i142 >> 18) & 112));
                            Clock(z3, onToggleNotif, composer2, ((i142 >> 9) & 14) | ((i142 >> 21) & 112));
                            ComposerKt.sourceInformationMarkerEnd(composer2);
                            composer2.endNode();
                            ComposerKt.sourceInformationMarkerEnd(composer2);
                            ComposerKt.sourceInformationMarkerEnd(composer2);
                            ComposerKt.sourceInformationMarkerEnd(composer2);
                            ComposerKt.sourceInformationMarkerEnd(composer2);
                            composer2.endNode();
                            ComposerKt.sourceInformationMarkerEnd(composer2);
                            ComposerKt.sourceInformationMarkerEnd(composer2);
                            ComposerKt.sourceInformationMarkerEnd(composer2);
                            if (ComposerKt.isTraceInProgress()) {
                            }
                            endRestartGroup = composer2.endRestartGroup();
                            if (endRestartGroup != null) {
                            }
                        }
                        i4 |= i7;
                        if ((i3 & 1024) == 0) {
                        }
                        if ((i3 & 2048) == 0) {
                        }
                        i6 = i3 & 4096;
                        if (i6 == 0) {
                        }
                        if ((i4 & 306783379) != 306783378) {
                        }
                        if (i6 != 0) {
                        }
                        if (ComposerKt.isTraceInProgress()) {
                        }
                        Modifier m236backgroundbw27NRU$default22 = BackgroundKt.m236backgroundbw27NRU$default(SizeKt.m712height3ABfNKs(SizeKt.fillMaxWidth$default(companion, 0.0f, 1, null), LauncherState.INSTANCE.m6622getTaskbarHeightD9Ej5fM()), NsColor.INSTANCE.m6651getAcrylicTaskbar0d7_KjU(), null, 2, null);
                        ComposerKt.sourceInformationMarkerStart(startRestartGroup, 733328855, "CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
                        MeasurePolicy maybeCachedBoxMeasurePolicy22 = BoxKt.maybeCachedBoxMeasurePolicy(Alignment.INSTANCE.getTopStart(), false);
                        modifier2 = companion;
                        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                        currentCompositeKeyHash = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
                        CompositionLocalMap currentCompositionLocalMap42 = startRestartGroup.getCurrentCompositionLocalMap();
                        Modifier materializeModifier42 = ComposedModifierKt.materializeModifier(startRestartGroup, m236backgroundbw27NRU$default22);
                        Function0<ComposeUiNode> constructor42 = ComposeUiNode.INSTANCE.getConstructor();
                        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                        if (!(startRestartGroup.getApplier() instanceof Applier)) {
                        }
                        startRestartGroup.startReusableNode();
                        if (startRestartGroup.getInserting()) {
                        }
                        m3333constructorimpl = Updater.m3333constructorimpl(startRestartGroup);
                        Updater.m3340setimpl(m3333constructorimpl, maybeCachedBoxMeasurePolicy22, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                        Updater.m3340setimpl(m3333constructorimpl, currentCompositionLocalMap42, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                        Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash42 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                        if (!m3333constructorimpl.getInserting()) {
                        }
                        m3333constructorimpl.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash));
                        m3333constructorimpl.apply(Integer.valueOf(currentCompositeKeyHash), setCompositeKeyHash42);
                        Updater.m3340setimpl(m3333constructorimpl, materializeModifier42, ComposeUiNode.INSTANCE.getSetModifier());
                        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -2146769399, "C73@3429L9:Box.kt#2w3rfo");
                        BoxScopeInstance boxScopeInstance22 = BoxScopeInstance.INSTANCE;
                        BoxKt.Box(BackgroundKt.m236backgroundbw27NRU$default(SizeKt.m712height3ABfNKs(SizeKt.fillMaxWidth$default(boxScopeInstance22.align(Modifier.INSTANCE, Alignment.INSTANCE.getTopCenter()), 0.0f, 1, null), Dp.m6300constructorimpl(1)), NsColor.INSTANCE.m6660getStroke0d7_KjU(), null, 2, null), startRestartGroup, 0);
                        float f42 = 6;
                        int i1122 = i5;
                        TextKt.m2373Text4IGK_g("72°  Sunny", PaddingKt.m682paddingVpY3zN4(ClickableKt.m269clickableXHw0xAI$default(ClipKt.clip(PaddingKt.m685paddingqDBjuR0$default(boxScopeInstance22.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenterStart()), Dp.m6300constructorimpl(f42), 0.0f, 0.0f, 0.0f, 14, null), RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(NsDim.INSTANCE.m6669getRadiusControlD9Ej5fM())), false, null, null, onToggleWidgets, 7, null), Dp.m6300constructorimpl(10), Dp.m6300constructorimpl(f42)), NsColor.INSTANCE.m6665getTextSecondary0d7_KjU(), TextUnitKt.getSp(12), (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1<? super TextLayoutResult, Unit>) null, (TextStyle) null, startRestartGroup, 3462, 0, 131056);
                        Modifier align22 = boxScopeInstance22.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenter());
                        Alignment.Vertical centerVertically32 = Alignment.INSTANCE.getCenterVertically();
                        float f222 = 2;
                        Arrangement.HorizontalOrVertical m561spacedBy0680j_432 = Arrangement.INSTANCE.m561spacedBy0680j_4(Dp.m6300constructorimpl(f222));
                        ComposerKt.sourceInformationMarkerStart(startRestartGroup, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
                        MeasurePolicy rowMeasurePolicy32 = RowKt.rowMeasurePolicy(m561spacedBy0680j_432, centerVertically32, startRestartGroup, 54);
                        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                        currentCompositeKeyHash2 = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
                        CompositionLocalMap currentCompositionLocalMap222 = startRestartGroup.getCurrentCompositionLocalMap();
                        Modifier materializeModifier222 = ComposedModifierKt.materializeModifier(startRestartGroup, align22);
                        Function0<ComposeUiNode> constructor222 = ComposeUiNode.INSTANCE.getConstructor();
                        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                        if (!(startRestartGroup.getApplier() instanceof Applier)) {
                        }
                        startRestartGroup.startReusableNode();
                        if (startRestartGroup.getInserting()) {
                        }
                        m3333constructorimpl2 = Updater.m3333constructorimpl(startRestartGroup);
                        Updater.m3340setimpl(m3333constructorimpl2, rowMeasurePolicy32, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                        Updater.m3340setimpl(m3333constructorimpl2, currentCompositionLocalMap222, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                        Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash222 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                        if (!m3333constructorimpl2.getInserting()) {
                        }
                        m3333constructorimpl2.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash2));
                        m3333constructorimpl2.apply(Integer.valueOf(currentCompositeKeyHash2), setCompositeKeyHash222);
                        Updater.m3340setimpl(m3333constructorimpl2, materializeModifier222, ComposeUiNode.INSTANCE.getSetModifier());
                        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -407840262, "C101@5126L9:Row.kt#2w3rfo");
                        RowScopeInstance rowScopeInstance32 = RowScopeInstance.INSTANCE;
                        int i1222 = i4 >> 15;
                        int i1322 = i1222 & 112;
                        float f322 = f222;
                        int i1422 = i4;
                        TaskbarButton(z, onToggleStart, ComposableSingletons$TaskbarKt.INSTANCE.m6607getLambda1$app_release(), startRestartGroup, ((i4 >> 3) & 14) | 384 | i1322, 0);
                        TaskbarButton(false, onToggleStart, ComposableSingletons$TaskbarKt.INSTANCE.m6608getLambda2$app_release(), startRestartGroup, i1322 | 384, 1);
                        TaskbarButton(z4, onToggleTaskView, ComposableSingletons$TaskbarKt.INSTANCE.m6609getLambda3$app_release(), startRestartGroup, ((i1422 >> 12) & 14) | 384 | ((i1422 >> 24) & 112), 0);
                        TaskbarButton(z5, onToggleWidgets, ComposableSingletons$TaskbarKt.INSTANCE.m6610getLambda4$app_release(), startRestartGroup, (i1222 & 14) | 384 | ((i1122 << 3) & 112), 0);
                        composer2 = startRestartGroup;
                        composer2.startReplaceGroup(-1916004207);
                        while (r4.hasNext()) {
                        }
                        composer2.endReplaceGroup();
                        ComposerKt.sourceInformationMarkerEnd(composer2);
                        composer2.endNode();
                        ComposerKt.sourceInformationMarkerEnd(composer2);
                        ComposerKt.sourceInformationMarkerEnd(composer2);
                        ComposerKt.sourceInformationMarkerEnd(composer2);
                        Modifier m685paddingqDBjuR0$default22 = PaddingKt.m685paddingqDBjuR0$default(boxScopeInstance22.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenterEnd()), 0.0f, 0.0f, Dp.m6300constructorimpl(4), 0.0f, 11, null);
                        Alignment.Vertical centerVertically222 = Alignment.INSTANCE.getCenterVertically();
                        Arrangement.HorizontalOrVertical m561spacedBy0680j_4222 = Arrangement.INSTANCE.m561spacedBy0680j_4(Dp.m6300constructorimpl(f322));
                        ComposerKt.sourceInformationMarkerStart(composer2, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
                        MeasurePolicy rowMeasurePolicy222 = RowKt.rowMeasurePolicy(m561spacedBy0680j_4222, centerVertically222, composer2, 54);
                        ComposerKt.sourceInformationMarkerStart(composer2, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                        currentCompositeKeyHash3 = ComposablesKt.getCurrentCompositeKeyHash(composer2, 0);
                        CompositionLocalMap currentCompositionLocalMap322 = composer2.getCurrentCompositionLocalMap();
                        Modifier materializeModifier322 = ComposedModifierKt.materializeModifier(composer2, m685paddingqDBjuR0$default22);
                        Function0<ComposeUiNode> constructor322 = ComposeUiNode.INSTANCE.getConstructor();
                        ComposerKt.sourceInformationMarkerStart(composer2, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                        if (!(composer2.getApplier() instanceof Applier)) {
                        }
                        composer2.startReusableNode();
                        if (composer2.getInserting()) {
                        }
                        m3333constructorimpl3 = Updater.m3333constructorimpl(composer2);
                        Updater.m3340setimpl(m3333constructorimpl3, rowMeasurePolicy222, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                        Updater.m3340setimpl(m3333constructorimpl3, currentCompositionLocalMap322, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                        Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash322 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                        if (!m3333constructorimpl3.getInserting()) {
                        }
                        m3333constructorimpl3.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash3));
                        m3333constructorimpl3.apply(Integer.valueOf(currentCompositeKeyHash3), setCompositeKeyHash322);
                        Updater.m3340setimpl(m3333constructorimpl3, materializeModifier322, ComposeUiNode.INSTANCE.getSetModifier());
                        ComposerKt.sourceInformationMarkerStart(composer2, -407840262, "C101@5126L9:Row.kt#2w3rfo");
                        RowScopeInstance rowScopeInstance222 = RowScopeInstance.INSTANCE;
                        TrayCluster(z2, onToggleQuick, composer2, ((i1422 >> 6) & 14) | ((i1422 >> 18) & 112));
                        Clock(z3, onToggleNotif, composer2, ((i1422 >> 9) & 14) | ((i1422 >> 21) & 112));
                        ComposerKt.sourceInformationMarkerEnd(composer2);
                        composer2.endNode();
                        ComposerKt.sourceInformationMarkerEnd(composer2);
                        ComposerKt.sourceInformationMarkerEnd(composer2);
                        ComposerKt.sourceInformationMarkerEnd(composer2);
                        ComposerKt.sourceInformationMarkerEnd(composer2);
                        composer2.endNode();
                        ComposerKt.sourceInformationMarkerEnd(composer2);
                        ComposerKt.sourceInformationMarkerEnd(composer2);
                        ComposerKt.sourceInformationMarkerEnd(composer2);
                        if (ComposerKt.isTraceInProgress()) {
                        }
                        endRestartGroup = composer2.endRestartGroup();
                        if (endRestartGroup != null) {
                        }
                    }
                    i4 |= i8;
                    if ((i3 & 512) == 0) {
                    }
                    i4 |= i7;
                    if ((i3 & 1024) == 0) {
                    }
                    if ((i3 & 2048) == 0) {
                    }
                    i6 = i3 & 4096;
                    if (i6 == 0) {
                    }
                    if ((i4 & 306783379) != 306783378) {
                    }
                    if (i6 != 0) {
                    }
                    if (ComposerKt.isTraceInProgress()) {
                    }
                    Modifier m236backgroundbw27NRU$default222 = BackgroundKt.m236backgroundbw27NRU$default(SizeKt.m712height3ABfNKs(SizeKt.fillMaxWidth$default(companion, 0.0f, 1, null), LauncherState.INSTANCE.m6622getTaskbarHeightD9Ej5fM()), NsColor.INSTANCE.m6651getAcrylicTaskbar0d7_KjU(), null, 2, null);
                    ComposerKt.sourceInformationMarkerStart(startRestartGroup, 733328855, "CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
                    MeasurePolicy maybeCachedBoxMeasurePolicy222 = BoxKt.maybeCachedBoxMeasurePolicy(Alignment.INSTANCE.getTopStart(), false);
                    modifier2 = companion;
                    ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                    currentCompositeKeyHash = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
                    CompositionLocalMap currentCompositionLocalMap422 = startRestartGroup.getCurrentCompositionLocalMap();
                    Modifier materializeModifier422 = ComposedModifierKt.materializeModifier(startRestartGroup, m236backgroundbw27NRU$default222);
                    Function0<ComposeUiNode> constructor422 = ComposeUiNode.INSTANCE.getConstructor();
                    ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                    if (!(startRestartGroup.getApplier() instanceof Applier)) {
                    }
                    startRestartGroup.startReusableNode();
                    if (startRestartGroup.getInserting()) {
                    }
                    m3333constructorimpl = Updater.m3333constructorimpl(startRestartGroup);
                    Updater.m3340setimpl(m3333constructorimpl, maybeCachedBoxMeasurePolicy222, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                    Updater.m3340setimpl(m3333constructorimpl, currentCompositionLocalMap422, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                    Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash422 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                    if (!m3333constructorimpl.getInserting()) {
                    }
                    m3333constructorimpl.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash));
                    m3333constructorimpl.apply(Integer.valueOf(currentCompositeKeyHash), setCompositeKeyHash422);
                    Updater.m3340setimpl(m3333constructorimpl, materializeModifier422, ComposeUiNode.INSTANCE.getSetModifier());
                    ComposerKt.sourceInformationMarkerStart(startRestartGroup, -2146769399, "C73@3429L9:Box.kt#2w3rfo");
                    BoxScopeInstance boxScopeInstance222 = BoxScopeInstance.INSTANCE;
                    BoxKt.Box(BackgroundKt.m236backgroundbw27NRU$default(SizeKt.m712height3ABfNKs(SizeKt.fillMaxWidth$default(boxScopeInstance222.align(Modifier.INSTANCE, Alignment.INSTANCE.getTopCenter()), 0.0f, 1, null), Dp.m6300constructorimpl(1)), NsColor.INSTANCE.m6660getStroke0d7_KjU(), null, 2, null), startRestartGroup, 0);
                    float f422 = 6;
                    int i11222 = i5;
                    TextKt.m2373Text4IGK_g("72°  Sunny", PaddingKt.m682paddingVpY3zN4(ClickableKt.m269clickableXHw0xAI$default(ClipKt.clip(PaddingKt.m685paddingqDBjuR0$default(boxScopeInstance222.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenterStart()), Dp.m6300constructorimpl(f422), 0.0f, 0.0f, 0.0f, 14, null), RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(NsDim.INSTANCE.m6669getRadiusControlD9Ej5fM())), false, null, null, onToggleWidgets, 7, null), Dp.m6300constructorimpl(10), Dp.m6300constructorimpl(f422)), NsColor.INSTANCE.m6665getTextSecondary0d7_KjU(), TextUnitKt.getSp(12), (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1<? super TextLayoutResult, Unit>) null, (TextStyle) null, startRestartGroup, 3462, 0, 131056);
                    Modifier align222 = boxScopeInstance222.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenter());
                    Alignment.Vertical centerVertically322 = Alignment.INSTANCE.getCenterVertically();
                    float f2222 = 2;
                    Arrangement.HorizontalOrVertical m561spacedBy0680j_4322 = Arrangement.INSTANCE.m561spacedBy0680j_4(Dp.m6300constructorimpl(f2222));
                    ComposerKt.sourceInformationMarkerStart(startRestartGroup, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
                    MeasurePolicy rowMeasurePolicy322 = RowKt.rowMeasurePolicy(m561spacedBy0680j_4322, centerVertically322, startRestartGroup, 54);
                    ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                    currentCompositeKeyHash2 = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
                    CompositionLocalMap currentCompositionLocalMap2222 = startRestartGroup.getCurrentCompositionLocalMap();
                    Modifier materializeModifier2222 = ComposedModifierKt.materializeModifier(startRestartGroup, align222);
                    Function0<ComposeUiNode> constructor2222 = ComposeUiNode.INSTANCE.getConstructor();
                    ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                    if (!(startRestartGroup.getApplier() instanceof Applier)) {
                    }
                    startRestartGroup.startReusableNode();
                    if (startRestartGroup.getInserting()) {
                    }
                    m3333constructorimpl2 = Updater.m3333constructorimpl(startRestartGroup);
                    Updater.m3340setimpl(m3333constructorimpl2, rowMeasurePolicy322, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                    Updater.m3340setimpl(m3333constructorimpl2, currentCompositionLocalMap2222, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                    Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash2222 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                    if (!m3333constructorimpl2.getInserting()) {
                    }
                    m3333constructorimpl2.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash2));
                    m3333constructorimpl2.apply(Integer.valueOf(currentCompositeKeyHash2), setCompositeKeyHash2222);
                    Updater.m3340setimpl(m3333constructorimpl2, materializeModifier2222, ComposeUiNode.INSTANCE.getSetModifier());
                    ComposerKt.sourceInformationMarkerStart(startRestartGroup, -407840262, "C101@5126L9:Row.kt#2w3rfo");
                    RowScopeInstance rowScopeInstance322 = RowScopeInstance.INSTANCE;
                    int i12222 = i4 >> 15;
                    int i13222 = i12222 & 112;
                    float f3222 = f2222;
                    int i14222 = i4;
                    TaskbarButton(z, onToggleStart, ComposableSingletons$TaskbarKt.INSTANCE.m6607getLambda1$app_release(), startRestartGroup, ((i4 >> 3) & 14) | 384 | i13222, 0);
                    TaskbarButton(false, onToggleStart, ComposableSingletons$TaskbarKt.INSTANCE.m6608getLambda2$app_release(), startRestartGroup, i13222 | 384, 1);
                    TaskbarButton(z4, onToggleTaskView, ComposableSingletons$TaskbarKt.INSTANCE.m6609getLambda3$app_release(), startRestartGroup, ((i14222 >> 12) & 14) | 384 | ((i14222 >> 24) & 112), 0);
                    TaskbarButton(z5, onToggleWidgets, ComposableSingletons$TaskbarKt.INSTANCE.m6610getLambda4$app_release(), startRestartGroup, (i12222 & 14) | 384 | ((i11222 << 3) & 112), 0);
                    composer2 = startRestartGroup;
                    composer2.startReplaceGroup(-1916004207);
                    while (r4.hasNext()) {
                    }
                    composer2.endReplaceGroup();
                    ComposerKt.sourceInformationMarkerEnd(composer2);
                    composer2.endNode();
                    ComposerKt.sourceInformationMarkerEnd(composer2);
                    ComposerKt.sourceInformationMarkerEnd(composer2);
                    ComposerKt.sourceInformationMarkerEnd(composer2);
                    Modifier m685paddingqDBjuR0$default222 = PaddingKt.m685paddingqDBjuR0$default(boxScopeInstance222.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenterEnd()), 0.0f, 0.0f, Dp.m6300constructorimpl(4), 0.0f, 11, null);
                    Alignment.Vertical centerVertically2222 = Alignment.INSTANCE.getCenterVertically();
                    Arrangement.HorizontalOrVertical m561spacedBy0680j_42222 = Arrangement.INSTANCE.m561spacedBy0680j_4(Dp.m6300constructorimpl(f3222));
                    ComposerKt.sourceInformationMarkerStart(composer2, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
                    MeasurePolicy rowMeasurePolicy2222 = RowKt.rowMeasurePolicy(m561spacedBy0680j_42222, centerVertically2222, composer2, 54);
                    ComposerKt.sourceInformationMarkerStart(composer2, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                    currentCompositeKeyHash3 = ComposablesKt.getCurrentCompositeKeyHash(composer2, 0);
                    CompositionLocalMap currentCompositionLocalMap3222 = composer2.getCurrentCompositionLocalMap();
                    Modifier materializeModifier3222 = ComposedModifierKt.materializeModifier(composer2, m685paddingqDBjuR0$default222);
                    Function0<ComposeUiNode> constructor3222 = ComposeUiNode.INSTANCE.getConstructor();
                    ComposerKt.sourceInformationMarkerStart(composer2, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                    if (!(composer2.getApplier() instanceof Applier)) {
                    }
                    composer2.startReusableNode();
                    if (composer2.getInserting()) {
                    }
                    m3333constructorimpl3 = Updater.m3333constructorimpl(composer2);
                    Updater.m3340setimpl(m3333constructorimpl3, rowMeasurePolicy2222, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                    Updater.m3340setimpl(m3333constructorimpl3, currentCompositionLocalMap3222, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                    Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash3222 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                    if (!m3333constructorimpl3.getInserting()) {
                    }
                    m3333constructorimpl3.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash3));
                    m3333constructorimpl3.apply(Integer.valueOf(currentCompositeKeyHash3), setCompositeKeyHash3222);
                    Updater.m3340setimpl(m3333constructorimpl3, materializeModifier3222, ComposeUiNode.INSTANCE.getSetModifier());
                    ComposerKt.sourceInformationMarkerStart(composer2, -407840262, "C101@5126L9:Row.kt#2w3rfo");
                    RowScopeInstance rowScopeInstance2222 = RowScopeInstance.INSTANCE;
                    TrayCluster(z2, onToggleQuick, composer2, ((i14222 >> 6) & 14) | ((i14222 >> 18) & 112));
                    Clock(z3, onToggleNotif, composer2, ((i14222 >> 9) & 14) | ((i14222 >> 21) & 112));
                    ComposerKt.sourceInformationMarkerEnd(composer2);
                    composer2.endNode();
                    ComposerKt.sourceInformationMarkerEnd(composer2);
                    ComposerKt.sourceInformationMarkerEnd(composer2);
                    ComposerKt.sourceInformationMarkerEnd(composer2);
                    ComposerKt.sourceInformationMarkerEnd(composer2);
                    composer2.endNode();
                    ComposerKt.sourceInformationMarkerEnd(composer2);
                    ComposerKt.sourceInformationMarkerEnd(composer2);
                    ComposerKt.sourceInformationMarkerEnd(composer2);
                    if (ComposerKt.isTraceInProgress()) {
                    }
                    endRestartGroup = composer2.endRestartGroup();
                    if (endRestartGroup != null) {
                    }
                }
                i4 |= i9;
                if ((i3 & 256) != 0) {
                }
                i4 |= i8;
                if ((i3 & 512) == 0) {
                }
                i4 |= i7;
                if ((i3 & 1024) == 0) {
                }
                if ((i3 & 2048) == 0) {
                }
                i6 = i3 & 4096;
                if (i6 == 0) {
                }
                if ((i4 & 306783379) != 306783378) {
                }
                if (i6 != 0) {
                }
                if (ComposerKt.isTraceInProgress()) {
                }
                Modifier m236backgroundbw27NRU$default2222 = BackgroundKt.m236backgroundbw27NRU$default(SizeKt.m712height3ABfNKs(SizeKt.fillMaxWidth$default(companion, 0.0f, 1, null), LauncherState.INSTANCE.m6622getTaskbarHeightD9Ej5fM()), NsColor.INSTANCE.m6651getAcrylicTaskbar0d7_KjU(), null, 2, null);
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, 733328855, "CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
                MeasurePolicy maybeCachedBoxMeasurePolicy2222 = BoxKt.maybeCachedBoxMeasurePolicy(Alignment.INSTANCE.getTopStart(), false);
                modifier2 = companion;
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                currentCompositeKeyHash = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
                CompositionLocalMap currentCompositionLocalMap4222 = startRestartGroup.getCurrentCompositionLocalMap();
                Modifier materializeModifier4222 = ComposedModifierKt.materializeModifier(startRestartGroup, m236backgroundbw27NRU$default2222);
                Function0<ComposeUiNode> constructor4222 = ComposeUiNode.INSTANCE.getConstructor();
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                if (!(startRestartGroup.getApplier() instanceof Applier)) {
                }
                startRestartGroup.startReusableNode();
                if (startRestartGroup.getInserting()) {
                }
                m3333constructorimpl = Updater.m3333constructorimpl(startRestartGroup);
                Updater.m3340setimpl(m3333constructorimpl, maybeCachedBoxMeasurePolicy2222, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                Updater.m3340setimpl(m3333constructorimpl, currentCompositionLocalMap4222, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash4222 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                if (!m3333constructorimpl.getInserting()) {
                }
                m3333constructorimpl.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash));
                m3333constructorimpl.apply(Integer.valueOf(currentCompositeKeyHash), setCompositeKeyHash4222);
                Updater.m3340setimpl(m3333constructorimpl, materializeModifier4222, ComposeUiNode.INSTANCE.getSetModifier());
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, -2146769399, "C73@3429L9:Box.kt#2w3rfo");
                BoxScopeInstance boxScopeInstance2222 = BoxScopeInstance.INSTANCE;
                BoxKt.Box(BackgroundKt.m236backgroundbw27NRU$default(SizeKt.m712height3ABfNKs(SizeKt.fillMaxWidth$default(boxScopeInstance2222.align(Modifier.INSTANCE, Alignment.INSTANCE.getTopCenter()), 0.0f, 1, null), Dp.m6300constructorimpl(1)), NsColor.INSTANCE.m6660getStroke0d7_KjU(), null, 2, null), startRestartGroup, 0);
                float f4222 = 6;
                int i112222 = i5;
                TextKt.m2373Text4IGK_g("72°  Sunny", PaddingKt.m682paddingVpY3zN4(ClickableKt.m269clickableXHw0xAI$default(ClipKt.clip(PaddingKt.m685paddingqDBjuR0$default(boxScopeInstance2222.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenterStart()), Dp.m6300constructorimpl(f4222), 0.0f, 0.0f, 0.0f, 14, null), RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(NsDim.INSTANCE.m6669getRadiusControlD9Ej5fM())), false, null, null, onToggleWidgets, 7, null), Dp.m6300constructorimpl(10), Dp.m6300constructorimpl(f4222)), NsColor.INSTANCE.m6665getTextSecondary0d7_KjU(), TextUnitKt.getSp(12), (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1<? super TextLayoutResult, Unit>) null, (TextStyle) null, startRestartGroup, 3462, 0, 131056);
                Modifier align2222 = boxScopeInstance2222.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenter());
                Alignment.Vertical centerVertically3222 = Alignment.INSTANCE.getCenterVertically();
                float f22222 = 2;
                Arrangement.HorizontalOrVertical m561spacedBy0680j_43222 = Arrangement.INSTANCE.m561spacedBy0680j_4(Dp.m6300constructorimpl(f22222));
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
                MeasurePolicy rowMeasurePolicy3222 = RowKt.rowMeasurePolicy(m561spacedBy0680j_43222, centerVertically3222, startRestartGroup, 54);
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                currentCompositeKeyHash2 = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
                CompositionLocalMap currentCompositionLocalMap22222 = startRestartGroup.getCurrentCompositionLocalMap();
                Modifier materializeModifier22222 = ComposedModifierKt.materializeModifier(startRestartGroup, align2222);
                Function0<ComposeUiNode> constructor22222 = ComposeUiNode.INSTANCE.getConstructor();
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                if (!(startRestartGroup.getApplier() instanceof Applier)) {
                }
                startRestartGroup.startReusableNode();
                if (startRestartGroup.getInserting()) {
                }
                m3333constructorimpl2 = Updater.m3333constructorimpl(startRestartGroup);
                Updater.m3340setimpl(m3333constructorimpl2, rowMeasurePolicy3222, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                Updater.m3340setimpl(m3333constructorimpl2, currentCompositionLocalMap22222, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash22222 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                if (!m3333constructorimpl2.getInserting()) {
                }
                m3333constructorimpl2.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash2));
                m3333constructorimpl2.apply(Integer.valueOf(currentCompositeKeyHash2), setCompositeKeyHash22222);
                Updater.m3340setimpl(m3333constructorimpl2, materializeModifier22222, ComposeUiNode.INSTANCE.getSetModifier());
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, -407840262, "C101@5126L9:Row.kt#2w3rfo");
                RowScopeInstance rowScopeInstance3222 = RowScopeInstance.INSTANCE;
                int i122222 = i4 >> 15;
                int i132222 = i122222 & 112;
                float f32222 = f22222;
                int i142222 = i4;
                TaskbarButton(z, onToggleStart, ComposableSingletons$TaskbarKt.INSTANCE.m6607getLambda1$app_release(), startRestartGroup, ((i4 >> 3) & 14) | 384 | i132222, 0);
                TaskbarButton(false, onToggleStart, ComposableSingletons$TaskbarKt.INSTANCE.m6608getLambda2$app_release(), startRestartGroup, i132222 | 384, 1);
                TaskbarButton(z4, onToggleTaskView, ComposableSingletons$TaskbarKt.INSTANCE.m6609getLambda3$app_release(), startRestartGroup, ((i142222 >> 12) & 14) | 384 | ((i142222 >> 24) & 112), 0);
                TaskbarButton(z5, onToggleWidgets, ComposableSingletons$TaskbarKt.INSTANCE.m6610getLambda4$app_release(), startRestartGroup, (i122222 & 14) | 384 | ((i112222 << 3) & 112), 0);
                composer2 = startRestartGroup;
                composer2.startReplaceGroup(-1916004207);
                while (r4.hasNext()) {
                }
                composer2.endReplaceGroup();
                ComposerKt.sourceInformationMarkerEnd(composer2);
                composer2.endNode();
                ComposerKt.sourceInformationMarkerEnd(composer2);
                ComposerKt.sourceInformationMarkerEnd(composer2);
                ComposerKt.sourceInformationMarkerEnd(composer2);
                Modifier m685paddingqDBjuR0$default2222 = PaddingKt.m685paddingqDBjuR0$default(boxScopeInstance2222.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenterEnd()), 0.0f, 0.0f, Dp.m6300constructorimpl(4), 0.0f, 11, null);
                Alignment.Vertical centerVertically22222 = Alignment.INSTANCE.getCenterVertically();
                Arrangement.HorizontalOrVertical m561spacedBy0680j_422222 = Arrangement.INSTANCE.m561spacedBy0680j_4(Dp.m6300constructorimpl(f32222));
                ComposerKt.sourceInformationMarkerStart(composer2, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
                MeasurePolicy rowMeasurePolicy22222 = RowKt.rowMeasurePolicy(m561spacedBy0680j_422222, centerVertically22222, composer2, 54);
                ComposerKt.sourceInformationMarkerStart(composer2, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                currentCompositeKeyHash3 = ComposablesKt.getCurrentCompositeKeyHash(composer2, 0);
                CompositionLocalMap currentCompositionLocalMap32222 = composer2.getCurrentCompositionLocalMap();
                Modifier materializeModifier32222 = ComposedModifierKt.materializeModifier(composer2, m685paddingqDBjuR0$default2222);
                Function0<ComposeUiNode> constructor32222 = ComposeUiNode.INSTANCE.getConstructor();
                ComposerKt.sourceInformationMarkerStart(composer2, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                if (!(composer2.getApplier() instanceof Applier)) {
                }
                composer2.startReusableNode();
                if (composer2.getInserting()) {
                }
                m3333constructorimpl3 = Updater.m3333constructorimpl(composer2);
                Updater.m3340setimpl(m3333constructorimpl3, rowMeasurePolicy22222, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                Updater.m3340setimpl(m3333constructorimpl3, currentCompositionLocalMap32222, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash32222 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                if (!m3333constructorimpl3.getInserting()) {
                }
                m3333constructorimpl3.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash3));
                m3333constructorimpl3.apply(Integer.valueOf(currentCompositeKeyHash3), setCompositeKeyHash32222);
                Updater.m3340setimpl(m3333constructorimpl3, materializeModifier32222, ComposeUiNode.INSTANCE.getSetModifier());
                ComposerKt.sourceInformationMarkerStart(composer2, -407840262, "C101@5126L9:Row.kt#2w3rfo");
                RowScopeInstance rowScopeInstance22222 = RowScopeInstance.INSTANCE;
                TrayCluster(z2, onToggleQuick, composer2, ((i142222 >> 6) & 14) | ((i142222 >> 18) & 112));
                Clock(z3, onToggleNotif, composer2, ((i142222 >> 9) & 14) | ((i142222 >> 21) & 112));
                ComposerKt.sourceInformationMarkerEnd(composer2);
                composer2.endNode();
                ComposerKt.sourceInformationMarkerEnd(composer2);
                ComposerKt.sourceInformationMarkerEnd(composer2);
                ComposerKt.sourceInformationMarkerEnd(composer2);
                ComposerKt.sourceInformationMarkerEnd(composer2);
                composer2.endNode();
                ComposerKt.sourceInformationMarkerEnd(composer2);
                ComposerKt.sourceInformationMarkerEnd(composer2);
                ComposerKt.sourceInformationMarkerEnd(composer2);
                if (ComposerKt.isTraceInProgress()) {
                }
                endRestartGroup = composer2.endRestartGroup();
                if (endRestartGroup != null) {
                }
            }
            i4 |= i10;
            if ((i3 & 128) == 0) {
            }
            i4 |= i9;
            if ((i3 & 256) != 0) {
            }
            i4 |= i8;
            if ((i3 & 512) == 0) {
            }
            i4 |= i7;
            if ((i3 & 1024) == 0) {
            }
            if ((i3 & 2048) == 0) {
            }
            i6 = i3 & 4096;
            if (i6 == 0) {
            }
            if ((i4 & 306783379) != 306783378) {
            }
            if (i6 != 0) {
            }
            if (ComposerKt.isTraceInProgress()) {
            }
            Modifier m236backgroundbw27NRU$default22222 = BackgroundKt.m236backgroundbw27NRU$default(SizeKt.m712height3ABfNKs(SizeKt.fillMaxWidth$default(companion, 0.0f, 1, null), LauncherState.INSTANCE.m6622getTaskbarHeightD9Ej5fM()), NsColor.INSTANCE.m6651getAcrylicTaskbar0d7_KjU(), null, 2, null);
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, 733328855, "CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
            MeasurePolicy maybeCachedBoxMeasurePolicy22222 = BoxKt.maybeCachedBoxMeasurePolicy(Alignment.INSTANCE.getTopStart(), false);
            modifier2 = companion;
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            currentCompositeKeyHash = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
            CompositionLocalMap currentCompositionLocalMap42222 = startRestartGroup.getCurrentCompositionLocalMap();
            Modifier materializeModifier42222 = ComposedModifierKt.materializeModifier(startRestartGroup, m236backgroundbw27NRU$default22222);
            Function0<ComposeUiNode> constructor42222 = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(startRestartGroup.getApplier() instanceof Applier)) {
            }
            startRestartGroup.startReusableNode();
            if (startRestartGroup.getInserting()) {
            }
            m3333constructorimpl = Updater.m3333constructorimpl(startRestartGroup);
            Updater.m3340setimpl(m3333constructorimpl, maybeCachedBoxMeasurePolicy22222, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m3340setimpl(m3333constructorimpl, currentCompositionLocalMap42222, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash42222 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
            if (!m3333constructorimpl.getInserting()) {
            }
            m3333constructorimpl.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash));
            m3333constructorimpl.apply(Integer.valueOf(currentCompositeKeyHash), setCompositeKeyHash42222);
            Updater.m3340setimpl(m3333constructorimpl, materializeModifier42222, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -2146769399, "C73@3429L9:Box.kt#2w3rfo");
            BoxScopeInstance boxScopeInstance22222 = BoxScopeInstance.INSTANCE;
            BoxKt.Box(BackgroundKt.m236backgroundbw27NRU$default(SizeKt.m712height3ABfNKs(SizeKt.fillMaxWidth$default(boxScopeInstance22222.align(Modifier.INSTANCE, Alignment.INSTANCE.getTopCenter()), 0.0f, 1, null), Dp.m6300constructorimpl(1)), NsColor.INSTANCE.m6660getStroke0d7_KjU(), null, 2, null), startRestartGroup, 0);
            float f42222 = 6;
            int i1122222 = i5;
            TextKt.m2373Text4IGK_g("72°  Sunny", PaddingKt.m682paddingVpY3zN4(ClickableKt.m269clickableXHw0xAI$default(ClipKt.clip(PaddingKt.m685paddingqDBjuR0$default(boxScopeInstance22222.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenterStart()), Dp.m6300constructorimpl(f42222), 0.0f, 0.0f, 0.0f, 14, null), RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(NsDim.INSTANCE.m6669getRadiusControlD9Ej5fM())), false, null, null, onToggleWidgets, 7, null), Dp.m6300constructorimpl(10), Dp.m6300constructorimpl(f42222)), NsColor.INSTANCE.m6665getTextSecondary0d7_KjU(), TextUnitKt.getSp(12), (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1<? super TextLayoutResult, Unit>) null, (TextStyle) null, startRestartGroup, 3462, 0, 131056);
            Modifier align22222 = boxScopeInstance22222.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenter());
            Alignment.Vertical centerVertically32222 = Alignment.INSTANCE.getCenterVertically();
            float f222222 = 2;
            Arrangement.HorizontalOrVertical m561spacedBy0680j_432222 = Arrangement.INSTANCE.m561spacedBy0680j_4(Dp.m6300constructorimpl(f222222));
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
            MeasurePolicy rowMeasurePolicy32222 = RowKt.rowMeasurePolicy(m561spacedBy0680j_432222, centerVertically32222, startRestartGroup, 54);
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            currentCompositeKeyHash2 = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
            CompositionLocalMap currentCompositionLocalMap222222 = startRestartGroup.getCurrentCompositionLocalMap();
            Modifier materializeModifier222222 = ComposedModifierKt.materializeModifier(startRestartGroup, align22222);
            Function0<ComposeUiNode> constructor222222 = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(startRestartGroup.getApplier() instanceof Applier)) {
            }
            startRestartGroup.startReusableNode();
            if (startRestartGroup.getInserting()) {
            }
            m3333constructorimpl2 = Updater.m3333constructorimpl(startRestartGroup);
            Updater.m3340setimpl(m3333constructorimpl2, rowMeasurePolicy32222, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m3340setimpl(m3333constructorimpl2, currentCompositionLocalMap222222, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash222222 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
            if (!m3333constructorimpl2.getInserting()) {
            }
            m3333constructorimpl2.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash2));
            m3333constructorimpl2.apply(Integer.valueOf(currentCompositeKeyHash2), setCompositeKeyHash222222);
            Updater.m3340setimpl(m3333constructorimpl2, materializeModifier222222, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -407840262, "C101@5126L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance32222 = RowScopeInstance.INSTANCE;
            int i1222222 = i4 >> 15;
            int i1322222 = i1222222 & 112;
            float f322222 = f222222;
            int i1422222 = i4;
            TaskbarButton(z, onToggleStart, ComposableSingletons$TaskbarKt.INSTANCE.m6607getLambda1$app_release(), startRestartGroup, ((i4 >> 3) & 14) | 384 | i1322222, 0);
            TaskbarButton(false, onToggleStart, ComposableSingletons$TaskbarKt.INSTANCE.m6608getLambda2$app_release(), startRestartGroup, i1322222 | 384, 1);
            TaskbarButton(z4, onToggleTaskView, ComposableSingletons$TaskbarKt.INSTANCE.m6609getLambda3$app_release(), startRestartGroup, ((i1422222 >> 12) & 14) | 384 | ((i1422222 >> 24) & 112), 0);
            TaskbarButton(z5, onToggleWidgets, ComposableSingletons$TaskbarKt.INSTANCE.m6610getLambda4$app_release(), startRestartGroup, (i1222222 & 14) | 384 | ((i1122222 << 3) & 112), 0);
            composer2 = startRestartGroup;
            composer2.startReplaceGroup(-1916004207);
            while (r4.hasNext()) {
            }
            composer2.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd(composer2);
            composer2.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            Modifier m685paddingqDBjuR0$default22222 = PaddingKt.m685paddingqDBjuR0$default(boxScopeInstance22222.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenterEnd()), 0.0f, 0.0f, Dp.m6300constructorimpl(4), 0.0f, 11, null);
            Alignment.Vertical centerVertically222222 = Alignment.INSTANCE.getCenterVertically();
            Arrangement.HorizontalOrVertical m561spacedBy0680j_4222222 = Arrangement.INSTANCE.m561spacedBy0680j_4(Dp.m6300constructorimpl(f322222));
            ComposerKt.sourceInformationMarkerStart(composer2, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
            MeasurePolicy rowMeasurePolicy222222 = RowKt.rowMeasurePolicy(m561spacedBy0680j_4222222, centerVertically222222, composer2, 54);
            ComposerKt.sourceInformationMarkerStart(composer2, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            currentCompositeKeyHash3 = ComposablesKt.getCurrentCompositeKeyHash(composer2, 0);
            CompositionLocalMap currentCompositionLocalMap322222 = composer2.getCurrentCompositionLocalMap();
            Modifier materializeModifier322222 = ComposedModifierKt.materializeModifier(composer2, m685paddingqDBjuR0$default22222);
            Function0<ComposeUiNode> constructor322222 = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer2, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer2.getApplier() instanceof Applier)) {
            }
            composer2.startReusableNode();
            if (composer2.getInserting()) {
            }
            m3333constructorimpl3 = Updater.m3333constructorimpl(composer2);
            Updater.m3340setimpl(m3333constructorimpl3, rowMeasurePolicy222222, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m3340setimpl(m3333constructorimpl3, currentCompositionLocalMap322222, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash322222 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
            if (!m3333constructorimpl3.getInserting()) {
            }
            m3333constructorimpl3.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash3));
            m3333constructorimpl3.apply(Integer.valueOf(currentCompositeKeyHash3), setCompositeKeyHash322222);
            Updater.m3340setimpl(m3333constructorimpl3, materializeModifier322222, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer2, -407840262, "C101@5126L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance222222 = RowScopeInstance.INSTANCE;
            TrayCluster(z2, onToggleQuick, composer2, ((i1422222 >> 6) & 14) | ((i1422222 >> 18) & 112));
            Clock(z3, onToggleNotif, composer2, ((i1422222 >> 9) & 14) | ((i1422222 >> 21) & 112));
            ComposerKt.sourceInformationMarkerEnd(composer2);
            composer2.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            composer2.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            if (ComposerKt.isTraceInProgress()) {
            }
            endRestartGroup = composer2.endRestartGroup();
            if (endRestartGroup != null) {
            }
        }
        if ((i3 & 32) == 0) {
        }
        if ((i3 & 64) != 0) {
        }
        i4 |= i10;
        if ((i3 & 128) == 0) {
        }
        i4 |= i9;
        if ((i3 & 256) != 0) {
        }
        i4 |= i8;
        if ((i3 & 512) == 0) {
        }
        i4 |= i7;
        if ((i3 & 1024) == 0) {
        }
        if ((i3 & 2048) == 0) {
        }
        i6 = i3 & 4096;
        if (i6 == 0) {
        }
        if ((i4 & 306783379) != 306783378) {
        }
        if (i6 != 0) {
        }
        if (ComposerKt.isTraceInProgress()) {
        }
        Modifier m236backgroundbw27NRU$default222222 = BackgroundKt.m236backgroundbw27NRU$default(SizeKt.m712height3ABfNKs(SizeKt.fillMaxWidth$default(companion, 0.0f, 1, null), LauncherState.INSTANCE.m6622getTaskbarHeightD9Ej5fM()), NsColor.INSTANCE.m6651getAcrylicTaskbar0d7_KjU(), null, 2, null);
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, 733328855, "CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
        MeasurePolicy maybeCachedBoxMeasurePolicy222222 = BoxKt.maybeCachedBoxMeasurePolicy(Alignment.INSTANCE.getTopStart(), false);
        modifier2 = companion;
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
        currentCompositeKeyHash = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
        CompositionLocalMap currentCompositionLocalMap422222 = startRestartGroup.getCurrentCompositionLocalMap();
        Modifier materializeModifier422222 = ComposedModifierKt.materializeModifier(startRestartGroup, m236backgroundbw27NRU$default222222);
        Function0<ComposeUiNode> constructor422222 = ComposeUiNode.INSTANCE.getConstructor();
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
        if (!(startRestartGroup.getApplier() instanceof Applier)) {
        }
        startRestartGroup.startReusableNode();
        if (startRestartGroup.getInserting()) {
        }
        m3333constructorimpl = Updater.m3333constructorimpl(startRestartGroup);
        Updater.m3340setimpl(m3333constructorimpl, maybeCachedBoxMeasurePolicy222222, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
        Updater.m3340setimpl(m3333constructorimpl, currentCompositionLocalMap422222, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
        Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash422222 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
        if (!m3333constructorimpl.getInserting()) {
        }
        m3333constructorimpl.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash));
        m3333constructorimpl.apply(Integer.valueOf(currentCompositeKeyHash), setCompositeKeyHash422222);
        Updater.m3340setimpl(m3333constructorimpl, materializeModifier422222, ComposeUiNode.INSTANCE.getSetModifier());
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -2146769399, "C73@3429L9:Box.kt#2w3rfo");
        BoxScopeInstance boxScopeInstance222222 = BoxScopeInstance.INSTANCE;
        BoxKt.Box(BackgroundKt.m236backgroundbw27NRU$default(SizeKt.m712height3ABfNKs(SizeKt.fillMaxWidth$default(boxScopeInstance222222.align(Modifier.INSTANCE, Alignment.INSTANCE.getTopCenter()), 0.0f, 1, null), Dp.m6300constructorimpl(1)), NsColor.INSTANCE.m6660getStroke0d7_KjU(), null, 2, null), startRestartGroup, 0);
        float f422222 = 6;
        int i11222222 = i5;
        TextKt.m2373Text4IGK_g("72°  Sunny", PaddingKt.m682paddingVpY3zN4(ClickableKt.m269clickableXHw0xAI$default(ClipKt.clip(PaddingKt.m685paddingqDBjuR0$default(boxScopeInstance222222.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenterStart()), Dp.m6300constructorimpl(f422222), 0.0f, 0.0f, 0.0f, 14, null), RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(NsDim.INSTANCE.m6669getRadiusControlD9Ej5fM())), false, null, null, onToggleWidgets, 7, null), Dp.m6300constructorimpl(10), Dp.m6300constructorimpl(f422222)), NsColor.INSTANCE.m6665getTextSecondary0d7_KjU(), TextUnitKt.getSp(12), (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1<? super TextLayoutResult, Unit>) null, (TextStyle) null, startRestartGroup, 3462, 0, 131056);
        Modifier align222222 = boxScopeInstance222222.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenter());
        Alignment.Vertical centerVertically322222 = Alignment.INSTANCE.getCenterVertically();
        float f2222222 = 2;
        Arrangement.HorizontalOrVertical m561spacedBy0680j_4322222 = Arrangement.INSTANCE.m561spacedBy0680j_4(Dp.m6300constructorimpl(f2222222));
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
        MeasurePolicy rowMeasurePolicy322222 = RowKt.rowMeasurePolicy(m561spacedBy0680j_4322222, centerVertically322222, startRestartGroup, 54);
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
        currentCompositeKeyHash2 = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
        CompositionLocalMap currentCompositionLocalMap2222222 = startRestartGroup.getCurrentCompositionLocalMap();
        Modifier materializeModifier2222222 = ComposedModifierKt.materializeModifier(startRestartGroup, align222222);
        Function0<ComposeUiNode> constructor2222222 = ComposeUiNode.INSTANCE.getConstructor();
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
        if (!(startRestartGroup.getApplier() instanceof Applier)) {
        }
        startRestartGroup.startReusableNode();
        if (startRestartGroup.getInserting()) {
        }
        m3333constructorimpl2 = Updater.m3333constructorimpl(startRestartGroup);
        Updater.m3340setimpl(m3333constructorimpl2, rowMeasurePolicy322222, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
        Updater.m3340setimpl(m3333constructorimpl2, currentCompositionLocalMap2222222, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
        Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash2222222 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
        if (!m3333constructorimpl2.getInserting()) {
        }
        m3333constructorimpl2.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash2));
        m3333constructorimpl2.apply(Integer.valueOf(currentCompositeKeyHash2), setCompositeKeyHash2222222);
        Updater.m3340setimpl(m3333constructorimpl2, materializeModifier2222222, ComposeUiNode.INSTANCE.getSetModifier());
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -407840262, "C101@5126L9:Row.kt#2w3rfo");
        RowScopeInstance rowScopeInstance322222 = RowScopeInstance.INSTANCE;
        int i12222222 = i4 >> 15;
        int i13222222 = i12222222 & 112;
        float f3222222 = f2222222;
        int i14222222 = i4;
        TaskbarButton(z, onToggleStart, ComposableSingletons$TaskbarKt.INSTANCE.m6607getLambda1$app_release(), startRestartGroup, ((i4 >> 3) & 14) | 384 | i13222222, 0);
        TaskbarButton(false, onToggleStart, ComposableSingletons$TaskbarKt.INSTANCE.m6608getLambda2$app_release(), startRestartGroup, i13222222 | 384, 1);
        TaskbarButton(z4, onToggleTaskView, ComposableSingletons$TaskbarKt.INSTANCE.m6609getLambda3$app_release(), startRestartGroup, ((i14222222 >> 12) & 14) | 384 | ((i14222222 >> 24) & 112), 0);
        TaskbarButton(z5, onToggleWidgets, ComposableSingletons$TaskbarKt.INSTANCE.m6610getLambda4$app_release(), startRestartGroup, (i12222222 & 14) | 384 | ((i11222222 << 3) & 112), 0);
        composer2 = startRestartGroup;
        composer2.startReplaceGroup(-1916004207);
        while (r4.hasNext()) {
        }
        composer2.endReplaceGroup();
        ComposerKt.sourceInformationMarkerEnd(composer2);
        composer2.endNode();
        ComposerKt.sourceInformationMarkerEnd(composer2);
        ComposerKt.sourceInformationMarkerEnd(composer2);
        ComposerKt.sourceInformationMarkerEnd(composer2);
        Modifier m685paddingqDBjuR0$default222222 = PaddingKt.m685paddingqDBjuR0$default(boxScopeInstance222222.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenterEnd()), 0.0f, 0.0f, Dp.m6300constructorimpl(4), 0.0f, 11, null);
        Alignment.Vertical centerVertically2222222 = Alignment.INSTANCE.getCenterVertically();
        Arrangement.HorizontalOrVertical m561spacedBy0680j_42222222 = Arrangement.INSTANCE.m561spacedBy0680j_4(Dp.m6300constructorimpl(f3222222));
        ComposerKt.sourceInformationMarkerStart(composer2, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
        MeasurePolicy rowMeasurePolicy2222222 = RowKt.rowMeasurePolicy(m561spacedBy0680j_42222222, centerVertically2222222, composer2, 54);
        ComposerKt.sourceInformationMarkerStart(composer2, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
        currentCompositeKeyHash3 = ComposablesKt.getCurrentCompositeKeyHash(composer2, 0);
        CompositionLocalMap currentCompositionLocalMap3222222 = composer2.getCurrentCompositionLocalMap();
        Modifier materializeModifier3222222 = ComposedModifierKt.materializeModifier(composer2, m685paddingqDBjuR0$default222222);
        Function0<ComposeUiNode> constructor3222222 = ComposeUiNode.INSTANCE.getConstructor();
        ComposerKt.sourceInformationMarkerStart(composer2, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
        if (!(composer2.getApplier() instanceof Applier)) {
        }
        composer2.startReusableNode();
        if (composer2.getInserting()) {
        }
        m3333constructorimpl3 = Updater.m3333constructorimpl(composer2);
        Updater.m3340setimpl(m3333constructorimpl3, rowMeasurePolicy2222222, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
        Updater.m3340setimpl(m3333constructorimpl3, currentCompositionLocalMap3222222, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
        Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash3222222 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
        if (!m3333constructorimpl3.getInserting()) {
        }
        m3333constructorimpl3.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash3));
        m3333constructorimpl3.apply(Integer.valueOf(currentCompositeKeyHash3), setCompositeKeyHash3222222);
        Updater.m3340setimpl(m3333constructorimpl3, materializeModifier3222222, ComposeUiNode.INSTANCE.getSetModifier());
        ComposerKt.sourceInformationMarkerStart(composer2, -407840262, "C101@5126L9:Row.kt#2w3rfo");
        RowScopeInstance rowScopeInstance2222222 = RowScopeInstance.INSTANCE;
        TrayCluster(z2, onToggleQuick, composer2, ((i14222222 >> 6) & 14) | ((i14222222 >> 18) & 112));
        Clock(z3, onToggleNotif, composer2, ((i14222222 >> 9) & 14) | ((i14222222 >> 21) & 112));
        ComposerKt.sourceInformationMarkerEnd(composer2);
        composer2.endNode();
        ComposerKt.sourceInformationMarkerEnd(composer2);
        ComposerKt.sourceInformationMarkerEnd(composer2);
        ComposerKt.sourceInformationMarkerEnd(composer2);
        ComposerKt.sourceInformationMarkerEnd(composer2);
        composer2.endNode();
        ComposerKt.sourceInformationMarkerEnd(composer2);
        ComposerKt.sourceInformationMarkerEnd(composer2);
        ComposerKt.sourceInformationMarkerEnd(composer2);
        if (ComposerKt.isTraceInProgress()) {
        }
        endRestartGroup = composer2.endRestartGroup();
        if (endRestartGroup != null) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Taskbar$lambda$5$lambda$3$lambda$2$lambda$1$lambda$0(Function1 function1, AppEntry appEntry) {
        function1.invoke(appEntry.getPackageName());
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit Taskbar$lambda$6(List list, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, Function0 function0, Function0 function02, Function0 function03, Function0 function04, Function0 function05, Function1 function1, Modifier modifier, int i, int i2, int i3, Composer composer, int i4) {
        Taskbar(list, z, z2, z3, z4, z5, function0, function02, function03, function04, function05, function1, modifier, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1), RecomposeScopeImplKt.updateChangedFlags(i2), i3);
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x004b  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x02ae  */
    /* JADX WARN: Removed duplicated region for block: B:21:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0073  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x007a  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00c0  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00cc  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0147  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x01b0  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x01bc  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0236  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x02a5  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x01c0  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x014e  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x00d0  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x004e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static final void TaskbarButton(boolean z, final Function0<Unit> function0, final Function2<? super Composer, ? super Integer, Unit> function2, Composer composer, final int i, final int i2) {
        boolean z2;
        int i3;
        int currentCompositeKeyHash;
        Composer m3333constructorimpl;
        int currentCompositeKeyHash2;
        Composer m3333constructorimpl2;
        ScopeUpdateScope endRestartGroup;
        Composer startRestartGroup = composer.startRestartGroup(1770023496);
        int i4 = i2 & 1;
        if (i4 != 0) {
            i3 = i | 6;
            z2 = z;
        } else if ((i & 6) == 0) {
            z2 = z;
            i3 = (startRestartGroup.changed(z2) ? 4 : 2) | i;
        } else {
            z2 = z;
            i3 = i;
        }
        if ((i2 & 2) != 0) {
            i3 |= 48;
        } else if ((i & 48) == 0) {
            i3 |= startRestartGroup.changedInstance(function0) ? 32 : 16;
            if ((i2 & 4) == 0) {
                i3 |= 384;
            } else if ((i & 384) == 0) {
                i3 |= startRestartGroup.changedInstance(function2) ? 256 : 128;
            }
            if ((i3 & 147) == 146 || !startRestartGroup.getSkipping()) {
                if (i4 != 0) {
                    z2 = false;
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(1770023496, i3, -1, "com.neversoft.launcher.ui.TaskbarButton (Taskbar.kt:145)");
                }
                Alignment center = Alignment.INSTANCE.getCenter();
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, 733328855, "CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
                Modifier.Companion companion = Modifier.INSTANCE;
                MeasurePolicy maybeCachedBoxMeasurePolicy = BoxKt.maybeCachedBoxMeasurePolicy(center, false);
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                currentCompositeKeyHash = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
                CompositionLocalMap currentCompositionLocalMap = startRestartGroup.getCurrentCompositionLocalMap();
                Modifier materializeModifier = ComposedModifierKt.materializeModifier(startRestartGroup, companion);
                Function0<ComposeUiNode> constructor = ComposeUiNode.INSTANCE.getConstructor();
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                if (!(startRestartGroup.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                startRestartGroup.startReusableNode();
                if (startRestartGroup.getInserting()) {
                    startRestartGroup.useNode();
                } else {
                    startRestartGroup.createNode(constructor);
                }
                m3333constructorimpl = Updater.m3333constructorimpl(startRestartGroup);
                Updater.m3340setimpl(m3333constructorimpl, maybeCachedBoxMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                Updater.m3340setimpl(m3333constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                if (!m3333constructorimpl.getInserting() || !Intrinsics.areEqual(m3333constructorimpl.rememberedValue(), Integer.valueOf(currentCompositeKeyHash))) {
                    m3333constructorimpl.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash));
                    m3333constructorimpl.apply(Integer.valueOf(currentCompositeKeyHash), setCompositeKeyHash);
                }
                Updater.m3340setimpl(m3333constructorimpl, materializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, -2146769399, "C73@3429L9:Box.kt#2w3rfo");
                BoxScopeInstance boxScopeInstance = BoxScopeInstance.INSTANCE;
                Modifier m269clickableXHw0xAI$default = ClickableKt.m269clickableXHw0xAI$default(BackgroundKt.m236backgroundbw27NRU$default(ClipKt.clip(SizeKt.m726size3ABfNKs(Modifier.INSTANCE, NsDim.INSTANCE.m6668getIconButtonD9Ej5fM()), RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(NsDim.INSTANCE.m6669getRadiusControlD9Ej5fM())), !z2 ? NsColor.INSTANCE.m6653getControlHover0d7_KjU() : Color.INSTANCE.m3875getTransparent0d7_KjU(), null, 2, null), false, null, null, function0, 7, null);
                Alignment center2 = Alignment.INSTANCE.getCenter();
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, 733328855, "CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
                MeasurePolicy maybeCachedBoxMeasurePolicy2 = BoxKt.maybeCachedBoxMeasurePolicy(center2, false);
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
                currentCompositeKeyHash2 = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
                CompositionLocalMap currentCompositionLocalMap2 = startRestartGroup.getCurrentCompositionLocalMap();
                Modifier materializeModifier2 = ComposedModifierKt.materializeModifier(startRestartGroup, m269clickableXHw0xAI$default);
                Function0<ComposeUiNode> constructor2 = ComposeUiNode.INSTANCE.getConstructor();
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
                if (!(startRestartGroup.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                startRestartGroup.startReusableNode();
                if (startRestartGroup.getInserting()) {
                    startRestartGroup.useNode();
                } else {
                    startRestartGroup.createNode(constructor2);
                }
                m3333constructorimpl2 = Updater.m3333constructorimpl(startRestartGroup);
                Updater.m3340setimpl(m3333constructorimpl2, maybeCachedBoxMeasurePolicy2, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                Updater.m3340setimpl(m3333constructorimpl2, currentCompositionLocalMap2, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash2 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
                if (!m3333constructorimpl2.getInserting() || !Intrinsics.areEqual(m3333constructorimpl2.rememberedValue(), Integer.valueOf(currentCompositeKeyHash2))) {
                    m3333constructorimpl2.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash2));
                    m3333constructorimpl2.apply(Integer.valueOf(currentCompositeKeyHash2), setCompositeKeyHash2);
                }
                Updater.m3340setimpl(m3333constructorimpl2, materializeModifier2, ComposeUiNode.INSTANCE.getSetModifier());
                ComposerKt.sourceInformationMarkerStart(startRestartGroup, -2146769399, "C73@3429L9:Box.kt#2w3rfo");
                BoxScopeInstance boxScopeInstance2 = BoxScopeInstance.INSTANCE;
                function2.invoke(startRestartGroup, Integer.valueOf((i3 >> 6) & 14));
                ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
                startRestartGroup.endNode();
                ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
                ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
                ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
                startRestartGroup.startReplaceGroup(-478607797);
                if (z2) {
                    float f = 2;
                    BoxKt.Box(BackgroundKt.m236backgroundbw27NRU$default(ClipKt.clip(SizeKt.m712height3ABfNKs(SizeKt.m731width3ABfNKs(PaddingKt.m685paddingqDBjuR0$default(boxScopeInstance.align(Modifier.INSTANCE, Alignment.INSTANCE.getBottomCenter()), 0.0f, 0.0f, 0.0f, Dp.m6300constructorimpl(f), 7, null), Dp.m6300constructorimpl(16)), Dp.m6300constructorimpl(3)), RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(Dp.m6300constructorimpl(f))), LauncherState.INSTANCE.m6621getAccent0d7_KjU(), null, 2, null), startRestartGroup, 0);
                }
                startRestartGroup.endReplaceGroup();
                ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
                startRestartGroup.endNode();
                ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
                ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
                ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            } else {
                startRestartGroup.skipToGroupEnd();
            }
            endRestartGroup = startRestartGroup.endRestartGroup();
            if (endRestartGroup == null) {
                final boolean z3 = z2;
                endRestartGroup.updateScope(new Function2() { // from class: com.neversoft.launcher.ui.TaskbarKt$$ExternalSyntheticLambda0
                    @Override // kotlin.jvm.functions.Function2
                    public final Object invoke(Object obj, Object obj2) {
                        Unit TaskbarButton$lambda$9;
                        TaskbarButton$lambda$9 = TaskbarKt.TaskbarButton$lambda$9(z3, function0, function2, i, i2, (Composer) obj, ((Integer) obj2).intValue());
                        return TaskbarButton$lambda$9;
                    }
                });
                return;
            }
            return;
        }
        if ((i2 & 4) == 0) {
        }
        if ((i3 & 147) == 146) {
        }
        if (i4 != 0) {
        }
        if (ComposerKt.isTraceInProgress()) {
        }
        Alignment center3 = Alignment.INSTANCE.getCenter();
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, 733328855, "CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
        Modifier.Companion companion2 = Modifier.INSTANCE;
        MeasurePolicy maybeCachedBoxMeasurePolicy3 = BoxKt.maybeCachedBoxMeasurePolicy(center3, false);
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
        currentCompositeKeyHash = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
        CompositionLocalMap currentCompositionLocalMap3 = startRestartGroup.getCurrentCompositionLocalMap();
        Modifier materializeModifier3 = ComposedModifierKt.materializeModifier(startRestartGroup, companion2);
        Function0<ComposeUiNode> constructor3 = ComposeUiNode.INSTANCE.getConstructor();
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
        if (!(startRestartGroup.getApplier() instanceof Applier)) {
        }
        startRestartGroup.startReusableNode();
        if (startRestartGroup.getInserting()) {
        }
        m3333constructorimpl = Updater.m3333constructorimpl(startRestartGroup);
        Updater.m3340setimpl(m3333constructorimpl, maybeCachedBoxMeasurePolicy3, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
        Updater.m3340setimpl(m3333constructorimpl, currentCompositionLocalMap3, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
        Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash3 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
        if (!m3333constructorimpl.getInserting()) {
        }
        m3333constructorimpl.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash));
        m3333constructorimpl.apply(Integer.valueOf(currentCompositeKeyHash), setCompositeKeyHash3);
        Updater.m3340setimpl(m3333constructorimpl, materializeModifier3, ComposeUiNode.INSTANCE.getSetModifier());
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -2146769399, "C73@3429L9:Box.kt#2w3rfo");
        BoxScopeInstance boxScopeInstance3 = BoxScopeInstance.INSTANCE;
        Modifier m269clickableXHw0xAI$default2 = ClickableKt.m269clickableXHw0xAI$default(BackgroundKt.m236backgroundbw27NRU$default(ClipKt.clip(SizeKt.m726size3ABfNKs(Modifier.INSTANCE, NsDim.INSTANCE.m6668getIconButtonD9Ej5fM()), RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(NsDim.INSTANCE.m6669getRadiusControlD9Ej5fM())), !z2 ? NsColor.INSTANCE.m6653getControlHover0d7_KjU() : Color.INSTANCE.m3875getTransparent0d7_KjU(), null, 2, null), false, null, null, function0, 7, null);
        Alignment center22 = Alignment.INSTANCE.getCenter();
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, 733328855, "CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
        MeasurePolicy maybeCachedBoxMeasurePolicy22 = BoxKt.maybeCachedBoxMeasurePolicy(center22, false);
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
        currentCompositeKeyHash2 = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
        CompositionLocalMap currentCompositionLocalMap22 = startRestartGroup.getCurrentCompositionLocalMap();
        Modifier materializeModifier22 = ComposedModifierKt.materializeModifier(startRestartGroup, m269clickableXHw0xAI$default2);
        Function0<ComposeUiNode> constructor22 = ComposeUiNode.INSTANCE.getConstructor();
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
        if (!(startRestartGroup.getApplier() instanceof Applier)) {
        }
        startRestartGroup.startReusableNode();
        if (startRestartGroup.getInserting()) {
        }
        m3333constructorimpl2 = Updater.m3333constructorimpl(startRestartGroup);
        Updater.m3340setimpl(m3333constructorimpl2, maybeCachedBoxMeasurePolicy22, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
        Updater.m3340setimpl(m3333constructorimpl2, currentCompositionLocalMap22, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
        Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash22 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
        if (!m3333constructorimpl2.getInserting()) {
        }
        m3333constructorimpl2.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash2));
        m3333constructorimpl2.apply(Integer.valueOf(currentCompositeKeyHash2), setCompositeKeyHash22);
        Updater.m3340setimpl(m3333constructorimpl2, materializeModifier22, ComposeUiNode.INSTANCE.getSetModifier());
        ComposerKt.sourceInformationMarkerStart(startRestartGroup, -2146769399, "C73@3429L9:Box.kt#2w3rfo");
        BoxScopeInstance boxScopeInstance22 = BoxScopeInstance.INSTANCE;
        function2.invoke(startRestartGroup, Integer.valueOf((i3 >> 6) & 14));
        ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
        startRestartGroup.endNode();
        ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
        ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
        ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
        startRestartGroup.startReplaceGroup(-478607797);
        if (z2) {
        }
        startRestartGroup.endReplaceGroup();
        ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
        startRestartGroup.endNode();
        ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
        ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
        ComposerKt.sourceInformationMarkerEnd(startRestartGroup);
        if (ComposerKt.isTraceInProgress()) {
        }
        endRestartGroup = startRestartGroup.endRestartGroup();
        if (endRestartGroup == null) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit TaskbarButton$lambda$9(boolean z, Function0 function0, Function2 function2, int i, int i2, Composer composer, int i3) {
        TaskbarButton(z, function0, function2, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1), i2);
        return Unit.INSTANCE;
    }

    private static final void TrayCluster(final boolean z, final Function0<Unit> function0, Composer composer, final int i) {
        int i2;
        Composer composer2;
        Composer startRestartGroup = composer.startRestartGroup(1028659203);
        if ((i & 6) == 0) {
            i2 = (startRestartGroup.changed(z) ? 4 : 2) | i;
        } else {
            i2 = i;
        }
        if ((i & 48) == 0) {
            i2 |= startRestartGroup.changedInstance(function0) ? 32 : 16;
        }
        if ((i2 & 19) == 18 && startRestartGroup.getSkipping()) {
            startRestartGroup.skipToGroupEnd();
            composer2 = startRestartGroup;
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1028659203, i2, -1, "com.neversoft.launcher.ui.TrayCluster (Taskbar.kt:171)");
            }
            Modifier m682paddingVpY3zN4 = PaddingKt.m682paddingVpY3zN4(ClickableKt.m269clickableXHw0xAI$default(BackgroundKt.m236backgroundbw27NRU$default(ClipKt.clip(Modifier.INSTANCE, RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(NsDim.INSTANCE.m6669getRadiusControlD9Ej5fM())), z ? NsColor.INSTANCE.m6653getControlHover0d7_KjU() : Color.INSTANCE.m3875getTransparent0d7_KjU(), null, 2, null), false, null, null, function0, 7, null), Dp.m6300constructorimpl(8), Dp.m6300constructorimpl(6));
            Arrangement.HorizontalOrVertical m561spacedBy0680j_4 = Arrangement.INSTANCE.m561spacedBy0680j_4(Dp.m6300constructorimpl(7));
            Alignment.Vertical centerVertically = Alignment.INSTANCE.getCenterVertically();
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
            MeasurePolicy rowMeasurePolicy = RowKt.rowMeasurePolicy(m561spacedBy0680j_4, centerVertically, startRestartGroup, 54);
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            int currentCompositeKeyHash = ComposablesKt.getCurrentCompositeKeyHash(startRestartGroup, 0);
            CompositionLocalMap currentCompositionLocalMap = startRestartGroup.getCurrentCompositionLocalMap();
            Modifier materializeModifier = ComposedModifierKt.materializeModifier(startRestartGroup, m682paddingVpY3zN4);
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
            Updater.m3340setimpl(m3333constructorimpl, rowMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m3340setimpl(m3333constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
            if (m3333constructorimpl.getInserting() || !Intrinsics.areEqual(m3333constructorimpl.rememberedValue(), Integer.valueOf(currentCompositeKeyHash))) {
                m3333constructorimpl.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash));
                m3333constructorimpl.apply(Integer.valueOf(currentCompositeKeyHash), setCompositeKeyHash);
            }
            Updater.m3340setimpl(m3333constructorimpl, materializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(startRestartGroup, -407840262, "C101@5126L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance = RowScopeInstance.INSTANCE;
            float f = 16;
            composer2 = startRestartGroup;
            IconKt.m1830Iconww6aTOc(WifiKt.getWifi(Icons.Filled.INSTANCE), "Network", SizeKt.m726size3ABfNKs(Modifier.INSTANCE, Dp.m6300constructorimpl(f)), NsColor.INSTANCE.m6664getText0d7_KjU(), startRestartGroup, 3504, 0);
            IconKt.m1830Iconww6aTOc(VolumeUpKt.getVolumeUp(Icons.Filled.INSTANCE), "Volume", SizeKt.m726size3ABfNKs(Modifier.INSTANCE, Dp.m6300constructorimpl(f)), NsColor.INSTANCE.m6664getText0d7_KjU(), startRestartGroup, 3504, 0);
            IconKt.m1830Iconww6aTOc(BatteryFullKt.getBatteryFull(Icons.Filled.INSTANCE), "Battery", SizeKt.m726size3ABfNKs(Modifier.INSTANCE, Dp.m6300constructorimpl(f)), NsColor.INSTANCE.m6664getText0d7_KjU(), startRestartGroup, 3504, 0);
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
            endRestartGroup.updateScope(new Function2() { // from class: com.neversoft.launcher.ui.TaskbarKt$$ExternalSyntheticLambda1
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    Unit TrayCluster$lambda$11;
                    TrayCluster$lambda$11 = TaskbarKt.TrayCluster$lambda$11(z, function0, i, (Composer) obj, ((Integer) obj2).intValue());
                    return TrayCluster$lambda$11;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit TrayCluster$lambda$11(boolean z, Function0 function0, int i, Composer composer, int i2) {
        TrayCluster(z, function0, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }
}
