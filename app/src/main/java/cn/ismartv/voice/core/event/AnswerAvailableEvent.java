package cn.ismartv.voice.core.event;

/**
 * Created by huaijie on 2/27/16.
 */
public class AnswerAvailableEvent {
    public static final int NETWORK_ERROR = 0x0001;

    public static final int REFRESH_APP_DATA_FIRST_TIEM = 0x0002;
    public static final int REFRESH_APP_DATA_AGAIN = 0x0003;
    public static final int REFRESH_VOD_DATA_FIRST_TIEM = 0x0002;
    public static final int REFRESH_VOD_DATA_AGAIN = 0x0003;


    private EventType eventType;
    private int eventCode;

    public EventType getEventType() {
        return eventType;
    }

    public int getEventCode() {
        return eventCode;
    }

    public AnswerAvailableEvent(EventType eventType, int eventCode) {
        this.eventType = eventType;
        this.eventCode = eventCode;
    }

    public enum EventType {
        NETWORK_ERROR,
        REFRESH_DATA
    }


}
