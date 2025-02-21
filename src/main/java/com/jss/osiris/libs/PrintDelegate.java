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
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceLabelResult;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

@Service
public class PrintDelegate {

  @Value("${printer.label.ip}")
  private String printerIp;

  @Value("${printer.label.bis.ip}")
  private String printerIpForRegisteredLetter;

  @Value("${printer.label.port}")
  private Integer printerPort;

  @Autowired
  ConstantService constantService;

  @Autowired
  TiersService tiersService;

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
      dOut.writeUTF("  " + customerOrder.getId());
      dOut.writeUTF("\r\n");
      dOut.flush();
      dOut.writeUTF("\r\n");
      dOut.flush();
      if (label.getBillingLabel() != null) {
        List<String> labelLines = Arrays.asList(label.getBillingLabel().split("\\n"));
        if (labelLines != null) {
          for (String line : labelLines) {
            List<String> lineToPrint = new ArrayList<String>();
            if (line.contains("\r\n")) {
              lineToPrint = Arrays.asList(line.split("\r\n"));
            } else
              lineToPrint.add(line);
            for (String lin : lineToPrint) {
              dOut.writeUTF("   " + StringUtils.stripAccents(lin).toUpperCase());
              dOut.writeUTF("\r\n");
              dOut.flush();
            }
          }
        }
      }

      dOut.writeUTF("   "
          + StringUtils
              .stripAccents(
                  (label.getBillingLabelAddress() != null ? label.getBillingLabelAddress() : ""))
              .replaceAll("\\p{C}", "").toUpperCase()
          +
          "    "
          + (StringUtils.stripAccents((customerOrder.getResponsable().getTiers().getIntercom() != null
              ? customerOrder.getResponsable().getTiers().getIntercom()
              : "")).replaceAll("\\p{C}", "").toUpperCase() + " ")
          +
          (customerOrder.getResponsable() != null ? (customerOrder.getResponsable().getBuilding() != null
              ? (" | " + customerOrder.getResponsable().getBuilding())
              : "") + " " +
              (customerOrder.getResponsable().getFloor() != null
                  ? (" | " + customerOrder.getResponsable().getFloor())
                  : "")
              : ""));

      dOut.flush();
      dOut.writeUTF("\r\n");

      dOut.writeUTF("   "
          + (label.getBillingLabelPostalCode() != null
              ? label.getBillingLabelPostalCode().replaceAll("\\p{C}", "")
              : "")
          + " "
          + (label.getBillingLabelComplementCedex() != null
              ? StringUtils.stripAccents(label.getBillingLabelComplementCedex()).toUpperCase()
                  .replaceAll("\\p{C}", "")
              : "")
          + " "
          + StringUtils.stripAccents(label.getBillingLabelCity() != null
              ? label.getBillingLabelCity().getLabel()
              : "").replaceAll("\\p{C}", "")
          + " "
          + ((label.getBillingLabelCountry() == null || label.getBillingLabelCountry().getId()
              .equals(constantService.getCountryFrance().getId())) ? ""
                  : label.getBillingLabelCountry().getLabel().replaceAll("\\p{C}", "")));

      dOut.flush();
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

