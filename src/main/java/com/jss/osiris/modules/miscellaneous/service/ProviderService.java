package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.model.Provider;

public interface ProviderService {
    public List<Provider> getProviders();

    public Provider getProvider(Integer id);

    public Provider addOrUpdateProvider(Provider provider) throws OsirisException;
}
