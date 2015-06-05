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
package de.inren.frontend.banking.review;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.inren.frontend.banking.common.TransactionSummaryListPanel;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.MoneyPanel;
import de.inren.service.banking.BankDataService;
import de.inren.service.banking.TransactionSummary;
import de.inren.service.banking.TransactionSummary.TransactionSummaryType;

/**
 * @author Ingo Renner
 *
 */
public class MonthlyReviewPanel extends ABasePanel<Void> {

    @SpringBean
    private BankDataService               bankDataService;

    private IModel<Integer>               yearModel;
    private IModel<Integer>               monthModel;
    private IModel<Date>                  startDateModel;
    private IModel<Date>                  endDateModel;

    private ListModel<TransactionSummary> incomeModel;
    private ListModel<TransactionSummary> expensesModel;

    public MonthlyReviewPanel(String id) {
        super(id);
        initDateModels();
    }

    private void initDateModels() {
        Calendar cal = Calendar.getInstance();
        yearModel = Model.of(Integer.valueOf(cal.get(Calendar.YEAR)));
        monthModel = Model.of(Integer.valueOf(cal.get(Calendar.MONTH)));
        startDateModel = new AbstractReadOnlyModel<Date>() {

            @Override
            public Date getObject() {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, yearModel.getObject());
                cal.set(Calendar.MONTH, monthModel.getObject());
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTime();
            }
        };

        endDateModel = new AbstractReadOnlyModel<Date>() {

            @Override
            public Date getObject() {
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDateModel.getObject());
                cal.add(Calendar.MONTH, 1);
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                cal.add(Calendar.SECOND, -1);
                return cal.getTime();
            }
        };
        incomeModel = new ListModel<TransactionSummary>(new ArrayList<TransactionSummary>());
        expensesModel = new ListModel<TransactionSummary>(new ArrayList<TransactionSummary>());
        recalculate();
    }

    private void recalculate() {
        incomeModel.getObject().clear();
        incomeModel.getObject().addAll(
                bankDataService.calculateTransactionSummary(TransactionSummaryType.INCOME, startDateModel.getObject(), endDateModel.getObject()));
        expensesModel.getObject().clear();
        expensesModel.getObject().addAll(
                bankDataService.calculateTransactionSummary(TransactionSummaryType.EXPENSE, startDateModel.getObject(), endDateModel.getObject()));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final Component incomeTable = getListPanel("income", incomeModel);
        final Component expensesTable = getListPanel("expenses", expensesModel);
        final Component incomeTotalLabel = new MoneyPanel("incomeTotal", createIncomeSummaryModel()).setOutputMarkupId(true);
        final Component expensesTotalLabel = new MoneyPanel("expensesTotal", createExpensesSummaryModel()).setOutputMarkupId(true);
        final Component monthTotalLabel = new MoneyPanel("monthTotal", createMonthTotalSummaryModel()).setOutputMarkupId(true);

        Form<Void> form = new Form<Void>("form");
        // Year
        StringResourceModel lYear = new StringResourceModel("year.label", MonthlyReviewPanel.this, null);
        form.add(new Label("year.label", lYear));
        DropDownChoice<Integer> yearChoice = new DropDownChoice<Integer>("year", yearModel, calculateYearChoice());
        yearChoice.setNullValid(false);
        form.add(yearChoice);
        // Month
        StringResourceModel lMonth = new StringResourceModel("month.label", MonthlyReviewPanel.this, null);
        form.add(new Label("month.label", lMonth));
        DropDownChoice<Integer> monthChoice = new DropDownChoice<Integer>("month", monthModel, calculateMonthChoice());
        monthChoice.setChoiceRenderer(new IChoiceRenderer<Integer>() {

            @Override
            public Object getDisplayValue(Integer object) {
                return getString("month." + object + ".label");
            }

            @Override
            public String getIdValue(Integer object, int index) {
                return String.valueOf(object);
            }
        });

        monthChoice.setNullValid(false);
        form.add(monthChoice);
        // calculate new
        form.add(new AjaxButton("calculate") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    recalculate();
                    target.add(incomeTable);
                    target.add(incomeTotalLabel);
                    target.add(expensesTable);
                    target.add(expensesTotalLabel);
                    target.add(monthTotalLabel);

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    form.error(new StringResourceModel("TODO", MonthlyReviewPanel.this, null).getString());
                    target.add(getFeedback());
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                FeedbackPanel f = getFeedback();
                if (target != null && f != null) {
                    target.add(f);
                }
            }
        });

        form.add(incomeTable);
        form.add(new Label("incomeTotal.label", new StringResourceModel("incomeTotal.label", MonthlyReviewPanel.this, null)));
        form.add(incomeTotalLabel);

        form.add(expensesTable);
        form.add(new Label("expensesTotal.label", new StringResourceModel("expensesTotal.label", MonthlyReviewPanel.this, null)));
        form.add(expensesTotalLabel);

        form.add(new Label("monthTotal.label", new StringResourceModel("monthTotal.label", MonthlyReviewPanel.this, null)));
        form.add(monthTotalLabel);

        add(form);

    }

    private Component getListPanel(String id, ListModel<TransactionSummary> model) {
        return new TransactionSummaryListPanel(id, model).setOutputMarkupId(true);
    }

    private IModel<BigDecimal> createMonthTotalSummaryModel() {
        return new AbstractReadOnlyModel<BigDecimal>() {

            @Override
            public BigDecimal getObject() {
                BigDecimal sum = calculateTotal(incomeModel).add(calculateTotal(expensesModel));
                return sum;
            }

        };
    }

    private IModel<BigDecimal> createIncomeSummaryModel() {
        return new AbstractReadOnlyModel<BigDecimal>() {

            @Override
            public BigDecimal getObject() {
                BigDecimal sum = calculateTotal(incomeModel);
                return sum;
            }

        };
    }

    private IModel<BigDecimal> createExpensesSummaryModel() {
        return new AbstractReadOnlyModel<BigDecimal>() {

            @Override
            public BigDecimal getObject() {
                BigDecimal sum = calculateTotal(expensesModel);
                return sum;
            }

        };
    }

    private BigDecimal calculateTotal(ListModel<TransactionSummary> transactionSummaryModel) {
        BigDecimal total = BigDecimal.ZERO;
        for (TransactionSummary transactionSummary : transactionSummaryModel.getObject()) {
            total = total.add(transactionSummary.getSum());
        }
        return total;
    }

    private List<Integer> calculateMonthChoice() {
        return Arrays.asList(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 });
    }

    private List<Integer> calculateYearChoice() {
        return Arrays.asList(new Integer[] { 2014, 2015 });
    }

    private SortableDataProvider<TransactionSummary, String> getDataProvider(ListModel<TransactionSummary> listModel) {
        return new SortableDataProvider<TransactionSummary, String>() {

            @Override
            public Iterator<? extends TransactionSummary> iterator(long first, long count) {
                return listModel.getObject().subList((int) first, (int) first + (int) count).iterator();
            }

            @Override
            public long size() {
                return listModel.getObject().size();
            }

            @Override
            public IModel<TransactionSummary> model(TransactionSummary object) {
                return Model.of(object);
            }

        };
    }

}
