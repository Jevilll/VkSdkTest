package ru.jevil.profitest.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class Photo {

    private String likes;
    private String comments;
    private String reposts;

    public Photo(JSONObject from) throws JSONException {

        JSONObject response = from.getJSONArray("response").getJSONObject(0);

        likes = String.valueOf(parseFromInt(response, "likes"));
        comments = String.valueOf(parseFromInt(response, "comments"));
        reposts = String.valueOf(parseFromInt(response, "reposts"));
    }

    public Photo(String likes, String comments, String reposts) {
        this.likes = likes;
        this.comments = comments;
        this.reposts = reposts;
    }

    public String getLikes() {
        if (likes.equals("0")) return "";
        else
        return likes;
    }

    public String getComments() {
        if (comments.equals("0")) return "";
        else
        return comments;
    }

    public String getReposts() {
        if (reposts.equals("0")) return "";
        else
        return reposts;
    }

    private int parseFromInt(JSONObject from, String name) {
        try {
            return from.getJSONObject(name).getInt("count");
        } catch (JSONException e) {
            return 0;
        }
    }
}
