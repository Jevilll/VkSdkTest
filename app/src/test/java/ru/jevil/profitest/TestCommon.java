package ru.jevil.profitest;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import ru.jevil.profitest.friends_list.FriendsPresenter;
import ru.jevil.profitest.pojo.Friend;
import ru.jevil.profitest.pojo.Photo;


@RunWith(JUnit4.class)
public class TestCommon {

    private FriendsPresenter mPresenter;
    private List<Friend> friendList;
    private TestResources mTestResources;
    private Photo photo;

    @Before
    public void setUp() {
        mPresenter = new FriendsPresenter();
        friendList = new ArrayList<>();
        mTestResources = new TestResources();
        friendList.add(new Friend("Иван Иванов", "https:photo_100", "https:photo_max_orig", "2242039_456239020", ""));
        friendList.add(new Friend("Петр Петров", "https:photo_100", "https:photo_max_orig", "4487777_456239232", "Online mobile"));

        photo = new Photo("186", "20", "0");

    }

    @Test
    public void testGetFriendsList() throws Exception {
        Assert.assertArrayEquals(friendList.toArray(), mPresenter.getFriendsList(new JSONObject(mTestResources.getTestFriendsList())).toArray());
    }

    @Test
    public void testCreatePhoto() throws Exception{
        Assert.assertEquals(photo.getReposts(), new Photo(new JSONObject(mTestResources.getTestPhoto())).getReposts());
    }

    @After
    public void tearDown() {
        mPresenter = null;
        friendList = null;
        mTestResources = null;
    }

}
