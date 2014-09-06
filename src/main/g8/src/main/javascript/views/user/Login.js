App.namespace("views.user");
App.views.user.Login = (function($) {
  "use strict";

  var inst = {};

  inst.timeoutRtn = 0;

  inst.init = function() {
    inst.startMonitor();
  };

  inst.startMonitor = function() {
    inst.timeoutRtn = setTimeout(App.views.user.Login.monitorPassword, 1000);
  };

  /**
    * Monitor the password input field and select the yes_password
    * radio if something has been entered.
    */
  inst.monitorPassword = function() {
    if (!$("#yes_password").attr("checked")) {
      var pwd = $("#id_password").val();
      if (pwd.length > 0) {
        $("#yes_password").attr("checked", "checked");
      }
      else {
        inst.startMonitor();
      }
    }
  };

  return inst;
}(jQuery));
