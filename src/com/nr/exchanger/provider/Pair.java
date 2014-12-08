package com.nr.exchanger.provider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Pair<F,S> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ByteArrayOutputStream bos;
	
	
	private F first;
	private S second;
	
	public Pair(F first, S second){
		this.first = first;
		this.second = second;
	}

	public F getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}
	
    private void writeObject(ObjectOutputStream out)
        throws IOException {
    	
    	
    	out.writeObject(first);
    	out.writeObject(second);
    }

    @SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {
    	
    	first = (F) in.readObject();
    	second = (S) in.readObject();
    }
	
	
}
