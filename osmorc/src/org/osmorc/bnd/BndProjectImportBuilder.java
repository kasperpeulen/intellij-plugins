package org.osmorc.bnd;

import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Condition;
import com.intellij.packaging.artifacts.ModifiableArtifactModel;
import com.intellij.projectImport.ProjectImportBuilder;
import com.intellij.util.containers.ContainerUtil;
import icons.OsmorcIdeaIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class BndProjectImportBuilder extends ProjectImportBuilder<Project> {
  private Workspace myWorkspace = null;
  private List<Project> myProjects = null;
  private Set<Project> myChosenProjects = null;
  private boolean myOpenProjectSettings = false;

  @NotNull
  @Override
  public String getName() {
    return "Bnd/Bndtools";
  }

  @Override
  public Icon getIcon() {
    return OsmorcIdeaIcons.Bnd;
  }

  public Workspace getWorkspace() {
    return myWorkspace;
  }

  public void setWorkspace(Workspace workspace, Collection<Project> projects) {
    myWorkspace = workspace;
    myProjects = ContainerUtil.newArrayList(projects);
  }

  @Override
  public List<Project> getList() {
    return myProjects;
  }

  @Override
  public void setList(List<Project> list) throws ConfigurationException {
    myChosenProjects = ContainerUtil.newHashSet(list);
  }

  @Override
  public boolean isMarked(Project project) {
    return myChosenProjects == null || myChosenProjects.contains(project);
  }

  @Override
  public boolean isOpenProjectSettingsAfter() {
    return myOpenProjectSettings;
  }

  @Override
  public void setOpenProjectSettingsAfter(boolean openProjectSettings) {
    myOpenProjectSettings = openProjectSettings;
  }

  @NotNull
  @Override
  public List<Module> commit(com.intellij.openapi.project.Project project,
                             ModifiableModuleModel model,
                             ModulesProvider modulesProvider,
                             ModifiableArtifactModel artifactModel) {
    if (model == null) {
      model = ModuleManager.getInstance(project).getModifiableModel();
      try {
        List<Module> result = commit(project, model, modulesProvider, artifactModel);
        commitModel(model);
        return result;
      }
      catch (RuntimeException e) {
        disposeModel(model);
        throw e;
      }
      catch (Error e) {
        disposeModel(model);
        throw e;
      }
    }

    List<Project> toImport = ContainerUtil.filter(myProjects, new Condition<Project>() {
      @Override
      public boolean value(Project project) {
        return isMarked(project);
      }
    });
    BndProjectImporter importer = new BndProjectImporter(project, myWorkspace, toImport);
    Module rootModule = importer.createRootModule(model);
    importer.setupProject();
    importer.resolve();
    return Collections.singletonList(rootModule);
  }

  private static void commitModel(final ModifiableModuleModel moduleModel) {
    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      @Override
      public void run() {
        moduleModel.commit();
      }
    });
  }

  private static void disposeModel(final ModifiableModuleModel moduleModel) {
    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      @Override
      public void run() {
        moduleModel.dispose();
      }
    });
  }

  @Override
  public void cleanup() {
    myWorkspace = null;
    myProjects = null;
    myChosenProjects = null;
    super.cleanup();
  }
}
