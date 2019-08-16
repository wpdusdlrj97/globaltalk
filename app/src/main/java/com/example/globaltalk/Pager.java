package com.example.globaltalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Belal on 2/3/2016.
 */
//Extending FragmentStatePagerAdapter
public class Pager extends FragmentStatePagerAdapter {

    Bundle bundle;

    //integer to count number of tabs
    int tabCount;

    //탭카운트와 번들객체 받기
    public Pager(FragmentManager fm, int tabCount, Bundle bundle) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
        this.bundle=bundle;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs


        switch (position) {
            case 0:
                //게시물 업로드하는 tab1에 필요한 정보 - email, name
                Tab1 tab1 = new Tab1();
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                Tab2 tab2 = new Tab2();
                return tab2;
            case 2:
                Tab3 tab3 = new Tab3();
                tab3.setArguments(bundle);
                return tab3;
            case 3:
                Tab4 tab4 = new Tab4();
                tab4.setArguments(bundle);
                return tab4;
            case 4:
                //번들 전달하기
                //프래그먼트 갱신
                Tab5 tab5 = new Tab5();
                tab5.setArguments(bundle);
                return tab5;

            case 5:
                //번들 전달하기
                //프래그먼트 갱신
                Tab6 tab6 = new Tab6();
                tab6.setArguments(bundle);
                return tab6;


            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}
