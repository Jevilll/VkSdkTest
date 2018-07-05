package ru.jevil.profitest.friends_list;

import android.graphics.Bitmap;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.jevil.profitest.base.BasePresenter;
import ru.jevil.profitest.cache.BitmapCache;
import ru.jevil.profitest.download.BitmapDownloadCallback;
import ru.jevil.profitest.download.BitmapWorkerTask;
import ru.jevil.profitest.pojo.Photo;
import ru.jevil.profitest.pojo.Friend;

public class FriendsPresenter extends BasePresenter<FriendsContract.View>
        implements FriendsContract.Presenter {

    private BitmapCache mBitmapCache;
    private String photoId;

    @Override
    public void attachView(FriendsContract.View mvpView) {
        super.attachView(mvpView);
        getView().logIn();
        mBitmapCache = BitmapCache.getInstance();
    }

    @Override
    public void onSuccessAuthorization() {
        makeFriendsRequest();
    }

    @Override
    public void onFailuresAuthorization(VKError error) {
        getView().showRepeat();
    }

    @Override
    public void onRepeatClick() {
        getView().restartActivity();
    }

    @Override
    public void onExpandedViewClick() {
        getView().reduceImage();
    }

    @Override
    public void onBackClick() {
        getView().reduceImage();
    }

    @Override
    public void networkChange(boolean isConnected) {
        if (!isConnected) getView().showSnackBar();
    }

    private void makeFriendsRequest() {
        final VKRequest request = VKApi.friends().get(VKParameters.from(
                VKApiConst.FIELDS, "photo_100, photo_max_orig, photo_max, photo_200, photo_200_orig, photo_400, photo_400_orig, photo_id, online"));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                getView().setList(getFriendsList(response.json));
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                getView().showSnackBar();
            }
        });
    }

    @Override
    public void onItemClick(String bigUrl, String photoId) {
        this.photoId = photoId;
        getPhotoAdditional(photoId);

        final Bitmap cacheBitmap = mBitmapCache.getBitmapFromMemory(bigUrl);
        if (cacheBitmap != null) {
            getView().zoomIt(cacheBitmap);
        } else {
            BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(new BitmapDownloadCallback() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    getView().zoomIt(bitmap);
                }

                @Override
                public void onError(Exception e) {

                }
            });
            bitmapWorkerTask.executeOnExecutor(BitmapWorkerTask.THREAD_POOL_EXECUTOR, bigUrl);
        }
    }

    @Override
    public void tryAgain() {
        makeFriendsRequest();
    }

    @Override
    public void imageExpanded() {
        if (!photoId.equals("deactivated")) getView().showPhotoInformation();
    }

    @Override
    public void imageCollapsed() {
        if (!photoId.equals("deactivated")) getView().hidePhotoInformation();
    }

    public List<Friend> getFriendsList(JSONObject from) {
        List<Friend> friends = new ArrayList<>();
        try {
            JSONArray friendsArray = from.getJSONObject("response").getJSONArray("items");
            for (int i = 0; i < friendsArray.length(); i++) {
                friends.add(new Friend(friendsArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return friends;
    }

    private void getPhotoAdditional(String photoId) {
        final VKRequest requestPhotos = new VKRequest("photos.getById", VKParameters.from(
                VKApiConst.PHOTOS, photoId,
                VKApiConst.EXTENDED, "1"));
        requestPhotos.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    Photo photo = new Photo(response.json);
                    getView().setPhotoInformation(photo);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }
        });
    }
}
