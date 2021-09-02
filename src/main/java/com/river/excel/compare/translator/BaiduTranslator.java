package com.river.excel.compare.translator;

import com.google.gson.Gson;
import com.river.excel.model.BdResult;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class BaiduTranslator implements Translator {
    final String APP_ID = "20210623000870214";

    final String SECRET_KEY = "MhLVvDBxYWev6Yw8yhDC";

    final int SALT = 77;

    public String translate(String langFrom, String langTo, String word) throws Exception {
        String url = "https://fanyi-api.baidu.com/api/trans/vip/translate?" +
                "from=" + langFrom +
                "&to=" + langTo +
                "&q=" + URLEncoder.encode(word, "UTF-8") +
                "&appid=" + APP_ID +
                "&salt=" + SALT +
                "&sign=" + getSignature(APP_ID, SECRET_KEY, word, SALT);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return parseResult(response.toString());
    }

    public String parseResult(String inputJson) {
        System.out.println(inputJson);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        BdResult bdResult = gson.fromJson(inputJson, BdResult.class);
        if (bdResult.getTrans_result().size() > 0) {
            return bdResult.getTrans_result().get(0).getDst();
        }

        return "";
    }

    private String getSignature(String appId, String secretKey, String query, int salt) {
        return DigestUtils.md5Hex(appId + query + salt + secretKey);
    }
}