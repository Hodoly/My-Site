//회의실 예약 설정
const interval = 10;
const currentTime = new Date();
let startTime, endTime;

$('.datepicker').datepicker({
	dateFormat: 'yy-mm-dd',
	showOn: "button",
	buttonImage: "https://cdn-icons-png.flaticon.com/512/2838/2838779.png",
	buttonImageOnly: true,
	defaultViewDate: { year: new Date().getFullYear(), month: new Date().getMonth(), day: new Date().getDate() },
}).datepicker('setDate', new Date());

function getNearestTime(currentTime, interval) {
	let nearestTime = new Date(currentTime);
	nearestTime.setSeconds(0, 0);
	const minutes = currentTime.getMinutes();
	if (minutes % interval !== 0) {
		nearestTime.setMinutes(Math.ceil(minutes / interval) * interval);
	}
	return nearestTime;
}


if (currentTime.getHours() < 9) {
	startTime = new Date(currentTime.setHours(9, 0, 0, 0));
	endTime = new Date(currentTime.setHours(10, 0, 0, 0));
} else if (currentTime.getHours() >= 17 && currentTime.getHours() < 18) {
	startTime = getNearestTime(currentTime, interval);
	endTime = new Date(startTime);
	endTime.setHours(18, 0, 0, 0);
} else if (currentTime.getHours() >= 18) {
	startTime = new Date(currentTime.setHours(9, 0, 0, 0));
	endTime = new Date(currentTime.setHours(10, 0, 0, 0));
} else {
	startTime = getNearestTime(currentTime, interval);
	endTime = new Date(startTime.getTime() + 60 * 60 * 1000);
	if (endTime.getHours() > 18) {
		endTime.setHours(18, 0, 0, 0);
	}
}

$('.timepicker').timepicker({
	timeFormat: 'HH:mm',
	showOn: "button",
	buttonImage: "https://cdn-icons-png.flaticon.com/512/2838/2838779.png",
	buttonImageOnly: true,
	interval: interval,
	minTime: '09:00',
	maxTime: '18:00',
	dynamic: false,
	dropdown: true,
	scrollbar: true,
});

$("[name='startTime']").timepicker('setTime', startTime.toTimeString().slice(0, 5));
$("[name='endTime']").timepicker('setTime', endTime.toTimeString().slice(0, 5));

// 사용자가 종일 선택 시 시간 선택하지 못하도록 disabled 설정
$("[id='allday']").off().on('change', function() {
	if ($("[id='allday']").is(":checked")) {
		$("[name='startTime']").attr("disabled", true)
		$("[name='endTime']").attr("disabled", true)
	} else {
		$("[name='startTime']").attr("disabled", false)
		$("[name='endTime']").attr("disabled", false)
	}
});

// 종료날짜가 시작날짜보다 늦도록 설정..
$("[name='startDate']").off().on('change', function() {
	if ($("[name='startDate']").val() > $("[name='endDate']").val()) {
		$("[name='endDate']").val($("[name='startDate']").val());
	}
});

$("[name='endDate']").off().on('change', function() {
	if ($("[name='startDate']").val() > $("[name='endDate']").val()) {
		$("[name='startDate']").val($("[name='endDate']").val());
	}
});

// 예약현황 확인 버튼 로직
$("[id='check']").off().on('click', function() {
	const valueToPass = $("[id='resource']").val();
	const popupWidth = 1000;
	const popupHeight = 650;

	// 화면의 중앙 위치 계산
	const left = (window.screen.width / 2) - (popupWidth / 2);
	const top = (window.screen.height / 2) - (popupHeight / 2);
	if (window.name == "PopupWindow") {
		const popupChild = window.open('/reservation/check?value=' + valueToPass, 'popupChild', `width=${popupWidth},height=${popupHeight},top=${top},left=${left}`);
	} else {
		const popupWindow = window.open('/reservation/check?value=' + valueToPass, 'PopupWindow', `width=${popupWidth},height=${popupHeight},top=${top},left=${left}`);
	}
});
