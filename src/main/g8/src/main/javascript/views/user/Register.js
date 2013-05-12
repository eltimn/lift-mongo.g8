App.namespace("views.user");
App.views.user.Register = (function($) {
	"use strict";

	var inst = {};
	
	// Global object for localizable strings per
	// https://groups.google.com/forum/?fromgroups=#!topic/liftweb/DwUOKgmiV-0
	inst.strings = {
		PasswordsMustMatch : "Passwords must match"
	};

	inst.init = function() {
		var confirm = $('.confirm-field');
		var password = $('.password-field');

		confirm.change(function() {
			inst.checkConfirmError();
		});
		password.change(function() {
			inst.checkConfirmError();
		});
		password.bind('input change keyup', function() {
			inst.checkConfirmClear();
		});
		confirm.bind('input change keyup', function() {
			inst.checkConfirmClear();
		});
	};

	inst.checkConfirmError = function () {
		var confirm = $('.confirm-field');
		var password = $('.password-field');

		if (confirm.val() !== password.val() && confirm.val().length > 0
				&& password.val().length > 0) {
			if (confirm.next().children().length == 0) {
				confirm.next().append(
						'<ul><li class="error">'
								+ inst.strings.PasswordsMustMatch
								+ '</li></ul>');
				confirm.parent().parent().addClass("error")
			}
		}
	};

	inst.checkConfirmClear = function () {
		var confirm = $('.confirm-field');
		var password = $('.password-field');

		if (confirm.val() === password.val()) {
			confirm.next().children().remove()
			confirm.parent().parent().removeClass("error")
		}
	};
	
	return inst;
}(jQuery));
