package com.qbic.openbismodel;

public class OpenBisExperiment {
	private String Id;
	private String Description;
	private int estimated_volume;
	private String current_volume;
	private boolean exp_designed;
	private boolean offer_accepted;
	private boolean data_generated;
	private boolean data_processed;
	private boolean data_analysed;
	private boolean invoice_sent;
	
	
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public int getEstimated_volume() {
		return estimated_volume;
	}
	public void setEstimated_volume(int estimated_volume) {
		this.estimated_volume = estimated_volume;
	}
	public String getCurrent_volume() {
		return current_volume;
	}
	public void setCurrent_volume(String current_volume) {
		this.current_volume = current_volume;
	}
	public boolean isExp_designed() {
		return exp_designed;
	}
	public void setExp_designed(boolean exp_designed) {
		this.exp_designed = exp_designed;
	}
	public boolean isOffer_accepted() {
		return offer_accepted;
	}
	public void setOffer_accepted(boolean offer_accepted) {
		this.offer_accepted = offer_accepted;
	}
	public boolean isData_generated() {
		return data_generated;
	}
	public void setData_generated(boolean data_generated) {
		this.data_generated = data_generated;
	}
	public boolean isData_processed() {
		return data_processed;
	}
	public void setData_processed(boolean data_processed) {
		this.data_processed = data_processed;
	}
	public boolean isData_analysed() {
		return data_analysed;
	}
	public void setData_analysed(boolean data_analysed) {
		this.data_analysed = data_analysed;
	}
	public boolean isInvoice_sent() {
		return invoice_sent;
	}
	public void setInvoice_sent(boolean invoice_sent) {
		this.invoice_sent = invoice_sent;
	}
}
