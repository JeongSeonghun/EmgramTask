package wgl.example.com.emtask1;
//http://mainia.tistory.com/1093 참고
//http://sunphiz.me/wp/archives/1688

import android.app.ActivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class MemInfoActivity extends AppCompatActivity {
    TextView mtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        mtext= (TextView)findViewById(R.id.metext);
        mtext.setText(ReadCPUinfo());
    }
    private String ReadCPUinfo()
    {
        ProcessBuilder cmd;

        StringBuffer strMemory = new StringBuffer();

        //ActivityManager 앱의 실행, 종료, 인탠트 전달/파라미터 시스템 내부의 상태 파악
        //getSystemService 시스템 레벨 서비스 제어관련/ Context클래스 메소드
        //ACTIVITY_SERVICE 파라미터 ActivityManager 이용시 사용
        ActivityManager actvityManager = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
        ActivityManager.MemoryInfo mInfo = new ActivityManager.MemoryInfo ();
        actvityManager.getMemoryInfo( mInfo );

        strMemory.append("Available Memory : ");
        strMemory.append(mInfo.availMem);//availMem: 사용가능, totalMem: 전체
        strMemory.append("\n");
        strMemory.append("\n");

        String result=strMemory.toString();

        try{
            String[] args = {"/system/bin/cat", "/proc/meminfo"};
            cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[1024];
            while(in.read(re) != -1){
                System.out.println(new String(re));
                result = result + new String(re);
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
}
