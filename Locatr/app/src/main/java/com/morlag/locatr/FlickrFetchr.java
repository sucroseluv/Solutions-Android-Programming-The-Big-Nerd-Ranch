package com.morlag.locatr;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "08f54eb5ae9295b378eb74969c74ad3b";
    public static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    public static final String SEARCH_METHOD = "flickr.photos.search";
    public static final Uri ENDPOINT_URI = Uri
                    .parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s,geo")
                    .build();

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                String redirect = null;
                if((redirect = connection.getHeaderField("Location")) != null){
                    return getUrlBytes(redirect);
                }

                throw new IOException(connection.getResponseMessage()
                        + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0){
                out.write(buffer,0,bytesRead);
            }
            out.close();
            return out.toByteArray();
        }
        finally {
            connection.disconnect();
        }
    }
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    private List<GalleryItem> downloadGalleryItems(String url){
        List<GalleryItem> resultArray = new ArrayList<>();
        try{
            String jsonString = getUrlString(url);
            Log.i(TAG, jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(resultArray, jsonBody);
        }
        catch (IOException ex){
            Log.e(TAG,"Failed to fetch items", ex);
        }
        catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return resultArray;
    }

    public List<GalleryItem> fetchRecentPhotos() {
        String url = buildUrl(FETCH_RECENTS_METHOD, null);
        return downloadGalleryItems(url);
    }
    public List<GalleryItem> searchPhotos(String query) {
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItems(url);
    }
    public List<GalleryItem> searchPhotos(Location location){
        String url = buildUrl(location);
        return downloadGalleryItems(url);
    }

    public String buildUrl(String method, String query){
        Uri.Builder uri = ENDPOINT_URI.buildUpon().appendQueryParameter("method",method);

        if(method.equals(SEARCH_METHOD))
            uri.appendQueryParameter("text",query);

        return uri.build().toString();
    }
    public String buildUrl(Location location){
        return ENDPOINT_URI.buildUpon()
                .appendQueryParameter("method",SEARCH_METHOD)
                .appendQueryParameter("lat","" + location.getLatitude())
                .appendQueryParameter("lon","" + location.getLongitude())
                .build().toString();
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody)
        throws JSONException{
        String json = jsonBody.getJSONObject("photos").getJSONArray("photo").toString();
        Gson gson = new Gson();
        GalleryItem[] temp = gson.fromJson(json, GalleryItem[].class);
        items.addAll(Arrays.asList(temp));
        /*
        for (int i = 0; i < array.length(); i++) {
            GalleryItem item = new GalleryItem();
            JSONObject obj = array.getJSONObject(i);

            item.setCaption(obj.getString("title"));
            item.setId(obj.getString("id"));

            if (!obj.has("url_s")) {
                continue;
            }
            item.setUrl(obj.getString("url_s"));
            items.add(item);
        }*/
    }

}
