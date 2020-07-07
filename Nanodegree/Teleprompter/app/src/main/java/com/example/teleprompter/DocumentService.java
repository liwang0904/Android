package com.example.teleprompter;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DocumentService extends IntentService {
    private static FirebaseDatabase database;

    private static final String ACTION_INSERT = "action-insert-document";
    public static final String ACTION_UPDATE = "action-update-document";
    public static final String ACTION_DELETE = "action-delete-document";
    private static final String ACTION_MOVE = "action-query-document";
    private static final String ACTION_SYNC = "action-sync-documents";
    private static final String ACTION_SYNC_STARTUP = "action-tutorial-document";
    public static final String ACTION_SYNC_STARTED = "com.example.prompter.started";
    public static final String ACTION_SYNC_END = "com.example.prompter.sync.ended";

    private static final String EXTRA_KEY = "extra_key";

    private static final String CHILD_PRIORITY = "priority";
    private static final String CHILD_PING = "ping";
    private static final String CHILD_DOCS = "documents";

    public static DatabaseReference getFirebaseDatabaseReference() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            database.getReference("documents").keepSynced(true);
        }
        return database.getReference();
    }

    public DocumentService() {
        super(DocumentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        if (ACTION_INSERT.equals(action)) handleInsert(intent);
        else if (ACTION_UPDATE.equals(action)) handleUpdate(intent);
        else if (ACTION_DELETE.equals(action)) handleDelete(intent);
        else if (ACTION_MOVE.equals(action)) handleMove(intent);
        else if (ACTION_SYNC.equals(action)) handleSync(intent);
        else if (ACTION_SYNC_STARTUP.equals(action)) handleSyncStartup(intent);
    }

    public static void insert(Context context, Document document) {
        Intent intent = new Intent(context, DocumentService.class);
        intent.putExtra(EXTRA_KEY, document);
        intent.setAction(ACTION_INSERT);
        context.startService(intent);
    }

    private void handleInsert(Intent intent) {
        Document document = intent.getParcelableExtra(EXTRA_KEY);
        String cloudId = getFirebaseDatabaseReference().child(CHILD_DOCS).child(document.getUserId()).push().getKey();
        document.setCloudId(cloudId);
        ContentValues contentValues = document.getContentValues();
        Uri uri = getContentResolver().insert(Contract.Entry.CONTENT_URI, contentValues);

        if (uri == null) return;

        String string = uri.getLastPathSegment();
        int id = Integer.parseInt(string);
        SharedPreferenceUtils.setLastStoredId(this, id);
        SharedPreferenceUtils.setLastStoredCloudId(this, cloudId);
        document.setPriority(id);
        getFirebaseDatabaseReference().child(CHILD_DOCS).child(document.getUserId()).child(cloudId).setValue(document);
    }

    public static void update(Context context, Document document) {
        Intent intent = new Intent(context, DocumentService.class);
        intent.putExtra(EXTRA_KEY, document);
        intent.setAction(ACTION_UPDATE);
        context.startService(intent);
    }

    private void handleUpdate(Intent intent) {
        Document document = intent.getParcelableExtra(EXTRA_KEY);
        ContentValues contentValues = document.getContentValues();
        getContentResolver().delete(Contract.Entry.CONTENT_URI, Contract.Entry._ID + "=?", new String[]{Integer.toString(document.getId())});
        Uri uri = getContentResolver().insert(Contract.Entry.CONTENT_URI, contentValues);
        String id = uri.getLastPathSegment();
        document.setPriority(Integer.parseInt(id));
        getFirebaseDatabaseReference().child(CHILD_DOCS).child(document.getUserId()).child(document.getCloudId()).setValue(document);
    }

    public static void delete(Context context, Document document) {
        Intent intent = new Intent(context, DocumentService.class);
        intent.putExtra(EXTRA_KEY, document);
        intent.setAction(ACTION_DELETE);
        context.startService(intent);
    }

    private void handleDelete(Intent intent) {
        Document document = intent.getParcelableExtra(EXTRA_KEY);
        if (document != null) {
            Uri uri = document.getUri();
            getContentResolver().delete(uri, null, null);
            document.setUserId(SharedPreferenceUtils.getUserId(this));
            getFirebaseDatabaseReference().child(CHILD_DOCS).child(document.getUserId()).child(document.getCloudId()).removeValue();
        } else getContentResolver().delete(Contract.Entry.CONTENT_URI, null, null);
    }

    public static void move(Context context, Document document) {
        Intent intent = new Intent(context, DocumentService.class);
        intent.putExtra(EXTRA_KEY, document);
        intent.setAction(ACTION_MOVE);
        context.startService(intent);
    }

    private void handleMove(Intent intent) {
        Document document = intent.getParcelableExtra(EXTRA_KEY);
        ContentValues contentValues = document.getContentValues();
        getContentResolver().update(Contract.Entry.CONTENT_URI, contentValues, Contract.Entry._ID + "=?", new String[]{Integer.toString(document.getId())});
        getFirebaseDatabaseReference().child(CHILD_DOCS).child(document.getUserId()).child(document.getCloudId()).child(CHILD_PRIORITY).setValue(document.getPriority());
    }

    public static void sync(Context context, String userId) {
        Intent intent = new Intent(context, DocumentService.class);
        intent.putExtra(EXTRA_KEY, userId);
        intent.setAction(ACTION_SYNC);
        context.startService(intent);
    }

    private void sendSyncBroadcast(boolean sync) {
        Intent intent = new Intent();
        if (sync) intent.setAction(ACTION_SYNC_STARTED);
        else intent.setAction(ACTION_SYNC_END);
        sendBroadcast(intent);
    }

    private void removeChildPing(String userId) {
        getFirebaseDatabaseReference().child(CHILD_DOCS).child(userId).child(CHILD_PING).removeValue();
    }

    private void handleSync(Intent intent) {
        sendSyncBroadcast(true);
        Document document = new Document();
        document.setPing(true);
        final String userId = intent.getStringExtra(EXTRA_KEY);
        getFirebaseDatabaseReference().child(CHILD_DOCS).child(userId).child(CHILD_PING).setValue(document, (databaseError, databaseReference) -> getFirebaseDatabaseReference().child(CHILD_DOCS).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ContentValues> contentValuesList = new ArrayList<>();
                if (dataSnapshot.getChildrenCount() == 0) {
                    sendSyncBroadcast(false);
                    removeChildPing(userId);
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Document document = snapshot.getValue(Document.class);
                    if (document != null && !document.isPing()) {
                        document.setUserId(userId);
                        contentValuesList.add(document.getContentValues());
                    }
                }

                ContentValues[] contentValues = contentValuesList.toArray(new ContentValues[1]);
                getContentResolver().delete(Contract.Entry.CONTENT_URI, null, null);
                getContentResolver().bulkInsert(Contract.Entry.CONTENT_URI, contentValues);
                sendSyncBroadcast(false);
                removeChildPing(userId);
                WidgetService.updateWidget(DocumentService.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                sendSyncBroadcast(false);
            }
        }));
    }

    public static void syncStartup(Context context, String userId) {
        Intent intent = new Intent(context, DocumentService.class);
        intent.setAction(ACTION_SYNC_STARTUP);
        intent.putExtra(EXTRA_KEY, userId);
        context.startService(intent);
    }

    private void handleSyncStartup(final Intent intent) {
        final String userId = intent.getStringExtra(EXTRA_KEY);
        Document document = new Document();
        document.setPing(true);

        getFirebaseDatabaseReference().child(CHILD_DOCS).child(userId).child(CHILD_PING).setValue(document, (databaseError, databaseReference) -> getFirebaseDatabaseReference().child(CHILD_DOCS).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 1) {
                    removeChildPing(userId);
                    handleSync(intent);
                    return;
                }
                removeChildPing(userId);
                handleSyncStartup(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        }));
    }
}