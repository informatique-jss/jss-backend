package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormaliteGuichetUniqueStatus;

public interface FormaliteGuichetUniqueStatusService {
    public List<FormaliteGuichetUniqueStatus> getFormaliteGuichetUniqueStatus();

    public FormaliteGuichetUniqueStatus getFormaliteGuichetUniqueStatus(String code);
}
