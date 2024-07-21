const delete_elements = document.getElementsByClassName("delete");
Array.from(delete_elements).forEach(function(e) {
	e.addEventListener('click', function() {
		if (confirm("정말로 삭제하시겠습니까?")) {
			var token = $("meta[name='_csrf']").attr("content");
			var header = $("meta[name='_csrf_header']").attr("content");

			$.ajax({
				url: this.dataset.uri,
				type: 'GET',
				beforeSend: function(xhr) {
					xhr.setRequestHeader(header, token);
				},
				success: function(response) {
					alert(response);
					window.opener.location.reload(); // 부모 창 새로 고침
					window.close(); // 자식 창 닫기
				},
				error: function(xhr, status, error) {
					alert('삭제 실패: ' + xhr.responseText);
				}
			});
		}
	});
});
