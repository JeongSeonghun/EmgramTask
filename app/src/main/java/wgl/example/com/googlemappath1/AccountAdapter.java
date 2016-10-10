package wgl.example.com.googlemappath1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;


import java.util.Vector;

/**
 * Created by EMGRAM on 2016-10-07.
 */
public class AccountAdapter extends BaseAdapter{
    Context context;
    int layout;
    Vector al;
    LayoutInflater layoutInflater;

    public AccountAdapter(Context context, int layout, Vector al){
        this.context= context;
        this.layout= layout;
        this.al=al;
        this.layoutInflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
        if (view == null) {//해당하는 행에 레이아웃객체 없을 시
            //xml파일로 레이아웃 객체 생성
            view = layoutInflater.inflate(layout, null);
        }
        System.out.println("test002");
        TextView txt=(TextView)view.findViewById(R.id.accTxt);
        txt.setText((String)al.get(i));
        return view;
    }
}
