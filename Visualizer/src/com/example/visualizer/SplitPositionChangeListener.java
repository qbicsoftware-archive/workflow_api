package com.example.visualizer;

import com.example.visualizer.MyHorizontalSplitPanel.SplitPositionChangeEvent;
import com.vaadin.ui.UI;

public class SplitPositionChangeListener implements MyHorizontalSplitPanel.SplitPositionChangeListener {

	@Override
	public void positiondReceived(SplitPositionChangeEvent event) {
		// TODO Auto-generated method stub
		float current = event.getPosition();
		MyHorizontalSplitPanel hsplit = (MyHorizontalSplitPanel) event.getSource();
		float max = hsplit.getMaxSplitPosition() - hsplit.getMinSplitPosition();
		float tablePercent = Math.max(current/hsplit.getWidth(), 0);
		float browserFramePercent = 1 - tablePercent;
		VisualizerUI ui  = ((VisualizerUI)UI.getCurrent());
		float newBrowserFrameW = Math.max(hsplit.getWidth()*browserFramePercent - 1,1);
		float newTableW = Math.max(hsplit.getWidth()*tablePercent - 1,1);
		ui.setBrowserWidth(newBrowserFrameW,newTableW);
		}

}
