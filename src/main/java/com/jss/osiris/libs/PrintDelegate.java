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
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderAssignation;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

@Service
public class PrintDelegate {

  @Value("${printer.label.ip}")
  private String printerIp;

  @Value("${printer.label.second.ip}")
  private String printerIpForRegisteredLetter;

  @Value("${printer.label.port}")
  private Integer printerPort;

  @Autowired
  ConstantService constantService;

  @Autowired
  TiersService tiersService;

  public void printMailingLabel(InvoiceLabelResult label, CustomerOrder customerOrder) throws OsirisException {

    String customerOrderReference = "";
    if (customerOrder != null) {
      customerOrderReference = customerOrder.getId() + "";

      if (customerOrder.getCustomerOrderAssignations() != null) {
        Employee employee = null;
        for (CustomerOrderAssignation customerOrderAssignation : customerOrder.getCustomerOrderAssignations())
          if (customerOrderAssignation.getAssignationType().getId()
              .equals(constantService.getAssignationTypeFormaliste().getId()))
            employee = customerOrderAssignation.getEmployee();

        if (employee != null) {
          customerOrderReference += "/" + employee.getFirstname().substring(0, 1).toUpperCase()
              + employee.getLastname().substring(0, 1).toUpperCase();
        }
      }
    }

    String address1 = "";
    String address2 = "";
    String address3 = "";
    String address4 = "";

    List<String> labelLines = Arrays.asList(label.getBillingLabel().split("\\n"));
    if (labelLines != null) {
      List<String> lineToPrint = new ArrayList<String>();
      for (String line : labelLines) {
        if (line.contains("\r\n")) {
          lineToPrint = Arrays.asList(line.split("\r\n"));
        } else
          lineToPrint.add(line);
      }

      if (label.getBillingLabelAddress() != null)
        lineToPrint.add(label.getBillingLabelAddress());

      if (lineToPrint != null) {
        if (lineToPrint.size() > 0)
          address1 = lineToPrint.get(0);
        if (lineToPrint.size() > 1)
          address2 = lineToPrint.get(1);
        if (lineToPrint.size() > 2)
          address3 = lineToPrint.get(2);
        if (lineToPrint.size() > 3)
          address3 = lineToPrint.get(3);
      }
    }

    String postalCodeAndCity = "";

    if (label.getBillingLabelPostalCode() != null)
      postalCodeAndCity += label.getBillingLabelPostalCode().replaceAll("\\p{C}", "");

    if (label.getBillingLabelComplementCedex() != null)
      postalCodeAndCity += " " + label.getBillingLabelComplementCedex().toUpperCase().replaceAll("\\p{C}", "");

    if (label.getBillingLabelCity() != null)
      postalCodeAndCity += " " + label.getBillingLabelCity().getLabel().replaceAll("\\p{C}", "");

    if (label.getBillingLabelCountry() != null
        && !label.getBillingLabelCountry().getId().equals(constantService.getCountryFrance().getId()))
      postalCodeAndCity += " " + label.getBillingLabelCountry().getLabel();

    printZebraLabel(address1, address2, address3, address4, postalCodeAndCity, customerOrderReference);

  }

  @Transactional(rollbackFor = Exception.class)
  public void printTiersLabel(Tiers tiers, Responsable responsable) throws OsirisException {
    tiers = tiersService.getTiers(tiers.getId());

    String customerOrderReference = "";
    String address1 = "";
    String address2 = "";
    String address3 = "";
    String address4 = "";

    String billingLabel = tiers.getMailRecipient();
    if (billingLabel == null || billingLabel.trim().length() == 0) {
      if (tiers.getIsIndividual() != null && tiers.getIsIndividual()) {
        billingLabel = tiers.getFirstname() + " " + tiers.getLastname();
      } else {
        billingLabel = tiers.getDenomination();
      }
    }

    List<String> labelLines = Arrays.asList(billingLabel.split("\\n"));
    if (billingLabel != null) {
      if (labelLines != null) {
        List<String> lineToPrint = new ArrayList<String>();
        if (responsable != null) {
          lineToPrint.add(responsable.getFirstname() + " " + responsable.getLastname());
        }
        for (String line : labelLines) {
          if (line.contains("\r\n")) {
            lineToPrint = Arrays.asList(line.split("\r\n"));
          } else
            lineToPrint.add(line);
        }

        if (tiers.getAddress() != null) {
          lineToPrint.add(tiers.getAddress().replaceAll("\\p{C}", "").toUpperCase());
        }

        if (lineToPrint != null) {
          if (lineToPrint.size() > 0)
            address1 = lineToPrint.get(0);
          if (lineToPrint.size() > 1)
            address2 = lineToPrint.get(1);
          if (lineToPrint.size() > 2)
            address3 = lineToPrint.get(2);
          if (lineToPrint.size() > 3)
            address3 = lineToPrint.get(3);
        }
      }
    }

    String postalCodeAndCity = "";

    if (tiers.getPostalCode() != null)
      postalCodeAndCity += tiers.getPostalCode().replaceAll("\\p{C}", "");

    if (tiers.getCedexComplement() != null)
      postalCodeAndCity += " " + tiers.getCedexComplement().toUpperCase().replaceAll("\\p{C}", "");

    if (tiers.getCity() != null && tiers.getCity().getLabel() != null)
      postalCodeAndCity += " " + tiers.getCity().getLabel().replaceAll("\\p{C}", "");

    if (tiers.getCountry() != null && !tiers.getCountry().getId().equals(constantService.getCountryFrance().getId()))
      postalCodeAndCity += " " + tiers.getCountry().getLabel();

    printZebraLabel(address1, address2, address3, address4, postalCodeAndCity, customerOrderReference);

  }

