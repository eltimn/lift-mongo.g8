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
		inst.confirm = $('#confirm-field');
		inst.password = $('#password-field');
		
		inst.confirm.change(function() {
			inst.checkConfirmError();
		});
		inst.password.change(function() {
			inst.checkConfirmError();
		});
		inst.password.bind('input change keyup', function() {
			inst.checkConfirmClear();
		});
		inst.confirm.bind('input change keyup', function() {
			inst.checkConfirmClear();
		});
	};

	inst.checkConfirmError = function () {
		if (inst.confirm.val() !== inst.password.val() && inst.confirm.val().length > 0
				&& inst.password.val().length > 0) {
			if (inst.confirm.next().children().length == 0) {
				inst.confirm.next().append(
						'<ul><li class="error">'
								+ inst.strings.PasswordsMustMatch
								+ '</li></ul>');
				inst.confirm.parent().parent().addClass("error")
			}
		}
	};

	inst.checkConfirmClear = function () {
		if (inst.confirm.val() === inst.password.val()) {
			inst.confirm.next().children().remove()
			inst.confirm.parent().parent().removeClass("error")
		}
	};
	
	return inst;
}(jQuery));
