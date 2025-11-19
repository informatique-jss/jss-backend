package com.jss.osiris.modules.osiris.crm.controller;

import java.time.LocalDate;
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
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValuePayload;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmQueueService;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;

@RestController
public class KpiController {

        private static final String inputEntryPoint = "kpi";

        @Autowired
        private KpiCrmService kpiCrmService;

        @Autowired
        KpiCrmFacade kpiCrmFacade;

        @Autowired
        KpiCrmQueueService kpiCrmQueueService;

        @GetMapping(inputEntryPoint + "/kpis-crm")
        public ResponseEntity<List<KpiCrm>> getCustomerMailByConfrere()
                        throws OsirisValidationException, OsirisException {
                return new ResponseEntity<List<KpiCrm>>(kpiCrmService.getKpiCrms(), HttpStatus.OK);
        }

        @PostMapping(inputEntryPoint + "/kpi-crm/values/tiers")
        public ResponseEntity<KpiCrmJob> getAggregateValuesForTiersList(@RequestParam String kpiCrmKey,
                        @RequestParam LocalDate startDate, @RequestParam LocalDate endDate,
                        @RequestParam Boolean isAllTiers,
                        @RequestBody List<Integer> tiersIds) throws OsirisValidationException {

                if (startDate == null)
                        throw new OsirisValidationException("startDate");

                if (endDate == null)
                        throw new OsirisValidationException("endDate");

                KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
                if (kpiCrm == null)
                        throw new OsirisValidationException("kpiCrm");

                return new ResponseEntity<KpiCrmJob>(
                                kpiCrmQueueService.submitJobForAggregateValuesForTiersList(kpiCrmKey, startDate,
                                                endDate, tiersIds, isAllTiers),
                                HttpStatus.OK);
        }

        @PostMapping(inputEntryPoint + "/kpi-crm/values/tiers/details")
        public ResponseEntity<KpiCrmValuePayload> getKpiCrmValuePayloadAggregatedByTiersAndDate(
                        @RequestParam String kpiCrmKey,
                        @RequestParam LocalDate startDate, @RequestParam LocalDate endDate,
                        @RequestParam Boolean isAllTiers,
                        @RequestBody List<Integer> tiersIds) throws OsirisValidationException {

                if (startDate == null)
                        throw new OsirisValidationException("startDate");

                if (endDate == null)
                        throw new OsirisValidationException("endDate");

                KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
                if (kpiCrm == null)
                        throw new OsirisValidationException("kpiCrm");

                return new ResponseEntity<KpiCrmValuePayload>(
                                kpiCrmFacade.getKpiCrmValuePayloadAggregatedByTiersAndDate(kpiCrmKey, startDate,
                                                endDate, tiersIds, isAllTiers),
                                HttpStatus.OK);
        }

        @GetMapping(inputEntryPoint + "/kpi-crm/values/tiers/result")
        public ResponseEntity<KpiCrmJob> getAggregateValuesForTiersList(@RequestParam String jobId)
                        throws OsirisValidationException {
                return new ResponseEntity<KpiCrmJob>(kpiCrmQueueService.getJobStatus(jobId), HttpStatus.OK);
        }

        @PostMapping(inputEntryPoint + "/kpi-crm/values/responsable")
        public ResponseEntity<KpiCrmJob> getAggregateValuesForResponsableList(@RequestParam String kpiCrmKey,
                        @RequestParam LocalDate startDate, @RequestParam LocalDate endDate,
                        @RequestParam Boolean isAllResponsable,
                        @RequestBody List<Integer> responsableIds) throws OsirisValidationException {

                if (startDate == null)
                        throw new OsirisValidationException("startDate");

                if (endDate == null)
                        throw new OsirisValidationException("endDate");

                KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
                if (kpiCrm == null)
                        throw new OsirisValidationException("kpiCrm");

                return new ResponseEntity<KpiCrmJob>(
                                kpiCrmQueueService.submitJobForAggregateValuesForResponsableList(kpiCrmKey, startDate,
                                                endDate, responsableIds, isAllResponsable),
                                HttpStatus.OK);
        }

