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


var initDoneOnce = false;
var initInprogress = false;

function initUpload(mt, fn) {
  $('#fileupload').addClass('fileupload-processing');
  var metadata = mt;
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
        initInprogress = false;
    }).done(function (result) {
    	initDoneOnce = true;
        $(this).fileupload('option', 'url', getLink(result,"self"));
        $(this).fileupload('option', 'done')
            .call(this, $.Event('done'), {result: result});
        if (fn) {
        	fn();
        }
    }).fail(function() {
    	$("#uploader-name")[0].disabled = false;
		alert("Failed communicating with the server");
    });
}

function uploadNow() {
	if (initInprogress) {
		return;
	}
	submitForm = function() {$('button.start')[0].click();};
	if (!initDoneOnce) {
		var nameInput = $("#uploader-name")[0];
		if (nameInput.value.length == 0) {
			alert("Please provide a name");
			return;
		}
		initInprogress = true;
		initUpload({uploaderName: nameInput.value}, submitForm);
		nameInput.disabled = true;
	} else {
		submitForm();
	}
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

   //initUpload();
  
});
