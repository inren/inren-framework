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
package de.inren.frontend.banking.review;

import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.inren.frontend.common.panel.ABasePanel;
import de.inren.service.banking.BankDataService;

/**
 * @author Ingo Renner
 *
 */
public class YearlyReviewPanel extends ABasePanel<Integer> {

    @SpringBean
    private BankDataService bankDataService;

    public YearlyReviewPanel(String id, IModel<Integer> yearModel) {
        super(id, yearModel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        bankDataService.calculateyearlyReview((Integer) getDefaultModel().getObject());
    }
}
