package ysiparticipo.itnovate.com.ysiparticipo.Adapters;

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
public class EventAdapter extends ArrayAdapter<Event> {

    private LayoutInflater mInflater;
    private List<Event> contracts;
    private int mResource;
    public EventAdapter(Context context,int resource,List<Event>candidatos){
        super(context,resource,candidatos);
        mResource=resource;
        mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        View view=convertView==null?mInflater.inflate(mResource,parent,false):convertView;
        Event event=getItem(position);
        TextView text = (TextView) view.findViewById(R.id.title);
        TextView fecha = (TextView) view.findViewById(R.id.fecha);
        TextView lugar = (TextView) view.findViewById(R.id.lugar);
        text.setText(event.getEVENTO());
        fecha.setText(event.getFECHA());
        lugar.setText(event.getLUGAR());
        return view;
        }
    }
