package de.coupies.framework.beans;

import java.io.Serializable;
import java.util.Date;

public class Receipt implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	private int id;
	private double sum;
	private Date date;
	private String imageUrl;
	
	public Receipt(int id, int sum, Date date, String imageUrl) {
		this.id=id;
		this.sum=sum;
		this.date=date;
		this.imageUrl=imageUrl;
	}
	
	public Receipt() {
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getSum() {
		return sum;
	}
	public void setSum(double sum) {
		this.sum = sum;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