        @PostMapping(inputEntryPoint + "/kpi-crm/values/responsable/details")
        public ResponseEntity<KpiCrmValuePayload> getKpiCrmValuePayloadAggregatedByResponsableAndDate(
                        @RequestParam String kpiCrmKey,
                        @RequestParam LocalDate startDate, @RequestParam LocalDate endDate,
                        @RequestParam Boolean isAllResponsable,
                        @RequestBody List<Integer> responsableIds) throws OsirisValidationException {

                if (startDate == null)
                        throw new OsirisValidationException("startDate");

                if (endDate == null)
                        throw new OsirisValidationException("endDate");

                KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
                if (kpiCrm == null)
                        throw new OsirisValidationException("kpiCrm");

                return new ResponseEntity<KpiCrmValuePayload>(
                                kpiCrmFacade.getKpiCrmValuePayloadAggregatedByResponsableAndDate(kpiCrmKey, startDate,
                                                endDate, responsableIds, isAllResponsable),
                                HttpStatus.OK);
        }

        @GetMapping(inputEntryPoint + "/kpi-crm/values/responsable/result")
        public ResponseEntity<KpiCrmJob> getAggregateValuesForResponsableList(@RequestParam String jobId)
                        throws OsirisValidationException {
                return new ResponseEntity<KpiCrmJob>(kpiCrmQueueService.getJobStatus(jobId), HttpStatus.OK);
        }

        /*
         * @GetMapping(inputEntryPoint + "/kpi-widgets")
         * public ResponseEntity<List<KpiWidgetDto>>
         * getKpiWidgetsByPageAndTimescaleForResponsables(
         * 
         * @RequestParam String displayedPageCode,
         * 
         * @RequestParam String timeScale,
         * 
         * @RequestParam List<Integer> responsableIds) throws OsirisValidationException
         * {
         * 
         * if (displayedPageCode == null ||
         * !KpiCrm.POSSIBLE_DISPLAYS.contains(displayedPageCode))
         * throw new OsirisValidationException("displayedPageCode");
         * 
         * List<Responsable> responsables = new ArrayList<Responsable>();
         * if (responsableIds != null) {
         * for (Integer responsableId : responsableIds) {
         * Responsable responsable = responsableService.getResponsable(responsableId);
         * if (responsable == null)
         * throw new OsirisValidationException("responsable");
         * responsables.add(responsable);
         * }
         * } else {
         * throw new OsirisValidationException("responsableIds");
         * }
         * 
         * return new ResponseEntity<List<KpiWidgetDto>>(
         * kpiCrmService.getKpiCrmWidget(displayedPageCode, timeScale, responsableIds),
         * HttpStatus.OK);
         * }
         */
        /*
         * @GetMapping(inputEntryPoint + "/kpi-values")
         * public ResponseEntity<String> getKpiValuesPayloadByKpiCrmForResponsables(
         * 
         * @RequestParam Integer kpiCrmId,
         * 
         * @RequestParam String timeScale,
         * 
         * @RequestParam List<Integer> responsableIds)
         * throws OsirisValidationException, JsonProcessingException {
         * 
         * if (kpiCrmId == null && kpiCrmService.getKpiCrm(kpiCrmId) == null)
         * throw new OsirisValidationException("kpiCrmId");
         * 
         * List<Responsable> responsables = new ArrayList<Responsable>();
         * if (responsableIds != null && responsableIds.size() > 0) {
         * for (Integer responsableId : responsableIds) {
         * Responsable responsable = responsableService.getResponsable(responsableId);
         * if (responsable == null)
         * throw new OsirisValidationException("responsable");
         * responsables.add(responsable);
         * }
         * } else {
         * throw new OsirisValidationException("responsableIds");
         * }
         * 
         * return new ResponseEntity<String>(
         * kpiCrmService.getKpiValues(kpiCrmId, timeScale, responsableIds),
         * HttpStatus.OK);
         * }
         */
}
