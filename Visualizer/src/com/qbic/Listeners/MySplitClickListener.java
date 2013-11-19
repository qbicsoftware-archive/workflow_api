package com.qbic.Listeners;

import com.vaadin.ui.AbstractSplitPanel.SplitterClickEvent;
import com.vaadin.ui.AbstractSplitPanel.SplitterClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.UI;
import com.example.visualizer.VisualizerUI;

public class MySplitClickListener implements SplitterClickListener{

	@Override
	public void splitterClick(SplitterClickEvent event) {
		// TODO Auto-generated method stub
		Component component = event.getComponent();
		if(component instanceof HorizontalSplitPanel){
			HorizontalSplitPanel hsplit = (HorizontalSplitPanel) component;
			float current = hsplit.getSplitPosition() - hsplit.getMinSplitPosition();
			float max = hsplit.getMaxSplitPosition() - hsplit.getMinSplitPosition();
			float percent = 1f - Math.max(current/hsplit.getWidth(), 0);
			VisualizerUI ui  = ((VisualizerUI)UI.getCurrent());
			float newFrameW = Math.max(hsplit.getWidth()*percent - 1,1);
			System.out.println("MySplitClickListener: current,max,percent" + current + "," + max + "," + percent);
			ui.setBrowserWidth(newFrameW);
		}
	}

}
