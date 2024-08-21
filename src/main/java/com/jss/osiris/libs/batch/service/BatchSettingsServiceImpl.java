package com.jss.osiris.libs.batch.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.model.BatchCategory;
import com.jss.osiris.libs.batch.model.BatchSettings;
import com.jss.osiris.libs.batch.repository.BatchSettingsRepository;
import com.jss.osiris.libs.exception.OsirisException;

@Service
public class BatchSettingsServiceImpl implements BatchSettingsService {

    @Autowired
    BatchSettingsRepository batchSettingsRepository;

    @Autowired
    BatchStatusService batchStatusService;

    @Autowired
    BatchCategoryService batchCategoryService;

    @Override
    public BatchSettings getByCode(String code) {
        return batchSettingsRepository.findByCode(code);
    }

    @Override
    public BatchSettings getBatchSettings(Integer id) {
        Optional<BatchSettings> batchSettings = batchSettingsRepository.findById(id);
        if (batchSettings.isPresent())
            return batchSettings.get();
        return null;
    }

    @Override
    public BatchSettings addOrUpdateBatchSettings(BatchSettings batchSettings) {
        return batchSettingsRepository.save(batchSettings);
    }

    @Override
    public List<BatchSettings> getAllBatchSettings() {
        return IterableUtils.toList(batchSettingsRepository.findAll());
    }

