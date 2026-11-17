package com.jpmc.midascore.foundation;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    @JsonProperty("senderId")
    @JsonAlias({"fromUserId", "senderId"})
    private long senderId;

    @JsonProperty("recipientId")
    @JsonAlias({"toUserId", "recipientId"})
    private long recipientId;

    private float amount;

    public Transaction() {
    }

    public Transaction(long senderId, long recipientId, float amount) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getFromUserId() {
        return senderId;
    }

    public void setFromUserId(long fromUserId) {
        this.senderId = fromUserId;
    }

    public long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(long recipientId) {
        this.recipientId = recipientId;
    }

    public long getToUserId() {
        return recipientId;
    }

    public void setToUserId(long toUserId) {
        this.recipientId = toUserId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction {senderId=" + senderId + ", recipientId=" + recipientId + ", amount=" + amount + "}";
    }
}
