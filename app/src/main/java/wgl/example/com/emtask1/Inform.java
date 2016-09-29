package wgl.example.com.emtask1;


public class Inform {
    String key;
    String value;

    public Inform(){

    }
    public Inform(String key, String value){
        this.key= key;
        this.value= value;
    }

    // key, value 동시 변경용
    public void setInfo(String key, String value){
        this.key= key;
        this.value= value;
    }

    // key 변경용
    public void setKey(String key){
        this.key= key;
    }

    // value 변경용
    public void setValue(String value){
        this.value= value;
    }

    // key 출력
    public String getKey(){
        return key;
    }

    // value 출력
    public String getValue(){
        return value;
    }

    @Override
    public String toString() {
        return "Inform{key='"+key+"', value='"+value+"'}";
    }
}
