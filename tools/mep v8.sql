 

ALTER TABLE IF EXISTS osiris.provision DROP COLUMN IF EXISTS id_formalite;
drop table formalite;

drop table content cascade;
drop table Entreprise_Mandataire cascade;
drop table options_fiscales cascade;
drop table pieces_jointe cascade;
drop table declarant cascade;
drop table Adresse_Domicile cascade;
drop table Caracteristiques cascade;
drop table Description_Etablissement cascade;
drop table Identite cascade;
drop table Description_Personne cascade;
drop table Adresse_Entreprise cascade;
drop table contact cascade;
drop table activite cascade;
drop table repreneur cascade;
drop table etablissement_principal cascade;
drop table Effectif_Salarie cascade;
drop table ancien_exploitant cascade;
drop table lien_entreprise cascade;
drop table nature_creation  cascade;
drop table origine  cascade;
drop table entreprise cascade;
drop table personne_morale  cascade;
drop table modalite cascade;
drop table autres_etablissement cascade;

-- start / stop osiris pour recréer la TABLE

-- Migration formalite

alter table provision drop column id_formalite;
drop table formalite;
delete from asso_formalite_status_successor ;
delete from asso_formalite_status_predecessor; 
delete from formalite_status;
insert into formalite_status 
SELECT id, replace(code,'SIMPLE_PROVISION','FORMALITE'), icon, is_close_state, is_open_state, label, aggregate_status
	FROM osiris.simple_provision_status;

-- stater osiris en update


update provision_type
set id_provision_screen_type = 114883
where id_provision_family_type in (
select id
	from provision_family_type pt
	where pt.label like 'Formalité%'
);


update provision_type set id_default_competent_authority_service_provider=1279 where   id_provision_screen_type = 114883;

insert into formalite (id, observations, id_formalite_status, id_waited_competent_authority)
select *
from simple_provision
where id in (
select id_simple_provision
from provision p 
join provision_family_type pt on pt.id = p.id_provision_family_type
left join asso_affaire_order a on a.id = p.id_asso_affaire_order
where pt.label like 'Formalité%'
); 


update provision set id_formalite = id_simple_provision
where id_simple_provision in (
select id_simple_provision
from provision p 
join provision_family_type pt on pt.id = p.id_provision_family_type
left join asso_affaire_order a on a.id = p.id_asso_affaire_order
where pt.label like 'Formalité%'
);


update provision set id_simple_provision = null where id_formalite is not null;

delete from simple_provision
where id in (
select id_formalite
from provision p 
join provision_family_type pt on pt.id = p.id_provision_family_type
left join asso_affaire_order a on a.id = p.id_asso_affaire_order
where pt.label like 'Formalité%'
);


update audit
set entity = 'Formalite',
field_name = case when field_name ='simpleProvisionStatus' then 'formaliteStatus' else field_name end,
old_value = case when field_name ='simpleProvisionStatus' then replace(old_value,'SIMPLE_PROVISION','FORMALITE') else old_value end,
new_value = case when field_name ='simpleProvisionStatus' then replace(new_value,'SIMPLE_PROVISION','FORMALITE') else new_value end
where entity_id in (select id_formalite from provision)
and entity = 'SimpleProvision';


select max(id) from formalite;
ALTER SEQUENCE IF EXISTS osiris.formalite_sequence
    START 493102;
	
	-- update AC
	update competent_authority set inpi_reference='G0101' where id = 301;
