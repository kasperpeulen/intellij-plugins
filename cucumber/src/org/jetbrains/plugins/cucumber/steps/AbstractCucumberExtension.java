package org.jetbrains.plugins.cucumber.steps;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.cucumber.CucumberJvmExtensionPoint;
import org.jetbrains.plugins.cucumber.psi.GherkinFile;
import org.jetbrains.plugins.cucumber.psi.GherkinStep;

import java.util.*;

/**
 * User: Andrey.Vokin
 * Date: 6/28/13
 */
public abstract class AbstractCucumberExtension implements CucumberJvmExtensionPoint {
  @Override
  public List<PsiElement> resolveStep(@NotNull final PsiElement element) {
    final Module module = ModuleUtilCore.findModuleForPsiElement(element);
    if (module == null) {
      return Collections.emptyList();
    }

    Set<String> stepVariants = getAllPossibleStepVariants(element);

    final List<AbstractStepDefinition> stepDefinitions = loadStepsFor(element.getContainingFile(), module);
    final List<PsiElement> result = new ArrayList<PsiElement>();
    for (AbstractStepDefinition stepDefinition : stepDefinitions) {

      for (String s : stepVariants) {
        if (stepDefinition.matches(s)) {
          result.add(stepDefinition.getElement());
        }
      }
    }

    return result;
  }

  protected Set<String> getAllPossibleStepVariants(@NotNull final PsiElement element) {
    if (element instanceof GherkinStep) {
      return ((GherkinStep)element).getSubstitutedNameList();
    }
    return Collections.emptySet();
  }

  @Override
  public void flush() {
  }

  @Override
  public void reset() {
  }

  @Override
  public void init(@NotNull Project project) {
  }

  @Override
  public Set<PsiDirectory> findStepDefsRoots(GherkinFile file) {
    return Collections.emptySet();
  }
}