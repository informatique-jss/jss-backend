package com.jss.osiris.libs.azure;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;

public interface TranslationService {
    public String translateTextToEnglish(String text) throws OsirisException, OsirisClientMessageException;
}
