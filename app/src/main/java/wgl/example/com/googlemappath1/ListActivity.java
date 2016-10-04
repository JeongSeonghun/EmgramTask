package wgl.example.com.googlemappath1;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
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

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    LatLng searchVal_s, searchVal_e;

    Vector<Vector<Vector<LatLng>>> nodeVec= new Vector();
    Vector<LatLng> nodes2;

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

                log+=timeLog+":"+"search click";
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
                }
            }
        });
    }

    public void receiveNodes(String receive){

        try {
            JSONObject gDirectJo = new JSONObject(receive);

            pathCk_s=gDirectJo.getString("status");

            parser = new DirectionsJSONParser();

            nodeVec=parser.parse(gDirectJo);    //ndedVec(rout):[legs:[steps:[point:{LatLng},...],...],...]


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

        nodes2= new Vector();
        String legNum="";
        String stepNum="";


        if(nodeVec.size()<1){
            legNum+="0";
            stepNum+="0";
        }else{
            for(int i=0; i<nodeVec.size(); i++){
                if(i==0){
                    legNum+=String.valueOf(i+1);
                    stepNum+=String.valueOf(nodeVec.get(i).size());
                }else{
                    legNum+="/"+String.valueOf(i+1);
                    stepNum+="/"+String.valueOf(nodeVec.get(i).size());
                }
            }
        }
        legTotal.setText(legNum);
        stepTotal.setText(stepNum);

        if(leg_Num==0&&step_Num==0){
            for(int i=0; i<nodeVec.size(); i++){        //i번째 leg의 좌표Vector Vector<Vector<LatLng>>
                for(int j=0; j<nodeVec.get(i).size(); j++){ //배열의 j번째 LatLng 객체 Vector<LatLng>
                    nodes2.addAll(nodeVec.get(i).get(j));
                }
            }

        }else if(leg_Num>0&&step_Num>=0){
            if(step_Num==0&&leg_Num<=Integer.valueOf(legNum)){
                for(int i=0; i<nodeVec.get(leg_Num-1).size(); i++){
                    nodes2.addAll(nodeVec.get(leg_Num-1).get(i));
                }

            }else if(leg_Num<=Integer.valueOf(legNum)&&
                    step_Num<=Integer.valueOf(stepNum)&&step_Num>0){
                nodes2.addAll(nodeVec.get(leg_Num-1).get(step_Num-1));
            }
        }
        node.setText(String.valueOf(nodes2.size()));
        //list.setAdapter(new NodeAdapter(getApplicationContext(), R.layout.info, nodes2));
        nodeAdapter= new NodeAdapter(getApplicationContext(), R.layout.info, nodes2);
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

                    searchVal_s=nodes2.get(i);
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
                    searchVal_e=nodes2.get(i);
                    showMainActivity(sendJSONString(searchPath(searchVal_s, searchVal_e)));
                }
            }
        });
    }

    public Vector<Vector<Vector<LatLng>>> searchPath(LatLng searchL_s, LatLng searchL_e){
        Vector<Vector<Vector<LatLng>>> searchVec= new Vector();
        boolean saveCk=false;
        boolean stopCk=false;

        LatLng start_l, stop_l;

        if(distaceAB(searchL_s, searchL_e)){
            start_l=searchL_s;
            stop_l=searchL_e;
        }else{
            start_l=searchL_e;
            stop_l=searchL_s;
        }

        for(int i=0; i<nodeVec.size(); i++){
            Vector<Vector<LatLng>> sLegs= new Vector();
            for(int j=0; j<nodeVec.get(i).size();j++){
                Vector<LatLng> sSteps= new Vector();
                for(int k=0; k<nodeVec.get(i).get(j).size();k++){
                    if(start_l.equals(nodeVec.get(i).get(j).get(k))){
                        saveCk=true;
                    }else if(stop_l.equals(nodeVec.get(i).get(j).get(k))){
                        saveCk=false;
                        stopCk=true;
                    }
                    if(saveCk){
                        sSteps.add(nodeVec.get(i).get(j).get(k));
                    }else if(stopCk){
                        sSteps.add(nodeVec.get(i).get(j).get(k));
                        break;
                    }
                }
                if(saveCk){
                    sLegs.add(sSteps);
                }else if(stopCk){
                    sLegs.add(sSteps);
                    break;
                }
            }
            if(saveCk) {
                searchVec.add(sLegs);
            }else if(stopCk){
                searchVec.add(sLegs);
                break;
            }
        }
        return searchVec;
    }

    public boolean distaceAB(LatLng l1, LatLng l2){
        float dis1, dis2;
        boolean flowCk;

        Location loc_s= new Location("point_s");
        loc_s.setLatitude(nodeVec.get(0).get(0).get(0).latitude);
        loc_s.setLongitude(nodeVec.get(0).get(0).get(0).longitude);

        Location loc_1= new Location("point_1");
        loc_1.setLatitude(l1.latitude);
        loc_1.setLongitude(l1.longitude);

        Location loc_2= new Location("point_2");
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

    public String sendJSONString(Vector<Vector<Vector<LatLng>>> searchPath){
        String sendString="{\"routes\":[";

        for(int i=0; i<searchPath.size();i++){//legs
            if(i==0) sendString+="{";
            else sendString+=",{";
            sendString+="\"legs\":[";
            for(int j=0; j<searchPath.get(i).size();j++){//steps
                if(j==0) sendString+="{";
                else sendString+=",{";
                sendString+="\"steps\":[";
                for(int k=0; k<searchPath.get(i).get(j).size();k++){//point
                    if(k==0) sendString+="{";
                    else sendString+=",{";
                    sendString+="\"lat\":\""+String.valueOf(searchPath.get(i).get(j).get(k).latitude)
                            +"\",\"lng\":\""+String.valueOf(searchPath.get(i).get(j).get(k).longitude)
                            +"\"}";
                }
                sendString+="]}";
            }
            sendString+="]}";
        }
        sendString+="]}";

        System.out.println("test005 : "+sendString);


        return sendString;
    }

    public void saveLog(String data){
        if (!checkExternalStorage()) return;
        // 외부메모리를 사용하지 못하면 끝냄


        String log_data = data;


        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path += "/MyDir";
        try {
            File f = new File(path, "log_vector.txt"); // 경로, 파일명

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

        String path= Environment.getExternalStorageDirectory().toString();
        String dirPath = getFilesDir().getAbsolutePath();
        System.out.println("test000_1: "+path);
        System.out.println("test000_1: "+dirPath);


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
