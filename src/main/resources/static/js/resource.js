	const colorPicker = document.getElementById('colorPicker');
	const colorInput = document.getElementById('color');

	// 초기화
	colorPicker.value = colorInput.value;

	// colorPicker 값이 변경될 때 colorInput에 반영
	colorPicker.addEventListener('input', () => {
	    colorInput.value = colorPicker.value;
	});

	// colorInput 값이 변경될 때 colorPicker에 반영
	colorInput.addEventListener('input', () => {
	    colorPicker.value = colorInput.value;
	});