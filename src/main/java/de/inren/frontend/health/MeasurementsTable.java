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
package de.inren.frontend.health;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.CSVDataExporter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.ExportToolbar;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import de.inren.data.domain.health.Measurement;
import de.inren.data.repositories.health.MeasurementRepository;
import de.inren.frontend.common.dataprovider.RepositoryDataProvider;
import de.inren.service.health.MeasurementService;

/**
 * @author Ingo Renner
 * 
 */
public class MeasurementsTable extends Panel {
    @SpringBean
    private MeasurementRepository measurementRepository;
    
    @SpringBean
    private MeasurementService measurementService;

    public MeasurementsTable(String id) {
	super(id);
    }

    @Override
    protected void onInitialize() {
	super.onInitialize();
	if (!hasBeenRendered()) {
	    initGui();
	}
    }

    private void initGui() {

	List<IColumn<Measurement, String>> columns = new ArrayList<IColumn<Measurement, String>>();

	columns.add(new HealthColumn<Measurement>(new Model<String>("weight"), "weight", "weight"));
	columns.add(new HealthColumn<Measurement>(new Model<String>("fat"), "fat", "fat"));
	columns.add(new HealthColumn<Measurement>(new Model<String>("water"), "water","water"));
	columns.add(new HealthColumn<Measurement>(new Model<String>("muscle"), "muscle","muscle"));
	columns.add(new HealthColumn<Measurement>(new Model<String>("bone"), "bone","bone"));

	ISortableDataProvider<Measurement, String> dataProvider = new RepositoryDataProvider<Measurement>(measurementRepository);
	
	AjaxFallbackDefaultDataTable<Measurement, String> table = new AjaxFallbackDefaultDataTable<Measurement, String>("datatable", columns, dataProvider, 3);
	table.addBottomToolbar(new ExportToolbar(table).addDataExporter(new CSVDataExporter()));
	table.add(new TableBehavior().bordered().condensed().striped());
	add(table);
    }

}
