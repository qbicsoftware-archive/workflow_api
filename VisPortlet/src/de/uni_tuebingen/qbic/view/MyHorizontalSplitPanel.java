package de.uni_tuebingen.qbic.view;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.splitpanel.AbstractSplitPanelRpc;
import com.vaadin.ui.HorizontalSplitPanel;

public class MyHorizontalSplitPanel extends HorizontalSplitPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private List<SplitPositionChangeListener> listeners;
	
	
	private AbstractSplitPanelRpc myRpc =  new AbstractSplitPanelRpc() { 
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@Override 
		public void setSplitterPosition(float position) { 
			// Do your magic here 
				_fireEvent(position);
			} 
		@Override 
		public void splitterClick(MouseEventDetails mouseDetails) { 
				// TODO Auto-generated method stub 
				} 
			}; 
	
	public MyHorizontalSplitPanel(){
		super();
		registerRpc(myRpc);
		listeners = new ArrayList<SplitPositionChangeListener>();
	}
	
	public synchronized  void addSplitPositionChangeListener(SplitPositionChangeListener listener){
		this.listeners.add(listener);
	}
	public synchronized void removeSplitPositionChangeListener(SplitPositionChangeListener listener){
		this.listeners.remove(listener);
	}
	
	private synchronized void  _fireEvent(float position){
		SplitPositionChangeEvent event = new SplitPositionChangeEvent(this,position);
		for(SplitPositionChangeListener listener: this.listeners){
			listener.positiondReceived(event);
		}
	}
	
	
	public class SplitPositionChangeEvent extends EventObject{

		public SplitPositionChangeEvent(Object source, float position) {
			super(source);
			this.position = position;

		}
		public float getPosition(){
			return this.position;
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private float position;
	}
	
	public interface SplitPositionChangeListener 
	{
	    public void positiondReceived( SplitPositionChangeEvent event );
	}
	
	
}