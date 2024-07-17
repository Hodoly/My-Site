document.addEventListener('DOMContentLoaded', function() {
	debugger;
	const urlParams = new URLSearchParams(window.location.search);
	const receivedValue = urlParams.get('value');
	var calendarEl = document.getElementById('calendar');
	var calendar = new FullCalendar.Calendar(calendarEl, {
		headerToolbar: {
			left: 'prev,next today',
			center: 'title',
			right: 'dayGridMonth,timeGridWeek,timeGridDay'
		},
		initialView: 'dayGridMonth',
		events: function(fetchInfo, successCallback, failureCallback) {
			var csrfToken = $("meta[name='_csrf']").attr("content");
			var csrfHeader = $("meta[name='_csrf_header']").attr("content");

			$.ajax({
				url: '/reservation/get/reservations?id=' + receivedValue,
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
				eventClick: function(info) {
			// 클릭된 이벤트 정보
			var eventObj = info.event;

			
			const valueToPass = $("[id='resource']").val();
			const popupWidth = 850;
			const popupHeight = 500;

			// 화면의 중앙 위치 계산
			const left = (window.screen.width / 2) - (popupWidth / 2);
			const top = (window.screen.height / 2) - (popupHeight / 2);
			const childWindow = window.open('/reservation/detail/room/' + eventObj.id, 'PopupWindow', `width=${popupWidth},height=${popupHeight},top=${top},left=${left}`);
			// 외부 링크로 이동하는 경우 기본 동작을 막기 위해 stopPropagation() 사용 가능
			info.jsEvent.preventDefault();
		}
	});
	calendar.render();
});
