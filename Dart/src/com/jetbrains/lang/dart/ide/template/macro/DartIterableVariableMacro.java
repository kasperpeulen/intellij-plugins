package com.jetbrains.lang.dart.ide.template.macro;

import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.jetbrains.lang.dart.psi.DartClass;
import org.jetbrains.annotations.NotNull;

/**
 * @author: Fedor.Korotkov
 */
public class DartIterableVariableMacro extends DartFilterByClassMacro {
  @Override
  public String getName() {
    return "dartIterableVariable";
  }

  @Override
  public String getPresentableName() {
    return "dartIterableVariable()";
  }

  @Override
  protected boolean filter(@NotNull DartClass dartClass, Expression[] params, ExpressionContext context) {
    return dartClass.findMemberByName("iterator") != null;
  }
}
