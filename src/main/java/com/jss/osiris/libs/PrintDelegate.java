package com.jss.osiris.libs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.InvoiceLabelResult;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;

@Service
public class PrintDelegate {

    @Value("${printer.label.ip}")
    private String printerIp;

    @Value("${printer.label.port}")
    private Integer printerPort;

    @Autowired
    ConstantService constantService;

    public void printMailingLabel(InvoiceLabelResult label) throws OsirisException {

        Socket socket = null;
        PrintWriter dOut = null;
        try {
            socket = new Socket(printerIp, printerPort);
            dOut = new PrintWriter(socket.getOutputStream());

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

            dOut.println();
            dOut.flush();
            dOut.println();
            dOut.flush();
            dOut.println("                       " + label.getBillingLabel() != null ? label.getBillingLabel() : "");
            dOut.flush();
            dOut.println(
                    "                       " + label.getBillingLabelAddress() != null ? label.getBillingLabelAddress()
                            : "");
            dOut.flush();
            dOut.println("                       ");
            dOut.flush();
            dOut.println("                       ");
            dOut.flush();
            dOut.println();
            dOut.flush();
            dOut.flush();
            dOut.println("                       " + label.getBillingLabelPostalCode() != null
                    ? label.getBillingLabelPostalCode()
                    : "" + " "
                            + label.getBillingLabelCity() != null
                                    ? label.getBillingLabelCity().getLabel()
                                    : "" + " " + label.getCedexComplement() != null ? label.getCedexComplement()
                                            : "" + " "
                                                    + (label.getBillingLabelCountry() != null && label
                                                            .getBillingLabelCountry().getId()
                                                            .equals(constantService.getCountryFrance().getId()) ? ""
                                                                    : label.getBillingLabelCountry().getLabel()));
        } catch (IOException e) {
            throw new OsirisException(e, "Error when printing");
        } finally {
            if (dOut != null)
                dOut.close();
        }

        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            throw new OsirisException(e, "Error when printing");
        }
    }
}
