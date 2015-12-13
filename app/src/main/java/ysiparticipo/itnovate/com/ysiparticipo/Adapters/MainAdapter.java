package ysiparticipo.itnovate.com.ysiparticipo.Adapters;

/**
 * Created by Angel Sirlopu C on 13/12/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ysiparticipo.itnovate.com.ysiparticipo.Event;
import ysiparticipo.itnovate.com.ysiparticipo.MainData;
import ysiparticipo.itnovate.com.ysiparticipo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ysiparticipo.itnovate.com.ysiparticipo.Event;
import ysiparticipo.itnovate.com.ysiparticipo.R;

/**
 * Created by Angel Sirlopu C on 12/12/2015.
 */
public class MainAdapter extends ArrayAdapter<MainData> {

    private LayoutInflater mInflater;
    private List<MainData> contracts;
    private int mResource;
    public MainAdapter(Context context,int resource,List<MainData>candidatos){
        super(context,resource,candidatos);
        mResource=resource;
        mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        View view=convertView==null?mInflater.inflate(mResource,parent,false):convertView;
        MainData event=getItem(position);
        TextView text = (TextView) view.findViewById(R.id.title);
        ImageView imageView = (ImageView) view.findViewById(R.id.resource);
        imageView.setImageResource(event.getResource());
        text.setText(event.getTitle());
        return view;
    }
}
