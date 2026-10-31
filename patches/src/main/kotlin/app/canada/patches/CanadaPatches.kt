package app.canada.patches

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.patcher.patch.*
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

private const val H = "Lapp/canada/walmart/CanadaHooks;"
private const val F = "Lapp/canada/walmart/WalmartFindItemButton;"
private const val TILE = "Lcom/walmart/glass/lists/view/lists/ListDetailTileView;"
private const val L = "Lapp/canada/walmart/ListEnhancements;"
private const val S = "Lapp/canada/walmart/ShoppingMode;"
private const val C = "Lapp/canada/walmart/ChecklistTools;"
private val canada = Compatibility(name="Walmart Canada", packageName="ca.walmart.ecommerceapp",
    apkFileType=ApkFileType.XAPK, appIconColor=0x0071CE,
    targets=listOf(AppTarget(version="26.40", isExperimental=true)))

val defaultLists = bytecodePatch(name="Default Lists Tab — Canada",
    description="Open Lists when entering My Items. Walmart Canada 26.40.", default=true) {
    compatibleWith(canada); extendWith("extensions/canada.mpe")
    execute {
        val method=Fingerprint(definingClass="LYk0/o;",name="onViewCreated",
            parameters=listOf("Landroid/view/View;","Landroid/os/Bundle;"),returnType="V").method
        method.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_VOID}.map{it.index}.reversed().forEach {
            method.addInstructions(it,"invoke-static/range {p1 .. p1}, $H->selectLists(Landroid/view/View;)V")
        }
    }
}
val stock = bytecodePatch(name="Stock Availability — Canada",
    description="Show Walmart's supplied availability and message below list item names. Unknown stays unknown; purchase limits are never treated as inventory counts.", default=true) {
    compatibleWith(canada); extendWith("extensions/canada.mpe")
    execute {
        val method=Fingerprint(definingClass=TILE,name="setBuyMoreAndSaveView",
            parameters=listOf("Lcom/walmart/glass/lists/domain/ListItem\$ListDetailItem;"),returnType="V").method
        method.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_VOID}.map{it.index}.reversed().forEach {
            method.addInstructions(it,"invoke-static/range {p0 .. p1}, $H->stock(Landroid/view/View;Ljava/lang/Object;)V")
        }
    }
}
val findItem = bytecodePatch(name="Find Item for Linked Items — Canada",
    description="Restore Find item on linked shopping-list products so they can be linked to a different product.", default=true) {
    compatibleWith(canada); extendWith("extensions/canada.mpe")
    execute {
        val action=Fingerprint(definingClass=TILE,name="setActionButton",parameters=listOf("LE50/r;"),returnType="V").method
        action.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_VOID}.map{it.index}.reversed().forEach {
            action.addInstructions(it,"invoke-static/range {p0 .. p1}, $F->onSetActionButton(Landroid/view/View;Ljava/lang/Object;)V")
        }
        val listener=Fingerprint(definingClass=TILE,name="setOnActionButtonClicked",parameters=listOf("Lkotlin/jvm/functions/Function1;"),returnType="V").method
        listener.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_VOID}.map{it.index}.reversed().forEach {
            listener.addInstructions(it,"invoke-static/range {p0 .. p1}, $F->onTileBind(Landroid/view/View;Ljava/lang/Object;)V")
        }
    }
}
val removeShop = bytecodePatch(name="Remove Shop — Canada",
    description="Hide Shop from bottom navigation and redirect the default Shop tab to My Items. Explicit other tabs are preserved.", default=false) {
    compatibleWith(canada); extendWith("extensions/canada.mpe")
    execute {
        val nav=Fingerprint(definingClass="LgU/b;",name="e",parameters=emptyList(),returnType="Ljava/util/Map;").method
        nav.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_OBJECT}.map{it.index}.reversed().forEach {
            val r=(nav.instructions[it] as OneRegisterInstruction).registerA
            nav.addInstructions(it,"invoke-static/range {v$r .. v$r}, $H->filterNav(Ljava/util/Map;)Ljava/util/Map;\nmove-result-object v$r")
        }
        val start=Fingerprint(definingClass="Lcom/walmart/glass/integration/root/RootActivity;",name="A",
            parameters=listOf("Landroid/os/Bundle;","Ljava/lang/String;"),returnType="Lja1/i;").method
        start.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_OBJECT}.map{it.index}.reversed().forEach {
            val r=(start.instructions[it] as OneRegisterInstruction).registerA
            start.addInstructions(it,"invoke-static/range {v$r .. v$r}, $H->defaultTab(Ljava/lang/Object;)Ljava/lang/Object;\nmove-result-object v$r\ncheck-cast v$r, Lja1/i;")
        }
    }
}

