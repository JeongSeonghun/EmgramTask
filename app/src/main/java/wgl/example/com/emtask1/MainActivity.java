package wgl.example.com.emtask1;
//http://mainia.tistory.com/1093 참고
//http://sunphiz.me/wp/archives/1688

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/*
    시스템 정보는 System.getProperty()와 process 두 종류 사용
    os정보에서는 System.getProperty()
    cpu와 memory 정보에서는 process, inputstream,
    BufferedReader, InputStreamReader 사용
*/
//cpu정보에서 string.isEmpty()는 빈 문자열을 저장하지 않기 위해 추가(218)
//cpu정보에서 빈 문자열을 리스트에 포함함
//한줄로 같이 나오는 명칭과 값 문자열을 나누는 부분 추가 (153)

public class MainActivity extends AppCompatActivity {
    //private ArrayList<String> list; //대신에 String 배열로 사용
    private ArrayAdapter<String> adapter;
    ListView listv;
    ArrayList al= new ArrayList();

    String[] str={"OS 정보", "Memory 정보", "CPU 정보"};//리스트뷰 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listv= (ListView)findViewById(R.id.infoList);

        AddSysInfo(al);
        ReadMemInfo(al);
        ReadCPUinfo(al);

        //list.setAdapter(new InfoAdapter(getApplicationContext(), R.layout.info_list, al));
        InfoAdapter adapter = new InfoAdapter(getApplicationContext(), R.layout.info_list, al);
        listv.setAdapter(adapter);

        /*클릭 이벤트
        AdapterView.OnItemClickListener m=new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){

            }
        };
        list.setOnItemClickListener(m);
        */

    }
//시스템 정보 출력(os)
    //getProperty() 만든 함수
    //텍스트뷰에 출력할 text로 만듬

    //private String ReadSYSinfo() 변경
    private void AddSysInfo(ArrayList<String> al)
    {
        /*
        os.arch os아키텍처
        file.separator 파일구분자
        pathline.separator 경로구분자
        line.separator 형구분자
         */

        al.add(getProInfo("OS Name", "os.name"));   //OS 이름
        al.add(getProInfo("OS Version", "os.version")); //OS 버전

        al.add(getProInfo("JAVA Vendor URL", "java.vendor.url"));   //자바 공급자 URL
        al.add(getProInfo("JAVA Version", "java.version")); //자바 버전
        al.add(getProInfo("JAVA Class Path", "java.class.path"));   //자바 클래스 경로
        al.add(getProInfo("JAVA Class Version", "java.class.version")); //자바 클래스 버전
        al.add(getProInfo("JAVA Vendor", "java.vendor"));   //자바 공급자
        al.add(getProInfo("JAVA Install dir", "java.home"));    //자바 설치 디렉토리

        al.add(getProInfo("User Name", "user.name"));   //사용자 계정
        al.add(getProInfo("User Home dir", "user.home"));   //사용자 홈 디렉토리
        al.add(getProInfo("Current dir", "user.dir"));  //현제 디렉토리

    }

    //System.getProperty() 사용
    //private void getProperty(String desc, String property, StringBuffer tBuffer) 변경
    //정보 명칭 : 값 문자열 형태로
    private String getProInfo(String name, String property){
        String str;
        str=name+" : "+String.valueOf(System.getProperty(property));

        return str;
    }

    //memory 정보
    private String ReadMemInfo(ArrayList al)
    {
        ProcessBuilder cmd;

        StringBuffer strMemory = new StringBuffer();

        //ActivityManager 앱의 실행, 종료, 인탠트 전달/파라미터 시스템 내부의 상태 파악
        //getSystemService 시스템 레벨 서비스 제어관련/ Context클래스 메소드
        //ACTIVITY_SERVICE 파라미터 ActivityManager 이용시 사용
        ActivityManager actvityManager = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
        ActivityManager.MemoryInfo mInfo = new ActivityManager.MemoryInfo ();
        actvityManager.getMemoryInfo( mInfo );// memory정보 mInfo에 저장

        /*
        strMemory.append("Available Memory : ");
        strMemory.append(mInfo.availMem);//availMem: 사용가능, totalMem: 전체
        strMemory.append("\n");
        strMemory.append("\n");
        */

        String result=strMemory.toString();


        al.add("Available Memory : "+mInfo.availMem);   //이용가능 메모리

        try{
            String[] args = {"/system/bin/cat", "/proc/meminfo"};
            cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            InputStream in = process.getInputStream();
            /*
            byte[] re = new byte[1024];
            while(in.read(re) != -1){
                System.out.println(new String(re));
                result = result + new String(re);
            }
            */

            // 한 줄씩 읽어서 arraylist에 저장
            // BufferedReader, InputStreamReader 사용
            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(in,"EUC_KR"));
            while(true){
                String string= bufferedReader.readLine();//한줄씩 읽어들임

                if(string != null){
                    al.add(string);
                }else{
                    break;
                }

                //추가 : 기준으로 명칭과 값 나눔
                String stname, stval;
                int dnum;   //":" 구분할 위치
                dnum=string.indexOf(":");
                //System.out.println("division string: "+string);
                //System.out.println("division num: "+dnum);
                if(!string.isEmpty()) { //비었을 때 오류 방지
                    //System.out.println("division front string: " + string.substring(0, dnum - 1));
                    //System.out.println("division behind string: " + string.substring(dnum+1, string.length()));
                    stname=string.substring(0, dnum - 1);
                    stval=string.substring(dnum+1, string.length());
                }
            }

            in.close();
        } catch(IOException ex){
            ex.printStackTrace();
        }
        return result;
    }

    /* 메모리 정보 구분
    안드로이드OS
    Available Memory : 남은 메모리

    MemTotal : 전체 메모리(예비 메모리 제외한)
    MemFree : 예비 메모리
    Buffers : 버퍼
    Cached : 캐시
    Dirty : 앱 정시에만 돌려 받는 메모리

     */


    //cpu 정보
    private String ReadCPUinfo(ArrayList al)
    {
        ProcessBuilder cmd;
        String result="";

        try{
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            InputStream in = process.getInputStream();
            /*
            byte[] re = new byte[1024];
            while(in.read(re) != -1){
                System.out.println(new String(re));
                result = result + new String(re);
            }
            */

            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(in,"EUC_KR"));

            while(true){
                String string= bufferedReader.readLine();

                if(string != null){
                    al.add(string);
                }else{
                    break;
                }

                //string.isEmpty()는 빈 문자열을 저장하지 않기 위해 추가(\n만 존제하는 경우)
                if(string.isEmpty()){
                    al.remove(al.size()-1);
                }

            }
            in.close();



        } catch(IOException ex){
            ex.printStackTrace();
        }
        return result;
    }
}

