package com.pashkobohdan.fastreading;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.honu.aloha.WelcomeHelper;
import com.morsebyte.shailesh.twostagerating.TwoStageRate;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfo;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfoFactory;
import com.pashkobohdan.fastreading.library.bookTextWorker.BookInfosList;
import com.pashkobohdan.fastreading.library.feedback.EmailCrashReport;
import com.pashkobohdan.fastreading.library.feedback.EmailFeedback;
import com.pashkobohdan.fastreading.library.fileSystem.file.InternalStorageFileHelper;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpening.core.BookReadingResult;
import com.pashkobohdan.fastreading.library.fileSystem.newFileOpeningThread.FileOpenThread;
import com.pashkobohdan.fastreading.library.firebase.downloadBooks.FirebaseBook;
import com.pashkobohdan.fastreading.library.ui.dialogs.BookAddDialog;
import com.pashkobohdan.fastreading.library.ui.dialogs.BookEditDialog;
import com.pashkobohdan.fastreading.library.ui.lists.booksList.BooksRecyclerViewAdapter;

import java.io.File;
import java.util.Collections;

import ir.sohreco.androidfilechooser.ExternalStorageNotAvailableException;
import ir.sohreco.androidfilechooser.FileChooserDialog;

import static com.pashkobohdan.fastreading.CurrentBook.BOOK_INFO_EXTRA_NAME;
import static com.pashkobohdan.fastreading.library.fileSystem.file.InternalStorageFileHelper.INTERNAL_FILE_EXTENSION;

