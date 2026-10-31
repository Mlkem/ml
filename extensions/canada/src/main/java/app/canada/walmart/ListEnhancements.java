package app.canada.walmart;

import android.app.AlertDialog;
import android.content.*;
import android.graphics.Typeface;
import android.text.*;
import android.text.style.*;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

public final class ListEnhancements {
    private static final int HISTORY_BIND=0x7e010010;
    private static final String HISTORY_MARK="\n↕ ";
    static Object field(Object object,String name)throws Exception {
        return ModelAccess.field(object,name);
    }
    static View id(View root,String name) {
        int n=root.getResources().getIdentifier(name,"id",root.getContext().getPackageName());return n==0?null:root.findViewById(n);
    }
    static boolean french(View view){return "fr".equals(view.getResources().getConfiguration().getLocales().get(0).getLanguage());}
    static int dp(View view,int value){return (int)(view.getResources().getDisplayMetrics().density*value+.5f);}
    static SharedPreferences history(Context c){return c.getSharedPreferences("morphe_canada_price_history_v1",Context.MODE_PRIVATE);}
    static String text(View v){return v instanceof TextView?((TextView)v).getText().toString().trim():"";}
    static String money(long cents,boolean fr){NumberFormat f=NumberFormat.getCurrencyInstance(fr?Locale.CANADA_FRENCH:Locale.CANADA);return f.format(cents/100.0);}
    static String scope(View tile) {
        for(View v=tile;v!=null;) {
            int id=v.getId();
            if(id!=View.NO_ID)try {
                if("checklist_scroll_view".equals(v.getResources().getResourceEntryName(id))) {
                    String name=text(id(v,"store_name")),address=text(id(v,"store_address"));
                    if(!address.isEmpty())return name+" · "+address;
                }
            }catch(Exception ignored){}
            v=v.getParent() instanceof View?(View)v.getParent():null;
        }
        return french(tile)?"Liste — magasin non identifié":"Shopping list — store not identified";
    }
    static String digest(String value)throws Exception {
        byte[] bytes=MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        StringBuilder s=new StringBuilder("p_");for(byte b:bytes)s.append(String.format(Locale.ROOT,"%02x",b&255));return s.toString();
    }
    public static void bindHistory(final View tile,Object detail) {
        final Object token=new Object();tile.setTag(HISTORY_BIND,token);
        View oldPrice=id(tile,"lists_item_detail_price");
        if(oldPrice instanceof TextView){TextView t=(TextView)oldPrice;String s=t.getText().toString();int i=s.indexOf(HISTORY_MARK);if(i>=0)t.setText(t.getText().subSequence(0,i));t.setOnLongClickListener(null);}
        try {
            final Object product=detail.getClass().getMethod("p").invoke(detail);
            Object info=field(product,"A");if(info==null)return;
            if(Boolean.TRUE.equals(field(product,"i")))return; // Price range: no exact unit price.
            final Long cents=PriceLedger.cents((Double)field(info,"f"));if(cents==null)return;
            final String identity=String.valueOf(field(product,"n"));if(identity.isEmpty()||identity.equals("null"))return;
            final String seller=String.valueOf(field(product,"t"));
            final String offer=String.valueOf(field(product,"p"));
            final String salesUnit=String.valueOf(field(product,"v"));
            final String name=String.valueOf(field(product,"o"));
            tile.post(() -> {
                if(tile.getTag(HISTORY_BIND)!=token)return;
                try {
                    View price=id(tile,"lists_item_detail_price");
                    if(!(price instanceof TextView)||price.getVisibility()!=View.VISIBLE||text(price).isEmpty())return;
                    final String context=scope(tile);
                    final String key=digest(identity+"\n"+offer+"\n"+seller+"\n"+salesUnit+"\n"+context);
                    final SharedPreferences prefs=history(tile.getContext());
                    String old=prefs.getString(key,"");
                    List<PriceLedger.Point> points=PriceLedger.observe(PriceLedger.decode(old),System.currentTimeMillis(),cents);
                    String updated=PriceLedger.encode(points);
                    if(!old.equals(updated)) {
                        SharedPreferences.Editor edit=prefs.edit().putString(key,updated);
                        if(old.isEmpty())prune(prefs,edit,key);
                        edit.apply();
                    }
                    boolean fr=french(tile);Long previous=PriceLedger.previousDifferent(points);
                    String label=fr?"Historique : appui long":"History: hold price";
                    if(previous!=null) {
                        long delta=cents-previous;
                        label=(delta<0?"↓ ":"↑ ")+money(Math.abs(delta),fr)+(fr?" depuis le prix précédent":" vs previous price");
                    }
                    TextView t=(TextView)price;
                    CharSequence base=t.getText();int prior=base.toString().indexOf(HISTORY_MARK);if(prior>=0)base=base.subSequence(0,prior);
                    SpannableStringBuilder s=new SpannableStringBuilder(base);int start=s.length();s.append(HISTORY_MARK).append(label);
                    s.setSpan(new RelativeSizeSpan(.68f),start,s.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    s.setSpan(new ForegroundColorSpan(previous!=null&&cents<previous?0xff176338:0xff40566d),start,s.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    t.setMaxLines(Math.max(4,t.getMaxLines()));t.setText(s);
                    t.setOnLongClickListener(v->{showHistory(tile,key,name,context,seller);return true;});
                }catch(Exception e){android.util.Log.w("WalmartCanadaMorphe","Price history unavailable",e);}
            });
        }catch(Exception e){android.util.Log.w("WalmartCanadaMorphe","Price not recordable",e);}
    }
    private static void prune(SharedPreferences prefs,SharedPreferences.Editor edit,String keep) {
        Map<String,?> all=prefs.getAll();if(all.size()<128)return;
        List<String> keys=new ArrayList<>(all.keySet());
        keys.sort(Comparator.comparingLong(k->{List<PriceLedger.Point> a=PriceLedger.decode(String.valueOf(all.get(k)));return a.isEmpty()?0L:a.get(a.size()-1).time;}));
        for(int i=0;i<=keys.size()-128;i++)if(!keys.get(i).equals(keep))edit.remove(keys.get(i));
    }
    static void showHistory(View tile,String key,String name,String context,String seller) {
        boolean fr=french(tile);List<PriceLedger.Point> rows=PriceLedger.decode(history(tile.getContext()).getString(key,""));
        StringBuilder body=new StringBuilder(context).append("\n").append(seller.equals("null")?"":seller).append("\n\n");
        body.append(fr?"Prix de liste observés, en CAD. Les modes de livraison peuvent différer.\n\n":"Observed list prices in CAD. Fulfilment options may differ.\n\n");
        DateFormat date=DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT);
        for(int i=rows.size()-1;i>=0;i--){PriceLedger.Point p=rows.get(i);body.append(date.format(new Date(p.time))).append("  ").append(money(p.cents,fr)).append('\n');}
        if(rows.isEmpty())body.append(fr?"Aucune observation enregistrée.":"No saved observations.");
        TextView content=new TextView(tile.getContext());content.setText(body);content.setTextSize(16);int pad=dp(tile,20);content.setPadding(pad,pad,pad,pad);content.setTextIsSelectable(true);
        ScrollView scroll=new ScrollView(tile.getContext());scroll.addView(content);
        new AlertDialog.Builder(tile.getContext()).setTitle(name).setView(scroll).setPositiveButton(fr?"Fermer":"Close",null)
          .setNeutralButton(fr?"Effacer":"Clear item",(d,w)->new AlertDialog.Builder(tile.getContext()).setMessage(fr?"Effacer cet historique local ?":"Clear this item's local history?").setNegativeButton(fr?"Annuler":"Cancel",null).setPositiveButton(fr?"Effacer":"Clear",(x,y)->{history(tile.getContext()).edit().remove(key).apply();View p=id(tile,"lists_item_detail_price");if(p instanceof TextView){TextView t=(TextView)p;String s=t.getText().toString();int at=s.indexOf(HISTORY_MARK);if(at>=0)t.setText(t.getText().subSequence(0,at));}}).show()).show();
    }
    public static void unitPrice(final View tile) {
        tile.post(() -> {
            try {
                Object raw=tile.getClass().getMethod("getListDetailPerEachPrice").invoke(tile);
                View view=id(tile,"lists_item_detail_per_each_price");
                if(!(view instanceof TextView)||!(raw instanceof String)||((String)raw).trim().isEmpty())return;
                TextView text=(TextView)view;text.setText((String)raw);text.setVisibility(View.VISIBLE);
                float sp=text.getTextSize()/text.getResources().getDisplayMetrics().scaledDensity;
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP,Math.max(16,sp));text.setTypeface(text.getTypeface(),Typeface.BOLD);
                text.setTextColor(0xff004b76);text.setMaxLines(3);
            }catch(Exception e){android.util.Log.w("WalmartCanadaMorphe","Unit price unavailable",e);}
        });
    }
}
