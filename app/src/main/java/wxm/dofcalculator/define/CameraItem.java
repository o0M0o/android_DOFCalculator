package wxm.dofcalculator.define;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;

import wxm.androidutil.DBHelper.IDBRow;

/**
 * camera data
 * Created by WangXM on2016/9/1.
 */
@DatabaseTable(tableName = "tbCamera")
public class CameraItem
        implements IDBRow<Integer> {
    public final static String FIELD_NAME       = "name";
    public final static String FIELD_ID         = "_id";

    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private int _id;

    @DatabaseField(columnName = "name", canBeNull = false, dataType = DataType.STRING)
    private String name;

    @DatabaseField(columnName = "filmName", canBeNull = false, dataType = DataType.STRING)
    private String filmName;

    /**
     * camera film size (unit : mm)
     */
    @DatabaseField(columnName = "filmSize", dataType = DataType.INTEGER, canBeNull = false)
    private int filmSize;

    /**
     * pixel count for camera
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

    // for filmName
    public String getFilmName()    {
        return filmName;
    }

    public void setFilmName(String fn) {
        filmName = fn;
    }

    // for pixel count
    public int getPixelCount()    {
        return pixelCount;
    }

    public void setPixelCount(int pc) {
        pixelCount = pc;
    }

    /**
     * get COC (circle of confusion, unit : mm)
     * @return      COC diameter
     */
    public BigDecimal getCameraCOC()    {
        return new BigDecimal((float)filmSize/1730);
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
