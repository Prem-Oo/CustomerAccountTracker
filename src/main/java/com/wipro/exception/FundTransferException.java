package com.wipro.exception;

public class FundTransferException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public FundTransferException(String msg){
		super(msg);
	}
}
