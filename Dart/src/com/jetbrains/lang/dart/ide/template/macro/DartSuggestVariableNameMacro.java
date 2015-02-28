package com.jetbrains.lang.dart.ide.template.macro;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.codeInsight.template.*;
import com.intellij.icons.AllIcons;
import com.intellij.util.ArrayUtil;
import com.jetbrains.lang.dart.DartIconProvider;
import com.jetbrains.lang.dart.util.DartNameSuggesterUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;

/**
 * @author: Fedor.Korotkov
 */
public class DartSuggestVariableNameMacro extends Macro {
  @Override
  public String getName() {
    return "dartSuggestVariableName";
  }

  @Override
  public String getPresentableName() {
    return "dartSuggestVariableName()";
  }

  @NotNull
  @Override
  public String getDefaultValue() {
    return "o";
  }

  @Override
  public Result calculateResult(@NotNull Expression[] params, ExpressionContext context) {

    Collection<String> list = DartNameSuggesterUtil.generateNames(params[0].calculateResult(context).toString());
    String[] strings = ArrayUtil.toStringArray(list);
    LookupItem[] items = new LookupItem[list.size()];
    for (int i = 0; i < strings.length; i++) {
      items[i] = LookupItem.fromString(strings[i]);
      items[i].setIcon(AllIcons.Nodes.Variable);
    }
    return new TextResult(items[0].getLookupString());
  }

  @Override
  public LookupElement[] calculateLookupItems(@NotNull Expression[] params, final ExpressionContext context) {

    Collection<String> list = DartNameSuggesterUtil.generateNames(params[0].calculateResult(context).toString());
    String[] strings = ArrayUtil.toStringArray(list);
    LookupItem[] items = new LookupItem[list.size()];
    for (int i = 0; i < strings.length; i++) {
      items[i] = LookupItem.fromString(strings[i]);
      //items[i].setIcon(AllIcons.Nodes.Variable);
    }
    return items;
  }
}
