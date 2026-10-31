import com.android.tools.smali.dexlib2.*;
import com.android.tools.smali.dexlib2.dexbacked.*;
import com.android.tools.smali.dexlib2.iface.*;
import com.android.tools.smali.dexlib2.iface.instruction.*;
import com.android.tools.smali.dexlib2.iface.reference.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
public class VerifyDex {
 static final String TILE="Lcom/walmart/glass/lists/view/lists/ListDetailTileView;";
 static Map<String,ClassDef> classes=new HashMap<>();
 static Set<String> wanted=new HashSet<>(Arrays.asList(TILE,"LYk0/o;","LgU/b;","Lcom/walmart/glass/integration/root/RootActivity;","Lcom/walmart/glass/lists/domain/ListItem$ListDetailItem;","Lcom/walmart/glass/lists/domain/ListItemProduct;","LE50/r;","Lja1/i;","Landroidx/constraintlayout/widget/ConstraintLayout$LayoutParams;"));
 static void require(boolean b,String message){if(!b)throw new AssertionError(message);}
 static void field(String owner,String name,String type){
   for(Field f:classes.get(owner).getFields()) if(f.getName().equals(name) && f.getType().equals(type))return;
   throw new AssertionError("Missing reflection field "+owner+"->"+name+":"+type);
 }
 static void method(String owner,String name,String ret){
   for(Method m:classes.get(owner).getMethods())if(m.getName().equals(name)&&m.getReturnType().equals(ret))return;
   throw new AssertionError("Missing runtime method "+owner+"->"+name);
 }
 public static void main(String[]args)throws Exception{
   wanted.add("Lcom/walmart/glass/lists/domain/PriceInfo;");
   wanted.add("Lcom/walmart/glass/lists/view/lists/b;");
   wanted.add("Lcom/walmart/glass/lists/view/lists/d;");
   wanted.add("Landroidx/recyclerview/widget/RecyclerView$A;");
   wanted.addAll(Arrays.asList("Landroidx/recyclerview/widget/z;","Landroidx/recyclerview/widget/h;","LH50/I0;","Lcom/walmart/glass/lists/domain/ListItem$ChecklistItem;","Lcom/walmart/glass/lists/domain/ListItem$AisleHeader;","Lcom/walmart/glass/lists/domain/ProductLocation;","Lcom/walmart/glass/lists/domain/Aisle;","Lcom/walmart/glass/globalscanner/views/GlobalScannerActivity;","Lcom/walmart/glass/lists/view/lists/ListDealsBottomBanner;"));
   Map<String,Integer> calls=new TreeMap<>();
   wanted.add("Lcom/walmart/glass/lists/view/lists/ListsDetailRecyclerView$c;");
   wanted.add("Lcom/walmart/glass/lists/domain/PromotionMessage;");
   try(ZipFile z=new ZipFile(args[0])) {
     for(Enumeration<? extends ZipEntry> es=z.entries();es.hasMoreElements();) {
       ZipEntry e=es.nextElement();if(!e.getName().matches("classes[0-9]*\\.dex"))continue;
       DexBackedDexFile dex=DexBackedDexFile.fromInputStream(Opcodes.forApi(35),new BufferedInputStream(z.getInputStream(e)));
       for(ClassDef c:dex.getClasses()) {
         if(wanted.contains(c.getType())||c.getType().startsWith("Lapp/canada/walmart/")) {
           require(classes.put(c.getType(),c)==null,"Duplicate class: "+c.getType());
           if(wanted.contains(c.getType()))for(Method m:c.getMethods())if(m.getImplementation()!=null)
             for(Instruction ins:m.getImplementation().getInstructions())if(ins instanceof ReferenceInstruction) {
               Reference ref=((ReferenceInstruction)ins).getReference();
               if(ref instanceof MethodReference) {
                 MethodReference mr=(MethodReference)ref;
                 if(mr.getDefiningClass().startsWith("Lapp/canada/walmart/")) {
                   String key=mr.getName();calls.put(key,calls.getOrDefault(key,0)+1);
                 }
               }
             }
         }
       }
     }
   }
   require(classes.keySet().containsAll(wanted),"Missing expected original classes");
   String p="Lcom/walmart/glass/lists/domain/ListItemProduct;";
   field(p,"e","Lv50/e;");field(p,"f","Ljava/lang/String;");
   field(p,"A","Lcom/walmart/glass/lists/domain/PriceInfo;");
   field(p,"i","Ljava/lang/Boolean;");
   for(String n:Arrays.asList("n","o","p","t"))field(p,n,"Ljava/lang/String;");
   field(p,"v","Lv50/Y;");
   field("Lcom/walmart/glass/lists/domain/PriceInfo;","f","Ljava/lang/Double;");
   field("Lcom/walmart/glass/lists/domain/PriceInfo;","i","Ljava/lang/Double;");
   field(p,"h","Z");field(p,"z","Lcom/walmart/glass/lists/domain/ProductLocation;");
   field(p,"P","Ljava/util/List;");field(p,"Q","Ljava/util/List;");
   field("Lcom/walmart/glass/lists/domain/PromotionMessage;","h","Ljava/lang/String;");
   field("Lcom/walmart/glass/lists/domain/ProductLocation;","d","Ljava/lang/String;");
   field("Lcom/walmart/glass/lists/domain/ProductLocation;","e","Lcom/walmart/glass/lists/domain/Aisle;");
   field("Lcom/walmart/glass/lists/domain/Aisle;","d","Ljava/lang/String;");
   field("Lcom/walmart/glass/lists/domain/Aisle;","e","Ljava/lang/Integer;");
   method("Lcom/walmart/glass/lists/domain/ListItem$ListDetailItem;","t","Ljava/lang/Integer;");
   method("Lcom/walmart/glass/lists/domain/ListItem$ListDetailItem;","n","Ljava/lang/String;");
   method("Lcom/walmart/glass/lists/domain/ListItem$ChecklistItem;","f","Lcom/walmart/glass/lists/domain/ListItem$ListDetailItem;");
   method("Lcom/walmart/glass/lists/domain/ListItem$ChecklistItem;","g","Z");
   method("Lcom/walmart/glass/lists/domain/ListItem$AisleHeader;","<init>","V");
   method("Landroidx/recyclerview/widget/z;","w","Ljava/lang/Object;");
   method("Landroidx/recyclerview/widget/h;","w","Ljava/util/List;");
   method(TILE,"getListDetailPromoExpiryMessage","Ljava/lang/String;");
   method(TILE,"getListDetailPerEachPrice","Ljava/lang/String;");
   boolean hasItemView=false;
   for(Field f:classes.get("Landroidx/recyclerview/widget/RecyclerView$A;").getFields())if(f.getType().equals("Landroid/view/View;"))hasItemView=true;
   require(hasItemView,"Missing ViewHolder itemView");
   method("Lcom/walmart/glass/lists/domain/ListItem$ListDetailItem;","p",p);
   method(TILE,"getActionButton","LE50/r;");
   method(TILE,"getOnActionButtonClicked","Lkotlin/jvm/functions/Function1;");
   for(String n:Arrays.asList("e","f","g","h","i","j","k","l","m","s","t","u","v"))
     field("Landroidx/constraintlayout/widget/ConstraintLayout$LayoutParams;",n,"I");
   field("Landroidx/constraintlayout/widget/ConstraintLayout$LayoutParams;","F","F");
   for(String name:Arrays.asList("selectLists","stock","onSetActionButton","onTileBind","filterNav","defaultTab","bindHistory","unitPrice","install","beforeBind","afterBind","selected","sortAisles","installTotal","installScanner","bindExpiry","hidePromotionBanner","hidePromotionHolder"))
     require(calls.getOrDefault(name,0)>0,"Missing injected call "+name);
   require(classes.containsKey("Lapp/canada/walmart/CanadaHooks;"),"Missing extension");
   require(classes.containsKey("Lapp/canada/walmart/WalmartFindItemButton;"),"Missing relink extension");
   System.out.println("Runtime field/method targets present; no duplicate target classes.");
   System.out.println("Injected calls: "+calls);
 }
}
