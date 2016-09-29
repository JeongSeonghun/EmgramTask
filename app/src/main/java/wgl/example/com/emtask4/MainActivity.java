package wgl.example.com.emtask4;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    PhpDown task;
    SebcAdapter seAdapter;
    ArrayList<SebcH> al=new ArrayList<SebcH>();
    ListView seList;
    String total_num;
    TextView total_num_t;
    Button total_bt;
    Boolean all_ch=true; //모두보기 버튼 한번만 작동용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seList= (ListView)findViewById(R.id.list);
        total_num_t=(TextView)findViewById(R.id.tot_num);
        total_bt=(Button)findViewById(R.id.button);

        //json관련 초반 20개 표시, 총 개수 확인, asynctask 사용
        task = new PhpDown();
        task.execute("http://openapi.seoul.go.kr:8088/63766f77687368753535566668746d/json/SebcHistoricSiteKor/1/20/");
//api key 63766f77687368753535566668746d
/*api 요청인자
KEY         String(필수)  인증키 OpenAPI 에서 발급된 인증키
TYPE        String(필수)  요청파일타입 xml : xml, xml파일 : xmlf, 엑셀파일 : xls, json파일 : json
SERVICE     String(필수)  서비스명 SebcHistoricSiteKor
START_INDEX INTEGER(필수) 요청시작위치 정수 입력 (페이징 시작번호 입니다 : 데이터 행 시작번호)
END_INDEX   INTEGER(필수) 요청종료위치 정수 입력 (페이징 끝번호 입니다 : 데이터 행 끝번호)
MAIN_KEY    STRING(선택)  키 문자열
*/

        //목록 클릭. intext활용 주소 및 명칭 보냄
        seList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(getApplicationContext(),AddrActivity.class);
                intent.putExtra("name",al.get(i).getName());
                intent.putExtra("addr",al.get(i).getAddr());
                startActivity(intent);
            }
        });

        total_bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(all_ch){
                    task= new PhpDown();
                    task.execute("http://openapi.seoul.go.kr:8088/63766f77687368753535566668746d/json/SebcHistoricSiteKor/21/"+
                            total_num);
                    all_ch=false;
                }

            }
        });
    }
    //doINBackground, execute 파라미터 타입: OnPressUpdate 파라미터,
    //: doInBackground 리턴값
    private class PhpDown extends AsyncTask<String, Integer,String> {
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

            String cate1, cate2, cate3, name, addr;
            //분류1, 분류2, 분류3, 명칭, 주소
            SebcH sebc_n;

            try {
                /* JSON 출력값
공통 list_total_count 총 데이터 건수 (정상조회 시 출력됨)
공통 RESULT.CODE 요청결과 코드
공통 RESULT.MESSAGE 요청결과 메시지
1   MAIN_KEY 키
2   CATE1_NAME 분류1
3   CATE2_NAME 분류2
4   CATE3_NAME 분류3
5   NAME_KOR 명칭
6   ADD_KOR 주소
7   ADD_KOR_ROAD 도로명주소
8   H_KOR_CITY 행정 시
9   H_KOR_GU 행정 구
10  H_KOR_DONG 행정 동
*/

                JSONObject root = new JSONObject(str);
                JSONArray ja;
                //전체 숫자 저장 및 표시
                total_num= root.getJSONObject("SebcHistoricSiteKor").getString("list_total_count");
                total_num_t.setText("총 숫자: "+total_num);

                //유적지 관련 내용
                ja=root.getJSONObject("SebcHistoricSiteKor").getJSONArray("row");

                for (int i = 0; i < ja.length(); i++) {

                    JSONObject jo = ja.getJSONObject(i);

                    cate1= jo.getString("CATE1_NAME");
                    cate2= jo.getString("CATE2_NAME");
                    cate3= jo.getString("CATE3_NAME");
                    name= jo.getString("NAME_KOR");
                    //시,구,동 종합
                    addr= jo.getString("H_KOR_CITY")+" "+jo.getString("H_KOR_GU")+" "+jo.getString("H_KOR_DONG");

                    sebc_n=new SebcH(cate1, cate2, cate3, name, addr);
                    al.add(sebc_n);
                }

            } catch (JSONException e) {

                e.printStackTrace();

            }
            seAdapter= new SebcAdapter(getApplicationContext(), R.layout.sebc_list_item, al);
            seAdapter.notifyDataSetChanged();
            seList.setAdapter(seAdapter);
        }

        //OnpreExecute 백그라운드 작업 전, 초기화, 셋팅
        //OnPostEexcute 백그란운드 작업 후
        //OnProgressUpdate 백그라운드 중간에 ui(main 매서드

    }
}
