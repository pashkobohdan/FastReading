package com.pashkobohdan.fastreading.library.ui.lists.booksList;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.pashkobohdan.fastreading.R;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo;

/**
 * Holder class for recycler view for Books list
 *
 * Created by Bohdan Pashko on 24.01.17.
 */
public class BookViewHolder extends RecyclerView.ViewHolder {
    private SwipeLayout swipeLayout;

    private TextView booksPicName;
    private TextView bookName;
    private TextView bookAuthor;
    private TextView bookCurrentAndTotalWords;

    private TextView buttonEdit;
    private TextView buttonShare;
    private TextView buttonUpload;
    private TextView buttonDelete;


    public BookViewHolder(View itemView) {
        super(itemView);

        swipeLayout = (SwipeLayout) itemView.findViewById(R.id.book_swipe_layout);

        booksPicName = (TextView) itemView.findViewById(R.id.book_pic_name);
        bookName = (TextView) itemView.findViewById(R.id.book_name);
        bookAuthor = (TextView) itemView.findViewById(R.id.book_author);
        bookCurrentAndTotalWords = (TextView) itemView.findViewById(R.id.book_current_position);

        setButtonEdit((TextView) itemView.findViewById(R.id.edit_book));
        setButtonShare((TextView) itemView.findViewById(R.id.share_book));
        setButtonUpload((TextView) itemView.findViewById(R.id.upload_book));
        setButtonDelete((TextView) itemView.findViewById(R.id.delete_book));
    }

    public SwipeLayout getSwipeLayout() {
        return swipeLayout;
    }

    public TextView getBooksPicName() {
        return booksPicName;
    }

    public TextView getBookName() {
        return bookName;
    }

    public TextView getBookAuthor() {
        return bookAuthor;
    }

    public TextView getBookCurrentAndTotalWords() {
        return bookCurrentAndTotalWords;
    }

    public TextView getButtonEdit() {
        return buttonEdit;
    }

    public void setButtonEdit(TextView buttonEdit) {
        this.buttonEdit = buttonEdit;
    }

    public TextView getButtonShare() {
        return buttonShare;
    }

    public void setButtonShare(TextView buttonShare) {
        this.buttonShare = buttonShare;
    }

    public TextView getButtonUpload() {
        return buttonUpload;
    }

    public void setButtonUpload(TextView buttonUpload) {
        this.buttonUpload = buttonUpload;
    }

    public TextView getButtonDelete() {
        return buttonDelete;
    }

    public void setButtonDelete(TextView buttonDelete) {
        this.buttonDelete = buttonDelete;
    }
}