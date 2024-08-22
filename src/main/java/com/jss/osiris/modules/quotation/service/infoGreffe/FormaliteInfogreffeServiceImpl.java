package com.jss.osiris.modules.quotation.service.infoGreffe;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.infoGreffe.EvenementInfogreffe;
import com.jss.osiris.modules.quotation.model.infoGreffe.FormaliteInfogreffe;
import com.jss.osiris.modules.quotation.repository.infoGreffe.FormaliteInfogreffeRepository;

@Service
public class FormaliteInfogreffeServiceImpl implements FormaliteInfogreffeService {

    @Autowired
    InfogreffeDelegateService infogreffeDelegateService;

    @Autowired
    FormaliteInfogreffeRepository formaliteInfogreffeRepository;

    @Autowired
    BatchService batchService;

    @Override
    public FormaliteInfogreffe addOrUpdFormaliteInfogreffe(FormaliteInfogreffe formaliteInfogreffe) {
        if (formaliteInfogreffe.getEvenements() != null && formaliteInfogreffe.getEvenements().size() > 0)
            for (EvenementInfogreffe evenementInfogreffe : formaliteInfogreffe.getEvenements())
                evenementInfogreffe.setFormaliteInfogreffe(formaliteInfogreffe);

        return formaliteInfogreffeRepository.save(formaliteInfogreffe);
    }

    @Override
    public FormaliteInfogreffe getFormaliteInfogreffe(Integer formaliteNumero) {
        FormaliteInfogreffe formaliteInfogreffe = formaliteInfogreffeRepository
                .findByIdentifiantFormalite_FormaliteNumero(formaliteNumero);
        return formaliteInfogreffe;
    }

    @Override
    public void refreshAllFormaliteInfogreffe(Boolean isRefreshOnlyToday)
            throws OsirisException {
        List<FormaliteInfogreffe> formalitesInfogreffe = infogreffeDelegateService
                .getAllInfogreffeFormalities();
        if (formalitesInfogreffe != null && formalitesInfogreffe.size() > 0) {
            for (FormaliteInfogreffe formaliteInfogreffe : formalitesInfogreffe) {
                setInfogreffeFormaliteEvenementDate(formaliteInfogreffe);
                if (formaliteInfogreffe.getEntreprise() != null
                        && formaliteInfogreffe.getEntreprise().getSiren() == null)
                    formaliteInfogreffe.setEntreprise(null);

                addOrUpdFormaliteInfogreffe(formaliteInfogreffe);
                // test boolean +injection batch
                if (!isRefreshOnlyToday || formaliteInfogreffe.getEvenements() == null
                        || formaliteInfogreffe.getEvenements() != null
                                && formaliteInfogreffe.getEvenements().size() > 0
                                && formaliteInfogreffe.getEvenements().get(0).getCreatedDate()
                                        .isAfter(LocalDateTime.now().withHour(00).withMinute(00).minusSeconds(01)))
                    batchService.declareNewBatch(Batch.REFRESH_FORMALITE_INFOGREFFE_DETAIL,
                            formaliteInfogreffe.getIdentifiantFormalite().getFormaliteNumero());

            }
        }
    }

    @Override
    public void refreshFormaliteInfogreffeDetail(FormaliteInfogreffe formaliteInfogreffe) throws OsirisException {
        FormaliteInfogreffe formaliteInfogreffeDetail = infogreffeDelegateService
                .getInfogreffeFormalite(formaliteInfogreffe);
        setInfogreffeFormaliteEvenementDate(formaliteInfogreffeDetail);

        if (formaliteInfogreffe.getEntreprise() != null
                && formaliteInfogreffe.getEntreprise().getSiren() == null)
            formaliteInfogreffe.setEntreprise(null);
        addOrUpdFormaliteInfogreffe(formaliteInfogreffeDetail);
    }

    @Override
    public List<FormaliteInfogreffe> getFormaliteInfogreffeByReference(String reference) {
        return formaliteInfogreffeRepository.findByReference(reference);
    }

    private void setInfogreffeFormaliteEvenementDate(FormaliteInfogreffe formaliteInfogreffe) {
        if (formaliteInfogreffe != null && formaliteInfogreffe.getEvenements() != null
                && formaliteInfogreffe.getEvenements().size() > 0) {
            for (EvenementInfogreffe evenementInfogreffe : formaliteInfogreffe.getEvenements()) {
                if (evenementInfogreffe.getDate() != null)
                    evenementInfogreffe.setCreatedDate(evenementInfogreffe.getDate());
            }
        }
    }
}
