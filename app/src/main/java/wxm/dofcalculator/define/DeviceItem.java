package wxm.dofcalculator.define;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import wxm.androidutil.DBHelper.IDBRow;

/**
 * device data(camera & lens)
 * Created by WangXM on2016/9/1.
 */
@DatabaseTable(tableName = "tbDevice")
public class DeviceItem
        implements IDBRow<Integer> {
    private static final String TAG = "DeviceItem";

    public final static String FIELD_NAME       = "name";
    public final static String FIELD_ID         = "_id";

    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private int _id;

    @DatabaseField(columnName = "name", canBeNull = false, dataType = DataType.STRING)
    private String name;

    @DatabaseField(columnName = "camera_id", foreign = true, foreignColumnName = CameraItem.FIELD_ID,
            canBeNull = false)
    private CameraItem camera;

    @DatabaseField(columnName = "lens_id", foreign = true, foreignColumnName = LensItem.FIELD_ID,
            canBeNull = false)
    private LensItem lens;

    public DeviceItem() {
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

    // for camera
    public CameraItem getCamera()    {
        return camera;
    }

    public void setCamera(CameraItem ca) {
        camera = ca;
    }

    // for lens
    public LensItem getLens()    {
        return lens;
    }

    public void setLens(LensItem li) {
        lens = li;
    }

    @Override
    public int hashCode()   {
        return getName().hashCode() + getID();
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
