package wxm.dofcalculator.define;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import wxm.androidutil.DBHelper.IDBRow;

/**
 * lens data
 * Created by WangXM on2016/9/1.
 */
@DatabaseTable(tableName = "tbLens")
public class LensItem
        implements IDBRow<Integer> {
    private static final String TAG = "LensItem";

    public final static String FIELD_NAME       = "name";
    public final static String FIELD_ID         = "_id";

    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private int _id;

    @DatabaseField(columnName = "name", canBeNull = false, dataType = DataType.STRING)
    private String name;

    /**
     * max focal length for lens (unit : mm)
     */
    @DatabaseField(columnName = "maxFocal", dataType = DataType.INTEGER, canBeNull = false)
    private int maxFocal;

    /**
     * min focal length for lens (unit : mm)
     */
    @DatabaseField(columnName = "minFocal", dataType = DataType.INTEGER, canBeNull = false)
    private int minFocal;


    public LensItem() {
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

    // for maxFocal
    public int getMaxFocal()    {
        return maxFocal;
    }

    public void setMaxFocal(int mf) {
        maxFocal = mf;
    }

    // for minFocal
    public int getMinFocal()    {
        return minFocal;
    }

    public void setMinFocal(int mf) {
        minFocal = mf;
    }

    @Override
    public int hashCode()   {
        return getName().hashCode() + getID()
                + getMaxFocal() + getMinFocal();
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
