package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.JacksonLocalDateTimeDeserializer;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.quotation.model.guichetUnique.PiecesJointe;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormaliteGuichetUniqueStatus;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.FormaliteGuichetUniqueStatusService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypeDocumentService;

@Service
public class GuichetUniqueSignatureServiceImpl implements GuichetUniqueSignatureService {

    @Autowired
    GuichetUniqueDelegateService guichetUniqueDelegateService;

    @Autowired
    FormaliteGuichetUniqueService formaliteGuichetUniqueService;

    @Autowired
    TypeDocumentService typeDocumentService;

    @Autowired
    BatchService batchService;

    @Autowired
    PieceJointeService pieceJointeService;

    @Autowired
    FormaliteGuichetUniqueStatusService formaliteGuichetUniqueStatusService;

    @Value("${guichet.unique.signature.path.input}")
    private String inSignatureFolder;

    @Value("${guichet.unique.signature.path.output}")
    private String outSignatureFolder;

    @Value("${guichet.unique.signature.path.jar}")
    private String signatureJarPath;

    @Value("${guichet.unique.signature.path.configuration}")
    private String signatureConfigurationPath;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void signFormalitesGuichetUnique() throws OsirisException, OsirisClientMessageException {
        List<FormaliteGuichetUnique> formalites = formaliteGuichetUniqueService.getFormaliteGuichetUniqueToSign();
        if (formalites != null && formalites.size() > 0) {
            List<FormaliteGuichetUnique> complexeSignatureFormalities = new ArrayList<FormaliteGuichetUnique>();
            for (FormaliteGuichetUnique formalite : formalites)
                if (formalite.getTypeFormalite() != null && formalite.getTypeFormalite().getCode().equals("C"))
                    changeFormalityStatusToSigned(formalite, null, null);
                else
                    complexeSignatureFormalities.add(formalite);

            if (complexeSignatureFormalities.size() > 0) {
                HashMap<FormaliteGuichetUnique, String[]> documentFilenameList = new HashMap<FormaliteGuichetUnique, String[]>();
                cleanWorkFolders();
                downloadUnsignedDocumentsToWorkFolder(complexeSignatureFormalities, documentFilenameList);
                signDocuments();
                grabBackDocumentsAndChangeStatus(documentFilenameList);
            }
        }

    }

    private void cleanWorkFolders() throws OsirisException {
        try {
            FileUtils.cleanDirectory(new File(inSignatureFolder));
            FileUtils.cleanDirectory(new File(outSignatureFolder));
        } catch (IOException e) {
            throw new OsirisException(e, "Impossible to clean signature directories for Guichet Unique");
        }
    }

    private void downloadUnsignedDocumentsToWorkFolder(List<FormaliteGuichetUnique> formalites,
            HashMap<FormaliteGuichetUnique, String[]> documentFilenameList)
            throws OsirisException, OsirisClientMessageException {
        for (FormaliteGuichetUnique formaliteGuichetUnique : formalites) {
            if (formaliteGuichetUnique.getContent().getPiecesJointes() != null
                    && formaliteGuichetUnique.getContent().getPiecesJointes().size() > 0) {
                PiecesJointe lastSynthesis = null;
                PiecesJointe lastBe = null;
                for (PiecesJointe piecesJointe : formaliteGuichetUnique.getContent().getPiecesJointes()) {

                    if (piecesJointe.getTypeDocument().getCode()
                            .equals(TypeDocument.UNSIGNED_SYNTHESES_DOCUMENT_CODE)) {
                        if (lastSynthesis == null || LocalDateTime.parse(lastSynthesis.getCreated().substring(0, 19),
                                JacksonLocalDateTimeDeserializer.formatter)
                                .isBefore(LocalDateTime.parse(piecesJointe.getCreated().substring(0, 19),
                                        JacksonLocalDateTimeDeserializer.formatter)))
                            lastSynthesis = piecesJointe;
                    }
                    if (piecesJointe.getTypeDocument().getCode()
                            .equals(TypeDocument.UNSIGNED_BE_DOCUMENT_CODE)) {
                        if (lastBe == null || LocalDateTime.parse(lastBe.getCreated().substring(0, 19),
                                JacksonLocalDateTimeDeserializer.formatter)
                                .isBefore(LocalDateTime.parse(piecesJointe.getCreated().substring(0, 19),
                                        JacksonLocalDateTimeDeserializer.formatter)))
                            lastBe = piecesJointe;
                    }
                }

                documentFilenameList.put(formaliteGuichetUnique, new String[2]);
                if (lastSynthesis != null && (lastSynthesis.getIsAlreadySigned() == null
                        || lastSynthesis.getIsAlreadySigned() == false)) {
                    File file = guichetUniqueDelegateService.getAttachmentById(lastSynthesis.getAttachmentId(),
                            formaliteGuichetUnique.getId() + "_" + lastSynthesis.getAttachmentId() + "_");
                    documentFilenameList.get(formaliteGuichetUnique)[0] = Paths
                            .get(file.getAbsolutePath()).getFileName().toString();
                    moveFileToInputFolder(file, formaliteGuichetUnique);

                    if (lastBe != null)
                        if (lastBe.getIsAlreadySigned() == null || lastBe.getIsAlreadySigned() == false) {
                            file = guichetUniqueDelegateService.getAttachmentById(lastBe.getAttachmentId(),
                                    formaliteGuichetUnique.getId() + "_" + lastBe.getAttachmentId() + "_");
                            documentFilenameList.get(formaliteGuichetUnique)[1] = Paths
                                    .get(file.getAbsolutePath()).getFileName().toString();
                            moveFileToInputFolder(file, formaliteGuichetUnique);
                        }
                }
            }
        }
    }

