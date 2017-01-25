package com.pashkobohdan.fastreading.library.lists.booksList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.pashkobohdan.fastreading.R;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bohdan on 24.01.17.
 */

public class BooksRecyclerViewAdapter extends RecyclerSwipeAdapter<BookViewHolder> {

    private List<BookInfo> mDataset;

    private BookEventListener edit;
    private BookEventListener share;
    private BookEventListener upload;
    private BookConfirmationEventListener delete;
    private BookEventListener clickOnBook;
    private BookEventListener longClickOnBook;

    public BooksRecyclerViewAdapter(List<BookInfo> objects,
                                    BookEventListener edit,
                                    BookEventListener share,
                                    BookEventListener upload,
                                    BookConfirmationEventListener delete,
                                    BookEventListener clickOnBook,
                                    BookEventListener longClickOnBook) {
        this.mDataset = objects;

        this.edit = edit;
        this.share = share;
        this.upload = upload;
        this.delete = delete;
        this.clickOnBook = clickOnBook;
        this.longClickOnBook = longClickOnBook;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_layout_row, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookViewHolder viewHolder, final int position) {
        final BookInfo item = mDataset.get(position);
        viewHolder.setBookInfo(item);

        viewHolder.getSwipeLayout().setShowMode(SwipeLayout.ShowMode.PullOut);


        viewHolder.getSwipeLayout().setOnClickListener(new View.OnClickListener() {

            boolean wasClosed = true;

            @Override
            public void onClick(View v) {
                if (SwipeLayout.Status.Close == viewHolder.getSwipeLayout().getOpenStatus()) {
                    if (wasClosed) {
                        clickOnBook.run(item);
                    } else {
                        wasClosed = true;
                    }
                } else {
                    wasClosed = false;
                }
            }
        });
        viewHolder.getSwipeLayout().setOnLongClickListener(new View.OnLongClickListener() {
            boolean wasClosed = true;

            @Override
            public boolean onLongClick(View v) {
                if (SwipeLayout.Status.Close == viewHolder.getSwipeLayout().getOpenStatus()) {
                    if (wasClosed) {
                        longClickOnBook.run(item);
                    } else {
                        wasClosed = true;
                    }
                } else {
                    wasClosed = false;
                }
                return false;
            }
        });


        viewHolder.getButtonEdit().setOnClickListener((View v) -> edit.run(item));
        viewHolder.getButtonShare().setOnClickListener((View v) -> share.run(item));
        viewHolder.getButtonUpload().setOnClickListener((View v) -> upload.run(item));
        viewHolder.getButtonDelete().setOnClickListener((View v) -> delete.run(item, () -> {
            mItemManger.removeShownLayouts(viewHolder.getSwipeLayout());

            mDataset.remove(position);

            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mDataset.size());

            mItemManger.closeAllItems();
        }));

        viewHolder.getBooksPicName().setText(item.getName().substring(0, 2).toUpperCase());
        viewHolder.getBookName().setText(item.getName());
        viewHolder.getBookAuthor().setText(item.getAuthor());

        item.readWords(() -> viewHolder.getBookCurrentAndTotalWords().setText(item.getCurrentWordNumber() + " / " + item.getWordsNumber()),
                () -> viewHolder.getBookCurrentAndTotalWords().setText("book reading error"));


        mItemManger.bindView(viewHolder.itemView, position); // was wrote BIND !
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.book_swipe_layout;
    }

}
