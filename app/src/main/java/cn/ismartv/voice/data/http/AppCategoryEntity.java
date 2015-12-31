package cn.ismartv.voice.data.http;

import java.util.ArrayList;

/**
 * Created by huaijie on 12/28/15.
 */
public class AppCategoryEntity {
    private Content content;

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public class Content {
        private JsonRes json_res;
        private ArrayList<String> item;

        public JsonRes getJson_res() {
            return json_res;
        }

        public void setJson_res(JsonRes json_res) {
            this.json_res = json_res;
        }

        public ArrayList<String> getItem() {
            return item;
        }

        public void setItem(ArrayList<String> item) {
            this.item = item;
        }
    }

    public class JsonRes {
        private String parsed_text;
        private String raw_text;
        private Results results;

        public String getParsed_text() {
            return parsed_text;
        }

        public void setParsed_text(String parsed_text) {
            this.parsed_text = parsed_text;
        }

        public String getRaw_text() {
            return raw_text;
        }

        public void setRaw_text(String raw_text) {
            this.raw_text = raw_text;
        }

        public Results getResults() {
            return results;
        }

        public void setResults(Results results) {
            this.results = results;
        }
    }

    public class Results {
        private int demand;
        private String domain;
        private String intent;
        private Object object;
        private int score;
        private int update;


        public int getDemand() {
            return demand;
        }

        public void setDemand(int demand) {
            this.demand = demand;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getIntent() {
            return intent;
        }

        public void setIntent(String intent) {
            this.intent = intent;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getUpdate() {
            return update;
        }

        public void setUpdate(int update) {
            this.update = update;
        }
    }

    public class Object {
        private String appname;

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }
    }
}
