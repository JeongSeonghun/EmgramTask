package wgl.example.com.emtask1;
//http://mainia.tistory.com/1093 참고
//http://sunphiz.me/wp/archives/1688

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //private ArrayList<String> list; //대신에 String 배열로 사용
    private ArrayAdapter<String> adapter;
    ListView listv;
    String[] str={"OS 정보", "Memory 정보", "CPU 정보"};//리스트뷰 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listv= (ListView)findViewById(R.id.listView);
        //list= new ArrayList<String>();
        adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,str);//(환경정보, 레이아웃, 리스트)

        //리스트뷰 어댑터 연결
        listv.setAdapter(adapter);

        //리스트뷰 클릭 이벤트, Activity 전환
        listv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    Intent intent= new Intent(getApplicationContext(),OSInfoActivity.class);
                    startActivity(intent);
                }else if(i==1){
                    Intent intent= new Intent(getApplicationContext(),MemInfoActivity.class);
                    startActivity(intent);
                }else if(i==2){
                    Intent intent= new Intent(getApplicationContext(),CPUInfoActivity.class);
                    startActivity(intent);
                }

            }
        });

    }

}
