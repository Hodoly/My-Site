$(document).ready(function() {
	$("[role='modal']").off().on('click', function() {
		event.preventDefault(); // a 태그의 기본 동작을 막습니다.
		var modalUrl = $(this).data("modal");
		$("body").addClass("no-scroll"); // 모달을 열 때 스크롤 비활성화
		$(".overlay").fadeIn(400); // 오버레이 페이드 인
		$("#modal-container").load(modalUrl, function() {
			$("#modal-container > div").dialog({
				modal: true,
				width: 500, // 너비 설정 (픽셀 단위)
				height: 400, // 높이 설정 (픽셀 단위)
				show: { effect: "fade", duration: 400 }, // 페이드 인 효과
				hide: { effect: "fade", duration: 400 }, // 페이드 아웃 효과
				close: function() {
					$("body").removeClass("no-scroll"); // 모달을 닫을 때 스크롤 활성화
					$(".overlay").fadeOut(400); // 오버레이 페이드 아웃
					$("#modal-container").empty(); // 모달 내용 제거
				},
				open: function() {
					// 제목 바 숨기기
					$(this).parent().find('.ui-dialog-titlebar').hide();
				},
				buttons: {
					"Close": function() {
						$(this).dialog("close");
					}
				}
			});
		});
	});
});