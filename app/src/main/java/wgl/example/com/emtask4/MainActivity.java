package wgl.example.com.emtask4;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    String total_num;
    Button total_bt;

    private GoogleMap mMap;

    TourWgs tourWgs;
    ArrayList<TourSt> tour_al= new ArrayList<TourSt>();
    TextView total_tx;


    int sw=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps);//생성 당시
        setContentView(R.layout.back);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        /*생성 당시
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        */

        mapFragment.getMapAsync(this);

        tourWgs= new TourWgs();
        //api 인증 키 6e6a6764497368753433674b494178
        tourWgs.execute("http://openAPI.seoul.go.kr:8088/6e6a6764497368753433674b494178/json/SebcTourStreetKor/1/20/");
//api key 63766f77687368753535566668746d
/*api 요청인자
KEY         String(필수)  인증키 OpenAPI 에서 발급된 인증키
TYPE        String(필수)  요청파일타입 xml : xml, xml파일 : xmlf, 엑셀파일 : xls, json파일 : json
SERVICE     String(필수)  서비스명 SebcHistoricSiteKor
START_INDEX INTEGER(필수) 요청시작위치 정수 입력 (페이징 시작번호 입니다 : 데이터 행 시작번호)
END_INDEX   INTEGER(필수) 요청종료위치 정수 입력 (페이징 끝번호 입니다 : 데이터 행 끝번호)
MAIN_KEY    STRING(선택)  키 문자열
*/


        total_tx= (TextView)findViewById(R.id.total);
        total_bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                tourWgs= new TourWgs();
                tourWgs.execute("http://openAPI.seoul.go.kr:8088/6e6a6764497368753433674b494178/json/SebcTourStreetKor/21/"+
                        total_num);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    //doINBackground, execute 파라미터 타입: OnPressUpdate 파라미터,
    //: doInBackground 리턴값
    private class TourWgs extends AsyncTask<String, Integer,String> {
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
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
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

            String name, wgs_x, wgs_y;
            //명칭, x, y
            TourSt tourSt;

            try {
                /* JSON 출력값
                공통 list_total_count 총 데이터 건수 (정상조회 시 출력됨)
                공통 RESULT.CODE 요청결과 코드 (하단 메세지설명 참고)
                공통 RESULT.MESSAGE 요청결과 메시지 (하단 메세지설명 참고)
                1 MAIN_KEY 키
                2 NM_DP 검색 키워드
                3 KOR_ALIAS alias
                4 NAME_KOR 최종 표기명
                5 ADD_KOR 지번 주소
                6 LAW_SIDO 법정 시
                7 LAW_SGG 법정 구
                8 LAW_HEMD 법정 동
                9 H_KOR_CITY 행정 시
                10 H_KOR_GU 행정 구
                11 H_KOR_DONG 행정 동
                12 WGS84_X 중심 좌표 X
                13 WGS84_Y 중심 좌표 Y
                */

                JSONObject root = new JSONObject(str);
                JSONArray ja;
                //전체 숫자 저장 및 표시
                total_num= root.getJSONObject("SebcTourStreetKor").getString("list_total_count");

                //관광 거리 명칭 및 좌표
                ja=root.getJSONObject("SebcTourStreetKor").getJSONArray("row");

                for (int i = 0; i < ja.length(); i++) {

                    JSONObject jo = ja.getJSONObject(i);

                    // 명칭, x, y
                    name= jo.getString("NAME_KOR");
                    wgs_x= jo.getString("WGS84_X");
                    wgs_y= jo.getString("WGS84_Y");

                    tourSt=new TourSt(name, wgs_x, wgs_y);
                    tour_al.add(tourSt);
                }

            } catch (JSONException e) {

                e.printStackTrace();

            }
            //마커 표시
            if(sw==0){// 처음 20개 표시
                for(int i=0; i<tour_al.size(); i++){

                    LatLng la2= new LatLng(tour_al.get(i).getWgsY(),tour_al.get(i).getWgsX());
                    mMap.addMarker(new MarkerOptions().position(la2).title((i+1)+":"+tour_al.get(i).getAlias()));

                    if(i==0){
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(la2));
                    }

                }
                sw=1;

            }else if(sw==1){//나머지 전체 표시
                for(int i=21; i<tour_al.size(); i++){

                    LatLng la2= new LatLng(tour_al.get(i).getWgsY(),tour_al.get(i).getWgsX());
                    mMap.addMarker(new MarkerOptions().position(la2).title((i+1)+":"+tour_al.get(i).getAlias()));

                }
                sw=2;
            }


            //현제 표시된 개수와 전체 관광거리 개수표시
            total_tx.setText("Current/Total : "+tour_al.size()+"/"+total_num);

        }

        //OnpreExecute 백그라운드 작업 전, 초기화, 셋팅
        //OnPostEexcute 백그란운드 작업 후
        //OnProgressUpdate 백그라운드 중간에 ui(main 매서드

    }
}
