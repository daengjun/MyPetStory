package com.example.petdiary.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.petdiary.Data;
import com.example.petdiary.Expand_ImageView;
import com.example.petdiary.R;
import com.example.petdiary.ui.AnimationUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CustomAdapterSub extends RecyclerView.Adapter<CustomAdapterSub.CustomViewHolder> implements com.example.petdiary.calbacklistener {

    private ArrayList<Data> arrayList;
    private Context context;
    com.example.petdiary.calbacklistener callbacklistenter;
    ViewPageAdapterSub viewPageAdapter;
    ViewPager viewPager;
    TextView textView;
    CardView cardView;
    private DatabaseReference mDatabase;
    private FirebaseDatabase firebaseDatabase;
    ArrayList<String> mainSource;



    public CustomAdapterSub(ArrayList<Data> arrayList, Context context,com.example.petdiary.calbacklistener callbacklistenter) {
        this.arrayList = arrayList;
        this.context = context;
        this.callbacklistenter = callbacklistenter;
    }

    @NonNull
    @Override
    //실제 리스트뷰가 어댑터에 연결된 다음에 뷰 홀더를 최초로 만들어낸다.
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;

    }
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {
        viewPager = (ViewPager) holder.itemView.findViewById(R.id.main_image);
        textView = (TextView) holder.itemView.findViewById(R.id.sub_textview);
        cardView = (CardView) holder.itemView.findViewById(R.id.sub_cardView);
        


        if(!arrayList.get(position).getImageUrl1().equals("https://firebasestorage.googleapis.com/v0/b/petdiary-794c6.appspot.com/o/images%2Fempty.png?alt=media&token=c41b1cc0-d610-4964-b00c-2638d4bfd8bd")) {
            cardView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            viewPageAdapter = new ViewPageAdapterSub(arrayList.get(position), arrayList.get(position).getImageUrl1(), context, callbacklistenter);
            viewPager.setAdapter(viewPageAdapter);
        }
        else{
            cardView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(arrayList.get(position).getContent());

        }

        if(textView.getVisibility()==View.VISIBLE){
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(arrayList.size() > 0) {
                        goPost(arrayList.get(position));
                    }
                }
            });

        }


        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) holder.itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;  // 핸드폰의 가로 해상도를 구함.
        // int deviceHeight = displayMetrics.heightPixels;  // 핸드폰의 세로 해상도를 구함.
        deviceWidth = (deviceWidth-60) / 3;
        holder.itemView.getLayoutParams().width = deviceWidth;  // 아이템 뷰의 세로 길이를 구한 길이로 변경
        holder.itemView.getLayoutParams().height = deviceWidth;  // 아이템 뷰의 세로 길이를 구한 길이로 변경
        holder.itemView.requestLayout(); // 변경 사항 적용

    }

    @Override
    public int getItemCount() {
        // 삼항 연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    @Override
    public void refresh(boolean check) {

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void goPost(final Data arrayLists) {
        final Intent intent = new Intent(context, Expand_ImageView.class);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();
        mainSource = new ArrayList<>();

        mainSource.clear();

        mDatabase = FirebaseDatabase.getInstance().getReference("friend/"+uid);
        Log.d("dsd", "goPost: arrayLists.getUid()"+arrayLists.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    mainSource.add(postSnapshot.getKey());
                }
                db.collection("user-checked/"+uid+"/bookmark")
                        .whereEqualTo("postID", arrayLists.getPostID())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    intent.putExtra("bookmark", "unchecked");
                                    for (final QueryDocumentSnapshot document : task.getResult()) {
                                        if(arrayLists.getPostID().equals(document.getData().get("postID").toString())){
                                            Log.d("ㅇㄴㅇ", "onComplete: 여기탐2");

                                            intent.putExtra("bookmark", "checked");
                                            break;
                                        }
                                    }
                                    db.collection("user-checked/"+uid+"/like")
                                            .whereEqualTo("postID", arrayLists.getPostID())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        intent.putExtra("postLike", "unchecked");
                                                        for (final QueryDocumentSnapshot document : task.getResult()) {
                                                            if(arrayLists.getPostID().equals(document.getData().get("postID").toString())){
                                                                intent.putExtra("postLike", "checked");
                                                                Log.d("ㅇㄴㅇ", "onComplete: 여기탐3");
                                                                break;
                                                            }
                                                        }
                                                        boolean chkFriend = false;
                                                        for (int i=0; i<mainSource.size(); i++) {
                                                            if (arrayLists.getUid().equals(mainSource.get(i))) {
                                                                chkFriend = true;
                                                                break;
                                                            }
                                                        }
                                                        Log.d("ㅇㄴ", "onComplete: 여깈ㅋㅋ");
                                                        Log.d("ds", "ㄴ " + (mainSource!=null ? mainSource.size() : "널값"));

                                                        if (chkFriend) {
                                                            intent.putExtra("friend", "checked");
                                                        } else {
                                                            intent.putExtra("friend", "unchecked");
                                                        }
                                                        intent.putExtra("postID", arrayLists.getPostID());
                                                        intent.putExtra("nickName", arrayLists.getNickName());
                                                        intent.putExtra("uid", arrayLists.getUid());
                                                        intent.putExtra("imageUrl1", arrayLists.getImageUrl1());
                                                        intent.putExtra("imageUrl2", arrayLists.getImageUrl2());
                                                        intent.putExtra("imageUrl3", arrayLists.getImageUrl3());
                                                        intent.putExtra("imageUrl4", arrayLists.getImageUrl4());
                                                        intent.putExtra("imageUrl5", arrayLists.getImageUrl5());
                                                        intent.putExtra("favoriteCount", arrayLists.getFavoriteCount());
                                                        intent.putExtra("date", arrayLists.getDate());
                                                        intent.putExtra("content", arrayLists.getContent());
                                                        intent.putExtra("postID", arrayLists.getPostID());
                                                        intent.putExtra("category", arrayLists.getCategory());
                                                        intent.putExtra("favoriteCount", arrayLists.getFavoriteCount());
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        context.startActivity(intent);

                                                        Expand_ImageView.setlistener(callbacklistenter);
                                                    } else {
                                                        Log.d("###", "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });
                                } else {
                                    Log.d("###", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}