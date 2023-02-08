

alter table announcement_status 
add aggregate_label character varying(255) COLLATE pg_catalog."default";

alter table bodacc_status 
add aggregate_label character varying(255) COLLATE pg_catalog."default";

alter table formalite_status 
add aggregate_label character varying(255) COLLATE pg_catalog."default";

alter table simple_provision_status 
add aggregate_label character varying(255) COLLATE pg_catalog."default";

alter table domiciliation_status 
add aggregate_label character varying(255) COLLATE pg_catalog."default";

/*alter table announcement_status 
add aggregate_priority integer;

alter table bodacc_status 
add aggregate_priority integer;

alter table formalite_status 
add aggregate_priority integer;

alter table simple_provision_status 
add aggregate_priority integer;

alter table domiciliation_status 
add aggregate_priority integer;
*/
alter table announcement_status 
drop aggregate_priority;

alter table bodacc_status 
drop aggregate_priority;

alter table formalite_status 
drop aggregate_priority;

alter table simple_provision_status 
drop aggregate_priority;

alter table domiciliation_status 
drop aggregate_priority;


update announcement_status 
set aggregate_label = label, aggregate_priority = 20;

update bodacc_status 
set aggregate_label = label, aggregate_priority = 20;

update formalite_status 
set aggregate_label = label, aggregate_priority = 20;

update simple_provision_status 
set aggregate_label = label, aggregate_priority = 20;

update domiciliation_status 
set aggregate_label = label, aggregate_priority = 20;

/*alter table announcement_status 
ALTER COLUMN aggregate_priority SET DEFAULT 20;

alter table bodacc_status 
ALTER COLUMN aggregate_priority SET DEFAULT 20;

alter table formalite_status 
ALTER COLUMN aggregate_priority SET DEFAULT 20;

alter table simple_provision_status 
ALTER COLUMN aggregate_priority SET DEFAULT 20;

alter table domiciliation_status 
ALTER COLUMN aggregate_priority SET DEFAULT 20;*/

alter table announcement_status 
ALTER COLUMN aggregate_label SET NOT NULL;

alter table bodacc_status 
ALTER COLUMN aggregate_label SET NOT NULL;

alter table formalite_status 
ALTER COLUMN aggregate_label SET NOT NULL;

alter table simple_provision_status 
ALTER COLUMN aggregate_label SET NOT NULL;

alter table domiciliation_status 
ALTER COLUMN aggregate_label SET NOT NULL;

