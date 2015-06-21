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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.inren.data.domain.banking.Category;
import de.inren.testsupport.InRenJUnit4SpringContextTests;

/**
 * @author Ingo Renner
 *
 */
public class BankDataServiceTest extends InRenJUnit4SpringContextTests {

    private static final Logger log = Logger.getLogger(BankDataServiceTest.class);
    @Autowired
    BankDataService             bankDataService;

    @Before
    public void setUp() {
        log.setLevel(Level.DEBUG);
        assertNotNull(bankDataService);
        ((BankDataService) bankDataService).init();
    }

    @Test
    public void testCategorySave() {
        Category category = createCategory("C1", true, "D1");
        Category saved = bankDataService.save(category);
        List<Category> categories = bankDataService.findAllCategories();
        assertEquals(1, categories.size());
        assertEquals(saved.getId(), categories.get(0).getId());
    }

    private Category createCategory(String name, boolean income, String description) {
        Category category = new Category();
        category.setName(name);
        category.setIncome(income);
        category.setDescription(description);
        return category;
    }
}
