package com.jetbrains.lang.dart.ide.template.macro;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.codeInsight.lookup.PsiTypeLookupItem;
import com.intellij.codeInsight.template.*;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.lang.dart.psi.DartClass;
import com.jetbrains.lang.dart.psi.DartComponentName;
import com.jetbrains.lang.dart.util.DartClassResolveResult;
import com.jetbrains.lang.dart.util.DartGenericSpecialization;
import com.jetbrains.lang.dart.util.DartRefactoringUtil;
import com.jetbrains.lang.dart.util.DartResolveUtil;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: Fedor.Korotkov
 */
public abstract class DartFilterByClassMacro extends Macro {
  @Override
  public PsiElementResult calculateResult(@NotNull final Expression[] params, final ExpressionContext context) {
    final PsiElement at = context.getPsiElementAtStartOffset();
    final Set<DartComponentName> variables = DartRefactoringUtil.collectUsedComponents(at);
    final List<DartComponentName> filtered = ContainerUtil.filter(variables, new Condition<DartComponentName>() {
      @Override
      public boolean value(DartComponentName name) {
        final PsiElement nameParent = name.getParent();
        if (nameParent instanceof DartClass) {
          return false;
        }
        final DartClassResolveResult result = DartResolveUtil.getDartClassResolveResult(nameParent);
        final DartClass dartClass = result.getDartClass();
        return dartClass != null && filter(dartClass, params, context);
      }
    });
    return filtered.isEmpty() ? null : new PsiElementResult(filtered.iterator().next());
  }

  @Override
  public LookupElement[] calculateLookupItems(@NotNull final Expression[] params, final ExpressionContext context) {
    final PsiElement at = context.getPsiElementAtStartOffset();
    final Set<DartComponentName> variables = DartRefactoringUtil.collectUsedComponents(at);
    final List<DartComponentName> filtered = ContainerUtil.filter(variables, new Condition<DartComponentName>() {
      @Override
      public boolean value(DartComponentName name) {
        final PsiElement nameParent = name.getParent();
        if (nameParent instanceof DartClass) {
          return false;
        }
        final DartClassResolveResult result = DartResolveUtil.getDartClassResolveResult(nameParent);
        final DartClass dartClass = result.getDartClass();

        return dartClass != null && filter(dartClass, params, context);
      }
    });

    LookupItem[] items = new LookupItem[filtered.size()];

    for (LookupItem item : items) {

    }

    for (int i = 0; i < filtered.size(); i++) {
      DartComponentName name = filtered.get(i );
      items[i] = LookupItem.fromString(name.getName());
      items[i].setIcon(name.getIcon(i));
      items[i].setTailType(TailType.UNKNOWN);
       //items[i].setTailText(DartResolveUtil.getDartClassResolveResult(name).getDartClass().getName(), true);

      DartGenericSpecialization gen = DartResolveUtil.getDartClassResolveResult(name).getSpecialization();
      String generic = "";
      for (String key : gen.map.keySet()) {
        final DartClassResolveResult value = gen.map.get(key);
        if (value.getDartClass() != null) {
          generic = "<"+value.getDartClass().getName()+">";
        }
      }
      items[i].setTypeText(DartResolveUtil.getDartClassResolveResult(name).getDartClass().getName() + generic);
    }


    //return items;
    return items;
  }


  protected abstract boolean filter(@NotNull DartClass dartClass, @NotNull Expression[] params,ExpressionContext context);

}
