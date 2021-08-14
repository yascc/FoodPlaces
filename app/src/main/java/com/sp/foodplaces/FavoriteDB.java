package com.sp.foodplaces;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class FavoriteDB extends SQLiteOpenHelper {

    private static int DB_VERSION = 1;
    private static String DATABASE_NAME = "FavoriteDB";
    private static String TABLE_NAME = "favoriteTable";
    public static String KEY_ID = "id";
    public static String ITEM_TITLE = "itemTitle";
    public static String ITEM_IMAGE = "itemImage";

    public static String ITEM_RATING = "itemRating";
    public static String ITEM_ADDRESS = "itemAddress";

    public static String FAVORITE_STATUS = "fStatus";

    //Wong: new parameter for favorite
    public  static String ITEM_PLACEID = "placeID";

    private static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ITEM_TITLE + " TEXT,"
            + ITEM_RATING + " TEXT,"
            + ITEM_ADDRESS + " TEXT,"
            + ITEM_IMAGE + " TEXT,"
            + FAVORITE_STATUS + " TEXT,"
            + ITEM_PLACEID + " TEXT)";

    public FavoriteDB(Context context) { super(context,DATABASE_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) { sqLiteDatabase.execSQL(CREATE_TABLE);
        Log.d("FavoriteDB created","Table created (first run)");}

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String upgradeQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(upgradeQuery);
        onCreate(sqLiteDatabase);
    }

    // create empty table
    public void insertEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        // enter your value
        for (int x = 1; x < 21; x++) {
            cv.put(KEY_ID, x);
            cv.put(FAVORITE_STATUS, "0");
            db.insert(TABLE_NAME,null, cv);
        }
    }

    // insert data into database
    public void insertIntoTheDatabase(String item_title, String item_image, String item_rating,
                                      String item_address, String id, String fav_status, String placeID) {
        SQLiteDatabase db;
        db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ITEM_TITLE, item_title);
        cv.put(ITEM_IMAGE, item_image);
        cv.put(ITEM_RATING, item_rating);
        cv.put(ITEM_ADDRESS, item_address);
        //key_id is now auto primary key, data input can be remove
        //cv.put(KEY_ID, id);
        cv.put(FAVORITE_STATUS, fav_status);
        cv.put(ITEM_PLACEID, placeID);
        long result = db.insert(TABLE_NAME,null, cv);
        if(result == -1){
            Log.d("FavoriteDB inserted", item_title + ", favstatus: " + fav_status + ". Insert failed" + cv);
        }else {
            Log.d("FavoriteDB inserted", item_title + ", favstatus: " + fav_status + ". Insert successfully" + cv);
        }
    }

    // read all data
    public Cursor read_all_data(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME + " where " + ITEM_PLACEID + "= \"" + id + "\"";
        return db.rawQuery(sql,null,null);
    }

    // remove line from database
    public void remove_fav(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + TABLE_NAME + " SET  "+ FAVORITE_STATUS + " ='0' WHERE " + ITEM_PLACEID + "= \"" + id + "\"";
        db.execSQL(sql);
        Log.d("FavoriteDB removed", id);

    }

    // select all favorite list
    public Cursor select_all_favorite_list() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM "+TABLE_NAME+" WHERE "+FAVORITE_STATUS+" ='1'";
        return db.rawQuery(sql,null,null);
    }

}
