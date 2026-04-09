TODO before production deployment for version 36 : 
- reindex entities (because of bug on customerOrder search when in myAccount).
- Manage NewspaperPage entities, either :
      - Launch fillDbWithNewspaperPages() in NewspaperPageService.java. With @Scheduler on any injected Service. This will create the NewletterPages           with the content of the PDF newsletter files.
      - Copy the data from local to REC, then from REC to PROD.
- Update REC and PROD db to integrate source column :
    update post set source='JSS' where id in (select post.id from post inner join asso_post_jss_category apjc on post.id = apjc.id_post) ; 
    update post set source='MYJSS' where id in (select post.id from post inner join asso_post_myjss_category apjc on post.id = apjc.id_post) ; 

- insertion new Webinar :
    insert into webinar (id, code, label, webinar_date ) values (nextval('webinar_sequence'), 'WEB-3', 'Donation, succession, quel est l’impact sur les formalités légales ?', '2026-04-16 09:30:00.000' );

TODO Before production deployment and REC deployemnt of version 37 :
- for OSI-9835 : reindex newspaperPages as we added the newspaperr id as an indexedField for retrieving newspaper images when entities are found.
