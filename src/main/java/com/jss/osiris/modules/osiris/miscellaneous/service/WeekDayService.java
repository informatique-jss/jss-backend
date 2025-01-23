package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.WeekDay;

public interface WeekDayService {
    public List<WeekDay> getWeekDays();

    public WeekDay getWeekDay(Integer id);

    public WeekDay addOrUpdateWeekDay(WeekDay weekDay);
}
