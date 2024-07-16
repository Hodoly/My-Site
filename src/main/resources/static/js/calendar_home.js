
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
					alert('일정불러오기 버튼 click');
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
		//		events: {
		//			//				googleCalendarId: 'eabb297417f5f757a18af1969644c30d4ddf3895e1a90e6323aef819f2bea506@group.calendar.google.com',
		//			googleCalendarId: 'f2bd4710f1a9961c54c8df15407d593f7ce9452f1c02e2237ce745f17002e746@group.calendar.google.com',
		//			className: 'gcal-event' // an option!
		//		},
		eventSources: [
			{
				googleCalendarId: 'f2bd4710f1a9961c54c8df15407d593f7ce9452f1c02e2237ce745f17002e746@group.calendar.google.com',
				// 클래스 지정 가능
				className: 'gcal-event',
				// 막대 색상 지정 가능
				backgroundColor: 'green',
			},
			{
				googleCalendarId: 'eabb297417f5f757a18af1969644c30d4ddf3895e1a90e6323aef819f2bea506@group.calendar.google.com',
				className: 'nice-event'
			},
			{
				googleCalendarId: 'choihoyeon30@gmail.com',
				className: 'nice-event'
			},
		],

		eventClick: function(info) {
			// 클릭 시 구글로 이동X
			info.jsEvent.stopPropagation();
			info.jsEvent.preventDefault();
		}
	});
	calendar.render();
});
