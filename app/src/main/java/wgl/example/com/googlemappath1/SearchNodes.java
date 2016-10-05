package wgl.example.com.googlemappath1;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by EMGRAM on 2016-10-04.
 */
public class SearchNodes implements Parcelable {
    List<HashMap<String, String>> searchList=new ArrayList<>();


    public SearchNodes(List<HashMap<String, String>> searchList) {
        this.searchList=searchList;
    }

    public SearchNodes() {
    }

    public SearchNodes(Parcel in) {
        in.readList(searchList,ClassLoader.getSystemClassLoader());
    }

    public void setSearchList(List<HashMap<String, String>> searchList) {
        this.searchList=searchList;
    }
    public List<HashMap<String, String>> getSearchList(){
        return searchList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(searchList);

    }

    // Parcel 에 저장된 값을 기반으로 객체를 생성할 수 있는 기능을 추가한다.​
    public static final Parcelable.Creator<SearchNodes> CREATOR = new Parcelable.Creator<SearchNodes>()
    {
        public SearchNodes createFromParcel(Parcel in )
        {
            return new SearchNodes(in);
        }
        public SearchNodes[] newArray(int size)
        {
            return new SearchNodes[size];
        }
    };

}
