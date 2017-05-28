var testState = {
        properties: [
        	{
                "name":"LOGGER_NAME",
                "type":"java.lang.String",
                "url":"mbean/beansService?propertyName=LOGGER_NAME&contextId=application",
                "value":{
                    "toString":"my.mbean.service.BeansService"
                },
                "annotationMarks":[

                ],
                "source":"FIELD"
            },
        ],
        methods: [
        ]
    };


// define componet.
Vue.component('total', {
    props: ['total'],
    template: '<span>Total: {{total}}</span>'
});

Vue.component('prop-value', function(resolve, reject) {
	$.get(staticUrlPrefix + "/template/property-value.html").then(function (resp) {
		resolve({
			props: ['value'],
			template: resp,
		})
	});
});

Vue.component('properties-view', {
    props: ['properties'],
    template: '#properties-view-template'
});

Vue.component('methods-view', {
    props: ['methods'],
    template: '#methods-view-template'
});
//Vue.component('app', {
//    template: '#app-template'
//});


//Vue.component('prop-item', function(resolve, reject) {
//	$.get(staticUrlPrefix + "/template/property-item.html").then(function (resp) {
//		resolve({
//			props: ['prop'],
//			template: resp
//		})
//	});
//});
//Vue.component('prop-list', {
//    props: ['properties'],
//    template: '#property-list-template'
//});


var app = new Vue({
    el : '#app',
    data : function() {
        if (initState) return initState;
        else return testState;
        //return testState;
    },
    template: '#app-template',
    components: {
//        'app': {
//            props: ['properties', 'methods'],
//            template: '#app-template'
//        }
    },
    computed: {
    },
    methods: {
    }
});