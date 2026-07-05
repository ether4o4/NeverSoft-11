package com.neversoft.launcher.ui.modifier;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.AndroidRenderEffect_androidKt;
import androidx.compose.ui.graphics.GraphicsLayerModifierKt;
import androidx.compose.ui.graphics.GraphicsLayerScope;
import androidx.compose.ui.unit.Dp;
import com.neversoft.launcher.ui.theme.NsColor;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Acrylic.kt */
@Metadata(d1 = {"\u0000\u001c\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\u001a/\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\t\u001a\u0019\u0010\n\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u000b\u0010\f¨\u0006\r"}, d2 = {"acrylic", "Landroidx/compose/ui/Modifier;", "tint", "Landroidx/compose/ui/graphics/Color;", "radius", "Landroidx/compose/ui/unit/Dp;", "stroke", "", "acrylic-jG9AyaU", "(Landroidx/compose/ui/Modifier;JFZ)Landroidx/compose/ui/Modifier;", "blurLayer", "blurLayer-3ABfNKs", "(Landroidx/compose/ui/Modifier;F)Landroidx/compose/ui/Modifier;", "app_release"}, k = 2, mv = {2, 0, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class AcrylicKt {
    /* renamed from: acrylic-jG9AyaU, reason: not valid java name */
    public static final Modifier m6643acrylicjG9AyaU(Modifier acrylic, long j, float f, boolean z) {
        Intrinsics.checkNotNullParameter(acrylic, "$this$acrylic");
        Modifier m236backgroundbw27NRU$default = BackgroundKt.m236backgroundbw27NRU$default(ClipKt.clip(acrylic, RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(f)), j, null, 2, null);
        return z ? BorderKt.m247borderxT4_qwU(m236backgroundbw27NRU$default, Dp.m6300constructorimpl(1), NsColor.INSTANCE.m6660getStroke0d7_KjU(), RoundedCornerShapeKt.m964RoundedCornerShape0680j_4(f)) : m236backgroundbw27NRU$default;
    }

    /* renamed from: acrylic-jG9AyaU$default, reason: not valid java name */
    public static /* synthetic */ Modifier m6644acrylicjG9AyaU$default(Modifier modifier, long j, float f, boolean z, int i, Object obj) {
        if ((i & 1) != 0) {
            j = NsColor.INSTANCE.m6649getAcrylicFlyout0d7_KjU();
        }
        if ((i & 2) != 0) {
            f = Dp.m6300constructorimpl(8);
        }
        if ((i & 4) != 0) {
            z = true;
        }
        return m6643acrylicjG9AyaU(modifier, j, f, z);
    }

    /* renamed from: blurLayer-3ABfNKs, reason: not valid java name */
    public static final Modifier m6645blurLayer3ABfNKs(Modifier blurLayer, final float f) {
        Intrinsics.checkNotNullParameter(blurLayer, "$this$blurLayer");
        return (Build.VERSION.SDK_INT < 31 || f <= 0.0f) ? blurLayer : GraphicsLayerModifierKt.graphicsLayer(blurLayer, new Function1() { // from class: com.neversoft.launcher.ui.modifier.AcrylicKt$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                Unit blurLayer_3ABfNKs$lambda$0;
                blurLayer_3ABfNKs$lambda$0 = AcrylicKt.blurLayer_3ABfNKs$lambda$0(f, (GraphicsLayerScope) obj);
                return blurLayer_3ABfNKs$lambda$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit blurLayer_3ABfNKs$lambda$0(float f, GraphicsLayerScope graphicsLayer) {
        Intrinsics.checkNotNullParameter(graphicsLayer, "$this$graphicsLayer");
        float f2 = graphicsLayer.mo373toPx0680j_4(f);
        RenderEffect createBlurEffect = RenderEffect.createBlurEffect(f2, f2, Shader.TileMode.CLAMP);
        Intrinsics.checkNotNullExpressionValue(createBlurEffect, "createBlurEffect(...)");
        graphicsLayer.setRenderEffect(AndroidRenderEffect_androidKt.asComposeRenderEffect(createBlurEffect));
        return Unit.INSTANCE;
    }
}
