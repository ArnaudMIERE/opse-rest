package fr.sedoo.openopse.rest.domain;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class OSEntry implements Comparable<OSEntry> {

	private String date;
	private Date realDate;
	private String parentIdentifier;
	private String type;
	private Long totalSize;
	private Integer fileNumber;
	private String name;
	private String url;
	private byte[] image;
	private double[] data;
	private LocalDateTime[] key;
	

	public double[] getData() {
		return data;
	}

	public void setData(double[] data) {
		this.data = data;
	}

	public LocalDateTime[] getKey() {
		return key;
	}

	public void setKey(LocalDateTime[] key) {
		this.key = key;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public void setDate(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		this.setRealDate(date);
		this.date = df.format(date);
	}

	public String getParentIdentifier() {
		return parentIdentifier;
	}

	public void setParentIdentifier(String parentIdentifier) {
		this.parentIdentifier = parentIdentifier;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getTotalSize() {
		if (totalSize == null) {
			return 0L;
		} else {
			return totalSize;
		}
	}

	public void setTotalSize(Long totalSize) {
		this.totalSize = totalSize;
	}

	public Integer getFileNumber() {
		if (fileNumber == null) {
			return 0;
		} else {
			return fileNumber;
		}
	}

	public void setFileNumber(Integer fileNumber) {
		this.fileNumber = fileNumber;
	}

	public void addOneFile(long size) {
		setFileNumber(getFileNumber() + 1);
		setTotalSize(getTotalSize() + size);
	}

	public Date getRealDate() {
		return realDate;
	}

	public void setRealDate(Date realDate) {
		this.realDate = realDate;
	}

	@Override
	public int compareTo(OSEntry o) {
		return realDate.compareTo(o.realDate);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

}
