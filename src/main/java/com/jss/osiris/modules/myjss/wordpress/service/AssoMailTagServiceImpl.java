package com.jss.osiris.modules.myjss.wordpress.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailTag;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;
import com.jss.osiris.modules.myjss.wordpress.repository.AssoMailTagRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class AssoMailTagServiceImpl implements AssoMailTagService {
    @Autowired
    AssoMailTagRepository assoMailTagRepository;

    @Autowired
    EmployeeService employeeService;

    @Override
    public AssoMailTag addNewTagFollow(Tag tag) throws OsirisException {

        Responsable responsable = employeeService.getCurrentMyJssUser();
        AssoMailTag asso = new AssoMailTag();

        if (responsable == null)
            throw new OsirisException("unknown user");

        if (responsable.getMail() != null) {
            asso.setMail(responsable.getMail());
            asso.setTag(tag);
            asso.setLastConsultationDate(LocalDateTime.now());
        }
        return addOrUpdateAssoMailTag(asso);
    }

    @Override
    public AssoMailTag getAssoMailTagByMailAndTag(Tag tag) {
        return assoMailTagRepository.findByMailAndTag(employeeService.getCurrentMyJssUser().getMail(), tag);
    }

    @Override
    public Boolean getIsAssoMailTagByMailAndTag(Tag tag) {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        AssoMailTag assoMailTag = null;

        if (responsable != null)
            assoMailTag = assoMailTagRepository.findByMailAndTag(responsable.getMail(), tag);
        if (assoMailTag != null)
            return true;
        return false;
    }

    @Override
    public AssoMailTag updateTagConsultationDate(Mail mail, Tag tag) {
        AssoMailTag assoMailTag = assoMailTagRepository.findByMailAndTag(mail, tag);
        if (assoMailTag != null) {
            assoMailTag.setLastConsultationDate(LocalDateTime.now());
            return addOrUpdateAssoMailTag(assoMailTag);
        }
        return assoMailTag;
    }

    @Override
    public List<AssoMailTag> getAssoMailTagForCurrentUser() {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        return assoMailTagRepository.findByMail(responsable.getMail());
    }

    @Override
    public AssoMailTag getAssoMailTag(Integer id) {
        Optional<AssoMailTag> assoMailTag = assoMailTagRepository.findById(id);
        if (assoMailTag.isPresent())
            return assoMailTag.get();
        return null;
    }

    @Override
    public void deleteAssoMailTag(Tag tag) throws OsirisValidationException {
        AssoMailTag assoMailTag = getAssoMailTagByMailAndTag(tag);

        if (assoMailTag == null)
            throw new OsirisValidationException("assoMailTag");

        assoMailTagRepository.delete(assoMailTag);
    }

    @Override
    public AssoMailTag addOrUpdateAssoMailTag(AssoMailTag assoMailTag) {
        return assoMailTagRepository.save(assoMailTag);
    }

}
