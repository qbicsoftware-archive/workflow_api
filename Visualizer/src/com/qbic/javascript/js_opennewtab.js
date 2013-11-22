org_vaadin_blog_JsLabel = function() {
	var e = this.getElement();
	
	this.onStateChange = function() {
		var win=window.open(this.getState().url, '_blank');
		  win.focus();
	}
}