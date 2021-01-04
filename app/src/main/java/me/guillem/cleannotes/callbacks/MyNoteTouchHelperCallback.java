package me.guillem.cleannotes.callbacks;

import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.ColumnInfo;

import me.guillem.cleannotes.R;
import me.guillem.cleannotes.adapters.NotesAdapter;

/**
 * * Created by Guillem on 30/12/20.
 */
public class MyNoteTouchHelperCallback extends ItemTouchHelper.Callback {

    CallBackNoteTouch callBackNoteTouch;

    public MyNoteTouchHelperCallback(CallBackNoteTouch callBackNoteTouch) {
        this.callBackNoteTouch = callBackNoteTouch;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        callBackNoteTouch.itemTouchOnMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        callBackNoteTouch.onSwiped(viewHolder, viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG){
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }else {
            final View foregroundView = ((NotesAdapter.NoteViewHolder)viewHolder).viewB;
            getDefaultUIUtil().onDrawOver(c,recyclerView,foregroundView,dX, dY, actionState,isCurrentlyActive);
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState != ItemTouchHelper.ACTION_STATE_DRAG) {
            final View foregroundView = ((NotesAdapter.NoteViewHolder) viewHolder).viewF;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //super.clearView(recyclerView, viewHolder);
        final View foregroundView = ((NotesAdapter.NoteViewHolder) viewHolder).viewF;
        //foregroundView.setBackgroundColor(ContextCompat.getColor(((NotesAdapter.NoteViewHolder)viewHolder).viewF.getContext(), R.color.colorDelete));
        getDefaultUIUtil().clearView(foregroundView );
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (viewHolder!=null){
            final View foregroundView = ((NotesAdapter.NoteViewHolder) viewHolder).viewF;
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG){
            }
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
}
