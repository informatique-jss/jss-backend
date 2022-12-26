package com.jss.osiris.libs.mail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.libs.mail.model.CustomerMail;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

public interface CustomerMailRepository extends CrudRepository<CustomerMail, Integer> {

    @Query("select m from CustomerMail m where hasErrors=false and isSent = false order by createdDateTime asc")
    List<CustomerMail> findAllByOrderByCreatedDateTimeAsc();

    List<CustomerMail> findByQuotation(Quotation quotation);

    List<CustomerMail> findByCustomerOrder(CustomerOrder customerOrder);

    List<CustomerMail> findByConfrere(Confrere confrere);

    List<CustomerMail> findByTiers(Tiers tiers);

    List<CustomerMail> findByResponsable(Responsable responsable);
}