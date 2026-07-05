package com.neversoft.launcher.ui.window;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.BoxWithConstraintsScope;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.OffsetKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.CloseKt;
import androidx.compose.material.icons.filled.CropSquareKt;
import androidx.compose.material.icons.filled.RemoveKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.vector.ImageVector;
import androidx.compose.ui.input.pointer.PointerInputScope;
import androidx.compose.ui.input.pointer.SuspendingPointerInputFilterKt;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.text.TextLayoutResult;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontFamily;
import androidx.compose.ui.text.font.FontStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.text.style.TextDecoration;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.IntOffset;
import androidx.compose.ui.unit.IntOffsetKt;
import androidx.compose.ui.unit.TextUnitKt;
import com.neversoft.launcher.ui.theme.NsColor;
import com.neversoft.launcher.ui.theme.NsDim;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;

/* compiled from: AppWindow.kt */
@Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes2.dex */
final class AppWindowKt$AppWindow$1 implements Function3<BoxWithConstraintsScope, Composer, Integer, Unit> {
    final /* synthetic */ MutableState<Boolean> $collapsed$delegate;
    final /* synthetic */ Function2<Composer, Integer, Unit> $content;
    final /* synthetic */ MutableState<Boolean> $maximized$delegate;
    final /* synthetic */ MutableFloatState $offsetX$delegate;
    final /* synthetic */ MutableFloatState $offsetY$delegate;
    final /* synthetic */ Function0<Unit> $onClose;
    final /* synthetic */ String $title;