update competent_authority set inpi_reference='G0202' where id = 310;
update competent_authority set inpi_reference='G0203' where id = 333;
update competent_authority set inpi_reference='G0301' where id = 259;
update competent_authority set inpi_reference='G0303' where id = 260;
update competent_authority set inpi_reference='G0401' where id = 289;
update competent_authority set inpi_reference='G0501' where id = 262;
update competent_authority set inpi_reference='G0601' where id = 388;
update competent_authority set inpi_reference='G0602' where id = 342;
update competent_authority set inpi_reference='G0603' where id = 370;
update competent_authority set inpi_reference='G0605' where id = 349;
update competent_authority set inpi_reference='G0702' where id = 325;
update competent_authority set inpi_reference='G0802' where id = 327;
update competent_authority set inpi_reference='G0901' where id = 371;
update competent_authority set inpi_reference='G1001' where id = 309;
update competent_authority set inpi_reference='G1101' where id = 272;
update competent_authority set inpi_reference='G1104' where id = 288;
update competent_authority set inpi_reference='G1203' where id = 376;
update competent_authority set inpi_reference='G1301' where id = 363;
update competent_authority set inpi_reference='G1303' where id = 256;
update competent_authority set inpi_reference='G1304' where id = 287;
update competent_authority set inpi_reference='G1305' where id = 290;
update competent_authority set inpi_reference='G1402' where id = 385;
update competent_authority set inpi_reference='G1407' where id = 356;
update competent_authority set inpi_reference='G1501' where id = 334;
update competent_authority set inpi_reference='G1601' where id = 386;
update competent_authority set inpi_reference='G1704' where id = 381;
update competent_authority set inpi_reference='G1708' where id = 307;
update competent_authority set inpi_reference='G1801' where id = 263;
update competent_authority set inpi_reference='G1901' where id = 383;
update competent_authority set inpi_reference='G2104' where id = 336;
update competent_authority set inpi_reference='G2202' where id = 377;
update competent_authority set inpi_reference='G2301' where id = 280;
update competent_authority set inpi_reference='G2402' where id = 390;
update competent_authority set inpi_reference='G2401' where id = 373;
update competent_authority set inpi_reference='G2501' where id = 358;
update competent_authority set inpi_reference='G2602' where id = 275;
update competent_authority set inpi_reference='G2701' where id = 282;
update competent_authority set inpi_reference='G2702' where id = 389;
update competent_authority set inpi_reference='G2801' where id = 359;
update competent_authority set inpi_reference='G2901' where id = 261;
update competent_authority set inpi_reference='G2903' where id = 335;
update competent_authority set inpi_reference='G2001' where id = 348;
update competent_authority set inpi_reference='G2002' where id = 283;
update competent_authority set inpi_reference='G3003' where id = 331;
update competent_authority set inpi_reference='G3102' where id = 380;
update competent_authority set inpi_reference='G3201' where id = 316;
update competent_authority set inpi_reference='G3302' where id = 367;
update competent_authority set inpi_reference='G3303' where id = 328;
update competent_authority set inpi_reference='G3402' where id = 350;
update competent_authority set inpi_reference='G3405' where id = 322;
update competent_authority set inpi_reference='G3501' where id = 276;
update competent_authority set inpi_reference='G3502' where id = 375;
update competent_authority set inpi_reference='G3601' where id = 315;
update competent_authority set inpi_reference='G3701' where id = 318;
update competent_authority set inpi_reference='G3801' where id = 294;
update competent_authority set inpi_reference='G3802' where id = 265;
update competent_authority set inpi_reference='G3902' where id = 398;
update competent_authority set inpi_reference='G4001' where id = 293;
update competent_authority set inpi_reference='G4002' where id = 361;
update competent_authority set inpi_reference='G4101' where id = 332;
update competent_authority set inpi_reference='G4201' where id = 258;
update competent_authority set inpi_reference='G4202' where id = 292;
update competent_authority set inpi_reference='G4302' where id = 278;
update competent_authority set inpi_reference='G4401' where id = 397;
update competent_authority set inpi_reference='G4402' where id = 268;
update competent_authority set inpi_reference='G4502' where id = 299;
update competent_authority set inpi_reference='G4601' where id = 351;
update competent_authority set inpi_reference='G4701' where id = 250;
update competent_authority set inpi_reference='G4801' where id = 274;
update competent_authority set inpi_reference='G4901' where id = 291;
update competent_authority set inpi_reference='G5001' where id = 341;
update competent_authority set inpi_reference='G5002' where id = 298;
update competent_authority set inpi_reference='G5101' where id = 399;
update competent_authority set inpi_reference='G5103' where id = 378;
update competent_authority set inpi_reference='G5201' where id = 362;
update competent_authority set inpi_reference='G5301' where id = 369;
update competent_authority set inpi_reference='G5401' where id = 396;
update competent_authority set inpi_reference='G5402' where id = 337;
update competent_authority set inpi_reference='G5501' where id = 357;
update competent_authority set inpi_reference='G5601' where id = 303;
update competent_authority set inpi_reference='G5602' where id = 254;
update competent_authority set inpi_reference='G5802' where id = 391;
update competent_authority set inpi_reference='G5952' where id = 273;
update competent_authority set inpi_reference='G5902' where id = 271;
update competent_authority set inpi_reference='G5906' where id = 253;
update competent_authority set inpi_reference='G5910' where id = 347;
update competent_authority set inpi_reference='G6001' where id = 339;
update competent_authority set inpi_reference='G6002' where id = 330;
update competent_authority set inpi_reference='G6101' where id = 311;
update competent_authority set inpi_reference='G6201' where id = 344;
update competent_authority set inpi_reference='G6202' where id = 395;
update competent_authority set inpi_reference='G6303' where id = 324;
update competent_authority set inpi_reference='G6401' where id = 329;
update competent_authority set inpi_reference='G6403' where id = 270;
update competent_authority set inpi_reference='G6502' where id = 321;
update competent_authority set inpi_reference='G6601' where id = 266;
update competent_authority set inpi_reference='G6901' where id = 360;
update competent_authority set inpi_reference='G6903' where id = 281;
update competent_authority set inpi_reference='G7001' where id = 252;
update competent_authority set inpi_reference='G7102' where id = 323;
update competent_authority set inpi_reference='G7106' where id = 300;
update competent_authority set inpi_reference='G7202' where id = 249;
update competent_authority set inpi_reference='G7301' where id = 364;
update competent_authority set inpi_reference='G7401' where id = 343;
update competent_authority set inpi_reference='G7402' where id = 393;
update competent_authority set inpi_reference='G7501' where id = 267;
update competent_authority set inpi_reference='G7601' where id = 304;
update competent_authority set inpi_reference='G7606' where id = 314;
update competent_authority set inpi_reference='G7608' where id = 354;
update competent_authority set inpi_reference='G7701' where id = 264;
update competent_authority set inpi_reference='G7702' where id = 372;
update competent_authority set inpi_reference='G7803' where id = 251;
update competent_authority set inpi_reference='G7901' where id = 269;
update competent_authority set inpi_reference='G8002' where id = 284;
update competent_authority set inpi_reference='G8101' where id = 345;
update competent_authority set inpi_reference='G8102' where id = 317;
update competent_authority set inpi_reference='G8201' where id = 302;
update competent_authority set inpi_reference='G8302' where id = 285;
update competent_authority set inpi_reference='G8303' where id = 297;
update competent_authority set inpi_reference='G8305' where id = 340;
update competent_authority set inpi_reference='G8401' where id = 352;
update competent_authority set inpi_reference='G8501' where id = 319;
update competent_authority set inpi_reference='G8602' where id = 355;
update competent_authority set inpi_reference='G8701' where id = 313;
update competent_authority set inpi_reference='G8801' where id = 366;
update competent_authority set inpi_reference='G8901' where id = 296;
update competent_authority set inpi_reference='G8903' where id = 279;
update competent_authority set inpi_reference='G9001' where id = 320;
update competent_authority set inpi_reference='G7801' where id = 346;
update competent_authority set inpi_reference='G9201' where id = 368;
update competent_authority set inpi_reference='G9301' where id = 392;
update competent_authority set inpi_reference='G9401' where id = 384;
update competent_authority set inpi_reference='G7802' where id = 305;
update competent_authority set inpi_reference='G5751' where id = 353;
update competent_authority set inpi_reference='G5752' where id = 394;
update competent_authority set inpi_reference='G5753' where id = 338;
update competent_authority set inpi_reference='G6751' where id = 382;
update competent_authority set inpi_reference='G6752' where id = 387;
update competent_authority set inpi_reference='G6851' where id = 312;
update competent_authority set inpi_reference='G6852' where id = 326;
update competent_authority set inpi_reference='G9711' where id = 400;
update competent_authority set inpi_reference='G9712' where id = 365;
update competent_authority set inpi_reference='G9721' where id = 257;
update competent_authority set inpi_reference='G9731' where id = 286;
update competent_authority set inpi_reference='G9741' where id = 255;
update competent_authority set inpi_reference='G9742' where id = 308;
update competent_authority set inpi_reference='G9761' where id = 306;
update competent_authority set inpi_reference='G9811' where id = 277;
update competent_authority set inpi_reference='G9812' where id = 374;



