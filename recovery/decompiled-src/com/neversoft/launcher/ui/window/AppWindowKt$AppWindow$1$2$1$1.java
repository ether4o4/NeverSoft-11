package com.neversoft.launcher.ui.window;

import androidx.compose.foundation.gestures.DragGestureDetectorKt;
import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.MutableState;
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.input.pointer.PointerInputChange;
import androidx.compose.ui.input.pointer.PointerInputScope;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;

/* compiled from: AppWindow.kt */
@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "", "Landroidx/compose/ui/input/pointer/PointerInputScope;"}, k = 3, mv = {2, 0, 0}, xi = 48)
@DebugMetadata(c = "com.neversoft.launcher.ui.window.AppWindowKt$AppWindow$1$2$1$1", f = "AppWindow.kt", i = {}, l = {81}, m = "invokeSuspend", n = {}, s = {})
/* loaded from: classes2.dex */
final class AppWindowKt$AppWindow$1$2$1$1 extends SuspendLambda implements Function2<PointerInputScope, Continuation<? super Unit>, Object> {
    final /* synthetic */ MutableState<Boolean> $maximized$delegate;
    final /* synthetic */ MutableFloatState $offsetX$delegate;
    final /* synthetic */ MutableFloatState $offsetY$delegate;
    private /* synthetic */ Object L$0;
    int label;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    AppWindowKt$AppWindow$1$2$1$1(MutableState<Boolean> mutableState, MutableFloatState mutableFloatState, MutableFloatState mutableFloatState2, Continuation<? super AppWindowKt$AppWindow$1$2$1$1> continuation) {
        super(2, continuation);
        this.$maximized$delegate = mutableState;
        this.$offsetX$delegate = mutableFloatState;
        this.$offsetY$delegate = mutableFloatState2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit invokeSuspend$lambda$0(MutableFloatState mutableFloatState, MutableFloatState mutableFloatState2, PointerInputChange pointerInputChange, Offset offset) {
        float AppWindow$lambda$1;
        float AppWindow$lambda$4;
        AppWindow$lambda$1 = AppWindowKt.AppWindow$lambda$1(mutableFloatState);
        mutableFloatState.setFloatValue(AppWindow$lambda$1 + Offset.m3599getXimpl(offset.getPackedValue()));
        AppWindow$lambda$4 = AppWindowKt.AppWindow$lambda$4(mutableFloatState2);
        mutableFloatState2.setFloatValue(AppWindow$lambda$4 + Offset.m3600getYimpl(offset.getPackedValue()));
        return Unit.INSTANCE;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        AppWindowKt$AppWindow$1$2$1$1 appWindowKt$AppWindow$1$2$1$1 = new AppWindowKt$AppWindow$1$2$1$1(this.$maximized$delegate, this.$offsetX$delegate, this.$offsetY$delegate, continuation);
        appWindowKt$AppWindow$1$2$1$1.L$0 = obj;
        return appWindowKt$AppWindow$1$2$1$1;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(PointerInputScope pointerInputScope, Continuation<? super Unit> continuation) {
        return ((AppWindowKt$AppWindow$1$2$1$1) create(pointerInputScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object obj) {
        boolean AppWindow$lambda$7;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        if (i == 0) {
            ResultKt.throwOnFailure(obj);
            PointerInputScope pointerInputScope = (PointerInputScope) this.L$0;
            AppWindow$lambda$7 = AppWindowKt.AppWindow$lambda$7(this.$maximized$delegate);
            if (!AppWindow$lambda$7) {
                final MutableFloatState mutableFloatState = this.$offsetX$delegate;
                final MutableFloatState mutableFloatState2 = this.$offsetY$delegate;
                this.label = 1;
                if (DragGestureDetectorKt.detectDragGestures$default(pointerInputScope, null, null, null, new Function2() { // from class: com.neversoft.launcher.ui.window.AppWindowKt$AppWindow$1$2$1$1$$ExternalSyntheticLambda0
                    @Override // kotlin.jvm.functions.Function2
                    public final Object invoke(Object obj2, Object obj3) {
                        Unit invokeSuspend$lambda$0;
                        invokeSuspend$lambda$0 = AppWindowKt$AppWindow$1$2$1$1.invokeSuspend$lambda$0(MutableFloatState.this, mutableFloatState2, (PointerInputChange) obj2, (Offset) obj3);
                        return invokeSuspend$lambda$0;
                    }
                }, this, 7, null) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            }
        } else {
            if (i != 1) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            ResultKt.throwOnFailure(obj);
        }
        return Unit.INSTANCE;
    }
}
