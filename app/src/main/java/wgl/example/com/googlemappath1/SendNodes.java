package wgl.example.com.googlemappath1;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendNodes implements Parcelable {
    List<HashMap<String, String>> sendList=new ArrayList<>();

    public SendNodes(List<HashMap<String, String>> sendList) {
        this.sendList=sendList;
    }

    public SendNodes() {
    }

    public SendNodes(Parcel in) {
        in.readList(sendList,ClassLoader.getSystemClassLoader());
    }

    public void setsendList(List<HashMap<String, String>> sendList) {
        this.sendList=sendList;
    }
    public List<HashMap<String, String>> getSendList(){
        return sendList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(sendList);

    }

    // Parcel 에 저장된 값을 기반으로 객체를 생성할 수 있는 기능을 추가한다.​
    public static final Parcelable.Creator<SendNodes> CREATOR = new Parcelable.Creator<SendNodes>()
    {
        public SendNodes createFromParcel(Parcel in )
        {
            return new SendNodes(in);
        }
        public SendNodes[] newArray(int size)
        {
            return new SendNodes[size];
        }
    };

}
