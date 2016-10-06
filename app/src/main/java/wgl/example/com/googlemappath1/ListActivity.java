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

    String pathCk_s="";
    boolean click_Ck=true;

    DirectionsJSONParser parser;

    LatLng searchVal_s, searchVal_e;

    Vector<Vector<Vector<LatLng>>> nodeVec= new Vector();
    Vector<LatLng> nodes;
    Vector<LatLng> allNodes;

    NodeAdapter nodeAdapter;

    LogSave logSave= new LogSave();

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
                logSave.save("search click");

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

        logSave.addFile(getApplicationContext(),"MyDir","log_vector");
    }

    //MainActivity에서 전달 받은 전체 경로 JSON을 Vector로 저장 및 list표시
    private void receiveNodes(String receive){

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
            allNodes=nodes;
        }

    }

    //경로 node들 리스트뷰에 표시
    private void setList(int leg_Num, int step_Num){

        nodes= new Vector();
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
                    nodes.addAll(nodeVec.get(i).get(j));
                }
            }

        }else if(leg_Num>0&&step_Num>=0){
            if(step_Num==0&&leg_Num<=Integer.valueOf(legNum)){
                for(int i=0; i<nodeVec.get(leg_Num-1).size(); i++){
                    nodes.addAll(nodeVec.get(leg_Num-1).get(i));
                }

            }else if(leg_Num<=Integer.valueOf(legNum)&&
                    step_Num<=Integer.valueOf(stepNum)&&step_Num>0){
                nodes.addAll(nodeVec.get(leg_Num-1).get(step_Num-1));
            }
        }
        node.setText(String.valueOf(nodes.size()));

        nodeAdapter= new NodeAdapter(getApplicationContext(), R.layout.info, nodes);
        list.setAdapter(nodeAdapter);

        logSave.save("list set");

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(click_Ck){
                    logSave.save("first list item click");

                    nodeAdapter.setClickNum(1);
                    nodeAdapter.setIndexChk(i);

                    nodeAdapter.notifyDataSetChanged();

                    searchVal_s=nodes.get(i);
                    click_Ck=false;

                }else{
                    logSave.save("second list item click");

                    nodeAdapter.setClickNum(2);
                    nodeAdapter.setIndexChk(i);

                    searchVal_e=nodes.get(i);
                    showMainActivity(sendJSONString(searchPath(searchVal_s, searchVal_e)));
                }
            }
        });
    }

    //부분 경로 검색 및 반환
    private Vector<Vector<Vector<LatLng>>> searchPath(LatLng click_item1, LatLng click_item2){
        Vector<Vector<Vector<LatLng>>> searchVec= new Vector();
        boolean saveChk=false;
        boolean stopChk=false;

        LatLng start_l, stop_l;

        //index 양방향 검색
        if(checkAB(click_item1, click_item2)){
            start_l=click_item1;
            stop_l=click_item2;
        }else{
            start_l=click_item2;
            stop_l=click_item1;
        }

        for(int i=0; i<nodeVec.size(); i++){
            Vector<Vector<LatLng>> search_legs= new Vector();
            for(int j=0; j<nodeVec.get(i).size();j++){
                Vector<LatLng> search_steps= new Vector();
                for(int k=0; k<nodeVec.get(i).get(j).size();k++){
                    if(start_l.equals(nodeVec.get(i).get(j).get(k))){
                        saveChk=true;
                    }else if(stop_l.equals(nodeVec.get(i).get(j).get(k))){
                        saveChk=false;
                        stopChk=true;
                    }
                    if(saveChk){
                        search_steps.add(nodeVec.get(i).get(j).get(k));
                    }else if(stopChk){
                        search_steps.add(nodeVec.get(i).get(j).get(k));
                        break;
                    }
                }
                if(saveChk){
                    search_legs.add(search_steps);
                }else if(stopChk){
                    search_legs.add(search_steps);
                    break;
                }
            }
            if(saveChk) {
                searchVec.add(search_legs);
            }else if(stopChk){
                searchVec.add(search_legs);
                break;
            }
        }
        return searchVec;
    }

    //클릭 아이템 index비교
    private boolean checkAB(LatLng l1, LatLng l2){
        boolean flowChk;
        int click1_index, click2_index;
        click1_index=allNodes.indexOf(l1);
        click2_index=allNodes.indexOf(l2);
        if(click1_index<click2_index){
            flowChk=true;
        }else{
            flowChk=false;
        }
        return flowChk;
    }

    //MainActivity로 부분 검색 경로 전달
    private void showMainActivity(String sendJSON){
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("list",sendJSON);

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private String sendJSONString(Vector<Vector<Vector<LatLng>>> searchPath){
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

        return sendString;
    }

}
