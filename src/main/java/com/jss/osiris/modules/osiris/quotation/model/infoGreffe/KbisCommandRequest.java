package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "commandeRequest", namespace = "https://infogreffe.fr/services/commercant-service/ws/commercant")
public class KbisCommandRequest {

    @JacksonXmlProperty(localName = "siren")
    private String siren;

    @JacksonXmlProperty(localName = "documentId")
    private String documentId;

    @JacksonXmlProperty(localName = "modesDiffusion")
    private ModesDiffusionCommande modesDiffusion;

    public KbisCommandRequest() {
    }

    public KbisCommandRequest(String siren, String documentId, String modeDiffusion) {
        this.siren = siren;
        this.documentId = documentId;
        this.modesDiffusion = new ModesDiffusionCommande(modeDiffusion);
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    class ModeDiffusion {
        @JacksonXmlProperty(localName = "mode")
        private String mode;

        public ModeDiffusion() {
        }

        public ModeDiffusion(String mode) {
            this.mode = mode;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

    }

    class ModesDiffusionCommande {

        @JacksonXmlProperty(localName = "mode")
        private ModeDiffusion mode;

        public ModesDiffusionCommande() {
        }

        public ModesDiffusionCommande(String modeValue) {
            this.mode = new ModeDiffusion(modeValue);
        }

        public ModeDiffusion getMode() {
            return mode;
        }

        public void setMode(ModeDiffusion mode) {
            this.mode = mode;
        }

    }

}