public class AllBooks extends AppCompatActivity implements FileChooserDialog.ChooserListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int RC_SIGN_IN = 9001;

    public static final int OPEN_PDF_FB2_TXT_FILE = 999;

    /**
     * UI elements
     */
    private RecyclerView booksRecyclerView;
    private RecyclerView.Adapter booksAdapter;

    private FloatingActionsMenu booksFloatingActionsMenu;
    private FloatingActionButton floatingActionButtonOpenFile,
            floatingActionButtonDownloadBook,
            floatingActionButtonCreateBook;

    private MenuItem signInSignOut;

    /**
     * Sorting
     */
    enum BooksSortTypes {
        BY_LAST_OPENING,
        BY_PROGRESS,
        BY_NAME,
        BY_BACK_NAME,
        BY_AUTHOR,
        BY_BACK_AUTHOR
    }

    private BooksSortTypes booksSortType = BooksSortTypes.BY_LAST_OPENING;


    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;

    private FirebaseDatabase database;
    private DatabaseReference booksReference;


    private TwoStageRate twoStageRate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);

        /**
         * Welcome activity show (if run first time)
         */
        if (WelcomeHelper.isWelcomeRequired(this)) {
            startActivity(new Intent(this, WelcomeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        /**
         * Toolbar setup
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.activity_my_books_title);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        /**
         * UI elements setup
         */
        booksFloatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.book_list_fab_menu);
        floatingActionButtonOpenFile = (FloatingActionButton) findViewById(R.id.book_list_open_pdf_fb2_txt_file);
        floatingActionButtonDownloadBook = (FloatingActionButton) findViewById(R.id.book_list_download_book_from_cloud);
        floatingActionButtonCreateBook = (FloatingActionButton) findViewById(R.id.book_list_create_new_book);

        booksRecyclerView = (RecyclerView) findViewById(R.id.books_recycler_view);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        initFABsListeners();


        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("first_start", true)) {
            BookInfo firstBook = BookInfoFactory.createNewInstance(new BookReadingResult(getResources().getString(R.string.first_book_text), getResources().getString(R.string.first_book_name), getResources().getString(R.string.first_book_author)), this);
            if (firstBook == null) {
                EmailCrashReport.sendCrashReport(this, new Exception("Cannot create first book. Device : " + System.getProperties().toString()));
            }

            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("first_start", false).apply();
        }

        // if data is already loaded
        if (BookInfosList.getAll().size() == 0) {
            initBookInfoData();
        }

        if (booksAdapter == null) {
            initBooksListAdapter();
        }


        /**
         * Init rate this app
         */
        twoStageRate = TwoStageRate.with(this);
        twoStageRate
                .setInstallDays(3)
                .setLaunchTimes(5)
                .setEventsTimes(5)
                .setFeedbackReceivedListener(feedback -> Toast.makeText(AllBooks.this, feedback, Toast.LENGTH_SHORT).show());

        twoStageRate.setRatePromptTitle(getString(R.string.rate_prompt_title)).
                setRatePromptDismissible(false).
                resetOnDismiss(true).
                resetOnFeedBackDeclined(true).
                resetOnRatingDeclined(true)

                .setShowAppIcon(false);

        twoStageRate.setConfirmRateDialogTitle(getString(R.string.confirm_rate_title)).
                setConfirmRateDialogDescription(getString(R.string.confirm_rate_description)).
                setConfirmRateDialogPositiveText(getString(R.string.confirm_rate_positive)).
                setConfirmRateDialogNegativeText(getString(R.string.confirm_rate_negative)).
                setConfirmRateDialogDismissible(false);

        twoStageRate.setFeedbackDialogTitle(getString(R.string.feedback_title)).
                setFeedbackDialogDescription(getString(R.string.feedback_description)).
                setFeedbackDialogPositiveText(getString(R.string.feedback_positive)).
                setFeedbackDialogNegativeText(getString(R.string.feedback_negative)).
                setFeedbackDialogDismissible(true);

        twoStageRate.setFeedbackWithRatingReceivedListener((rating, feedback) -> {
            EmailFeedback.sendEmailToDeveloper(AllBooks.this, "Fast Reading. Feedback", "Rating : " + rating + "\n\n" + feedback);
        });

        /**
         * Firebase and google init
         */
        authorizeInitialize();


        database = FirebaseDatabase.getInstance();
        booksReference = database.getReference("books");
    }

    private void authorizeInitialize() {
        mFirebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.google_service_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshBookList();

        if (mShareEmail) {
            openFileChooserDialog();
            mShareEmail = false;
        }

        twoStageRate.incrementEvent();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BookInfosList.getAll().clear();
    }


    @Override
    public void onSelect(String path) {
        selectOpeningFile(new File(path));
    }


    private boolean mShareEmail = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mShareEmail = true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_books, menu);

        signInSignOut = menu.findItem(R.id.sign_in_or_out);

        if (signInSignOut != null) {
            if (mFirebaseAuth.getCurrentUser() != null) {
                signInSignOut.setTitle(R.string.sign_out);
            } else {
                signInSignOut.setTitle(R.string.sign_in);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.action_sort_by_last_open:
                if (!item.isChecked()) {
                    booksSortType = BooksSortTypes.BY_LAST_OPENING;
                    refreshBookList();
                    item.setChecked(true);
                }
                break;

            case R.id.action_sort_by_progress:
                if (!item.isChecked()) {
                    booksSortType = BooksSortTypes.BY_PROGRESS;
                    refreshBookList();
                    item.setChecked(true);
                }
                break;

            case R.id.action_sort_by_name:
                if (!item.isChecked()) {
                    booksSortType = BooksSortTypes.BY_NAME;
                    refreshBookList();
                    item.setChecked(true);
                }
                break;

//            case R.id.action_back_sort_by_name:
//                if (!item.isChecked()) {
//                    booksSortType = BooksSortTypes.BY_BACK_NAME;
//                    refreshBookList();
//                    item.setChecked(true);
//                }
//                break;

            case R.id.action_sort_by_author:
                if (!item.isChecked()) {
                    booksSortType = BooksSortTypes.BY_AUTHOR;
                    refreshBookList();
                    item.setChecked(true);
                }
                break;

//            case R.id.action_back_sort_by_author:
//                if (!item.isChecked()) {
//                    booksSortType = BooksSortTypes.BY_BACK_AUTHOR;
//                    refreshBookList();
//                    item.setChecked(true);
//                }
//                break;

            case R.id.sign_in_or_out:
                signInOrOut();
                break;


//            case R.id.action_help:
//                startActivity(new Intent(this, Help.class));
//                break;

            case R.id.rate_app:
                twoStageRate.showRatePromptDialog();
                break;

            case R.id.else_by_bsapps:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:B+S+Apps")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=B+S+Apps")));
                }
                break;

            case R.id.show_help:
                startActivity(new Intent(this, WelcomeActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;


        }

        return super.onOptionsItemSelected(item);
    }


    private Runnable signInSuccess = null;

    private void signInOrOut() {
        if (mFirebaseAuth.getCurrentUser() != null) {
            mFirebaseAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);

            Toast.makeText(this, R.string.sign_out_ok, Toast.LENGTH_SHORT).show();

            if (signInSignOut != null) {
                if (mFirebaseAuth.getCurrentUser() != null) {
                    signInSignOut.setTitle(R.string.sign_out);
                } else {
                    signInSignOut.setTitle(R.string.sign_in);
                }
            }
        } else {
            Intent authorizeIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(authorizeIntent, RC_SIGN_IN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                Toast.makeText(AllBooks.this, R.string.sign_in_error, Toast.LENGTH_SHORT).show();
                signInSuccess = null;
            }
        } else if (requestCode == OPEN_PDF_FB2_TXT_FILE) {
            if (resultCode == RESULT_OK) {
                File myFile = new File(getRealPathFromURI(data.getData()));

                if (myFile.getAbsolutePath().endsWith("pdf") ||
                        myFile.getAbsolutePath().endsWith("fb2") ||
                        myFile.getAbsolutePath().endsWith("txt")) {

                    selectOpeningFile(myFile);
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.information)
                            .setMessage(R.string.choose_cerrect_file)
                            .create()
                            .show();
                }

            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.information)
                        .setMessage(R.string.choose_cerrect_file)
                        .create()
                        .show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(AllBooks.this, R.string.auth_error, Toast.LENGTH_SHORT).show();
                        signInSuccess = null;
                    } else {
                        Toast.makeText(AllBooks.this, R.string.sign_in_ok, Toast.LENGTH_SHORT).show();

                        if (signInSignOut != null) {
                            if (mFirebaseAuth.getCurrentUser() != null) {
                                signInSignOut.setTitle(R.string.sign_out);
                            } else {
                                signInSignOut.setTitle(R.string.sign_in);
                            }
                        }

                        if (signInSuccess != null) {
                            signInSuccess.run();
                        }
                    }
                });
    }


    /**
     * Business logic
     */
    private void refreshBookList() {
        switch (booksSortType) {
            case BY_LAST_OPENING:
                Collections.sort(BookInfosList.getAll(), (o1, o2) ->
                        Integer.valueOf(o2.getLastOpeningDate()).compareTo(o1.getLastOpeningDate())
                );
                break;

            case BY_PROGRESS:
                Collections.sort(BookInfosList.getAll(), (o2, o1) -> {
                            if (!o1.isWasRead()) {
                                if (!o2.isWasRead()) {
                                    return 0;
                                }
                                return -1;
                            } else {
                                if (!o2.isWasRead()) {
                                    return 1;
                                } else {
                                    return Integer.valueOf((int) (100.0 * o1.getCurrentWordNumber() / o1.getWords().length))
                                            .compareTo((int) (100.0 * o2.getCurrentWordNumber() / o2.getWords().length));
                                }
                            }
                        }
                );
                break;

            case BY_NAME:
                Collections.sort(BookInfosList.getAll(), (o1, o2) -> o1.getName().compareTo(o2.getName()));
                break;

            case BY_BACK_NAME:
                Collections.sort(BookInfosList.getAll(), (o1, o2) -> o2.getName().compareTo(o1.getName()));
                break;

            case BY_AUTHOR:
                Collections.sort(BookInfosList.getAll(), (o1, o2) -> o1.getAuthor().compareTo(o2.getAuthor()));
                break;

            case BY_BACK_AUTHOR:
                Collections.sort(BookInfosList.getAll(), (o1, o2) -> o2.getAuthor().compareTo(o1.getAuthor()));
                break;
        }

        booksAdapter.notifyDataSetChanged();
    }

    private void initFABsListeners() {
        floatingActionButtonOpenFile.setOnClickListener(v -> {
            tryOpenFile();
            booksFloatingActionsMenu.collapse();
        });
        floatingActionButtonDownloadBook.setOnClickListener(v -> {
            tryDownloadBookFromCloud();
            booksFloatingActionsMenu.collapse();
        });
        floatingActionButtonCreateBook.setOnClickListener(v -> {
            new BookAddDialog(this, bookInfo -> {
                BookInfosList.add(bookInfo);
                refreshBookList();
            }).show();
            booksFloatingActionsMenu.collapse();
        });
    }

    private void initBookInfoData() {
        for (File file : getCacheDir().listFiles((directory, fileName) -> fileName.endsWith(INTERNAL_FILE_EXTENSION))) {
            BookInfosList.add(BookInfoFactory.newInstance(file, this));
        }
    }

    private void initBooksListAdapter() {


        booksAdapter = new BooksRecyclerViewAdapter(this, BookInfosList.getAll(), (bookInfo) -> {

            if (!checkBookReady(bookInfo)) {
                return;
            }

            new BookEditDialog(this, bookInfo, this::refreshBookList).show();

        }, (bookInfo) -> {

            Toast.makeText(this, "sharing : " + bookInfo.getName(), Toast.LENGTH_SHORT).show();

        }, (bookInfo) -> {
            if (!checkBookReady(bookInfo)) {
                return;
            }

            FirebaseBook book = new FirebaseBook(bookInfo.getName(), bookInfo.getAuthor(), bookInfo.getAllText());

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.dialog_book_upload_title));
            progressDialog.setMessage(getString(R.string.dialog_book_upload_text));
            progressDialog.show();

            booksReference.push().setValue(book, (databaseError, databaseReference) -> {
                progressDialog.dismiss();
                Toast.makeText(AllBooks.this, R.string.book_upload_success, Toast.LENGTH_SHORT).show();
            });


        }, (bookInfo, ifConfirmed) -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (bookInfo.getFile().delete()) {
                            ifConfirmed.run();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.delete_confirm_1) + bookInfo.getName()).setPositiveButton(R.string.yes, dialogClickListener)
                    .setNegativeButton(R.string.no, dialogClickListener).show();
        }, (bookInfo) -> {

            if (!checkBookReady(bookInfo)) {
                return;
            }

            Intent i = new Intent(this, CurrentBook.class);
            i.putExtra(BOOK_INFO_EXTRA_NAME, bookInfo.getFile());
            startActivity(i);

        }, (bookInfo) -> {

            //Toast.makeText(this, "long click : " + bookInfo.getName(), Toast.LENGTH_SHORT).show();

        });


        ((BooksRecyclerViewAdapter) booksAdapter).setMode(Attributes.Mode.Single);
        booksRecyclerView.setAdapter(booksAdapter);

    }


    private boolean checkBookReady(BookInfo bookInfo) {
        if (bookInfo.isWasRead()) {
            if (bookInfo.getWords().length < 1) {
                new AlertDialog.Builder(this)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                        })
                        .setTitle(R.string.information)
                        .setMessage(R.string.book_is_empty)
                        .create()
                        .show();

                return false;
            }
            return true;
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.information)
                    .setMessage(R.string.book_still_opening)
                    .create()
                    .show();

            return false;
        }
    }

    private void tryOpenFile() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            openFileChooserDialog();
        }
    }

    private void openFileChooserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.chooe_file_manager)
                .setTitle(R.string.open_file)
                .setPositiveButton(R.string.default_file_manager, (dialog, which) -> {
                    FileChooserDialog.Builder openFileBuilder =
                            new FileChooserDialog.Builder(FileChooserDialog.ChooserType.FILE_CHOOSER, this)
                                    .setTitle(getString(R.string.select_file_dialog_title))
                                    .setFileFormats(new String[]{".txt", ".pdf", ".fb2"});
                    try {
                        openFileBuilder.build().show(getSupportFragmentManager(), null);
                    } catch (ExternalStorageNotAvailableException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton(R.string.system_file_manager, (dialog, which) -> {
                    try {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("file/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);

                        try {
                            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_pdf_fb2_txt_files)), OPEN_PDF_FB2_TXT_FILE);
                        } catch (ActivityNotFoundException e) {
                            throw e;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .show();


    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void selectOpeningFile(File inputFile) {
        String bookName = InternalStorageFileHelper.fileNameWithoutExtension(inputFile);

        if (InternalStorageFileHelper.isFileWasOpened(this, inputFile)) {

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        openFileWithUI(inputFile);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.book_rewriting_confirm) + "\"" +
                    bookName +
                    "\"")
                    .setPositiveButton(R.string.yes, dialogClickListener)
                    .setNegativeButton(R.string.no, dialogClickListener)
                    .show();

        } else {
            openFileWithUI(inputFile);
        }
    }

    private void openFileWithUI(File inputFile) {
        final Activity activity = this;
        final ProgressDialog pd = new ProgressDialog(activity);
        pd.setTitle(getString(R.string.reading_dialog_title));
        pd.setMessage(getString(R.string.reading_dialog_text));
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMax(100);
        pd.setProgress(0);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        new FileOpenThread(inputFile, this, (o, n) -> {
            pd.setIndeterminate(false);
            pd.setProgress(n);
        }, pd::dismiss, bookOpeningResult -> {
            if (bookOpeningResult != null) {
                addNewBookToBooksList(bookOpeningResult);
            }
        }).start();

    }

    private void addNewBookToBooksList(BookReadingResult bookOpeningResult) {
        BookInfo newBookInfo = BookInfoFactory.createNewInstance(bookOpeningResult, this);
        if (newBookInfo == null) {
            Toast.makeText(this, R.string.book_writing_error, Toast.LENGTH_SHORT).show();
            return;
        }

        BookInfosList.add(newBookInfo);
        refreshBookList();

        booksAdapter.notifyItemInserted(BookInfosList.getAll().size() - 1);
    }


    private void tryDownloadBookFromCloud() {
        if (mFirebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, Download.class));
        } else {
            signInSuccess = () -> {
                startActivity(new Intent(AllBooks.this, Download.class));
                signInSuccess = null;
            };

            signInOrOut();
        }
    }

}