    private void moveFileToInputFolder(File file, FormaliteGuichetUnique formaliteGuichetUnique)
            throws OsirisException {
        try {
            Files.move(file.toPath(),
                    Paths.get(inSignatureFolder.trim())
                            .resolve(
                                    Paths.get(file.getAbsolutePath()).getFileName().toString()),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new OsirisException(e,
                    "Impossible to move unsigned synthesis temp file for formalite "
                            + formaliteGuichetUnique.getId());
        }
    }

    private void signDocuments() throws OsirisException {
        executeJar(signatureJarPath,
                Arrays.asList("-c", signatureConfigurationPath, "-i", inSignatureFolder, "-o", outSignatureFolder));
    }

    private void grabBackDocumentsAndChangeStatus(HashMap<FormaliteGuichetUnique, String[]> documentFilenameList)
            throws OsirisException, OsirisClientMessageException {
        if (documentFilenameList != null && documentFilenameList.size() > 0) {
            for (FormaliteGuichetUnique formalite : documentFilenameList.keySet()) {
                File signedSynthesis = null;
                File signedBe = null;
                PiecesJointe signedSynthesisPieceJointe = null;
                PiecesJointe signedSynthesisBePieceJointe = null;

                if (documentFilenameList.get(formalite)[0] != null) {
                    File directoryPath = new File(outSignatureFolder);
                    File filesList[] = directoryPath.listFiles();
                    for (File file : filesList) {
                        if (file.getName().contains(documentFilenameList.get(formalite)[0]))
                            signedSynthesis = file;

                        if (documentFilenameList.get(formalite)[1] != null
                                && file.getName().contains(documentFilenameList.get(formalite)[1]))
                            signedBe = file;
                    }
                }

                if (signedSynthesis != null) {
                    signedSynthesisPieceJointe = guichetUniqueDelegateService.uploadAttachment(formalite,
                            signedSynthesis,
                            typeDocumentService.getTypeDocumentByCode(TypeDocument.SIGNED_SYNTHESES_DOCUMENT_CODE),
                            "Signed_synthesis_" + formalite.getId() + "_"
                                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                                    + ".pdf");
                    if (signedBe != null)
                        signedSynthesisBePieceJointe = guichetUniqueDelegateService.uploadAttachment(formalite,
                                signedBe,
                                typeDocumentService.getTypeDocumentByCode(TypeDocument.SIGNED_BE_DOCUMENT_CODE),
                                "Signed_be_" + formalite.getId() + "_"
                                        + LocalDateTime.now()
                                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                                        + ".pdf");

                }
                changeFormalityStatusToSigned(formalite, signedSynthesisPieceJointe, signedSynthesisBePieceJointe);
                batchService.declareNewBatch(Batch.REFRESH_FORMALITE_GUICHET_UNIQUE, formalite.getId());

            }
        }
    }

    private void changeFormalityStatusToSigned(FormaliteGuichetUnique formalite,
            PiecesJointe signedSynthesisPieceJointe, PiecesJointe signedSynthesisBePieceJointe)
            throws OsirisException, OsirisClientMessageException {
        guichetUniqueDelegateService.signeFormality(formalite, signedSynthesisPieceJointe,
                signedSynthesisBePieceJointe);
        formalite.setStatus(formaliteGuichetUniqueStatusService
                .getFormaliteGuichetUniqueStatus(FormaliteGuichetUniqueStatus.SIGNED));
        formaliteGuichetUniqueService.addOrUpdateFormaliteGuichetUnique(formalite);

        if (formalite.getFormalite() != null
                && formalite.getIsAuthorizedToSign() != null
                && formalite.getIsAuthorizedToSign()) {
            formalite.setIsAuthorizedToSign(false);
            formaliteGuichetUniqueService.addOrUpdateFormaliteGuichetUnique(formalite);
        }
    }

    private void executeJar(String jarFilePath, List<String> args) throws OsirisException {
        final List<String> actualArgs = new ArrayList<String>();
        actualArgs.add(0, "java");
        actualArgs.add(1, "-jar");
        actualArgs.add(2, jarFilePath);
        actualArgs.addAll(args);
        BufferedReader error;
        BufferedReader op;
        int exitVal;
        try {
            final Runtime re = Runtime.getRuntime();
            final Process command = re.exec(actualArgs.toArray(new String[0]));
            error = new BufferedReader(new InputStreamReader(command.getErrorStream()));
            op = new BufferedReader(new InputStreamReader(command.getInputStream()));
            // Wait for the application to Finish
            command.waitFor();
            exitVal = command.exitValue();
            if (exitVal != 0) {
                throw new OsirisException(null, "Failed to execure jar, " + getExecutionLog(error, op, exitVal));
            }

        } catch (final IOException | InterruptedException e) {
            throw new OsirisException(e, "Error when executing signature JAR");
        }
    }

    private String getExecutionLog(BufferedReader errorBuffer, BufferedReader opBuffer, int exitVal) {
        String error = "";
        String line;
        try {
            while ((line = errorBuffer.readLine()) != null) {
                error = error + "\n" + line;
            }
        } catch (final IOException e) {
        }
        String output = "";
        try {
            while ((line = opBuffer.readLine()) != null) {
                output = output + "\n" + line;
            }
        } catch (final IOException e) {
        }
        try {
            errorBuffer.close();
            opBuffer.close();
        } catch (final IOException e) {
        }
        return "exitVal: " + exitVal + ", error: " + error + ", output: " + output;
    }
}
