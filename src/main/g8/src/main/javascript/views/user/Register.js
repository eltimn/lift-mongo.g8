App.namespace("views.user");

$(document).ready(function() {
    $('.confirm-field').bind('input', function() { 
        confirmPassword();
    });
//    $('.confirm-field').keyup(confirmPassoword());
});

function confirmPassword() {
	var confirm = $('.confirm-field');
	var password = $('.password-field');
	
	if(confirm.val() !== password.val()) {
		if(confirm.next().children().length == 0) {
			confirm.next().append('<ul><li class="error">Passwords must match</li></ul>');
			confirm.parent().parent().addClass("error")
		}
	}
	else {
		confirm.next().children().remove()
		confirm.parent().parent().removeClass("error")
	}
};