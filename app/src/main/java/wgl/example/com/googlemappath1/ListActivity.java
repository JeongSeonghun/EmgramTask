package wgl.example.com.googlemappath1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    ListView list;
    String receive="";
    TextView legTotal;
    TextView stepTotal;
    TextView node;
    EditText searchstep;
    EditText searchleg;
    Button searchBt;

    DirectionsJSONParser parser;

    String pathCk_s="";

    boolean click_Ck=true;
    //LatLng searchVal_s, searchVal_e;
    HashMap<String,String> searchIn_s, searchIn_e;

    //Vector<Vector<Vector<LatLng>>> nodeVec= new Vector();
    //Vector<LatLng> nodes2;
    List<HashMap<String,String>> path= new ArrayList<>();
    List<HashMap<String,String>> path_search;
    NodeAdapter nodeAdapter;

    long now;
    Date date;
    SimpleDateFormat sim= new SimpleDateFormat("MM/dd HH:mm:ss.SSS");
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        list= (ListView)findViewById(R.id.list);
        legTotal= (TextView)findViewById(R.id.leg_total);
        stepTotal= (TextView)findViewById(R.id.step_total);
        node= (TextView)findViewById(R.id.node);
        searchstep= (EditText)findViewById(R.id.step_num);
        searchleg= (EditText)findViewById(R.id.leg_num);
        searchBt= (Button)findViewById(R.id.search);

        Intent intent= getIntent();

        receive= intent.getStringExtra("list");

        receiveNodes(receive);

        searchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int leg_i, step_i;
                now= System.currentTimeMillis();
                date= new Date(now);
                String timeLog=sim.format(date);
                String log="";

                log+=timeLog+":"+"search Click";
                saveLog(log);

                try{
                    if(searchleg.getText().toString().equals(""))
                        leg_i=0;
                    else
                        leg_i=Integer.valueOf(searchleg.getText().toString());

                    if(searchstep.getText().toString().equals(""))
                        step_i=0;
                    else
                        step_i=Integer.valueOf(searchstep.getText().toString());

                    if(pathCk_s.equals("OK")){
                        setList(leg_i,step_i);

                    }
                }catch(NumberFormatException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"조건을 확인해 주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void receiveNodes(String receive){

        try {
            JSONObject gDirectJo = new JSONObject(receive);

            pathCk_s=gDirectJo.getString("status");

            parser = new DirectionsJSONParser();

            path=parser.parse(gDirectJo);    //ndedVec(rout):[legs:[steps:[point:{LatLng},...],...],...]


        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(pathCk_s.equals("OK")){
            setList(0,0);
        }

    }

    //경로 node들 리스트뷰에 표시
    //public void setList(String receive, int leg_Num, int step_Num){
    public void setList(int leg_Num, int step_Num){
        //num: step 인덱스, 0=전체

        //nodes2= new Vector();
        path_search= new ArrayList<>();
        String legNum="";
        String stepNum="";

        int all_num=path.size();
        int leg_all=Integer.valueOf(path.get(all_num-1).get("leg"));
        if(path.size()<1){
            legNum+="0";
            stepNum+="0";
        }else{
            if(leg_all==1){
                legNum=path.get(all_num-1).get("leg");
                stepNum=path.get(all_num-1).get("step");
            }else{
                String leg_num="1";
                for(int i=0; i<path.size(); i++){
                    if(!leg_num.equals(path.get(i).get("leg"))){
                        if(path.get(i).get("leg").equals("1")){
                            legNum+=path.get(i).get("leg");
                            stepNum+=path.get(i).get("step");
                        }else{
                            legNum+="/"+path.get(i).get("leg");
                            stepNum+="/"+path.get(i).get("step");
                        }
                    }
                    if(i==path.size()-1){
                        legNum+="/"+path.get(i).get("leg");
                        stepNum+="/"+path.get(i).get("step");
                    }
                }
            }

        }
        legTotal.setText(legNum);
        stepTotal.setText(stepNum);

        if(leg_Num==0&&step_Num==0){
            for(int i=0; i<path.size(); i++){        //i번째 leg의 좌표Vector Vector<Vector<LatLng>>
                path_search.add(path.get(i));
                //Toast.makeText(getApplicationContext(),"전체 목록입니다.",Toast.LENGTH_SHORT).show();
            }

        }else if(leg_Num>0&&step_Num>=0){
            if(step_Num==0&&leg_Num<=Integer.valueOf(legNum)){
                for(int i=0; i<path.size(); i++){
                    if(Integer.valueOf(path.get(i).get("leg"))==leg_Num)
                    path_search.add(path.get(i));
                }

            }else if(leg_Num<=Integer.valueOf(legNum)&&
                    step_Num<=Integer.valueOf(stepNum)&&step_Num>0){
                for(int i=0; i<path.size(); i++){
                    if(Integer.valueOf(path.get(i).get("leg"))==leg_Num&&
                            Integer.valueOf(path.get(i).get("step"))==step_Num){
                        path_search.add(path.get(i));
                    }
                }
            }else{
                Toast.makeText(getApplicationContext(),"검색 조건을 확인해주세요.",Toast.LENGTH_SHORT).show();
            }
        }
        node.setText(String.valueOf(path_search.size()));
        //list.setAdapter(new NodeAdapter(getApplicationContext(), R.layout.info, nodes2));
        nodeAdapter= new NodeAdapter(getApplicationContext(), R.layout.info, path_search);
        list.setAdapter(nodeAdapter);

        now= System.currentTimeMillis();
        date= new Date(now);
        String timeLog=sim.format(date);
        String log="";

        log+=timeLog+":"+"list set";
        saveLog(log);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(click_Ck){
                    nodeAdapter.setClickNum(1);
                    nodeAdapter.setIndexCk(i);

                    nodeAdapter.notifyDataSetChanged();

                    //searchVal_s=nodes2.get(i);
                    searchIn_s=path_search.get(i);
                    click_Ck=false;

                    now= System.currentTimeMillis();
                    date= new Date(now);
                    String timeLog=sim.format(date);
                    String log="";

                    log+=timeLog+":"+"first list click";
                    saveLog(log);
                }else{
                    now= System.currentTimeMillis();
                    date= new Date(now);
                    String timeLog=sim.format(date);
                    String log="";

                    log+=timeLog+":"+"second list click";
                    saveLog(log);

                    view.setBackgroundColor(Color.GREEN);
                    //searchVal_e=nodes2.get(i);
                    searchIn_e=path_search.get(i);

                    showMainActivity(searchPath(searchIn_s, searchIn_e));

                }
            }
        });
    }

    public List<HashMap<String, String>> searchPath(HashMap<String,String> searchL_s,
                                                     HashMap<String,String> searchL_e){
        //Vector<Vector<Vector<LatLng>>> searchVec= new Vector();
        //boolean saveCk=false;
        //boolean stopCk=false;

        //LatLng start_l, stop_l;
        HashMap<String, String> start_l, stop_l;
        List<HashMap<String, String>> searchHash=new ArrayList<>();
        int start_i, stop_i;

        /*
        if(distaceAB(new LatLng(Double.valueOf(searchL_s.get("lat"))
                , Double.valueOf(searchL_s.get("lng")))
                , new LatLng(Double.valueOf(searchL_e.get("lat"))
                        , Double.valueOf(searchL_e.get("lng"))))){
                        */
        if(checkAB(searchL_s, searchL_e)){
            start_l=searchL_s;
            stop_l=searchL_e;
        }else{
            start_l=searchL_e;
            stop_l=searchL_s;
        }


        start_i=path.indexOf(start_l);
        stop_i=path.indexOf(stop_l);
        searchHash=path.subList(start_i,stop_i);
        searchHash.add(path.get(stop_i));   //두번째 선택지 같이 표시하기 위해서
        return searchHash;
    }

    public boolean checkAB(HashMap<String, String> startH, HashMap<String, String> stopH){
        int start;
        int stop;
        boolean check;

        start=Integer.valueOf(startH.get("index"));
        stop=Integer.valueOf(stopH.get("index"));


        if(start<stop){
            check=true;
        }else{
            check = false;
        }

        return check;
    }

   
    public void showMainActivity(List<HashMap<String, String>> list){
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        
        SearchNodes searchNods= new SearchNodes(list);
        intent.putExtra("path",searchNods);

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void saveLog(String data){
        if (!checkExternalStorage()) return;
        // 외부메모리를 사용하지 못하면 끝냄

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path += "/MyDir";
        try {
            File f = new File(path, "log_hashmap.txt"); // 경로, 파일명

            FileWriter write = new FileWriter(f, true);

            write.append(data+"\n");
            write.close();
            System.out.println("저장완료");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean checkExternalStorage() {
        String state;
        state = Environment.getExternalStorageState();

        // 외부메모리 상태
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // 읽기 쓰기 모두 가능
            Log.d("test0", "외부메모리 읽기 쓰기 모두 가능");
            System.out.println("test000: 외부메모리 읽기 쓰기 모두 가능");
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            //읽기전용
            Log.d("test0", "외부메모리 읽기만 가능");
            System.out.println("test000: 외부메모리 읽기만 가능");
            return false;
        } else {
            // 읽기쓰기 모두 안됨
            Log.d("test0", "외부메모리 읽기쓰기 모두 안됨 : "+ state);
            System.out.println("test000: 외부메모리 읽기쓰기 모두 안됨: "+state);

            return false;
        }
    }
}
