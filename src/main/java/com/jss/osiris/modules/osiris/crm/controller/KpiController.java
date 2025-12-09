package com.jss.osiris.modules.osiris.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.crm.facade.KpiCrmFacade;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmJob;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmSearchModel;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValuePayload;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmQueueService;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;

@RestController
public class KpiController {

        private static final String inputEntryPoint = "kpi";

        @Autowired
        private KpiCrmService kpiCrmService;

        @Autowired
        KpiCrmFacade kpiCrmFacade;

        @Autowired
        KpiCrmQueueService kpiCrmQueueService;

        @Autowired
        EmployeeService employeeService;

        @GetMapping(inputEntryPoint + "/kpis-crm")
        public ResponseEntity<List<KpiCrm>> getAllKpisCrm()
                        throws OsirisValidationException, OsirisException {
                return new ResponseEntity<List<KpiCrm>>(kpiCrmService.getKpiCrms(), HttpStatus.OK);
        }

        @PostMapping(inputEntryPoint + "/kpi-crm/values/tiers")
        public ResponseEntity<KpiCrmJob> getAggregateValuesForTiersList(@RequestParam String kpiCrmKey,
                        @RequestBody KpiCrmSearchModel searchModel) throws OsirisValidationException {

                if (searchModel.getStartDateKpis() == null)
                        throw new OsirisValidationException("startDate");

                if (searchModel.getEndDateKpis() == null)
                        throw new OsirisValidationException("endDate");

                if (searchModel.getSalesEmployeeId() != null) {
                        Employee employee = employeeService.getEmployee(searchModel.getSalesEmployeeId());
                        if (employee == null)
                                throw new OsirisValidationException("salesEmployeeId");
                }

                KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
                if (kpiCrm == null)
                        throw new OsirisValidationException("kpiCrm");

                return new ResponseEntity<KpiCrmJob>(
                                kpiCrmQueueService.submitJobForAggregateValuesForTiersList(kpiCrmKey, searchModel),
                                HttpStatus.OK);
        }

        @PostMapping(inputEntryPoint + "/kpi-crm/values/tiers/details")
        public ResponseEntity<KpiCrmValuePayload> getKpiCrmValuePayloadAggregatedByTiersAndDate(
                        @RequestParam String kpiCrmKey,
                        @RequestBody KpiCrmSearchModel searchModel) throws OsirisValidationException {

                if (searchModel.getStartDateKpis() == null)
                        throw new OsirisValidationException("startDate");

                if (searchModel.getEndDateKpis() == null)
                        throw new OsirisValidationException("endDate");

                if (searchModel.getSalesEmployeeId() != null) {
                        Employee employee = employeeService.getEmployee(searchModel.getSalesEmployeeId());
                        if (employee == null)
                                throw new OsirisValidationException("salesEmployeeId");
                }

                KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
                if (kpiCrm == null)
                        throw new OsirisValidationException("kpiCrm");

                return new ResponseEntity<KpiCrmValuePayload>(
                                kpiCrmFacade.getKpiCrmValuePayloadAggregatedByTiersAndDate(kpiCrmKey, searchModel,
                                                true),
                                HttpStatus.OK);
        }

        @GetMapping(inputEntryPoint + "/kpi-crm/values/tiers/result")
        public ResponseEntity<KpiCrmJob> getAggregateValuesForTiersList(@RequestParam String jobId)
                        throws OsirisValidationException {
                return new ResponseEntity<KpiCrmJob>(kpiCrmQueueService.getJobStatus(jobId), HttpStatus.OK);
        }

        @PostMapping(inputEntryPoint + "/kpi-crm/values/responsable")
        public ResponseEntity<KpiCrmJob> getAggregateValuesForResponsableList(@RequestParam String kpiCrmKey,
                        @RequestBody KpiCrmSearchModel searchModel) throws OsirisValidationException {

                if (searchModel.getStartDateKpis() == null)
                        throw new OsirisValidationException("startDate");

                if (searchModel.getEndDateKpis() == null)
                        throw new OsirisValidationException("endDate");

                if (searchModel.getSalesEmployeeId() != null) {
                        Employee employee = employeeService.getEmployee(searchModel.getSalesEmployeeId());
                        if (employee == null)
                                throw new OsirisValidationException("salesEmployeeId");
                }

                KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
                if (kpiCrm == null)
                        throw new OsirisValidationException("kpiCrm");

                return new ResponseEntity<KpiCrmJob>(
                                kpiCrmQueueService.submitJobForAggregateValuesForResponsableList(kpiCrmKey,
                                                searchModel),
                                HttpStatus.OK);
        }

        @PostMapping(inputEntryPoint + "/kpi-crm/values/responsable/details")
        public ResponseEntity<KpiCrmValuePayload> getKpiCrmValuePayloadAggregatedByResponsableAndDate(
                        @RequestParam String kpiCrmKey,
                        @RequestBody KpiCrmSearchModel searchModel) throws OsirisValidationException {

                if (searchModel.getStartDateKpis() == null)
                        throw new OsirisValidationException("startDate");

                if (searchModel.getEndDateKpis() == null)
                        throw new OsirisValidationException("endDate");

                if (searchModel.getSalesEmployeeId() != null) {
                        Employee employee = employeeService.getEmployee(searchModel.getSalesEmployeeId());
                        if (employee == null)
                                throw new OsirisValidationException("salesEmployeeId");
                }

                KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
                if (kpiCrm == null)
                        throw new OsirisValidationException("kpiCrm");

                return new ResponseEntity<KpiCrmValuePayload>(
                                kpiCrmFacade.getKpiCrmValuePayloadAggregatedByResponsableAndDate(kpiCrmKey, searchModel,
                                                true),
                                HttpStatus.OK);
        }

        @GetMapping(inputEntryPoint + "/kpi-crm/values/responsable/result")
        public ResponseEntity<KpiCrmJob> getAggregateValuesForResponsableList(@RequestParam String jobId)
                        throws OsirisValidationException {
                return new ResponseEntity<KpiCrmJob>(kpiCrmQueueService.getJobStatus(jobId), HttpStatus.OK);
        }
}
