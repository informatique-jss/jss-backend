package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormaliteGuichetUniqueStatus;
import com.jss.osiris.modules.quotation.repository.guichetUnique.FormaliteGuichetUniqueStatusRepository;

@Service
public class FormaliteGuichetUniqueStatusServiceImpl implements FormaliteGuichetUniqueStatusService {

    @Autowired
    FormaliteGuichetUniqueStatusRepository formaliteGuichetUniqueStatusRepository;

    @Override
    public List<FormaliteGuichetUniqueStatus> getFormaliteGuichetUniqueStatus() {
        return IterableUtils.toList(formaliteGuichetUniqueStatusRepository.findAll());
    }

    @Override
    public FormaliteGuichetUniqueStatus getFormaliteGuichetUniqueStatus(String code) {
        Optional<FormaliteGuichetUniqueStatus> formaliteGuichetUniqueStatus = formaliteGuichetUniqueStatusRepository
                .findById(code);
        if (formaliteGuichetUniqueStatus.isPresent())
            return formaliteGuichetUniqueStatus.get();
        return null;
    }
}
