package wgl.example.com.emtask4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.zip.Inflater;

/**
 * Created by EMGRAM on 2016-09-20.
 */
public class testAdapter extends BaseAdapter {
    Context con;
    int layout;
    SebcH al;
    LayoutInflater inf;

   public testAdapter(Context con, int layout, SebcH al){
       this.con=con;
       this.layout=layout;
       this.al=al;
       inf=(LayoutInflater)con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
   }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inf.inflate(layout,null);
        }
        return null;
    }
}
