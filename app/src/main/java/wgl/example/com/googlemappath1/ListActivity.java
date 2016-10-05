package wgl.example.com.googlemappath1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    TextView legTotal;  //n개의 leg중 index    ex) 1/2/...
    TextView stepTotal; //leg하나마다 step의 개수 ex) 1번leg의 step수/2번 leg의 step수/...
    TextView node;  //list에 표시되는 node의 개수
    EditText searchstep;
    EditText searchleg;
    Button searchBt;
    ListView list;
    NodeAdapter nodeAdapter;

    boolean click_Ck=true;

    HashMap<String,String> clickItem1, clickItem2;
    List<HashMap<String,String>> path= new ArrayList<>();   //MainActivity에서 받은 전체 list
    List<HashMap<String,String>> path_search;   //부분 검색결과 list

    LogSave logSave=new LogSave();

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
        path= ((SendNodes)intent.getParcelableExtra("list")).getSendList();
        if(path.size()>0)
        setList(0,0);

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

                    if(path.size()>0){
                        setList(leg_i,step_i);
                    }

                }catch(NumberFormatException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"조건을 확인해 주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //log저장 준비
        logSave.addFile(getApplicationContext(), "MyDir", "log_hashmap");

    }

    //경로 node들 리스트뷰에 표시, (0,0): 전체 표시용
    //경유지 미사용에 따라 leg의 개수는 1개이지만 경유지 사용할 경우를 생각해서 leg에 따른 step수 표현
    public void setList(int leg_Num, int step_Num){
        //leg_num, step_Num 검색 조건 int
        //legNum, stepNum -> leg : 1/2/3/... , step : leg1의 step개수/ leg2의 step개수/...

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
        //전체 leg(구간)에따른 전체 step표시
        legTotal.setText(legNum);
        stepTotal.setText(stepNum);

        //검색 결과 list 표시
        //전체 표시
        if(leg_Num==0&&step_Num==0){
            for(int i=0; i<path.size(); i++){        //i번째 leg의 좌표Vector Vector<Vector<LatLng>>
                path_search.add(path.get(i));
            }
        //leg와 step에 따른경로 표시
        }else if(leg_Num>0&&step_Num>=0){

            if(step_Num==0&&leg_Num<=Integer.valueOf(legNum)){  //leg가 1개 일경우 차이 없음.
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

        nodeAdapter= new NodeAdapter(getApplicationContext(), R.layout.info, path_search);
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

                    clickItem1=path_search.get(i);
                    click_Ck=false;

                }else{
                    logSave.save("second list item click");

                    nodeAdapter.setClickNum(2);
                    nodeAdapter.setIndexChk(i);

                    clickItem2=path_search.get(i);
                    showMainActivity(searchPath(clickItem1, clickItem2));
                }
            }
        });
    }

    public List<HashMap<String, String>> searchPath(HashMap<String,String> listItemClick1,
                                                     HashMap<String,String> listItemClick2){

        HashMap<String, String> start_l, stop_l;
        List<HashMap<String, String>> searchHash=new ArrayList<>();
        int startIndex, stopIndex;

        if(checkAB(listItemClick1, listItemClick2)){
            start_l=listItemClick1;
            stop_l=listItemClick2;
        }else{
            start_l=listItemClick2;
            stop_l=listItemClick1;
        }

        startIndex=path.indexOf(start_l);
        stopIndex=path.indexOf(stop_l);
        searchHash=path.subList(startIndex,stopIndex);    //startIndex~stopIndex-1까지 입력
        searchHash.add(path.get(stopIndex));   //두번째 선택지 같이 표시하기 위해서(stopIndex번째 입력)

        return searchHash;
    }

    //index 방향에 관계없이 검색하기 위해서
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

   //list item 클릭에 따른 부분 경로 전달
    public void showMainActivity(List<HashMap<String, String>> list){
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        
        SendNodes searchNods= new SendNodes(list);
        intent.putExtra("path",searchNods);

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
