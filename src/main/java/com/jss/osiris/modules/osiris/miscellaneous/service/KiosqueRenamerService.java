package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class KiosqueRenamerService {

    public void renameFromExcel(Path excelPath, Path kiosqueRoot) {
        System.err.println("[OK]Démarrage du processus.");

        int fichiersRenommes = 0;
        int fichiersIntrouvables = 0;

        try (FileInputStream fis = new FileInputStream(excelPath.toFile());
                Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                System.err.println("[ERREUR] Aucune feuille trouvée dans le fichier Excel.");
                return;
            }

            Map<String, Integer> columnIndexes = getColumnIndexes(sheet.getRow(0));
            if (!columnIndexes.keySet().containsAll(
                    Arrays.asList("CodeJournal", "DateEdition", "NomFichierOriginal", "NouveauNomFichier"))) {
                System.err
                        .println("[ERREUR] Une ou plusieurs colonnes nécessaires sont absentes dans l'en-tête Excel.");
                return;
            }

            String currentJournal = "";
            String currentYear = "";
            Path currentDir = null;
            Map<String, Path> currentFilesMap = new HashMap<>();

            System.err.println("[OK] Renommage des fichiers...");
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                String codeJournal = getCellString(row.getCell(columnIndexes.get("CodeJournal"))).toLowerCase();
                String dateEditionStr = getCellString(row.getCell(columnIndexes.get("DateEdition")));
                String nomFichierOriginal = getCellString(row.getCell(columnIndexes.get("NomFichierOriginal")));
                String nouveauNomFichier = getCellString(row.getCell(columnIndexes.get("NouveauNomFichier")));

                if (codeJournal.isEmpty() || dateEditionStr.isEmpty() || nomFichierOriginal.isEmpty()
                        || nouveauNomFichier.isEmpty()) {
                    continue; // ligne invalide
                }

                // Parsing date
                LocalDate dateEdition;
                try {
                    dateEdition = LocalDate.parse(dateEditionStr);
                } catch (Exception e) {
                    System.err.printf("[WARN] Ligne %d : DateEdition invalide (%s)%n", i + 1, dateEditionStr);
                    continue;
                }

                String dossierJournal = codeJournal.equals("jds") ? "AS" : "JSS";
                String annee = String.valueOf(dateEdition.getYear());

                // Si journal ou année changent, on recharge la liste des fichiers
                if (!codeJournal.equals(currentJournal) || !annee.equals(currentYear)) {
                    currentJournal = codeJournal;
                    currentYear = annee;
                    currentDir = kiosqueRoot.resolve(Paths.get(dossierJournal, annee));
                    currentFilesMap = loadLowerCaseFilenameMap(currentDir);
                }

                Path originalPath = currentFilesMap.get(nomFichierOriginal.toLowerCase());
                if (originalPath != null && Files.exists(originalPath)) {
                    try {
                        Path targetPath = currentDir.resolve(nouveauNomFichier);
                        Files.move(originalPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        fichiersRenommes++;
                    } catch (IOException e) {
                        System.err.printf("[WARN] Ligne %d : Échec du renommage de %s → %s (%s)%n",
                                i + 1, nomFichierOriginal, nouveauNomFichier, e.getMessage());
                    }
                } else {
                    fichiersIntrouvables++;
                    // System.err.printf("[WARN] Ligne %d : Fichier non trouvé : %s%n", i + 1,
                    // nomFichierOriginal);
                }
            }

        } catch (IOException e) {
            System.err.println("[ERREUR] Erreur lors de la lecture du fichier Excel : " + e.getMessage());
        }

        System.out.printf("[FIN] %d fichiers renommés, %d fichiers introuvables.%n", fichiersRenommes,
                fichiersIntrouvables);
    }

    private Map<String, Integer> getColumnIndexes(Row headerRow) {
        Map<String, Integer> indexMap = new HashMap<>();
        if (headerRow == null)
            return indexMap;

        for (Cell cell : headerRow) {
            String value = cell.getStringCellValue().trim();
            indexMap.put(value, cell.getColumnIndex());
        }
        return indexMap;
    }

    private String getCellString(Cell cell) {
        if (cell == null)
            return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toLocalDate().toString()
                    : String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }

    private Map<String, Path> loadLowerCaseFilenameMap(Path dir) {
        Map<String, Path> map = new HashMap<>();
        if (!Files.isDirectory(dir))
            return map;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.pdf")) {
            for (Path file : stream) {
                map.put(file.getFileName().toString().toLowerCase(), file);
            }
        } catch (IOException e) {
            System.err.printf("[ERREUR] Impossible de lister les fichiers dans le dossier : %s (%s)%n", dir,
                    e.getMessage());
        }

        return map;
    }
}
