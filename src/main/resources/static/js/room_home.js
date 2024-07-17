document.addEventListener('DOMContentLoaded', function() {
	var calendarEl = document.getElementById('calendar');
	var calendar = new FullCalendar.Calendar(calendarEl, {
		customButtons: {
			reservation: {
				text: '회의실 예약',
				click: function() {
					location.href = '/reservation/create/room';
				},
			},
			resource: {
				text: '자원',
				click: function() {
					location.href = '/resource/list';
				},
			},
		},
		headerToolbar: {
			left: 'prev,next today reservation resource',
			center: 'title',
			right: 'dayGridMonth,timeGridWeek,timeGridDay'
		},
		initialView: 'dayGridMonth',
		events: function(fetchInfo, successCallback, failureCallback) {
			var token = $("meta[name='_csrf']").attr("content");
			var header = $("meta[name='_csrf_header']").attr("content");

			$.ajax({
				url: '/reservation/get/reservations?kind=room',
				type: 'GET',
				beforeSend: function(xhr) {
					xhr.setRequestHeader(header, token);
				},
				contentType: "application/json; charset=utf-8",
				success: function(response) {
					debugger;
					successCallback(response);
				},
				error: function(error) {
					failureCallback(error);
				}
			});
		},
		eventClick: function(info) {
			// 클릭된 이벤트 정보
			var eventObj = info.event;

			
			const valueToPass = $("[id='resource']").val();
			const popupWidth = 850;
			const popupHeight = 500;

			// 화면의 중앙 위치 계산
			const left = (window.screen.width / 2) - (popupWidth / 2);
			const top = (window.screen.height / 2) - (popupHeight / 2);
			const popupWindow = window.open('/reservation/detail/room/' + eventObj.id, 'PopupWindow', `width=${popupWidth},height=${popupHeight},top=${top},left=${left}`);
			// 외부 링크로 이동하는 경우 기본 동작을 막기 위해 stopPropagation() 사용 가능
			info.jsEvent.preventDefault();
		}
	});
	calendar.render();
});
