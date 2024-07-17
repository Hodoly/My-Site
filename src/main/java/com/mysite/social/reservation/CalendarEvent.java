package com.mysite.social.reservation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarEvent {
    private String start;
    private String end;
    private String title;
    private String color;
    private boolean allday;
    
    public CalendarEvent(String start, String end, String title, String color, boolean allday) {
        this.start = start;
        this.end = end;
        this.title = title;
        this.color = color;
        this.allday = allday;
    }
}