    /* JADX WARN: Multi-variable type inference failed */
    AppWindowKt$AppWindow$1(MutableState<Boolean> mutableState, MutableFloatState mutableFloatState, MutableFloatState mutableFloatState2, String str, Function0<Unit> function0, MutableState<Boolean> mutableState2, Function2<? super Composer, ? super Integer, Unit> function2) {
        this.$maximized$delegate = mutableState;
        this.$offsetX$delegate = mutableFloatState;
        this.$offsetY$delegate = mutableFloatState2;
        this.$title = str;
        this.$onClose = function0;
        this.$collapsed$delegate = mutableState2;
        this.$content = function2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final IntOffset invoke$lambda$1$lambda$0(MutableState mutableState, MutableFloatState mutableFloatState, MutableFloatState mutableFloatState2, Density offset) {
        boolean AppWindow$lambda$7;
        float AppWindow$lambda$1;
        float AppWindow$lambda$4;
        long IntOffset;
        Intrinsics.checkNotNullParameter(offset, "$this$offset");
        AppWindow$lambda$7 = AppWindowKt.AppWindow$lambda$7(mutableState);
        if (AppWindow$lambda$7) {
            IntOffset = IntOffsetKt.IntOffset(0, 0);
        } else {
            AppWindow$lambda$1 = AppWindowKt.AppWindow$lambda$1(mutableFloatState);
            int roundToInt = MathKt.roundToInt(AppWindow$lambda$1);
            AppWindow$lambda$4 = AppWindowKt.AppWindow$lambda$4(mutableFloatState2);
            IntOffset = IntOffsetKt.IntOffset(roundToInt, MathKt.roundToInt(AppWindow$lambda$4));
        }
        return IntOffset.m6419boximpl(IntOffset);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$9$lambda$7$lambda$4$lambda$3(MutableState mutableState) {
        boolean AppWindow$lambda$10;
        AppWindow$lambda$10 = AppWindowKt.AppWindow$lambda$10(mutableState);
        AppWindowKt.AppWindow$lambda$11(mutableState, !AppWindow$lambda$10);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invoke$lambda$9$lambda$7$lambda$6$lambda$5(MutableState mutableState) {
        boolean AppWindow$lambda$7;
        AppWindow$lambda$7 = AppWindowKt.AppWindow$lambda$7(mutableState);
        AppWindowKt.AppWindow$lambda$8(mutableState, !AppWindow$lambda$7);
        return Unit.INSTANCE;
    }

    @Override // kotlin.jvm.functions.Function3
    public /* bridge */ /* synthetic */ Unit invoke(BoxWithConstraintsScope boxWithConstraintsScope, Composer composer, Integer num) {
        invoke(boxWithConstraintsScope, composer, num.intValue());
        return Unit.INSTANCE;
    }

    public final void invoke(BoxWithConstraintsScope BoxWithConstraints, Composer composer, int i) {
        int i2;
        boolean AppWindow$lambda$7;
        boolean AppWindow$lambda$72;
        boolean AppWindow$lambda$73;
        final MutableState<Boolean> mutableState;
        boolean AppWindow$lambda$10;
        Intrinsics.checkNotNullParameter(BoxWithConstraints, "$this$BoxWithConstraints");
        if ((i & 6) == 0) {
            i2 = i | (composer.changed(BoxWithConstraints) ? 4 : 2);
        } else {
            i2 = i;
        }
        if ((i2 & 19) == 18 && composer.getSkipping()) {
            composer.skipToGroupEnd();
            return;
        }
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-573855337, i2, -1, "com.neversoft.launcher.ui.window.AppWindow.<anonymous> (AppWindow.kt:60)");
        }
        AppWindow$lambda$7 = AppWindowKt.AppWindow$lambda$7(this.$maximized$delegate);
        float m6300constructorimpl = AppWindow$lambda$7 ? Dp.m6300constructorimpl(BoxWithConstraints.mo590getMaxWidthD9Ej5fM() * 0.98f) : Dp.m6300constructorimpl(380);
        AppWindow$lambda$72 = AppWindowKt.AppWindow$lambda$7(this.$maximized$delegate);
        float m6300constructorimpl2 = AppWindow$lambda$72 ? Dp.m6300constructorimpl(BoxWithConstraints.mo589getMaxHeightD9Ej5fM() * 0.82f) : Dp.m6300constructorimpl(460);
        Modifier align = BoxWithConstraints.align(Modifier.INSTANCE, Alignment.INSTANCE.getCenter());
        composer.startReplaceGroup(1522090272);
        final MutableState<Boolean> mutableState2 = this.$maximized$delegate;
        final MutableFloatState mutableFloatState = this.$offsetX$delegate;
        final MutableFloatState mutableFloatState2 = this.$offsetY$delegate;
        Object rememberedValue = composer.rememberedValue();
        if (rememberedValue == Composer.INSTANCE.getEmpty()) {
            rememberedValue = new Function1() { // from class: com.neversoft.launcher.ui.window.AppWindowKt$AppWindow$1$$ExternalSyntheticLambda0
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    IntOffset invoke$lambda$1$lambda$0;
                    invoke$lambda$1$lambda$0 = AppWindowKt$AppWindow$1.invoke$lambda$1$lambda$0(MutableState.this, mutableFloatState, mutableFloatState2, (Density) obj);
                    return invoke$lambda$1$lambda$0;
                }
            };
            composer.updateRememberedValue(rememberedValue);
        }
        composer.endReplaceGroup();
        Modifier m247borderxT4_qwU = BorderKt.m247borderxT4_qwU(BackgroundKt.m236backgroundbw27NRU$default(ClipKt.clip(SizeKt.m731width3ABfNKs(OffsetKt.offset(align, (Function1) rememberedValue), m6300constructorimpl), RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(NsDim.INSTANCE.m6670getRadiusOverlayD9Ej5fM())), NsColor.INSTANCE.m6659getSolid0d7_KjU(), null, 2, null), Dp.m6300constructorimpl(1), NsColor.INSTANCE.m6661getStrokeStrong0d7_KjU(), RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(NsDim.INSTANCE.m6670getRadiusOverlayD9Ej5fM()));
        final MutableState<Boolean> mutableState3 = this.$maximized$delegate;
        MutableFloatState mutableFloatState3 = this.$offsetX$delegate;
        MutableFloatState mutableFloatState4 = this.$offsetY$delegate;
        String str = this.$title;
        Function0<Unit> function0 = this.$onClose;
        MutableState<Boolean> mutableState4 = this.$collapsed$delegate;
        Function2<Composer, Integer, Unit> function2 = this.$content;
        ComposerKt.sourceInformationMarkerStart(composer, -483455358, "CC(Column)P(2,3,1)86@4330L61,87@4396L133:Column.kt#2w3rfo");
        MeasurePolicy columnMeasurePolicy = ColumnKt.columnMeasurePolicy(Arrangement.INSTANCE.getTop(), Alignment.INSTANCE.getStart(), composer, 0);
        ComposerKt.sourceInformationMarkerStart(composer, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
        int currentCompositeKeyHash = ComposablesKt.getCurrentCompositeKeyHash(composer, 0);
        CompositionLocalMap currentCompositionLocalMap = composer.getCurrentCompositionLocalMap();
        Modifier materializeModifier = ComposedModifierKt.materializeModifier(composer, m247borderxT4_qwU);
        Function0<ComposeUiNode> constructor = ComposeUiNode.INSTANCE.getConstructor();
        ComposerKt.sourceInformationMarkerStart(composer, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
        if (!(composer.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
        }
        composer.startReusableNode();
        if (composer.getInserting()) {
            composer.createNode(constructor);
        } else {
            composer.useNode();
        }
        Composer m3333constructorimpl = Updater.m3333constructorimpl(composer);
        Updater.m3340setimpl(m3333constructorimpl, columnMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
        Updater.m3340setimpl(m3333constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
        Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
        if (m3333constructorimpl.getInserting() || !Intrinsics.areEqual(m3333constructorimpl.rememberedValue(), Integer.valueOf(currentCompositeKeyHash))) {
            m3333constructorimpl.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash));
            m3333constructorimpl.apply(Integer.valueOf(currentCompositeKeyHash), setCompositeKeyHash);
        }
        Updater.m3340setimpl(m3333constructorimpl, materializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
        ComposerKt.sourceInformationMarkerStart(composer, -384784025, "C88@4444L9:Column.kt#2w3rfo");
        ColumnScopeInstance columnScopeInstance = ColumnScopeInstance.INSTANCE;
        Modifier m236backgroundbw27NRU$default = BackgroundKt.m236backgroundbw27NRU$default(SizeKt.m712height3ABfNKs(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), Dp.m6300constructorimpl(34)), NsColor.INSTANCE.m6657getMica0d7_KjU(), null, 2, null);
        AppWindow$lambda$73 = AppWindowKt.AppWindow$lambda$7(mutableState3);
        Boolean valueOf = Boolean.valueOf(AppWindow$lambda$73);
        composer.startReplaceGroup(523177947);
        AppWindowKt$AppWindow$1$2$1$1 rememberedValue2 = composer.rememberedValue();
        if (rememberedValue2 == Composer.INSTANCE.getEmpty()) {
            rememberedValue2 = new AppWindowKt$AppWindow$1$2$1$1(mutableState3, mutableFloatState3, mutableFloatState4, null);
            composer.updateRememberedValue(rememberedValue2);
        }
        composer.endReplaceGroup();
        Modifier pointerInput = SuspendingPointerInputFilterKt.pointerInput(m236backgroundbw27NRU$default, valueOf, (Function2<? super PointerInputScope, ? super Continuation<? super Unit>, ? extends Object>) rememberedValue2);
        Alignment.Vertical centerVertically = Alignment.INSTANCE.getCenterVertically();
        ComposerKt.sourceInformationMarkerStart(composer, 693286680, "CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
        MeasurePolicy rowMeasurePolicy = RowKt.rowMeasurePolicy(Arrangement.INSTANCE.getStart(), centerVertically, composer, 48);
        ComposerKt.sourceInformationMarkerStart(composer, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
        int currentCompositeKeyHash2 = ComposablesKt.getCurrentCompositeKeyHash(composer, 0);
        CompositionLocalMap currentCompositionLocalMap2 = composer.getCurrentCompositionLocalMap();
        Modifier materializeModifier2 = ComposedModifierKt.materializeModifier(composer, pointerInput);
        Function0<ComposeUiNode> constructor2 = ComposeUiNode.INSTANCE.getConstructor();
        ComposerKt.sourceInformationMarkerStart(composer, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
        if (!(composer.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
        }
        composer.startReusableNode();
        if (composer.getInserting()) {
            composer.createNode(constructor2);
        } else {
            composer.useNode();
        }
        Composer m3333constructorimpl2 = Updater.m3333constructorimpl(composer);
        Updater.m3340setimpl(m3333constructorimpl2, rowMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
        Updater.m3340setimpl(m3333constructorimpl2, currentCompositionLocalMap2, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
        Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash2 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
        if (m3333constructorimpl2.getInserting() || !Intrinsics.areEqual(m3333constructorimpl2.rememberedValue(), Integer.valueOf(currentCompositeKeyHash2))) {
            m3333constructorimpl2.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash2));
            m3333constructorimpl2.apply(Integer.valueOf(currentCompositeKeyHash2), setCompositeKeyHash2);
        }
        Updater.m3340setimpl(m3333constructorimpl2, materializeModifier2, ComposeUiNode.INSTANCE.getSetModifier());
        ComposerKt.sourceInformationMarkerStart(composer, -407840262, "C101@5126L9:Row.kt#2w3rfo");
        RowScopeInstance rowScopeInstance = RowScopeInstance.INSTANCE;
        SpacerKt.Spacer(SizeKt.m731width3ABfNKs(Modifier.INSTANCE, Dp.m6300constructorimpl(12)), composer, 6);
        TextKt.m2373Text4IGK_g(str, (Modifier) null, NsColor.INSTANCE.m6665getTextSecondary0d7_KjU(), TextUnitKt.getSp(12), (FontStyle) null, (FontWeight) null, (FontFamily) null, 0L, (TextDecoration) null, (TextAlign) null, 0L, 0, false, 0, 0, (Function1<? super TextLayoutResult, Unit>) null, (TextStyle) null, composer, 3456, 0, 131058);
        SpacerKt.Spacer(RowScope.weight$default(rowScopeInstance, Modifier.INSTANCE, 1.0f, false, 2, null), composer, 0);
        ImageVector remove = RemoveKt.getRemove(Icons.Filled.INSTANCE);
        composer.startReplaceGroup(1099274235);
        Object rememberedValue3 = composer.rememberedValue();
        if (rememberedValue3 == Composer.INSTANCE.getEmpty()) {
            mutableState = mutableState4;
            rememberedValue3 = new Function0() { // from class: com.neversoft.launcher.ui.window.AppWindowKt$AppWindow$1$$ExternalSyntheticLambda1
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    Unit invoke$lambda$9$lambda$7$lambda$4$lambda$3;
                    invoke$lambda$9$lambda$7$lambda$4$lambda$3 = AppWindowKt$AppWindow$1.invoke$lambda$9$lambda$7$lambda$4$lambda$3(MutableState.this);
                    return invoke$lambda$9$lambda$7$lambda$4$lambda$3;
                }
            };
            composer.updateRememberedValue(rememberedValue3);
        } else {
            mutableState = mutableState4;
        }
        composer.endReplaceGroup();
        AppWindowKt.CaptionButton(remove, "Minimize", false, (Function0) rememberedValue3, composer, 3120, 4);
        ImageVector cropSquare = CropSquareKt.getCropSquare(Icons.Filled.INSTANCE);
        composer.startReplaceGroup(1099277243);
        Object rememberedValue4 = composer.rememberedValue();
        if (rememberedValue4 == Composer.INSTANCE.getEmpty()) {
            rememberedValue4 = new Function0() { // from class: com.neversoft.launcher.ui.window.AppWindowKt$AppWindow$1$$ExternalSyntheticLambda2
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    Unit invoke$lambda$9$lambda$7$lambda$6$lambda$5;
                    invoke$lambda$9$lambda$7$lambda$6$lambda$5 = AppWindowKt$AppWindow$1.invoke$lambda$9$lambda$7$lambda$6$lambda$5(MutableState.this);
                    return invoke$lambda$9$lambda$7$lambda$6$lambda$5;
                }
            };
            composer.updateRememberedValue(rememberedValue4);
        }
        composer.endReplaceGroup();
        AppWindowKt.CaptionButton(cropSquare, "Maximize", false, (Function0) rememberedValue4, composer, 3120, 4);
        AppWindowKt.CaptionButton(CloseKt.getClose(Icons.Filled.INSTANCE), "Close", true, function0, composer, 432, 0);
        ComposerKt.sourceInformationMarkerEnd(composer);
        composer.endNode();
        ComposerKt.sourceInformationMarkerEnd(composer);
        ComposerKt.sourceInformationMarkerEnd(composer);
        ComposerKt.sourceInformationMarkerEnd(composer);
        composer.startReplaceGroup(523204448);
        AppWindow$lambda$10 = AppWindowKt.AppWindow$lambda$10(mutableState);
        if (!AppWindow$lambda$10) {
            Modifier m712height3ABfNKs = SizeKt.m712height3ABfNKs(Modifier.INSTANCE, m6300constructorimpl2);
            ComposerKt.sourceInformationMarkerStart(composer, 733328855, "CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
            MeasurePolicy maybeCachedBoxMeasurePolicy = BoxKt.maybeCachedBoxMeasurePolicy(Alignment.INSTANCE.getTopStart(), false);
            ComposerKt.sourceInformationMarkerStart(composer, -1323940314, "CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            int currentCompositeKeyHash3 = ComposablesKt.getCurrentCompositeKeyHash(composer, 0);
            CompositionLocalMap currentCompositionLocalMap3 = composer.getCurrentCompositionLocalMap();
            Modifier materializeModifier3 = ComposedModifierKt.materializeModifier(composer, m712height3ABfNKs);
            Function0<ComposeUiNode> constructor3 = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -692256719, "CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor3);
            } else {
                composer.useNode();
            }
            Composer m3333constructorimpl3 = Updater.m3333constructorimpl(composer);
            Updater.m3340setimpl(m3333constructorimpl3, maybeCachedBoxMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m3340setimpl(m3333constructorimpl3, currentCompositionLocalMap3, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Function2<ComposeUiNode, Integer, Unit> setCompositeKeyHash3 = ComposeUiNode.INSTANCE.getSetCompositeKeyHash();
            if (m3333constructorimpl3.getInserting() || !Intrinsics.areEqual(m3333constructorimpl3.rememberedValue(), Integer.valueOf(currentCompositeKeyHash3))) {
                m3333constructorimpl3.updateRememberedValue(Integer.valueOf(currentCompositeKeyHash3));
                m3333constructorimpl3.apply(Integer.valueOf(currentCompositeKeyHash3), setCompositeKeyHash3);
            }
            Updater.m3340setimpl(m3333constructorimpl3, materializeModifier3, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, -2146769399, "C73@3429L9:Box.kt#2w3rfo");
            BoxScopeInstance boxScopeInstance = BoxScopeInstance.INSTANCE;
            function2.invoke(composer, 0);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
        }
        composer.endReplaceGroup();
        ComposerKt.sourceInformationMarkerEnd(composer);
        composer.endNode();
        ComposerKt.sourceInformationMarkerEnd(composer);
        ComposerKt.sourceInformationMarkerEnd(composer);
        ComposerKt.sourceInformationMarkerEnd(composer);
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
        }
    }
}
