package com.example.petdiary;


import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.petdiary.adapter.ViewPageAdapterSub;
import com.example.petdiary.ui.AnimationUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Kon_MypageAdapter extends RecyclerView.Adapter<Kon_MypageAdapter.MypageViewHolder> implements calbacklistener {

    private ArrayList<Data> arrayList ;
    private ArrayList<Data> arrayList2;
    private Context context;
    private LayoutInflater inf;
    private int squareSize;
    private int columnNum;
    calbacklistener calbacklistener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    int previousPostition;



    //어댑터에서 액티비티 액션을 가져올 때 context가 필요한데 어댑터에는 context가 없다.
    //선택한 액티비티에 대한 context를 가져올 때 필요하다.

    public Kon_MypageAdapter(ArrayList<Data> arrayList, int columnNum, Context context , calbacklistener calbacklistener) {
        this.arrayList = arrayList;
        this.arrayList2 = arrayList;
        this.context = context;
        this.columnNum = columnNum;
        this.calbacklistener = calbacklistener;
    }

    public void setArray(ArrayList<Data> arrayList)
    {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MypageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kon_mypage_item, parent, false);
        MypageViewHolder holder = new MypageViewHolder(view);

        int layout_width = parent.getMeasuredWidth();
        //int layout_height = parent.getMeasuredHeight();
        int itemSize = layout_width / columnNum;
        squareSize = itemSize - (itemSize / 32);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MypageViewHolder holder, int position) {

        holder.itemView.getLayoutParams().width = squareSize;  // 아이템 뷰의 세로 길이를 구한 길이로 변경
        holder.itemView.getLayoutParams().height = squareSize;  // 아이템 뷰의 세로 길이를 구한 길이로 변경
        holder.itemView.requestLayout(); // 변경 사항 적용

        String url = arrayList.get(position).getImageUrl1();
        // if(url != null)
//        Glide.with(context).load(url).centerCrop().override(500).into(holder.postImage);


        if(!url.equals("https://firebasestorage.googleapis.com/v0/b/petdiary-794c6.appspot.com/o/images%2Fempty.png?alt=media&token=c41b1cc0-d610-4964-b00c-2638d4bfd8bd")) {
            holder.cardView.setVisibility(View.VISIBLE);
            holder.textView.setVisibility(View.GONE);
            Glide.with(context).load(url).centerCrop().override(500).into(holder.postImage);

        }
        else{
            holder.cardView.setVisibility(View.GONE);
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(arrayList.get(position).getContent());

        }

//        if(textView.getVisibility()==View.VISIBLE){
//            textView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(arrayList.size() > 0) {
//                        goPost2(arrayList.get(position));
//                    }
//                }
//            });
//
//        }

        ///////////////////////test
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity) holder.itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int deviceWidth = displayMetrics.widthPixels;  // 핸드폰의 가로 해상도를 구함.
//        // int deviceHeight = displayMetrics.heightPixels;  // 핸드폰의 세로 해상도를 구함.
//        deviceWidth = (deviceWidth-60) / 3;
//        holder.itemView.getLayoutParams().width = deviceWidth;  // 아이템 뷰의 세로 길이를 구한 길이로 변경
//        holder.itemView.getLayoutParams().height = deviceWidth;  // 아이템 뷰의 세로 길이를 구한 길이로 변경
//        holder.itemView.requestLayout(); // 변경 사항 적용
//
//

    }

    ViewPageAdapterSub viewPageAdapter;
    //  ViewPager viewPager;
    ImageView pageView;


    @Override
    public int getItemCount() {
        // 삼항 연산자
        return arrayList.size();
    }

    @Override
    public void refresh(boolean check) {


    }

    public class MypageViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView textView;
        CardView cardView;

        public MypageViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.postImage = itemView.findViewById(R.id.mypage_image);
            this.textView = itemView.findViewById(R.id.my_textview);
            this.cardView = itemView.findViewById(R.id.my_cardView);

//            arrayList2 = arrayList;
            //   DisplayMetrics displayMetrics = new DisplayMetrics();
            //((Activity) itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            //  int deviceWidth = displayMetrics.widthPixels;  // 핸드폰의 가로 해상도를 구함.
            // int deviceHeight = displayMetrics.heightPixels;  // 핸드폰의 세로 해상도를 구함.
            //  deviceWidth = (deviceWidth-60) / 3;
//        itemView.getLayoutParams().width = squareSize;  // 아이템 뷰의 세로 길이를 구한 길이로 변경
//        itemView.getLayoutParams().height = squareSize;  // 아이템 뷰의 세로 길이를 구한 길이로 변경
//        itemView.requestLayout(); // 변경 사항 적용


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(arrayList.size() > 0) {
                        goPost2(arrayList.get(getAdapterPosition()));
                    }
//                    else{
////                        Toast.makeText(context,"널값임",Toast.LENGTH_SHORT).show();
//                        goPost(arrayList2.get(getAdapterPosition()));
//
//                    }

                    }
            });
        }
    }

