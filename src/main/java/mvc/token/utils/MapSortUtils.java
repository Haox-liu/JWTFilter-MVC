package mvc.token.utils;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Map;

public class MapSortUtils {

    public static String sortLetter(Map<String,Object> map) {

        int countKey =map.keySet().size();

        String[] letterStr = new String[countKey];
        int index = 0;
        for(String key : map.keySet()){
            letterStr[index] = key;
            index++;
        }
        //严格按字母表顺序排序，也就是忽略大小写排序 Case-insensitive sort
        Arrays.sort(letterStr,String.CASE_INSENSITIVE_ORDER);

        //要加密的字符串；key=value&key=value（realName=哈哈&borrowingTime=2018）
        StringBuffer str = new StringBuffer();
        for(int i=0;i<letterStr.length;i++) {
            //realName=realName&borrowingTime=2018
            str.append(letterStr[i]+"="+map.get(letterStr[i])+"&");
        }


        String md5Str = MD5Utils.digest(str.toString());

        return md5Str;
    }

    public static void main(String[] args) {
        Gson gson = new Gson();

        Map<String, Object> map = gson.fromJson("{\"username\":\"admin\",\"password\":\"admin\"}", Map.class);

        System.out.println(sortLetter(map));
    }

}
