var testData = {
        beans: [
        ]
    }

var app = new Vue({
	el : '#beansContainer',
	data : function() {
		if (beans) {
			return {
				'beans': beans
			}
		} else {
			return testData;
		}
	},
	components: {
		'bean-item': {
			props: ['bean'],
		    template: '<li class="list-group-item"><a :href="bean.url">{{bean.name}}</a></li>'
		}
	},
	methods: {
		search: function() {
			var self = this;
			$.post({
		        url: window.location.href,
		        dataType: 'json',
		        success: function(resp) {
		        	//console.log("resp: ", resp);
		            if (resp && resp.result && resp.result.beans) {
		            	self.beans = resp.result.beans;
		            } else {
		            	// TODO.
		            }
		        	console.log("this.beans: ", self.beans);
		        }
		    });
		}
	}
})
//app.search();