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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import de.inren.data.domain.banking.Account;
import de.inren.data.domain.banking.Category;
import de.inren.data.domain.banking.CategoryFilter;
import de.inren.data.domain.banking.Transaction;
import de.inren.data.domain.banking.TransactionCategoryFilterSpecification;
import de.inren.data.domain.banking.TransactionDateSpecification;
import de.inren.data.domain.tagging.Tag;
import de.inren.data.repositories.banking.AccountRepository;
import de.inren.data.repositories.banking.CategoryFilterRepository;
import de.inren.data.repositories.banking.CategoryRepository;
import de.inren.data.repositories.banking.TransactionRepository;
import de.inren.data.repositories.tagging.TagRepository;
import de.inren.service.banking.TransactionSummary.TransactionSummaryType;

@Service(value = "bankDataService")
@Transactional(readOnly = true)
public class BankDataServiceImpl implements BankDataService {
    private static final Sort        SORT_VALUTA_ASC = new Sort(Direction.ASC, "valutaDate");

    private final static Logger      log             = LoggerFactory.getLogger(BankDataServiceImpl.class);

    @Resource
    private AccountRepository        accountRepository;

    @Resource
    private TransactionRepository    transactionRepository;

    @Resource
    private CategoryRepository       categoryRepository;

    @Resource
    private TagRepository       tagRepository;

    @Resource
    private CategoryFilterRepository categoryFilterRepository;

    private boolean                  initDone        = false;

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
                    account.setOwner(record.get(1).trim());
                    break;
                case 3: // Konto
                    String[] vals = record.get(1).split(":");
                    account.setName(vals[0].trim());
                    account.setNumber(vals[1].trim());
                    account = validateAccount(account);
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
                    transaction.setAccountNumber(account.getNumber().trim());
                    transaction.setAccountingDate(getDate(record.get(0)));
                    transaction.setValutaDate(getDate(record.get(1)));
                    transaction.setPrincipal(record.get(2).trim());
                    transaction.setAccountingText(record.get(3).trim());
                    transaction.setPurpose(record.get(4).trim());
                    transaction.setAmount(getBigDecimal(record.get(5)));
                    transaction.setTransactionCurrency(record.get(6).trim());
                    transaction.setBalance(getBigDecimal(record.get(7)));
                    transaction.setBalanceCurrency(record.get(8).trim());
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
        } else {
            if (!account.getName().equals(entity.getName())) {
                entity.setName(account.getName());
                entity = accountRepository.save(entity);
            }
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

        List<Transaction> entities = transactionRepository.findAll(new TransactionCategoryFilterSpecification(categoryFilter),
                SORT_VALUTA_ASC);
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
        List<Transaction> entities = transactionRepository.findAll(new TransactionCategoryFilterSpecification(categoryFilter), SORT_VALUTA_ASC);
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
        List<Category> categories = categoryRepository.findByIncome(income);
        Set<String> categoryNames = new HashSet<String>();
        if (!income) {
            categoryNames.add("no category");
        }
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        List<Transaction> entities = transactionRepository.findAll(new TransactionDateSpecification(from, until), SORT_VALUTA_ASC);
        for (Transaction transaction : entities) {
            if (!income && StringUtils.isEmpty(transaction.getCategory())) {
                transaction.setCategory("no category");
            }
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
    public BalanceSummary loadBalanceSummary(Date from, Date until) {
        BalanceSummary balanceSummary = new BalanceSummary();
        balanceSummary.setFrom(from);
        balanceSummary.setUntil(until);
        Iterable<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            balanceSummary.getAccounts().add(account);
            List<Transaction> transactions = transactionRepository.findAll(new TransactionDateSpecification(from, until, account.getNumber()), SORT_VALUTA_ASC);
            if (!transactions.isEmpty()) {
                balanceSummary.getFromBalance().put(account.getNumber(), transactions.get(0).getBalance());
                balanceSummary.getUntilBalance().put(account.getNumber(), transactions.get(transactions.size() - 1).getBalance());
            } else {
                balanceSummary.getFromBalance().put(account.getNumber(), BigDecimal.ZERO);
                balanceSummary.getUntilBalance().put(account.getNumber(), BigDecimal.ZERO);
            }
        }
        return balanceSummary;
    }

    @Override
    public List<Category> findAllCategoriesForMonthReport() {
        return categoryRepository.findByMarksMonth(true);
    }

    @Override
    public List<Transaction> findTransactionsByCategory(Category category) {
        return transactionRepository.findAllByCategory(category.getName(), SortByValutaDateAsc());
    }

    private Sort SortByValutaDateAsc() {
        return new Sort(Sort.Direction.ASC, "valutaDate");
    }

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public void deleteTag(Tag tag) {
        tagRepository.delete(tag);
        
    }

    @Override
    public void applyTagToTransactions(Tag tag) {
        System.out.println("applyTagToTransactions(Tag "+tag+")");        
    }

    @Override
    public void removeTagFromTransactions(Tag tag) {
        System.out.println("removeTagFromTransactions(Tag "+tag+")");        
    }

    @Override
    public List<String> loadAllTagNames() {
        List<String> names = new ArrayList<>();
        Iterable<Tag> all = tagRepository.findAll();
        for (Tag tag : all) {
            names.add(tag.getName());
        }
        return names;
    }
}