  public void printRegisteredLabel(InvoiceLabelResult label, CustomerOrder customerOrder)
      throws OsirisException {
    Socket socket = null;
    DataOutputStream dOut = null;
    try {
      socket = new Socket(printerIpForRegisteredLetter, printerPort);
      dOut = new DataOutputStream(socket.getOutputStream());

      String customerOrderReference = "";
      if (customerOrder != null) {
        customerOrderReference = customerOrder.getId() + "";

        if (customerOrder.getCustomerOrderAssignations() != null) {
          Employee employee = null;
          for (CustomerOrderAssignation customerOrderAssignation : customerOrder.getCustomerOrderAssignations())
            if (customerOrderAssignation.getId().equals(constantService.getAssignationTypeFormaliste().getId()))
              employee = customerOrderAssignation.getEmployee();

          if (employee != null) {
            customerOrderReference += "/" + employee.getFirstname().substring(0, 1).toUpperCase()
                + employee.getLastname().substring(0, 1).toUpperCase();
          }
        }
      }

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
      dOut.writeUTF("    " + customerOrderReference);
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
      dOut.writeUTF("                                          " + "10" + "    " + "Boulevard Haussmann");
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

  private void printZebraLabel(String addressLine1, String addressLine2, String addressLine3, String addressLine4,
      String postalCodeAndCity, String orderReference) throws OsirisException {
    Socket socket = null;
    DataOutputStream dOut = null;
    try {
      socket = new Socket(printerIp, printerPort);
      dOut = new DataOutputStream(socket.getOutputStream());

      addressLine1 = StringUtils.stripAccents(addressLine1);
      addressLine2 = StringUtils.stripAccents(addressLine2);
      addressLine3 = StringUtils.stripAccents(addressLine3);
      addressLine4 = StringUtils.stripAccents(addressLine4);
      postalCodeAndCity = StringUtils.stripAccents(postalCodeAndCity);

      String fontSize = "30,30";
      String blockWidth = "520";
      String maxLines = "2"; // allow to display on 2 lines if text too long
      String justification = "L"; // L for left

      String zpl = "" +
          " ^XA " +
          " ^CI28 " +
          " ^PW800 " +
          " ^LL400 " +
          " ^LH0,0 " +
          " ^CWZ,E:TT0003M_.TTF" +
          "  " +
          " ^FO20,50^GFA,4450,4450,25,gM07F8,gL03IF8,gL07KF803C,gL0OFC,gK03FE03IFE7,gK03FL01E,gK07EL078,gK0FCL0F,gK0F8K01E,g04I01FL07C,Y03FC001FL0F8,Y0IF003FK01F,X03IFC03EK03E,X07E0FE03EK07C,X0F807F03EK078,X0E003F03EK0F8,W01C001F83EJ01F,W038001F83EJ01F,W038I0FC3EJ03E,W07J07C3EJ03E,W0EJ07C3EJ07C,W0CJ03C3EJ07C,V01CJ01C3EJ078,V018K043EJ0F8,V038L03EJ0F8,V03M03EJ0F8,:V06M03EI01F,:V04M03EI01F,V0CM03EI01F,V08M07EI01F,V08M07CI01F,U018M07CI01F,U01N07CI01F,:U01N07C2003F,U02N0F83003E,U02N0F87003E,U02N0F87803E,:gI01F07C03E,gI01F03C03E,gI01F03E03E,gI03E03E07E,gI03E01F07E,gI07C01F07C,gI07C00F87C,gI0F800FCFC,gI0F8007CFC,gH01FI07FF8,gH01FI03FF8,gH03EI03FF,gH03CI01FF,gH07CJ0FE,gH0F8J0FE,gG01FJ01FE,gG01EJ03FF,gG03CJ07FF8,gG07CJ0IF8,gG0F8I03FCFC,g01FJ07F87E,g03EJ0FF03E,g07CI03FE03F,g0FJ0FF801F,Y03EI01FFI0F8,Y07CI07FCI0F8,Y0FI01FFJ07C,X03EI07FCJ07C,X078003FFK03E,W01EI0FFCK03E,W07C007FEL01E,V01E003FFM01E,V07800FF8M01F,U01C007F8O0F,U06007F8P0F,W03FR0F,W0CS0F,gQ0E,:gP01E,:gP01C,gP03C,gP078,gP07,gP0F,gO03E,gO07C,gN01F,gN0FE,gJ03JF,gK01FF,,:::::::::::::::::::::gJ06gJ0301FCg07I01F8X0703FEg07I07FEM0CJ018J0701FEg07I0IFL03CJ01CJ07I0Eg07001C02L078J01CJ07I0Eg07001CN06Q07I0Eg070038g07:I0Eg07003Cg07I0Eg07001Cg07I0E03F01C03067CCFC03F807001FI0CFC00FC01FC181FC07I0E0FF81C030EFCFFE07FE07I0FC00FFE03FE03FE1C3FE07I0E0F3C1C030FC0F8F070E07I07F00F8F078707871C30F07I0E1C0E1C030F80F07800707I01FC0F0787038F001C00707I0E1C0E1C030F00E03800707J07E0E038E038E001C00387I0E38071C030E00E03800307J01F0E038E018E001C00387I0E38071C030E00E03800307K070E038E038E001C00387I0E38071C030E00E0380FF07K070E038IF8E001C0FF87I0E38071C030E00E0383FF07K070E038IF0E001C3FF87E00E38071C030E00E03870307K030E038EI0E001C78387E00E38071C070E00E038E0307K070E038EI0E001C70387E01C1C0E1C070E00E038E07070030070E038EI0E001C60387701C1C0E0E0F0E00E038E070700380F0F0787I0F001C707877C780F3C0F1E0E00E038F1F07003E3E0F8F078387871C70F873FF80FF807FC0E00E0387FB07001FFC0FFE03FF83FF1C7FB870FE003F001F80600E0183F306I07F80EFC00FE01FE183F187gR0E,::::::gR0C,,::X03F8,X0FFEN03I0C04I03,W01F3EN07803C0EI0F,W03804N0700F00E003C,N02N038S0C00E003,N03N03W0E,:N03N038V0E,:N03N03EI03F003F8301FC0FF07F00FE,N03N01F800FFC0FFC703FE0FF0FF81FF,N03O0FF00F1E0F0C707070E01C1C383,M0F303E07EI03F81C0E1C0070E038E0380E3,L01FF0FF0FFJ07C1C071C0070E038E0380E3,L038F1C38CK01E38071C0070E038E0380E38,L070718198L0E3807180070E038E0380E3E,L06071819CL0E3807180070IF8E03FFE1FC,L06031818FL073807180070IF0E03FFC07E,L06031FF87CK073807180070EI0E038J0F,L060318I0EK0638071C0070EI0E038J038,L060318I0300600E1C0E1C0070EI0E038J038,L070718I0300701E1C0E1C0070FI0E03C002038,L038F1C1983007E7C0F3C0F1E7078787E1E1E707,L01FF0FF9FE003FF807F807FC703FF07F0FFC3FF,M0F303F0FCI0FF003F003F8300FE01F03F81FC,,^FS "
          +
          "  " +
          " ^FO660,50 " +
          " ^AZN,22,22 " +
          " ^FD" + orderReference + "^FS " +
          "  " +
          " ^FO260,90 " +
          " ^AZN," + fontSize + " " +
          " ^FB" + blockWidth + "," + maxLines + ",0," + justification + " " +
          " ^FD" + addressLine1 + "^FS " +
          "  " +

          " ^FO260,160 " +
          " ^AZN," + fontSize + " " +
          " ^FB" + blockWidth + "," + maxLines + ",0," + justification + " " +
          " ^FD" + addressLine2 + "^FS " +
          "  " +

          " ^FO260,230 " +
          " ^AZN," + fontSize + " " +
          " ^FB" + blockWidth + "," + maxLines + ",0," + justification + " " +
          " ^FD" + addressLine3 + "^FS " +
          "  " +

          " ^FO260,300 " +
          " ^AZN," + fontSize + " " +
          " ^FB" + blockWidth + "," + maxLines + ",0," + justification + " " +
          " ^FD" + addressLine4 + "^FS " +
          "  " +

          " ^FO260,370 " +
          " ^AZN," + fontSize + " " +
          " ^FB" + blockWidth + "," + maxLines + ",0," + justification + " " +
          " ^FD" + postalCodeAndCity + "^FS " +
          "  " +
          "  " +
          " ^XZ     ";

      dOut.write(zpl.getBytes("CP850"));
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
