package com.jss.osiris.libs.search.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.repository.IndexEntityRepository;
import com.jss.osiris.modules.myjss.profile.service.UserScopeService;
import com.jss.osiris.modules.myjss.quotation.controller.MyJssQuotationValidationHelper;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.service.AffaireService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

@Service
public class SearchServiceImpl implements SearchService {

    @Value("${search.results.max}")
    private Integer maxNumberOfResults;
    @Autowired
    IndexEntityRepository indexEntityRepository;

    @Autowired
    MyJssQuotationValidationHelper myJssQuotationValidationHelper;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    AffaireService affaireService;

    @Autowired
    UserScopeService userScopeService;

    @Override
    public List<IndexEntity> searchForEntities(String search) {
        List<IndexEntity> entities = null;
        try {
            entities = indexEntityRepository.searchForEntitiesById(Integer.parseInt(search.trim()));
        } catch (Exception e) {
        }
        if (entities == null || entities.size() == 0)
            entities = indexEntityRepository.searchForEntities(search, maxNumberOfResults);
        if (entities == null || entities.size() == 0)
            entities = indexEntityRepository.searchForContainsSimilarEntities(search, maxNumberOfResults);
        return entities;
    }

    @Override
    public List<IndexEntity> searchForEntities(String search, String entityType, boolean onlyExactMatch) {
        List<IndexEntity> entities = null;
        try {
            entities = indexEntityRepository.searchForEntitiesByIdAndEntityType(Integer.parseInt(search.trim()),
                    Arrays.asList(entityType));
        } catch (Exception e) {
        }
        if (entities == null || entities.size() == 0)
            entities = indexEntityRepository.searchForEntities(search, entityType, maxNumberOfResults);
        if (entities == null || entities.size() == 0 && !onlyExactMatch)
            entities = indexEntityRepository.searchForContainsSimilarEntities(search, entityType, maxNumberOfResults);
        return entities;
    }

    @Override
    public List<IndexEntity> searchForEntitiesById(Integer id, List<String> entityTypeToSearch) {
        return indexEntityRepository.searchForEntitiesByIdAndEntityType(id, entityTypeToSearch);
    }

    @Override
    public List<IndexEntity> getActifResponsableByKeyword(String searchedValue, Boolean onlyActive) {
        List<IndexEntity> responsables = searchForEntities(searchedValue, Responsable.class.getSimpleName(), false);
        if (!onlyActive)
            return responsables;

        List<IndexEntity> outResponsables = new ArrayList<IndexEntity>();
        if (responsables != null && responsables.size() > 0)
            for (IndexEntity entity : responsables)
                if (entity.getText() != null && entity.getText().contains("\"isActive\":true"))
                    outResponsables.add(entity);
        return outResponsables;
    }

    @Override
    public List<IndexEntity> getCustomerOrdersByKeyword(String searchedValue) {
        return searchForEntities(searchedValue, CustomerOrder.class.getSimpleName(), false);
    }

    @Override
    public List<IndexEntity> getPostByKeyword(String searchedValue) {
        return searchForEntities(searchedValue, Post.class.getSimpleName(), false);
    }

    @Override
    public List<IndexEntity> getIndividualTiersByKeyword(String searchedValue) {
        List<IndexEntity> tiers = searchForEntities(searchedValue, Tiers.class.getSimpleName(), false);
        List<IndexEntity> outTiers = new ArrayList<IndexEntity>();

        if (tiers != null && tiers.size() > 0)
            for (IndexEntity entity : tiers) {
                if (entity.getText() != null && !entity.getText().contains("\"denomination\""))
                    outTiers.add(entity);
            }
        return outTiers;
    }

    @Override
    public List<IndexEntity> searchEntitiesForCustomer(String searchString) {
        List<IndexEntity> entities = new ArrayList<IndexEntity>();
        List<IndexEntity> authorizedEntities = new ArrayList<IndexEntity>();
        List<Responsable> userResponsableScope = userScopeService.getUserCurrentScopeResponsables();
        try {
            entities = indexEntityRepository.searchForEntitiesByIdAndEntityType(Integer.parseInt(searchString.trim()),
                    Arrays.asList(Invoice.class.getSimpleName(), CustomerOrder.class.getSimpleName(),
                            Quotation.class.getSimpleName(), Affaire.class.getSimpleName()));
        } catch (Exception e) {
        }

        // Add plain text affaire search
        List<IndexEntity> affaireEntities = null;
        if (entities.size() == 0)
            affaireEntities = searchForEntities(searchString, Affaire.class.getSimpleName(), false);
        if (affaireEntities != null && affaireEntities.size() > 0)
            entities.addAll(affaireEntities);

        if (entities != null && entities.size() > 0) {
            for (IndexEntity indexEntity : entities) {
                if (indexEntity.getEntityType().equals(CustomerOrder.class.getSimpleName())) {
                    if (myJssQuotationValidationHelper
                            .canSeeQuotation(customerOrderService.getCustomerOrder(indexEntity.getEntityId())))
                        authorizedEntities.add(indexEntity);
                }
                if (indexEntity.getEntityType().equals(Quotation.class.getSimpleName())) {
                    if (myJssQuotationValidationHelper
                            .canSeeQuotation(quotationService.getQuotation(indexEntity.getEntityId())))
                        authorizedEntities.add(indexEntity);
                }
                if (indexEntity.getEntityType().equals(Invoice.class.getSimpleName())) {
                    Invoice invoice = invoiceService.getInvoice(indexEntity.getEntityId());
                    if (invoice != null && invoice.getResponsable() != null && invoice.getCustomerOrder() != null) {
                        for (Responsable responsable : userResponsableScope) {
                            if (responsable.getId().equals(invoice.getResponsable().getId()))
                                authorizedEntities.add(indexEntity);
                        }
                    }
                }
                if (indexEntity.getEntityType().equals(Affaire.class.getSimpleName())) {
                    List<CustomerOrder> customerOrders = customerOrderService
                            .searchOrdersForCurrentUserAndAffaire(affaireService.getAffaire(indexEntity.getEntityId()));
                    if (customerOrders != null && customerOrders.size() > 0)
                        authorizedEntities.add(indexEntity);
                }
            }
        }

        return entities;
    }
}
