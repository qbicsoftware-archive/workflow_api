package com.qbic.javascript;

import com.vaadin.annotations.JavaScript;

@SuppressWarnings("serial")
@JavaScript({ "js_opennnewtab.js" })
public class OpenNewTab extends com.vaadin.ui.AbstractJavaScriptComponent {

    public OpenNewTab(final String url) {
        getState().url = url;
    }

    @Override
    protected OpenNewTabState getState() {
        return (OpenNewTabState) super.getState();
    }
}
