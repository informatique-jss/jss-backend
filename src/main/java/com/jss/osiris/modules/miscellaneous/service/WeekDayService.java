package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.WeekDay;

public interface WeekDayService {
    public List<WeekDay> getWeekDays();

    public WeekDay getWeekDay(Integer id);
}
