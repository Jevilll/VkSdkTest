package ru.jevil.profitest.pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Friend {

    private String name;
    private String photoThumbnail;
    private String photoMax;
    private String photoId;
    private String online = "";

    public Friend(JSONObject from) {

        try {
            name = from.getString("first_name") + " " + from.getString("last_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            photoThumbnail = from.getString("photo_100");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            photoMax = from.getString("photo_max_orig");//можно поменять на photo_max, ниже качество, но анимация будет ровнее (трансформация квадрат в квадрат)
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            photoId = from.getString("photo_id");
        } catch (JSONException e) {
            photoId = "deactivated";
        }

        try {
            int onlineInt = from.getInt("online");
            if (onlineInt == 1) {
                try {
                    from.getInt("online_mobile");
                    online = "Online mobile";
                } catch (JSONException e) {
                    online = "Online";
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public Friend(String name, String photoThumbnail, String photoMax, String photoId, String online) {
        this.name = name;
        this.photoThumbnail = photoThumbnail;
        this.photoMax = photoMax;
        this.photoId = photoId;
        this.online = online;
    }

    public String getName() {
        return name;
    }

    public String getPhotoThumbnail() {
        return photoThumbnail;
    }

    public String getPhotoMax() {
        return photoMax;
    }

    public String getPhotoId() {
        return photoId;
    }

    public String getOnline() {
        return online;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Friend)) {
            return false;
        }

        Friend that = (Friend) other;

        // Custom equality check here.
        return this.name.equals(that.name)
                && this.photoThumbnail.equals(that.photoThumbnail)
                && this.photoMax.equals(that.photoMax)
                && this.photoId.equals(that.photoId)
                && this.online.equals(that.online);
    }

    @Override
    public int hashCode() {
        int hashCode = 1;

        hashCode = hashCode * 37 + this.name.hashCode();
        hashCode = hashCode * 37 + this.photoThumbnail.hashCode();
        hashCode = hashCode * 37 + this.photoMax.hashCode();
        hashCode = hashCode * 37 + this.photoId.hashCode();
        hashCode = hashCode * 37 + this.online.hashCode();

        return hashCode;
    }
}
