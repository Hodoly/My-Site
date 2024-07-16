// 이메일 전송
$("[name='send']").off().on("click", function() {
	var subject = "My Site 회원인증번호 입니다.";
	var body = "\n 인증번호 : ";
	var email = $("[id='email']").val();


	if (email == "") {
		alert('이메일을 입력해주세요.');
		return false;
	}

	if (!emailCheck(email)) {
		alert('유효하지 않은 이메일 주소입니다.');
		return false;
	}

	var params = {
		email: email
		, subject: subject
		, body: body
	}
	
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(function() {
		$(document).ajaxSend(function(e, xhr, options) {
			xhr.setRequestHeader(header, token);
		});
	});

	$.ajax({
		url: "/email/signup"
		, beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
		}
		, type: "POST"
		, contentType: "application/json; charset=utf-8"
		, data: JSON.stringify(params)
		, dataType: "json"
		, success: function(data) {
			alert("이메일 전송에 성공하였습니다.");
			$("[id='send']").text("인증번호 재발송");
			takeTarget();
		},

		error: function(jqXHR, textStatus, errorThrown) {
			alert(jqXHR.responseJSON.message);
		}
	});
});


// 인증번호 확인
$("[name='authcheck']").off().on("click", function() {
	var email = $("[id='email']").val();
	var serial = $("[id='auth']").val();

	var params = {
		email: email
		, serial: serial
	}
	
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(function() {
		$(document).ajaxSend(function(e, xhr, options) {
			xhr.setRequestHeader(header, token);
		});
	});
	
	$.ajax({
		url: "/check/signup"
		, beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
		}
		, type: "POST"
		, contentType: "application/json; charset=utf-8"
		, data: JSON.stringify(params)
		, dataType: "json"
		, success: function(data) {
			if (data == 1) {
				alert("인증에 성공하였습니다.");
				$("[id='authcheck']").remove();
				$("[id='send']").remove();
				$("[class='target__time']").hide();
				$("[id='auth']").attr("readonly", true);
			} else {
				alert("인증에 실패하였습니다.");
			}
		},

		error: function(jqXHR, textStatus, errorThrown) {
			alert("인증에 실패하였습니다.");
		}
	});
});

//타이머
const remainingMin = document.getElementById("remaining__min");
const remainingSec = document.getElementById("remaining__sec");
const completeBtn = document.getElementById("authcheck");

let time = 300;
const takeTarget = () => {
	setInterval(function() {
		if (time > 0) { // >= 0 으로하면 -1까지 출력된다.
			completeBtn.disabled = false;
			time = time - 1; // 여기서 빼줘야 3분에서 3분 또 출력되지 않고, 바로 2분 59초로 넘어간다.
			let min = Math.floor(time / 60);
			let sec = String(time % 60).padStart(2, "0");
			remainingMin.innerText = min;
			remainingSec.innerText = sec;
			// time = time - 1
		} else {
			completeBtn.disabled = true;
		}
	}, 1000);
};

//이메일 유효성 검사
function emailCheck(email_address) {
	email_regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/i;
	if (!email_regex.test(email_address)) {
		return false;
	} else {
		return true;
	}
}


