package com.qbic.util;

import java.util.ArrayList;
import java.util.List;

//import org.icefaces.application.PortableRenderer;

//import ch.systemsx.ethz.imsb.openbis.DataSource;

/**
 * 
 * @author hullarb
 *
 *DataLoader class wrap a long running data access process in background thread and 
 *notifies the gui through ice push when the data is available
 *
 * @param <T> the type the data loader service will provide in a List 
 */
public abstract class DataLoader<T> implements Runnable {

 //   private final PortableRenderer renderer;
    private final String notificationGroup;
    
    private boolean available;
    private boolean loading;
    private List<T> data;
    
 //   protected DataSource dataSource;

//    public DataLoader(DataSource dataSource,PortableRenderer renderer,String notificationGroup) {
//	this.renderer = renderer;
//	this.notificationGroup = notificationGroup;
//	this.data = new ArrayList<T>();
//	this.dataSource = dataSource;
 //   }
    public DataLoader(String notificationGroup){
    	this.notificationGroup = notificationGroup;
    }
    @Override
    public void run() {
	synchronized (this) {
	    available = false;
	    loading = true;
	}
	data = loadData();
	synchronized (this) {
	    available = true;
	    loading = false;
	}
//	renderer.render(notificationGroup);
    }

    /**
     * Retrieves the loaded data if it's available or an empty List
     * @return
     */
    public List<T> getData() {
	return data;
    }
    
    /**
     * True when the data is loaded
     * @return
     */
    public synchronized boolean isDataAvailable() {
	return available;
    }
    
    /**
     * True when the data loading has started
     * @return
     */
    public synchronized boolean isLoading() {
	return loading;
    }
    
    /**
     * overload this method to provide the data loading mechanism
     * @return
     */
    protected abstract List<T> loadData();
}
