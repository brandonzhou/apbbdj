package com.mt.bbdj.baseconfig.view.wheel;
public class LocaWheelAdapter<T> implements WheelAdapter{
	
	
	/** The default items length */
	public static final int DEFAULT_LENGTH = -1;
	
	// items
	private T items[];
	// length
	private int length;
	// format
	private String format;
	
	
	/**
	 * Constructor
	 * @param items the items
	 * @param length the max items length
	 */
	public LocaWheelAdapter(T items[], int length) {
		this.items = items;
		this.length = length;
	}
	/**
	 * Contructor
	 * @param items the items
	 */
	public LocaWheelAdapter(T items[]) {
		this.items = items;
		this.length = length;
	}

	public String getItem(int index) {
		if (index >= 0 && index < items.length) {
			return items[index].toString();
		}
		return null;
	}

	public int getItemsCount() {
		return items.length;
	}

	public int getMaximumLength() {
		return length;
	}

}

