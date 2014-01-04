package com.couplechallenge.imagesearch;

public class ResultObject {
	private String query;
	private String id;
	private String secret;
	private String title;
	private String ownerName;
	private String dateupload;
	private String urlSmall75; //75x75 square
	private String urlSmall150; //150x150 square
	private String urlMedium640; //640 on longest side
	private String filePathMedium = null; //640 on longest side
	private String filePathSmall = null; //640 on longest side

	
	
	ResultObject(String query, String id, String secret, String title, String ownername, String dateupload, String url_sq, String url_s, String url_z){
		this.query = query;
		this.id = id;
		this.secret = secret;
		this.title = title;
		this.ownerName = ownername;
		this.dateupload = dateupload;
		this.urlSmall75 = url_sq;
		this.urlSmall150 = url_s;
		this.urlMedium640 = url_z;
		
	}
	
	@Override
	public String toString() {
		return "ResultObject [id=" + id + ", secret="
				+ secret + ", title=" + title + ", ownerName=" + ownerName
				+ ", datetaken=" + dateupload + ", urlSmall75=" + urlSmall75
				+ ", urlLarge150=" + urlSmall150 + ", urlMedium640="
				+ urlMedium640 + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getDatetaken() {
		return dateupload;
	}

	public void setDatetaken(String datetaken) {
		this.dateupload = datetaken;
	}

	public String getUrlSmall75() {
		return urlSmall75;
	}

	public void setUrlSmall75(String urlSmall75) {
		this.urlSmall75 = urlSmall75;
	}

	public String getUrlSmall150() {
		return urlSmall150;
	}

	public void setUrlSmall150(String urlLarge150) {
		this.urlSmall150 = urlLarge150;
	}

	public String getUrlMedium640() {
		return urlMedium640;
	}

	public void setUrlMedium640(String urlMedium640) {
		this.urlMedium640 = urlMedium640;
	}

	public String getFilePathMedium() {
		return filePathMedium;
	}

	public void setFilePathMedium(String filePathMedium) {
		this.filePathMedium = filePathMedium;
	}

	public String getFilePathSmall() {
		return filePathSmall;
	}

	public void setFilePathSmall(String filePathSmall) {
		this.filePathSmall = filePathSmall;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}
