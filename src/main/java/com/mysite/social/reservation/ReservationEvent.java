package com.mysite.social.reservation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationEvent {
    private String start;
    private String end;
    private String title;
    private String color;
    private boolean allday;
    private Integer id; 
    
    public ReservationEvent(String start, String end, String title, String color, boolean allday, Integer id) {
        this.start = start;
        this.end = end;
        this.title = title;
        this.color = color;
        this.allday = allday;
        this.id = id;
    }
}
