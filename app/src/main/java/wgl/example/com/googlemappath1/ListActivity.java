package wgl.example.com.googlemappath1;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

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
                }else{
                    view.setBackgroundColor(Color.GREEN);
                    //searchVal_e=nodes2.get(i);
                    searchIn_e=path_search.get(i);
                    showMainActivity(sendJSONString(searchPath(searchIn_s, searchIn_e)));
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

        if(distaceAB(new LatLng(Double.valueOf(searchL_s.get("lat"))
                , Double.valueOf(searchL_s.get("lng")))
                , new LatLng(Double.valueOf(searchL_e.get("lat"))
                        , Double.valueOf(searchL_e.get("lng"))))){
            start_l=searchL_s;
            stop_l=searchL_e;
        }else{
            start_l=searchL_e;
            stop_l=searchL_s;
        }


        start_i=path.indexOf(start_l);
        stop_i=path.indexOf(stop_l);
        searchHash=path.subList(start_i,stop_i);

        return searchHash;
    }

    public boolean distaceAB(LatLng l1, LatLng l2){
        float dis1, dis2;
        boolean flowCk;

        Location loc_s= new Location("point_s");
        loc_s.setLatitude(Double.valueOf(path.get(0).get("lat")));
        loc_s.setLongitude(Double.valueOf(path.get(0).get("lng")));

        Location loc_1= new Location("point_s");
        loc_1.setLatitude(l1.latitude);
        loc_1.setLongitude(l1.longitude);

        Location loc_2= new Location("point_s");
        loc_2.setLatitude(l2.latitude);
        loc_2.setLongitude(l2.longitude);

        dis1=loc_s.distanceTo(loc_1);
        dis2=loc_s.distanceTo(loc_2);

        if(dis1<dis2){
            flowCk=true;
        }else
            flowCk=false;

        return flowCk;
    }

    public void showMainActivity(String sendJSON){
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("list",sendJSON);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //public String sendJSONString(Vector<Vector<Vector<LatLng>>> searchPath){
    public String sendJSONString(List<HashMap<String,String>> searchPath){

        String sendString="{\"routes\":[";


            sendString+="{";

            sendString+="\"legs\":[";

                sendString+="{";

                sendString+="\"steps\":[";
                for(int i=0; i<searchPath.size();i++){//point
                    if(i==0) sendString+="{";
                    else sendString+=",{";
                    sendString+="\"lat\":\""+searchPath.get(i).get("lat")
                            +"\",\"lng\":\""+searchPath.get(i).get("lng")
                            +"\"}";
                }
                sendString+="]}";

            sendString+="]}";

        sendString+="]}";

        System.out.println("test005 : "+sendString);


        return sendString;
    }
}
