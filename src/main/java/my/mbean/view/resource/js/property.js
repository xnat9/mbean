Vue.component('prop-value', function(resolve, reject) {
	$.get(staticUrlPrefix + "/template/property-value.html").then(function (resp) {
		resolve({
			props: ['value'],
			template: resp
		})
	});
});
Vue.component('property-change-form', {
    props: ['changeUrl', 'type', 'writable', 'value', 'contextId', 'propertyName', 'beanName'],
    template: '#property-change-form-template',
    data: function() {
        var newValue = null;
        if (this.type == 'boolean') {
            newValue = this.value.originValue;
        }
        return {
            formState: {
                newValue: newValue,
                changeResult: null
            }
        };
    },
    methods: {
        buildSubmitData: function() {
            return {
                contextId: this.contextId,
                propertyName: this.propertyName,
                beanName: this.beanName,
                newValue: this.formState.newValue
            }
        },
        submit: function() {
            var formState = this.formState;
            var submitData = this.buildSubmitData();
            $.post({
                url: this.changeUrl,
                data: submitData,
                dataType: 'json',
                success: function(resp) {
//                    console.log('resp: ', resp);
                    if (resp.success) {
                        window.location.reload();
                    } else {
                        formState.changeResult = resp.errorMsg;
                    }
                }
            });
        }
    }
});

var app = new Vue({
    el : '#app',
    template: '#app-template',
    data : function() {
        if (initState) return initState;
        else return testState;
        //return testState;
    },
    methods: {
        changeProp: function(event) {

        }
    }
});