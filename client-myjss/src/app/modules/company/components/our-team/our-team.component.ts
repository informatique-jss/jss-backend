import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { PlatformService } from '../../../main/services/platform.service';

@Component({
  selector: 'our-team',
  templateUrl: './our-team.component.html',
  styleUrls: ['./our-team.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class OurTeamComponent implements OnInit {

  constructor(private platformService: PlatformService) { }

  departments: any[] = [
    { name: 'Direction', key: 'direction' },
    { name: 'Annonces', key: 'annonces' },
    { name: 'Formalités', key: 'formalites' },
    { name: 'Commercial', key: 'commercial' },
    { name: 'Marketing', key: 'marketing' },
    { name: 'Rédaction', key: 'redaction' },
    { name: 'Comptabilité', key: 'comptabilite' },
    { name: 'Informatique', key: 'informatique' },
    { name: 'RH', key: 'rh' }
  ];

  selectedTab: any = this.departments[0];

  staff: { [key: string]: { firstname: string; lastname: string; title: string; photo: string }[] } = {
    direction: [
      { firstname: "Myriam", lastname: "de M.", title: "Présidente", photo: "myriam_presidente.jpg" },
      { firstname: "Cyrille", lastname: "de M.", title: "Directeur de la Rédaction", photo: "cyrille_directeur_de_la_redaction.jpg" },
      { firstname: "Arsène", lastname: "de M.", title: "Directeur Général Délégué", photo: "arsene_directeur_general_delegue.jpg" },
      { firstname: "Alex", lastname: "G.", title: "Directeur de la Production", photo: "alex_directeur_de_la_production.jpg" },
    ],
    rh: [
      { firstname: "Yllia", lastname: "V.", title: "Gestionnaire RH", photo: "yllia_gestionnaire_rh.jpg" }
    ],
    commercial: [
      { firstname: "Martine", lastname: "S.", title: "Directrice commerciale", photo: "martine_directrice_commerciale.jpg" },
      { firstname: "Bruno", lastname: "V.", title: "Chargé de clientèle", photo: "bruno_charge_de_clientele.jpg" },
      { firstname: "Gaëlle", lastname: "H.", title: "Chargée de clientèle", photo: "gaelle_chargee_de_clientele.jpg" },
      { firstname: "Martine", lastname: "F.", title: "Chargée de clientèle", photo: "martine_chargee_de_clientele.jpg" },
      { firstname: "Benjamin", lastname: "B.", title: "Chargé de clientèle", photo: "benjamin_charge_de_clientele.jpg" },
      { firstname: "Catherine", lastname: "D.", title: "Assistante commerciale", photo: "catherine_assistante_commerciale.jpg" },
      { firstname: "Emma", lastname: "L.", title: "Assistante commerciale", photo: "emma_assistante_commerciale.jpg" },
      { firstname: "Jean Philippe", lastname: "D.", title: "Chargé de recouvrement", photo: "jean_charge_de_recouvrement.jpg" },
      { firstname: "Jovanka", lastname: "M.", title: "Chargé de recouvrement", photo: "jovanka_charge_de_recouvrement.jpg" },
      { firstname: "Catherine", lastname: "C.", title: "Hôtesse d'accueil", photo: "catherine_hotesse_d_accueil.jpg" },
    ],
    annonces: [
      { firstname: "Hélène", lastname: "P.", title: "Responsable Service Annonces Légales", photo: "helene_responsable_service_annonces_legales.jpg" },
      { firstname: "Didier", lastname: "C.", title: "Gestionnaire d'annonces légales", photo: "didier_gestionnaire_d_annonces_legales.jpg" },
      { firstname: "Elianna", lastname: "L.", title: "Gestionnaire d'annonces légales", photo: "elianna_gestionnaire_d_annonces_legales.jpg" },
      { firstname: "Diana", lastname: "L.", title: "Gestionnaire d'annonces légales", photo: "diana_gestionnaire_d_annonces_legales.jpg" },
      { firstname: "Martine", lastname: "J.", title: "Gestionnaire d'annonces légales", photo: "martine_gestionnaire_d_annonces_legales.jpg" }
    ],
    formalites: [
      { firstname: "Géraldine", lastname: "M.", title: "Formaliste Senior", photo: "geraldine_formaliste_senior.jpg" },
      { firstname: "Léa", lastname: "H.", title: "Formaliste Sénior", photo: "lea_formaliste_senior.jpg" },
      { firstname: "Murielle", lastname: "R.", title: "Formaliste Senior", photo: "murielle_formaliste_senior.jpg" },
      { firstname: "Léa", lastname: "P.", title: "Formaliste", photo: "lea_formaliste.jpg" },
      { firstname: "Chuan", lastname: "H.", title: "Formaliste", photo: "chuan_formaliste.jpg" },
      { firstname: "Sarah", lastname: "C.", title: "Formaliste", photo: "sarah_formaliste.jpg" },
      { firstname: "Nicolas", lastname: "J.", title: "Formaliste", photo: "nicolas_juriste_formaliste.jpg" },
      { firstname: "Estelle", lastname: "N.", title: "Assistante formaliste", photo: "estelle_assistante_formaliste.jpg" },
      { firstname: "Anne-Esther", lastname: "G.", title: "Assistante formaliste", photo: "anne_esther_assistante_formaliste.jpg" },
      { firstname: "Stella", lastname: "S.", title: "Assistante formaliste", photo: "stella_formaliste.jpg" },
      { firstname: "Nathalie", lastname: "D.", title: "Assistante administrative", photo: "nathalie_assistante_administrative.jpg" },
      { firstname: "Caroline", lastname: "C.", title: "Assistante administrative", photo: "caroline_assistante_administrative.jpg" },
    ],
    marketing: [
      { firstname: "Yseult", lastname: "V.", title: "Responsable Marketing et Communication", photo: "yseult_responsable_marketing_et_communication.jpg" },
      { firstname: "Jessica", lastname: "L.", title: "Chargée de Marketing et de Communication", photo: "jessica_chargee_de_marketing_et_de_communication.jpg" },
      { firstname: "Solène", lastname: "F.", title: "Graphiste Community Manager", photo: "solene_graphiste_community_manager.jpg" },
    ],
    redaction: [
      { firstname: "Bérengère", lastname: "M.", title: "Rédactrice en chef", photo: "berengere_redactrice_en_chef.jpg" },
      { firstname: "Alexis", lastname: "D.", title: "Journaliste", photo: "alexis_journaliste.jpg" },
      { firstname: "Allison", lastname: "V.", title: "Journaliste", photo: "allison_journaliste.jpg" },
      { firstname: "Mylène", lastname: "H.", title: "Secrétaire de Rédaction", photo: "mylene_secretaire_de_redaction.jpg" },
      { firstname: "Romain", lastname: "T.", title: "Journaliste", photo: "romain_journaliste.jpg" }
    ],
    comptabilite: [
      { firstname: "Belinda", lastname: "B.", title: "Chef Comptable", photo: "belinda_chef_comptable.jpg" },
      { firstname: "Marie Line", lastname: "D.", title: "Comptable", photo: "marie_comptable.jpg" },
      { firstname: "Amal", lastname: "A.", title: "Aide Comptable", photo: "amal_aide_comptable.jpg" },
      { firstname: "Clara", lastname: "R.", title: "Agent de facturation", photo: "clara_agent_de_facturation.jpg" },
      { firstname: "Céline", lastname: "L.", title: "Agent de facturation", photo: "celine_agent_de_facturation.jpg" },
    ],
    informatique: [
      { firstname: "Frédéric", lastname: "B.", title: "Analyste Programmeur", photo: "frederic_analyste_programmeur.jpg" },
      { firstname: "Pierre", lastname: "C.", title: "Analyste Programmeur", photo: "pierre_analyste_programmeur.jpg" },
    ]
  };

  ngOnInit() {
  }


  ngAfterViewInit(): void {
    if (this.platformService.getNativeDocument())
      import('jarallax').then(module => {
        module.jarallax(this.platformService.getNativeDocument()!.querySelectorAll('.jarallax'), {
          speed: 0.5
        });
      });
  }

  onTabClick(department: string) {
    this.selectedTab = department;
  }

}
