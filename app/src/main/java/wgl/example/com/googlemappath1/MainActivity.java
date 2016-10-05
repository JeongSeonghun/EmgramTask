package wgl.example.com.googlemappath1;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
        ,GoogleMap.OnMapClickListener{

    private GoogleMap map;
    Marker startMark, stopMark;

    SupportMapFragment mapfrag;

    Button pathBt, resetBt;
    Button listShow;
    Button logShow;
    RadioButton startRadio, stopRadio;
    TextView startTxt, stopTxt;

    boolean startMarkCheck=true, stopMarkCheck=true; //마커 표시 여부
    boolean rePolyCheck=true;  //경로 polyline 표시 여부
    int searchNum=1;    //부분 검색 경로 polyline 횟수 확인

    List<HashMap<String, String>> path;

    LogSave logSave=new LogSave();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pathBt= (Button)findViewById(R.id.path);
        resetBt= (Button)findViewById(R.id.reset);

        startTxt= (TextView)findViewById(R.id.start_t);
        stopTxt= (TextView)findViewById(R.id.stop_t);

        startRadio= (RadioButton)findViewById(R.id.start_r);
        stopRadio= (RadioButton)findViewById(R.id.stop_r);

        mapfrag= ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapfrag.getMapAsync(this);

        
        pathBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                NodeWgs nodeWgs= new NodeWgs();

                logSave.save("path Click");
                
                if(rePolyCheck){
                    nodeWgs.execute("https://maps.googleapis.com/maps/api/directions/json?"
                            +"origin="+startTxt.getText().toString()
                            +"&destination="+stopTxt.getText().toString()
                            +"&key=AIzaSyCc2PqOCbvrNGtDRwINl4X_tiywxt9TDPA");
                }else{
                    Toast.makeText(getApplicationContext(),"Reset버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
                }
                rePolyCheck=false;
            }
        });

        resetBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                logSave.save("reset Click");
                map.clear();
                startMarkCheck=true;
                stopMarkCheck=true;
                rePolyCheck=true;
                searchNum=1;
                path.clear();
                startTxt.setText("");
                stopTxt.setText("");
            }
        });

        listShow= (Button)findViewById(R.id.list_show);
        listShow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), ListActivity.class);

                logSave.save("list click");
                
                if(searchNum<=3){
                    SendNodes sendNodes= new SendNodes(path);
                    intent.putExtra("list",sendNodes);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"3번까지로 제한됩니다. reset을 눌러주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logShow= (Button)findViewById(R.id.log_show);
        logShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),LogActivity.class);
                startActivity(intent);
            }
        });

        //외부저장소 권한 확인
        checkDangerousPermissions();
        
        //log저장 준비
        logSave.addFile(getApplicationContext(), "MyDir", "log_hashmap");

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        List<HashMap<String,String>> searchPath;

        SendNodes sendNodes =(SendNodes)intent.getParcelableExtra("path");
        searchPath= sendNodes.getSendList();
        addPolyline(searchPath, false);

        logSave.save("search polyline end");
        
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);
        
        LatLng la= new LatLng(34.052, -118.246);    //로스앤젤레스
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(la, 10));

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

    //google 서비스, 경로 JSON
    private class NodeWgs extends AsyncTask<String, Integer,String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {

                // 연결 url 설정
                URL url2 = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
                // 연결되었으면.
                if (conn2 != null) {
                    conn2.setConnectTimeout(10000);//최대 연결시간(10초)
                    conn2.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.

                    if (conn2.getResponseCode() == HttpURLConnection.HTTP_OK) {//url주소 사이에 띄어쓰기 존제시 다른코드 반환됨

                        BufferedReader br2 = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br2.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line );
                        }
                        br2.close();
                    }
                    conn2.disconnect();
                }

            } catch (Exception ex) {
                ex.printStackTrace();

            }

            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {


            String pathChk_str;    //경로 데이터 확인
            try {
                JSONObject gDirectJo = new JSONObject(str);
                
                //경로 얻기
                DirectionsJSONParser parser = new DirectionsJSONParser();
                path=parser.parse(gDirectJo);
                                
                pathChk_str=gDirectJo.getString("status"); //길찾기 응답요소, 상태코드 참고
                if(pathCk(pathChk_str)) {
                    addPolyline(path, true);
                    logSave.save("path polyline end");
                    
                }else {
                    Toast.makeText(getApplicationContext(), "지원되지 않아요!:" + pathChk_str, Toast.LENGTH_SHORT).show();
                    logSave.save("path polyline false");
                }

            } catch (JSONException e) {

                e.printStackTrace();

            }

        }

        //경로결과 확인
        public boolean pathCk(String checkStr){
            boolean check;
            //경로 결과가 없을시 status는 OK가 아닌 다른 값을 보임
            if(checkStr.equals("OK"))
                check=true;
            else
                check=false;
            return check;
        }

        //OnpreExecute 백그라운드 작업 전, 초기화, 셋팅
        //OnPostEexcute 백그란운드 작업 후
        //OnProgressUpdate 백그라운드 중간에 ui(main 매서드

    }


    //polyline 그리기, searchChk: true-전체경로 표시, false- 부분 검색 경로 표시
    public void addPolyline(List<HashMap<String, String>> node, boolean searchChk){

        PolylineOptions poly= new PolylineOptions().geodesic(true);
        int[] width={3,10,8,6};
        int[] polColor={Color.RED, Color.BLUE, Color.CYAN, Color.GREEN};
        int setNum;
        int colorNum;

        //전체경로와 부분검색경로 구분용, 0:전체경로, 1~3:부분경로
        if(searchChk){
            setNum=0;
            colorNum=0;
        }else{
            setNum=searchNum;
            colorNum=searchNum;
            searchNum+=1;
        }

        for(int i=0; i<node.size(); i++){
            poly.add(new LatLng(Double.valueOf(node.get(i).get("lat")),
                    Double.valueOf(node.get(i).get("lng"))));
            poly.width(width[setNum]);
            poly.color(polColor[colorNum]);
        }

        map.addPolyline(poly);

    }


    @Override
    public void onMapClick(LatLng latLng) {

        if(startRadio.isChecked()){   //start 선택시
            if(startMarkCheck){   //start Marker 존제 여부 확인 후 추가
                startMark=map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(30)));//마커색
                startMarkCheck=false;
            }else{  //start Marker 위치 변경
                startMark.setPosition(latLng);
            }

            //start 좌표 표시 "latitude,longitude"
            startTxt.setText(latLng.latitude+","+latLng.longitude);
            logSave.save("start location click");
        }

        if(stopRadio.isChecked()){  //stop선택시
            if(stopMarkCheck){
                stopMark=map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(90)));//마커색
                stopMarkCheck=false;
            }else{
                stopMark.setPosition(latLng);
            }
            stopTxt.setText(latLng.latitude+","+latLng.longitude);
            logSave.save("stop location click");
        }

    }

    //권한 확인
    private void checkDangerousPermissions() {
        String[] permissions = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "log 텍스트 저장.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }
}
