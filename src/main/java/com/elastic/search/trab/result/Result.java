package com.elastic.search.trab.result;

public class Result { //classe para conseguir exibir os resultados em JSON
        String url;
        String title;
        String content;

    public Result() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
