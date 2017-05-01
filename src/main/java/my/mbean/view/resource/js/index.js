$(function () {
	// 方法调用 表单ajax提交
	$('#beanMethodInvokeForm').ajaxForm({
		dataType: 'json',
		beforeSubmit: function(formData, jqForm, options) {
			jqForm.find('.successMsg').html('');
			jqForm.find('.errorMsg').html('');
		    return true;
		},
		success: function(responseText, statusText, xhr, $form) {
			if (responseText.success) {
				if (responseText.result) {
					$form.find('.successMsg').html('success: ' + responseText.result);
				} else {
					$form.find('.successMsg').html('success');
				}
			} else {
				$form.find('.errorMsg').html('failure: ' + responseText.errorMsg);
			}
		}
	});
	// 按下alt+k 后聚焦到 搜索 bean 的输入框.
	$(document).keyup(function(e) {
		// 'k' = 75
		const KEY_COED_K = 75;
		if (e.altKey && e.keyCode == KEY_COED_K) {
			var $kwInput = $('#beanSearchForm input[name=beanName]');
			let v = $kwInput.val();
			$kwInput.val('').focus().val(v).select();
		}
	});
	// 当焦点在 搜索bean的输入框时 按下 ESC 失去焦点.
	$('#beanSearchForm input[name=beanName]').keyup(function(e) {
		const KEY_CODE_ESC = 27;
		if (e.keyCode == KEY_CODE_ESC) {
			$(this).blur();
		}
	});
	$('#typesSelect').change(function(e) {
		$.get({
			url: '',
			data: {
				action: 'getSubtypes', typeName: $(this).val()
			},
			success: function(resp) {
				$('#subtypsArea').html(resp);
			}
		})
	});
	$('#beanChangePropertyForm').submit(function() {
        // inside event callbacks 'this' is the DOM element so we first 
        // wrap it in a jQuery object and then invoke ajaxSubmit 
		$(this).ajaxSubmit({
			type: 'post',
			// timeout:   3000,
	        // other available options: 
	        //url:       url         // override for form's 'action' attribute 
	        //type:      type        // 'get' or 'post', override for form's 'method' attribute 
	        //dataType:  null        // 'xml', 'script', or 'json' (expected server response type) 
	        //clearForm: true        // clear all form fields after successful submit 
	        //resetForm: true        // reset the form after successful submit
			// pre-submit callback
			dataType: 'json',
			beforeSubmit: function(formData, jqForm, options) {
			    // formData is an array; here we use $.param to convert it to a string to display it 
			    // but the form plugin does this for you automatically when it submits the data 
			    var formParams = $.param(formData); 
			 
			    // jqForm is a jQuery object encapsulating the form element.  To access the 
			    // DOM element for the form do this: 
			    // var formElement = jqForm[0]; 
			    // console.log('form params: ', formParams);
			 
			    var valid = this.validate(jqForm);
			    // here we could return false to prevent the form from being submitted; 
			    // returning anything other than false will allow the form submit to continue 
			    return valid; 
			},
			validate: function(jqForm) {
			    // jqForm is a jQuery object which wraps the form DOM element 
			    // 
			    // To validate, we can access the DOM elements directly and return true 
			 
			    var form = jqForm[0];
			    // console.log('=====', form.newValue.value);
			    // if (!form.newValue.value) return false;
			    return true;
			},
			// post-submit callback
			success: function(responseText, statusText, xhr, $form) {
			    // for normal html responses, the first argument to the success callback 
			    // is the XMLHttpRequest object's responseText property 
			 
			    // if the ajaxForm method was passed an Options Object with the dataType 
			    // property set to 'xml' then the first argument to the success callback 
			    // is the XMLHttpRequest object's responseXML property 
			 
			    // if the ajaxForm method was passed an Options Object with the dataType 
			    // property set to 'json' then the first argument to the success callback 
			    // is the json data object returned by the server 
				if (responseText.success) {
					window.location.reload();
				} else {
					$form.find('.errorMsg').html(responseText.errorMsg);
				}
			}
		});
        // !!! Important !!! 
        // always return false to prevent standard browser submit and page navigation 
        return false;
	});
//	$('#beanChangePropertyForm').ajaxForm({});
});
