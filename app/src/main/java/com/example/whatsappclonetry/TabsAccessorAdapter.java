package com.example.whatsappclonetry;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.whatsappclonetry.Fragments.ChatsFrag;
import com.example.whatsappclonetry.Fragments.ContactsFrag;
import com.example.whatsappclonetry.Fragments.GroupsFrag;
import com.example.whatsappclonetry.Fragments.RequestFrag;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                ChatsFrag chatsFrag = new ChatsFrag();
                return chatsFrag;
            case 1:
                GroupsFrag groupsFrag = new GroupsFrag();
                return groupsFrag;
            case 2:
                ContactsFrag contactsFrag = new ContactsFrag();
                return contactsFrag;
            case 3:
                RequestFrag requestFrag = new RequestFrag();
                return requestFrag;

                default:
                    return null;
        }


    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch(position){
            case 0:
                return "Chat";
            case 1:
                return "Groups";
            case 2:
                return "Contacts";
            case 3:
                return "Requests";

            default:
                return null;
        }

    }
}
