package com.jss.osiris.modules.quotation.model.guichetUnique;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Generate {

        @Autowired
        EntityManager em;

        // @Scheduled(initialDelay = 1000, fixedDelay = 10000000)
        @Transactional
        public void generate() throws FileNotFoundException, IOException {
                XSSFWorkbook wb = null;
                PrintWriter writerScript = new PrintWriter(
                                "C:\\TEMP\\script.sql",
                                "UTF-8");
                try {
                        String xlsAdress = "C:\\TEMP\\dico.xlsx";
                        wb = new XSSFWorkbook(new FileInputStream(xlsAdress));

                        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                                XSSFSheet sheet = wb.getSheetAt(i);
                                String className = StringUtils.capitalize(sheet.getSheetName());

                                File f = new File(
                                                "C:\\Users\\GAPIN\\Desktop\\workspace-osiris\\src\\main\\java\\com\\jss\\osiris\\modules\\quotation\\model\\guichetUnique\\referentials\\"
                                                                + className + ".java");
                                if (!f.exists()) {

                                        PrintWriter writer = new PrintWriter(
                                                        "C:\\Users\\GAPIN\\Desktop\\workspace-osiris\\src\\main\\java\\com\\jss\\osiris\\modules\\quotation\\model\\guichetUnique\\referentials\\"
                                                                        + className + ".java",
                                                        "UTF-8");
                                        writer.println("package com.jss.osiris.modules.quotation.model.guichetUnique.referentials;");
                                        writer.println("");
                                        writer.println("import java.io.Serializable;");
                                        writer.println("");
                                        writer.println("import javax.persistence.Column;");
                                        writer.println("import javax.persistence.Entity;");
                                        writer.println("import javax.persistence.Id;");
                                        writer.println("");
                                        writer.println("import com.jss.osiris.modules.miscellaneous.model.ICode;");
                                        writer.println("");
                                        writer.println("@Entity");
                                        writer.println("public class " + className
                                                        + " implements Serializable, ICode {");
                                        writer.println("");
                                        writer.println("    @Id");
                                        writer.println("    private String code;");
                                        writer.println("");
                                        writer.println("    @Column( columnDefinition = \"TEXT\")");
                                        writer.println("    private String label;");
                                        writer.println("");
                                        writer.println("    public String getCode() {");
                                        writer.println("        return code;");
                                        writer.println("    }");
                                        writer.println("");
                                        writer.println("    public void setCode(String code) {");
                                        writer.println("        this.code = code;");
                                        writer.println("    }");
                                        writer.println("");
                                        writer.println("    public String getLabel() {");
                                        writer.println("        return label;");
                                        writer.println("    }");
                                        writer.println("");
                                        writer.println("    public void setLabel(String label) {");
                                        writer.println("        this.label = label;");
                                        writer.println("    }");
                                        writer.println("");
                                        writer.println("}");
                                        writer.close();

                                        writer = new PrintWriter(
                                                        "C:\\Users\\GAPIN\\Desktop\\workspace-osiris\\client\\src\\app\\modules\\quotation\\model\\guichet-unique\\referentials\\"
                                                                        + className + ".ts",
                                                        "UTF-8");
                                        writer.println("import { IReferential } from '../../../../administration/model/IReferential';");
                                        writer.println("export interface " + className + " extends IReferential {");
                                        writer.println("}");
                                        writer.close();
                                }

                                String tableName = String.join("_", className.split("(?=\\p{Upper})"));
                                if (className.equals("CodeEEEPays"))
                                        tableName = "codeeeepays";
                                if (className.equals("ConditionVersementTVA"))
                                        tableName = "condition_versementtva";
                                if (className.equals("DiffusionINSEE"))
                                        tableName = "diffusioninsee";
                                if (className.equals("OptionJQPA"))
                                        tableName = "optionjqpa";
                                if (className.equals("RegimeImpositionTVA"))
                                        tableName = "regime_impositionTVA";
                                if (className.equals("SituationVisAVisMsa"))
                                        tableName = "situation_visavis_msa";
                                if (className.equals("StatutVisAVisFormalite"))
                                        tableName = "statut_visavis_formalite";

                                em.createNativeQuery("delete from " + tableName).executeUpdate();
                                writerScript.println("delete from " + tableName + ";");
                                for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
                                        if (j != 0) {
                                                String codeValue = sheet.getRow(j).getCell(0).getCellType()
                                                                .equals(CellType.NUMERIC)
                                                                                ? Math.round(sheet.getRow(j).getCell(0)
                                                                                                .getNumericCellValue())
                                                                                                + ""
                                                                                : sheet.getRow(j).getCell(0)
                                                                                                .getStringCellValue();
                                                String labelValue = sheet.getRow(j).getCell(1).getCellType()
                                                                .equals(CellType.NUMERIC)
                                                                                ? Math.round(sheet.getRow(j).getCell(1)
                                                                                                .getNumericCellValue())
                                                                                                + ""
                                                                                : sheet.getRow(j).getCell(1)
                                                                                                .getStringCellValue();

                                                codeValue = codeValue.replaceAll("'", "''");
                                                labelValue = labelValue.replaceAll("'", "''");
                                                writerScript.println(
                                                                "insert into " + tableName + "(code,label) values ('"
                                                                                + codeValue + "','"
                                                                                + labelValue + "');");
                                                System.out.println("insert into " + tableName + "(code,label) values ('"
                                                                + codeValue + "','"
                                                                + labelValue + "')");
                                                em.createNativeQuery(
                                                                "insert into " + tableName + "(code,label) values ('"
                                                                                + codeValue + "','"
                                                                                + labelValue + "')")
                                                                .executeUpdate();
                                        }
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                } finally {
                        writerScript.close();
                        if (wb != null)
                                wb.close();
                }
        }

        // @Scheduled(initialDelay = 1000, fixedDelay = 10000000)
        public void generateForms() throws FileNotFoundException, IOException {
                XSSFWorkbook wb = null;
                PrintWriter writerController = new PrintWriter(
                                "C:\\Users\\GAPIN\\Desktop\\workspace-osiris\\src\\main\\java\\com\\jss\\osiris\\modules\\quotation\\controller\\GuicheUniqueReferentialController.java",
                                "UTF-8");

                PrintWriter writerCache = new PrintWriter(
                                "C:\\TEMP\\cache.txt",
                                "UTF-8");
                PrintWriter writerComponentName = new PrintWriter(
                                "C:\\TEMP\\component_name.txt",
                                "UTF-8");

                writerController.println("package com.jss.osiris.modules.quotation.controller;");
                writerController.println("");
                writerController.println("import java.util.List;");
                writerController.println("");
                writerController.println("import org.slf4j.Logger;");
                writerController.println("import org.slf4j.LoggerFactory;");
                writerController.println("import org.springframework.beans.factory.annotation.Autowired;");
                writerController.println("import org.springframework.http.HttpStatus;");
                writerController.println("import org.springframework.http.ResponseEntity;");
                writerController.println("import org.springframework.web.bind.annotation.GetMapping;");
                writerController.println("import org.springframework.web.bind.annotation.RestController;");
                writerController.println("import org.springframework.web.client.HttpStatusCodeException;");
                writerController.println("");
                writerController.println("");
                writerController.println("@RestController");
                writerController.println("public class GuicheUniqueReferentialController {");
                writerController.println("");
                writerController.println(
                                "  private static final String inputEntryPoint = \"/quotation/guichet-unique\";");
                writerController.println("");
                writerController.println(
                                "  private static final Logger logger = LoggerFactory.getLogger(GuicheUniqueReferentialController.class);");
                writerController.println("");

                try {
                        String xlsAdress = "C:\\TEMP\\dico.xlsx";
                        wb = new XSSFWorkbook(new FileInputStream(xlsAdress));

                        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                                XSSFSheet sheet = wb.getSheetAt(i);
                                String className = StringUtils.capitalize(sheet.getSheetName());

                                int nbr = sheet.getPhysicalNumberOfRows();
                                nbr--;

                                String formFolder = "C:\\Users\\GAPIN\\Desktop\\workspace-osiris\\client\\src\\app\\modules\\miscellaneous\\components\\forms\\guichet-unique\\";

                                String formName = String.join("-", className.split("(?=\\p{Upper})"));
                                if (className.equals("CodeEEEPays"))
                                        formName = "code-eee-pays";
                                if (className.equals("ConditionVersementTVA"))
                                        formName = "condition-versement-tva";
                                if (className.equals("DiffusionINSEE"))
                                        formName = "diffusion-insee";
                                if (className.equals("OptionJQPA"))
                                        formName = "option-jqpa";
                                if (className.equals("RegimeImpositionTVA"))
                                        formName = "regime-imposition-tva";
                                if (className.equals("SituationVisAVisMsa"))
                                        formName = "situation-visavis-msa";
                                if (className.equals("StatutVisAVisFormalite"))
                                        formName = "statut-visavis-formalite";

                                formName = formName.toLowerCase();

                                if (nbr <= 3) {
                                        // radio
                                        Files.createDirectories(Paths.get(formFolder + "radio-group-" + formName));

                                        writerComponentName.println("RadioGroup" + className + "Component,");

                                        PrintWriter writer = new PrintWriter(
                                                        formFolder + "radio-group-" + formName + "\\" + "radio-group-"
                                                                        + formName + ".component.ts",
                                                        "UTF-8");

                                        writer.println("import { Component, OnInit } from '@angular/core';");
                                        writer.println("import { UntypedFormBuilder } from '@angular/forms';");
                                        writer.println(
                                                        "import { " + className
                                                                        + "Service } from 'src/app/modules/miscellaneous/services/guichet-unique/"
                                                                        + formName.replaceAll("-", ".") + ".service';");
                                        writer.println(
                                                        "import { " + className
                                                                        + " } from 'src/app/modules/quotation/model/guichet-unique/referentials/"
                                                                        + className + "';");
                                        writer.println("import { UserNoteService } from 'src/app/services/user.notes.service';");
                                        writer.println(
                                                        "import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';");
                                        writer.println("");
                                        writer.println("@Component({");
                                        writer.println("  selector: 'radio-group-" + formName + "',");
                                        writer.println("  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',");
                                        writer.println("  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']");
                                        writer.println("})");
                                        writer.println(
                                                        "export class RadioGroup" + className
                                                                        + "Component extends GenericRadioGroupComponent<"
                                                                        + className + "> implements OnInit {");
                                        writer.println("  types: " + className + "[] = [] as Array<" + className
                                                        + ">;");
                                        writer.println("");
                                        writer.println("  constructor(");
                                        writer.println(
                                                        "    private formBuild: UntypedFormBuilder, private "
                                                                        + className + "Service: " + className
                                                                        + "Service, private userNoteService2: UserNoteService) {");
                                        writer.println("    super(formBuild, userNoteService2);");
                                        writer.println("  }");
                                        writer.println("");
                                        writer.println("  initTypes(): void {");
                                        writer.println(
                                                        "    this." + className + "Service.get" + className
                                                                        + "().subscribe(response => { this.types = response })");
                                        writer.println("  }");
                                        writer.println("}");
                                        writer.close();
                                } else if (nbr <= 15) {
                                        // select
                                        Files.createDirectories(Paths.get(formFolder + "select-" + formName));

                                        writerComponentName.println("Select" + className + "Component,");

                                        PrintWriter writer = new PrintWriter(
                                                        formFolder + "select-" + formName + "\\" + "select-"
                                                                        + formName + ".component.ts",
                                                        "UTF-8");

                                        writer.println("import { Component, OnInit } from '@angular/core';");
                                        writer.println("import { UntypedFormBuilder } from '@angular/forms';");
                                        writer.println("import { " + className
                                                        + "Service } from 'src/app/modules/miscellaneous/services/guichet-unique/"
                                                        + formName.replaceAll("-", ".") + ".service';");
                                        writer.println("import { UserNoteService } from 'src/app/services/user.notes.service';");
                                        writer.println("import { " + className
                                                        + " } from '../../../../../quotation/model/guichet-unique/referentials/"
                                                        + className + "';");
                                        writer.println("import { GenericSelectComponent } from '../../generic-select/generic-select.component';");
                                        writer.println("");
                                        writer.println("@Component({");
                                        writer.println("  selector: 'select-" + formName + "',");
                                        writer.println("  templateUrl: '../../generic-select/generic-select.component.html',");
                                        writer.println("  styleUrls: ['../../generic-select/generic-select.component.css']");
                                        writer.println("})");
                                        writer.println("export class Select" + className
                                                        + "Component extends GenericSelectComponent<" + className
                                                        + "> implements OnInit {");
                                        writer.println("");
                                        writer.println("  types: " + className + "[] = [] as Array<" + className
                                                        + ">;");
                                        writer.println("");
                                        writer.println("  constructor(private formBuild: UntypedFormBuilder, private "
                                                        + className + "Service: " + className
                                                        + "Service, private userNoteService2: UserNoteService,) {");
                                        writer.println("    super(formBuild, userNoteService2)");
                                        writer.println("  }");
                                        writer.println("");
                                        writer.println("  initTypes(): void {");
                                        writer.println("    this." + className + "Service.get" + className
                                                        + "().subscribe(response => {");
                                        writer.println("      this.types = response;");
                                        writer.println("    })");
                                        writer.println("  }");
                                        writer.println("}");

                                        writer.close();
                                } else {
                                        // autocompletlocal
                                        Files.createDirectories(Paths.get(formFolder + "autocomplete-" + formName));

                                        writerComponentName.println("Autocomplete" + className + "Component,");

                                        PrintWriter writer = new PrintWriter(
                                                        formFolder + "autocomplete-" + formName + "\\" + "autocomplete-"
                                                                        + formName + ".component.ts",
                                                        "UTF-8");

                                        writer.println("import { Component, OnInit } from '@angular/core';");
                                        writer.println("import { UntypedFormBuilder } from '@angular/forms';");
                                        writer.println("import { " + className
                                                        + "Service } from 'src/app/modules/miscellaneous/services/guichet-unique/"
                                                        + formName.replaceAll("-", ".") + ".service';");
                                        writer.println("import { UserNoteService } from 'src/app/services/user.notes.service';");
                                        writer.println("import { " + className
                                                        + " } from '../../../../../quotation/model/guichet-unique/referentials/"
                                                        + className + "';");
                                        writer.println("import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';");
                                        writer.println("");
                                        writer.println("@Component({");
                                        writer.println("  selector: 'autocomplete-" + formName + "',");
                                        writer.println("  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',");
                                        writer.println("  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']");
                                        writer.println("})");
                                        writer.println("export class Autocomplete" + className
                                                        + "Component extends GenericLocalAutocompleteComponent<"
                                                        + className + "> implements OnInit {");
                                        writer.println("");
                                        writer.println("  types: " + className + "[] = [] as Array<" + className
                                                        + ">;");
                                        writer.println("");
                                        writer.println("  constructor(private formBuild: UntypedFormBuilder, private "
                                                        + className + "Service: " + className
                                                        + "Service, private userNoteService2: UserNoteService,) {");
                                        writer.println("    super(formBuild, userNoteService2)");
                                        writer.println("  }");
                                        writer.println("");
                                        writer.println("  filterEntities(types: " + className + "[], value: string): "
                                                        + className + "[] {");
                                        writer.println("    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : \"\";");
                                        writer.println("    return types.filter(item =>");
                                        writer.println("      item && item.label && item.code");
                                        writer.println("      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));");
                                        writer.println("  }");
                                        writer.println("");
                                        writer.println("  initTypes(): void {");
                                        writer.println("    this." + className + "Service.get" + className
                                                        + "().subscribe(response => this.types = response);");
                                        writer.println("  }");
                                        writer.println("");
                                        writer.println("}");

                                        writer.close();
                                }

                                // Service front
                                PrintWriter writer = new PrintWriter(
                                                "C:\\Users\\GAPIN\\Desktop\\workspace-osiris\\client\\src\\app\\modules\\miscellaneous\\services\\guichet-unique\\"
                                                                + (formName.replaceAll("-", ".")) + ".service.ts",
                                                "UTF-8");

                                writer.println("import { HttpClient, HttpParams } from '@angular/common/http';");
                                writer.println("import { Injectable } from '@angular/core';");
                                writer.println(
                                                "import { " + className
                                                                + " } from 'src/app/modules/quotation/model/guichet-unique/referentials/"
                                                                + className
                                                                + "';");
                                writer.println("import { AppRestService } from 'src/app/services/appRest.service';");
                                writer.println("");
                                writer.println("@Injectable({");
                                writer.println("  providedIn: 'root'");
                                writer.println("})");
                                writer.println("export class " + className + "Service extends AppRestService<"
                                                + className + ">{");
                                writer.println("");
                                writer.println("  constructor(http: HttpClient) {");
                                writer.println("    super(http, 'quotation/guichet-unique');");
                                writer.println("  }");
                                writer.println("");
                                writer.println("  get" + className + "() {");
                                writer.println("    return this.getList(new HttpParams(), '" + formName + "');");
                                writer.println("  }");
                                writer.println("");
                                writer.println("}                        ");
                                writer.close();

                                // Repo back
                                writer = new PrintWriter(
                                                "C:\\Users\\GAPIN\\Desktop\\workspace-osiris\\src\\main\\java\\com\\jss\\osiris\\modules\\quotation\\repository\\guichetUnique\\"
                                                                + className + "Repository.java",
                                                "UTF-8");

                                writer.println("package com.jss.osiris.modules.quotation.repository.guichetUnique;");
                                writer.println("");
                                writer.println("import org.springframework.data.repository.CrudRepository;");
                                writer.println("");
                                writer.println(
                                                "import com.jss.osiris.modules.quotation.model.guichetUnique.referentials."
                                                                + className + ";");
                                writer.println("");
                                writer.println(
                                                "public interface " + className + "Repository extends CrudRepository<"
                                                                + className
                                                                + ", String> {");
                                writer.println("}");

                                writer.close();

                                // Service back
                                writer = new PrintWriter(
                                                "C:\\Users\\GAPIN\\Desktop\\workspace-osiris\\src\\main\\java\\com\\jss\\osiris\\modules\\quotation\\service\\guichetUnique\\"
                                                                + className + "Service.java",
                                                "UTF-8");

                                writer.println("package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;");
                                writer.println("");
                                writer.println("import java.util.List;");
                                writer.println("");
                                writer.println(
                                                "import com.jss.osiris.modules.quotation.model.guichetUnique.referentials."
                                                                + className + ";");
                                writer.println("");
                                writer.println("public interface " + className + "Service {");
                                writer.println("    public List<" + className + "> get" + className + "();");
                                writer.println("}");

                                writer.close();

                                writer = new PrintWriter(
                                                "C:\\Users\\GAPIN\\Desktop\\workspace-osiris\\src\\main\\java\\com\\jss\\osiris\\modules\\quotation\\service\\guichetUnique\\"
                                                                + className + "ServiceImpl.java",
                                                "UTF-8");

                                writer.println("package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;");
                                writer.println("");
                                writer.println("import java.util.List;");
                                writer.println("");
                                writer.println("import org.apache.commons.collections4.IterableUtils;");
                                writer.println("import org.springframework.beans.factory.annotation.Autowired;");
                                writer.println("import org.springframework.cache.annotation.Cacheable;");
                                writer.println("import org.springframework.stereotype.Service;");
                                writer.println("");
                                writer.println(
                                                "import com.jss.osiris.modules.quotation.model.guichetUnique.referentials."
                                                                + className + ";");
                                writer.println(
                                                "import com.jss.osiris.modules.quotation.repository.guichetUnique."
                                                                + className
                                                                + "Repository;");
                                writer.println("");
                                writer.println("@Service");
                                writer.println("public class " + className + "ServiceImpl implements " + className
                                                + "Service {");
                                writer.println("");
                                writer.println("    @Autowired");
                                writer.println("    " + className + "Repository " + className + "Repository;");
                                writer.println("");
                                writer.println("    @Override");
                                writer.println(
                                                "    @Cacheable(value = \"" + sheet.getSheetName()
                                                                + "List\", key = \"#root.methodName\")");
                                writer.println("    public List<" + className + "> get" + className + "() {");
                                writer.println("        return IterableUtils.toList(" + className
                                                + "Repository.findAll());");
                                writer.println("    }");
                                writer.println("}");

                                writer.close();

                                // Controller
                                writerController.println("@Autowired");
                                writerController.println(
                                                "" + className + "Service " + sheet.getSheetName() + "Service;");
                                writerController.println("");
                                writerController.println("@GetMapping(inputEntryPoint + \"/" + formName + "\")");
                                writerController.println("public ResponseEntity<List<" + className + ">> get"
                                                + className + "() {");
                                writerController.println(
                                                "  List<" + className + "> " + sheet.getSheetName() + " = null;");
                                writerController.println("  try {");
                                writerController.println("    " + sheet.getSheetName() + " = " + sheet.getSheetName()
                                                + "Service.get" + className + "();");
                                writerController.println("  } catch (HttpStatusCodeException e) {");
                                writerController.println("    logger.error(\"HTTP error when fetching "
                                                + sheet.getSheetName() + "\", e);");
                                writerController.println("    return new ResponseEntity<List<" + className
                                                + ">>(HttpStatus.INTERNAL_SERVER_ERROR);");
                                writerController.println("  } catch (Exception e) {");
                                writerController.println("    logger.error(\"Error when fetching "
                                                + sheet.getSheetName() + "\", e);");
                                writerController.println("    return new ResponseEntity<List<" + className
                                                + ">>(HttpStatus.INTERNAL_SERVER_ERROR);");
                                writerController.println("  }");
                                writerController.println("  return new ResponseEntity<List<" + className + ">>("
                                                + sheet.getSheetName() + ", HttpStatus.OK);");
                                writerController.println("}");

                                // Cache
                                writerCache.println("<cache alias=\"" + sheet.getSheetName()
                                                + "List\" uses-template=\"default\">");
                                writerCache.println("<key-type>java.lang.String</key-type>");
                                writerCache.println("<value-type>java.util.List</value-type>");
                                writerCache.println("</cache>");
                        }
                        writerController.println("}");
                        writerController.close();
                        writerCache.close();
                        writerComponentName.close();
                } catch (Exception e) {
                        e.printStackTrace();
                } finally {
                        if (wb != null)
                                wb.close();
                }
        }

        private ArrayList<File> listFilesForFolder(final File folder) {
                ArrayList<File> files = new ArrayList<File>();
                for (final File fileEntry : folder.listFiles()) {
                        if (!fileEntry.isDirectory()) {
                                files.add(fileEntry);
                        }
                }
                return files;
        }

        // @Scheduled(initialDelay = 500, fixedDelay = 100000)
        public void reworkTs() throws FileNotFoundException, UnsupportedEncodingException {
                XSSFWorkbook wb = null;

                try {
                        String xlsAdress = "C:\\TEMP\\dico.xlsx";
                        wb = new XSSFWorkbook(new FileInputStream(xlsAdress));

                        ArrayList<String> sheetNames = new ArrayList<String>();

                        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                                XSSFSheet sheet = wb.getSheetAt(i);
                                sheetNames.add(sheet.getSheetName());
                        }

                        ArrayList<File> files = listFilesForFolder(new File(
                                        "C:\\Users\\GAPIN\\Desktop\\workspace-osiris\\client\\src\\app\\modules\\quotation\\model\\guichet-unique"));

                        for (File file : files) {
                                List<String> newLines = new ArrayList<>();
                                for (String line : Files.readAllLines(Paths.get(file.getAbsolutePath()),
                                                StandardCharsets.UTF_8)) {
                                        if (line.contains("string;")) {
                                                String[] values = line.split(":");
                                                if (sheetNames.contains(values[0].trim())) {
                                                        newLines.add(values[0] + ":"
                                                                        + StringUtils.capitalize(
                                                                                        values[0].trim())
                                                                        + ";");
                                                } else {
                                                        newLines.add(line);
                                                }
                                        } else {
                                                newLines.add(line);
                                        }
                                }
                                Files.write(Paths.get(file.getAbsolutePath()), newLines,
                                                StandardCharsets.UTF_8);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        // @Scheduled(initialDelay = 500, fixedDelay = 100000)
        public void generateHtml() {
                try {
                        List<String> componentNames = Files.readAllLines(Paths.get("C:\\TEMP\\component_name.txt"));

                        ArrayList<File> files = listFilesForFolder(new File(
                                        "C:\\Users\\GAPIN\\Desktop\\workspace-osiris\\client\\src\\app\\modules\\quotation\\model\\guichet-unique"));

                        for (File file : files) {
                                List<String> newLines = new ArrayList<>();
                                for (String line : Files.readAllLines(Paths.get(file.getAbsolutePath()),
                                                StandardCharsets.UTF_8)) {
                                        if (line.contains(":")) {
                                                String[] values = line.split(":");
                                                String[] types = values[1].split("//");
                                                String type = types[0].trim().replace(";", "");
                                                String field = values[0].trim();
                                                String typeName = file.getName().toLowerCase().replace(".ts", "");
                                                if (type.equals("string")) {
                                                        newLines.add("<table class=\"full-width\" dummyTable><tr dummyTr><td dummyTd><generic-input [(model)]=\""
                                                                        + typeName + "." + field + "\" label=\"" + field
                                                                        + "\" [form]=\"formaliteForm\" propertyName=\""
                                                                        + field
                                                                        + "\" [isMandatory]=\"true\" [isDisabled]=\"!editMode\" [conditionnalRequired]=\"true\" dummyInput dummyComponent> </generic-input></td></tr></table>");
                                                } else if (type.equals("number")) {
                                                        newLines.add("<table class=\"full-width\" dummyTable><tr dummyTr><td dummyTd><generic-input [(model)]=\""
                                                                        + typeName + "." + field + "\" label=\"" + field
                                                                        + "\" [form]=\"formaliteForm\" propertyName=\""
                                                                        + field
                                                                        + "\" [isMandatory]=\"true\" [isDisabled]=\"!editMode\" [conditionnalRequired]=\"true\" type=\"number\" dummyInput dummyComponent> </generic-input></td></tr></table>");
                                                } else if (type.equals("boolean")) {
                                                        newLines.add("<table class=\"full-width\" dummyTable><tr dummyTr><td dummyTd><generic-toggle [(model)]=\""
                                                                        + typeName + "." + field + "\" label=\"" + field
                                                                        + "\" [form]=\"formaliteForm\" propertyName=\""
                                                                        + field
                                                                        + "\" [isDisabled]=\"!editMode\" dummyToggle dummyComponent></generic-toggle></td></tr></table>");
                                                } else if (type.equals("Date")) {
                                                        newLines.add("<table class=\"full-width\" dummyTable><tr dummyTr><td dummyTd><generic-datepicker  [(model)]=\""
                                                                        + typeName + "." + field + "\" label=\"" + field
                                                                        + "\" [form]=\"formaliteForm\" propertyName=\""
                                                                        + field
                                                                        + "\"  [isMandatory]=\"true\" [isDisabled]=\"!editMode\"  dummyDatePicket dummyComponent></generic-datepicker></td></tr></table>");
                                                } else {
                                                        boolean found = false;
                                                        for (String componentName : componentNames) {
                                                                if (componentName.endsWith(type + "Component,")) {
                                                                        found = true;
                                                                        String domName = String.join("-", componentName
                                                                                        .split("(?=\\p{Upper})"))
                                                                                        .toLowerCase()
                                                                                        .replace("-component,", "");
                                                                        newLines.add("<table class=\"full-width\"><tr><td><"
                                                                                        + domName + " [(model)]=\""
                                                                                        + typeName + "." + field
                                                                                        + "\" label=\"" + field
                                                                                        + "\" [form]=\"formaliteForm\" propertyName=\""
                                                                                        + field
                                                                                        + "\"  [isMandatory]=\"true\"   [isDisabled]=\"!editMode\" [conditionnalRequired]=\"true\"  ></"
                                                                                        + domName
                                                                                        + "></td></tr></table>");
                                                                        break;
                                                                }
                                                        }
                                                        if (!found) {
                                                                newLines.add("// : not found for property "
                                                                                + field);
                                                        }
                                                }
                                        }
                                }
                                Files.write(Paths.get("C:\\TEMP\\components\\" + file.getName() + ".html"), newLines,
                                                StandardCharsets.UTF_8);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        // @Scheduled(initialDelay = 500, fixedDelay = 100000)
        public void generatePojo() {
                try {
                        ArrayList<File> files = listFilesForFolder(new File(
                                        "C:\\Users\\GAPIN\\Desktop\\workspace-osiris\\client\\src\\app\\modules\\quotation\\model\\guichet-unique"));

                        for (File file : files) {
                                List<String> newLines = new ArrayList<>();
                                String typeName = file.getName().replace(".ts", "");

                                newLines.add("package com.jss.osiris.modules.quotation.model.guichetUnique;");
                                newLines.add("");
                                newLines.add("import java.io.Serializable;");
                                newLines.add("");
                                newLines.add("import javax.persistence.Column;");
                                newLines.add("import javax.persistence.Entity;");
                                newLines.add("import javax.persistence.GeneratedValue;");
                                newLines.add("import javax.persistence.GenerationType;");
                                newLines.add("import javax.persistence.Id;");
                                newLines.add("import javax.persistence.JoinColumn;");
                                newLines.add("import javax.persistence.ManyToOne;");
                                newLines.add("");
                                newLines.add("import com.jss.osiris.modules.miscellaneous.model.IId;");
                                newLines.add("");
                                newLines.add("@Entity");
                                newLines.add("public class " + typeName + " implements Serializable, IId {");
                                newLines.add("");
                                newLines.add("    @Id");
                                newLines.add("    @GeneratedValue(strategy = GenerationType.AUTO)");
                                newLines.add("    private Integer id;");

                                for (String line : Files.readAllLines(Paths.get(file.getAbsolutePath()),
                                                StandardCharsets.UTF_8)) {
                                        if (line.contains(":")) {
                                                String[] values = line.split(":");
                                                String[] types = values[1].split("//");
                                                String type = types[0].trim().replace(";", "");
                                                String field = values[0].trim();

                                                if (type.equals("string")) {
                                                        newLines.add("@Column( length = 255)    private String "
                                                                        + field + ";");
                                                } else if (type.equals("number")) {
                                                        newLines.add("    private Integer "
                                                                        + field + ";");
                                                } else if (type.equals("boolean")) {
                                                        newLines.add("    private Boolean "
                                                                        + field + ";");
                                                } else if (type.equals("Date")) {
                                                        newLines.add("    private LocalDate "
                                                                        + field + ";");
                                                } else {
                                                        newLines.add("@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)    @JoinColumn(name = \"id_"
                                                                        + String.join("_", type.split("(?=\\p{Upper})"))
                                                                                        .toLowerCase()
                                                                        + "\", nullable=false)    " + type + " " + field
                                                                        + ";");
                                                }
                                        }
                                        newLines.add("  ");
                                }

                                newLines.add("    }");
                                Files.write(Paths.get("C:\\TEMP\\pojo\\" + file.getName().replace(".ts", ".java")),
                                                newLines,
                                                StandardCharsets.UTF_8);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }
}