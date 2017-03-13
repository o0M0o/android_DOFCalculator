package wxm.dofcalculator.define;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cn.wxm.andriodutillib.DBHelper.IDBRow;
import cn.wxm.andriodutillib.util.UtilFun;

/**
 * camera数据类
 * Created by 123 on 2016/9/1.
 */
@DatabaseTable(tableName = "tbCamera")
public class CameraItem
        implements IDBRow<Integer> {
    private static final String TAG = "CameraItem";

    public final static String FIELD_NAME       = "name";
    public final static String FIELD_ID         = "_id";

    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private int _id;

    @DatabaseField(columnName = "name", canBeNull = false, dataType = DataType.STRING)
    private String name;

    /**
     * 相机底片对角线尺寸，单位为mm
     */
    @DatabaseField(columnName = "filmSize", dataType = DataType.INTEGER, canBeNull = false)
    private int filmSize;

    /**
     * 相机底片像素数量
     */
    @DatabaseField(columnName = "pixelCount", dataType = DataType.INTEGER, canBeNull = false)
    private int pixelCount;

    public CameraItem() {
        setID(-1);
        setName("");

    }

    // for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // for filmSize
    public int getFilmSize()    {
        return filmSize;
    }

    public void setFilmSize(int fs) {
        filmSize = fs;
    }

    // for filmSize
    public int getPixelCount()    {
        return pixelCount;
    }

    public void setPixelCount(int pc) {
        pixelCount = pc;
    }

    @Override
    public int hashCode()   {
        return getName().hashCode() + getID()
                + getFilmSize() + getPixelCount();
    }

    @Override
    public Integer getID() {
        return _id;
    }

    @Override
    public void setID(Integer integer) {
        _id = integer;
    }
}
