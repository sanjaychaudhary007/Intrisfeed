package com.archi.intrisfeed.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by archi on 12/19/2016.
 */

public class IntrisfeedDetail
{
    ArrayList<HashMap<String, String>> videoImageFeedList,allFeedList,imageFeedList,videoFeedList,AudioFeedList,BlogsFeedList,ArticalsFeedList,videoImageArticalesFeedList;


    public ArrayList<HashMap<String, String>> getVideoImageArticalesFeedList() {
        return videoImageArticalesFeedList;
    }

    public void setVideoImageArticalesFeedList(ArrayList<HashMap<String, String>> videoImageArticalesFeedList) {
        this.videoImageArticalesFeedList = videoImageArticalesFeedList;
    }

    public ArrayList<HashMap<String, String>> getVideoImageFeedList() {
        return videoImageFeedList;
    }

    public void setVideoImageFeedList(ArrayList<HashMap<String, String>> videoImageFeedList) {
        this.videoImageFeedList = videoImageFeedList;
    }

    public ArrayList<HashMap<String, String>> getAllFeedList() {
        return allFeedList;
    }

    public void setAllFeedList(ArrayList<HashMap<String, String>> allFeedList) {
        this.allFeedList = allFeedList;
    }

    public ArrayList<HashMap<String, String>> getImageFeedList() {
        return imageFeedList;
    }

    public void setImageFeedList(ArrayList<HashMap<String, String>> imageFeedList) {
        this.imageFeedList = imageFeedList;
    }


    public ArrayList<HashMap<String, String>> getVideoFeedList() {
        return videoFeedList;
    }

    public void setVideoFeedList(ArrayList<HashMap<String, String>> videoFeedList) {
        this.videoFeedList = videoFeedList;
    }

    public ArrayList<HashMap<String, String>> getAudioFeedList() {
        return AudioFeedList;
    }

    public void setAudioFeedList(ArrayList<HashMap<String, String>> audioFeedList) {
        AudioFeedList = audioFeedList;
    }

    public ArrayList<HashMap<String, String>> getBlogsFeedList() {
        return BlogsFeedList;
    }

    public void setBlogsFeedList(ArrayList<HashMap<String, String>> blogsFeedList) {
        BlogsFeedList = blogsFeedList;
    }

    public ArrayList<HashMap<String, String>> getArticalsFeedList() {
        return ArticalsFeedList;
    }

    public void setArticalsFeedList(ArrayList<HashMap<String, String>> articalsFeedList) {
        ArticalsFeedList = articalsFeedList;
    }
}
