package wgl.example.com.googlemappath1;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
//import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
//import android.widget.ListView;
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
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
        ,GoogleMap.OnMapClickListener{
    private GoogleMap map;
    Marker startMark, stopMark;
    SupportMapFragment mapfrag;
    
    TextView startTxt, stopTxt;
    Button pathBt, resetBt;
    Button listShow;
    Button logShow;
    RadioButton startR, stopR;
    
    boolean startMark_chk=true, stopMark_chk=true; //마커 표시 여부
    boolean rePolyCheck=true;  //경로 polyline 확인
    String list_val="";    //intent전달용 json값
    int searchPolyNum=1;    //부분 검색 polyline 횟수 확인
    
    LogSave logSave= new LogSave();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pathBt= (Button)findViewById(R.id.path);
        resetBt= (Button)findViewById(R.id.reset);

        startTxt= (TextView)findViewById(R.id.start_t);
        stopTxt= (TextView)findViewById(R.id.stop_t);

        startR= (RadioButton)findViewById(R.id.start_r);
        stopR= (RadioButton)findViewById(R.id.stop_r);

        mapfrag= ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapfrag.getMapAsync(this);

        pathBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                logSave.save("path click");
                NodeWgs nodeWgs= new NodeWgs();

                if(rePolyCheck){
                    nodeWgs.execute("https://maps.googleapis.com/maps/api/directions/json?"
                            +"origin="+startTxt.getText().toString()
                            +"&destination="+stopTxt.getText().toString()
                            +"&key=AIzaSyCc2PqOCbvrNGtDRwINl4X_tiywxt9TDPA");
                }else{
                    Toast.makeText(getApplicationContext(),"Reset버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        resetBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                logSave.save("reset click");
                map.clear();
                startMark_chk=true;
                stopMark_chk=true;
                rePolyCheck=true;
                searchPolyNum=1;
                list_val="";
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

                if(searchPolyNum<4){
                    intent.putExtra("list",list_val);
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

        //권한 체크
        checkDangerousPermissions();
        //파일 생성 및 확인
        logSave.addFile(getApplicationContext(),"MyDir","log_vector");
        Button testBt=(Button)findViewById(R.id.button);
        testBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), Main3Activity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Vector<Vector<Vector<LatLng>>> searchPath;
        
        try {
            JSONObject gMapSearchJo = new JSONObject(intent.getStringExtra("list"));

            SearchJSONParser searchParser= new SearchJSONParser();
            searchPath=searchParser.parse(gMapSearchJo);

            addPolyline(searchPath, false);

            logSave.save("search polyline end");

        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    //google 서비스
    private class NodeWgs extends AsyncTask<String, Integer,String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {

                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.
                if (conn != null) {
                    conn.setConnectTimeout(10000);//최대 연결시간(10초)
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {//url주소 사이에 띄어쓰기 존제시 다른코드 반환됨

                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line );
                        }
                        br.close();
                    }
                    conn.disconnect();
                }

            } catch (Exception ex) {
                ex.printStackTrace();

            }

            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {

            Vector<Vector<Vector<LatLng>>> nodeVec;
            String pathChk_s;    //경로 데이터 확인
            try {
                JSONObject gDirectJo = new JSONObject(str);

                pathChk_s=gDirectJo.getString("status"); //길찾기 응답요소, 상태코드 참고

                //경로 얻기
                DirectionsJSONParser parser = new DirectionsJSONParser();
                nodeVec=parser.parse(gDirectJo);

                if(rePolyCheck){
                    list_val=gDirectJo.toString();
                    rePolyCheck=false;
                }

                if(pathChk(pathChk_s)) {
                    addPolyline(nodeVec, true);
                    logSave.save("path polyline end");

                }else {
                    Toast.makeText(getApplicationContext(), "지원되지 않아요!:" + pathChk_s, Toast.LENGTH_SHORT).show();
                    logSave.save("path polyline false");

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        //경로결과 확인
        public boolean pathChk(String checkStr){
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


    //polyline 그리기
    private void addPolyline(Vector<Vector<Vector<LatLng>>> node, boolean seachCk){

        PolylineOptions poly= new PolylineOptions().geodesic(true);
        int[] width={3,10, 7, 4};
        int[] polColor={Color.RED, Color.BLUE, Color.CYAN, Color.GREEN};
        //횟수에 따른 경로 색, 두깨 변경용
        int setNum;
        int colorNum;

        if(seachCk){
            setNum=0;
            colorNum=0;
        }else{
            setNum=searchPolyNum;
            colorNum=searchPolyNum;
            searchPolyNum+=1;
        }

        for(int i=0; i<node.size(); i++){
            for(int j=0; j<node.get(i).size(); j++){
                poly.addAll(node.get(i).get(j));
                poly.width(width[setNum]);
                poly.color(polColor[colorNum]);
            }
        }

        map.addPolyline(poly);

    }


    @Override
    public void onMapClick(LatLng latLng) {

        if(startR.isChecked()){   //start 선택시
            if(startMark_chk){   //start Marker 존제 여부 확인 후 추가
                startMark=map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(30)));
                startMark_chk=false;
            }else{  //start Marker 위치 변경
                startMark.setPosition(latLng);
            }

            //start 좌표 표시 "latitude,longitude"
            startTxt.setText(latLng.latitude+","+latLng.longitude);

            logSave.save("start mark click");
        }

        if(stopR.isChecked()){  //stop선택시
            if(stopMark_chk){
                stopMark=map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(90)));
                stopMark_chk=false;
            }else{
                stopMark.setPosition(latLng);
            }
            stopTxt.setText(latLng.latitude+","+latLng.longitude);

            logSave.save("stop mark click");
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
                Toast.makeText(this, "log text 저장.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

}
