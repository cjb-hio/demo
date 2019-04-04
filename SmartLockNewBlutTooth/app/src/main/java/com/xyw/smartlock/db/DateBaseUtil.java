package com.xyw.smartlock.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库的增删改查
 */
public class DateBaseUtil {
    private DbHelper dbHelper;
    private LockFile lockFile;
    private LockRecord lockRecord;
    private UnLock unLock;

    public DateBaseUtil(Context context) {
        super();
        dbHelper = new DbHelper(context);
    }

    /**
     * 数据库增加数据操作
     */
    public boolean Insert1(LockFile lockFile) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql1 = "insert into " + DbHelper.LockFile_Table + "(LID,L_NAME,L_ADDR,L_GPS_X,L_GPS_Y,L_CREATE_DT," +
                "L_CREATE_OP,L_BOX_NO,L_BOX_TYPE,ZONE_NO,KEY_VER,PASSNUM,ZONE_NAME)" +
                " values( '" + lockFile.getLID() + "' ," + "'" + lockFile.getL_NAME() + ""
                + "' ," + "'" + lockFile.getL_ADDR() + "' ," + "'" + lockFile.getL_GPS_X() + ""
                + "' ," + "'" + lockFile.getL_GPS_Y() + "' ," + "'" + lockFile.getL_CREATE_DT() + ""
                + "' ," + "'" + lockFile.getL_CREATE_OP() + "' ," + "'" + lockFile.getL_BOX_NO() + ""
                + "' ," + "'" + lockFile.getL_BOX_TYPE() + "" + "' ," + "'" + lockFile.getZONE_NO() + ""
                + "' ," + "'" + lockFile.getKEY_VER() + "" + "' ," + "'" + lockFile.getPASSNUM() + ""
                + "' ," + "'" + lockFile.getZONE_NAME() + "')";
        try {
            db.execSQL(sql1);
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            db.close();
        }
    }

    public boolean Insert2(LockRecord lockRecord) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql2 = "insert into " + DbHelper.UnLockRecord_Table + "(OP_NAME,L_RET,L_OPTYPE,L_GPS_X,L_GPS_Y,L_CREATE_DT" +
                ",LID,L_CREATE_OP)values('" + lockRecord.getOP_NAME() + "' ," + "'" + lockRecord.getL_RET() + ""
                + "' ," + "'" + lockRecord.getL_OPTYPE() + "' ," + "'" + lockRecord.getL_GPS_X() + ""
                + "' ," + "'" + lockRecord.getL_GPS_Y() + "' ," + "'" + lockRecord.getL_CREATE_DT() + ""
                + "' ," + "'" + lockRecord.getLID() + "' ," + "'" + lockRecord.getL_CREATE_OP() + "')";
        try {
            db.execSQL(sql2);
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            db.close();
        }
    }

    public boolean Insert3(UnLock unLock) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql3 = "insert into " + DbHelper.UnLock_Table + "(Lid,GPS_X,GPS_Y,OP_NO,OP_TYPE,OP_RET," +
                "OP_DATETIME,USER_CONTEXT)values("
                + "'" + unLock.getLid() + "' ," + "'" + unLock.getGPS_X() + ""
                + "' ," + "'" + unLock.getGPS_Y() + "' ," + "'" + unLock.getOP_NO() + ""
                + "' ," + "'" + unLock.getOP_TYPE() + "' ," + "'" + unLock.getOP_RET() + ""
                + "' ," + "'" + unLock.getOP_DATETIME() + "' ," + "'" + unLock.getUSER_CONTEXT() + "')";
        try {
            db.execSQL(sql3);
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            db.close();
        }
    }

    public boolean insert4(MeterId meterId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql4 = "insert into " + DbHelper.meter_Table + "(LID, meter_1, meter_12, meter_3, meter_4, meter_5, " +
                "meter_6, meter_7, meter_8, meter_9, meter_10, meter_11, meter_12, meter_13, meter_14, meter_15) values (" +
                "'" + meterId.getLid() + "', '" + meterId.getMeter1() + "', '" + meterId.getMeter2() + "', '" + meterId.getMeter3() + "', '" +
                meterId.getMeter4() + "', '" + meterId.getMeter5() + "', '" + meterId.getMeter6() + "', '" + meterId.getMeter7() + "', '" +
                meterId.getMeter8() + "', '" + meterId.getMeter9() + "', '" + meterId.getMeter10() + "', '" + meterId.getMeter11() + "', '" +
                meterId.getMeter12() + "', '" + meterId.getMeter13() + "', '" + meterId.getMeter14() + "', '" + meterId.getMeter15() + "')";
        try {
            db.execSQL(sql4);
            return true;
        } catch (SQLException e){
            return false;
        } finally {
            db.close();
        }
    }

    public boolean delete4(String lockId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String delete_sql4 = "delete from " + DbHelper.meter_Table + " where LID = " + lockId;
        try {
            db.execSQL(delete_sql4);
            return true;
        } catch (SQLException e){
            return false;
        } finally {
            db.close();
        }
    }

    public MeterId queryMeterId(String lockId) {
        MeterId.Builder builder = new MeterId.Builder();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.query(DbHelper.meter_Table, null, "LID = ?", new String[]{lockId}, null, null, null);
        String sql = "select * from " + DbHelper.meter_Table + " where LID = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{lockId});
        if (cursor.getCount() == 0) {
            return null;
        }
        while (cursor.moveToNext()) {
            String meter1 = getMeter(cursor, "meter_1");
            String meter2 = getMeter(cursor, "meter_2");
            String meter3 = getMeter(cursor, "meter_3");
            String meter4 = getMeter(cursor, "meter_4");
            String meter5 = getMeter(cursor, "meter_5");
            String meter6 = getMeter(cursor, "meter_6");
            String meter7 = getMeter(cursor, "meter_7");
            String meter8 = getMeter(cursor, "meter_8");
            String meter9 = getMeter(cursor, "meter_9");
            String meter10 = getMeter(cursor, "meter_10");
            String meter11 = getMeter(cursor, "meter_11");
            String meter12 = getMeter(cursor, "meter_12");
            String meter13 = getMeter(cursor, "meter_13");
            String meter14 = getMeter(cursor, "meter_14");
            String meter15 = getMeter(cursor, "meter_15");
            builder.Lid(lockId)
            .meter1(meter1)
            .meter2(meter2)
            .meter3(meter3)
            .meter4(meter4)
            .meter5(meter5)
            .meter6(meter6)
            .meter7(meter7)
            .meter8(meter8)
            .meter9(meter9)
            .meter10(meter10)
            .meter11(meter11)
            .meter12(meter12)
            .meter13(meter13)
            .meter14(meter14)
            .meter15(meter15);
        }
        cursor.close();
        db.close();
        return builder.build();
    }

    /**
     * 判断是否存在该列，不存在则返回""
     * @param cursor
     * @param column
     * @return
     */
    private String getMeter(Cursor cursor, String column) {
        if (cursor.getColumnIndex(column) != -1) {
            return cursor.getString(cursor.getColumnIndex(column));
        }
        return "";
    }


    /**
     * 数据库减少数据操作
     */
    /**
     * 数据库修改数据操作
     */
    public void deleteUnLock(){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from "+ DbHelper.UnLock_Table);
        db.close();
    }
    /**
     * 数据库查询数据操作
     */
    //锁档案数据库查询
    public List<LockFile> queryLockFill() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<LockFile> list = new ArrayList<>();
        Cursor cursor = db.query(DbHelper.LockFile_Table, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            LockFile lockFile = new LockFile();
            lockFile.setLID(cursor.getString(cursor.getColumnIndex("LID")));
            lockFile.setL_NAME(cursor.getString(cursor.getColumnIndex("L_NAME")));
            lockFile.setL_ADDR(cursor.getString(cursor.getColumnIndex("L_ADDR")));
            lockFile.setL_GPS_X(cursor.getString(cursor.getColumnIndex("L_GPS_X")));
            lockFile.setL_GPS_Y(cursor.getString(cursor.getColumnIndex("L_GPS_Y")));
            lockFile.setL_CREATE_DT(cursor.getString(cursor.getColumnIndex("L_CREATE_DT")));
            lockFile.setL_CREATE_OP(cursor.getString(cursor.getColumnIndex("L_CREATE_OP")));
            lockFile.setL_BOX_NO(cursor.getString(cursor.getColumnIndex("L_BOX_NO")));
            lockFile.setL_BOX_TYPE(cursor.getString(cursor.getColumnIndex("L_BOX_TYPE")));
            lockFile.setKEY_VER(cursor.getString(cursor.getColumnIndex("KEY_VER")));
            lockFile.setZONE_NO(cursor.getString(cursor.getColumnIndex("ZONE_NO")));
            lockFile.setPASSNUM(cursor.getString(cursor.getColumnIndex("PASSNUM")));
            lockFile.setZONE_NAME(cursor.getString(cursor.getColumnIndex("ZONE_NAME")));
            list.add(lockFile);
        }
        System.out.println("list=" + list);
        Log.e("TAG", String.valueOf(list));
        cursor.close();
        db.close();
        return list;
    }

    //开锁数据库查询
    public List<UnLock> queryUnLock() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<UnLock> list = new ArrayList<>();
        Cursor cursor = db.query(DbHelper.UnLock_Table, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            UnLock unLock = new UnLock();
            unLock.setLid(cursor.getString(cursor.getColumnIndex("LID")));
            unLock.setGPS_X(cursor.getString(cursor.getColumnIndex("GPS_X")));
            unLock.setGPS_Y(cursor.getString(cursor.getColumnIndex("GPS_Y")));
            unLock.setOP_NO(cursor.getString(cursor.getColumnIndex("OP_NO")));
            unLock.setOP_TYPE(cursor.getString(cursor.getColumnIndex("OP_TYPE")));
            unLock.setOP_RET(cursor.getString(cursor.getColumnIndex("OP_RET")));
            unLock.setOP_DATETIME(cursor.getString(cursor.getColumnIndex("OP_DATETIME")));
            unLock.setUSER_CONTEXT(cursor.getString(cursor.getColumnIndex("USER_CONTEXT")));
            list.add(unLock);
        }
        System.out.println("list=" + list);
        Log.e("TAG", String.valueOf(list));
        cursor.close();
        db.close();
        return list;
    }


    //开锁记录查询

    public List<LockRecord> queryAll_LockRecord(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<LockRecord> list = new ArrayList<LockRecord>();
        Cursor cursor = db.query(DbHelper.UnLockRecord_Table,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            LockRecord lockRecord = new LockRecord();
            lockRecord.setOP_NAME(cursor.getString(cursor.getColumnIndex("OP_NAME")));
            lockRecord.setL_RET(cursor.getString(cursor.getColumnIndex("L_RET")));
            lockRecord.setL_OPTYPE(cursor.getString(cursor.getColumnIndex("L_OPTYPE")));
            lockRecord.setL_GPS_X(cursor.getString(cursor.getColumnIndex("L_GPS_X")));
            lockRecord.setL_GPS_Y(cursor.getString(cursor.getColumnIndex("L_GPS_Y")));
            lockRecord.setL_CREATE_DT(cursor.getString(cursor.getColumnIndex("L_CREATE_DT")));
            lockRecord.setLID(cursor.getString(cursor.getColumnIndex("LID")));
            lockRecord.setL_CREATE_OP(cursor.getString(cursor.getColumnIndex("L_CREATE_OP")));
            list.add(lockRecord);
        }
        cursor.close();
        db.close();
        return list;
    }
    public List<LockRecord> queryDates_LockRecord(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<LockRecord> list = new ArrayList<LockRecord>();
        Cursor cursor = db.rawQuery("select * from "+ DbHelper.UnLockRecord_Table+" where L_CREATE_DT between '2016/5/19' and '2016/5/29'",null);
        while (cursor.moveToNext()){
            LockRecord lockRecord = new LockRecord();
            lockRecord.setOP_NAME(cursor.getString(cursor.getColumnIndex("OP_NAME")));
            lockRecord.setL_RET(cursor.getString(cursor.getColumnIndex("L_RET")));
            lockRecord.setL_OPTYPE(cursor.getString(cursor.getColumnIndex("L_OPTYPE")));
            lockRecord.setL_GPS_X(cursor.getString(cursor.getColumnIndex("L_GPS_X")));
            lockRecord.setL_GPS_Y(cursor.getString(cursor.getColumnIndex("L_GPS_Y")));
            lockRecord.setL_CREATE_DT(cursor.getString(cursor.getColumnIndex("L_CREATE_DT")));
            lockRecord.setLID(cursor.getString(cursor.getColumnIndex("LID")));
            lockRecord.setL_CREATE_OP(cursor.getString(cursor.getColumnIndex("L_CREATE_OP")));
            list.add(lockRecord);
        }
        cursor.close();
        db.close();
        return list;
    }




}
