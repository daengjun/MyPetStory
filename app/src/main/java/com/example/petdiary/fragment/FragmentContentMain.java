package com.example.petdiary.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.petdiary.R;
import com.example.petdiary.activity.MainActivity;
import com.example.petdiary.activity.SettingBlockFriendsActivity;
import com.example.petdiary.activity.SettingBookMarkActivity;

public class FragmentContentMain extends Fragment implements com.example.petdiary.calbacklistener {

    ViewGroup viewGroup;
    boolean contentCheck;
    boolean oncrate = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_content_main, container, false);
        oncrate = true;
        SettingBookMarkActivity.setlistener(this);
        SettingBlockFriendsActivity.setlistener(this);
        return viewGroup;
    }

    public void refresh(boolean check){

        /*메인에서만 check가 false면 게시글 삭제 , 북마크 최신업데이트 true면 게시글 수정 업데이트 */
        if(check==false){
            ((MainActivity)getActivity()).refresh(check);
        }
        contentCheck = check;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(oncrate){
            if(contentCheck){
                ((MainActivity)getActivity()).refresh(contentCheck);
                contentCheck = false;
            }
        }
    }
}
