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
package de.inren.frontend.common.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import de.inren.frontend.common.dataprovider.RepositoryDataProvider;

/**
 * Sammlung von diversen Spaltentpen um auf einfache Weise eine Tabelle zu
 * erstellen. Die Tabellenspalten Überschriften werden als propertyname.label in
 * den Sprachdateien gesucht.
 * 
 * 
 * 
 * TODO Muss noch total überarbeitet werden. Im Moment Kraut und Rüben.
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * @author Ingo Renner
 */
public final class AjaxFallbackDefaultDataTableBuilder<T extends Serializable> implements Serializable {

    List<IColumn<T, String>> columns = new ArrayList<IColumn<T, String>>();

    private static final String LABEL = ".label";

    private final Component component;
    private ISortableDataProvider<T, String> dataProvider;
    private int numberOfRows = 10;

    /**
     * Wieviele Zeilen sollen dargestellt werden.
     * 
     * @param numberOfRows
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
        return this;
    }

    public AjaxFallbackDefaultDataTableBuilder(Component component) {
        this.component = component;
        columns = new ArrayList<IColumn<T, String>>();
    }

    /**
     * Fügt eine individuelle Spalte ein
     * 
     * @param abstractColumn
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> add(IColumn<T, String> column) {
        columns.add(column);
        return this;
    }

    public AjaxFallbackDefaultDataTableBuilder(final String id, final List<? extends IColumn<T, String>> columns,
            final ISortableDataProvider<T, String> dataProvider, final int rowsPerPage) {
        this.component = null;
    };

    public Component build(String id) {

        return new AjaxFallbackDefaultDataTable<T, String>(id, columns, dataProvider, numberOfRows).setOutputMarkupId(true);
    }

    /**
     * Fügt eine normale Spalte ein. Spaltenueberschrift wird ein
     * StringRescourceModel mit Key = property.label Inhalt ist ein
     * PropertyModel mit dem property
     * 
     * @param property
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> addPropertyColumn(String property) {
        return addPropertyColumn(property, false);
    }

    /**
     * Fügt eine normale Spalte ein. Spaltenueberschrift wird ein
     * StringRescourceModel mit Key = property.label Inhalt ist ein
     * PropertyModel mit dem property, bei sortable true, kann nach dem Property
     * sortiert werden.
     * 
     * @param property
     * @param sortable
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> addPropertyColumn(String property, boolean sortable) {
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

    /**
     * Die Datenquelle für die tabelle.
     * 
     * @param repositoryDataProvider
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> addDataProvider(RepositoryDataProvider<T> repositoryDataProvider) {
        this.dataProvider = repositoryDataProvider;
        return this;
    }

    public AjaxFallbackDefaultDataTableBuilder<T> addListProperty(String listProperty, String itemProperty) {
        ListColumn<T> listColumn = new ListColumn<T>(new StringResourceModel(listProperty + LABEL, component, null), listProperty, itemProperty);
        columns.add(listColumn);
        return this;
    }

    /**
     * Zeigt einen * an, wenn der Propertywert true ist.
     * 
     * @param property
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> addBooleanPropertyColumn(String property) {
        return addBooleanPropertyColumn(property, false);
    }

    /**
     * Zeigt einen + an, wenn der Propertywert true ist für false ein -.
     * 
     * @param property
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> addBooleanPropertyColumn(String property, boolean sortable) {
        columns.add(createBooleanPropertyColumn(property, sortable));
        return this;
    }

    private PropertyColumn<T, String> createBooleanPropertyColumn(final String property, boolean sortable) {
        if (sortable) {
            return new BooleanPropertyColumn<T>(new StringResourceModel(property + LABEL, component, null), property, property, property);
        } else {
            return new BooleanPropertyColumn<T>(new StringResourceModel(property + LABEL, component, null), property, property);
        }
    }

    private static final class BooleanPropertyColumn<T> extends PropertyColumn<T, String> {
        private final String property;

        private BooleanPropertyColumn(IModel<String> displayModel, String propertyExpression, String property) {
            super(displayModel, propertyExpression);
            this.property = property;
        }

        private BooleanPropertyColumn(IModel<String> displayModel, String sortProperty, String propertyExpression, String property) {
            super(displayModel, sortProperty, propertyExpression);
            this.property = property;
        }

        @Override
        public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
            PropertyModel<Boolean> model = new PropertyModel<Boolean>(rowModel, property);
            Boolean bool = model.getObject();
            // We tread null as false
            if (bool == null) {
                bool = Boolean.FALSE;
            }
            // item.add(bool ? new Label(componentId, "<span class=\"ui-icon ui-icon-check\"></span>")
            // .setEscapeModelStrings(false) : new Label(componentId, "<span>--</span>")
            // .setEscapeModelStrings(false));
            item.add(bool ? new Label(componentId, "<span>+</span>").setEscapeModelStrings(false) : new Label(componentId, "<span>-</span>")
                    .setEscapeModelStrings(false));
        }

    }

    /**
     * Fügt eine individuelle Spalte ein
     * 
     * @param abstractColumn
     * @return this for chaining
     */
    public AjaxFallbackDefaultDataTableBuilder<T> add(AbstractColumn<T, String> abstractColumn) {
        columns.add(abstractColumn);
        return this;
    }

}
