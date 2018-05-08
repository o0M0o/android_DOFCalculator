package wxm.dofcalculator.define

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

import java.math.BigDecimal

import wxm.androidutil.DBHelper.IDBRow

/**
 * camera data
 * Created by WangXM on2016/9/1.
 */
@DatabaseTable(tableName = "tbCamera")
class CameraItem : IDBRow<Int> {

    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private var _id: Int = 0

    // for name
    @DatabaseField(columnName = "name", canBeNull = false, dataType = DataType.STRING)
    var name: String? = null

    // for filmName
    @DatabaseField(columnName = "filmName", canBeNull = false, dataType = DataType.STRING)
    var filmName: String? = null

    /**
     * camera film size (unit : mm)
     */
    // for filmSize
    @DatabaseField(columnName = "filmSize", dataType = DataType.INTEGER, canBeNull = false)
    var filmSize: Int = 0

    /**
     * pixel count for camera
     */
    // for pixel count
    @DatabaseField(columnName = "pixelCount", dataType = DataType.INTEGER, canBeNull = false)
    var pixelCount: Int = 0

    /**
     * get COC (circle of confusion, unit : mm)
     * @return      COC diameter
     */
    val cameraCOC: BigDecimal
        get() = BigDecimal((filmSize.toFloat() / 1730).toDouble())

    init {
        id = -1
        name = ""
    }

    override fun hashCode(): Int {
        return (name!!.hashCode() + id!!
                + filmSize + pixelCount)
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
