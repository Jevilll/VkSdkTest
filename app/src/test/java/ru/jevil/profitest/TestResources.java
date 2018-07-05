package ru.jevil.profitest;

import org.json.JSONException;
import org.json.JSONObject;

public class TestResources {

    private String testFriendsList = "{\"response\":{\"count\":2,\"items\":[{\"id\":2242039,\"first_name\":\"Иван\",\"last_name\":\"Иванов\",\"photo_100\":\"https:photo_100\",\"photo_200\":\"https:photo_200\",\"photo_400_orig\":\"https:photo_400_orig\",\"photo_max_orig\":\"https:photo_max_orig\",\"photo_id\":\"2242039_456239020\",\"online\":0},{\"id\":4487777,\"first_name\":\"Петр\",\"last_name\":\"Петров\",\"photo_100\":\"https:photo_100\",\"photo_200\":\"https:photo_200\",\"photo_400_orig\":\"https:photo_400_orig\",\"photo_max_orig\":\"https:photo_max_orig\",\"photo_id\":\"4487777_456239232\",\"online\":1, \"online_mobile\": 1 }]}}";
    private String testPhoto = "{\n" +
            "  \"response\": [\n" +
            "    {\n" +
            "      \"likes\": {\n" +
            "        \"user_likes\": 0,\n" +
            "        \"count\": 186\n" +
            "      },\n" +
            "      \"reposts\": {\n" +
            "        \"count\": 0,\n" +
            "        \"user_reposted\": 0\n" +
            "      },\n" +
            "      \"comments\": {\n" +
            "        \"count\": 20\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";


    public String getTestFriendsList() {
        return testFriendsList;
    }

    public String getTestPhoto() {
        return testPhoto;
    }
}
