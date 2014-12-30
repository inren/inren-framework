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
package de.inren.frontend.storehouse;

import java.util.List;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import de.inren.data.domain.folder.Folder;
import de.inren.data.domain.folder.FolderType;
import de.inren.data.domain.picture.Picture;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.service.folder.FolderService;
import de.inren.service.picture.PictureService;
import de.inren.service.storehouse.StorehouseService;

/**
 * @author Ingo Renner
 *
 */
public class ActionPanel extends ABasePanel<Folder> {

    @SpringBean
    private FolderService     folderService;

    @SpringBean
    private StorehouseService storehouseService;

    @SpringBean
    private PictureService    pictureService;

    public ActionPanel(String id, IModel<Folder> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        // Create Folder
        add(createAddFolder("addFolder"));

        // upload file
        addUpload();
    }

    private Form<String> createAddFolder(String id) {

        final Form<String> form = new Form<String>(id, Model.of(""));
        form.add(new TextField<String>("name", form.getModel()));
        form.add(new Button("submit", Model.of("add")) {
            @Override
            public void onSubmit() {
                String fname = form.getModel().getObject();
                if (!Strings.isEmpty(fname)) {
                    Folder srcFolder = ActionPanel.this.getModel().getObject();
                    Folder folder = new Folder();
                    folder.setName(fname);
                    folder.setRoles(srcFolder.getGrantedRoles());
                    folder.setType(FolderType.FOLDER);
                    folder = folderService.saveFolder(folder);
                    folderService.insertIntoFolder(srcFolder, folder);
                    PageParameters parameters = new PageParameters();
                    parameters.add("id", folder.getId());
                    throw new RestartResponseException(FileManagerPage.class, parameters);
                }
            }
        });
        return form;
    }

    private void addUpload() {
        // Add one file input field
        FileUploadField fileUploadField = new FileUploadField("fileInput");
        final Form progressUploadForm = new Form("restore") {
            @Override
            protected void onSubmit() {
                final List<FileUpload> uploads = fileUploadField.getFileUploads();
                if (uploads != null) {
                    for (FileUpload upload : uploads) {

                        try {
                            String digest = storehouseService.doImport(upload.getInputStream(), upload.getMD5());
                            Picture picture = new Picture();
                            picture.setDigest(digest);
                            picture.setFilename(upload.getClientFileName());
                            picture = pictureService.savePicture(picture);
                            Folder srcFolder = ActionPanel.this.getModel().getObject();
                            folderService.insertIntoFolder(srcFolder, picture);
                            PageParameters parameters = new PageParameters();
                            parameters.add("id", srcFolder.getId());
                            throw new RestartResponseException(FileManagerPage.class, parameters);
                        } catch (Exception e) {
                            throw new IllegalStateException("Unable to restore data.", e);
                        }
                    }
                }
            };
        };
        // set this form to multipart mode (allways needed for uploads!)
        progressUploadForm.setMultiPart(true);
        progressUploadForm.add(fileUploadField);
        progressUploadForm.add(new UploadProgressBar("progress", progressUploadForm, fileUploadField));

        add(progressUploadForm);
    }

}
