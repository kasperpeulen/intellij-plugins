package com.jetbrains.lang.dart.ide.template.macro;

import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.intellij.codeInsight.template.Result;
import com.intellij.codeInsight.template.TextResult;
import org.jetbrains.annotations.NotNull;

public class DartProjectNameMacro extends DartMacroBase {
  @Override
  public String getName() {
    return "projectName";
  }

  @Override
  public Result calculateResult(@NotNull Expression[] params, final ExpressionContext context) {
    final String projectName =  context.getProject().getName();
    return new TextResult(projectName);
  }
}
