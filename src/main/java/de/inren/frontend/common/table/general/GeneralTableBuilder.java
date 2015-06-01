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
package de.inren.frontend.common.table.general;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.StringResourceModel;

import de.inren.frontend.common.dataprovider.RepositoryDataProvider;
import de.inren.frontend.common.table.ITableBuilder;
import de.inren.frontend.common.table.ListColumn;

/**
 * The new universal version of the table builder.
 * 
 * 
 * @author Ingo Renner
 *
 */
public class GeneralTableBuilder<T extends Serializable> implements ITableBuilder<T> {

    /** For a column with property name the description is found as name.label. */
    private static final String            LABEL = ".label";

    /** List of the rows. */
    private final List<IColumn<T, String>> columns;

    /** The component holding the table. Used for property resolving. */
    private final Component                component;

    /** Dataprovider for the table */
    private IDataProvider<T>               dataProvider;

    /** Default number of rows to display in the table. */
    private int                            numberOfRows;

    public GeneralTableBuilder(Component component) {
        this.component = component;
        this.columns = new ArrayList<IColumn<T, String>>();
        this.numberOfRows = 10;
    }

    @Override
    public Component build(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ITableBuilder<T> setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows > 0 ? numberOfRows : 10;
        return this;
    }

    @Override
    public ITableBuilder<T> add(IColumn<T, String> column) {
        this.columns.add(column);
        return this;
    }

    @Override
    @Deprecated
    public ITableBuilder<T> add(AbstractColumn<T, String> abstractColumn) {
        return add((IColumn<T, String>) abstractColumn);
    }

    @Override
    public ITableBuilder<T> addPropertyColumn(String property) {
        return addPropertyColumn(property, false);
    }

    @Override
    public ITableBuilder<T> addPropertyColumn(String property, boolean sortable) {
        columns.add(createPropertyColumn(property, sortable));
        return this;
    }

    private PropertyColumn<T, String> createPropertyColumn(String property, boolean sortable) {
        if (sortable) {
            return new PropertyColumn<T, String>(new StringResourceModel(property + LABEL, component, null), property, property);
        } else {
            return new PropertyColumn<T, String>(new StringResourceModel(property + LABEL, component, null), property);
        }
    }

    @Override
    public ITableBuilder<T> addDataProvider(RepositoryDataProvider<T> repositoryDataProvider) {
        this.dataProvider = repositoryDataProvider;
        return this;
    }

    @Override
    public ITableBuilder<T> addListProperty(String listProperty, String itemProperty) {
        ListColumn<T> listColumn = new ListColumn<T>(new StringResourceModel(listProperty + LABEL, component, null), listProperty, itemProperty);
        columns.add(listColumn);
        return this;
    }

    @Override
    public ITableBuilder<T> addBooleanPropertyColumn(String property) {
        return addBooleanPropertyColumn(property, false);
    }

    @Override
    public ITableBuilder<T> addBooleanPropertyColumn(String property, boolean sortable) {
        columns.add(createBooleanPropertyColumn(property, sortable));
        return this;
    }

    private PropertyColumn<T, String> createBooleanPropertyColumn(final String property, boolean sortable) {
        if (sortable) {
            return new BooleanColumn<T>(new StringResourceModel(property + LABEL, component, null), property, property, property);
        } else {
            return new BooleanColumn<T>(new StringResourceModel(property + LABEL, component, null), property, property);
        }
    }

    @Override
    public ITableBuilder<T> addMoneyPropertyColumn(String property) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ITableBuilder<T> addMoneyPropertyColumn(String property, boolean sortable) {
        // TODO Auto-generated method stub
        return null;
    }

}
