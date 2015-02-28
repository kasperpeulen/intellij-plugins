package com.jetbrains.lang.dart.ide.template.macro;

import com.google.common.annotations.VisibleForTesting;
import com.intellij.codeInsight.template.*;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.lang.dart.psi.*;
import com.jetbrains.lang.dart.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class DartClassNameOfVariable extends DartMacroBase {
  @Override
  public String getName() {
    return "dartClassNameOfVariable";
  }

  @Override
  public Result calculateResult(@NotNull Expression[] params, final ExpressionContext context) {

    if (params.length != 1) return null;

    final String result = params[0].calculateResult(context).toString();
    if (result == null) return null;

    int index = context.getEditor().getDocument().getText().indexOf(" " + result + ";");
    if (index == -1) {
      index =  context.getEditor().getDocument().getText().indexOf(" "+result+" ");
    }

    PsiElement element = context.getPsiElementAtStartOffset().getContainingFile().findElementAt(
      index);

    PsiElement component = PsiTreeUtil.getParentOfType(element, DartComponent.class);

    DartGenericSpecialization gen = DartResolveUtil.getDartClassResolveResult(component).getSpecialization();

    for (String key : gen.map.keySet()) {
      final DartClassResolveResult value = gen.map.get(key);
      if (value.getDartClass() != null) {
        return new TextResult(value.getDartClass().getName());
      }
    }

    return null;
  }
}
