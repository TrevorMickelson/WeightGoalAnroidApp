package com.example.trevorsandroidapplication.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.trevorsandroidapplication.objects.User;
import com.example.trevorsandroidapplication.objects.Weight;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WeightDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "WeightData.db";

    private static final String TABLE_NAME = "weight_data";
    private static final String WEIGHT_UUID_COLUMN_NAME = "weight_uuid";
    private static final String USER_UUID_COLUMN_NAME = "user_uuid";
    private static final String WEIGHT_COLUMN_AMOUNT = "weight";
    private static final String DATE_COLUMN_NAME = "date";

    private static final String SQL_CREATE_QUERY = "CREATE TABLE IF NOT EXISTS %s " +
            "(%s TEXT PRIMARY KEY, %s TEXT, %s INT, %s TEXT)";

    private final SQLiteDatabase writableDatabase;
    private final SQLiteDatabase readableDatabase;

    public WeightDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.writableDatabase = getWritableDatabase();
        this.readableDatabase = getReadableDatabase();

        onCreate(writableDatabase);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(String.format(SQL_CREATE_QUERY, TABLE_NAME, WEIGHT_UUID_COLUMN_NAME,
                USER_UUID_COLUMN_NAME, WEIGHT_COLUMN_AMOUNT, DATE_COLUMN_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * This function gets the user from the DB if it's already there,
     * if not, it creates a new user/stores that user in the DB, and
     * returns the new user object
     */
    @SuppressLint("Range, Recycle")
    public CompletableFuture<List<Weight>> getAllWeights(User user) {
        return CompletableFuture.supplyAsync(() -> {
            Cursor cursor = readableDatabase.query(
                    TABLE_NAME, null, USER_UUID_COLUMN_NAME + "=?",
                    new String[] { user.getUuid().toString() }, null, null, null, null);

            List<Weight> weightList = new ArrayList<>();

            while (cursor.moveToNext()) {
                UUID weightUuid = UUID.fromString(cursor.getString(cursor.getColumnIndex(WEIGHT_UUID_COLUMN_NAME)));
                int weightAmount = cursor.getInt(cursor.getColumnIndex(WEIGHT_COLUMN_AMOUNT));
                String weightDate = cursor.getString(cursor.getColumnIndex(DATE_COLUMN_NAME));

                weightList.add(new Weight(weightUuid, weightAmount, weightDate));
            }

            cursor.close();
            return weightList;
        });
    }

    public void addWeight(User user, Weight weight) {
        CompletableFuture.runAsync(() -> {
            ContentValues contentValues = new ContentValues();

            contentValues.put(WEIGHT_UUID_COLUMN_NAME, weight.getUuid().toString());
            contentValues.put(USER_UUID_COLUMN_NAME, user.getUuid().toString());
            contentValues.put(WEIGHT_COLUMN_AMOUNT, weight.getWeight());
            contentValues.put(DATE_COLUMN_NAME, weight.getDate());

            writableDatabase.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        });
    }

    public void removeWeight(Weight weight) {
        CompletableFuture.runAsync(() -> {
            writableDatabase.execSQL(String.format("DELETE FROM %S WHERE %s='%s'",
                    TABLE_NAME, WEIGHT_UUID_COLUMN_NAME, weight.getUuid()));
        });
    }
}
