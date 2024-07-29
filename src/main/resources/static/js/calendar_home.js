
document.addEventListener('DOMContentLoaded', function() {
	$("#dialog").dialog({
		autoOpen: false,
		buttons: {
			"닫기": function() {
				$(this).dialog("close");
			}
		}
	});
	var provider = $("[id='provider']").val();
	var buttons = "";
	if (provider == "google") {
		buttons = "prev,next today show_menual get_schedule";
	} else {
		buttons = "prev,next today";
	}
	var calendarEl = document.getElementById('calendar');
	var calendar = new FullCalendar.Calendar(calendarEl, {
		customButtons: {
			show_menual: {
				text: '구글 연동 메뉴얼',
				click: function() {
					$("#dialog").dialog("open");
				},
			},
			get_schedule: {
				text: '구글일정 불러오기',
				click: function() {
					var token = $("meta[name='_csrf']").attr("content");
					var header = $("meta[name='_csrf_header']").attr("content");
					var prevEvents = calendar.getEvents();

					//구글 일정만 초기화
					prevEvents.forEach(function(e) {
						if (e.extendedProps.role != "mysite") {
							e.remove();
						}
					});
					$.ajax({
						url: '/calendar/get/google',
						type: 'GET',
						beforeSend: function(xhr) {
							xhr.setRequestHeader(header, token);
						},
						contentType: "application/json; charset=utf-8",
						success: function(result) {
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
			},
			create_schedule: {
				text: '일정 생성',
				click: function() {
					location.href = "/calendar/create";
				},
			},
		},
		headerToolbar: {
			left: buttons,
			center: 'title',
			right: 'create_schedule dayGridMonth,timeGridWeek,timeGridDay'
		},
		initialView: 'dayGridMonth',
		events: function(fetchInfo, successCallback, failureCallback) {
			var csrfToken = $("meta[name='_csrf']").attr("content");
			var csrfHeader = $("meta[name='_csrf_header']").attr("content");
			var id = $("[id='providerid']").val();
			$.ajax({
				url: '/calendar/get/schedule?id=' + id,
				type: 'GET',
				beforeSend: function(xhr) {
					xhr.setRequestHeader(csrfHeader, csrfToken);
				},
				success: function(response) {
					successCallback(response);
				},
				error: function(error) {
					failureCallback(error);
				}
			});
		},
		googleCalendarApiKey: 'AIzaSyCDpt53ZB1UMcNNCA83j_VUHTmZdykkaYw',
		eventClick: function(info) {
			var role = info.event.extendedProps.role;
			if (role === 'mysite') {
				// 클릭된 이벤트 정보
				var eventObj = info.event;

				const popupWidth = 850;
				const popupHeight = 500;

				// 화면의 중앙 위치 계산
				const left = (window.screen.width / 2) - (popupWidth / 2);
				const top = (window.screen.height / 2) - (popupHeight / 2);
				const childWindow = window.open('/calendar/detail/' + eventObj.id, 'childWindow', `width=${popupWidth},height=${popupHeight},top=${top},left=${left}`);
				// 외부 링크로 이동하는 경우 기본 동작을 막기 위해 stopPropagation() 사용 가능
				info.jsEvent.preventDefault();
			} else {
				// 클릭 시 구글로 이동X
				info.jsEvent.stopPropagation();
				info.jsEvent.preventDefault();
			}
		}
	});
	calendar.render();
});
