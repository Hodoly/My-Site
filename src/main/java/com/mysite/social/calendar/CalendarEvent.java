package com.mysite.social.calendar;

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
    private Integer id; 
    private String role;
    
    public CalendarEvent(String start, String end, String title, String color, boolean allday, Integer id, String role) {
        this.start = start;
        this.end = end;
        this.title = title;
        this.color = color;
        this.allday = allday;
        this.id = id;
        this.role = role;
    }
}
