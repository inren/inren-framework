/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.inren.service.banking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.Resource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import de.inren.data.domain.banking.Account;
import de.inren.data.domain.banking.Category;
import de.inren.data.domain.banking.CategoryFilter;
import de.inren.data.domain.banking.Transaction;
import de.inren.data.domain.banking.TransactionCategoryFilterSpecification;
import de.inren.data.domain.banking.TransactionDateSpecification;
import de.inren.data.repositories.banking.AccountRepository;
import de.inren.data.repositories.banking.CategoryFilterRepository;
import de.inren.data.repositories.banking.CategoryRepository;
import de.inren.data.repositories.banking.TransactionRepository;
import de.inren.service.banking.TransactionSummary.TransactionSummaryType;

@Service(value = "bankDataService")
@Transactional(readOnly = true)
public class BankDataServiceImpl implements BankDataService {
    private final static Logger      log      = LoggerFactory.getLogger(BankDataServiceImpl.class);

    @Resource
    private AccountRepository        accountRepository;

    @Resource
    private TransactionRepository    transactionRepository;

    @Resource
    private CategoryRepository       categoryRepository;

    @Resource
    private CategoryFilterRepository categoryFilterRepository;

    private boolean                  initDone = false;

    @Override
    public void init() {
        if (!initDone) {
            log.info("BankDataService init start.");
            initDone = true;
            log.info("BankDataService init done.");
        }
    }

