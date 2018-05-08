package wxm.dofcalculator.define

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

import wxm.androidutil.DBHelper.IDBRow

/**
 * device data(camera & lens)
 * Created by WangXM on2016/9/1.
 */
@DatabaseTable(tableName = "tbDevice")
class DeviceItem : IDBRow<Int> {

    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private var _id: Int = 0

    // for name
    @DatabaseField(columnName = "name", canBeNull = false, dataType = DataType.STRING)
    var name: String? = null

    // for camera
    @DatabaseField(columnName = "camera_id", foreign = true, foreignColumnName = CameraItem.FIELD_ID, canBeNull = false)
    var camera: CameraItem? = null

    // for lens
    @DatabaseField(columnName = "lens_id", foreign = true, foreignColumnName = LensItem.FIELD_ID, canBeNull = false)
    var lens: LensItem? = null

    init {
        id = -1
        name = ""
    }

    override fun hashCode(): Int {
        return name!!.hashCode() + id!!
    }

    override fun getID(): Int? {
        return _id
    }

    override fun setID(integer: Int?) {
        _id = integer!!
    }

    companion object {
        const val FIELD_NAME = "name"
        const val FIELD_ID = "_id"
    }
}
