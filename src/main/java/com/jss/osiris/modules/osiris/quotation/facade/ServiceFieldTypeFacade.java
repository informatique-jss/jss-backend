package com.jss.osiris.modules.osiris.quotation.facade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.dto.ServiceFieldTypeDto;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFieldType;
import com.jss.osiris.modules.osiris.quotation.service.RneDelegateService;
import com.jss.osiris.modules.osiris.quotation.service.ServiceFieldTypeService;

@Service
public class ServiceFieldTypeFacade {

    @Autowired
    RneDelegateService rneDelegateService;

    @Autowired
    ServiceFieldTypeService serviceFieldTypeService;

    @Transactional
    public List<ServiceFieldTypeDto> getServiceFieldTypesDtos(Affaire affaire) throws OsirisClientMessageException,
            OsirisException, URISyntaxException, IOException, InterruptedException {
        List<ServiceFieldTypeDto> serviceFieldTypeDtos = new ArrayList<>();

        if (affaire == null) {
            List<ServiceFieldType> serviceFieldTypes = serviceFieldTypeService.getServiceFieldTypes();
            for (ServiceFieldType serviceFieldType : serviceFieldTypes)
                serviceFieldTypeDtos.add(mapServiceFieldType(serviceFieldType, null));

        } else {
            serviceFieldTypeDtos = getServiceFieldTypesDtosForAffaire(affaire);
        }
        return serviceFieldTypeDtos;
    }

    private List<ServiceFieldTypeDto> getServiceFieldTypesDtosForAffaire(Affaire affaire)
            throws OsirisClientMessageException, OsirisException, URISyntaxException, IOException,
            InterruptedException {

        List<ServiceFieldTypeDto> serviceFieldTypeDtos = new ArrayList<>();

        // Getting raw JSON from RNE API
        String rawRneJson = rneDelegateService.getRawJsonBySiret(affaire.getSiret());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(rawRneJson);

        // Getting all ServiceFieldType
        List<ServiceFieldType> serviceFieldTypes = serviceFieldTypeService.getServiceFieldTypes();

        // Building each Dto and adding it to the returned list
        for (ServiceFieldType serviceFieldType : serviceFieldTypes) {
            if (serviceFieldType.getJsonPathToRneValue() != null
                    && !serviceFieldType.getJsonPathToRneValue().isEmpty()) {
                String value = null;
                //
                List<String> possiblePaths = Arrays.asList(serviceFieldType.getJsonPathToRneValue().split("\\|\\|"));
                for (String path : possiblePaths) {
                    JsonNode valueNode = root.at(path.trim());
                    if (!valueNode.isMissingNode() && !valueNode.asText().isEmpty()) {
                        value = valueNode.asText();
                        break;
                    }
                }
                serviceFieldTypeDtos.add(mapServiceFieldType(serviceFieldType, value));
            }
        }
        return serviceFieldTypeDtos;
    }

    ServiceFieldTypeDto mapServiceFieldType(ServiceFieldType serviceFieldType, String value) {
        ServiceFieldTypeDto serviceFieldTypeDto = new ServiceFieldTypeDto();

        serviceFieldTypeDto.setId(serviceFieldType.getId());
        serviceFieldTypeDto.setCode(serviceFieldType.getCode());
        serviceFieldTypeDto.setDataType(serviceFieldType.getDataType());
        serviceFieldTypeDto.setLabel(serviceFieldType.getLabel());
        serviceFieldTypeDto.setServiceFieldTypePossibleValues(serviceFieldType.getServiceFieldTypePossibleValues());
        serviceFieldTypeDto.setValue(value);

        return serviceFieldTypeDto;
    }
}
