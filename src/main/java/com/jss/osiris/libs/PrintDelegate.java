package com.jss.osiris.libs;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.InvoiceLabelResult;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.CustomerOrder;

@Service
public class PrintDelegate {

    @Value("${printer.label.ip}")
    private String printerIp;

    @Value("${printer.label.port}")
    private Integer printerPort;

    @Autowired
    ConstantService constantService;

    public void printMailingLabel(InvoiceLabelResult label, CustomerOrder customerOrder) throws OsirisException {

        Socket socket = null;
        DataOutputStream dOut = null;
        try {
            socket = new Socket(printerIp, printerPort);
            dOut = new DataOutputStream(socket.getOutputStream());

            // Handle manual adresses
            if (label.getBillingLabelCity() == null) {
                List<String> lines = Arrays.asList(label.getBillingLabelAddress().split("\\R"));
                if (lines != null && lines.size() > 1) {
                    label.setBillingLabelAddress(lines.get(0));
                    label.setBillingLabelPostalCode("");
                    for (int i = 1; i < lines.size(); i++)
                        label.setBillingLabelPostalCode(label.getBillingLabelPostalCode() + " " + lines.get(i));
                }
            }
            dOut.writeUTF("\r\n");
            dOut.writeUTF(" " + customerOrder.getId());
            dOut.writeUTF("\r\n");
            dOut.flush();
            dOut.writeUTF("\r\n");
            dOut.flush();
            if (label.getBillingLabel() != null) {
                List<String> labelLines = Arrays.asList(label.getBillingLabel().split("\\R"));
                if (labelLines != null) {
                    for (String line : labelLines) {
                        List<String> lineToPrint = new ArrayList<String>();
                        if (line.contains("\r\n")) {
                            lineToPrint = Arrays.asList(line.split("\r\n"));
                        } else
                            lineToPrint.add(line);
                        for (String lin : lineToPrint) {
                            dOut.writeUTF("               " + StringUtils.stripAccents(lin).toUpperCase());
                            dOut.writeUTF("\r\n");
                            dOut.flush();
                        }
                    }
                }
            }

            dOut.writeUTF(
                    "               " + StringUtils
                            .stripAccents((label.getBillingLabelAddress() != null ? label.getBillingLabelAddress()
                                    : ""))
                            .toUpperCase());
            dOut.writeUTF("\r\n");
            dOut.flush();
            dOut.writeUTF("\r\n");
            dOut.flush();
            dOut.writeUTF(
                    "               ");
            if (customerOrder.getTiers() != null) {
                dOut.writeUTF(StringUtils.stripAccents((customerOrder.getTiers().getIntercom() != null
                        ? customerOrder.getTiers().getIntercom()
                        : ""))
                        .toUpperCase() + " ");
            }
            if (customerOrder.getResponsable() != null) {
                dOut.writeUTF((customerOrder.getResponsable().getBuilding() != null
                        ? (" | " + customerOrder.getResponsable().getBuilding())
                        : "") + " "
                        + (customerOrder.getResponsable().getFloor() != null
                                ? (" | " + customerOrder.getResponsable().getFloor())
                                : ""));
            }
            dOut.flush();
            dOut.writeUTF("\r\n");
            dOut.flush();
            dOut.writeUTF("\r\n");
            dOut.flush();
            dOut.writeUTF("               " + (label.getBillingLabelPostalCode() != null
                    ? label.getBillingLabelPostalCode()
                    : "") + " "
                    + (label.getBillingLabelCity() != null
                            ? label.getBillingLabelCity().getLabel()
                            : "")
                    + " "
                    + (label.getCedexComplement() != null
                            ? StringUtils.stripAccents(label.getCedexComplement()).toUpperCase()
                            : "")
                    + " "
                    + ((label.getBillingLabelCountry() == null || label
                            .getBillingLabelCountry().getId()
                            .equals(constantService.getCountryFrance().getId())) ? ""
                                    : label.getBillingLabelCountry().getLabel()));
            dOut.writeUTF("\r\n");
            dOut.flush();
        } catch (IOException e) {
            throw new OsirisException(e, "Error when printing");
        } finally {
            if (dOut != null)
                try {
                    dOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            throw new OsirisException(e, "Error when printing");
        }
    }
}
