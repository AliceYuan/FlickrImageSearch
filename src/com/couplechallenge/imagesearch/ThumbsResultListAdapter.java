package com.couplechallenge.imagesearch;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ThumbsResultListAdapter extends ArrayAdapter<ResultObject> {
	private Context context; 
	private int layoutResourceId;    
	private ArrayList<ResultObject> data = null;
	private PhotoResultHolder holder = null;


	public ThumbsResultListAdapter(Context context, int layoutResourceId,	ArrayList<ResultObject> searchData) {
		super(context, layoutResourceId, searchData);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = searchData;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		if(row == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new PhotoResultHolder();
			holder.imgIcon = (ImageView)row.findViewById(R.id.img_thumbnail);
			holder.searchTextView = (TextView) row.findViewById(R.id.search_field);
			row.setTag(holder);
		} else {
			holder = (PhotoResultHolder)row.getTag();
			holder.imgIcon.setImageDrawable(null);
		}

		final ResultObject result = data.get(position);
		holder.searchTextView.setText(result.getTitle());

		if (result.getFilePathSmall() != null){
			File file = new File(result.getFilePathSmall());
			try {
				byte[] key = Encryption.generateKey(Encryption.SECURE_KEY);
				byte[] decodedData = Encryption.decodeFile(key, file);
				Bitmap bitmap =BitmapFactory.decodeByteArray(decodedData , 0, decodedData.length);
				holder.imgIcon.setImageBitmap(bitmap);
			} catch (Exception e) {
				holder.imgIcon.setImageDrawable(null);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else{
			MainActivity.imageLoader.displayImage(result.getUrlSmall75(),holder.imgIcon, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					String imagePath = Encryption.saveAndEncryptImage(loadedImage, "s75", result.getQuery(), result.getId());
					result.setFilePathSmall(imagePath);
				}
			});
		}

		return row;
	}

	//photo result holder of views
	private class PhotoResultHolder
	{
		ImageView imgIcon;
		TextView  searchTextView;
	}
}
