package com.jetbrains.lang.dart.ide.template.macro;

import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.jetbrains.lang.dart.psi.DartClass;
import org.jetbrains.annotations.NotNull;

/**
 * @author: Fedor.Korotkov
 */
public class DartSuggestVariable extends DartFilterByClassMacro {
  @Override
  public String getName() {
    return "dartSuggestVariable";
  }

  @Override
  public String getPresentableName() {
    return "dartSuggestVariable()";
  }

  @Override
  protected boolean filter(@NotNull DartClass dartClass, Expression[] params, ExpressionContext context) {

    return (dartClass.getName().equals(params[0].calculateResult(context).toString()));
  }
}
