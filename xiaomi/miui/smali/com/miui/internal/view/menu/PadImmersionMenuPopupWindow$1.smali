.class Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow$1;
.super Ljava/lang/Object;
.source "SourceFile"

# interfaces
.implements Landroid/widget/AdapterView$OnItemClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow;-><init>(Lcom/miui/internal/app/ActionBarDelegateImpl;Landroid/view/Menu;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic Ps:Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow;


# direct methods
.method constructor <init>(Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow;)V
    .locals 0

    .prologue
    .line 35
    iput-object p1, p0, Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow$1;->Ps:Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onItemClick(Landroid/widget/AdapterView;Landroid/view/View;IJ)V
    .locals 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/widget/AdapterView",
            "<*>;",
            "Landroid/view/View;",
            "IJ)V"
        }
    .end annotation

    .prologue
    .line 38
    iget-object v0, p0, Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow$1;->Ps:Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow;

    invoke-static {v0}, Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow;->a(Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow;)Lcom/miui/internal/view/menu/ImmersionMenuAdapter;

    move-result-object v0

    invoke-virtual {v0, p3}, Lcom/miui/internal/view/menu/ImmersionMenuAdapter;->getItem(I)Landroid/view/MenuItem;

    move-result-object v0

    .line 39
    invoke-interface {v0}, Landroid/view/MenuItem;->hasSubMenu()Z

    move-result v1

    if-eqz v1, :cond_0

    .line 40
    invoke-interface {v0}, Landroid/view/MenuItem;->getSubMenu()Landroid/view/SubMenu;

    move-result-object v0

    .line 41
    iget-object v1, p0, Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow$1;->Ps:Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow;

    new-instance v2, Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow$1$1;

    invoke-direct {v2, p0, v0}, Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow$1$1;-><init>(Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow$1;Landroid/view/SubMenu;)V

    invoke-virtual {v1, v2}, Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow;->setOnDismissListener(Landroid/widget/PopupWindow$OnDismissListener;)V

    .line 52
    :goto_0
    iget-object v0, p0, Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow$1;->Ps:Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow;

    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow;->dismiss(Z)V

    .line 53
    return-void

    .line 50
    :cond_0
    iget-object v1, p0, Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow$1;->Ps:Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow;

    invoke-static {v1}, Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow;->d(Lcom/miui/internal/view/menu/PadImmersionMenuPopupWindow;)Lcom/miui/internal/app/ActionBarDelegateImpl;

    move-result-object v1

    const/4 v2, 0x0

    invoke-virtual {v1, v2, v0}, Lcom/miui/internal/app/ActionBarDelegateImpl;->onMenuItemSelected(ILandroid/view/MenuItem;)Z

    goto :goto_0
.end method
