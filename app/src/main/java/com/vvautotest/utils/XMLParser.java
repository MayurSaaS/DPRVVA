package com.vvautotest.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class XMLParser {

    public static JSONArray parseHtmlToJSON(String source) throws JSONException {
        Document doc = Jsoup.parse(source);

        Elements links = doc.select("a[href]"); // a with href
        JSONArray jsonParentArray = new JSONArray();
        L.printError("Links : "+ links);
        for (Element table : links) {
            Elements tds = table.select("a");
            String linkText = tds.get(0).text();
            L.printError("Links text : "+ linkText);
            if(linkText.contains(".jpg"))
            {
                JSONObject ob = new JSONObject();
                ob.put("file", linkText);
                jsonParentArray.put(ob);
            }
        }
        return jsonParentArray;
    }

    public static JSONArray parseHtmlToJSON2(String source) throws JSONException {
        Document doc = Jsoup.parse(source);

        Elements links = doc.select("a[href]"); // a with href
        L.printError("Links : "+ links);
        JSONArray jsonParentArray = new JSONArray();
        int count = 0;
        for (Element table : links) {
            Elements tds = table.select("a");
            String linkText = tds.get(0).text();
            L.printError("Links text : "+ linkText);
            if(count > 0)
            {
                JSONObject ob = new JSONObject();
                ob.put("file", linkText);
                jsonParentArray.put(ob);
            }
            count ++;
        }
        return jsonParentArray;
    }
}
