package com.jss.osiris.modules.quotation.model.infoGreffe;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EvenementInfogreffeEntityKey implements Serializable {

    public LocalDateTime createdDate;
    public String codeEtat;

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

   
}
