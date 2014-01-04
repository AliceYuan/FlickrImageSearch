package com.couplechallenge.imagesearch;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.couplechallenge.imagesearch.Constants.Extra;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class MainActivity extends SherlockActivity {


	private ListView searchDisplay;
	private ThumbsResultListAdapter resultAdapter;
	private static ArrayList<ResultObject> searchData = new ArrayList<ResultObject>();
	public static ImageLoader imageLoader = ImageLoader.getInstance();
	public static ImageLoaderConfiguration config;
	private EditText searchText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		searchDisplay = (ListView) findViewById(R.id.image_search);
		searchText = (EditText)findViewById(R.id.search_edit);

		// Define a new Adapter
		resultAdapter = new ThumbsResultListAdapter(this, R.layout.listview_item_thumb_result, searchData);
		searchDisplay.setAdapter(resultAdapter);
		config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
		ImageLoader.getInstance().init(config);

		searchDisplay.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent newActivity = new Intent(MainActivity.this, ImagePagerActivity.class);  
				newActivity.putExtra(Extra.IMAGE_POSITION, position);
				startActivity(newActivity);
			}
		});
		searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					searchText.setText("");
			}
		});

	}

	public void searchFlickr(View view){
		//get user entered search term
		EditText searchText = (EditText)findViewById(R.id.search_edit);
		String searchTerm = searchText.getText().toString();
		if(searchTerm.length()>0){
			try{
				//encode in case user has included symbols such as spaces etc
				String encodedSearch = URLEncoder.encode(searchTerm, "UTF-8");
				//append encoded user search term to search URL
				//instantiate and execute AsyncTask
				InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				in.hideSoftInputFromWindow(searchText.getWindowToken(), 0);

				//clear adapter first so that new content is added on new listview
				resultAdapter.clear();
				new GetImageResult(1, encodedSearch).execute();
			}
			catch(Exception e){ 
				e.printStackTrace(); 
			}
		}
	}

	private class GetImageResult extends AsyncTask<Void, Void, String> {
		// Create a new HTTP Client
		// example search

		int pageNum;
		String searchQuery;

		public GetImageResult(int page, String query) {
			this.pageNum = page;
			this.searchQuery = query;
		}

		protected void onPreExecute()  
		{
			setProgressBarIndeterminateVisibility(Boolean.TRUE); 

		}  


		@Override
		protected String doInBackground(Void... params) {
			StringBuilder searchResultBuilder = new StringBuilder();
			String searchURL = Constants.FLICKR_SEARCH_URL+Constants.API_KEY+Constants.SORT+Constants.PER_PAGE+Constants.EXTRAS+Constants.JSON_FORMAT+Constants.TEXT+searchQuery+Constants.PAGE+pageNum;
			HttpClient defaultClient = new DefaultHttpClient();
			// Setup the get request
			try {
				//pass search URL string to fetch
				HttpGet httpGetRequest = new HttpGet(searchURL);
				HttpResponse httpResponse = defaultClient.execute(httpGetRequest);
				StatusLine searchStatus = httpResponse.getStatusLine();
				// Escape early if cancel() is called
				if (searchStatus.getStatusCode() == 200) {
					//get the response
					HttpEntity httpEntity = httpResponse.getEntity();
					InputStream inputContent = httpEntity.getContent();
					//process the results
					InputStreamReader inputStream = new InputStreamReader(inputContent);
					BufferedReader resultReader = new BufferedReader(inputStream);
					String lineIn;
					while ((lineIn = resultReader.readLine()) != null) {
						searchResultBuilder.append(lineIn);
						if (isCancelled()) break;
					}
				}
			}
			catch(Exception e){
				e.printStackTrace(); 

			}
			//return result string
			return searchResultBuilder.toString();
		}


		protected void onPostExecute(String jsonResult) {

			try {
				JSONObject resultObject = new JSONObject(jsonResult);
				JSONObject photosObject = resultObject.getJSONObject("photos");

				JSONArray  photoArray = photosObject.getJSONArray("photo");

				//loop through each item in the flickr result array 
				for (int t=0; t<photoArray.length(); t++) {
					//each item is a JSONObject
					if (isCancelled()) break;
					JSONObject photoObject = photoArray.getJSONObject(t);
					ResultObject result = createResultObject(photoObject, searchQuery);
					searchData.add(result);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			//check result exists
			resultAdapter.notifyDataSetChanged();
			//query searchData to download all images to sd_card
			for (int i=0; i<searchData.size(); i++){
				final ResultObject result = searchData.get(i);
				imageLoader.loadImage(result.getUrlSmall75(), new ImageLoadingListener() {

					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						String imagePath = Encryption.saveAndEncryptImage(loadedImage, "s75", result.getQuery(), result.getId());
						result.setFilePathSmall(imagePath);
					}

					@Override
					public void onLoadingStarted(String imageUri, View view) {

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {

					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {

					}

				});
			}
			setProgressBarIndeterminateVisibility(Boolean.FALSE); 


		}

	}

	private ResultObject createResultObject(JSONObject photoObject, String searchQuery) throws JSONException{
		String id = photoObject.getString("id");
		String secret = photoObject.getString("secret");
		String title = null;
		String ownerName = null;
		String dateupload = null;
		String urlSmall75 = null; //75x75 square
		String urlLarge150 = null; //150x150 square
		String urlMedium640 = null; //640 on longest side


		if (photoObject.has("title"))
			title = photoObject.getString("title");
		if (photoObject.has("ownername"))
			ownerName = photoObject.getString("ownername");
		if  (photoObject.has("dateupload"))
			dateupload = photoObject.getString("dateupload");
		if (photoObject.has("url_sq"))
			urlSmall75=	photoObject.getString("url_sq");
		if (photoObject.has("url_s"))
			urlLarge150= photoObject.getString("url_s");
		if (photoObject.has("url_z"))
			urlMedium640=photoObject.getString("url_z");
		ResultObject photoResult = new ResultObject(searchQuery, id, secret,title, ownerName,
				dateupload, urlSmall75,urlLarge150, urlMedium640);
		return photoResult;
	}

	public static ArrayList<ResultObject> getSearchData() {
		return searchData;
	}


}
