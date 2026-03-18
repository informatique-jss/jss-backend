package com.jss.osiris.libs.mail;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.model.MailTracker;
import com.jss.osiris.libs.mail.repository.MailTrackerRepository;

import jakarta.mail.Address;
import jakarta.mail.FetchProfile;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.UIDFolder;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.ReceivedDateTerm;
import jakarta.mail.search.SearchTerm;

@Service
public class MailTrackerDelegate {

    @Autowired
    MailSharedDelegate mailSharedDelegate;

    @Autowired
    private MailTrackerRepository repository;

    public void performFullScan() throws MessagingException, OsirisException {
        mailSharedDelegate.executeWithLock(() -> {
            LocalDateTime currentScanTime = LocalDateTime.now();
            scanAllFolders(mailSharedDelegate.getFolderClassement(), currentScanTime);
            markExitedMailsInJava(currentScanTime);
            return null;
        });
    }

    private void scanAllFolders(Folder rootFolder, LocalDateTime scanTime) throws MessagingException {
        if ((rootFolder.getType() & Folder.HOLDS_FOLDERS) != 0) {
            for (Folder subFolder : rootFolder.list()) {
                scanAllFolders(subFolder, scanTime);
            }
        }

        if ((rootFolder.getType() & Folder.HOLDS_MESSAGES) != 0) {
            processFolderMessages(rootFolder, scanTime);
        }
    }

    private void processFolderMessages(Folder folder, LocalDateTime scanTime) throws MessagingException {
        try {
            if (!folder.isOpen())
                folder.open(Folder.READ_ONLY);
            UIDFolder uidFolder = (UIDFolder) folder;
            Message[] messages;

            if (folder.getName().contains("Trait")) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, -7);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                Date thresholdDate = cal.getTime();

                SearchTerm newerThanTerm = new ReceivedDateTerm(ComparisonTerm.GE, thresholdDate);

                messages = folder.search(newerThanTerm);
            } else {
                messages = folder.getMessages();

                FetchProfile fp = new FetchProfile();
                fp.add(FetchProfile.Item.ENVELOPE);
                folder.fetch(messages, fp);
            }

            String currentFolderPath = folder.getFullName();

            for (Message msg : messages) {
                long uid = uidFolder.getUID(msg);

                MailTracker activeRecord = repository.findByUidAndExitDateIsNull(uid);

                if (activeRecord == null) {
                    createNewRecord(uid, msg.getSubject(), currentFolderPath, scanTime, msg.getReceivedDate(),
                            String.join(" / ", Arrays.asList(msg.getFrom()).stream().map(Address::toString).toList()));
                } else {
                    if (activeRecord.getCurrentFolder().equals(currentFolderPath)) {
                        activeRecord.setLastSeen(scanTime);
                        repository.save(activeRecord);
                    } else {
                        activeRecord.setExitDate(scanTime);
                        repository.save(activeRecord);

                        createNewRecord(uid, msg.getSubject(), currentFolderPath, scanTime, msg.getReceivedDate(),
                                String.join(" / ",
                                        Arrays.asList(msg.getFrom()).stream().map(Address::toString).toList()));
                    }
                }
            }
        } finally {
            if (folder.isOpen()) {
                folder.close(false);
            }
        }
    }

    private void createNewRecord(long uid, String subject, String folderName, LocalDateTime scanTime,
            Date receivedDate, String fromRecipient) {
        MailTracker tracker = new MailTracker();
        tracker.setUid(uid);
        tracker.setSubject(subject);
        tracker.setCurrentFolder(folderName);
        tracker.setEntryDate(scanTime);
        tracker.setLastSeen(scanTime);
        tracker.setFromRecipient(fromRecipient);
        tracker.setReceivedDate(receivedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        repository.save(tracker);
    }

    private void markExitedMailsInJava(LocalDateTime currentScanTime) {
        List<MailTracker> activeMails = repository.findByExitDateIsNull();

        for (MailTracker mail : activeMails) {
            if (mail.getLastSeen().isBefore(currentScanTime)) {
                mail.setExitDate(currentScanTime);
                repository.save(mail);
            }
        }
    }
}