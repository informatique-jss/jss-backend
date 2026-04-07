package com.jss.osiris.libs;
/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import java.io.InputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.invoicing.model.InpiInvoicingExtract;

@Service
public class InpiInvoicingExtractParser {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public InpiInvoicingExtract parseInpiItnvoicingExtract(InputStream file) throws OsirisException {
        return null;
    }

}
