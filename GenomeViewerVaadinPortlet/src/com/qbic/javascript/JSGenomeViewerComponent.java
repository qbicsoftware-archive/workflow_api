package com.qbic.javascript;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;

@SuppressWarnings("serial")
@StyleSheet({"CSSGV/bootstrap-theme.css","CSSGV/bootstrap.css","CSSGV/jquery.qtip.min.css" , "CSSGV/style.css","CSSGV/typeahead.js-bootstrap.css"})
@JavaScript({"vendor/underscore-min.js" ,"vendor/backbone-min.js", "vendor/jquery.min.js","vendor/jquery.cookie.js","vendor/jquery.qtip.min.js","vendor/bootstrap.min.js", "vendor/typeahead.min.js","genome-viewer-1.0.3.js", "gv-config.js","js_genomeViewerComponent.js" })
public class JSGenomeViewerComponent extends com.vaadin.ui.AbstractJavaScriptComponent {

    public JSGenomeViewerComponent(final String url) {
        getState().url = url;
       // addStyleName("bootstrap-theme");
       // addStyleName("bootstrap");
       // addStyleName("jquery.qtip.min");
       // addStyleName("style");
    }

    @Override
    protected JSGenomeViewerComponentState getState() {
        return (JSGenomeViewerComponentState) super.getState();
    }
}
