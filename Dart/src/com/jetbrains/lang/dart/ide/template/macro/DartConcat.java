package com.jetbrains.lang.dart.ide.template.macro;

import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.intellij.codeInsight.template.Result;
import com.intellij.codeInsight.template.TextResult;
import org.jetbrains.annotations.NotNull;

public class DartConcat extends DartMacroBase {
  @Override
  public String getName() {
    return "concat";
  }


  @Override
  public Result calculateResult(@NotNull Expression[] params, final ExpressionContext context) {
    if (params.length>0){
    String concat = "";
    for (int i = 0; i < params.length; i++) {
      concat += params[i].calculateResult(context).toString();
    }
    return new TextResult(concat);
  }
    else return new TextResult("");
  }
}
