
document.addEventListener('DOMContentLoaded', function() {
	$("#dialog").dialog({ autoOpen: false });
	var calendarEl = document.getElementById('calendar');
	var calendar = new FullCalendar.Calendar(calendarEl, {
		customButtons: {
			show_menual: {
				text: '일정연동 메뉴얼',
				click: function() {
					$("#dialog").dialog("open");
				},
			},
			get_schedule: {
				text: '일정불러오기',
				click: function() {
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
							// 기존 이벤트 제거
							calendar.removeAllEvents();
							
							var events = [];
							for (var i = 0; i < result.items.length; i++) {
								calendar.addEventSource({
									title: result.items[i].summary
									, backgroundColor: result.items[i].backgroundColor
									, googleCalendarId: result.items[i].id
								});
							}
						},
						error: function(error) {
							failureCallback(error);
						}
					});
				}
			}
		},
		headerToolbar: {
			left: 'prev,next today show_menual get_schedule',
			center: 'title',
			right: 'dayGridMonth,timeGridWeek,timeGridDay'
		},
		initialView: 'dayGridMonth',
		googleCalendarApiKey: 'AIzaSyCDpt53ZB1UMcNNCA83j_VUHTmZdykkaYw',
		eventClick: function(info) {
			// 클릭 시 구글로 이동X
			info.jsEvent.stopPropagation();
			info.jsEvent.preventDefault();
		}
	});
	calendar.render();
});
