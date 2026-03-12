package com.jss.osiris.modules.osiris.quotation.facade;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.jss.osiris.modules.osiris.quotation.dto.GuichetUniqueDepositInfoDto;
import com.jss.osiris.modules.osiris.quotation.model.MissingAttachmentQuery;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.ValidationRequest;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormaliteGuichetUniqueStatus;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormaliteStatusHistoryItem;

@org.springframework.stereotype.Service
public class FormaliteGuichetUniqueDtoHelper {

        public GuichetUniqueDepositInfoDto getCurrentGuInfoDto(FormaliteGuichetUnique formaliteGuichetUnique) {
                GuichetUniqueDepositInfoDto currentGuInfoDto = new GuichetUniqueDepositInfoDto();
                // Deposit date
                currentGuInfoDto.setDepositDate(
                                OffsetDateTime.parse(formaliteGuichetUnique.getCreated()).toLocalDate());

                // Waiting for validation
                if (formaliteGuichetUnique.getValidationsRequests() != null
                                && formaliteGuichetUnique.getValidationsRequests().size() > 0) {
                        LocalDate lastValidationRequestDate = formaliteGuichetUnique
                                        .getValidationsRequests().stream()
                                        .map(val -> OffsetDateTime.parse(val.getCreated()).toLocalDate())
                                        .max(Comparator.naturalOrder()).get();

                        currentGuInfoDto.setWaitingForValidationFromDate(lastValidationRequestDate);

                        String partnerCenterName = "";
                        for (ValidationRequest validationRequest : formaliteGuichetUnique
                                        .getValidationsRequests()) {
                                if (validationRequest.getPartnerCenter() != null && !partnerCenterName
                                                .contains(validationRequest.getPartnerCenter().getName())) {
                                        partnerCenterName = partnerCenterName
                                                        + validationRequest.getPartnerCenter().getName() + ", ";
                                }
                        }
                        currentGuInfoDto.setWaitingForValidationPartnerCenterName(partnerCenterName);
                }

                // If validated
                if (formaliteGuichetUnique.getStatus().getCode()
                                .equals(FormaliteGuichetUniqueStatus.VALIDATED)) {
                        if (formaliteGuichetUnique.getFormaliteStatusHistoryItems() != null) {
                                for (FormaliteStatusHistoryItem formaliteStatusHistoryItem : formaliteGuichetUnique
                                                .getFormaliteStatusHistoryItems()) {
                                        if (FormaliteGuichetUniqueStatus.VALIDATED
                                                        .equals(formaliteStatusHistoryItem.getStatus().getCode())) {
                                                currentGuInfoDto.setValidationDate(OffsetDateTime
                                                                .parse(formaliteStatusHistoryItem.getCreated())
                                                                .toLocalDate());
                                        }
                                }
                        } else {
                                currentGuInfoDto
                                                .setValidationDate(OffsetDateTime
                                                                .parse(formaliteGuichetUnique.getStatusDate())
                                                                .toLocalDate());
                        }
                }
                return currentGuInfoDto;
        }

        public void addMissingAttachmentQueryToGuDepositInfoDtos(Service service,
                        List<GuichetUniqueDepositInfoDto> guichetUniqueDepositInfoDtos) {

                List<MissingAttachmentQuery> missingAttachmentsQueries = service.getMissingAttachmentQueries();
                missingAttachmentsQueries = missingAttachmentsQueries.stream()
                                .sorted((o1, o2) -> o1.getCreatedDateTime().compareTo(o2.getCreatedDateTime()))
                                .toList();

                if (missingAttachmentsQueries != null && !missingAttachmentsQueries.isEmpty())
                        for (GuichetUniqueDepositInfoDto guichetUniqueDepositInfoDto : guichetUniqueDepositInfoDtos) {
                                List<LocalDate> askingMissingDocumentDates = new ArrayList<>();
                                for (MissingAttachmentQuery missingAttachmentQuery : missingAttachmentsQueries)
                                        if (missingAttachmentQuery.getCreatedDateTime().toLocalDate()
                                                        .isAfter(guichetUniqueDepositInfoDto.getDepositDate())
                                                        && missingAttachmentQuery.getCreatedDateTime().toLocalDate()
                                                                        .isBefore(guichetUniqueDepositInfoDto
                                                                                        .getValidationDate() != null
                                                                                                        ? guichetUniqueDepositInfoDto
                                                                                                                        .getValidationDate()
                                                                                                        : LocalDate.MAX))
                                                askingMissingDocumentDates.add(missingAttachmentQuery
                                                                .getCreatedDateTime().toLocalDate());

                                guichetUniqueDepositInfoDto.setAskingMissingDocumentDates(askingMissingDocumentDates);
                        }
        }

}
