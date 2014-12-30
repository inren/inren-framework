package de.inren.frontend.storehouse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.inren.service.folder.gui.FolderGM;

public class FolderGMProvider implements ITreeProvider<FolderGM> {

    private IModel<FolderGM> model;

    public FolderGMProvider(IModel<FolderGM> model) {
        this.model = model;
    }

    @Override
    public void detach() {
        model.detach();
    }

    @Override
    public Iterator<? extends FolderGM> getRoots() {
        List<FolderGM> root = new ArrayList<FolderGM>();
        root.add(model.getObject());
        return root.iterator();
    }

    @Override
    public boolean hasChildren(FolderGM node) {
        return !node.getFolders().isEmpty();
    }

    @Override
    public Iterator<? extends FolderGM> getChildren(FolderGM node) {
        return node.getFolders().iterator();
    }

    @Override
    public IModel<FolderGM> model(FolderGM object) {
        return Model.of(object);
    }
}
