package wgl.example.com.emtask1;

/**
 * Created by EMGRAM on 2016-09-06.
 */
public class Inform {
    String name;
    String val;
    public Inform(String name, String val){
        this.name=name;
        this.val=val;
    }

    public String getNam(){
        return name;
    }

    public String getVal(){
        return val;
    }

    public void setNam(String name){
        this.name=name;
    }

    public void setVal(String val){
        this.val=val;
    }

}
