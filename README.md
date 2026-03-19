TODO before production deployment for version 36 : 
- reindex entities (because of bug on customerOrder search when in myAccount).
- Manage NewspaperPage entities, either :
      - Launch fillDbWithNewspaperPages() in NewspaperPageService.java. With @Scheduler on any injected Service. This will create the NewletterPages           with the content of the PDF newsletter files.
      - Copy the data from local to REC, then from REC to PROD.
- Update REC and PROD db to integrate source column