//    private void goPost(int position) {
//        Intent intent = new Intent(context, Expand_ImageView.class);
//
//        intent.putExtra("postLike", "unchecked");
//        intent.putExtra("bookmark", "unchecked");
//        intent.putExtra("friend", "unchecked");
//
//
//        intent.putExtra("postID", this.arrayList.get(position).getPostID());
//        Log.d("dsds", "goPost: " + this.arrayList.get(position).getPostID());
//        intent.putExtra("nickName", this.arrayList.get(position).getNickName());
//        intent.putExtra("uid", this.arrayList.get(position).getUid());
//        intent.putExtra("imageUrl1", this.arrayList.get(position).getImageUrl1());
//        intent.putExtra("imageUrl2", this.arrayList.get(position).getImageUrl2());
//        intent.putExtra("imageUrl3", this.arrayList.get(position).getImageUrl3());
//        intent.putExtra("imageUrl4", this.arrayList.get(position).getImageUrl4());
//        intent.putExtra("imageUrl5", this.arrayList.get(position).getImageUrl5());
//        intent.putExtra("favoriteCount", this.arrayList.get(position).getFavoriteCount());
//        intent.putExtra("date", this.arrayList.get(position).getDate());
//        intent.putExtra("content", this.arrayList.get(position).getContent());
//
//        context.startActivity(intent);
//    }


//    private void goPost(Data arrayList) {
//        Intent intent = new Intent(context, Expand_ImageView.class);
//
//
//
//
//        intent.putExtra("postLike", "unchecked");
//        intent.putExtra("bookmark", "unchecked");
//        intent.putExtra("friend", "unchecked");
//
//        intent.putExtra("postID", arrayList.getPostID());
//        Log.d("dsds", "goPost: " + arrayList.getPostID());
//        intent.putExtra("nickName", arrayList.getNickName());
//        intent.putExtra("uid", arrayList.getUid());
//        intent.putExtra("imageUrl1", arrayList.getImageUrl1());
//        intent.putExtra("imageUrl2", arrayList.getImageUrl2());
//        intent.putExtra("imageUrl3", arrayList.getImageUrl3());
//        intent.putExtra("imageUrl4", arrayList.getImageUrl4());
//        intent.putExtra("imageUrl5", arrayList.getImageUrl5());
//        intent.putExtra("favoriteCount", arrayList.getFavoriteCount());
//        intent.putExtra("date", arrayList.getDate());
//        intent.putExtra("content", arrayList.getContent());
//        Log.d("dsds", "goPost: " + arrayList.getContent());
//
//
//        context.startActivity(intent);
//    }

    private void goPost2(final Data arrayList) {
        final Intent intent = new Intent(context, Expand_ImageView.class);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        final ArrayList<String> mainSource = new ArrayList<>();

        mainSource.clear();

        mDatabase = FirebaseDatabase.getInstance().getReference("friend/"+arrayList.getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    mainSource.add(postSnapshot.getKey());
                }
                db.collection("user-checked/"+arrayList.getUid()+"/bookmark")
                        .whereEqualTo("postID", arrayList.getPostID())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    intent.putExtra("bookmark", "unchecked");
                                    for (final QueryDocumentSnapshot document : task.getResult()) {
                                        if(arrayList.getPostID().equals(document.getData().get("postID").toString())){
                                            intent.putExtra("bookmark", "checked");
                                            break;
                                        }
                                    }
                                    db.collection("user-checked/"+arrayList.getUid()+"/like")
                                            .whereEqualTo("postID", arrayList.getPostID())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        intent.putExtra("postLike", "unchecked");
                                                        for (final QueryDocumentSnapshot document : task.getResult()) {
                                                            if(arrayList.getPostID().equals(document.getData().get("postID").toString())){
                                                                intent.putExtra("postLike", "checked");
                                                                break;
                                                            }
                                                        }
                                                        boolean chkFriend = false;
                                                        for (int i=0; i<mainSource.size(); i++) {
                                                            if (arrayList.getUid().equals(mainSource.get(i))) {
                                                                chkFriend = true;
                                                                break;
                                                            }
                                                        }
                                                        if (chkFriend) {
                                                            intent.putExtra("friend", "checked");
                                                        } else {
                                                            intent.putExtra("friend", "unchecked");
                                                        }
                                                        intent.putExtra("postID", arrayList.getPostID());
                                                        intent.putExtra("nickName", arrayList.getNickName());
                                                        intent.putExtra("uid", arrayList.getUid());
                                                        intent.putExtra("imageUrl1", arrayList.getImageUrl1());
                                                        intent.putExtra("imageUrl2", arrayList.getImageUrl2());
                                                        intent.putExtra("imageUrl3", arrayList.getImageUrl3());
                                                        intent.putExtra("imageUrl4", arrayList.getImageUrl4());
                                                        intent.putExtra("imageUrl5", arrayList.getImageUrl5());
                                                        intent.putExtra("favoriteCount", arrayList.getFavoriteCount());
                                                        intent.putExtra("date", arrayList.getDate());
                                                        intent.putExtra("content", arrayList.getContent());
                                                        intent.putExtra("postID", arrayList.getPostID());
                                                        intent.putExtra("category", arrayList.getCategory());
                                                        intent.putExtra("favoriteCount", arrayList.getFavoriteCount());
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        context.startActivity(intent);
                                                        Expand_ImageView.setlistener(calbacklistener);
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