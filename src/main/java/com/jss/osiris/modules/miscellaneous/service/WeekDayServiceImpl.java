package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.miscellaneous.model.WeekDay;
import com.jss.osiris.modules.miscellaneous.repository.WeekDayRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeekDayServiceImpl implements WeekDayService {

    @Autowired
    WeekDayRepository weekDayRepository;

    @Override
    public List<WeekDay> getWeekDays() {
        return IterableUtils.toList(weekDayRepository.findAll());
    }

    @Override
    public WeekDay getWeekDay(Integer id) {
        Optional<WeekDay> weekDay = weekDayRepository.findById(id);
        if (!weekDay.isEmpty())
            return weekDay.get();
        return null;
    }
	
	 @Override
    public WeekDay addOrUpdateWeekDay(
            WeekDay weekDay) {
        return weekDayRepository.save(weekDay);
    }
}
