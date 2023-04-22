package com.example.trevorsandroidapplication.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.trevorsandroidapplication.objects.User;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserData.db";

    private static final String TABLE_NAME = "user_data";
    private static final String UUID_COLUMN_NAME = "uuid";
    private static final String NAME_COLUMN_NAME = "name";
    private static final String PASSWORD_COLUMN_NAME = "password";
    private static final String WEIGHT_GOAL_COLUMN_NAME = "weight_goal";

    private static final String SQL_CREATE_QUERY = "CREATE TABLE IF NOT EXISTS %s " +
            "(%s TEXT PRIMARY KEY, %s TEXT, %s TEXT, %s INT)";

    private final SQLiteDatabase writableDatabase;
    private final SQLiteDatabase readableDatabase;

    public UserDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.writableDatabase = getWritableDatabase();
        this.readableDatabase = getReadableDatabase();


        onCreate(writableDatabase);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(String.format(SQL_CREATE_QUERY, TABLE_NAME, UUID_COLUMN_NAME,
                NAME_COLUMN_NAME, PASSWORD_COLUMN_NAME, WEIGHT_GOAL_COLUMN_NAME));
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
    public CompletableFuture<User> getUser(String name, String password) {
        return CompletableFuture.supplyAsync(() -> {
            Cursor cursor = readableDatabase.query(
                    TABLE_NAME, null, "name=? AND password=?",
                    new String[] { name, password }, null, null, null, null);

            if (cursor.moveToNext()) {
                UUID cursorUuid = UUID.fromString(cursor.getString(cursor.getColumnIndex(UUID_COLUMN_NAME)));
                String cursorName = cursor.getString(cursor.getColumnIndex(NAME_COLUMN_NAME));
                String cursorPassword = cursor.getString(cursor.getColumnIndex(PASSWORD_COLUMN_NAME));
                int weightGoal = cursor.getInt(cursor.getColumnIndex(WEIGHT_GOAL_COLUMN_NAME));

                cursor.close();
                return new User(cursorUuid, cursorName, cursorPassword, weightGoal);
            }

            cursor.close();
            return createNewUserAccount(name, password);
        });
    }

    /**
     * The weight value is the only one that
     * ever changes, so this is the only update
     * function that actually needs to exist
     */
    public void updateUserWeight(User user) {
        writableDatabase.execSQL(String.format("UPDATE %s SET %s=%s WHERE %s='%s'",
                TABLE_NAME, WEIGHT_GOAL_COLUMN_NAME, user.getWeightGoal(), UUID_COLUMN_NAME, user.getUuid()));
    }

    private User createNewUserAccount(String name, String password) {
        ContentValues contentValues = new ContentValues();
        UUID uuid = UUID.randomUUID();

        contentValues.put(UUID_COLUMN_NAME, uuid.toString());
        contentValues.put(NAME_COLUMN_NAME, name);
        contentValues.put(PASSWORD_COLUMN_NAME, password);
        contentValues.put(WEIGHT_GOAL_COLUMN_NAME, -1);

        writableDatabase.insert(TABLE_NAME, null, contentValues);
        return new User(uuid, name, password, -1);
    }
}
