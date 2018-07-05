package ru.jevil.profitest.friends_list;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.util.List;

import ru.jevil.profitest.pojo.Friend;
import ru.jevil.profitest.adapters.FriendsAdapter;
import ru.jevil.profitest.R;
import ru.jevil.profitest.pojo.Photo;


public class FriendsActivity extends AppCompatActivity
        implements FriendsContract.View,
        FriendsAdapter.OnFriendClickListener {
    private String[] scope = new String[]{VKScope.FRIENDS, VKScope.PHOTOS};
    private Group groupInfo, groupAccess;
    private Button btnRepeat;
    private TextView tvLikes, tvComments, tvReposts;
    private ImageView ivExpanded;
    FriendsContract.Presenter mPresenter;
    Context context;
    View imageBack;
    private Animator mCurrentAnimator;
    FriendsAdapter.ViewHolder currentHolder;
    private BroadcastReceiver networkStateReceiver;
    ConstraintLayout container;
    private float startScaleFinal;
    private Rect startBounds;
    private int mAnimationDuration = 400;
    private View thumbView;
    private FriendsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friends_list_main);
        context = this;
        mPresenter = new FriendsPresenter();
        mPresenter.attachView(this);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        currentHolder = null;
        ivExpanded.setOnClickListener(null);
        btnRepeat.setOnClickListener(null);
        unregisterReceiver();
    }

    @Override
    public void logIn() {
        VKSdk.login(this, scope);
    }

    @Override
    public void setList(List<Friend> friendsList) {
        adapter.update(friendsList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                mPresenter.onSuccessAuthorization();
                registerReceiver();
            }

            @Override
            public void onError(VKError error) {
                mPresenter.onFailuresAuthorization(error);
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void registerReceiver() {
        networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.networkChange(isConnected(context));
            }
        };
        registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void unregisterReceiver() {
        unregisterReceiver(networkStateReceiver);
        networkStateReceiver = null;
    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnected();
    }

    @Override
    public void showSnackBar() {
        Snackbar snackbar = Snackbar.make(container, getString(R.string.connectionLost), Snackbar.LENGTH_SHORT);
            snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.repeat), v -> mPresenter.tryAgain());
        snackbar.show();
    }

    @Override
    public void onClick(final FriendsAdapter.ViewHolder holder, final String bigUrl, String photoId) {
        currentHolder = holder;
        mPresenter.onItemClick(bigUrl, photoId);
    }

    @Override
    public void restartActivity() {
        this.recreate();
    }

    @Override
    public void zoomIt(Bitmap bitmap) {
        currentHolder.pb.setVisibility(View.GONE);
        zoomThumb(currentHolder.ivThumb, bitmap);
    }

    @Override
    public void setPhotoInformation(Photo photoInformation) {
        tvLikes.setText(photoInformation.getLikes());
        tvComments.setText(photoInformation.getComments());
        tvReposts.setText(photoInformation.getReposts());
    }

    @Override
    public void showPhotoInformation() {
        groupInfo.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePhotoInformation() {
        groupInfo.setVisibility(View.GONE);
    }

    private void initView() {
        ivExpanded = findViewById(R.id.ivExpanded);
        imageBack = findViewById(R.id.imageBack);
        groupInfo = findViewById(R.id.groupInfo);
        groupAccess = findViewById(R.id.groupNoAccess);
        tvLikes = findViewById(R.id.tvLikes);
        tvComments = findViewById(R.id.tvComments);
        tvReposts = findViewById(R.id.tvReposts);
        btnRepeat = findViewById(R.id.btnRepeat);
        btnRepeat.setOnClickListener(clickListener);
        RecyclerView mRecycler = findViewById(R.id.rvFriends);
        container = findViewById(R.id.container);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FriendsAdapter((FriendsAdapter.OnFriendClickListener) context);
        mRecycler.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (imageBack.getVisibility() == View.VISIBLE) {
            mPresenter.onBackClick();
        } else super.onBackPressed();
    }

    @Override
    public void reduceImage() {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        groupInfo.setVisibility(View.GONE);
        AnimatorSet set1 = new AnimatorSet();
        set1.play(ObjectAnimator
                .ofFloat(ivExpanded, View.X, startBounds.left))
                .with(ObjectAnimator.ofFloat(imageBack, View.ALPHA, 1f, 0f))
                .with(ObjectAnimator
                        .ofFloat(ivExpanded,
                                View.Y, startBounds.top))
                .with(ObjectAnimator
                        .ofFloat(ivExpanded,
                                View.SCALE_X, startScaleFinal))
                .with(ObjectAnimator
                        .ofFloat(ivExpanded,
                                View.SCALE_Y, startScaleFinal));
        set1.setDuration(mAnimationDuration);
        set1.setInterpolator(new DecelerateInterpolator());
        set1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                reduceAnimEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                reduceAnimEnd();
            }
        });
        set1.start();

        mCurrentAnimator = set1;
        ivExpanded.setOnClickListener(null);
    }

    private void reduceAnimEnd() {
        thumbView.setAlpha(1f);
        ivExpanded.setVisibility(View.GONE);
        mCurrentAnimator = null;
        imageBack.setVisibility(View.GONE);
        mPresenter.imageCollapsed();
    }

    @Override
    public void showRepeat() {
        groupAccess.setVisibility(View.VISIBLE);
    }

    private void zoomThumb(final View thumbView, Bitmap imageResId) {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        this.thumbView = thumbView;
        ivExpanded.setImageBitmap(imageResId);
        imageBack.setVisibility(View.VISIBLE);
        startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumbView.getGlobalVisibleRect(startBounds);

        container.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        thumbView.setAlpha(0f);
        ivExpanded.setVisibility(View.VISIBLE);
        ivExpanded.setPivotX(0f);
        ivExpanded.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(ivExpanded, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(ivExpanded, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(ivExpanded, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(imageBack, View.ALPHA, 0f, 1f))
                .with(ObjectAnimator.ofFloat(ivExpanded,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(mAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                zoomAnimEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                zoomAnimEnd();
            }
        });
        set.start();

        mCurrentAnimator = set;
        startScaleFinal = startScale;
        ivExpanded.setOnClickListener(clickListener);
    }

    private void zoomAnimEnd() {
        mPresenter.imageExpanded();
        mCurrentAnimator = null;
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivExpanded:
                    mPresenter.onExpandedViewClick();
                    break;
                case R.id.btnRepeat:
                    mPresenter.onRepeatClick();
                    break;
            }
        }
    };

}