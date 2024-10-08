package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.WeekDay;
import com.jss.osiris.modules.osiris.miscellaneous.repository.WeekDayRepository;

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
        if (weekDay.isPresent())
            return weekDay.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WeekDay addOrUpdateWeekDay(
            WeekDay weekDay) {
        return weekDayRepository.save(weekDay);
    }
}