//BaseAdapter 사용 클래스
//layout은 info_list로 textview 1개로 구성된 layout 제작 사용

class InfoAdapter extends BaseAdapter {

    Context context;
    int layout;
    ArrayList al=new ArrayList();

    LayoutInflater inf;// xml에 정의된 자원(resource)들을 view로 반환

    public InfoAdapter(Context context, int layout, ArrayList al){
        this.context= context;
        this.layout=layout;
        this.al=al;
        this.inf= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    // 어댑터로 처리될 데이터 개수 반환
    @Override
    public int getCount() {
        return al.size();
    }

    // 특정 위치 데이터 반환
    @Override
    public Object getItem(int i) {
        return al.get(i);
    }

    // 특정 위치 데이터 ID 반환
    @Override
    public long getItemId(int i) {
        return i;
    }

    // 특정 위치의 데이터를 출력할 뷰를 얻음
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {//(해당행 위치, 해당행 레이아웃, 해당행 부모뷰(리스트뷰))
        if(view==null){//해당하는 행에 레이아웃객체 없을 시
            //xml파일로 레이아웃 객체 생성
            view= inf.inflate(layout, null);
        }
        TextView sysInfo= (TextView)view.findViewById(R.id.sys_info);//view레이아웃 객체내의 텍스트뷰 id 사용

        sysInfo.setText(al.get(i).toString());

        return view;
    }
    /*
    View inflate( int resource, ViewGroup root ) 현제 사용
    View inflate( XmlPullParser parser, ViewGroup root )
    View inflate( XMLPullParser parser, ViewGroup root, boolean attachToRoot )
    View inflate( int resource, ViewGroup root, boolean attachToRoot )
    */
}
