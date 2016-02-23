package cn.ismartv.voice.data.http;

/**
 * Created by huaijie on 2/22/16.
 */
public class AttributesEntity {
    private String[][] actor;
    private String[][] director;
    private String[] area;
    private String[][] attendee;


    public String[][] getActor() {
        return actor;
    }

    public void setActor(String[][] actor) {
        this.actor = actor;
    }

    public String[][] getDirector() {
        return director;
    }

    public void setDirector(String[][] director) {
        this.director = director;
    }

    public String[] getArea() {
        return area;
    }

    public void setArea(String[] area) {
        this.area = area;
    }

    public String[][] getAttendee() {
        return attendee;
    }

    public void setAttendee(String[][] attendee) {
        this.attendee = attendee;
    }
}