  @Transactional(rollbackFor = Exception.class)
  public void printTiersLabel(Tiers tiers) throws OsirisException {
    tiers = tiersService.getTiers(tiers.getId());
    Socket socket = null;
    DataOutputStream dOut = null;
    try {
      socket = new Socket(printerIp, printerPort);
      dOut = new DataOutputStream(socket.getOutputStream());

      dOut.writeUTF("\r\n");
      dOut.writeUTF("  " + tiers.getId());
      dOut.writeUTF("\r\n");
      dOut.flush();
      dOut.writeUTF("\r\n");
      dOut.flush();

      String billingLabel = tiers.getMailRecipient();
      if (billingLabel == null || billingLabel.trim().length() == 0) {
        if (tiers.getIsIndividual() != null && tiers.getIsIndividual()) {
          billingLabel = tiers.getFirstname() + " " + tiers.getLastname();
        } else {
          billingLabel = tiers.getDenomination();
        }
      }

      if (billingLabel != null) {
        List<String> labelLines = Arrays.asList(billingLabel.split("\\n"));
        if (labelLines != null) {
          for (String line : labelLines) {
            List<String> lineToPrint = new ArrayList<String>();
            if (line.contains("\r\n")) {
              lineToPrint = Arrays.asList(line.split("\r\n"));
            } else
              lineToPrint.add(line);
            dOut.writeUTF("\r\n");
            for (String lin : lineToPrint) {
              dOut.writeUTF("   " + StringUtils.stripAccents(lin).toUpperCase());
              dOut.writeUTF("\r\n");
              dOut.flush();
            }
          }
        }
      }

      dOut.writeUTF("   "
          + StringUtils
              .stripAccents(
                  (tiers.getAddress() != null ? tiers.getAddress() : ""))
              .replaceAll("\\p{C}", "").toUpperCase()
          +
          "    "
          + (StringUtils.stripAccents(
              (tiers.getIntercom() != null ? tiers.getIntercom().replaceAll("\\p{C}", "").toUpperCase() + " " : ""))));

      dOut.flush();
      dOut.writeUTF("\r\n");

      dOut.writeUTF("   "
          + (tiers.getPostalCode() != null
              ? tiers.getPostalCode().replaceAll("\\p{C}", "")
              : "")
          + " "
          + (tiers.getCedexComplement() != null
              ? StringUtils.stripAccents(tiers.getCedexComplement()).toUpperCase()
                  .replaceAll("\\p{C}", "")
              : "")
          + " "
          + StringUtils.stripAccents(tiers.getCity() != null
              ? tiers.getCity().getLabel()
              : "").replaceAll("\\p{C}", "")
          + " "
          + ((tiers.getCountry() == null || tiers.getCountry().getId()
              .equals(constantService.getCountryFrance().getId())) ? ""
                  : tiers.getCountry().getLabel().replaceAll("\\p{C}", "")));

      dOut.flush();
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

  public void printRegisteredLabel(InvoiceLabelResult label, CustomerOrder customerOrder, String username)
      throws OsirisException {
    Socket socket = null;
    DataOutputStream dOut = null;
    try {
      socket = new Socket(printerIpForRegisteredLetter, printerPort);
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
      dOut.writeUTF("\r\n");
      dOut.writeUTF("\r\n");
      dOut.writeUTF("\r\n");
      dOut.writeUTF("\r\n");
      dOut.writeUTF("\r\n");
      dOut.writeUTF("    " + customerOrder.getId() + '/' + username);
      dOut.writeUTF("\r\n");
      dOut.writeUTF("\r\n");
      dOut.writeUTF("\r\n");
      dOut.writeUTF("\r\n");
      dOut.writeUTF("\r\n");
      if (label.getBillingLabel() != null) {
        List<String> labelLines = Arrays.asList(label.getBillingLabel().split("\\n"));
        if (labelLines != null) {
          for (String line : labelLines) {
            List<String> lineToPrint = new ArrayList<String>();
            if (line.contains("\r\n")) {
              lineToPrint = Arrays.asList(line.split("\r\n"));
            } else
              lineToPrint.add(line);
            for (String lin : lineToPrint) {
              dOut.writeUTF(
                  "                                          " + StringUtils.stripAccents(lin).toUpperCase());
              dOut.writeUTF("\r\n");
              dOut.flush();
            }
          }
        }
      }
      dOut.writeUTF("                                          "
          + StringUtils
              .stripAccents(
                  (label.getBillingLabelAddress() != null ? label.getBillingLabelAddress() : ""))
              .replaceAll("\\p{C}", "").toUpperCase()
          +
          "     "
          + (StringUtils.stripAccents((customerOrder.getResponsable().getTiers().getIntercom() != null
              ? customerOrder.getResponsable().getTiers().getIntercom()
              : "")).replaceAll("\\p{C}", "").toUpperCase() + " ")
          +
          (customerOrder.getResponsable() != null ? (customerOrder.getResponsable().getBuilding() != null
              ? (" | " + customerOrder.getResponsable().getBuilding())
              : "") + " " +
              (customerOrder.getResponsable().getFloor() != null
                  ? (" | " + customerOrder.getResponsable().getFloor())
                  : "")
              : ""));

      dOut.flush();
      dOut.writeUTF("\r\n");
      dOut.flush();
      dOut.writeUTF("                                          "
          + (label.getBillingLabelPostalCode() != null
              ? label.getBillingLabelPostalCode().replaceAll("\\p{C}", "")
              : "")
          + "     "
          + (label.getBillingLabelComplementCedex() != null
              ? StringUtils.stripAccents(label.getBillingLabelComplementCedex()).toUpperCase()
                  .replaceAll("\\p{C}", "")
              : "")
          + "     "
          + StringUtils.stripAccents(label.getBillingLabelCity() != null
              ? label.getBillingLabelCity().getLabel()
              : "").replaceAll("\\p{C}", "")
          + "     "
          + ((label.getBillingLabelCountry() == null || label.getBillingLabelCountry().getId()
              .equals(constantService.getCountryFrance().getId())) ? ""
                  : label.getBillingLabelCountry().getLabel().replaceAll("\\p{C}", "")));

      dOut.flush();
      dOut.writeUTF("\r\n");
      dOut.writeUTF("\r\n");
      dOut.writeUTF("\r\n");
      dOut.writeUTF("\r\n");
      dOut.flush();
      dOut.writeUTF("                                          " + "Journal Special des Societes");
      dOut.writeUTF("\r\n");
      dOut.writeUTF("                                          " + "10" + "    " + "Boulevard Haussman");
      dOut.writeUTF("\r\n");
      dOut.writeUTF("                                          " + "75009" + "      " + "Paris");
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