val priceHistory = bytecodePatch(name="Personal Price History — Canada",
    description="Save observed shopping-list prices locally. Long-press a price for history. Identified stores and sellers have separate histories; starts recording after installation.", default=true) {
    compatibleWith(canada); extendWith("extensions/canada.mpe")
    execute {
        val method=Fingerprint(definingClass=TILE,name="setBuyMoreAndSaveView",
            parameters=listOf("Lcom/walmart/glass/lists/domain/ListItem\$ListDetailItem;"),returnType="V").method
        method.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_VOID}.map{it.index}.reversed().forEach {
            method.addInstructions(it,"invoke-static/range {p0 .. p1}, $L->bindHistory(Landroid/view/View;Ljava/lang/Object;)V")
        }
    }
}
val unitPrices = bytecodePatch(name="Clearer Unit Prices — Canada",
    description="Show Walmart's supplied price-per-unit in larger bold text on list items, including the shopping checklist. No guessed conversions.", default=true) {
    compatibleWith(canada); extendWith("extensions/canada.mpe")
    execute {
        val method=Fingerprint(definingClass=TILE,name="setListDetailPerEachPrice",
            parameters=listOf("Ljava/lang/String;"),returnType="V").method
        method.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_VOID}.map{it.index}.reversed().forEach {
            method.addInstructions(it,"invoke-static/range {p0 .. p0}, $L->unitPrice(Landroid/view/View;)V")
        }
    }
}
val shoppingMode = bytecodePatch(name="Shopping Mode — Canada",
    description="Checklist switches for keeping the screen awake, larger checkboxes and hiding collected rows. Show collected restores them without changing your list.", default=true) {
    compatibleWith(canada); extendWith("extensions/canada.mpe")
    execute {
        val screen=Fingerprint(definingClass="Lcom/walmart/glass/lists/view/lists/d;",name="onViewCreated",
            parameters=listOf("Landroid/view/View;","Landroid/os/Bundle;"),returnType="V").method
        screen.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_VOID}.map{it.index}.reversed().forEach {
            screen.addInstructions(it,"invoke-static/range {p1 .. p1}, $S->install(Landroid/view/View;)V")
        }
        for (adapter in listOf("Lcom/walmart/glass/lists/view/lists/b;", "LH50/I0;")) {
        val bind=Fingerprint(definingClass=adapter,name="onBindViewHolder",
            parameters=listOf("Landroidx/recyclerview/widget/RecyclerView\$A;","I"),returnType="V").method
        bind.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_VOID}.map{it.index}.reversed().forEach {
            bind.addInstructions(it,"invoke-static/range {p1 .. p1}, $S->afterBind(Ljava/lang/Object;)V")
        }
        bind.addInstructions(0,"invoke-static/range {p1 .. p1}, $S->beforeBind(Ljava/lang/Object;)V")
        }
        val select=Fingerprint(definingClass=TILE,name="setIsItemSelected",parameters=listOf("Z"),returnType="V").method
        select.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_VOID}.map{it.index}.reversed().forEach {
            select.addInstructions(it,"invoke-static/range {p0 .. p0}, $S->selected(Landroid/view/View;)V")
        }
    }
}

