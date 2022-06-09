package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.WeekDay;

public interface WeekDayService {
    public List<WeekDay> getWeekDays();

    public WeekDay getWeekDay(Integer id);
}
