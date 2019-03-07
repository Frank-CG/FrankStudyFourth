package com.example.frankstudyfourth;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingDialogView extends DialogFragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    Boolean audioOnly;
    String language;

    public SettingDialogView(){}

    public Boolean getAudioOnly() {
        return audioOnly;
    }

    public void setAudioOnly(Boolean audioOnly) {
        this.audioOnly = audioOnly;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    //    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        AlertDialog.Builder b=  new  AlertDialog.Builder(getActivity())
//                .setTitle("Enter Players")
//                .setPositiveButton("OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                // do something...
//                                Log.d("Dialog.OK","whichButton="+whichButton);
//                            }
//                        }
//                )
//                .setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                dialog.dismiss();
//                            }
//                        }
//                );
//
//        LayoutInflater i = getActivity().getLayoutInflater();
//
//        View rootview = i.inflate(R.layout.page_setting_dialog,null);
//
//        tabLayout = (TabLayout) rootview.findViewById(R.id.settingDialogTabLayout);
//        tabLayout.addTab(tabLayout.newTab().setText("Audio"));
//        tabLayout.addTab(tabLayout.newTab().setText("Video"));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//
//
//        viewPager = (ViewPager) rootview.findViewById(R.id.settingDialogViewPager);
//
//        SettingDialogAdapter adapter = new SettingDialogAdapter(getChildFragmentManager(),2 );
//        adapter.addFragment("Audio",SettingDialogFragment.createInstance(new String[]{"English Audio","French Audio","Floor Audio"}));
//        adapter.addFragment("Video",SettingDialogFragment.createInstance(new String[]{"English Video","French Video","Floor Video"},audioOnly));
//
//        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        viewPager.setOffscreenPageLimit(2);
//
//        tabLayout.setupWithViewPager(viewPager);
//
//        b.setView(rootview);
//        return b.create();
//    }

    private int getCurrentSelection(){
        int rst = 0;
        if(language!=null){
            switch (language){
                case "en":
                    rst = 1;
                    break;
                case "fr":
                    rst = 2;
                    break;
                case "fl":
                    rst = 3;
                    break;
                default:
                    break;
            }
        }
        return rst;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("SettingDialogView","onCreateView");
        View rootview = inflater.inflate(R.layout.page_setting_dialog,container,false);

//        tabLayout = (TabLayout) rootview.findViewById(R.id.settingDialogTabLayout);

        tabLayout = (TabLayout) rootview.findViewById(R.id.settingDialogTabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Audio"));
        tabLayout.addTab(tabLayout.newTab().setText("Video"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        viewPager = (ViewPager) rootview.findViewById(R.id.settingDialogViewPager);

        SettingDialogAdapter adapter = new SettingDialogAdapter(getChildFragmentManager(),2 );
        SettingDialogFragment settingDialogFragment = SettingDialogFragment.createInstance(new String[]{"English Audio","French Audio","Floor Audio"});
        settingDialogFragment.setCurrentSelection(getCurrentSelection());
        adapter.addFragment("Audio",settingDialogFragment);

        settingDialogFragment = SettingDialogFragment.createInstance(new String[]{"English Video","French Video","Floor Video"});
        settingDialogFragment.setDisabled(audioOnly);
        settingDialogFragment.setCurrentSelection(getCurrentSelection());
        adapter.addFragment("Video",settingDialogFragment);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(audioOnly?0:1);

//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        tabLayout.setupWithViewPager(viewPager);

        Button buttonCancel = rootview.findViewById(R.id.settingCancel);
        buttonCancel.setTag(-1);
        buttonCancel.setOnClickListener(v -> {
                Log.d("Button", "Cancel" + v.getId());
                this.getDialog().dismiss();
            }
        );

        Button buttonOK = rootview.findViewById(R.id.settingOK);
        buttonOK.setTag(-2);
        buttonOK.setOnClickListener(v -> {
            Log.d("Button","OK"+viewPager.getCurrentItem());
            int curPage = viewPager.getCurrentItem();
            SettingDialogFragment page = (SettingDialogFragment) adapter.getItem(curPage);
            int curSelection = page.getCurrentSelection();
            Log.d("SettingDialogView", String.valueOf(curSelection));
            ExoVideoPlayerActivity activity = (ExoVideoPlayerActivity) getActivity();
            activity.reInitializePlayer(curPage,curSelection);
            this.getDialog().dismiss();
        });

        return rootview;
    }
}