    @Override
    public void importTransactionCsv(byte[] bytes) throws IOException {
        Iterable<CSVRecord> records = getIngDibaCsvFormat().parse(createReader(bytes));
        Account account = new Account();
        for (CSVRecord record : records) {
            switch ((int) record.getRecordNumber()) {
                case 1: // Umsatzanzeige
                    break;
                case 2: // Kunde
                    account.setOwner(record.get(1));
                    break;
                case 3: // Konto
                    String line = record.get(1);
                    if (line.startsWith("Girokonto: ")) {
                        account.setNumber(line.substring("Girokonto: ".length()));
                        account = validateAccount(account);
                    } else {
                        throw new IllegalStateException("Can't interpret line:" + line);
                    }
                    break;
                case 4: //
                    break;
                case 5: // Zeitraum
                    break;
                case 6: // Saldo
                    break;
                case 7: // Leer
                    break;
                case 8: // Überschrift
                    break;
                default: // Eintrag
                    Transaction transaction = new Transaction();
                    transaction.setAccountNumber(account.getNumber());
                    transaction.setAccountingDate(getDate(record.get(0)));
                    transaction.setValutaDate(getDate(record.get(1)));
                    transaction.setPrincipal(record.get(2));
                    transaction.setAccountingText(record.get(3));
                    transaction.setPurpose(record.get(4));
                    transaction.setAmount(getBigDecimal(record.get(5)));
                    transaction.setTransactionCurrency(record.get(6));
                    transaction.setBalance(getBigDecimal(record.get(7)));
                    transaction.setBalanceCurrency(record.get(8));
                    transaction.setHashCode(transaction.createHashCode());
                    Transaction oldTransaction = transactionRepository.findByHashCode(transaction.getHashCode());
                    // only save new transactions
                    if (oldTransaction == null) {
                        transactionRepository.save(transaction);
                    }
            }
        }
        // Add the categories to the new (all) transactions. Should be
        // optimized.
        Iterable<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            applyCategoryToTransactions(category);
        }
    }

    private BigDecimal getBigDecimal(String value) {
        value = value.replace(".", "");
        value = value.replace(',', '.');
        return BigDecimal.valueOf(Double.parseDouble(value));
    }

    private Date getDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return formatter.parse(dateStr.trim());
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }

    }

    private Account validateAccount(Account account) {
        Account entity = accountRepository.findByNumber(account.getNumber());
        if (entity == null) {
            entity = accountRepository.save(account);
        }
        return entity;
    }

    @Override
    public void importFinanceCsv(byte[] bytes) throws IOException {
        Iterable<CSVRecord> records = getIngDibaCsvFormat().parse(createReader(bytes));
        for (CSVRecord record : records) {
            log.info(record.toString());
        }
    }

    private Reader createReader(byte[] bytes) throws UnsupportedEncodingException {
        String csv = new String(bytes, "ISO-8859-15");
        Reader reader = new BufferedReader(new StringReader(csv));
        return reader;
    }

    private CSVFormat getIngDibaCsvFormat() {
        return CSVFormat.DEFAULT.withDelimiter(';');
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public CategoryFilter save(CategoryFilter categoryFilter) {
        categoryFilter = categoryFilterRepository.save(categoryFilter);
        applyCategoryToTransactions(categoryFilter);
        return categoryFilter;
    }

    @Override
    public List<String> getCategoryNames() {
        List<String> result = new ArrayList<String>();
        Iterable<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            result.add(category.getName());
        }
        return result;
    }

    @Override
    public void removeCategoryFromTransactions(CategoryFilter categoryFilter) {
        List<Transaction> entities = transactionRepository.findAll(new TransactionCategoryFilterSpecification(categoryFilter));
        Iterator<Transaction> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Transaction transaction = (Transaction) iterator.next();
            if (transaction.isCategoryFixed()) {
                iterator.remove();
            } else {
                transaction.setCategory(null);
            }

        }
        transactionRepository.save(entities);
    }

    @Override
    public void applyCategoryToTransactions(CategoryFilter categoryFilter) {
        List<Transaction> entities = transactionRepository.findAll(new TransactionCategoryFilterSpecification(categoryFilter));
        Iterator<Transaction> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Transaction transaction = (Transaction) iterator.next();
            if (transaction.isCategoryFixed()) {
                iterator.remove();
            } else {
                transaction.setCategory(categoryFilter.getCategory().getName());
            }
        }
        transactionRepository.save(entities);
    }

    @Override
    public List<Category> findAllCategories() {
        List<Category> categories = new ArrayList<Category>();
        Iterable<Category> entities = categoryRepository.findAll();
        for (Category category : entities) {
            categories.add(category);
        }
        return categories;
    }

    @Override
    public void applyCategoryToTransactions(Category category) {
        for (CategoryFilter categoryFilter : category.getFilter()) {
            List<Transaction> entities = transactionRepository.findAll(new TransactionCategoryFilterSpecification(categoryFilter));
            for (Transaction transaction : entities) {
                transaction.setCategory(category.getName());
            }
            transactionRepository.save(entities);
        }

    }

    @Override
    public void removeCategoryFromTransactions(Category category) {
        List<Transaction> entities = transactionRepository.findAllByCategory(category.getName());
        for (Transaction transaction : entities) {
            transaction.setCategory(null);
        }
        transactionRepository.save(entities);
    }

    @Override
    public void deleteCategory(Category category) {
        categoryFilterRepository.delete(category.getFilter());
        removeCategoryFromTransactions(category);
        categoryRepository.delete(category);
    }

    @Override
    public Category save(Category entity) {
        return categoryRepository.save(entity);
    }

    @Override
    public Collection<TransactionSummary> calculateTransactionSummary(TransactionSummaryType transactionSummaryType, Date from, @Nullable Date until) {
        if (transactionSummaryType.name().equals(TransactionSummaryType.ALL.name())) {
            Map<String, TransactionSummary> summery = new HashMap<String, TransactionSummary>();
            List<Transaction> entities = transactionRepository.findAll(new TransactionDateSpecification(from, until));
            for (Transaction transaction : entities) {
                if (StringUtils.isEmpty(transaction.getCategory())) {
                    transaction.setCategory("no category");
                }
                if (summery.containsKey(transaction.getCategory())) {
                    TransactionSummary sum = summery.get(transaction.getCategory());
                    sum.addTransaction(transaction);
                    log.info(String.valueOf(transaction.getAmount()) + "=>" + sum.toString());
                } else {
                    summery.put(transaction.getCategory(),
                            new TransactionSummary(transaction.getCategory(), transaction.getAmount(), transaction.getBalanceCurrency(), transaction));
                }
            }
            return summery.values();
        } else {
            return calculateTransactionSummary(transactionSummaryType.name().equals(TransactionSummaryType.INCOME.name()), from, until);
        }
    }

    private Collection<TransactionSummary> calculateTransactionSummary(boolean income, Date from, @Nullable Date until) {
        Map<String, TransactionSummary> summery = new HashMap<String, TransactionSummary>();
        List<Category> categories = categoryRepository.findByOnlyTopAndIncome(true, income);
        Set<String> categoryNames = new HashSet<String>();
        for (Category category : categories) {
            fillCategoryNames(categoryNames, category);
        }

        List<Transaction> entities = transactionRepository.findAll(new TransactionDateSpecification(from, until));
        for (Transaction transaction : entities) {
            if (categoryNames.contains(transaction.getCategory())) {
                if (summery.containsKey(transaction.getCategory())) {
                    TransactionSummary sum = summery.get(transaction.getCategory());
                    sum.addTransaction(transaction);
                    log.info(String.valueOf(transaction.getAmount()) + "=>" + sum.toString());
                } else {
                    summery.put(transaction.getCategory(),
                            new TransactionSummary(transaction.getCategory(), transaction.getAmount(), transaction.getBalanceCurrency(), transaction));
                }
            }
        }
        return summery.values();
    }

    private void fillCategoryNames(Set<String> categoryNames, Category category) {
        categoryNames.add(category.getName());
        if (category.getSubCategories() != null) {
            for (Category subCategory : category.getSubCategories()) {
                fillCategoryNames(categoryNames, subCategory);
            }
        }

    }

    @Override
    public List<PrincipalInfo> getPrincipalInfo() {
        Map<String, PrincipalInfo> infoMap = new HashMap<String, PrincipalInfo>();
        Iterable<Transaction> all = transactionRepository.findAll();
        for (Transaction transaction : all) {
            PrincipalInfo info;
            if (infoMap.containsKey(transaction.getPrincipal())) {
                info = infoMap.get(transaction.getPrincipal());
                info.setCount(info.getCount() + 1);
                info.setFiltered(info.isFiltered() || !StringUtils.isEmpty(transaction.getCategory()));
            } else {
                infoMap.put(transaction.getPrincipal(), new PrincipalInfo(transaction.getPrincipal(), 1, !StringUtils.isEmpty(transaction.getCategory())));
            }
        }
        return new ArrayList<PrincipalInfo>(infoMap.values());
    }

    @Override
    public List<Category> loadAvailableSubCategories(Category category) {
        List<Category> categories = categoryRepository.findByParentCategoryAndOnlyTop(null, false);
        if (category != null) {
            Iterator<Category> categoriesIterator = categories.iterator();

            while (categoriesIterator.hasNext()) {
                Category aCategory = (Category) categoriesIterator.next();
                if (aCategory.equals(category)) {
                    categoriesIterator.remove();
                    break;
                }
            }

        }
        return categories;
    }

    @Override
    public void calculateyearlyReview(Integer year) {
        for (int month = 0; month < 12; month++) {

        }

    }
}
