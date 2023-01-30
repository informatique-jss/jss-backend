select id, 'announcement' as type, code, label, aggregate_label, aggregate_priority
	from announcement_status 
	where is_close_state = false
union
select id, 'bodacc' as type, code, label, aggregate_label, aggregate_priority   
	from bodacc_status 
	where is_close_state = false
order  by aggregate_priority

select id, 'formalite' as type, code, label, aggregate_label, aggregate_priority  
	from formalite_status p 
	where is_close_state = false
union
select id, 'simple_provision' as type, code, label, aggregate_label, aggregate_priority  
	from simple_provision_status p 
	where is_close_state = false
union
select id, 'domiciliation' as type, code, label, aggregate_label, aggregate_priority  
	from domiciliation_status p 
	where is_close_state = false
order  by aggregate_priority


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

alter table announcement_status 
add aggregate_priority integer;

alter table bodacc_status 
add aggregate_priority integer;

alter table formalite_status 
add aggregate_priority integer;

alter table simple_provision_status 
add aggregate_priority integer;

alter table domiciliation_status 
add aggregate_priority integer;


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

alter table announcement_status 
ALTER COLUMN aggregate_priority SET DEFAULT 20;

alter table bodacc_status 
ALTER COLUMN aggregate_priority SET DEFAULT 20;

alter table formalite_status 
ALTER COLUMN aggregate_priority SET DEFAULT 20;

alter table simple_provision_status 
ALTER COLUMN aggregate_priority SET DEFAULT 20;

alter table domiciliation_status 
ALTER COLUMN aggregate_priority SET DEFAULT 20;

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

