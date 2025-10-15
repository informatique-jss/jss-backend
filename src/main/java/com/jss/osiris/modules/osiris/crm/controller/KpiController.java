package com.jss.osiris.modules.osiris.crm.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.crm.dto.KpiWidgetDto;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

@RestController
public class KpiController {

        private static final String inputEntryPoint = "kpi";

        @Autowired
        private KpiCrmService kpiCrmService;

        @Autowired
        private ResponsableService responsableService;

        @GetMapping(inputEntryPoint + "/kpi-widgets")
        public ResponseEntity<List<KpiWidgetDto>> getKpiWidgetsByPageAndTimescaleForResponsables(
                        @RequestParam String displayedPageCode,
                        @RequestParam String timeScale,
                        @RequestParam List<Integer> responsableIds) throws OsirisValidationException {

                if (displayedPageCode == null || !KpiCrm.POSSIBLE_DISPLAYS.contains(displayedPageCode))
                        throw new OsirisValidationException("displayedPageCode");

                if (timeScale == null || (!KpiCrm.WEEKLY_PERIOD.equals(timeScale)
                                && !KpiCrm.MONTHLY_PERIOD.equals(timeScale)
                                && !KpiCrm.ANNUALLY_PERIOD.equals(timeScale)))
                        throw new OsirisValidationException("timescale");

                List<Responsable> responsables = new ArrayList<Responsable>();
                if (responsableIds != null) {
                        for (Integer responsableId : responsableIds) {
                                Responsable responsable = responsableService.getResponsable(responsableId);
                                if (responsable == null)
                                        throw new OsirisValidationException("responsable");
                                responsables.add(responsable);
                        }
                } else {
                        throw new OsirisValidationException("responsableIds");
                }

                return new ResponseEntity<List<KpiWidgetDto>>(
                                kpiCrmService.getKpiCrmWidget(displayedPageCode, timeScale, responsableIds),
                                HttpStatus.OK);
        }

        @GetMapping(inputEntryPoint + "/kpi-values")
        public ResponseEntity<String> getKpiValuesPayloadByKpiCrmForResponsables(
                        @RequestParam Integer kpiCrmId,
                        @RequestParam String timeScale,
                        @RequestParam List<Integer> responsableIds)
                        throws OsirisValidationException, JsonProcessingException {

                if (kpiCrmId == null && kpiCrmService.getKpiCrmById(kpiCrmId) == null)
                        throw new OsirisValidationException("kpiCrmId");

                if (timeScale == null || (!KpiCrm.WEEKLY_PERIOD.equals(timeScale)
                                && !KpiCrm.MONTHLY_PERIOD.equals(timeScale)
                                && !KpiCrm.ANNUALLY_PERIOD.equals(timeScale)))
                        throw new OsirisValidationException("timescale");

                List<Responsable> responsables = new ArrayList<Responsable>();
                if (responsableIds != null && responsableIds.size() > 0) {
                        for (Integer responsableId : responsableIds) {
                                Responsable responsable = responsableService.getResponsable(responsableId);
                                if (responsable == null)
                                        throw new OsirisValidationException("responsable");
                                responsables.add(responsable);
                        }
                } else {
                        throw new OsirisValidationException("responsableIds");
                }

                return new ResponseEntity<String>(
                                kpiCrmService.getKpiValues(kpiCrmId, timeScale, responsableIds),
                                HttpStatus.OK);
        }
}
