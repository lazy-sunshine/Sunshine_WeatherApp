package com.example.dell.sunshine;

/**
 * Created by DeLL on 12/28/2015.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.sunshine.data.WeatherContract;
import com.squareup.picasso.Picasso;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;
    boolean mUseTodayLayout=true;
    Cursor mCursor;
    public ForecastAdapterOnClickHandler handler;
    TextView EmptyView;
 Context mContext;
    public ForecastAdapter(Context context, View empty_text,ForecastAdapterOnClickHandler h) {
        mContext=context;
      EmptyView= (TextView) empty_text;
                   handler=h;

    }


    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }


    @Override
    public ForecastAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int layoutId = -1;
        if ( viewGroup instanceof RecyclerView ) {
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.list_item_forecast;
                break;
            }
        }

            // useeee me View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);

            View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
            return viewHolder;
    } else {
        throw new RuntimeException("Not bound to RecyclerViewSelection");
    }


    }

    @Override
    public void onBindViewHolder(ForecastAdapter.ViewHolder holder, int position) {
        int img = 0;
        // Read weather icon ID from cursor

        mCursor.moveToPosition(position);
        int viewType = getItemViewType(position);

        int weather = mCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID);
        int weatherId=       mCursor.getInt(weather);

        Log.v("weather id " ,weatherId+ "  " +weather);
        // Use placeholder image for now
        if(viewType== VIEW_TYPE_TODAY) {
            img = Utility.getArtResourceForWeatherCondition(weatherId);
        }
        else{
            img=Utility.getIconResourceForWeatherCondition(weatherId);
        }


        Picasso.with(mContext).load(Utility.getArtUrl( mContext,weatherId))
                .error(img).into(holder.iconView);



        holder.iconView.setContentDescription("Image of weather");

        Long date=mCursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        String t=Utility.getFriendlyDayString(mContext, date);


        holder.dateView.setText(t);
        String desc=mCursor.getString(ForecastFragment.COL_WEATHER_DESC);

        holder.descriptionView.setText(desc);

        // TODO Read date from cursor

        // TODO Read weather forecast from cursor

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(mContext);

        // Read high temperature from cursor
        double high = mCursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);

        holder.highTempView.setText(Utility.formatTemperature(mContext,high));

        // TODO Read low temperature from cursor
        double low=mCursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        holder.lowTempView.setText(Utility.formatTemperature(mContext,low));
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout)? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

   public interface ForecastAdapterOnClickHandler{
       public void OnItemClick(long date,ViewHolder pos);


   }


    public Cursor getCursor() {
        return mCursor;
    }


    public void swapCursor(Cursor newCursor) {
        mCursor=newCursor ;
     if(getItemCount()==0) EmptyView.setVisibility(View.VISIBLE);
        else EmptyView.setVisibility(View.INVISIBLE);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public  final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            super(view);
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos=getAdapterPosition();
            mCursor.moveToPosition(pos);
           ViewCompat.setTransitionName(iconView, "iconView" + pos);
            int date=    mCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);

     handler.OnItemClick(mCursor.getLong(date),this);
        }
    }

}


