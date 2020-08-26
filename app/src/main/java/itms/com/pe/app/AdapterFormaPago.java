package itms.com.pe.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by MEDIPRO on 12/06/2017.
 */

public class AdapterFormaPago extends ArrayAdapter<String> {

    private List<Integer> images;

    public AdapterFormaPago(Context context, List<String> items, List<Integer> images) {
        super(context, android.R.layout.select_dialog_item, items);
        this.images = images;
    }

    public AdapterFormaPago(Context context, String[] items, Integer[] images) {
        super(context, android.R.layout.select_dialog_item, items);
        this.images = Arrays.asList(images);
    }

    public AdapterFormaPago(Context context, int items, int images) {
        super(context, android.R.layout.select_dialog_item, (String[]) context.getResources().getTextArray(items));

        final TypedArray imgs = context.getResources().obtainTypedArray(images);
        this.images = new ArrayList<Integer>() {{ for (int i = 0; i < imgs.length(); i++) {add(imgs.getResourceId(i, -1));} }};

        // recycle the array
        imgs.recycle();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(15);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(images.get(position), 0, 0, 0);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(images.get(position), 0, 0, 0);
        }
        textView.setCompoundDrawablePadding(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics()));
        return view;
    }
    
}
