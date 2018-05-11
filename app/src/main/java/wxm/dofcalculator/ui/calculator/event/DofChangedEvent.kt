package wxm.dofcalculator.ui.calculator.event

/**
 * for dof change
 * [frontDof] front dof, unit is mm
 * [objectDistance] object distance, unit is mm
 * [backDof] back dof, unit is mm
 * Created by WangXM on2017/3/20.
 */
data class DofChangedEvent(
        val frontDof: Float,
        val objectDistance: Float,
        val backDof: Float) {
    val frontDofInMeter
        get() = frontDof / 1000

    val objectDistanceInMeter
        get() = objectDistance / 1000

    val backDofInMeter
        get() = backDof / 1000

    val focalLengthInMeter: Float
        get() = (backDof - frontDof) / 1000
}
