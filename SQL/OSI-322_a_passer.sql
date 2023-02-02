


-- NOUVELLES OPTIONS 

begin;
insert into accounting_account (id, label, accounting_account_sub_number, id_principal_accounting_account, is_view_restricted) 
select max(aa1.id)+1, 'Charge - Formalité - Déclaration Supplémentaire', 
	cast(max(cast(aa2.accounting_account_sub_number as integer))+1 as text), 
	5, false 
from accounting_account aa1, accounting_account aa2 
where aa2.id_principal_accounting_account = 5;
insert into accounting_account (id, label, accounting_account_sub_number, id_principal_accounting_account, is_view_restricted) 
select max(aa1.id)+1, 'Produit - Formalité - Déclaration Supplémentaire', 
	max(aa2.accounting_account_sub_number), 
	4, false 
from accounting_account aa1, accounting_account aa2 
where aa2.id_principal_accounting_account = 5 and aa2.label = 'Charge - Formalité - Déclaration Supplémentaire';

INSERT INTO public.billing_type(
	id, label, code, can_override_price, is_price_based_on_character_number, is_override_vat, 
	is_optionnal, id_accounting_account_charge, id_accounting_account_product, is_generate_account_product, is_generate_account_charge, is_debour, is_non_taxable)
select max(b.id)+1, 'Formalité - Déclaration Supplémentaire', 'PG - 0801', false, false, false, 
	true, max(aa1.id), max(aa2.id), true, true, false, false 
from public.billing_type b, accounting_account aa1, accounting_account aa2 
where aa1.id_principal_accounting_account = 5 and aa1.label = 'Charge - Formalité - Déclaration Supplémentaire' 
and aa2.id_principal_accounting_account = 4 and aa2.label = 'Produit - Formalité - Déclaration Supplémentaire';

commit;

begin;

insert into accounting_account (id, label, accounting_account_sub_number, id_principal_accounting_account, is_view_restricted) 
select max(aa1.id)+1, 'Charge - Frais de Correspondance', 
	cast(max(cast(aa2.accounting_account_sub_number as integer))+1 as text), 
	5, false 
from accounting_account aa1, accounting_account aa2 
where aa2.id_principal_accounting_account = 5;
insert into accounting_account (id, label, accounting_account_sub_number, id_principal_accounting_account, is_view_restricted) 
select max(aa1.id)+1, 'Produit - Frais de Correspondance', 
	max(aa2.accounting_account_sub_number), 
	4, false 
from accounting_account aa1, accounting_account aa2 
where aa2.id_principal_accounting_account = 5 and aa2.label = 'Charge - Frais de Correspondance';

INSERT INTO public.billing_type(
	id, label, code, can_override_price, is_price_based_on_character_number, is_override_vat, 
	is_optionnal, id_accounting_account_charge, id_accounting_account_product, is_generate_account_product, is_generate_account_charge, is_debour, is_non_taxable)
select max(b.id)+1, 'Frais de Correspondance', 'PG - 0812', false, false, false, 
	true, max(aa1.id), max(aa2.id), true, true, false, false 
from public.billing_type b, accounting_account aa1, accounting_account aa2 
where aa1.id_principal_accounting_account = 5 and aa1.label = 'Charge - Frais de Correspondance' 
and aa2.id_principal_accounting_account = 4 and aa2.label = 'Produit - Frais de Correspondance';

commit;


-- OPTIONS AJOUT DANS constant ET  provision 

-- Vacation - Dépôt des Bénéficiaires Effectifs
begin;
alter table public.constant 
add id_billing_type_vacation_deposit_beneficial_owners integer;

update public.constant 
set id_billing_type_vacation_deposit_beneficial_owners = (select id from billing_type where code = 'PS - D6 - 03');
commit;
rollback;

begin;
alter table public.provision 
add is_vacation_deposit_beneficial_owners boolean;

update public.provision 
set is_vacation_deposit_beneficial_owners = false;
commit;

--Vacation - Mise à jour - Bénéficiaires Effectifs
begin;
alter table public.constant 
add id_billing_type_vacation_update_beneficial_owners integer;

update public.constant 
set id_billing_type_vacation_update_beneficial_owners = (select id from billing_type where code = 'PS - D6 - 031');
commit;
rollback;

begin;
alter table public.provision 
add is_vacation_update_beneficial_owners boolean;

update public.provision 
set is_vacation_update_beneficial_owners = false;
commit;

-- Formalité - Déclaration Supplémentaire
begin;
alter table public.constant 
add id_billing_type_formality_additional_declaration integer;

update public.constant 
set id_billing_type_formality_additional_declaration = (select id from billing_type where code = 'PG - 0801');
commit;
rollback;

begin;
alter table public.provision 
add is_formality_additional_declaration boolean;

update public.provision 
set is_formality_additional_declaration = false;
commit;

-- Frais de Correspondance
begin;
alter table public.constant 
add id_billing_type_correspondence_fees integer;

update public.constant 
set id_billing_type_correspondence_fees = (select id from billing_type where code = 'PG - 0812');
commit;
rollback;

begin;
alter table public.provision 
add is_correspondence_fees boolean;

update public.provision 
set is_correspondence_fees = false;
commit;


--LIAISON WITH PROVISIONS
INSERT INTO public.asso_provision_billing_type(
	id_provision, id_billing_type)
select p.id, b.id
from provision_type p, billing_type b 
where p.code = 'D6 - 03' 
and b.code in ('PS - D6 - 03', 'PS - D6 - 031', 'PG - 0801', 'PG - 0812');

--FACTURATION
INSERT INTO public.billing_item(
	id, id_billing_type, pre_tax_price, start_date)
select max(i.id)+1, b.id, 0, '01/01/2023' from  public.billing_item i, billing_type b  
where b.code = 'PS - D6 - 03'
group by b.id;
INSERT INTO public.billing_item(
	id, id_billing_type, pre_tax_price, start_date)
select max(i.id)+1, b.id, 0, '01/01/2023' from  public.billing_item i, billing_type b  
where b.code = 'PS - D6 - 031'
group by b.id;
INSERT INTO public.billing_item(
	id, id_billing_type, pre_tax_price, start_date)
select max(i.id)+1, b.id, 0, '01/01/2023' from  public.billing_item i, billing_type b  
where b.code = 'PG - 0801'
group by b.id;
INSERT INTO public.billing_item(
	id, id_billing_type, pre_tax_price, start_date)
select max(i.id)+1, b.id, 0, '01/01/2023' from  public.billing_item i, billing_type b  
where b.code = 'PG - 0812'
group by b.id;

