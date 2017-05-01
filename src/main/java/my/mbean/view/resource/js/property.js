Vue.component('prop-value', function(resolve, reject) {
	$.get(staticUrlPrefix + "/template/property-value.html").then(function (resp) {
		resolve({
			props: ['value'],
			template: resp
		})
	});
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