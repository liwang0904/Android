package com.example.teleprompter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements GridAdapter.DocumentGridAdapterCallbacks, LoaderManager.LoaderCallbacks<Cursor>, DeleteDialogFragment.DeleteConfirmationDialogFragmentCallbacks, InternetDialogFragment.RequestInternetDialogFragmentCallbacks {
    private static final int LOADER_ID = 10001;
    private static final String REQUEST_INTERNET_DIALOG = "request_internet_dialog";
    private String DELETE_DIALOG_TAG = "delete_dialog_tag";
    private static final String DELETED_KEY = "deleted-document";
    private static final String DELETED_POS = "deleted-document-pos";
    public static final String NO_PINNED = "no-pinned-document";

    private Document deletedDocument;
    private int deletedPosition;

    private SyncDocumentsReceiver syncDocumentsReciever;
    private boolean docsMoved;
    private GridAdapter adapter;

    @BindView(R.id.main_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.empty_cursor_text)
    TextView emptyCursorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        docsMoved = false;

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(DELETED_KEY)) {
                deletedDocument = savedInstanceState.getParcelable(DELETED_KEY);
                deletedPosition = savedInstanceState.getInt(DELETED_POS);
            }
        }

        initWidgets();

        if (savedInstanceState == null && getIntent().hasExtra(NO_PINNED)) {
            if (getIntent().getBooleanExtra(NO_PINNED, false)) {
                Snackbar.make(findViewById(R.id.constraintLayout), getString(R.string.no_pinned_docs_msg), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void initWidgets() {
        adapter = new GridAdapter(this, null);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(columnCount(), StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                adapter.move(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
                docsMoved = true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Document document = adapter.getDocumentAtPosition(position);
                deletedDocument = document;
                deletedPosition = position;

                DeleteDialogFragment fragment = new DeleteDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString(DeleteDialogFragment.EXTRA_DOCUMENT_NAME, document.getTitle());
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), DELETE_DIALOG_TAG);
            }
        }).attachToRecyclerView(recyclerView);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.settings) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawers();
            } else if (id == R.id.sync) {
                startDocumentSync();
                drawerLayout.closeDrawers();
            } else if (id == R.id.logout) logout();
            return false;
        });

        View view = navigationView.getHeaderView(0);
        String name = SharedPreferenceUtils.getUsername(this);
        TextView user = view.findViewById(R.id.nav_name);
        user.setText(name);
        TextView email = view.findViewById(R.id.nav_email);
        email.setText(SharedPreferenceUtils.getEmail(this));
        recyclerView.setAdapter(adapter);
    }

    private int columnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return Math.max(displayMetrics.widthPixels / 500, 2);
    }

    private void startDocumentSync() {
        if (Utils.isConnected(MainActivity.this)) {
            DocumentService.sync(MainActivity.this, SharedPreferenceUtils.getUserId(MainActivity.this));
        } else {
            InternetDialogFragment fragment = new InternetDialogFragment();
            fragment.show(getSupportFragmentManager(), REQUEST_INTERNET_DIALOG);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == LOADER_ID)
            return new CursorLoader(this, Contract.Entry.CONTENT_URI, null, null, null, Contract.Entry.COLUMN_PRIORITY + " DESC");
        return null;
    }

    @Override
    public void onDocumentClicked(Document document) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EditActivity.PARCEL_KEY, document);
        startActivity(intent);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0) {
            showProgressBar(false);
            showEmptyCursorMessage(true);
            return;
        }
        showEmptyCursorMessage(false);
        showProgressBar(false);
        adapter.swapCursor(data);
    }

    private void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        recyclerView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }

    private void logout() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        SharedPreferenceUtils.invalidateUserDetails(MainActivity.this);
        DocumentService.delete(this, null);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignIn.getClient(this, googleSignInOptions).signOut();
        WidgetService.updateWidget(this);
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showEmptyCursorMessage(boolean show) {
        emptyCursorMessage.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        recyclerView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (deletedDocument != null) {
            outState.putParcelable(DELETED_KEY, deletedDocument);
            outState.putInt(DELETED_POS, deletedPosition);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfirm(DialogInterface dialogInterface) {
        DocumentService.delete(this, deletedDocument);
        adapter.deletePosition(deletedPosition);
        Snackbar.make(drawerLayout, "Deleted " + deletedDocument.getTitle(), Snackbar.LENGTH_LONG).show();
        deletedDocument = null;
        deletedPosition = -1;
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAccept(DialogInterface dialogInterface) {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    @Override
    public void onDeny(DialogInterface dialogInterface) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(syncDocumentsReciever);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (docsMoved) updatePositions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DocumentService.ACTION_SYNC_STARTED);
        intentFilter.addAction(DocumentService.ACTION_SYNC_END);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        syncDocumentsReciever = new SyncDocumentsReceiver();
        registerReceiver(syncDocumentsReciever, intentFilter);
    }

    private void updatePositions() {
        List<Document> documents = adapter.getDocuments();
        for (int i = 0; i < documents.size(); i++) {
            Document document = documents.get(i);
            int newPriority = documents.size() - i;
            document.setPriority(newPriority);
            DocumentService.move(MainActivity.this, document);
        }
        docsMoved = false;
    }

    @OnClick({R.id.fab})
    protected void onFabButtonClicked() {
        Document document = new Document();
        document.setTitle("");
        document.setText("");
        document.setNew(true);
        document.setUserId(SharedPreferenceUtils.getUserId(MainActivity.this));
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra(EditActivity.PARCEL_KEY, document);
        startActivity(intent);
    }

    private class SyncDocumentsReceiver extends BroadcastReceiver {
        Snackbar snackbar;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DocumentService.ACTION_SYNC_STARTED.equals(action)) showProgressBar(true);
            else if (DocumentService.ACTION_SYNC_END.equals(action)) showProgressBar(false);
            else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                if (!Utils.isConnected(MainActivity.this)) {
                    snackbar = Snackbar.make(drawerLayout, "Offline Mode", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                } else if (snackbar != null) snackbar.dismiss();
            }
        }
    }
}