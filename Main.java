import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONObject;
import com.mashape.unirest.http.*;

class Main {

  public static String URL_API = "https://api.fund-pay.dev/api/";
  public static String authorization = "5e66IvcpA089r4oaYuxLm90D1123Zy3E";
  public static String secertkey = "EBnIetGKawROEtNDXjMY2OgYq3yEkxZDFBRA3npADU0261NIk2m4SduOvtXzkbBo";

  public static void main(String[] args) {
    // autoqrcode

    JSONObject autoqrcode_payload = new JSONObject()
    .put("orderid", "string")
    .put("account", "0912541428")
    .put("price", 0)
    .put("from_account", "string")
    .put("from_bank", "BBL")
    .put("from_name", "string");

    System.out.println(autoqrcode(autoqrcode_payload));

   

    // withdraw
    JSONObject withdraw_payload = new JSONObject()
    .put("orderid", "string")
    .put("account", "0912541428")
    .put("price", 0)
    .put("to_banking", "BBL")
    .put("name", "string");
       
    System.out.println(withdraw(withdraw_payload));

    // deposit
    JSONObject deposit_payload = new JSONObject()
    .put("orderid","string")
    .put("account", "0912541428")
    .put("price", 0)
    .put("from_account", "string")
    .put("from_bank", "BBL")
    .put("from_name","string");
    
    System.out.println(deposit(deposit_payload));
  }

  static String autoqrcode(JSONObject payload) {
      return sendData("autoqrcode", genSignature(payload));
  }

  static String deposit(JSONObject payload){
    return sendData("deposit", genSignature(payload));
  }

  static String withdraw(JSONObject payload){
    return sendData("withdraw", genSignature(payload));
  }

  static String getURLForSend(String endpoint) {
    return URL_API + endpoint;
  }

  static String sendData(String url, String payload) {
    String response = "null";
    try {
      response = Unirest.post(getURLForSend(url))
          .header("authorization", authorization)
          .header("Content-Type", "application/json")
          .body(payload)
          .asString().getBody();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return response;
  }

  static String genSignature(JSONObject payload) {

    Map<String, Object> joToMap = new HashMap<>();

    Iterator<String> keysItr = payload.keys();
    while (keysItr.hasNext()) {
      String key = keysItr.next();
      Object value = payload.get(key);

      joToMap.put(key, value);
    }

    Map<String, Object> sortedMap = new TreeMap<>(joToMap); // this will auto-sort by key alphabetically

    JSONObject sortedJSON = new JSONObject(); // recreate new JSON object for sorted result

    try {
      Field changeMap = sortedJSON.getClass().getDeclaredField("map");
      changeMap.setAccessible(true);
      changeMap.set(sortedJSON, new LinkedHashMap<>());
      changeMap.setAccessible(false);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      System.out.println(e.getMessage());
    }

    String ToQueryString = "";
    for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
      sortedJSON.put(entry.getKey(), entry.getValue());
      ToQueryString += entry.getKey() + "=" + entry.getValue() + "&";
    }

    String signature = Md5Hash(ToQueryString + "key=" + secertkey);

    // System.out.println(signature);

    sortedJSON.put("signature", signature);

    return sortedJSON.toString();
  }

  private static String Md5Hash(String md5) {
    try {
      java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
      byte[] array = md.digest(md5.getBytes());
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < array.length; ++i) {
        sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
      }
      return sb.toString();
    } catch (java.security.NoSuchAlgorithmException e) {
    }
    return null;

  }
}