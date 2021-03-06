package com.example.petdiary.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.petdiary.Data;
import com.example.petdiary.Kon_MypageAdapter;
import com.example.petdiary.Kon_Mypage_petAdapter;
import com.example.petdiary.PetData;
import com.example.petdiary.RecyclerDecoration;
import com.example.petdiary.activity.*;
import com.bumptech.glide.Glide;
import com.example.petdiary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;

import static android.app.Activity.RESULT_OK;


public class FragmentMy extends Fragment implements com.example.petdiary.calbacklistener{

    private static final String TAG = "MyPage_Fragment";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BottomNavigationView bottomNavigationView;
    private ViewGroup viewGroup;

    TextView profileName;
    TextView profileMemo;
    String profileImgName;
    ImageView profileEditImg;
    boolean contentCheck;

    Map<String, String> userInfo = new HashMap<>();   // ?????? ????????? ???????????? ????????? ???????????????
    //Map<String, String> petInfo = new HashMap<>();
    ArrayList<Data> postList = new ArrayList<Data>();
    ArrayList<Data> selectedPostList = new ArrayList<Data>();
    ArrayList<PetData> petList = new ArrayList<PetData>();
    int listCount = 0;


    // ?????? ??????????????? ??????
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;


    // ??? ?????? ??????????????? ??????
    RecyclerView petRecyclerView;
    RecyclerView.Adapter petAdapter;
    String choicePetId;

    @Override
    public void refresh(boolean check) {
        if(check==false) {
            ((MainActivity) getActivity()).refresh(check);
        }
        contentCheck = check;
    }


    public interface StringCallback {
        void callback(String choice);

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewGroup = (ViewGroup) inflater.inflate(R.layout.kon_fragment_mypage, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) viewGroup.findViewById(R.id.swipe_layout);
        profileEditImg = viewGroup.findViewById(R.id.profile_image);
        profileName = viewGroup.findViewById(R.id.profile_name);
        profileMemo = viewGroup.findViewById(R.id.profile_memo);

        ImageView petAddBtn = viewGroup.findViewById(R.id.profile_petAddBtn);
        final ImageView profileImage = viewGroup.findViewById(R.id.profile_image);

        SettingBookMarkActivity.setlistener(this);
        SettingBlockFriendsActivity.setlistener(this);

        //////////////////////////////////// ?????? ?????? ????????????
        getUserInfo();

        //////////////////////////////////// ???????????? ?????? ????????????
        getPetInfo();

        //////////////////////////////////// ????????? ?????? ????????????
        loadPostsAfterCheck(false);

        //////////////////////////////////// ???????????? ?????????????????? setting
//        setPetRecyclerView();

        //////////////////////////////////// ?????? ?????????????????? setting
        setPicRecyclerView();

        //////////////////////////////////// ????????? ????????? ?????? ????????? ??????
        profileImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //  setImg();
                setProfileImg(profileImgName);
                String userId = "IAmTarget";//"IAmUser"
                String targetId = "IAmTarget";

                Intent intent = new Intent(getContext(), ProfileEditActivity.class);
                intent.putExtra("targetId", targetId);
                intent.putExtra("userId", userId);
                intent.putExtra("userImage", profileImgName);// userInfo.get("profileImg")); // ????????? ?????? ?????????
                intent.putExtra("userName", profileName.getText().toString());// userInfo.get("nickName"));//userName.getText().toString());
                intent.putExtra("userMemo", profileMemo.getText().toString());//userInfo.get("memo"));//userMemo.getText().toString());

                startActivityForResult(intent, 0);
                return true;
            }


        });

        //////////////////////////////////// ?????? ?????? ??????
        //TextView allBtn = viewGroup.findViewById(R.id.profile_allBtn);
