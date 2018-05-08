package wxm.dofcalculator.define

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

import wxm.androidutil.DBHelper.IDBRow

/**
 * lens data
 * Created by WangXM on2016/9/1.
 */
@DatabaseTable(tableName = "tbLens")
class LensItem : IDBRow<Int> {

    @DatabaseField(generatedId = true, columnName = "_id", dataType = DataType.INTEGER)
    private var _id: Int = 0

    // for name
    @DatabaseField(columnName = "name", canBeNull = false, dataType = DataType.STRING)
    var name: String? = null

    /**
     * max focal length for lens (unit : mm)
     */
    // for maxFocal
    @DatabaseField(columnName = "maxFocal", dataType = DataType.INTEGER, canBeNull = false)
    var maxFocal: Int = 0

    /**
     * min focal length for lens (unit : mm)
     */
    // for minFocal
    @DatabaseField(columnName = "minFocal", dataType = DataType.INTEGER, canBeNull = false)
    var minFocal: Int = 0

    init {
        id = -1
        name = ""

    }

    override fun hashCode(): Int {
        return (name!!.hashCode() + id!!
                + maxFocal + minFocal)
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
