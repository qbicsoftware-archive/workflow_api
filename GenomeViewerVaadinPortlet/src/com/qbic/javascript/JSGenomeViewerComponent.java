package com.qbic.javascript;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;

@SuppressWarnings("serial")
@StyleSheet({"CSSGV/bootstrap-theme.css","CSSGV/bootstrap.css","CSSGV/jquery.qtip.min.css" , "CSSGV/style.css","CSSGV/typeahead.js-bootstrap.css"})
@JavaScript({"vendor/underscore-min.js" ,"vendor/backbone-min.js", "vendor/jquery.min.js","vendor/jquery.cookie.js","vendor/jquery.qtip.min.js","vendor/bootstrap.min.js", "vendor/typeahead.min.js","genome-viewer-1.0.3.js", "gv-config.js","js_genomeViewerComponent.js" })
public class JSGenomeViewerComponent extends AbstractJavaScriptComponent {
	
    public JSGenomeViewerComponent(final String chr, final String start, final String end) {
       // addStyleName("bootstrap-theme");
       // addStyleName("bootstrap");
       // addStyleName("jquery.qtip.min");
       // addStyleName("style");
        addFunction("func", new JavaScriptFunction() {
            @Override
            public void call(JSONArray arguments) throws JSONException {
                for (ValueChangeListener listener: listeners) {
                	listener.valueChange();
                }
            }
        });
        getState().chr = chr;
        getState().start = start;
        getState().end = end;
        getState().region = chr+start+end;
    }

	public interface ValueChangeListener extends Serializable {
        void valueChange();
    }
    ArrayList<ValueChangeListener> listeners =
            new ArrayList<ValueChangeListener>();
    public void addValueChangeListener(
                   ValueChangeListener listener) {
        listeners.add(listener);
    }
    
    public void setValue(String c, String s, String e) {
        getState().chr = c;
        getState().start = s;
        getState().end = e;
        markAsDirty();
    }
    
    public String getChr() {
        return getState().chr;
    }

    public String getStart() {
    	return getState().start;
    }
    
    public String getEnd() {
    	return getState().end;
    }

	public String getRegion() {
		return getState().region;
	}
	
    @Override
    protected JSGenomeViewerComponentState getState() {
        return (JSGenomeViewerComponentState) super.getState();
    }
}
