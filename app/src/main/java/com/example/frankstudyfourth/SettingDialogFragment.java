package com.example.frankstudyfourth;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;

public class SettingDialogFragment extends Fragment {
    private String[] options;
    private CheckedTextView[] optionsList;
    private Boolean[] optionsStatus;
    private Boolean isDisabled;

    private int currentSelection;
    private int optionStart;

    public SettingDialogFragment(){
    }

    public Boolean getDisabled() {
        return isDisabled;
    }

    public void setDisabled(Boolean disabled) {
        isDisabled = disabled;
    }

    public int getCurrentSelection() {
        return currentSelection;
    }

    public void setCurrentSelection(int currentSelection) {
        this.currentSelection = currentSelection;
    }

    public static SettingDialogFragment createInstance(String[] options)
    {
        Log.d("SettingDialogFragment","createInstance");
        SettingDialogFragment fragment = new SettingDialogFragment();
        fragment.options = new String[options.length+1];
        fragment.options[0] = "Auto";
        for(int i=0; i<options.length; i++){
            fragment.options[i+1] = options[i];
        }
        fragment.optionsList = new CheckedTextView[options.length+1];
        fragment.optionsStatus = new Boolean[options.length+1];
        fragment.isDisabled = false;
        fragment.optionStart = 1;
        fragment.currentSelection = 1;
        return fragment;
    }

    public static SettingDialogFragment createInstance(String[] options,Boolean isDisabled)
    {
        SettingDialogFragment fragment = new SettingDialogFragment();
//        fragment.mText = txt;
        fragment.options = new String[options.length+1];
        fragment.options[0] = "Auto";
        for(int i=0; i<options.length; i++){
            fragment.options[i+1] = options[i];
        }
        fragment.optionsList = new CheckedTextView[options.length+1];
        fragment.optionsStatus = new Boolean[options.length+1];
        fragment.isDisabled = isDisabled;
        fragment.optionStart = 1;
        fragment.currentSelection = 1;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("SettingDialogFragment", "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("SettingDialogFragment","onCreateView");
        View v = inflater.inflate(R.layout.fragment_setting_dialog,container,false);
//        ((TextView) v.findViewById(R.id.textView)).setText(mText);

        ViewGroup dialogList = (ViewGroup) v.findViewById(R.id.dialogList);
        TypedArray attributeArray =
                getContext()
                        .getTheme()
                        .obtainStyledAttributes(new int[] {android.R.attr.selectableItemBackground});
        int selectableItemBackgroundResourceId = attributeArray.getResourceId(0, 0);


        ComponentListener componentListener = new ComponentListener();
        for(int i=optionStart; i<options.length; i++){

            Log.d("onCreateView", options[i]);
            CheckedTextView option = (CheckedTextView)
                    inflater.inflate(android.R.layout.simple_list_item_single_choice, container, false);
            option.setBackgroundResource(selectableItemBackgroundResourceId);
            option.setText(options[i]);
            option.setTag(i);
            option.setEnabled(!isDisabled);
            option.setFocusable(true);
            option.setVisibility(View.VISIBLE);
//            option.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


            option.setOnClickListener(componentListener);

            optionsList[i] = option;
            optionsStatus[i] = false;

            dialogList.addView(option);
        }
        updateViewStates(currentSelection);


        return v;
    }

    private void updateViewStates(int index) {
        for(int i=optionStart; i<optionsList.length; i++){
            if(optionsStatus[i]){
                optionsList[i].setChecked(false);
                optionsStatus[i] = false;
            }
        }
        optionsList[index].setChecked(true);
        optionsStatus[index] = true;
    }

    private class ComponentListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            currentSelection = (int) view.getTag();
            Log.d("Click", String.valueOf(currentSelection));
            updateViewStates(currentSelection);
        }
    }
}
