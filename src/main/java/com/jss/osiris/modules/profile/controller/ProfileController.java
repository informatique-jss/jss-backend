package com.jss.osiris.modules.profile.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.model.Team;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.profile.service.TeamService;

@RestController
public class ProfileController {

	private static final String inputEntryPoint = "/profile";

	private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

	@Autowired
	EmployeeService employeeService;

	@Autowired
	ValidationHelper validationHelper;

	@Autowired
	TeamService teamService;

	@GetMapping("/test")
	public void test() throws IOException, DocumentException {

		String folder = "C:\\TEMP\\pdf\\";
		File f = new File(folder);
		String[] pathnames = f.list();

		for (String path : pathnames) {

			splitPage1(folder + path, path);
			splitPage2(folder + path, path);

			String[] inputs = { "C:\\TEMP\\page1.pdf" + path, "C:\\TEMP\\page2.pdf" + path };

			for (int page = 2; page < 25; page++) {

				PdfCopyFields pcf = null;
				if (page <= 9)
					pcf = new PdfCopyFields(
							new FileOutputStream(folder + path.substring(0, path.length() - 6) + "0" + page + ".pdf"));
				if (page > 9 && page <= 19)
					pcf = new PdfCopyFields(
							new FileOutputStream(folder + path.substring(0, path.length() - 6) + page + ".pdf"));
				if (page > 19)
					pcf = new PdfCopyFields(
							new FileOutputStream(folder + path.substring(0, path.length() - 6) + page + ".pdf"));

				PdfReader reader = new PdfReader("C:\\TEMP\\page1.pdf" + path);
				pcf.addDocument(reader);

				for (int i = 2; i <= page; i++) {
					reader = new PdfReader("C:\\TEMP\\page2.pdf" + path);
					Set<String> keys = new HashSet<String>(reader.getAcroFields()
							.getFields().keySet());

					for (String key : keys) {
						try {
							Integer.parseInt(key.substring(1, 2));
							if (i <= 9) {
								reader.getAcroFields().renameField(key, "0" + i + key.substring(2));
							} else {
								reader.getAcroFields().renameField(key, i + key.substring(2));
							}
						} catch (Exception e) {

						}
					}
					pcf.addDocument(reader);

				}

				pcf.close();
			}
		}
	}

	private void splitPage1(String inputPath, String suffix) throws IOException, DocumentException {

		String path;
		PdfStamper stamper;
		PdfReader reader = new PdfReader(inputPath);

		reader.selectPages(String.valueOf(1));
		path = "C:\\TEMP\\page1.pdf" + suffix;
		stamper = new PdfStamper(reader, new FileOutputStream(path));
		stamper.close();

		reader.close();
	}

	private void splitPage2(String inputPath, String suffix) throws IOException, DocumentException {
		String path;
		PdfStamper stamper;
		PdfReader reader = new PdfReader(inputPath);
		reader.selectPages(String.valueOf(2));
		path = "C:\\TEMP\\page2.pdf" + suffix;
		stamper = new PdfStamper(reader, new FileOutputStream(path));
		stamper.close();
		reader.close();

	}

	@GetMapping(inputEntryPoint + "/teams")
	public ResponseEntity<List<Team>> getTeams() {
		List<Team> teams = null;
		try {
			teams = teamService.getTeams();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching team", e);
			return new ResponseEntity<List<Team>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching team", e);
			return new ResponseEntity<List<Team>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Team>>(teams, HttpStatus.OK);
	}

	@PostMapping(inputEntryPoint + "/team")
	public ResponseEntity<Team> addOrUpdateTeam(
			@RequestBody Team teams) {
		Team outTeam;
		try {
			if (teams.getId() != null)
				validationHelper.validateReferential(teams, true);
			validationHelper.validateString(teams.getCode(), true, 20);
			validationHelper.validateString(teams.getLabel(), true, 100);
			validationHelper.validateReferential(teams.getManager(), false);

			outTeam = teamService
					.addOrUpdateTeam(teams);
		} catch (

		ResponseStatusException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching team", e);
			return new ResponseEntity<Team>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching team", e);
			return new ResponseEntity<Team>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Team>(outTeam, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/employee")
	public ResponseEntity<Employee> getEmployeeById(@RequestParam Integer id) {
		Employee employee = null;
		try {
			employee = employeeService.getEmployee(id);
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<Employee>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<Employee>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Employee>(employee, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/employee/sales")
	public ResponseEntity<List<Employee>> getSalesEmployees() {
		List<Employee> salesEmployees = null;
		try {
			salesEmployees = employeeService.getSalesEmployees();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Employee>>(salesEmployees, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/employee/all")
	public ResponseEntity<List<Employee>> getEmployees() {
		List<Employee> salesEmployees = null;
		try {
			salesEmployees = employeeService.getEmployees();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Employee>>(salesEmployees, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/employee/formalistes")
	public ResponseEntity<List<Employee>> getFormalisteEmployees() {
		List<Employee> formalisteEmployees = null;
		try {
			formalisteEmployees = employeeService.getFormalisteEmployees();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Employee>>(formalisteEmployees, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/employee/insertions")
	public ResponseEntity<List<Employee>> getInsertionEmployees() {
		List<Employee> insertionEmployees = null;
		try {
			insertionEmployees = employeeService.getInsertionEmployees();
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching client types", e);
			return new ResponseEntity<List<Employee>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Employee>>(insertionEmployees, HttpStatus.OK);
	}

}
