INSERT INTO public.tiers_type(
	id, code, label)
	VALUES (1, 'CLIENT', 'Client');
	
INSERT INTO public.tiers_type(
	id, code, label)
	VALUES (2, 'PROSPECT', 'Prospect');
	
	INSERT INTO public.civility(
	id, label)
	VALUES (2, 'Maître');
	
	
	INSERT INTO public.civility(
	id, label)
	VALUES (3, 'Monsieur');
	
	INSERT INTO public.civility(
	id, label)
	VALUES (4, 'Madame');
	
	INSERT INTO public.tiers_category(
	id, code,label)
	VALUES (1,'AV', 'Avocat');
	
	
	INSERT INTO public.tiers_category(
	id, code,label)
	VALUES (2,'AW', 'Architecte');
	
	INSERT INTO public.team(
	id, code, label, mail, id_manager)
	VALUES (1, 'COMMERCIAL', 'Commerciale', 'commercial@jss.fr', null);
	
	
	INSERT INTO public.employee(
	id, firstname, lastname, id_team)
	VALUES (1, 'John', 'Doe', 1);
	
	update public.team set id_manager = 1 where id = 1;
	
	INSERT INTO public.team(
	id, code, label, mail, id_manager)
	VALUES (2, 'FORMALISTE', 'Formaliste', 'formaliste@jss.fr', null);
	
	
	INSERT INTO public.employee(
	id, firstname, lastname, id_team)
	VALUES (2, 'Johanna', 'Doe', 2);
	
	update public.team set id_manager = 1 where id = 2;
	
	INSERT INTO public.team(
	id, code, label, mail, id_manager)
	VALUES (3, 'INSERTIONS', 'Insertions', 'insertion@jss.fr', null);
	
	
	INSERT INTO public.employee(
	id, firstname, lastname, id_team)
	VALUES (3, 'Emile', 'Doe', 3);
	
	update public.team set id_manager = 1 where id = 3;
	
	INSERT INTO public.language(
	id, label)
	VALUES (1, 'Français');
	
	
	INSERT INTO public.language(
	id, label)
	VALUES (2, 'Anglais');
	
	commit;
	rollback;
	
	select * from team;
	select * from employee; 
	
	