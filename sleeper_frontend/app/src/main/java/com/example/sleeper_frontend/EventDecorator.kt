package com.example.sleeper_frontend

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class EventDecorator(context: Context, date: CalendarDay): DayViewDecorator {
    private var date = date
    val drawable: Drawable = ContextCompat.getDrawable(context, R.drawable.calendar_selector)!!

    override fun shouldDecorate(day: CalendarDay?): Boolean{
        return day?.equals(date)!!
    }

    override fun decorate(view: DayViewFacade?) {
//view?.addSpan(ForegroundColorSpan(Color.parseColor("#34A94B")))   //텍스트 색상이 색상코드와 다르게 보이는 문제
        if(drawable != null) {
//view?.setBackgroundDrawable(drawable)	// 이거 쓰면 기존 circle이 그대로 남아있다..
            view?.setSelectionDrawable(drawable)
//Log.d("event_decorator","ok")
        }
        else{
        }
    }
}