//        allBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // ??? ?????????????????? ????????? ?????? ????????? ?????? ??????
//            }
//        });


        // ??? ?????? ??????
        petAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), kon_AnimalProfileActivity.class);
                intent.putExtra("isAddMode", true);
                intent.putExtra("isEditMode", false);
                intent.putExtra("petId","");
                intent.putExtra("petMaster","");
                intent.putExtra("userId","");
                intent.putExtra("petImage","");

                //startActivity(intent);
                startActivityForResult(intent, 1);

                Log.d(TAG, "onClick: ?????????????");
            }
        });

        // ??????????????? ????????????
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postList.clear();
                loadPostsAfterCheck(false);
                mSwipeRefreshLayout.setRefreshing(false);  // ?????? ??????????????? ?????????

            }
        });

        //////////////////////////////////// ??????????????? ?????? ????????? ??????
        moveTop();

        return viewGroup;
    }


    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 0:  //????????? ?????? // ProfileEditActivity?????? ????????? ???
                if (resultCode == RESULT_OK) {
                    setProfileImg(data.getStringExtra("profileImg"));
                    profileName.setText(data.getStringExtra("nickName"));
                    profileMemo.setText(data.getStringExtra("memo"));
                    ImageView hambugerProfileImg = getActivity().findViewById(R.id.genter_icon);
                    Glide.with(this).load(data.getStringExtra("profileImg")).centerCrop().override(500).into(hambugerProfileImg);

                } else {
                }
                break;
            case 1: // ??? ??????, ??????
                if (resultCode == RESULT_OK) {
                    getPetInfo();
                }
                break;

        }
    }

    //////////////////////////////////// ????????? ?????????, ?????????, ?????? ????????????,
    private void getUserInfo() {
        //  ??????
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    userInfo.put("nickName", document.getString("nickName"));
                    userInfo.put("profileImg", document.getString("profileImg"));
                    userInfo.put("memo", document.getString("memo"));

                    profileName.setText(userInfo.get(("nickName")));
                    profileMemo.setText(userInfo.get(("memo")));
                    profileImgName = document.getString("profileImg");
                    if(profileImgName.length() > 0){
                        setProfileImg(profileImgName);
                    }
                    //setImg();
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }


    //////////////////////////////////// ??? ?????? ??????
    private void getPetInfo() {
        Log.d("??????????????????~~~~", "??? ?????? ?????? ??????" );
        //  ??????
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("pets").document(uid).collection("pets")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            petList.clear();

                            Log.d(TAG, "onComplete: ???????");
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map<String, Object> data = document.getData();
                                // ?????? ????????? ??????
                                PetData pet = new PetData(
                                        document.getId(),
                                        data.get("petName").toString(),
                                        data.get("profileImg").toString(),
                                        data.get("petMemo").toString(),
                                        data.get("master").toString());
                                petList.add(pet);

                                Log.d(TAG, "onComplete: ??????????????? ???????" + petList.size());

                            }
