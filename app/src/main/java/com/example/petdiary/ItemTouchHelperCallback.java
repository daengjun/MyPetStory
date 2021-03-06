package com.example.petdiary;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

enum ButtonsState{
    GONE,
    RIGHT_VISIBLE
}
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchHelperListener listener;
    private ButtonsState buttonsShowedState = ButtonsState.GONE;
    private static final float buttonWidth = 215;
    private RectF buttonInstance = null;
    private RecyclerView.ViewHolder currenrtItemViewHolder = null;
    private boolean swipeBack = false;

    public ItemTouchHelperCallback(ItemTouchHelperListener listener){
        this.listener = listener;
    }
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder){
        int drag_flags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipe_flags = ItemTouchHelper.START ;
        return makeMovementFlags(drag_flags, swipe_flags);
    }
    @Override
    public boolean isLongPressDragEnabled(){
        return true;
    }
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target){

        return false;
        //return listener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    }
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder , int direction){
        listener.onItemSwipe(viewHolder.getAdapterPosition());
    }
    
    ///////////////////////////////////////////////////
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean    isCurrentlyActive) {
        //???????????? ???????????? ???????????? ????????? ???????????? ????????? ??????????????? ????????? ??????

        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            if(buttonsShowedState != ButtonsState.GONE){
                if(buttonsShowedState == ButtonsState.RIGHT_VISIBLE)
                    dX = Math.min(dX, -buttonWidth);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            else{
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            if(buttonsShowedState == ButtonsState.GONE){
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        } currenrtItemViewHolder = viewHolder;
        //????????? ???????????? ??????
        drawButtons(c, currenrtItemViewHolder);
    }
    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder){
        float buttonWidthWithOutPadding = buttonWidth - 10;
        float corners = 5;
        View itemView = viewHolder.itemView;
        Paint p = new Paint();
        buttonInstance = null;

            //???????????? ???????????? ????????? (???????????? ????????? ???????????? ??? ??????)
        if(buttonsShowedState == ButtonsState.RIGHT_VISIBLE){
            RectF rightButton =
                    new RectF(itemView.getRight() - buttonWidthWithOutPadding, itemView.getTop() + 25, itemView.getRight() -15, itemView.getBottom() - 25);
            p.setColor(Color.RED);
            c.drawRoundRect(rightButton, corners, corners, p);

            drawText("??????", c, rightButton, p);
            buttonInstance = rightButton;
        }
    }
    //????????? ????????? ????????????
    private void drawText(String text, Canvas c, RectF button, Paint p){
        float textSize = 40; p.setColor(Color.WHITE); p.setAntiAlias(true);
        p.setTextSize(textSize);
        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX() - (textWidth/2), button.centerY() + (textSize/2), p);
    }
    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if(swipeBack){
            swipeBack = false; return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
    private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY, final int actionState, final boolean isCurrentlyActive){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if(swipeBack){
                    if(dX < -buttonWidth) buttonsShowedState = ButtonsState.RIGHT_VISIBLE;

                    if(buttonsShowedState != ButtonsState.GONE){
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }
    private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView , final RecyclerView.ViewHolder viewHolder,
                                      final float dX, final float dY , final int actionState, final boolean isCurrentlyActive){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                } return false;
            }
        });
    }
    private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView , final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY , final int actionState, final boolean isCurrentlyActive){
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ItemTouchHelperCallback.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                setItemsClickable(recyclerView, true);
                swipeBack = false;
                if(listener != null && buttonInstance != null && buttonInstance.contains(event.getX(), event.getY())){
                    if(buttonsShowedState == ButtonsState.RIGHT_VISIBLE){
                        listener.onRightClick(viewHolder.getAdapterPosition(), viewHolder);
                    }
                }
                buttonsShowedState = ButtonsState.GONE; currenrtItemViewHolder = null;
                return false;
            }
        });
    }
    private void setItemsClickable(RecyclerView recyclerView, boolean isClickable){
        for(int i = 0; i < recyclerView.getChildCount(); i++){ recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }
}