val aisleSort = bytecodePatch(name="Aisle-Sorted Checklist — Canada",
    description="Group remaining checklist items by supplied aisle, in natural order (A2 before A10). Unknown locations last. No floor-plan routing.", default=true) {
    compatibleWith(canada); extendWith("extensions/canada.mpe")
    execute {
        for ((name,params) in listOf("y" to listOf("Ljava/util/List;"), "z" to listOf("Ljava/util/List;","Ljava/lang/Runnable;"))) {
            val method=Fingerprint(definingClass="Landroidx/recyclerview/widget/z;",name=name,parameters=params,returnType="V").method
            method.addInstructions(0,"invoke-static/range {p0 .. p1}, Lapp/canada/walmart/AisleSort;->sortAisles(Ljava/lang/Object;Ljava/util/List;)Ljava/util/List;\nmove-result-object p1")
        }
    }
}
val runningTotal = bytecodePatch(name="Running List Total — Canada",
    description="Checklist subtotal, remaining and collected amounts from all loaded items, including offscreen rows. Estimated CAD before tax/fees; missing prices are counted and excluded.", default=true) {
    compatibleWith(canada); extendWith("extensions/canada.mpe")
    execute {
        val method=Fingerprint(definingClass="Lcom/walmart/glass/lists/view/lists/d;",name="onViewCreated",
            parameters=listOf("Landroid/view/View;","Landroid/os/Bundle;"),returnType="V").method
        method.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_VOID}.map{it.index}.reversed().forEach {
            method.addInstructions(it,"invoke-static/range {p1 .. p1}, $C->installTotal(Landroid/view/View;)V")
        }
    }
}
val quickScanner = bytecodePatch(name="Quick Scanner — Canada",
    description="Add a Scan barcode shortcut to the shopping checklist, opening Walmart's native scanner and permission flow.", default=true) {
    compatibleWith(canada); extendWith("extensions/canada.mpe")
    execute {
        val method=Fingerprint(definingClass="Lcom/walmart/glass/lists/view/lists/d;",name="onViewCreated",
            parameters=listOf("Landroid/view/View;","Landroid/os/Bundle;"),returnType="V").method
        method.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_VOID}.map{it.index}.reversed().forEach {
            method.addInstructions(it,"invoke-static/range {p1 .. p1}, $C->installScanner(Landroid/view/View;)V")
        }
    }
}
val promotionCleanup = bytecodePatch(name="Promotion Cleanup — Canada",
    description="Collapse the shopping-list deals promotional banner. Product discounts, expiry conditions, rewards, totals and checkout are preserved. Not an app-wide ad blocker.", default=true) {
    compatibleWith(canada); extendWith("extensions/canada.mpe")
    execute {
        // The active deals-list adapter binds its layout directly, without the custom-view setters.
        val bind=Fingerprint(definingClass="Lcom/walmart/glass/lists/view/lists/ListsDetailRecyclerView\$c;",name="p",
            parameters=listOf("Ljava/lang/Object;","I","Landroidx/recyclerview/widget/RecyclerView\$A;","Ljava/util/List;"),returnType="V").method
        bind.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_VOID}.map{it.index}.reversed().forEach {
            bind.addInstructions(it,"invoke-static/range {p3 .. p3}, $C->hidePromotionHolder(Ljava/lang/Object;)V")
        }
        for (name in listOf("setHeader","setCtaText")) {
            val method=Fingerprint(definingClass="Lcom/walmart/glass/lists/view/lists/ListDealsBottomBanner;",name=name,
                parameters=listOf("Ljava/lang/String;"),returnType="V").method
            method.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_VOID}.map{it.index}.reversed().forEach {
                method.addInstructions(it,"invoke-static/range {p0 .. p0}, $C->hidePromotionBanner(Landroid/view/View;)V")
            }
        }
    }
}
val saleReminders = bytecodePatch(name="Sale-Ending Reminders — Canada",
    description="Make Walmart's supplied promotion expiry text prominent on list items. In-list reminders only; no invented deadlines or background notifications.", default=true) {
    compatibleWith(canada); extendWith("extensions/canada.mpe")
    execute {
        val method=Fingerprint(definingClass=TILE,name="setBuyMoreAndSaveView",parameters=listOf("Lcom/walmart/glass/lists/domain/ListItem\$ListDetailItem;"),returnType="V").method
        method.instructions.withIndex().filter{it.value.opcode==Opcode.RETURN_VOID}.map{it.index}.reversed().forEach {
            method.addInstructions(it,"invoke-static/range {p0 .. p1}, $C->bindExpiry(Landroid/view/View;Ljava/lang/Object;)V")
        }
    }
}
