package com.jpmc.midascore.service;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.component.IncentiveService;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final DatabaseConduit databaseConduit;
    private final IncentiveService incentiveService;

    public TransactionService(DatabaseConduit databaseConduit, IncentiveService incentiveService) {
        this.databaseConduit = databaseConduit;
        this.incentiveService = incentiveService;
    }

    @Transactional
    public boolean processTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }

        long senderId = transaction.getSenderId();
        long recipientId = transaction.getRecipientId();
        float amount = transaction.getAmount();

        if (amount <= 0) {
            logger.warn("Transaction rejected: non-positive amount {}", amount);
            return false;
        }

        UserRecord sender = databaseConduit.findById(senderId);
        UserRecord recipient = databaseConduit.findById(recipientId);

        if (sender == null) {
            logger.warn("Transaction rejected: sender with id {} does not exist", senderId);
            return false;
        }

        if (recipient == null) {
            logger.warn("Transaction rejected: recipient with id {} does not exist", recipientId);
            return false;
        }

        if (sender.getBalance() < amount) {
            logger.warn("Transaction rejected: sender {} insufficient balance (balance={}, amount={})",
                    senderId, sender.getBalance(), amount);
            return false;
        }

        // Call the incentive service
        Incentive incentive = incentiveService.getIncentive(transaction);
        float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;

        // Deduct transaction amount from sender
        sender.setBalance(sender.getBalance() - amount);

        // Add transaction amount + incentive to recipient
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        // Persist both updated balances
        databaseConduit.save(sender);
        databaseConduit.save(recipient);

        logger.info("Transaction completed: senderId={}, recipientId={}, amount={}, incentive={}",
                senderId, recipientId, amount, incentiveAmount);

        return true;
    }
}
