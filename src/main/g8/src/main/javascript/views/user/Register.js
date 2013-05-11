App.namespace("views.user");
App.views.user.Register = (function($) {
	"use strict";

	// Global object for localizable strings per
	// https://groups.google.com/forum/?fromgroups=#!topic/liftweb/DwUOKgmiV-0
	window.strings = {
		PasswordsMustMatch : "Passwords must match"
	};

	$(document).ready(function() {
		var confirm = $('.confirm-field');
		var password = $('.password-field');

		confirm.change(function() {
			checkConfirmError();
		});
		password.change(function() {
			checkConfirmError();
		});
		password.bind('input change keyup', function() {
			checkConfirmClear();
		});
		confirm.bind('input change keyup', function() {
			checkConfirmClear();
		});
	});

	function checkConfirmError() {
		var confirm = $('.confirm-field');
		var password = $('.password-field');

		if (confirm.val() !== password.val() && confirm.val().length > 0
				&& password.val().length > 0) {
			if (confirm.next().children().length == 0) {
				confirm.next().append(
						'<ul><li class="error">'
								+ window.strings.PasswordsMustMatch
								+ '</li></ul>');
				confirm.parent().parent().addClass("error")
			}
		}
	};

	function checkConfirmClear() {
		var confirm = $('.confirm-field');
		var password = $('.password-field');

		if (confirm.val() === password.val()) {
			confirm.next().children().remove()
			confirm.parent().parent().removeClass("error")
		}
	};
	
	return inst;
}(jQuery));
