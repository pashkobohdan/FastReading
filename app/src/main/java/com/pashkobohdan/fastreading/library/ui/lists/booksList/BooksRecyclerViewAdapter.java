package com.pashkobohdan.fastreading.library.ui.lists.booksList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.pashkobohdan.fastreading.R;
import com.pashkobohdan.fastreading.data.dto.DBBookDTO;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo;

import java.util.List;

/**
 * Book list (RecyclerView) adapter.
 * <p>
 * Created by bohdan on 24.01.17.
 */

public class BooksRecyclerViewAdapter extends RecyclerSwipeAdapter<BookViewHolder> {
    private static final String PERCENT_SYMBOL = " %";

    private Activity activity;

    private List<DBBookDTO> bookInfoList;

    private BookEventListener edit;
    private BookEventListener share;
    private BookEventListener upload;
    private BookConfirmationEventListener delete;
    private BookEventListener clickOnBook;
    private BookEventListener longClickOnBook;

    public BooksRecyclerViewAdapter(Activity activity, List<DBBookDTO> bookInfoList,
                                    BookEventListener edit,
                                    BookEventListener share,
                                    BookEventListener upload,
                                    BookConfirmationEventListener delete,
                                    BookEventListener clickOnBook,
                                    BookEventListener longClickOnBook) {
        this.activity = activity;

        this.bookInfoList = bookInfoList;

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
        final DBBookDTO item = bookInfoList.get(position);

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

            bookInfoList.remove(position);

            notifyItemRemoved(position);
            notifyItemRangeChanged(position, bookInfoList.size());

            mItemManger.closeAllItems();
        }));

        viewHolder.getBooksPicName().setText(item.getName().length() > 1 ? item.getName().substring(0, 2).toUpperCase() : item.getName().toUpperCase());
        viewHolder.getBooksPicName().setBackgroundColor(item.getColor());
        viewHolder.getBookName().setText(item.getName());
        viewHolder.getBookAuthor().setText(item.getAuthor());

        if (item.isWasRead()) {
            viewHolder.getBookCurrentAndTotalWords().setText((int) (100.0 * item.getCurrentWordNumber() / item.getWords().length) + PERCENT_SYMBOL);
        } else {
            //            item.readWords(() -> activity.runOnUiThread(() ->
            viewHolder.getBookCurrentAndTotalWords().setText((int) (100.0 * item.getCurrentWordNumber() / item.getWords().length) + PERCENT_SYMBOL);
//                    () -> activity.runOnUiThread(() ->
//                            viewHolder.getBookCurrentAndTotalWords().setText(R.string.book_list_book_reading_error)));
        }


        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return bookInfoList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.book_swipe_layout;
    }

}
