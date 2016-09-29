package wgl.example.com.emtask1;
//http://mainia.tistory.com/1093 참고
//System.getProperty 관련
//http://sunphiz.me/wp/archives/1688
//http://forum.falinux.com/zbxe/index.php?document_srl=571152&mid=lecture_tip

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;


public class OSInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TextView SYSinfo = (TextView) findViewById(R.id.ostext);
        SYSinfo.setText(ReadSYSinfo());
    }


    //시스템 정보 출력
    //getProperty() 만든 함수
    //텍스트뷰에 출력할 text로 만듬
    private static StringBuffer SYSinfoBuffer;// 저장버퍼

    private String ReadSYSinfo()
    {
        SYSinfoBuffer = new StringBuffer();

        getProperty("os.name", "os.name", SYSinfoBuffer);   //OS이름
        getProperty("os.version", "os.version", SYSinfoBuffer); //OS버전

        getProperty("java.vendor.url", "java.vendor.url", SYSinfoBuffer);   //자바 공급자 url
        getProperty("java.version", "java.version", SYSinfoBuffer); //자바 버전
        getProperty("java.class.path", "java.class.path", SYSinfoBuffer);   //자바 클래스 경로
        getProperty("java.class.version", "java.class.version", SYSinfoBuffer); //자바 클래스 버전
        getProperty("java.vendor", "java.vendor", SYSinfoBuffer);   //자바 공급자
        getProperty("java.home", "java.home", SYSinfoBuffer);   //자바 설치 디렉토리

        getProperty("user.name", "user.name", SYSinfoBuffer);   //사용자 계정
        getProperty("user.home", "user.home", SYSinfoBuffer);   //사용자 홈 디렉토리
        getProperty("user.dir", "user.dir", SYSinfoBuffer); //현제 디렉토리
        /*
        os.arch os아키텍처
        file.separator 파일구분자
        pathline.separator 경로구분자
        line.separator 형구분자
         */

        return SYSinfoBuffer.toString();
    }

    //System.getProperty() 사용
    private void getProperty(String desc, String property, StringBuffer tBuffer)
    {
        tBuffer.append(desc);//명칭
        tBuffer.append(" : ");
        tBuffer.append(System.getProperty(property));//값
        tBuffer.append("\n");
    }

    //OS정보, OSInfoActivity에서는 사용하지 않고 System.getProperty()를 이용한 getProperty이용 사용
    private String ReadOSinfo() {
        ProcessBuilder cmd; //명령어 실행용
        String result = "";

        try {
            String[] args = { "/system/bin/cat", "/proc/version" };
            cmd = new ProcessBuilder(args);

            Process process = cmd.start(); //명령어 실행

            InputStream in = process.getInputStream(); //명령어 실행 결과 저장
            byte[] re = new byte[1024];
            //한줄씩 읽어서 저장
            while (in.read(re) != -1) {
                System.out.println(new String(re));
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {  //입축력 예외
            ex.printStackTrace();   //애러 출력
        }
        return result;
    }
}
