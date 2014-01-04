package com.couplechallenge.imagesearch;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.couplechallenge.imagesearch.Constants.Extra;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ImagePagerActivity extends SherlockActivity {

	private static final String STATE_POSITION = "STATE_POSITION";

	DisplayImageOptions options;

	ViewPager pager;
	ImageLoader imageLoader = ImageLoader.getInstance();
	;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_pager);

		Bundle bundle = getIntent().getExtras();
		int pagerPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}
		options = new DisplayImageOptions.Builder()
		.resetViewBeforeLoading()
		.cacheOnDisc()
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(MainActivity.getSearchData()));
		pager.setCurrentItem(pagerPosition);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private LayoutInflater inflater;
		private ArrayList<ResultObject> searchData;
		private ResultObject searchResult;
		ImagePagerAdapter(ArrayList<ResultObject> searchData) {
			inflater = getLayoutInflater();
			this.searchData = searchData;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return searchData.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, final int position) {
			View imageLayout = inflater.inflate(R.layout.pager_item_image, view, false);
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			final TextView titleTextView = (TextView) imageLayout.findViewById(R.id.image_title);
			final TextView ownerTextView = (TextView) imageLayout.findViewById(R.id.image_owner);
			final TextView errorTextView = (TextView) imageLayout.findViewById(R.id.image_not_found);
			errorTextView.setVisibility(View.GONE);
			searchResult = (searchData.get(position));
			titleTextView.setText(searchResult.getTitle());
			ownerTextView.setText(searchResult.getOwnerName());

			if (searchResult.getUrlMedium640() != null){
				imageLoader.displayImage(searchResult.getUrlMedium640(), imageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						spinner.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						String message = null;
						switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
						}
						errorTextView.setVisibility(View.VISIBLE);
						errorTextView.setText(message);
						spinner.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						spinner.setVisibility(View.GONE);
					}
				});
			}else{
				errorTextView.setText(Constants.IMAGE_NOT_FOUND);
				errorTextView.setVisibility(View.VISIBLE);
			}

			((ViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

	}
}