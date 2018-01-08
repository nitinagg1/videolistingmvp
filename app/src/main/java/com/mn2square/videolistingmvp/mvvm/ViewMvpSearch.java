package com.mn2square.videolistingmvp.mvvm;

import android.view.MenuItem;

/**
 * Created by nitinagarwal on 3/5/17.
 */

public interface ViewMvpSearch {

    public interface SearchVideo
    {
        public void onVideoSearched(String seachText);
    }

    void SetSearchListener(SearchVideo searchListener);

    void AddSearchBar(MenuItem searchViewMenuItem);

    void searchClose();

}
