/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.inren.data.domain.banking;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.wicket.util.string.Strings;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.inren.data.domain.core.DomainObject;

/**
 * @author Ingo Renner
 *
 */
@Entity(name = "btransaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Transaction extends DomainObject {

    @Column(unique = true, nullable = false)
    private String     hashCode;
    @Column(nullable = true)
    private String     category;
    private boolean    categoryFixed;
    @Column(nullable = true)
    private String     tags;
    @Column(nullable = false)
    private String     accountNumber;
    @Column(nullable = false)
    private Date       accountingDate;
    @Column(nullable = false)
    private Date       valutaDate;
    @Column(nullable = false)
    private String     principal;
    @Column(nullable = false)
    private String     accountingText;
    @Column(nullable = false)
    private String     purpose;
    private BigDecimal amount;
    @Column(nullable = false)
    private String     transactionCurrency;
    private BigDecimal balance;
    @Column(nullable = false)
    private String     balanceCurrency;

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Date getAccountingDate() {
        return new Date(accountingDate.getTime());
    }

    public void setAccountingDate(Date accountingDate) {
        if (accountingDate == null) {
            this.accountingDate = null;
        } else {
            this.accountingDate = new Date(accountingDate.getTime());
        }

    }

    public Date getValutaDate() {
        return new Date(valutaDate.getTime());
    }

    public void setValutaDate(Date valutaDate) {
        if (valutaDate == null) {
            this.valutaDate = null;
        } else {
            this.valutaDate = new Date(valutaDate.getTime());
        }
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getAccountingText() {
        return accountingText;
    }

    public void setAccountingText(String accountingText) {
        this.accountingText = accountingText;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getTransactionCurrency() {
        return transactionCurrency;
    }

    public void setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }

    public String getBalanceCurrency() {
        return balanceCurrency;
    }

    public void setBalanceCurrency(String balanceCurrency) {
        this.balanceCurrency = balanceCurrency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isCategoryFixed() {
        return categoryFixed;
    }

    public void setCategoryFixed(boolean categoryFixed) {
        this.categoryFixed = categoryFixed;
    }

    public String getDisplayValue() {

        if (!Strings.isEmpty(getPrincipal())) {
            return getPrincipal();
        } else {
            if (!Strings.isEmpty(getAccountingText())) {
                return getAccountingText();
            }
        }
        return getPurpose();

    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<String> getTagList() {
        if(tags!=null) {
            return Arrays.asList(tags.split(":"));            
        }
        return new ArrayList<>();
    }

    public void setTagList(List<String> tagList) {
        StringBuilder sb = new StringBuilder();
        String sep ="";
        for (String tag : tagList) {
            sb.append(sep).append(tag);
            sep =":";
        }
        this.tags = sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Transaction [hashCode=").append(hashCode).append(", category=").append(category).append(", categoryFixed=").append(categoryFixed)
                .append(", tags=").append(tags).append(", accountNumber=").append(accountNumber).append(", accountingDate=").append(accountingDate)
                .append(", valutaDate=").append(valutaDate).append(", principal=").append(principal).append(", accountingText=").append(accountingText)
                .append(", purpose=").append(purpose).append(", amount=").append(amount).append(", transactionCurrency=").append(transactionCurrency)
                .append(", balance=").append(balance).append(", balanceCurrency=").append(balanceCurrency).append("]");
        return builder.toString();
    }

    /**
     * Hash to identify transaction which have already been imported.
     * 
     */
    public String createHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountNumber == null) ? 0 : accountNumber.hashCode());
        result = prime * result + ((accountingDate == null) ? 0 : accountingDate.hashCode());
        result = prime * result + ((accountingText == null) ? 0 : accountingText.hashCode());
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((balance == null) ? 0 : balance.hashCode());
        result = prime * result + ((balanceCurrency == null) ? 0 : balanceCurrency.hashCode());
        result = prime * result + ((principal == null) ? 0 : principal.hashCode());
        result = prime * result + ((purpose == null) ? 0 : purpose.hashCode());
        result = prime * result + ((transactionCurrency == null) ? 0 : transactionCurrency.hashCode());
        result = prime * result + ((valutaDate == null) ? 0 : valutaDate.hashCode());
        return String.valueOf(result);
    }

}
