var app = new Vue({
  el: '#app',
  data: {
    myKeywords: [],
    newsProviders: [],
  },
  created: function() {
    this.initMyKeywords();
    this.initNewsProviders();
  },
  methods: {
    changed: function(index, newsProvider, event) {
      var providers = this.myKeywords[index].newsProviders;
      if (event.target.checked) {
        var hasProvider = false;
        for (i in providers) {
            if (providers[i].code == newsProvider.code) {
              hasProvider = true;
            }
        }
        if (hasProvider == false) {
          providers.push(newsProvider);
        }
      } else {
        for (i in providers) {
            if (providers[i].code == newsProvider.code) {
              providers.splice(i, 1);
            }
        }
      }
      this.$set(this.myKeywords[index], 'newsProviders', providers);
    },
    save: function(index) {
      if (!this.myKeywords[index].keyword.name.trim()) {
        alert('검색어를 입력하세요.');
      } else if (this.myKeywords[index].newsProviders.length < 1) {
        alert('매체를 하나 이상 선택하세요.');
      } else {
        // 저장처리
        var self = this;
        var csrfHeader = document.querySelector("meta[name='_csrf_header']").getAttribute('content');
        var csrf = document.querySelector("meta[name='_csrf']").getAttribute('content');
        var headers = {'Content-Type': 'application/json'};
        headers[csrfHeader] = csrf;
        var uri = '';
        var method = '';

        if (this.myKeywords[index].code) { // 수정
          uri = '/users/my/keywords/' + this.myKeywords[index].code;
          method = 'PUT';
        } else { // 추가
          uri = '/users/my/keywords';
          method = 'POST';
        }

        fetch(uri, {
          method: method,
          headers: headers,
          body: JSON.stringify(this.myKeywords[index])
        }).then((response) => {
          if (!response.ok) {
            throw new Error("에러");
          }
          return response.json();
        }).then(json => {
          // vue 업데이트
          self.$set(this.myKeywords, index, json);
          self.$set(this.myKeywords[index], 'isEditable', false);
        }).catch(err => {
          alert('저장하지 못했습니다.');
        });
      }
    },
    remove: function(index) {
      var self = this;
      var csrfHeader = document.querySelector("meta[name='_csrf_header']").getAttribute('content');
      var csrf = document.querySelector("meta[name='_csrf']").getAttribute('content');
      var headers = {'Content-Type': 'application/json'};
      headers[csrfHeader] = csrf;
      var uri = '/users/my/keywords/' + this.myKeywords[index].code;
      var method = 'DELETE';

      fetch(uri, {
        method: method,
        headers: headers,
        body: JSON.stringify(this.myKeywords[index])
      }).then((response) => {
        if (!response.ok) {
          throw new Error("에러");
        }
        // vue 업데이트
        self.$delete(this.myKeywords, index);
      }).catch(err => {
        alert('삭제하지 못했습니다.');
      });
    },
    edit: function(index) {
      this.$set(this.myKeywords[index], 'isEditable', true);
    },
    cancel: function(index) {
      if (this.myKeywords[index].code) {
        this.$set(this.myKeywords[index], 'isEditable', false);
      } else {
        this.$delete(app.myKeywords, index);
      }
    },
    addNewKeyword: function() {
      this.myKeywords.push({isEditable:true, code:null, keyword: {name: ""}, newsProviders: []});
    },
    checkProvider: function(myNewsProviders, newsProviderCode) {
      for (var i in myNewsProviders) {
        if (myNewsProviders[i].code == newsProviderCode) {
          return true;
        }
      }
      return false;
    },
    initMyKeywords: function() {
      var self = this;
      fetch('/users/my/keywords', {method: 'GET'}).then((response) => {
        return response.json();
      }).then(json => {
        self.myKeywords = json;
      });
    },
    initNewsProviders: function() {
      var self = this;
      fetch('/providers', {method: 'GET'}).then((response) => {
        return response.json();
      }).then(json => {
        self.newsProviders = json;
      });
    },
    getProviderId: function(newsProviderCode, keywordCode) {
      return "provider_" + newsProviderCode + "_" + keywordCode;
    }
  }
});
