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
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthorityType;
import com.jss.osiris.modules.miscellaneous.repository.CityRepository;
import com.jss.osiris.modules.miscellaneous.repository.CompetentAuthorityRepository;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.guichetUnique.RneCompany;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeJuridique;
import com.jss.osiris.modules.quotation.repository.guichetUnique.FormeJuridiqueRepository;
import com.jss.osiris.modules.quotation.service.RneDelegateService;

@Service
public class PrintDelegate {

  @Autowired
  RneDelegateService rneDelegateService;

  @Autowired
  CityRepository cityRepository;

  @Autowired
  CompetentAuthorityRepository competentAuthorityRepository;

  @Autowired
  ConstantService constantService;

  @Autowired
  FormeJuridiqueRepository formeJuridiqueRepository;

  String provisionType, registreAC, affaireName, affaireAddress, cpCedex, siren, legalForm = "";

  @Value("${printer.label.ip}")
  private String printerIp;

  @Value("${printer.label.port}")
  private Integer printerPort;

  public void printMailingLabel(InvoiceLabelResult label, CustomerOrder customerOrder, boolean printProvisionRegister) throws OsirisException {

    if (customerOrder.getAssoAffaireOrders() != null
        && !customerOrder.getAssoAffaireOrders().isEmpty()
        && customerOrder.getAssoAffaireOrders().get(0).getAffaire() != null) {

      provisionType = customerOrder.getAssoAffaireOrders().get(0).getProvisions().get(0).getProvisionFamilyType()
          .getLabel();
      Affaire affaire = customerOrder.getAssoAffaireOrders().get(0).getAffaire();
      
      List<RneCompany> rneCompany = (affaire != null && affaire.getSiren() != null)
          ? rneDelegateService.getCompanyBySiren(affaire.getSiren())
          : null;

      String legalFormNumber = (rneCompany != null && rneCompany.get(0).getFormality() != null)
          ? rneCompany.get(0).getFormality().getFormeJuridique().getLabel()
          : "";
      FormeJuridique legalForm = (legalFormNumber.isEmpty()) ? new FormeJuridique()
          : formeJuridiqueRepository.findByCode(legalFormNumber);

      String legalFormLabel = (legalForm != null) ? legalForm.getLabel() : "";

      if (!affaire.getIsIndividual())
        affaireName = affaire.getDenomination();
      else {
        if (affaire.getCivility() != null && affaire.getFirstname() != null && affaire.getLastname() != null)
          affaireName = affaire.getCivility().getLabel() + " " + affaire.getFirstname() + " " + affaire.getLastname();
      }

      if (affaire.getAddress() != null) {
        affaireAddress = affaire.getAddress();
        if (affaire.getCity() != null) {
          affaireAddress += " - " + affaire.getCity().getLabel();
        }
        if (affaire.getPostalCode() != null) {
          affaireAddress += " - " + affaire.getPostalCode();
        }
      }
      if (affaire.getCedexComplement() != null)
        cpCedex = affaire.getCedexComplement();

      if (affaire.getSiren() != null)
        siren = affaire.getSiren();

      String cityRne = "";
      if (rneCompany != null &&
          rneCompany.get(0).getFormality() != null &&
          rneCompany.get(0).getFormality().getContent() != null &&
          rneCompany.get(0).getFormality().getContent().getPersonneMorale() != null &&
          rneCompany.get(0).getFormality().getContent().getPersonneMorale().getAdresseEntreprise() != null &&
          rneCompany.get(0).getFormality().getContent().getPersonneMorale().getAdresseEntreprise()
              .getAdresse() != null) {
        cityRne = rneCompany.get(0).getFormality().getContent().getPersonneMorale().getAdresseEntreprise().getAdresse()
            .getCommune();
      }
      List<City> city = cityRepository.findByLabelContainingIgnoreCase(cityRne);
      Integer idCity = (city != null && !city.isEmpty()) ? city.get(0).getId() : 0;

      CompetentAuthorityType competentAuthorityType = (constantService.getCompetentAuthorityTypeRcs() != null)
          ? constantService.getCompetentAuthorityTypeRcs() : "";
      List<CompetentAuthority> ca = competentAuthorityRepository.findByCityAndCompetentAuthorityType(city.get(0),
          competentAuthorityType);

      if (ca != null) {
        registreAC = ca.get(0).getLabel();
      } else {
        registreAC = "";
      }

    Socket socket = null;
    DataOutputStream dOut = null;
    try {
      socket = new Socket(printerIp, printerPort);
      dOut = new DataOutputStream(socket.getOutputStream());

      if (printProvisionRegister) {

        dOut.writeUTF("\r\n");
        dOut.flush();
        dOut.writeUTF(
            "       " + StringUtils
                .stripAccents((provisionType != null ? provisionType
                    : ""))
                .toUpperCase());
        dOut.writeUTF("\r\n");
        dOut.flush();
        dOut.writeUTF("\r\n");
        dOut.flush();
        dOut.writeUTF(
            "            " + StringUtils
                .stripAccents((affaireName != null ? affaireName
                    : ""))
                .toUpperCase());
        dOut.writeUTF("\r\n");
        dOut.flush();
        dOut.writeUTF("\r\n");
        dOut.flush();
        if (affaireAddress != null) {
          List<String> labelLines = Arrays.asList(affaireAddress.split("\\R"));
          if (labelLines != null) {
            for (String line : labelLines) {
              List<String> lineToPrint = new ArrayList<String>();
              if (line.contains("\r\n")) {
                lineToPrint = Arrays.asList(line.split("\r\n"));
              } else
                lineToPrint.add(line);
              for (String lin : lineToPrint) {
                dOut.writeUTF("            " + StringUtils.stripAccents(lin).toUpperCase());
                dOut.writeUTF("\r\n");
                dOut.flush();
              }
            }
          }
        }
        dOut.writeUTF("\r\n");
        dOut.flush();
        dOut.writeUTF("             " + StringUtils.stripAccents((cpCedex != null
            ? cpCedex
            : ""))
            .toUpperCase() + " ");
        dOut.writeUTF("\r\n");
        dOut.flush();
        dOut.writeUTF(
            "             " + StringUtils
                .stripAccents((siren != null ? siren
                    : ""))
                .toUpperCase());
        dOut.writeUTF("\r\n");
        dOut.flush();
        dOut.writeUTF(
            "            " + StringUtils.stripAccents((legalFormLabel != null ? legalFormLabel
                : ""))
                .toUpperCase());
        dOut.writeUTF("\r\n");
        dOut.flush();
        dOut.writeUTF(
            "            " + StringUtils.stripAccents((registreAC != null ? registreAC
                : ""))
                .toUpperCase());
      } else {


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
          + (customerOrder.getTiers() != null ? StringUtils.stripAccents(
              (customerOrder.getTiers().getIntercom() != null ? customerOrder.getTiers().getIntercom()
                  : ""))
              .replaceAll("\\p{C}", "").toUpperCase() + " " : "")
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
}
