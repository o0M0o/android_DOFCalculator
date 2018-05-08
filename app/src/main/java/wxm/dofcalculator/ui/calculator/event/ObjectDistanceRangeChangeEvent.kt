package wxm.dofcalculator.ui.calculator.event

/**
 * for object distance
 * [objectDistanceMin] min distance, unit is m
 * [objectDistanceMax] max distance, unit is m
 * Created by WangXM on2017/3/22.
 */
class ObjectDistanceRangeChangeEvent(
        val objectDistanceMin: Int,
        val objectDistanceMax: Int)
