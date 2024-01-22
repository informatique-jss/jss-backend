import { Mail } from "../../miscellaneous/model/Mail";
import { Phone } from "../../miscellaneous/model/Phone";
import { Responsable } from "./Responsable";

export interface TiersRff {
  tiersDenomination:  string | null;
  tiersAddress: string;
  tiersId: number;
  tiersMail: string;
  tiersPhone: string;
  tiersRffInsertion: number| null;
  tiersRffTotal: number| null;
  tiersRffFormalite: number| null;
  responsableLastName: string;
  responsableFirstName: string;
  responsables: Responsable[];

  /*
     this.displayedColumnsTiers.push({ id: "denomination", fieldName: "denomination", label: "Dénomination"  } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "address", fieldName: "address", label: "address" } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "mails[0].mail", fieldName: "mails[0].mail", label: "mail" } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "phones[0].phoneNumber", fieldName: "phones[0].phoneNumber", label: "phone" } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "rffInsertion", fieldName: "rffInsertion", label: "RFF AL", valueFonction: formatEurosForSortTable } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "rffFormalite", fieldName: "rffFormalite", label: "RFF Formalités", valueFonction: formatEurosForSortTable } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "rffTotal", fieldName: "rffTotal", label: "Total HT", valueFonction: formatEurosForSortTable } as SortTableColumn<Tiers>);

    this.displayedColumnsResponsables.push({ id: "id", fieldName: "id", label: "N° du responsable" } as SortTableColumn<Responsable>);
    this.displayedColumnsResponsables.push({ id: "lastname", fieldName: "lastname", label: "Nom" } as SortTableColumn<Responsable>);
    this.displayedColumnsResponsables.push({ id: "firstname", fieldName: "firstname", label: "Prénom" } as SortTableColumn<Responsable>);
    this.displayedColumnsResponsables.push({ id: "function", fieldName: "function", label: "Fonction" } as SortTableColumn<Responsable>);
    this.displayedColumnsResponsables.push({ id: "mails", fieldName: "mails", label: "Mails", valueFonction: (element: Responsable, column: SortTableColumn<Responsable>) => { return ((element.mails) ? element.mails.map((e: { mail: any; }) => e.mail).join(", ") : "") } } as SortTableColumn<Responsable>);
    this.displayedColumnsResponsables.push({ id: "phones", fieldName: "phones", label: "Téléphones", valueFonction: (element: Responsable, column: SortTableColumn<Responsable>) => { return ((element.phones) ? element.phones.map((e: { phoneNumber: any; }) => e.phoneNumber).join(", ") : "") } } as SortTableColumn<Responsable>);
 */
}
