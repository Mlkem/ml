import app.canada.walmart.AisleSort;
import app.canada.walmart.ModelAccess;
import com.walmart.glass.lists.domain.ListItem.*;
import java.util.*;
import java.lang.reflect.*;
public class FeatureTest {
  static int checks;
  static void check(boolean v){checks++;if(!v)throw new AssertionError("Feature check "+checks);}
  public static void main(String[] args)throws Exception {
    Object adapter=new com.walmart.glass.lists.view.lists.b();
    ChecklistItem a10=new ChecklistItem("A10"),a2=new ChecklistItem("A2"),unknown=new ChecklistItem(null),secondA2=new ChecklistItem("a02");
    List<Object> input=Collections.unmodifiableList(Arrays.asList(new AisleHeader("old",4),a10,unknown,a2,secondA2));
    List<?> result=AisleSort.sortAisles(adapter,input);
    check(result!=input && input.size()==5);
    check(result.size()==7);
    check(((AisleHeader)result.get(0)).name.equals("A2") && ((AisleHeader)result.get(0)).count==2);
    check(result.get(1)==a2 && result.get(2)==secondA2); // stable equal aisle order
    check(result.get(4)==a10 && result.get(6)==unknown);
    check(AisleSort.sortAisles(new Object(),input)==input);
    check(AisleSort.sortAisles(adapter,null)==null);
    List<?> allUnknown=Collections.singletonList(unknown);check(AisleSort.sortAisles(adapter,allUnknown)==allUnknown);
    List<?> otherRows=Arrays.asList(a2,"unrecognized row");check(AisleSort.sortAisles(adapter,otherRows)==otherRows);
    check(!a2.checked && !a10.checked); // no checkoff changes
    Method price=ModelAccess.class.getDeclaredMethod("price",Object.class);
    check(price.invoke(null,a2.f()).equals(800L));
    a2.f().p().A.i=null;check(price.invoke(null,a2.f()).equals(1000L));
    a2.f().p().v=com.walmart.glass.lists.domain.ListItemProduct.Unit.WEIGHT;check(price.invoke(null,a2.f())==null);
    a2.f().p().A.i=9.99;check(price.invoke(null,a2.f()).equals(999L));
    a2.f().p().h=true;check(price.invoke(null,a2.f())==null); // suppressed offer not exposed
    a2.f().p().h=false;a2.f().p().i=true;check(price.invoke(null,a2.f())==null);
    check(ModelAccess.expiry(a2.f()).isEmpty());
    a2.f().p().P=Collections.singletonList(new com.walmart.glass.lists.domain.ListItemProduct.Promo("Ends Nov 5"));
    check(ModelAccess.expiry(a2.f()).equals("Ends Nov 5"));
    a2.f().p().Q=Collections.singletonList(new com.walmart.glass.lists.domain.ListItemProduct.Promo("Fin le 6 nov."));
    check(ModelAccess.expiry(a2.f()).equals("Fin le 6 nov."));
    a2.f().p().Q=Collections.singletonList(new com.walmart.glass.lists.domain.ListItemProduct.Promo(null));
    check(ModelAccess.expiry(a2.f()).isEmpty()); // do not borrow a different promotion's date
    check(ModelAccess.expiry(unknown.f()).isEmpty()); // next card has no stale expiry
    System.out.println(checks+" aisle transformation and runtime-model subtotal checks passed");
  }
}