    @Override
    public void initializeBatchSettings() throws OsirisException {
        batchStatusService.updateStatusReferential();
        batchCategoryService.updateCategoryReferential();

        if (getByCode(Batch.REFRESH_FORMALITE_GUICHET_UNIQUE) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.REFRESH_FORMALITE_GUICHET_UNIQUE);
            batchSettings.setLabel("Mise à jour des dossiers GU ouverts");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.GUICHET_UNIQUE));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.PAY_FORMALITE_GUICHET_UNIQUE) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.PAY_FORMALITE_GUICHET_UNIQUE);
            batchSettings.setLabel("Paiement des dossiers GU");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.GUICHET_UNIQUE));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.DECLARE_NEW_ACTE_DEPOSIT_ON_GUICHET_UNIQUE) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.DECLARE_NEW_ACTE_DEPOSIT_ON_GUICHET_UNIQUE);
            batchSettings.setLabel("Déclaration des dépôts d'actes sur le GU");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.GUICHET_UNIQUE));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.DAILY_ACCOUNT_CLOSING) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.DAILY_ACCOUNT_CLOSING);
            batchSettings.setLabel("Clôture comptable journalière");
            batchSettings.setFixedRate(5 * 60 * 1000);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.ACCOUNTING));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.ACTIVE_DIRECTORY_USER_UPDATE) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.ACTIVE_DIRECTORY_USER_UPDATE);
            batchSettings.setLabel("Mise à jour des utilisateurs depuis l'AD");
            batchSettings.setFixedRate(60 * 1000);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.MISCELLANEOUS));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.SEND_MAIL) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.SEND_MAIL);
            batchSettings.setLabel("Envoi des mails");
            batchSettings.setFixedRate(2 * 1000);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(1);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.MAILS));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.PURGE_NOTIFICATION) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.PURGE_NOTIFICATION);
            batchSettings.setLabel("Purge des notifications");
            batchSettings.setFixedRate(5 * 60 * 1000);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.SYSTEM));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.PURGE_LOGS) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.PURGE_LOGS);
            batchSettings.setLabel("Purge des logs");
            batchSettings.setFixedRate(5 * 60 * 1000);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.SYSTEM));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.PURGE_BATCH) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.PURGE_LOGS);
            batchSettings.setLabel("Purge des batchs");
            batchSettings.setFixedRate(5 * 60 * 1000);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.SYSTEM));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.CLEAN_AUDIT) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.CLEAN_AUDIT);
            batchSettings.setLabel("Nettoyage de l'audit");
            batchSettings.setFixedRate(5 * 60 * 1000);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.SYSTEM));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.CHECK_CENTRAL_PAY_PAYMENT_REQUEST) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.CHECK_CENTRAL_PAY_PAYMENT_REQUEST);
            batchSettings.setLabel("Récupération des paiements CentralPay");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(3);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.ACCOUNTING));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.SEND_REMINDER_FOR_QUOTATION) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.SEND_REMINDER_FOR_QUOTATION);
            batchSettings.setLabel("Relance par mail des devis");
            batchSettings.setFixedRate(60 * 1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.REMINDERS));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.SEND_REMINDER_FOR_CUSTOMER_ORDER_DEPOSITS) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.SEND_REMINDER_FOR_CUSTOMER_ORDER_DEPOSITS);
            batchSettings.setLabel("Relance par mail des acomptes de commande");
            batchSettings.setFixedRate(60 * 1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.REMINDERS));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.SEND_REMINDER_FOR_INVOICES) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.SEND_REMINDER_FOR_INVOICES);
            batchSettings.setLabel("Relance par mail des factures non payées");
            batchSettings.setFixedRate(60 * 1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.REMINDERS));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.SEND_REMINDER_TO_CONFRERE_FOR_ANNOUNCEMENTS) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.SEND_REMINDER_TO_CONFRERE_FOR_ANNOUNCEMENTS);
            batchSettings.setLabel("Relance par mail des confrère pour la publication de AL");
            batchSettings.setFixedRate(60 * 1000);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.REMINDERS));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.SEND_REMINDER_TO_CONFRERE_FOR_PROVIDER_INVOICE) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.SEND_REMINDER_TO_CONFRERE_FOR_PROVIDER_INVOICE);
            batchSettings.setLabel("Relance par mail des confrère pour la réception des factures fournisseurs");
            batchSettings.setFixedRate(60 * 1000);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.REMINDERS));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.SEND_REMINDER_TO_CUSTOMER_FOR_PROOF_READING) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.SEND_REMINDER_TO_CUSTOMER_FOR_PROOF_READING);
            batchSettings.setLabel("Relance par mail des clients pour la validation des BAT");
            batchSettings.setFixedRate(60 * 1000);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.REMINDERS));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.SEND_REMINDER_TO_CUSTOMER_FOR_BILAN_PUBLICATION) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.SEND_REMINDER_TO_CUSTOMER_FOR_BILAN_PUBLICATION);
            batchSettings.setLabel("Relance par mail des clients pour la publication des bilans annuels");
            batchSettings.setFixedRate(60 * 1000);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.REMINDERS));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.SEND_REMINDER_TO_CUSTOMER_FOR_MISSING_ATTACHMENT_QUERIES) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.SEND_REMINDER_TO_CUSTOMER_FOR_MISSING_ATTACHMENT_QUERIES);
            batchSettings.setLabel("Relance par mail des clients pour les pièces manquantes");
            batchSettings.setFixedRate(60 * 1000);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.REMINDERS));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.PUBLISH_ANNOUNCEMENT_TO_ACTU_LEGALE) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.PUBLISH_ANNOUNCEMENT_TO_ACTU_LEGALE);
            batchSettings.setLabel("Publication des annonces sur ActuLegale");
            batchSettings.setFixedRate(60 * 1000);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.MISCELLANEOUS));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.SEND_PUBLICATION_FLAG) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.SEND_PUBLICATION_FLAG);
            batchSettings.setLabel("Envoi des témoins de publication");
            batchSettings.setFixedRate(60 * 1000);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.MAILS));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.UPDATE_COMPETENT_AUTHORITY) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.UPDATE_COMPETENT_AUTHORITY);
            batchSettings.setLabel("Mise à jour des autorité compétentes");
            batchSettings.setFixedRate(60 * 1000);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.REFERENTIALS));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.DO_OCR_ON_INVOICE) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.DO_OCR_ON_INVOICE);
            batchSettings.setLabel("OCR des factures");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(1);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.ACCOUNTING));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.DO_OCR_ON_RECEIPT) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.DO_OCR_ON_RECEIPT);
            batchSettings.setLabel("OCR des relevés de comptes");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(1);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.ACCOUNTING));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.UPDATE_AFFAIRE_FROM_RNE) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.UPDATE_AFFAIRE_FROM_RNE);
            batchSettings.setLabel("Mise à jour des affaires depuis le RNE");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(4);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.REFERENTIALS));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.AUTOMATCH_PAYMENT) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.AUTOMATCH_PAYMENT);
            batchSettings.setLabel("Matching automatique de paiements");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(10);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.ACCOUNTING));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.SEND_BILLING_CLOSURE_RECEIPT) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.SEND_BILLING_CLOSURE_RECEIPT);
            batchSettings.setLabel("Envoi des relevés de compte aux clients");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.MAILS));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.REINDEX_DIRECT_DEBIT_BANK_TRANSFERT) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.REINDEX_DIRECT_DEBIT_BANK_TRANSFERT);
            batchSettings.setLabel("Indexation - Prélèvements");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.INDEXATION));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.REINDEX_INVOICE) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.REINDEX_INVOICE);
            batchSettings.setLabel("Indexation - Factures");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.INDEXATION));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.REINDEX_PAYMENT) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.REINDEX_PAYMENT);
            batchSettings.setLabel("Indexation - Paiments");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.INDEXATION));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.REINDEX_REFUND) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.REINDEX_REFUND);
            batchSettings.setLabel("Indexation - Remboursements");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.INDEXATION));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.REINDEX_AFFAIRE) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.REINDEX_AFFAIRE);
            batchSettings.setLabel("Indexation - Affaires");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setIsActive(true);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.INDEXATION));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.REINDEX_ASSO_AFFAIRE_ORDER) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.REINDEX_ASSO_AFFAIRE_ORDER);
            batchSettings.setLabel("Indexation - Associations Affaire/Prestations");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.INDEXATION));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.REINDEX_BANK_TRANSFERT) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.REINDEX_BANK_TRANSFERT);
            batchSettings.setLabel("Indexation - Virements");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.INDEXATION));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.REINDEX_CUSTOMER_ORDER) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.REINDEX_CUSTOMER_ORDER);
            batchSettings.setLabel("Indexation - Commandes");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.INDEXATION));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.REINDEX_QUOTATION) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.REINDEX_QUOTATION);
            batchSettings.setLabel("Indexation - Devis");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.INDEXATION));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.REINDEX_RESPONSABLE) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.REINDEX_RESPONSABLE);
            batchSettings.setLabel("Indexation - Responsables");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.INDEXATION));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.REINDEX_TIERS) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.REINDEX_TIERS);
            batchSettings.setLabel("Indexation - Tiers");
            batchSettings.setFixedRate(1000);
            batchSettings.setQueueSize(5);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(false);
            batchSettings.setMaxAddedNumberPerIteration(0);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.INDEXATION));
            addOrUpdateBatchSettings(batchSettings);
        }
        if (getByCode(Batch.SIGN_FORMALITE_GUICHET_UNIQUE) == null) {
            BatchSettings batchSettings = new BatchSettings();
            batchSettings.setCode(Batch.SIGN_FORMALITE_GUICHET_UNIQUE);
            batchSettings.setLabel("Signature des dossiers GU");
            batchSettings.setFixedRate(1000 * 60 * 2);
            batchSettings.setQueueSize(1);
            batchSettings.setIsActive(true);
            batchSettings.setIsOnlyOneJob(true);
            batchSettings.setMaxAddedNumberPerIteration(1);
            batchSettings.setBatchCategory(batchCategoryService.getBatchCategoryByCode(BatchCategory.GUICHET_UNIQUE));
            addOrUpdateBatchSettings(batchSettings);
        }
    }
}
