package ru.jevil.profitest.friends_list;

import android.graphics.Bitmap;

import com.vk.sdk.api.VKError;

import java.util.List;

import ru.jevil.profitest.base.MvpPresenter;
import ru.jevil.profitest.base.MvpView;
import ru.jevil.profitest.pojo.Photo;
import ru.jevil.profitest.pojo.Friend;

public interface FriendsContract {

    interface View extends MvpView {

        void logIn();
        void setList(List<Friend> friendsList);
        void zoomIt(Bitmap bitmap);
        void setPhotoInformation(Photo photoInformation);
        void reduceImage();
        void showPhotoInformation();
        void hidePhotoInformation();
        void restartActivity();
        void showRepeat();
        void showSnackBar();
    }

    interface Presenter extends MvpPresenter<View> {

        void onSuccessAuthorization();
        void onFailuresAuthorization(VKError error);
        void onExpandedViewClick();
        void onBackClick();
        void onItemClick(String bigUrl, String photoId);
        void imageExpanded();
        void imageCollapsed();
        void onRepeatClick();
        void networkChange(boolean isConnected);
        void tryAgain();
    }

}
