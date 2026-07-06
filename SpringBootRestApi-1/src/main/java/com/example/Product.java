package com.example;

public class Product {
	private int pid;
	private String pName;
	private double pPrice;
	
	public Product(int pid, String pName, double pPrice) {
		super();
		this.pid = pid;
		this.pName = pName;
		this.pPrice = pPrice;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public double getpPrice() {
		return pPrice;
	}

	public void setpPrice(double pPrice) {
		this.pPrice = pPrice;
	}

	@Override
	public String toString() {
		return "Product [pid=" + pid + ", pName=" + pName + ", pPrice=" + pPrice + "]";
	}
	
	

}
