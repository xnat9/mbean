Vue.component('prop-value', function(resolve, reject) {
	$.get(staticUrlPrefix + "/template/property-value.html").then(function (resp) {
		resolve({
			props: ['value'],
			template: resp
		})
	});
});
Vue.component('property-change-form', {
    props: ['changeUrl'],
    template: '#property-change-form-template',
    data: function() {
        var newValue = null;
        if (this.value.type == 'boolean') {
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
                beanName: this.beanName
                newValue: this.formState.newValue
            }
        },
        submit: function() {
            var formState = this.changePropertyFormState;
            // console.log("changePropertyFormState: ", this.changePropertyFormState);
            $.post({
                url: this.changeUrl,
                data: this.changePropertyFormState,
                dataType: 'json',
                success: function(resp) {
                    if (resp.success) {
                        window.location.reload();
                    } else {
                        formState.changeResult = resp.errorMsg;
                    }
                    // console.log('resp: ', resp);
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