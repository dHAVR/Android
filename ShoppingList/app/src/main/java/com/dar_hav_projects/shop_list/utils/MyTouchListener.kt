package com.dar_hav_projects.shop_list.utils

import android.view.MotionEvent
import android.view.View

//необохідний для того щоб ми
// могли перетаскувати ColorPicker
 class MyTouchListener: View.OnTouchListener {
    var xDelta = 0.0f
    var yDelta = 0.0f

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        //ми спостерігаємо за діями з елементом
        when(p1?.action){
            //працює коли ми відпускаємо об'єкт
            MotionEvent.ACTION_DOWN ->{
                if (p0 != null) {
                    xDelta = p0.x - p1.rawX
                }
                if (p0 != null) {
                    yDelta = p0.y - p1.rawY
                }
            }
            //кожен раз працює коли ми рухаємо об'єкт
            MotionEvent.ACTION_MOVE ->{
                if (p0 != null) {
                    p0.x = xDelta + p1.rawX
                }
                if (p0 != null) {
                    p0.y = yDelta + p1.rawY
                }

            }
        }
        return true
    }

}