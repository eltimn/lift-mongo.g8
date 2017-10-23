(function(exports, $) {
  "use strict";

  var timeoutRtn = 0;

  function startMonitor() {
    timeoutRtn = setTimeout(monitorPassword, 1000);
  }

  /**
    * Monitor the password input field and select the yes_password
    * radio if something has been entered.
    */
  function monitorPassword() {
    if (!$("#yes_password").attr("checked")) {
      var pwd = $("#id_password").val();
      if (pwd.length > 0) {
        $("#yes_password").attr("checked", "checked");
      }
      else {
        startMonitor();
      }
    }
  }

  startMonitor();

})(this, jQuery);
