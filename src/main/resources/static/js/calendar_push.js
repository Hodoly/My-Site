
document.addEventListener('DOMContentLoaded', function() {
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$.ajax({
		url: '/calendar/get/google',
		type: 'GET',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		contentType: "application/json; charset=utf-8",
		success: function(result) {
			var select = $("[id='calendar']");
			for (var i = 0; i < result.items.length; i++) {
				if (result.items[i].accessRole === "owner" || result.items[i].accessRole === "writer") {
					var option = $("<option value='" + result.items[i].id + "'>" + result.items[i].summary + "</option>");
					select.append(option);
				}
			}
		},
		error: function(error) {
			failureCallback(error);
		}
	});
});
