window.ATL_JQ_PAGE_PROPS = {
  "triggerFunction": function (showCollectorDialog) {
    jQuery("#jiraBugReporter").click(function (e) {
      e.preventDefault();
      showCollectorDialog();
    });
  }
};