-- fin migration formalité


-- rattrapage prélèvement paris / nanterre :

**update débours prélèvement => virement :

update debour set id_payment_type = 112720
where id in (
select  d.id
from debour d
join invoice_item i on i.id = d.id_invoice_item
where i.id_invoice in (
	174674,
167095,
168156,
169904,
169914
));

A lancer dans BankTransfertServiceImpl
generateBankTransfertForManualInvoice(invoiceService.getInvoice(174674));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(167095));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(168156));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(169904));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(169914));

** update mise en compte => virement

delete from accounting_record where id_debour in (
select  d.id
from debour d
join invoice_item i on i.id = d.id_invoice_item
where i.id_invoice in (
	165695,
150915,
150642,
150172,
150934,
150691,
161863,
150211,
150269,
150326,
143538,
146119,
146324
));

update debour set id_payment_type = 112720
where id in (
select  d.id
from debour d
join invoice_item i on i.id = d.id_invoice_item
where i.id_invoice in (
	165695,
150915,
150642,
150172,
150934,
150691,
161863,
150211,
150269,
150326,
143538,
146119,
146324
));

generateBankTransfertForManualInvoice(invoiceService.getInvoice(165695));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(150915));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(150642));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(150172));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(150934));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(150691));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(161863));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(150211));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(150269));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(150326));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(143538));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(146119));
generateBankTransfertForManualInvoice(invoiceService.getInvoice(146324));

