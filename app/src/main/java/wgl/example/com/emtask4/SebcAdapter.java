package wgl.example.com.emtask4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by EMGRAM on 2016-09-08.
 */
public class SebcAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<SebcH> al=new ArrayList<SebcH>();

    LayoutInflater inf;// xml에 정의된 자원(resource)들을 view로 반환

    public SebcAdapter(Context context, int layout, ArrayList<SebcH> al){
        this.context= context;
        this.layout=layout;
        this.al=al;
        this.inf= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return al.size();
    }

    @Override
    public Object getItem(int i) {
        return al.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {//해당하는 행에 레이아웃객체 없을 시
            //xml파일로 레이아웃 객체 생성
            view = inf.inflate(layout, null);
        }

        TextView cateTex1= (TextView) view.findViewById(R.id.cate1);//view레이아웃 객체내의 텍스트뷰 id 사용
        TextView cateTex2= (TextView) view.findViewById(R.id.cate2);
        TextView cateTex3= (TextView) view.findViewById(R.id.cate3);
        TextView nameTex= (TextView) view.findViewById(R.id.name);
        TextView index= (TextView) view.findViewById(R.id.index);
        //분류1, 분류2, 분류3, 유적지명, 인덱스

        cateTex1.setText(al.get(position).getCate1());
        cateTex2.setText(al.get(position).getCate2());
        cateTex3.setText(al.get(position).getCate3());
        nameTex.setText(al.get(position).getName());
        index.setText(String.valueOf(position+1));
        return view;
    }
}
