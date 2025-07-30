import { Affaire } from "../../my-account/model/Affaire";
import { City } from "../../profile/model/City";
import { Country } from "../../profile/model/Country";

export interface BeneficialOwner {
    id: number;
    birthName: string;
    usedName: string;
    firstNames: string;
    pseudonym: string;
    nationality: Country;
    birthDate: Date;
    birthDepartement: number;
    birthCity: string;
    birthCountry: Country;
    residenceAddress: string;
    postalCode: string;
    city: City;
    country: Country;
    creationDate: Date;
    affaire: Affaire;
    // More than 25% ownership of capital :
    isOwnerOfMoreThanQuarterOfCapital: boolean;
    totalPercentageOfOwnedCapital: number;
    // Direct ownership of capital :
    isDirectOwnershipOfCapital: boolean;
    isIndirectOwnershipOfCapital: boolean;
    directFullCapitalOwnershipPercentage: number;
    directBareCapitalOwnershipPercentage: number;
    // Indirect ownership of capital :
    jointCapitalTotalOwnershipPercentage: number;
    jointCapitalFullOwnershipPercentage: number;
    jointCapitalBareOwnershipPercentage: number;
    moralPersonTotalCapitalOwnershipPercentage: number;
    moralPersonCapitalFullOwnershipPercentage: number;
    moralPersonCapitalBareOwnershipPercentage: number;
    // More than 25% ownership of votes rights :
    isOwnerOfMoreThanQuarterOfVotesRights: boolean;
    totalPercentageOfVotesRights: number;
    // Direct ownership of votes rights :
    isDirectOwnershipOfVotesRights: boolean;
    isIndirectOwnershipOfVotesRights: boolean;
    directFullVotesRightsOwnershipPercentage: number;
    directBareVotesRightsOwnershipPercentage: number;
    directUsufructVotesRightsOwnershipPercentage: number;
    // Indirect ownership of votes rights :
    jointVotesRightsTotalOwnershipPercentage: number;
    jointVotesRightsFullOwnershipPercentage: number;
    jointVotesRightsBareOwnershipPercentage: number;
    jointVotesRightsUsufructOwnershipPercentage: number;
    moralPersonTotalVotesRightsOwnershipPercentage: number;
    moralPersonVotesRightsFullOwnershipPercentage: number;
    moralPersonVotesRightsBareOwnershipPercentage: number;
    moralPersonVotesRightsUsufructOwnershipPercentage: number;
    // Can have a control on the company by other means
    isOtherMeansForControl: boolean;
    isDeterminesAGDecisionsByVotingRights: boolean;
    isCanNominateDismissBoardMembers: boolean;
    isBETheLegalRepresentative: boolean;

}
