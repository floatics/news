var app = new Vue({
  el: '#app',
  data: {
    articles: [],
    page: {
      size: 5,
      page: 0,
      last: false
    }
  },
  created() {
    this.initArticles();
  },
  methods: {
    more: function() {
      this.page.page += 1;
      this.getArticles();
    },
    dateFormat: function(dateString) {
      return moment(dateString).fromNow();
    },
    initArticles: function() {
      this.getArticles();
    },
    getArticles: function() {
      var self = this;
      var query = new URLSearchParams({size: this.page.size, page: this.page.page});
      fetch('/users/my/articles?'+query, {method: 'GET'}).then((response) => {
        return response.json();
      }).then(json => {
        self.articles = self.articles.concat(json.content);
        self.page.size = json.size;
        self.page.page = json.number;
        self.page.last = json.last;
      });
    },
  },
  computed: {
    isEmpty: function() {
      return this.articles.length < 1;
    }
  }
});
