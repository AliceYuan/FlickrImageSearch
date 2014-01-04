package com.couplechallenge.imagesearch;

public class Constants {
	
	public static String IMAGE_NOT_FOUND = "Image Not Found";
	static final String FLICKR_SEARCH_URL = "http://api.flickr.com/services/rest/?method=flickr.photos.search";
	static final String API_KEY = "&api_key=27a524af0fe9ef1cdef807bdecd0fd81";
	static final String TEXT = "&text=";
	static final String SORT = "&sort=relevance";
	static final String PER_PAGE = "&per_page=10";
	static final String PAGE = "&page=";
	static final String JSON_FORMAT = "&format=json&nojsoncallback=1";
	static final String EXTRAS = "&extras=+date_upload%2C+owner_name%2C+url_sq%2C+url_s%2C+url_z";
	public static class Extra {
		public static final String IMAGE_POSITION = "com.couplechallenge.imagesearch.IMAGE_POSITION";
	}
}