-- fin rattrapage prélèvement paris / nanterre :
 
insert into code_pays values ('AFG','AFGHANISTAN');
insert into code_pays values ('ZAF','AFRIQUE DU SUD');
insert into code_pays values ('ALB','ALBANIE');
insert into code_pays values ('DZA','ALGÉRIE');
insert into code_pays values ('DEU','ALLEMAGNE');
insert into code_pays values ('AND','ANDORRE');
insert into code_pays values ('AGO','ANGOLA');
insert into code_pays values ('AIA','ANGUILLA');
insert into code_pays values ('ATA','ANTARCTIQUE');
insert into code_pays values ('ATG','ANTIGUA-ET-BARBUDA');
insert into code_pays values ('ANT','ANTILLES NÉERLANDAISES');
insert into code_pays values ('SAU','ARABIE SAOUDITE');
insert into code_pays values ('ARG','ARGENTINE');
insert into code_pays values ('ARM','ARMÉNIE');
insert into code_pays values ('ABW','ARUBA');
insert into code_pays values ('AUS','AUSTRALIE');
insert into code_pays values ('AUT','AUTRICHE');
insert into code_pays values ('AZE','AZERBAÏDJAN');
insert into code_pays values ('BHS','BAHAMAS');
insert into code_pays values ('BHR','BAHREÏN');
insert into code_pays values ('BGD','BANGLADESH');
insert into code_pays values ('BRB','BARBADE');
insert into code_pays values ('BLR','BÉLARUS');
insert into code_pays values ('BEL','BELGIQUE');
insert into code_pays values ('BLZ','BELIZE');
insert into code_pays values ('BEN','BÉNIN');
insert into code_pays values ('BES','BONAIRE, SAINT EUSTACHE ET SABA');
insert into code_pays values ('BMU','BERMUDES');
insert into code_pays values ('BTN','BHOUTAN');
insert into code_pays values ('BOL','BOLIVIE');
insert into code_pays values ('BIH','BOSNIE-HERZÉGOVINE');
insert into code_pays values ('BWA','BOTSWANA');
insert into code_pays values ('BVT','BOUVET, ÎLE');
insert into code_pays values ('BRA','BRÉSIL');
insert into code_pays values ('BRN','BRUNÉI DARUSSALAM');
insert into code_pays values ('BGR','BULGARIE');
insert into code_pays values ('BFA','BURKINA FASO');
insert into code_pays values ('BDI','BURUNDI');
insert into code_pays values ('CYM','CAÏMANS, ÎLES');
insert into code_pays values ('KHM','CAMBODGE');
insert into code_pays values ('CMR','CAMEROUN');
insert into code_pays values ('CAN','CANADA');
insert into code_pays values ('CPV','CAP-VERT');
insert into code_pays values ('CAF','CENTRAFRICAINE, RÉPUBLIQUE');
insert into code_pays values ('CHL','CHILI');
insert into code_pays values ('CHN','CHINE');
insert into code_pays values ('CXR','CHRISTMAS, ÎLE');
insert into code_pays values ('CYP','CHYPRE');
insert into code_pays values ('CCK','COCOS (KEELING), ÎLES');
insert into code_pays values ('COL','COLOMBIE');
insert into code_pays values ('COM','COMORES');
insert into code_pays values ('COG','CONGO');
insert into code_pays values ('COD','CONGO, LA RÉPUBLIQUE DÉMOCRATIQUE DU');
insert into code_pays values ('COK','COOK, ÎLES');
insert into code_pays values ('KOR','CORÉE, RÉPUBLIQUE DE');
insert into code_pays values ('PRK','CORÉE, RÉPUBLIQUE POPULAIRE DÉMOCRATIQUE DE');
insert into code_pays values ('CRI','COSTA RICA');
insert into code_pays values ('CIV','CÔTE D''IVOIRE');
insert into code_pays values ('HRV','CROATIE');
insert into code_pays values ('CUB','CUBA');
insert into code_pays values ('CUW','CURAÇAO');
insert into code_pays values ('DNK','DANEMARK');
insert into code_pays values ('DJI','DJIBOUTI');
insert into code_pays values ('DOM','DOMINICAINE, RÉPUBLIQUE');
insert into code_pays values ('DMA','DOMINIQUE');
insert into code_pays values ('EGY','ÉGYPTE');
insert into code_pays values ('SLV','EL SALVADOR');
insert into code_pays values ('ARE','ÉMIRATS ARABES UNIS');
insert into code_pays values ('ECU','ÉQUATEUR');
insert into code_pays values ('ERI','ÉRYTHRÉE');
insert into code_pays values ('ESP','ESPAGNE');
insert into code_pays values ('EST','ESTONIE');
insert into code_pays values ('USA','ÉTATS-UNIS');
insert into code_pays values ('ETH','ÉTHIOPIE');
insert into code_pays values ('FLK','FALKLAND, ÎLES (MALVINAS)');
insert into code_pays values ('FRO','FÉROÉ, ÎLES');
insert into code_pays values ('FJI','FIDJI');
insert into code_pays values ('FIN','FINLANDE');
insert into code_pays values ('FRA','FRANCE');
insert into code_pays values ('GAB','GABON');
insert into code_pays values ('GMB','GAMBIE');
insert into code_pays values ('GEO','GÉORGIE');
insert into code_pays values ('SGS','GÉORGIE DU SUD ET LES ÎLES SANDWICH DU SUD');
insert into code_pays values ('GHA','GHANA');
insert into code_pays values ('GIB','GIBRALTAR');
insert into code_pays values ('GRC','GRÈCE');
insert into code_pays values ('GRD','GRENADE');
insert into code_pays values ('GRL','GROENLAND');
insert into code_pays values ('GUM','GUAM');
insert into code_pays values ('GTM','GUATEMALA');
insert into code_pays values ('GGY','GUERNESEY');
insert into code_pays values ('GIN','GUINÉE');
insert into code_pays values ('GNB','GUINÉE-BISSAU');
insert into code_pays values ('GNQ','GUINÉE ÉQUATORIALE');
insert into code_pays values ('GUY','GUYANA');
insert into code_pays values ('HTI','HAÏTI');
insert into code_pays values ('HMD','HEARD, ÎLE ET MCDONALD, ÎLES');
insert into code_pays values ('HND','HONDURAS');
insert into code_pays values ('HKG','HONG-KONG');
insert into code_pays values ('HUN','HONGRIE');
insert into code_pays values ('IMN','ÎLE DE MAN');
insert into code_pays values ('UMI','ÎLES MINEURES ÉLOIGNÉES DES ÉTATS-UNIS');
insert into code_pays values ('VGB','ÎLES VIERGES BRITANNIQUES');
insert into code_pays values ('VIR','ÎLES VIERGES DES ÉTATS-UNIS');
insert into code_pays values ('IND','INDE');
insert into code_pays values ('IDN','INDONÉSIE');
insert into code_pays values ('IRN','RÉPUBLIQUE ISLAMIQUE D''IRAN');
insert into code_pays values ('IRQ','IRAQ');
insert into code_pays values ('IRL','IRLANDE');
insert into code_pays values ('ISL','ISLANDE');
insert into code_pays values ('ISR','ISRAËL');
insert into code_pays values ('ITA','ITALIE');
insert into code_pays values ('JAM','JAMAÏQUE');
insert into code_pays values ('JPN','JAPON');
insert into code_pays values ('JEY','JERSEY');
insert into code_pays values ('JOR','JORDANIE');
insert into code_pays values ('KAZ','KAZAKHSTAN');
insert into code_pays values ('KEN','KENYA');
insert into code_pays values ('KGZ','KIRGHIZISTAN');
insert into code_pays values ('KIR','KIRIBATI');
insert into code_pays values ('KWT','KOWEÏT');
insert into code_pays values ('UNK','KOSOVO');
insert into code_pays values ('LAO','LAO, RÉPUBLIQUE DÉMOCRATIQUE POPULAIRE');
insert into code_pays values ('LSO','LESOTHO');
insert into code_pays values ('LVA','LETTONIE');
insert into code_pays values ('LBN','LIBAN');
insert into code_pays values ('LBR','LIBÉRIA');
insert into code_pays values ('LBY','LIBYENNE, JAMAHIRIYA ARABE');
insert into code_pays values ('LIE','LIECHTENSTEIN');
insert into code_pays values ('LTU','LITUANIE');
insert into code_pays values ('LUX','LUXEMBOURG');
insert into code_pays values ('MAC','MACAO');
insert into code_pays values ('MKD','MACÉDOINE, L''EX-RÉPUBLIQUE YOUGOSLAVE DE');
insert into code_pays values ('MDG','MADAGASCAR');
insert into code_pays values ('MYS','MALAISIE');
insert into code_pays values ('MWI','MALAWI');
insert into code_pays values ('MDV','MALDIVES');
insert into code_pays values ('MLI','MALI');
insert into code_pays values ('MLT','MALTE');
insert into code_pays values ('MNP','MARIANNES DU NORD, ÎLES');
insert into code_pays values ('MAR','MAROC');
insert into code_pays values ('MHL','MARSHALL, ÎLES');
insert into code_pays values ('MUS','MAURICE');
insert into code_pays values ('MRT','MAURITANIE');
insert into code_pays values ('MEX','MEXIQUE');
insert into code_pays values ('FSM','MICRONÉSIE, ÉTATS FÉDÉRÉS DE');
insert into code_pays values ('MDA','MOLDOVA, RÉPUBLIQUE DE');
insert into code_pays values ('MCO','MONACO');
insert into code_pays values ('MNG','MONGOLIE');
insert into code_pays values ('MNE','MONTÉNÉGRO');
insert into code_pays values ('MSR','MONTSERRAT');
insert into code_pays values ('MOZ','MOZAMBIQUE');
insert into code_pays values ('MMR','MYANMAR');
insert into code_pays values ('NAM','NAMIBIE');
insert into code_pays values ('NRU','NAURU');
insert into code_pays values ('NPL','NÉPAL');
insert into code_pays values ('NIC','NICARAGUA');
insert into code_pays values ('NER','NIGER');
insert into code_pays values ('NGA','NIGÉRIA');
insert into code_pays values ('NIU','NIUE');
insert into code_pays values ('NFK','NORFOLK, ÎLE');
insert into code_pays values ('NOR','NORVÈGE');
insert into code_pays values ('NZL','NOUVELLE-ZÉLANDE');
insert into code_pays values ('IOT','TERRITOIRE BRITANNIQUE DE L''OCÉAN INDIEN');
insert into code_pays values ('OMN','OMAN');
insert into code_pays values ('UGA','OUGANDA');
insert into code_pays values ('UZB','OUZBÉKISTAN');
insert into code_pays values ('PAK','PAKISTAN');
insert into code_pays values ('PLW','PALAOS');
insert into code_pays values ('PSE','PALESTINIEN OCCUPÉ, TERRITOIRE');
insert into code_pays values ('PAN','PANAMA');
insert into code_pays values ('PNG','PAPOUASIE-NOUVELLE-GUINÉE');
insert into code_pays values ('PRY','PARAGUAY');
insert into code_pays values ('NLD','PAYS-BAS');
insert into code_pays values ('PER','PÉROU');
insert into code_pays values ('PHL','PHILIPPINES');
insert into code_pays values ('PCN','PITCAIRN');
insert into code_pays values ('POL','POLOGNE');
insert into code_pays values ('PRI','PORTO RICO');
insert into code_pays values ('PRT','PORTUGAL');
insert into code_pays values ('QAT','QATAR');
insert into code_pays values ('ROU','ROUMANIE');
insert into code_pays values ('GBR','ROYAUME-UNI');
insert into code_pays values ('RUS','RUSSIE, FÉDÉRATION DE');
insert into code_pays values ('RWA','RWANDA');
insert into code_pays values ('ESH','SAHARA OCCIDENTAL');
insert into code_pays values ('SHN','SAINTE-HÉLÈNE');
insert into code_pays values ('LCA','SAINTE-LUCIE');
insert into code_pays values ('KNA','SAINT-KITTS-ET-NEVIS');
insert into code_pays values ('SMR','SAINT-MARIN');
insert into code_pays values ('SXM','SAINT-MARTIN (PARTIE NEERLANDAISE)');
insert into code_pays values ('VAT','SAINT-SIÈGE (ÉTAT DE LA CITÉ DU VATICAN)');
insert into code_pays values ('VCT','SAINT-VINCENT-ET-LES GRENADINES');
insert into code_pays values ('SLB','SALOMON, ÎLES');
insert into code_pays values ('WSM','SAMOA');
insert into code_pays values ('ASM','SAMOA AMÉRICAINES');
insert into code_pays values ('STP','SAO TOMÉ-ET-PRINCIPE');
insert into code_pays values ('SEN','SÉNÉGAL');
insert into code_pays values ('SRB','SERBIE');
insert into code_pays values ('SYC','SEYCHELLES');
insert into code_pays values ('SLE','SIERRA LEONE');
insert into code_pays values ('SGP','SINGAPOUR');
insert into code_pays values ('SVK','SLOVAQUIE');
insert into code_pays values ('SVN','SLOVÉNIE');
insert into code_pays values ('SOM','SOMALIE');
insert into code_pays values ('SDN','SOUDAN');
insert into code_pays values ('SSD','SOUDAN DU SUD');
insert into code_pays values ('LKA','SRI LANKA');
insert into code_pays values ('SWE','SUÈDE');
insert into code_pays values ('CHE','SUISSE');
insert into code_pays values ('SUR','SURINAME');
insert into code_pays values ('SJM','SVALBARD ET ÎLE JAN MAYEN');
insert into code_pays values ('SWZ','SWAZILAND');
insert into code_pays values ('SYR','SYRIENNE, RÉPUBLIQUE ARABE');
insert into code_pays values ('TJK','TADJIKISTAN');
insert into code_pays values ('TWN','TAÏWAN, PROVINCE DE CHINE');
insert into code_pays values ('TZA','TANZANIE, RÉPUBLIQUE-UNIE DE');
insert into code_pays values ('TCD','TCHAD');
insert into code_pays values ('CZE','TCHÈQUE, RÉPUBLIQUE');
insert into code_pays values ('THA','THAÏLANDE');
insert into code_pays values ('TLS','TIMOR-LESTE');
insert into code_pays values ('TGO','TOGO');
insert into code_pays values ('TKL','TOKELAU');
insert into code_pays values ('TON','TONGA');
insert into code_pays values ('TTO','TRINITÉ-ET-TOBAGO');
insert into code_pays values ('TUN','TUNISIE');
insert into code_pays values ('TKM','TURKMÉNISTAN');
insert into code_pays values ('TCA','TURKS ET CAÏQUES, ÎLES');
insert into code_pays values ('TUR','TURQUIE');
insert into code_pays values ('TUV','TUVALU');
insert into code_pays values ('UKR','UKRAINE');
insert into code_pays values ('URY','URUGUAY');
insert into code_pays values ('VUT','VANUATU');
insert into code_pays values ('VEN','VENEZUELA');
insert into code_pays values ('VNM','VIET NAM');
insert into code_pays values ('YEM','YÉMEN');
insert into code_pays values ('ZMB','ZAMBIE');
insert into code_pays values ('ZWE','ZIMBABWE');
