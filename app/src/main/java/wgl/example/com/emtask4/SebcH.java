package wgl.example.com.emtask4;

/**
 * Created by EMGRAM on 2016-09-08.
 */
public class SebcH {
    String cate1, cate2, cate3;
    String name, addr;

    public SebcH(String cate1, String cate2, String cate3, String name, String addr){
        this.cate1= cate1;
        this.cate2= cate2;
        this.cate3= cate3;
        this.name= name;
        this.addr= addr;
    }

    public String getCate1(){
        return cate1;
    }
    public String getCate2(){
        return cate2;
    }
    public String getCate3(){
        return cate3;
    }

    public String getName(){
        return name;
    }

    public String getAddr(){
        return addr;
    }

}
