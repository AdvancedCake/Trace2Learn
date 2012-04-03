package edu.upenn.cis350.Trace2Learn;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;

    private ArrayList<Bitmap> images;

    public ImageAdapter(Context c, ArrayList<Bitmap> images) {
        mContext = c;
        TypedArray attr = mContext.obtainStyledAttributes(R.styleable.HelloGallery);
        mGalleryItemBackground = attr.getResourceId(
                R.styleable.HelloGallery_android_galleryItemBackground, 0);
        attr.recycle();
        this.images = images;
    }

    public int getCount() {
        return images.size();
    }

    public Object getItem(int position) {
        return images.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
    public void update(ArrayList<Bitmap> images){
    	this.images = images;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);

        imageView.setImageBitmap(images.get(position));
        imageView.setLayoutParams(new Gallery.LayoutParams(50, 50));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setBackgroundResource(mGalleryItemBackground);

        return imageView;
    }
}
