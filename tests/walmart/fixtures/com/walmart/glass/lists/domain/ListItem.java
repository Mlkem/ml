package com.walmart.glass.lists.domain;
/** Minimal test doubles for the observed Canada 26.40 model contract. */
public class ListItem {
  public static class AisleHeader {public final String name;public final int count;public AisleHeader(String name,int count){this.name=name;this.count=count;}}
  public static class ListDetailItem {
    private final ListItemProduct product;
    public ListDetailItem(ListItemProduct p){product=p;}
    public ListItemProduct p(){return product;}
    public Integer t(){return 2;}
  }
  public static class ChecklistItem {
    private final ListDetailItem detail;public boolean checked;
    public ChecklistItem(String aisle){detail=new ListDetailItem(new ListItemProduct(aisle));}
    public ListDetailItem f(){return detail;}
    public boolean g(){return checked;}
  }
}