//                            petAdapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        setPetRecyclerView();
                        Log.d("??????????????????~~~~", "????????????: ");
                        petAdapter.notifyDataSetChanged();
                        Log.d("??????????????????~~~~", "?????????: ");
                    }
                });

    }


    //////////////////////////////////// ?????? ????????? ??????. ???????????? ?????? ?????? ????????? ????????? ????????? ????????????,
    //////////////////////////////////// ?????? ????????? ?????? ????????????
    private void loadPostsAfterCheck(final boolean needCheck) {
        //  ??????
        postList.clear();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("post").whereEqualTo("uid", uid);
        //query.get
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
//                    postList.clear();
                    int resultCount = task.getResult().size();
                    if (needCheck)
                        if (listCount == resultCount)
                            return;

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Data dataList = new Data();
                        dataList.setPostID(document.getId());
                        dataList.setUid(document.getData().get("uid").toString());
                        dataList.setContent(document.getData().get("content").toString());
                        dataList.setDate(document.getData().get("date").toString());
                        dataList.setImageUrl1(document.getData().get("imageUrl1").toString());
                        dataList.setImageUrl2(document.getData().get("imageUrl2").toString());
                        dataList.setImageUrl3(document.getData().get("imageUrl3").toString());
                        dataList.setImageUrl4(document.getData().get("imageUrl4").toString());
                        dataList.setImageUrl5(document.getData().get("imageUrl5").toString());
                        dataList.setNickName(document.getData().get("nickName").toString());
                        dataList.setFavoriteCount(Integer.parseInt(document.getData().get("favoriteCount").toString()));
                        postList.add(0, dataList);
                    }
                    adapter.notifyDataSetChanged();
                    listCount = resultCount;

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }


    ////////////////////////////////////  ?????? ??? ????????? ??????
    private void loadSelectedPosts(String petId) {
        //  ??????
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("post").whereEqualTo("petsID", petId);
        //query.get
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int resultCount = task.getResult().size();
                    postList.clear();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Data dataList = new Data();
                        dataList.setPostID(document.getId());
                        Log.d(TAG, "onComplete: getid"+document.getId());
                        dataList.setUid(document.getData().get("uid").toString());
                        dataList.setDate(document.getData().get("date").toString());
                        dataList.setContent(document.getData().get("content").toString());
                        dataList.setImageUrl1(document.getData().get("imageUrl1").toString());
                        dataList.setImageUrl2(document.getData().get("imageUrl2").toString());
                        dataList.setImageUrl3(document.getData().get("imageUrl3").toString());
                        dataList.setImageUrl4(document.getData().get("imageUrl4").toString());
                        dataList.setImageUrl5(document.getData().get("imageUrl5").toString());
                        dataList.setNickName(document.getData().get("nickName").toString());
                        postList.add(0, dataList);
                    }
                    adapter.notifyDataSetChanged();
                    //listCount = resultCount;

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }

    private void startToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void setProfileImg(String profileImg) {
        Glide.with(this).load(profileImg).centerCrop().override(500).into(profileEditImg);
    }

    //////////////////////////////////// ??????????????? ?????? ????????? ??????
    private void moveTop() {
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.tab4) {
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });
    }

    //////////////////////////////////// ?????? ?????????????????? setting
    private void setPicRecyclerView() {
        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // ?????????????????? ???????????? ??????

        int columnNum = 3;
        adapter = new Kon_MypageAdapter(postList, columnNum, getContext(),this);
//        Log.d(TAG, "setPicRecyclerView: ?????? ????????? ?????????????"+postList.get(0).getPostID());
        recyclerView.setAdapter(adapter); // ????????????????????? ????????? ??????
        layoutManager = new GridLayoutManager(getContext(), columnNum);
        recyclerView.setLayoutManager(layoutManager);

        // ?????????????????? ????????????
        RecyclerDecoration spaceDecoration = new RecyclerDecoration(10);
        recyclerView.addItemDecoration(spaceDecoration);
    }

    //////////////////////////////////// ??? ?????????????????? setting
    private void setPetRecyclerView() {

        Log.d(TAG, "setPetRecyclerView: petList ????????????" + petList.size());
        petRecyclerView = (RecyclerView) viewGroup.findViewById(R.id.pet_recyclerView);
        petRecyclerView.setHasFixedSize(true); // ?????????????????? ???????????? ??????

        int columnNum = 3;
        petAdapter = new Kon_Mypage_petAdapter(petList, getContext(), getActivity(), new StringCallback() {
            @Override
            public void callback(String choice) {
                choicePetId = choice;
                /* ?????? ???????????? ?????? ?????? ????????? ???????????? */
                ((MainActivity)getActivity()).refresh(true);
                if (choice.equals(""))
                    loadPostsAfterCheck(false);
                else
                    loadSelectedPosts(choicePetId);
            }
        });
        petRecyclerView.setAdapter(petAdapter); // ????????????????????? ????????? ??????
        //layoutManager = new GridLayoutManager(getContext(), columnNum);
        //petRecyclerView.setLayoutManager(layoutManager);
        petAdapter.notifyDataSetChanged();

        // ?????????????????? ????????????
        //RecyclerDecoration spaceDecoration = new RecyclerDecoration(10);
        // petRecyclerView.addItemDecoration(spaceDecoration);
    }


    //////////////////////////////////// ?????? ?????? ????????? ?????? ??? ??????
    @Override
    public void onResume() {
        super.onResume();
//        loadPostsAfterCheck(true);
        Log.d(TAG, "onResume: ???????????????");
        getPetInfo();
        if(mSwipeRefreshLayout != null){
            if(contentCheck){
                ((MainActivity)getActivity()).refresh(true);
                contentCheck = false;
            }
        }


    }

    public void MypageRefresh(){
//        postList.clear();
        loadPostsAfterCheck(false);
        getPetInfo();
    }


}


