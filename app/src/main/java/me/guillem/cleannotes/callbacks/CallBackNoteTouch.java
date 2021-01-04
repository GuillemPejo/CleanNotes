package me.guillem.cleannotes.callbacks;

import androidx.recyclerview.widget.RecyclerView;

/**
 * * Created by Guillem on 30/12/20.
 */
public interface CallBackNoteTouch {

    void itemTouchOnMove(int oldPositions, int newPosition);

    void onSwiped(RecyclerView.ViewHolder viewHolder, int position);

}
