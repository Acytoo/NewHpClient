package com.acytoo.newhpcliend;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 目前设想只支持一天的日程或课表， 不支持交互
 * Alec　Chen 6.5.2018 16:11
 *
 * 是不是应该有一个service在后台， 提供日程信息？
 */
public class NewAppWidget extends AppWidgetProvider {

    //private static MyDBHandler dbHandler;
    //private static Calendar calendar;
    private static Calendar caForEnd;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        MyDBHandler dbHandler;
        Calendar calendar;
        dbHandler = new MyDBHandler(context, null, null, 2);
        calendar = Calendar.getInstance();
        caForEnd = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.setTimeZone(TimeZone.getDefault());    //get your TimeZone
        calendar.set(Calendar.MILLISECOND, 0);  //We need to set the millisecond to 0
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        CharSequence plans =  dbHandler.getSomePlans(calendar.getTimeInMillis(),getNextDayMillis(calendar.getTimeInMillis()));

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.widget_plans, plans);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    public static long getNextDayMillis(long givenMillis){

        caForEnd.setTimeInMillis(givenMillis);
        caForEnd.add(Calendar.DATE, 1);
        return caForEnd.getTimeInMillis();
    }
}

