package de.uni_tuebingen.qbic.view;

import de.uni_tuebingen.qbic.visportlet.SplitPositionChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class MainViewPanel extends Panel{
	
	private MyHorizontalSplitPanel hsplitpanel;
	private Table table;
	private TabSheet tabsheet;
	private VerticalLayout firstComponent = new VerticalLayout();
	private CheckBox checkbox1 = new CheckBox("Open results in new Window or Tab. Browser settings might need adjustments. E.g. allow javascript at least temporarily.");
	private Button downloadButton = new Button("Download result");
	
	public MainViewPanel(){
		hsplitpanel = new MyHorizontalSplitPanel();
		
		hsplitpanel.setFirstComponent(firstComponent);
		hsplitpanel.setSecondComponent(tabsheet);
		checkbox1.setImmediate(true);
		firstComponent.addComponent(this.table);
		firstComponent.addComponent(checkbox1);
		firstComponent.addComponent(this.downloadButton);
		this.downloadButton.setEnabled(false);
		this.hsplitpanel.addSplitPositionChangeListener(new SplitPositionChangeListener());
		
		
		this.setContent(hsplitpanel);
	}
	
	
	public void setSize(float w, float h){
		
	}
	
	public void setWidth(float w){
		
	}
	
	public void setHeight(float h){
		
	}
}
