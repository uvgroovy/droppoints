/*
 * jQuery File Upload Plugin JS Example 8.9.1
 * https://github.com/blueimp/jQuery-File-Upload
 *
 * Copyright 2010, Sebastian Tschan
 * https://blueimp.net
 *
 * Licensed under the MIT license:
 * http://www.opensource.org/licenses/MIT
 */

/* global $, window */

function getLink(obj, rel) {
	if('_links' in obj) {
		return obj["_links"][rel]["href"];
	}
	
	for (var i = 0; i < obj["links"].length; i++) {
	    var link = obj["links"][i];
	    if (link["rel"] == rel) {
	    	return link["href"];
	    }
	}
	return null;
}

function initUpload() {
  $('#fileupload').addClass('fileupload-processing');
  var metadata = {name : 'yuval'};
  $.ajax({
        // Uncomment the following to send cross-domain cookies:
        //xhrFields: {withCredentials: true},
        url: $('#uploadapientry')[0].href,
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(metadata),
        type: 'POST',
        context: $('#fileupload')[0]
    }).always(function () {
        $(this).removeClass('fileupload-processing');
    }).done(function (result) {

        $(this).fileupload('option', 'url', getLink(result,"self"));
        $(this).fileupload('option', 'done')
            .call(this, $.Event('done'), {result: result});
    });
}

$(function () {
    'use strict';

    // Initialize the jQuery File Upload widget:
    $('#fileupload').fileupload({
        // Uncomment the following to send cross-domain cookies:
        //xhrFields: {withCredentials: true},
    });

    // Enable iframe cross-domain access via redirect option:
    $('#fileupload').fileupload(
        'option',
        'redirect',
        window.location.href.replace(
            /\/[^\/]*$/,
            '/cors/result.html?%s'
        )
    );

   initUpload();
  
});
